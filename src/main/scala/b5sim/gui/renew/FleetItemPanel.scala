package b5sim.gui.renew

import b5sim.gui._
import b5sim.model._
import scala.swing._
import rx.lang.scala._
import rx.lang.scala.subjects._
import java.awt.event.ActionListener

object FleetItemPanel extends FleetItemPanel with SwingAppFrame {
  
}
class FleetItemPanel extends SwingRxApi with GridLayoutHelper {
  
  def content = new GridBagPanel {
    
    
    val comboRace = newComboBox[Race.Value](Race.values.toSeq)
    val comboShips = newComboBox[SpaceShip](Ships.ships)
    val countField = newIntField()
    val comboStrategy = newComboBox[Strategy](Strategy.strategies)
    val removeButton = newRemoveButton
  
    val widgets = Array(
        comboRace,
        comboShips,
        countField,
        comboStrategy,
        removeButton
    )
    
    layoutWidgets(this, 0, widgets)
    
    val shipModelObs = comboRace.selectionObservable
                .observeOn(poolScheduler).map(race => {
      log.debug("Producing ShipModel({})", race) 
      Ships.byRace(race)
    })
    
    shipModelObs.observeOn(eventScheduler).subscribe(
        shipModel => comboShips.model = shipModel
    )
    
    val fleetItemObs = PublishSubject[Option[FleetItem]]
    
    comboShips.selectionObservable
              .combineLatest(countField.intObservable)
              .combineLatest(comboStrategy.selectionObservable)
              .map { case ((ship, count), strategy) => 
                        Some(FleetItem(ship, count, strategy)) }
              .observeOn(poolScheduler)
              .subscribe(fleetItemObs)
    
    removeButton.clickObservable.map(_ => None)
                .observeOn(poolScheduler)
                .subscribe(fleetItemObs)  
    
                
    fleetItemObs.observeOn(poolScheduler)
                .subscribe( next => log.debug("FleetItem: {}", next) )
                
    countField.intObservable.observeOn(poolScheduler)
              .subscribe( next => log.debug("next int: {}", next) ) 
              
    comboShips.selectionObservable.observeOn(poolScheduler)
              .subscribe( next => log.debug("next ship: {}", next) )
              
    comboStrategy.selectionObservable.observeOn(poolScheduler)
              .subscribe( next => log.debug("next strategy: {}", next) )         
              
    removeButton.clickObservable.observeOn(poolScheduler)
              .subscribe( next => log.debug("next button: {}", next) )
              
    removeButton.clickObservable.map(_ => None)
                .observeOn(poolScheduler)
                .subscribe( next => log.debug("next None: {}", next) )  
                  
  }
    
    
    
//    val shipObs = comboShips.selectionObservable
//    val countObs = countField.intObservable
//    val strategyObs = comboStrategy.selectionObservable  
//    val removeObs: Observable[Option[FleetItem]] = removeButton.clickObservable.map(_ => {log.debug("removeObs"); None})

    
}