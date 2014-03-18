package b5sim.processor

import b5sim.model.ShipClass
import b5sim.model.FleetItem
import b5sim.model.Price
import b5sim.model.SpaceShip
import b5sim.model.Strategy
import b5sim.model.ShipClass

  
object FleetClass {
  def emptyByClass(cls: ShipClass.Value) = 
    FleetClass(Option(cls), None, None, cls.toString, 0, 0, 0, 0, 0.0, 0.5, Price(0, 0, 0), 0)
    
  def create(ship: SpaceShip, count: Int): FleetClass = {
    val shipClass = Option(ship.shipClass)
    val shipOpt = Option(ship)
    val strategy = None
    val maxHp = ship.hitpoints * count
    val actHp = ship.hitpoints * count
    val us = ship.attackFighter * count
    val ul = ship.attackShip * count
    val status = 1.0
    val exp = 0.5
    val price = ship.price * count
    val fighterCapacity = ship.fighterCapacity * count
    
    val name = SpaceShip.label(ship)
    FleetClass(shipClass, shipOpt, strategy, name, count, maxHp, us, ul, status, exp, price, fighterCapacity)    
  }
    
  def create(name: String, seq: Seq[FleetClass]): FleetClass = {
    val shipClass = getShipClass(seq)
    val ship = getShip(seq)
    val strategy = getStrategy(seq)
    val maxHp = seq.map( _.fullHp ).sum
    val actHp = seq.map{ _.hp }.sum
    val us = seq.map{ _.fullUs }.sum
    val ul = seq.map{ _.fullUl }.sum
    val status = if (maxHp != 0) actHp.toDouble / maxHp else 0.0
    val expSum = seq.map(ai => 1L * ai.count * ai.exp).sum
    val count = seq.map( _.count ).sum
    val exp = if (count != 0) expSum / count else 0.5
    val prices = seq.map(_.price)
    val ti = prices.map(_.ti).sum
    val q40 = prices.map(_.q40).sum
    val cr = prices.map(_.cr).sum
    val price = Price(ti, q40, cr)
    val fighterCapacity = seq.map(_.fighterCapacity).sum
    
    FleetClass(shipClass, ship, strategy, name, count, maxHp, us, ul, status, exp, price, fighterCapacity)
  }
  
  def createFromFleetItems(name: String, seq: Seq[FleetItem]): FleetClass = {
    val shipClass = getShipClass2(seq)
    val ship = getShip2(seq)
    val strategy = getStrategy2(seq)
    val maxHp = seq.map( _.hp ).sum
    val actHp = seq.map{ _.hp }.sum
    val us = seq.map{ _.us }.sum
    val ul = seq.map{ _.ul }.sum
    val status = if (maxHp != 0) actHp.toDouble / maxHp else 0.0
    val expSum = seq.map(ai => 1L * ai.count * ai.exp).sum
    val count = seq.map( _.count ).sum
    val exp = if (count != 0) expSum / count else 0.5
    val prices = seq.map(_.ship.price)
    val ti = prices.map(_.ti).sum
    val q40 = prices.map(_.q40).sum
    val cr = prices.map(_.cr).sum
    val price = Price(ti, q40, cr)
    val fighterCapacity = seq.map(_.fighterCapacity).sum
    
    FleetClass(shipClass, ship, strategy, name, count, maxHp, us, ul, status, exp, price, fighterCapacity)
  }     
  
 def create(item: FleetItem): FleetClass = {
    FleetClass(Option(item.ship.shipClass), Option(item.ship), Option(item.strategy), 
        item.ship.toString, item.count,
        item.countHitpoints, 1L * item.count * item.ship.attackFighter , 
        1L * item.count * item.ship.attackShip,
        1.0, 0.5, item.ship.price, item.fighterCapacity)
 }
  
  private def getShipClass(seq: Seq[FleetClass]): Option[ShipClass.Value] = {
    val set = seq.map(_.shipClass).toSet
    if (set.size == 1) set.head
    else None
  }
  private def getShipClass2(seq: Seq[FleetItem]): Option[ShipClass.Value] = {
    val set = seq.map(_.ship.shipClass).toSet
    if (set.size == 1) Option(set.head)
    else None
  }  
  private def getShip(seq: Seq[FleetClass]): Option[SpaceShip] = {
    val set = seq.map(_.ship).toSet
    if (set.size == 1) set.head
    else None
  }  
  private def getShip2(seq: Seq[FleetItem]): Option[SpaceShip] = {
    val set = seq.map(_.ship).toSet
    if (set.size == 1) Option(set.head)
    else None
  }    
  private def getStrategy(seq: Seq[FleetClass]): Option[Strategy] = {
    val set = seq.map(_.strategy).toSet
    if (set.size == 1) set.head
    else None
  }
  private def getStrategy2(seq: Seq[FleetItem]): Option[Strategy] = {
    val set = seq.map(_.strategy).toSet
    if (set.size == 1) Option(set.head)
    else None
  }    
}
case class FleetClass(
  val shipClass: Option[ShipClass.Value],
  val ship: Option[SpaceShip],
  val strategy: Option[Strategy],
  val name: String, 
  val count: Int, 
  val fullHp: Long,
  val fullUs: Long,
  val fullUl: Long,
  val status: Double, 
  val exp: Double,
  val price: Price,
  val fighterCapacity: Int) {
//    val strategies: Map[Strategy, Int])

  def hp: Long = Math.round(fullHp * status)
  def us: Long = Math.round(fullUs * status * exp)
  def ul: Long = Math.round(fullUl * status * exp)

  def ratioUs: Double = if (hp != 0) 1.0 * us / hp else 0.0
  def ratioUl: Double = if (hp != 0) 1.0 * ul / hp else 0.0
  
  
}  