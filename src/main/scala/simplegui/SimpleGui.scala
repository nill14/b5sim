package simplegui

import scala.swing._
import scala.swing.BorderPanel.Position._
import event._
import java.awt.Color
import scala.util.Random

object SimpleGUI extends SimpleSwingApplication {

  def top = new MainFrame { // top is a required method
    title = "A Sample Scala Swing GUI"

    // declare Components here
    val label = new Label {
      text = "I'm a big label!."
      font = new Font("Ariel", java.awt.Font.ITALIC, 24)
    }
    val button = new Button {
      text = "Throw!"
      foreground = Color.blue
      background = Color.red
      borderPainted = true
      enabled = true
      tooltip = "Click to throw a dart"
    }
    val toggle = new ToggleButton { text = "Toggle" }
    val checkBox = new CheckBox { text = "Check me" }
    val textField = new TextField {
      columns = 10
      text = "Click on the target!"
    }
    val textArea = new TextArea {
      text = "initial text\nline two"
      background = Color.green
    }
    val canvas = new Canvas {
      preferredSize = new Dimension(100, 100)
    }
    val gridPanel = new GridPanel(1, 2) {
      contents += checkBox
      contents += label
      contents += textArea
    }

    // choose a top-level Panel and put components in it
    // Components may include other Panels
    contents = new BorderPanel {
      layout(gridPanel) = North
      layout(button) = West
      layout(canvas) = Center
      layout(toggle) = East
      layout(textField) = South
    }
    size = new Dimension(300, 200)
    menuBar = new MenuBar {
      contents += new Menu("File") {
        contents += new MenuItem(Action("Exit") {
          sys.exit(0)
        })
      }
    }

    // specify which Components produce events of interest
    listenTo(button)
    listenTo(toggle)
    listenTo(canvas.mouse.clicks)

    // react to events
    reactions += {
      case ButtonClicked(component) if component == button =>
        val x = Random.nextInt(100)
        val y = Random.nextInt(100)
        val c = new Color(Random.nextInt(Int.MaxValue))
        canvas.throwDart(new Dart(x, y, c))
        textField.text = s"Dart thrown at $x, $y"
      case ButtonClicked(component) if component == toggle =>
        toggle.text = if (toggle.selected) "On" else "Off"
      case MouseClicked(_, point, _, _, _) =>
        canvas.throwDart(new Dart(point.x, point.y, Color.black))
        textField.text = (s"You clicked in the Canvas at x=${point.x}, y=${point.y}.") 
    }
  }
}