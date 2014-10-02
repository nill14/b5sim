package suggestions
package observablex

import java.util.concurrent.Executor
import rx.lang.scala.{ ImplicitFunctionConversions, Subscription }
import rx.lang.scala.Scheduler
import languageFeature.implicitConversions
import rx.lang.scala.Scheduler
import java.util.concurrent.Executors
import javax.swing.SwingUtilities
import rx.schedulers.Schedulers
import rx.lang.scala.Scheduler
import rx.lang.scala.Worker
import scala.concurrent.duration.Duration
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.LinkedBlockingQueue

object SchedulerEx {

  
  def fromExecutor(executor: Executor): Scheduler = new Scheduler { val asJavaScheduler = Schedulers.from(executor) }
  
  val SwingEventThreadScheduler = new Scheduler {
    val timer = new java.util.Timer
    class SwingTask(action: rx.functions.Action0, subscription: rx.Subscription) extends java.util.TimerTask with LogHelper {
      override def run() {
//        log.debug("scheduling action {}", action.hashCode.toHexString)
        javax.swing.SwingUtilities.invokeLater(new Runnable {
          def run() {
              action.call();
//              log.debug("called action {}", action.hashCode.toHexString)
          }
        })
      }
    }
    
         
    
    lazy val asJavaScheduler = new rx.Scheduler {
      def createWorker = new rx.Scheduler.Worker {
        val subscription = Subscription {
          timer.cancel();
        }
        
        def unsubscribe: Unit = subscription.unsubscribe
        def isUnsubscribed: Boolean = subscription.isUnsubscribed
        
        def schedule(action: rx.functions.Action0): rx.Subscription = {
          val subscription = rx.subscriptions.BooleanSubscription.create()
          timer.schedule(new SwingTask(action, subscription), 0L)
          subscription
        }
        
        def schedule(action: rx.functions.Action0, delayTime: Long, unit: java.util.concurrent.TimeUnit): rx.Subscription = {
          val subscription = rx.subscriptions.BooleanSubscription.create()
          timer.schedule(new SwingTask(action, subscription), unit.toMillis(delayTime))
          subscription
        }
      }
      
    }
    
  }
  
  
  class MyExecutor(nThreads: Int) extends ThreadPoolExecutor(nThreads, nThreads,
                                      0L, TimeUnit.MILLISECONDS,
                                      new LinkedBlockingQueue[Runnable]()) with LogHelper {
    
    override def execute(r: Runnable) = {
//      log.debug(s"execute $r");
      super.execute(r)
    }  
    
    override def beforeExecute(t: Thread, r: Runnable) {
//      log.debug(s"beforeExecute ${r.hashCode.toHexString}");
    }
    
    override def afterExecute(r: Runnable, t: Throwable) {
//      log.debug(s"afterExecute ${r.hashCode.toHexString} $t");
    }
  }
  
  
  val poolExecutor = new MyExecutor(20)
  val poolScheduler: Scheduler = fromExecutor(poolExecutor)
  
  
  
  
}