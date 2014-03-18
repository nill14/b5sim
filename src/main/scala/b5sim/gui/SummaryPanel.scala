package b5sim.gui

import java.awt.Color
import java.awt.Font

import scala.swing.CheckBox
import scala.swing.FlowPanel
import scala.swing.GridPanel
import scala.swing.Label
import scala.swing.SwingRxApi
import scala.swing.TextField

import b5sim.processor._
import b5sim.model.FleetItem
import b5sim.model.FleetItem
import b5sim.model.ShipClass
import b5sim.model.ShipClass
import b5sim.model.Strategy
import javax.swing.BorderFactory
import rx.lang.scala.Observable
import suggestions.observablex.LogHelper

class SummaryPanel(
    thisObs: Observable[Seq[FleetItem]], 
    thatObs: Observable[Seq[FleetItem]]) extends GridPanel(4, 1) with SwingRxApi with LogHelper {

  
  border = BorderFactory.createLineBorder(Color.BLACK)
  hGap = 1
  vGap = 1
  
  val defenseCheck = new CheckBox("OS (+20% attack)")
  
  val controlPanel = new FlowPanel {
    contents += defenseCheck
  }
  val labelHitpoints = newTextField
  val labelAttacks = newTextField
  val labelPercents = newTextField
  
  contents += controlPanel
  contents += labelHitpoints
  contents += labelAttacks
  contents += labelPercents
  
  val defenseStationObs = defenseCheck.checkObservable.map(check => if (check) 1.2 else 1.0)
  
  def newTextField = new TextField {
    editable = false
    font = new Font(Font.SANS_SERIF, Font.PLAIN, 14)
  }
  
//  val shipClasses = ShipClass.values.toArray
  
  val classHitpointsObs = for {
    seq <- thisObs
  } yield {
    for {
    	(group, items) <- ShipClass.groupByClass(seq.toVector, Vector.empty)
    	sum = items.map(_.countHitpoints).sum
    } yield {
      sum
    }
  }
  
  val classAttacksObs = for {
    (seq, stationRatio) <- thisObs.combineLatest(defenseStationObs)
    groups = seq.groupBy{ case (FleetItem(ship, count, strategy)) => strategy.primary }
  } yield { 
    for {
    	(group, items) <- ShipClass.groupItems(groups, Vector.empty)
    	sum = (items.map(_.countPrimaryAttacks).sum * stationRatio).toInt
    } yield {
      sum
    }    
  }  
  
  val classDamagesObs = for {
    seq <- thatObs
    groups = seq.groupBy{ case (FleetItem(ship, count, strategy)) => strategy.primary }
  } yield { 
    for {
    	(group, items) <- ShipClass.groupItems(groups, Vector.empty)
    	sum = items.map(_.countPrimaryAttacks).sum
    } yield {
      sum
    }     
  }    
  
  val classPercentsObs = for {
    hitpoints <- classHitpointsObs
    damages <- classDamagesObs
  } yield {
    hitpoints.zip(damages).map{
      case ((hp, dmg)) => 
        if (hp == 0) 1.0
        else if (dmg > hp) 0.0
        else 1 - (dmg.toDouble / hp)
    }
  }
  
  classHitpointsObs.observeOn(eventScheduler) subscribe {
    (hitpoints: Array[Int]) => 
  		labelHitpoints.text = hitpoints.map(numFmt).mkString("HP (S/K/L/D): ", " / ", "")
  }
  
  classAttacksObs.observeOn(eventScheduler) subscribe {
    (attacks: Array[Int]) => 
  		labelAttacks.text = attacks.map(numFmt).mkString("Attack (S/K/L/D): ", " / ", "")
  }  

  classPercentsObs.observeOn(eventScheduler) subscribe {
    (percents: Array[Double]) => 
  		labelPercents.text = percents.map(pctFmt).mkString("Status (S/K/L/D): ", " / ", "")
  }      
  
}

