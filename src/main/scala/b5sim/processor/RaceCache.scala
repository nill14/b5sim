package b5sim.processor

import b5sim.model.Race

object RaceCache {

  private val raceCache: Map[String, Race.Value] = {
		val builder = Vector.newBuilder[(Race.Value, Array[String])]

		builder += Race.Shadow -> Array("Vampire", "Spitfire", "Death Cloud", "Battle Crab")
    builder += Race.Vorlon -> Array("Lightning", "Ambassador", "Star Dreadnought", "Planet Killer")
    builder += Race.Centaur -> Array("Sentri Fighter", "Primus Cruiser","Vorchan Cruiser",
        "Covran Gunship", "Covran Scout", "Kutai Gunship","Rutharian Strike")
    builder += Race.Narn -> Array("Gorith Fighter", "Bin'Tak", "Th'Nor", "G'Quan", "Sho'kar Scout", "Frazi", "Toreth")
    builder += Race.Minbar -> Array("Sharlin", "Torotha Assault", "Shargoti", "Morshin Carrier", "Tinashi Frigate", "Nial Fighter")
    builder += Race.Drakh -> Array("Drakh Advanced", "Mother Ship", "Drakh Raider", "Drakh Cruiser", "Drakh Carrier", "Drakh Shuttle")
    builder += Race.Human -> Array("Warlock Destroyer", "Hyperion Cruiser", "Starfury", "Omega Destroyer", "Nova Dreadnought", "Olympus Corvette")
    
    
		val pairs = for {
		  (race, seq) <- builder.result    
		  str <- seq
		} yield str -> race
		
		pairs.toMap
  }
  
  def getRace(name: String) = raceCache.getOrElse(name, {
    println(s"missing key $name")
    null
  })  
  
}