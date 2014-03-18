//package b5sim.gui
//
//import java.awt.Insets
//import scala.swing.Action
//import scala.swing.Dimension
//import scala.swing.GridBagPanel
//import scala.swing.GridBagPanel.Anchor
//import scala.swing.GridBagPanel.Fill
//import scala.swing.MainFrame
//import scala.swing.Menu
//import scala.swing.MenuBar
//import scala.swing.MenuItem
//import scala.swing.SimpleSwingApplication
//import b5sim.model.FleetItem
//import b5sim.model.ShipClass
//import b5sim.model.Ships
//import b5sim.model.Strategy
//import suggestions.observablex.SchedulerEx
//import suggestions.observablex.LogHelper
//import scala.swing.SwingRxApi
//import scala.swing.BoxPanel
//import scala.swing.Table
//import scala.swing.Orientation
//
//object B5App extends SimpleSwingApplication with SwingRxApi with LogHelper {
//  
//  val model = Ships.ships.drop(4).take(4).map(s => FleetItem(s, 2, Strategy.strategyFor(s)) )
////  model foreach println
//  
//  
//  def top = new MainFrame { // top is a required method
//    title = "B5 simulator"
//
//      
//    val table = new Table(4, 5)  
//      
//    contents = table
//    	
////    	table.mo
//      
//    
//    size = new Dimension(400, 400)
//    menuBar = new MenuBar {
//      contents += new Menu("File") {
//        contents += new MenuItem(Action("Exit") {
//          sys.exit(0)
//        })
//      }
//    }
//    
//   
//  }
//  
//}