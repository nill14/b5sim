package b5sim.gui.renew

import scala.swing.MainFrame
import scala.swing.SwingRxApi
import scala.swing.SimpleSwingApplication
import scala.swing.Button
import scala.swing.Label
import java.awt.Font
import java.awt.Color
import scala.swing.Swing
import java.awt.Dimension
import scala.swing.Reactor
import scala.swing.Menu
import scala.swing.MenuBar
import scala.swing.MenuItem
import scala.swing.Action
import scala.swing.Component
import scala.swing.GridBagPanel
import javax.swing.BorderFactory
import java.awt.Insets

trait SwingAppFrame extends Reactor {
  
  /** Initializes the application and runs the given program. */
  def main(args: Array[String]) = Swing.onEDT { startup(args) }

  /** Finalizes the application by calling `shutdown` and exits.*/
  def quit() { shutdown(); sys.exit(0) }

  /** Called before the application is exited. Override to customize. */
  def shutdown() {}  
  
  
  /**
   * A GUI application's version of the main method. Called by the default
   * main method implementation provided by this class.
   * Implement to return the top-level frame of this application.
   */
  def content: Component

  /**
   * Calls `top`, packs the frame, and displays it.
   */
  def startup(args: Array[String]) {
    val t = top
    if (t.size == new Dimension(0,0)) t.pack()
    t.visible = true
  }

  def resourceFromClassloader(path: String): java.net.URL =
    this.getClass.getResource(path)

  def resourceFromUserDirectory(path: String): java.io.File =
    new java.io.File(util.Properties.userDir, path)  
  

  def top = new MainFrame { // top is a required method
    title = "A Sample Scala Swing GUI"
    
    contents = content
    
    size = new Dimension(500, 300)
    menuBar = new MenuBar {
      contents += new Menu("File") {
        contents += new MenuItem(Action("Exit") {
          sys.exit(0)
        })
      }
    }
  }

}