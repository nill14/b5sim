package b5sim.model

object Strategy {
//  type T = Target.Value
  import ShipClass._
  

  
  def generate: Seq[Strategy] = {
		ShipClass.values.map{ Strategy(_) }.toVector
  }
  
  val strategies = generate
 
  def strategyFor(ship: SpaceShip) = ship.shipClass match {
    
    case Fighter => Strategy(Cruiser)
    case Cruiser => Strategy(Fighter)
    case Ship 	 => Strategy(Destroyer)
    case Destroyer => Strategy(Ship)
  }
  
  def main(args: Array[String]) {
  	strategies foreach println
  }
  
  def of(input: String*): Strategy = {
    val targets = for (letter <- input) yield ShipClass.of(letter)
    Strategy(targets)
  }
  
  def apply(target: ShipClass.Value): Strategy = Strategy(Array(target))
  def apply(): Strategy = Strategy(Vector.empty)
}


case class Strategy(targets: Seq[ShipClass.Value]) {
  
  
  def isUnknown = targets.isEmpty
  
  def primary: ShipClass.Value = targets.head
  
  val fullString = 
    if (isUnknown) "-"
    else targets.map(ShipClass.label).mkString(";")
  
  override lazy val toString = 
    if (isUnknown) "-"
    else ShipClass.label(primary)
}