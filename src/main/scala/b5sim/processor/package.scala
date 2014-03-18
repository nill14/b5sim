package b5sim

import b5sim.model.Strategy
import b5sim.model.SpaceShip
import b5sim.model.Ships
import b5sim.model.ShipClass
import b5sim.model.Price
import java.text.DecimalFormat
import b5sim.model.FleetItem
import b5sim.model.FleetItem
package object processor {

//  def fmtLen(length: Int, any: Any*) = s"%${length}s".format(any)

  private val intFormat = new DecimalFormat("###,###")
  def numFmt(num: Int) = intFormat.format(num)
  def numFmt(num: Long) = intFormat.format(num)
  def longFmt(num: Long) = intFormat.format(num)
  
  private val pctFormat = new DecimalFormat("###%")
  def pctFmt(double: Double) = pctFormat.format(double)
  
  def ratioFmt(double: Double) = "%1.5f".format(double)
  
  def mkString(seq: Seq[_], sep: String, len: Int) = seq.map(x => s"%${len}s".format(x)).mkString(sep)  
    
  
  def parseNumber(str: String): Int = {
    val a = str.replace("'", "")
    if (a.endsWith(" mil")) {
      a.substring(0, a.length - 4).toInt * 1000000
    }
    else a.toInt
  }
  
  def parseAttack(str: String): (Int, Int) = {
    val parts = str.split("\\s*/\\s*")
    val us = parseNumber(parts(0))
    val ul = parseNumber(parts(1))
    (us, ul)
  }
  
  val strategyRegex = "([SKLD]);([SKLD]);([SKLD]);([SKLD])".r
  def parseStrategy(input: String): Strategy = input match {
    case `strategyRegex`(a, b, c, d) => Strategy.of(a, b, c, d)
    case "-" => Strategy()
  }  
  
  val statusRegex = "(\\d+)%".r
  def parseStatus(input: String): Double = input match {
    case `statusRegex`(num) => num.toInt / 100.0 
    case "-" => 1.0
  }
  
  
  val fleetItemRegex = "\\[[SKLD]\\] ([^:]+)".r
  def getShip(str: String): SpaceShip = str match {
    case `fleetItemRegex`(name) => Ships.byName(name).get
    case x => throw new Error(x)
  }  

  def toMultimap[K, V](seq: Seq[(K, V)]): Map[K, Seq[V]] = {
		seq.groupBy( _._1 ).mapValues( _.map( _._2 ) )
  }

  def collectAndGroupBy[A, K](seq: Seq[A], f: A => Option[K]) = {
    val g: PartialFunction[A, (K, A)] = (a: A) => { f(a) match { case Some(k) => (k, a) }}
    toMultimap(seq.collect(g))
  }
  
  def collectGroupBySorted[A, K](seq: Seq[A], f: A => Option[K])(implicit ordering: Ordering[K]) = {
    collectAndGroupBy(seq, f).toVector.sortBy(_._1)
  }
  
}