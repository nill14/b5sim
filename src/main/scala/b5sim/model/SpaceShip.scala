package b5sim.model
import Race._
import ShipClass._

object SpaceShip {
  def label(ship: SpaceShip) = s"[${ShipClass.label(ship.shipClass)}] ${ship.name}"
}

case class SpaceShip(
    val race: Race,
    val shipClass: ShipClass,
    val name: String,
    val hitpoints: Int,
    val attackFighter: Int,
    val attackShip: Int,
    val fighterCapacity: Int,
    val price: Price) {
  
  def this(
    race: Race,
    shipClass: ShipClass,
    name: String,
    hitpoints: Int,
    attackFighter: Int,
    attackShip: Int) = 
      this(race, shipClass, name, hitpoints, attackFighter, attackShip, 0, Price(0, 0, 0)) 
  
  override def toString = SpaceShip.label(this)
  
  def toDebugString = super.toString
}