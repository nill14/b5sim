package b5sim.model

import b5sim.processor.ParametersProcessor


object Ships {

	lazy val ships = ParametersProcessor.loadParameters

	private lazy val nameMap = ships.map(x => (x.name, x)).toMap
	
	
	def byRace(race: Race.Value) = ships.filter(_.race == race)
	
	def byName(name: String): Option[SpaceShip] = {
	  nameMap.get(name)
	}
	
	object ShipsOrdering extends Ordering[SpaceShip] {
	    def compare(x: SpaceShip, y: SpaceShip): Int = order(x) - order(y)
	}
	
	private lazy val ordering = (for {
	  i <- 0 to ships.size - 1
	  ship = ships(i)
	} yield (ship, i)).toMap
	
	private def order(ship: SpaceShip): Int = ordering.applyOrElse(ship, (x: SpaceShip) => 0)//ships.indexOf(ship)
}