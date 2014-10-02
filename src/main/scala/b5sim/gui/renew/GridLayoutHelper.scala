package b5sim.gui.renew

import java.awt.{Color, Insets}

import scala.swing.{Component, GridBagPanel}
import scala.swing.GridBagPanel.{Anchor, Fill}

import javax.swing.BorderFactory

trait GridLayoutHelper {

  def content: Component  
  
  def layoutWidgets(panel: GridBagPanel, y: Int, widgets: IndexedSeq[Component]) {
    import GridBagPanel._
    for {
      x <- 0 to widgets.size - 1
      widget = widgets(x)
    } {
      val c = new panel.Constraints
      c.gridx = x
      c.gridy = y
      c.fill = Fill.Horizontal
      c.weightx = 0.5
      c.weighty = 1.0
      c.insets = new Insets(2, 2, 2, 2)
      c.anchor = Anchor.Center  
      
      panel.layout(widget) = c
    }
    panel.border = BorderFactory.createLineBorder(Color.BLACK)
  }
  
}