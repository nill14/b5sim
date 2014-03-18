package b5sim

import scala.collection.immutable.Iterable
import rx.lang.scala._
import rx.lang.scala.Observable
import rx.lang.scala.Observer
import rx.lang.scala.Scheduler
import rx.lang.scala.Scheduler
import rx.lang.scala.Scheduler
import rx.lang.scala.Subscription
import rx.lang.scala.Subscription
import suggestions.observablex.SchedulerEx
import rx.lang.scala.subjects.PublishSubject
import suggestions.observablex.LogHelper
import rx.lang.scala.concurrency.Schedulers

object RxTest extends App with LogHelper {
  
	implicit val scheduler = SchedulerEx.poolScheduler
  
  val subject = PublishSubject[Int]()
	
	
	subject.subscribeOn(scheduler).subscribe{
	  onNext => log.info(onNext.toString)
	}
	
	subject.subscribe(
	  {(onNext: Int) => log.info(onNext.toString)}, scheduler
	)
	
	
	val it = Iterable.range(1, 20)
	
	for (t <- it) {
	  log.info(s"sending $t")
	  subject.onNext(t)
	}
	  
//	for (t <- it) {
//    Thread.sleep(100)
//    subject.onNext(t)
//  }
	
//	val obs = iterableToObservable(it.map(i => 100 + i))
//	val subs = obs.subscribe{
//	  onNext => println(onNext)
//	}
	
	
//	subs.unsubscribe()
	
	Thread.sleep(2000)
	
//	val infinite = nats()
//	
//	val subscription = from(infinite).subscribe {
//	  x => println(x)
//	}
//	subscription.unsubscribe()
	
//	def iterableToObservable[A](it: Iterable[A]): Observable[A] = Observable {
//	  var cancel = false
//	  (observer: Observer[A]) => {
//	    try {
////	      it.foreach(t => observer.onNext(t))
//	      for (t <- it if !cancel) {
//	        Thread.sleep(100)
//	        observer.onNext(t)
//	      }
//	      observer.onCompleted()
//	    } catch {
//	      case ex: Throwable => observer.onError(ex)
//	    }
//	    Subscription{ 
//	      println("cancel") 
//	      cancel = true 
//	    }
//	  }
//	}
	
	
	def nats(): Iterable[Int] = new Iterable[Int] {
	  var i = -1
	  def iterator() : Iterator[Int] = new Iterator[Int] {
	  	def hasNext = true
	  	def next = {i += 1; i}
	  }
	}
	
	def from[T](seq: Iterable[T])(implicit scheduler: Scheduler): Observable[T] = 
	  Observable { observer => 
      Subscription()
	}
}