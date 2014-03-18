package scala.swing

import scala.collection.mutable._
import javax.swing._

/**
 * A tool bar. Contains a number of buttons.
 *
 * @see javax.swing.JToolBar
 */
class ToolBar extends Component with SequentialContainer.Wrapper {
  override lazy val peer: JToolBar = new JToolBar

  def buttons: Seq[Button] = contents.filter(_.isInstanceOf[Button]).map(_.asInstanceOf[Button])

  def addSeparator = peer.addSeparator();
}