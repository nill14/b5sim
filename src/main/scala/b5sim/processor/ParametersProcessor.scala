package b5sim.processor

import scala.io.Source
import b5sim.model._

object ParametersProcessor extends App {
  
  def loadFragments: Iterator[Array[String]] = {
	  val is = this.getClass.getResourceAsStream("/parameters.txt")
		val src = Source.fromInputStream(is)
		val fragments = src.getLines().map(line => line.split("\t"))
		for {
		  fragment <- fragments
		  if !fragment.isEmpty && !fragment(0).isEmpty
		  if fragment(0) != "Trieda"
		  if fragment(0) != "výroby"
		  if fragment(0) != "stíhačov"
		  unit = { if (fragment.size != 10) println("Invalid line: " + fragment.mkString(" ")) }
		  if fragment.size == 10
		} yield fragment
  }    
  
  def spaceShipsItems(fragments: Iterator[Array[String]]): Vector[SpaceShip] = {
    (for {
      row <- fragments
      item = newShipItem(row)
      if item.isDefined
    } yield item.get).toVector
  }  
  
  def newShipItem(row: Array[String]): Option[SpaceShip] = {
    try {
      val shipClass = parseShipClass(row(0)).get
		  val name = row(1)
		  val ti = parseNumber(row(3))
		  val q40 = parseNumber(row(4))
		  val cr = parseNumber(row(5))
		  val numFighters = parseNumber(row(7))
		  val hp = parseNumber(row(8))
		  val (us, ul) = parseAttack(row(9))
		  val race = RaceCache.getRace(name)
		  val price = Price(ti, q40, cr)
		  
		  Some(SpaceShip(race, shipClass, name, hp, us, ul, numFighters, price))
    }  catch {
    case e: Throwable => 
      println(row.toVector); e.printStackTrace()
      None
    }
  }
  
  def loadParameters: Vector[SpaceShip] = {
    spaceShipsItems(loadFragments)
  }
  

  

  
  
  
  def parseShipClass(str: String): Option[ShipClass.Value] = str match {
    case "Stíhač" => Some(ShipClass.Fighter)
    case "Krížnik" => Some(ShipClass.Cruiser)
    case "Loď" => Some(ShipClass.Ship)
    case "Destroyer" => Some(ShipClass.Destroyer)
    case _ => None
  }
  
  loadParameters.foreach(par => println(par))
  
}