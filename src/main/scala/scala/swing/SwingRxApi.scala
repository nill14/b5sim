package scala.swing

import scala.swing.event.ButtonClicked
import scala.swing.event.EditDone
import scala.swing.event.Event
import scala.swing.event.SelectionChanged
import rx.lang.scala.Observable
import rx.lang.scala.subjects.BehaviorSubject
import rx.lang.scala.subjects.PublishSubject
import suggestions.observablex.LogHelper
import suggestions.observablex.SchedulerEx
import scala.concurrent.ExecutionContext

/** Basic facilities for dealing with Swing-like components.
*
* Instead of committing to a particular widget implementation
* functionality has been factored out here to deal only with
* abstract types like `ValueChanged` or `TextField`.
* Extractors for abstract events like `ValueChanged` have also
* been factored out into corresponding abstract `val`s.
*/
trait SwingRxApi extends LogHelper {

  
  val poolExecutor = SchedulerEx.poolExecutor
  val poolScheduler = SchedulerEx.poolScheduler
  val eventScheduler = SchedulerEx.SwingEventThreadScheduler

  implicit lazy val executionContext = ExecutionContext.fromExecutor(poolExecutor)
  
  implicit class ComboBoxOps[A](combobox: MyComboBox[A]) {
    
    var item = combobox.selection.item
    
    /** Returns a stream of text field values entered in the given text field.
      *
      * @param field the text field
      * @return an observable with a stream of text field updates
      */
    def selectionObservable: Observable[A] = {
      val channel = BehaviorSubject[A](item)
      
      combobox.peer.addActionListener(new java.awt.event.ActionListener {
        def actionPerformed(e: java.awt.event.ActionEvent) {
          if (combobox.selection.item != item) {
            item = combobox.selection.item
            log.debug(s"SelectionChanged($item)")
            channel.onNext(item)
          } 
        }
      })    
      
//      combobox.selection.subscribe {
//        case SelectionChanged(`combobox`) if combobox.selection.item != item => 
//          item = combobox.selection.item
//          log.debug(s"SelectionChanged($item)")
//          channel.onNext(item)
//        case _ => 
//      } 
      
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
//      field.subscribe {
//        case EditDone(`field`) if field.text != textValue => 
//          textValue = field.text
//          log.debug(s"EditDone($textValue)")
//          channel.onNext(textValue)
//        case _ =>  
//      } 
      
      field.peer.addPropertyChangeListener("text", new java.beans.PropertyChangeListener {
        def propertyChange(evt: java.beans.PropertyChangeEvent) {
          if (field.text != textValue) {
            textValue = field.text
            log.debug(s"EditDone($textValue)")
            channel.onNext(textValue)
          } 
        }
      })
      
      channel
    }
    
    def intObservable = {
      val subject = PublishSubject[Int]()      
      field.textObservable.observeOn(poolScheduler).subscribe{ x => 
        try {
          subject.onNext(x.toInt)
        } catch {
          case e: NumberFormatException => log.debug("not int", x)
        }
      }
      subject
    }
  }

  implicit class ButtonOps(button: Button) {

    /** Returns a stream of button clicks.
     *
     * @param field the button
     * @return an observable with a stream of buttons that have been clicked
     */
    def clickObservable: Observable[Button] = {
      val channel = PublishSubject[Button]()
      
      button.peer.addActionListener(new java.awt.event.ActionListener {
        def actionPerformed(e: java.awt.event.ActionEvent) {
          log.debug(s"ButtonClicked()")
          channel.onNext(button)
        }
      })       
//      button.subscribe {
//        case ButtonClicked(`button`)  => 
//          log.debug(s"ButtonClicked()")
//          channel.onNext(button)
//        case _ =>
//      }
        
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
      
      checkbox.peer.addActionListener(new java.awt.event.ActionListener {
        def actionPerformed(e: java.awt.event.ActionEvent) {
          if (selected != checkbox.selected) {
            selected = checkbox.selected
            log.debug(s"Checkbox($selected)")
            channel.onNext(selected)
          } 
        }
      })       
//      checkbox.subscribe {
//        case ButtonClicked(`checkbox`) if selected != checkbox.selected => 
//          selected = checkbox.selected
//          log.debug(s"Checkbox($selected)")
//          channel.onNext(selected)
//        case _ => 
//      }
        
      channel  
    }

  }  

}