package b5sim.processor

import scala.io.Source
import b5sim.model._
import b5sim.gui._
import suggestions.observablex.LogHelper

object ScountProcessor extends App with LogHelper {
  
  def loadFragments: Iterator[Array[String]] = {
	  val is = this.getClass.getResourceAsStream("/scoutCenter2.txt")
		val src = Source.fromInputStream(is)
		val fragments = src.getLines().map(line => line.split("\t"))
		for {
		  fragment <- fragments
		  if !fragment.isEmpty && !fragment(0).isEmpty
//		  if fragment.size == 10
		} yield fragment
  }      
  
//  def rowItems(fragments: Iterator[Array[String]]): Vector[Token] = {
//    (for {
//      row <- fragments
//      if row(0) != "Meno lode/letky"
//      items = newRowItem(row)
//      unit = {println(row); println(items)}
////      if !item.isEmpty
//    } yield GroupNameToken("a")).toVector
//  }
  
	 val namespacedIdRegex = "([a-zA-Z]+):(\\d+)".r
	 val shortcutSectorRegex = "\\[(\\w+)\\]".r
	 val relationRegex = "(\\w|-)/(\\w|-)".r
	 val GroupNameRegex = "([A-Za-z ]+)".r
	 val fleetItemRegex = "\\[[SKLD]\\] ([^:]+): (\\d+)".r
	 val newline = "\n"
	 val fightersOnly = "iba stíhače"
	 val actualFight = "vojnový stav"   
  
//  def newRowItem(row: Array[String]): Seq[Token] = row.toList match {
//    case List() => List.empty
//    case List("Quadrant 13") => List.empty
//    case `shortcutSectorRegex`(short) :: Nil => Array(ShortcutToken(s"[$short]"))
//    case List(`fleetItemRegex`(shipName, count)) => Array(FleetToken(shipName, count.toInt))
//    case `List`(`GroupNameRegex`(groupName)) => Array(SectorToken(groupName))
//    case List(`relationRegex`(a, b),`groupNameRegex`(groupName),`fleetItemRegex`(shipName, count)) => 
//    	Array(GroupNameToken(groupName), FleetToken(shipName, count.toInt))
//    case List("vojnový stav",`relationRegex`(a, b),`groupNameRegex`(groupName),`fleetItemRegex`(shipName, count)) => 
//      Array(GroupNameToken(groupName), FleetToken(shipName, count.toInt))
//  }

  
  def groupReport(group: String, items: Vector[FleetClass]) = {
    
	  val groups0 = collectGroupBySorted(items, (fc: FleetClass) => fc.shipClass)(ShipClass.ValueOrdering)
    //    val groups = ShipClass.groupItems(items.groupBy(_.shipClass.get), Vector.empty)

    def calcFighters(groups: Vector[(ShipClass.Value, Seq[FleetClass])]): Vector[(ShipClass.Value, Seq[FleetClass])] = {
      if (!groups.contains(ShipClass.Fighter)) {
        val fighterCapacity = groups.flatMap(_._2).map(_.fighterCapacity).sum
        val races = groups.flatMap(_._2).map(_.ship).flatten.map(_.race).toSet
        if (races.size == 1) {
          val race = races.head
          val fighters = Ships.byRace(race).filter(_.shipClass == ShipClass.Fighter)
          if (fighters.size >= 1) {
            val fighter = fighters.head
            val fighterGroup = FleetClass.create(fighter, fighterCapacity)
            return ((ShipClass.Fighter, List(fighterGroup))) +: groups
          }
        }
      }
      return groups
    }
	  
	  val groups = calcFighters(groups0)
	  
    val res = for ( (shipClass, seq) <- groups) {
      val name = s"[${ShipClass.label(shipClass)}] (sum) ${shipClass.toString}"
      println(fleetClassReport(FleetClass.create(name, seq)))
    }
    
	  val total = FleetClass.create(group, items)
    println(fleetClassReport(total))
//    println(priceReport(group, total))
    
  }
  
  def scoutReport(groups: Map[String, Vector[FleetClass]]) = {
    for {
      (group, items) <- groups.toArray
//      item <- items
    } yield {
      println(group)
      println("=======================================================================")
      for (item <- items) println(fleetClassReport(item))
      println
      groupReport(group, items)
      println
    }
  }
  
  val reporter = new AttackReporter(List(), List(), false, false)
  def fleetClassReport(fc: FleetClass) = reporter.fleetClassReport(fc, false)
  def priceReport(label: String, item: FleetClass) = reporter.priceReport(label, Array(item))
  
