package simplegui

import scala.swing.Panel
import java.awt.{ Graphics2D, Color }

class Canvas extends Panel {
  var centerColor = Color.yellow
  
  var darts = List[Dart]()

  override def paintComponent(g: Graphics2D) {
    
    // Start by erasing this Canvas
    g.clearRect(0, 0, size.width, size.height)
    
    // Draw background here
    g.setColor(Color.blue)
    g.fillOval(0, 0, 100, 100)
    g.setColor(Color.red)
    g.fillOval(20, 20, 60, 60)
    g.setColor(centerColor)
    g.fillOval(40, 40, 20, 20)
    
    // Draw things that change on top of background
    for (dart <- darts) {
      g.setColor(dart.color)
      g.fillOval(dart.x, dart.y, 10, 10)
    }
  }

  /** Add a "dart" to list of things to display */
  def throwDart(dart: Dart) {
    darts = darts :+ dart
    // Tell Scala that the display should be repainted
    repaint()
  }
}