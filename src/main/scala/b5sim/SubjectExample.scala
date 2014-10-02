package b5sim

import rx.lang.scala.Observable
import rx.lang.scala.subjects.PublishSubject

object SubjectExample extends App {
	
	val subject = PublishSubject[Int]()            
	val initial = Observable.from(Array(1,2,3,4))     
	val target = initial ++ subject   // concat the observables
	
	val subscription1 = target subscribe(println(_))
	val subscription2 = target subscribe(println(_))
	
	subject.onNext(5)    // emit '5'
	subject.onNext(6)    // emit '6'
}