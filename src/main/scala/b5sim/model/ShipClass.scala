package b5sim.model


object ShipClass extends Enumeration {
  type ShipClass = Value
  val Fighter, Cruiser, Ship, Destroyer = Value
  
  def label(shipClass: ShipClass.Value) = shipClass match {
    case Fighter => "S"
    case Cruiser => "K"
    case Ship 	 => "L"
    case Destroyer => "D"
  }
  
  def of(letter: String): ShipClass.Value = letter match {
    case "S" => Fighter
    case "K" => Cruiser
    case "L" => Ship
    case "D" => Destroyer
  }  
  
  def groupItems[Col](groups: Map[ShipClass, Col], empty: => Col): Array[(ShipClass.Value, Col)] = {
    for {
      cls <- ShipClass.values.toArray
      items = groups.applyOrElse(cls, (x: ShipClass.Value) => empty)
    } yield {
      cls -> items
    }
  }  
  
  def groupByClass[Col <: Seq[FleetItem]](items: Col, empty: => Col): Array[(ShipClass.Value, Seq[FleetItem])] = {
    val groups = items.groupBy(_.ship.shipClass)
    groupItems(groups, empty)
  }
  
}