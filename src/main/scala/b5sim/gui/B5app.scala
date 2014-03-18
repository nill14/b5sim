package b5sim.gui

import java.awt.Insets
import scala.swing.Action
import scala.swing.Dimension
import scala.swing.GridBagPanel
import scala.swing.GridBagPanel.Anchor
import scala.swing.GridBagPanel.Fill
import scala.swing.MainFrame
import scala.swing.Menu
import scala.swing.MenuBar
import scala.swing.MenuItem
import scala.swing.SimpleSwingApplication
import b5sim.model.FleetItem
import b5sim.model.ShipClass
import b5sim.model.Ships
import b5sim.model.Strategy
import suggestions.observablex.SchedulerEx
import suggestions.observablex.LogHelper
import scala.swing.SwingRxApi

object B5App extends SimpleSwingApplication with SwingRxApi with LogHelper {
  
//  val model = Ships.ships.drop(4).take(4).map(s => FleetItem(s, 2, Strategy.strategyFor(s)) )
//  model foreach println

  def top = new MainFrame { // top is a required method
    title = "B5 simulator"

    val fleetLeft = new FleetPanel(this)
    val fleetRight = new FleetPanel(this)
     
    val summaryLeft = new SummaryPanel(fleetLeft.modelObs, fleetRight.modelObs)
    val summaryRight = new SummaryPanel(fleetRight.modelObs, fleetLeft.modelObs)

    contents = new GridBagPanel {
      import GridBagPanel._
      val c = new Constraints
      c.gridx = 0
      c.gridy = 0
      c.fill = Fill.Horizontal
      c.weightx = 0.5
      c.weighty = 1.0
      c.insets = new Insets(2, 2, 2, 2)
      c.anchor = Anchor.North
      
      layout(fleetLeft) = c
      
      c.gridx = 1
      layout(fleetRight) = c
      
      c.gridx = 0
      c.gridy = 1
      c.anchor = Anchor.PageEnd
      layout(summaryLeft) = c
      
      c.gridx = 1
      layout(summaryRight) = c
    }
    
    size = new Dimension(800, 500)
    menuBar = new MenuBar {
      contents += new Menu("File") {
        contents += new MenuItem(Action("Exit") {
          sys.exit(0)
        })
      }
    }
    
   
  }
  
}