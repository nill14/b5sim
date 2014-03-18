package b5sim.gui

import java.awt.Color
import java.awt.Insets

import scala.swing.Button
import scala.swing.Component
import scala.swing.Frame
import scala.swing.GridBagPanel
import scala.swing.GridBagPanel.Anchor
import scala.swing.GridBagPanel.Fill
import scala.swing.SwingRxApi

import b5sim.model.FleetItem
import javax.swing.BorderFactory
import rx.lang.scala.subjects.BehaviorSubject
import suggestions.observablex.LogHelper

class FleetPanel(var model: Seq[FleetItem], frame: Frame) extends GridBagPanel with SwingRxApi with LogHelper {

  def this(frame: Frame) = this(Vector.empty, frame)

  log.info(s"FleetPanel($model)")
  
  
  var fleetRows = Vector.empty[FleetRow]
  val modelObs = BehaviorSubject[Seq[FleetItem]](model)


  val btnAdd = newAddButton
  val addObs = btnAdd.clickObservable
  
  val widgets = Array(
      newLabel("Category"),
      newLabel("Ship"),
      newLabel("Count"),
      newLabel("Strategy"),
      btnAdd
  )
  
  layoutWidgets(0, widgets)
  

  fleetRows = {
    val count = if (model.isEmpty) 1 else model.size
	  for {
	    i <- (1 to count).toVector
	    item = model.map(Some(_)).applyOrElse(i-1, (x: Int) => None)
	    row = newFleetRow(i, item)
	  } yield row    
  }


  
  addObs.subscribe{
    (btnClick: Button) => {
      log.debug(s"appendRow()")
      val row = newFleetRow(fleetRows.size + 1, None)
      fleetRows +:= row
      frame.pack()
    }
  }
  
 
  def newFleetRow(y: Int, item: Option[FleetItem]) = {
    val row = new FleetRow(y, item, this)
    layoutWidgets(y, row.widgets)
    row.modelObs.subscribe(item => item match {
      case Some(_) => updateRow(row, item)
      case None => removeRow(row)
    })
    row
  }


  def updateRow(row: FleetRow, item: Option[FleetItem]) {
    log.debug(s"updateRow(_, $item)")
    recalculate
  }
  def removeRow(row: FleetRow) {
    log.debug(s"removeRow($row)")
    fleetRows = fleetRows.filterNot(_ == row)
    removeWidgets(row.widgets)
    for {
      fleetRow <- fleetRows
    } {
      if (fleetRow.y > row.y) {
      	fleetRow.y -= 1
      	layoutWidgets(fleetRow.y, fleetRow.widgets)
      }
    }
    frame.pack()
    recalculate
  }

  def recalculate {
    val fleetItems = fleetRows.map(_.item).flatten.toVector
		log.debug(s"recalculate ${fleetItems.size} items")
    modelObs.onNext(fleetItems)
  }
  
  
  def layoutWidgets(y: Int, components: IndexedSeq[Component]) {
    import GridBagPanel._
	  for {
	    x <- 0 to components.size - 1
	    widget = components(x)
	  } {
	  	val c = new Constraints
		  c.gridx = x
		  c.gridy = y
		  c.fill = Fill.Horizontal
		  c.weightx = 0.5
		  c.weighty = 1.0
		  c.insets = new Insets(2, 2, 2, 2)
		  c.anchor = Anchor.Center  
		  border = BorderFactory.createLineBorder(Color.BLACK)
		  
		  layout(widget) = c
	  }
  }
  
  def removeWidgets(components: IndexedSeq[Component]) {
	  for {
	    widget <- components
	  } {
	    layout -= widget
	  }
  }
}