package b5sim.processor

import scala.io.Source
import b5sim.model._
import b5sim.gui._
import suggestions.observablex.LogHelper
import scala.math.Ordering

object SectorProcessor extends App with LogHelper {
  
  def loadFragments: Iterator[Array[String]] = {
	  val is = this.getClass.getResourceAsStream("/sectorDetail1.txt")
		val src = Source.fromInputStream(is)
		val fragments = src.getLines().map(line => line.split("\t"))
		for {
		  fragment <- fragments
		  if !fragment.isEmpty && !fragment(0).isEmpty
		  unit = { if (fragment.size != 9) println("Invalid line: " + fragment.mkString(" ")) }
		  if fragment.size == 9
		} yield fragment
  }    
  
  def attackItems(fragments: Iterator[Array[String]]): Vector[AttackItem] = {
    (for {
      row <- fragments
      if row(0) != "Meno lode/letky"
      item = newAttackItem(row)
      if item.isDefined
    } yield item.get).toVector
  }
  
  
  def newAttackItem(row: Array[String]): Option[AttackItem] = try {
    Some(AttackItem(row))
  } catch {
    case e: Throwable => 
      println(row.toVector); e.printStackTrace()
      None
  }
  
  
  
//  def simReport(allies: Seq[FleetClass], enemies: Seq[FleetClass]) {
//    val hpf = allies.filter(_.shipClass == ShipClass.Fighter)
//  }
  

  
  val osAllies = false
  val osEnemies = true
  
  val items = attackItems(loadFragments)
  val (itemsAllies, itemsEnemies) = items.partition(_.isAlly)
  val allies = itemsAllies.map(_.toFleetClass)
  val enemies = itemsEnemies.map(_.toFleetClass)
  
  new AttackReporter(allies, enemies, osAllies, osEnemies).report
}

	

case class SectorItem(row: Array[String]) {
  //Vzťah	Vlastník	Skupina	Stav	Počet lodí	Obsah
  val relation = row(0)
  val owner = row(1)
  val groupName = row(2)
  val status = row(3)
  val groupCount = row(4)
  val count = row(5).toInt
  val hp = parseNumber(row(6))
  val (us, ul) = parseAttack(row(7))
  val strategy = parseStrategy(row(8))
  
  def isAlly = !strategy.isUnknown
  
//  def toFleetClass = {
//  	val fullHp = hp * count
//    val us = this.us * count
//    val ul = this.ul * count
//    val price = ship.price
//    val str = if(!strategy.isUnknown) Some(strategy) else None
//    
//    FleetClass(Some(ship.shipClass), Some(ship), str, SpaceShip.label(ship), 
//        count, fullHp, us, ul, status, exp, price)
//  }
  
//  override def toString = s"($shipName, $owner, exp, $status, $ship, $count, ${strategy.fullString})"
}