  def parseReport: Map[String, Vector[FleetClass]] = {
    val tokens = stateMachine(State.initialState, List())(loadFragmentsOld)
//    tokens.reverse.filter(unknownToken) foreach println
    val fleetSeq = exec(tokens.reverse.filterNot(ignoreToken))
    
    val fleet = for {
      (group, shipSeq) <- fleetSeq.groupBy{ case (group, ship, count) => group }
    } yield {
    	val value = for {
    		(ship, countSeq) <- shipSeq.groupBy{ case (group, ship, count) => ship }
    		counts = countSeq.map{ case (group, ship, count) => count }
    		count = counts.foldLeft(0){ (acc, count) => acc + count}
    	} yield {
    	  FleetClass.create(ship, count)
    	}
    	val sortedValue = value.toVector.sortBy(_.ship.get)(Ships.ShipsOrdering)
    	(group, sortedValue)
    }
    
//    fleet foreach println
    fleet
  }
  
  def ignoreToken(token: Token): Boolean = token match {
    case NewlineToken => true
    case FighterToken => true
    case FightToken => true
    case RelationToken(_,_) => true
    case UnknownToken(_) => true
    case _ => false
  }
  
  def unknownToken(token: Token): Boolean = token match {
    case UnknownToken(_) => true
    case _ => false
  }  
  
  
  def loadFragmentsOld: Iterator[String] = {
		for { 
		  line <- loadFragments
		  fragment <- ("\n" +: line)
		} yield fragment
  }

  
  
//implicit class RegexContext(sc: StringContext) {
//  def r = new Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
//}  
//r"\\[(\\w+)$shortcut\\]"
  

 
  type Fleet = (String, SpaceShip, Int)
// type State = (String) => Token
  object State extends Enumeration {
    type State = Value
    val initialState, sectorState, relationState, groupState, fleetState, fightState = Value
  }
  import State._
 

  
 def stateMachine(state: State, tokens: List[Token])(fragments: Iterator[String]): List[Token] = {
   if (fragments.hasNext) {
  	 val fragment = fragments.next	
  	 val (token, newState) = (state, fragment) match {
  	   case (`initialState`, `shortcutSectorRegex`(shortcut)) => (SectorToken(shortcut), sectorState)
  	   case (`sectorState`, `relationRegex`(a, b)) => (RelationToken(a, b), relationState)
  	   case (`sectorState`, `newline`) => (NewlineToken, fightState)
  	   case (`fightState`, `actualFight`) => (FightToken, sectorState)
  	   case (`relationState`, groupName) => (GroupNameToken(groupName), groupState)
  	   case (`groupState`, `fightersOnly`) => (FighterToken, fleetState)//initialState
  	   case (`groupState`, `fleetItemRegex`(name, count)) => (FleetToken(name, count.toInt), fleetState)
  	   case (`fleetState`, `fleetItemRegex`(name, count)) => (FleetToken(name, count.toInt), fleetState)
  	   case (`fleetState`, `relationRegex`(a, b)) => (RelationToken(a, b), relationState)
  	   case (`fleetState`, `newline`) => (NewlineToken, fleetState)
  	   case (`initialState`, `newline`) => (NewlineToken, initialState)
  	   case (`fleetState`, x) => (UnknownToken(x), initialState)
  	   case (`initialState`, x) => (UnknownToken(x), initialState)
  	 }
  	 log.debug((state, token).toString)
  	 stateMachine(newState, token :: tokens)(fragments)
   }
   else tokens
 }
  
  def exec(tokens: List[Token]): Seq[Fleet] = execSector(List(), tokens)
  
  def execSector(results: List[Fleet], tokens: List[Token]): Seq[Fleet] = tokens match {
    case SectorToken(sector) :: xs => execGroup(results, xs, sector)
    case `Nil` => results 
  }
  
  def execGroup(results: List[Fleet], tokens: List[Token], sector: String): Seq[Fleet] = tokens match {
    case SectorToken(sector) :: xs => execGroup(results, xs, sector)
  	case GroupNameToken(group) :: xs => execFleet(results, xs, group)
    case `Nil` => results 
  }

  def execFleet(results: List[Fleet], tokens: List[Token], group: String): Seq[Fleet] = tokens match {
    case FleetToken(name, count) :: xs => 
      val fleet = (group, Ships.byName(name).get, count)
      execFleet(fleet :: results, xs, group)
    case `Nil` => results 
    case GroupNameToken(group) :: xs => execFleet(results, xs, group)
    case SectorToken(sector) :: xs => execGroup(results, xs, sector)
  }
  
  scoutReport(parseReport)
  
//  rowItems(loadFragments) foreach println
  
  abstract sealed class Token
  case class SectorToken(sector: String) extends Token
  case class ShortcutToken(shortcut: String) extends Token
  case class FleetToken(name: String, count: Int) extends Token
  case object FighterToken extends Token
  case object FightToken extends Token
  case class RelationToken(a: String, b: String) extends Token
  case class UnknownToken(str: String) extends Token
  case class GroupNameToken(name: String) extends Token
  case object NewlineToken extends Token
}