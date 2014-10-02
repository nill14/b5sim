package b5sim.gui

import java.awt.Image
import scala.collection.mutable.Buffer
import scala.swing.Button
import scala.swing.Component
import scala.swing.MyComboBox
import scala.swing.Panel
import scala.swing.Reactor
import scala.swing.TextField
import b5sim.model.Race
import b5sim.model.Ships
import b5sim.model.SpaceShip
import b5sim.model.SpaceShip
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import rx.lang.scala.Observable
import rx.lang.scala.Observer
import rx.lang.scala.Subscription
import rx.lang.scala.subjects.BehaviorSubject
import suggestions.observablex.SchedulerEx
import scala.swing.SwingRxApi
import rx.lang.scala.Subject
import b5sim.model.Race
import java.awt.event.FocusAdapter
import suggestions.observablex.LogHelper
import scala.concurrent._
import rx.lang.scala.subjects.PublishSubject
import org.slf4j.MarkerFactory
import rx.lang.scala.subjects.AsyncSubject
import b5sim.model._

class FleetRow (
  var y: Int,  
  var item: Option[FleetItem],
  implicit val panel: Panel) extends SwingRxApi with LogHelper {
  
  
  
  val (raceSel, shipSel, countSel, strategySel) = item match {
    case Some(FleetItem(ship, count, strategy)) => 
      ( Some(ship.race), Some(ship), Some(count), Some(strategy) )
    case None => ( None, None, Some(1), None )
  }

  val comboRace = newComboBox[Race.Value](Race.values.toSeq, raceSel)
  val comboShips = newComboBox[SpaceShip](Ships.ships, shipSel)
  val countField = newIntField(countSel)
  val comboStrategy = newComboBox[Strategy](Strategy.strategies, strategySel)
  val removeButton = newRemoveButton

  val widgets = Array(
  		comboRace,
  		comboShips,
  		countField,
  		comboStrategy,
  		removeButton
  )
  

  val raceObs = comboRace.selectionObservable
  val shipObs = comboShips.selectionObservable
  val countObs = countField.intObservable
  val strategyObs = comboStrategy.selectionObservable  
  val removeObs: Observable[Option[FleetItem]] = removeButton.clickObservable.map(_ => {log.debug("removeObs"); None})

  val modelObs: Observable[Option[FleetItem]] = {
    val subject = PublishSubject[Option[FleetItem]]
    //use subject cause of error
    //Caused by: java.lang.IllegalStateException: Only one Observer can subscribe to this Observable.
    
    val obs = shipObs.combineLatest(countObs).combineLatest(strategyObs).map {
      case ((ship, count), strategy) => Some(FleetItem(ship, count, strategy))
    } 
    
    obs.subscribe(subject)
    removeObs.subscribe(subject)
    subject
  }
  
  modelObs.subscribe(optItem => {
    log.debug(s"[$this] $optItem")
    this.item = optItem
    optItem match {
      case Some(FleetItem(ship, count, strategy)) =>
        log.debug(s"[$this] initialize row with $optItem")
        comboRace.selection.item = ship.race
        comboShips.selection.item = ship
        countField.text = count.toString
        comboStrategy.selection.item = strategy
      case None =>
    }
  })
  	
  val shipsInputObs: Observable[Vector[SpaceShip]] = {
    var old: Race.Value = null
    for (race <- raceObs if race != old) yield {
      old = race
      log.debug(s"[$this] comboRace: $race")
      Ships.byRace(race)
    }
  }

  shipsInputObs.observeOn(eventScheduler) subscribe {
    (items: Vector[SpaceShip]) =>
      log.debug(s"[$this] newItems: $items")
      val selection = comboShips.selection.item
      comboShips.model = items
      if (items.contains(selection)) {
        comboShips.selection.item = selection
      }
  }
  
  shipObs.observeOn(eventScheduler) subscribe {
    (ship: SpaceShip) => comboStrategy.selection.item = Strategy.strategyFor(ship)
  }

} 