package b5sim.model

case class FleetItem(val ship: SpaceShip, val count: Int, val strategy: Strategy) {

  val exp = 0.50
  
  def countHitpoints = ship.hitpoints * count

  def fighterAttack = (ship.attackFighter * count * exp).toInt

  def shipAttack = (ship.attackShip * count * exp).toInt
  
  def hp = countHitpoints
  def us = fighterAttack
  def ul = shipAttack

  def countPrimaryAttacks: Int = strategy.primary match {
    case ShipClass.Fighter => fighterAttack
    case _ => shipAttack
  }
  
  def sumTitanium = ship.price.ti * count
  def sumQuantium = ship.price.q40 * count
  def sumCredits = ship.price.cr * count
  
  def fighterCapacity = ship.fighterCapacity * count

}