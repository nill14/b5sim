package scala.swing

import scala.concurrent.ExecutionContext.Implicits.global
import scala.swing.event.ButtonClicked
import scala.swing.event.ButtonClicked
import scala.swing.event.EditDone
import scala.swing.event.Event
import scala.swing.event.SelectionChanged
import rx.lang.scala.Observable
import rx.lang.scala.subjects.BehaviorSubject
import rx.lang.scala.subjects.PublishSubject
import suggestions.observablex.LogHelper
import suggestions.observablex.SchedulerEx

/** Basic facilities for dealing with Swing-like components.
*
* Instead of committing to a particular widget implementation
* functionality has been factored out here to deal only with
* abstract types like `ValueChanged` or `TextField`.
* Extractors for abstract events like `ValueChanged` have also
* been factored out into corresponding abstract `val`s.
*/
trait SwingRxApi extends LogHelper {

  val eventScheduler = SchedulerEx.SwingEventThreadScheduler
  
  def isInt(x: String): Boolean = {
    try {
      x.toInt
      true
    } catch {
      case e: NumberFormatException => false
    }
  }
  
  implicit class ComboBoxOps[A](combobox: MyComboBox[A]) {
    
    var item = combobox.selection.item
    
    /** Returns a stream of text field values entered in the given text field.
      *
      * @param field the text field
      * @return an observable with a stream of text field updates
      */
    def selectionObservable: Observable[A] = {
      val channel = BehaviorSubject[A](item)
      combobox.selection.subscribe {
        case SelectionChanged(`combobox`) if combobox.selection.item != item => 
          log.debug(s"SelectionChanged($combobox)")
          item = combobox.selection.item
          channel.onNext(item)
        case _ => 
      } 
      
      channel
    }
  }  

  implicit class TextFieldOps(field: TextField) {
    
    var textValue = field.text
    
    /** Returns a stream of text field values entered in the given text field.
      *
      * @param field the text field
      * @return an observable with a stream of text field updates
      */
    def textObservable: Observable[String] = {
      val channel = BehaviorSubject[String](textValue)
      field.subscribe {
        case EditDone(`field`) if field.text != textValue => 
          log.debug(s"EditDone($field)")
          textValue = field.text
          channel.onNext(textValue)
        case _ =>  
      } 
      
      channel
    }
    
    def intObservable = field.textObservable.filter(isInt).map{_.toInt}
  }

  implicit class ButtonOps(button: Button) {

    /** Returns a stream of button clicks.
     *
     * @param field the button
     * @return an observable with a stream of buttons that have been clicked
     */
    def clickObservable: Observable[Button] = {
      val channel = PublishSubject[Button]()
      button.subscribe {
        case ButtonClicked(`button`)  => 
          log.debug(s"ButtonClicked($button)")
          channel.onNext(button)
        case _ =>
      }
        
      channel  
    }

  }
  
  implicit class CheckBoxOps(checkbox: CheckBox) {

    var selected = checkbox.selected
    /** Returns a stream of button clicks.
     *
     * @param field the button
     * @return an observable with a stream of buttons that have been clicked
     */
    def checkObservable: Observable[Boolean] = {
      val channel = BehaviorSubject[Boolean](selected)
      checkbox.subscribe {
        case ButtonClicked(`checkbox`) if selected != checkbox.selected => 
          selected = checkbox.selected
          log.debug(s"Checkbox($selected)")
          channel.onNext(selected)
        case _ => 
      }
        
      channel  
    }

  }  

}