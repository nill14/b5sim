package b5sim

import java.text.DecimalFormat
import rx.lang.scala.Observable
import rx.lang.scala.Observer
import rx.lang.scala.Subscription
import javax.imageio.ImageIO
import scala.swing.MyComboBox
import scala.swing.Button
import scala.swing.TextField
import java.awt.event.FocusAdapter
import javax.swing.ImageIcon
import java.awt.Image
package object gui {

  def isInt(x: String): Boolean = {
    try {
      x.toInt
      true
    } catch {
      case e: NumberFormatException => false
    }
  }  
  
  def newLabel(text: String) = new scala.swing.Label(text)


  def newComboBox[E](model: Seq[E]) = new MyComboBox(model)

  def newIntField() = new TextField {
    columns = 3
//    verifier = isInt
    peer.addFocusListener(new FocusAdapter() {
      override def focusGained(e: java.awt.event.FocusEvent) {
        peer.selectAll
      }
    })
  }

  def newComboBox[E](model: Seq[E], selection: Option[E]) = {
    val combobox = new MyComboBox(model)
    selection match {
      case Some(sel) => combobox.selection.item = sel
      case None =>
    }
    combobox
  }

  def newIntField(value: Option[Int]) = new TextField {
    columns = 3
    verifier = isInt
    peer.addFocusListener(new FocusAdapter() {
      override def focusGained(e: java.awt.event.FocusEvent) {
        peer.selectAll
      }
    })
    value match {
      case Some(count) => text = count.toString
      case None =>
    }
  }

  def newRemoveButton = new Button {
    val res = this.getClass.getResourceAsStream("/delete.png")
    val ic = new javax.swing.ImageIcon(ImageIO.read(res))
    icon = new ImageIcon(ic.getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT))
  }  
  
  def newAddButton = new Button {
    val res = this.getClass.getResourceAsStream("/add.png")
    val ic = new javax.swing.ImageIcon(ImageIO.read(res))
    icon = new ImageIcon(ic.getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT))
  }    
}