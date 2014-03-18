//package suggestions.observablex
//
//import rx.lang.scala.Observable
//import rx.lang.scala.Observer
//
//class LastSubject[T](observable: Observable[T]) {
//  
//  var _value: T
//  
//  def value = synchronized {
//    _value
//  }
//  
//  def value_= (value: T): Unit = synchronized {
//    _value = value
//  }
//  
//  observable.subscribe{
//    (onNext: T) => value = onNext
//  }
//  
//  observable.combineLatest(that)
//  
////	def flatMap[R](f: T => Observable[R]): Observable[R] = {
////	  f(value)
////  }
//}