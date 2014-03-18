//package b5sim.model
//
//case class FleetModel (
//  val fleetItems: Seq[FleetItem],
//  val defenseStation: Boolean,
//  val damageRatio: Seq[Double],
//  val expRatio: Seq[Double]) {
//
//  
//  def this(fleetItems: Seq[FleetItem]) = 
//    this(fleetItems, false, Array(0, 0, 0, 0), Array(0.5, 0.5, 0.5, 0.5))
//    
//  def this() = this(List.empty[FleetItem])    
//}