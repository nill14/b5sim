package b5sim.gui.renew

import scala.swing._

object SwingAppFrameTest extends SwingAppFrameTest(0) with SwingAppFrame {
  
}
class SwingAppFrameTest(a: Int)  {
  
  def content: Component = {
    // declare Components here
    val label = new Label {
      text = "I'm a big label!."
      font = new Font("Ariel", java.awt.Font.ITALIC, 24)
    }
    label
  }
}