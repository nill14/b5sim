package b5sim.processor

import scala.io.Source
import b5sim.model._
import b5sim.gui._
import suggestions.observablex.LogHelper
import StateMachine._

//examples
//Quadrant 13
//[q13]
//vojnový stav	V/V	Centaurská Republika	[K] Vorchan Cruiser: 4
//[L] Covran Gunship: 17
//[L] Primus Cruiser: 4
//A/A	Kolónia Drakhov	[L] Drakh Advanced: 4
//[L] Drakh Carrier: 1
//A/A	Pozemská Aliancia	[K] Olympus Corvette: 3
//[L] Omega Destroyer: 11
//-/-	Tienske Spoločenstvo	iba stíhače

object ScountCenterProcessor extends App with LogHelper {
  
  def loadFragments: Iterator[Array[String]] = {
	  val is = this.getClass.getResourceAsStream("/scoutCenter2.txt")
		val src = Source.fromInputStream(is)
		val fragments = src.getLines().map(line => line.split("\\s+"))
		for {
		  fragment <- fragments
		  if !fragment.isEmpty && !fragment(0).isEmpty
//		  if fragment.size == 10
		} yield fragment
  }      
  
  object Tokens {
  	case class SectorToken(name: String) extends Token
  	case class ShortcutToken(shortcut: String) extends Token
  	case object FightInSector1Token extends Token
  	case object FightInSectorToken extends Token
  	case class RelationsToken(ours: String, theirs: String) extends Token
  	case class ShipClassToken(cls: String) extends Token
  	case class PartyToken(party: String) extends Token
  	case object Fighter1Token extends Token
  	case object FighterToken extends Token
  	case class ShipToken(ship: String) extends Token
	  case class FleetToken(name: String, count: Int) extends Token
//	  case class UnknownToken(str: String) extends Token
//	  case object NewlineToken extends Token    
  }
  import Tokens._
  
  object Regexes {
  	val nameRegex = "([^:\\[]+)".r
		val shortcutRegex = "\\[(\\w+)\\]".r
		val runningFight1 = "vojnový"     
	  val runningFight2 = "stav"     
		val relationsRegex = "(\\w|-)/(\\w|-)".r
		val shipClassRegex = "\\[([SKLD])\\]".r
		val fightersOnly1 = "iba"
		val fightersOnly2 = "stíhače"
		val shipNameTerm = "([A-Za-z0-9' ]+):".r
	  val decimalRegex = "(\\d+)".r
  }
  import Regexes._
  
  def stateMachine(fragments: Iterator[String]): List[Token] = {
    val exitStates = Set(sectorState, decisionState)
    val tokens = StateMachine.stateMachine(sectorState, fragments, exitStates)
    tokens
  }	
  
  
  def sectorState: StateFunction = (tokens, input) => (input, tokens) match {
    case (`nameRegex`(name), SectorToken(prefix) :: xs) => 
      (SectorToken(s"$prefix $name") :: xs, sectorState) 
    case (`nameRegex`(name), xs) => (SectorToken(name) :: tokens, sectorState)      
    case (`shortcutRegex`(shortcut), xs) => (ShortcutToken(shortcut) :: tokens, relationsState)
  }
  
  def relationsState: StateFunction = (tokens, input) => (input, tokens) match {
  	case (`runningFight1`, _) => (FightInSector1Token :: tokens, relationsState)    
  	case (`runningFight2`, FightInSector1Token :: xs) => (FightInSectorToken :: xs, relationsState)    
  	case (`relationsRegex`(ours, theirs), _) => (RelationsToken(ours, theirs) :: tokens, partyState)
  }  
  
  def partyState: StateFunction = (tokens, input) => (input, tokens) match {
    case (`nameRegex`(name), PartyToken(prefix) :: xs) => 
      (PartyToken(s"$prefix $name") :: xs, partyState) 
    case (`nameRegex`(name), _) => 
      (PartyToken(name) :: tokens, partyState)
    case (`shipClassRegex`(cls), xs) => 
      (ShipClassToken(cls) :: tokens, fleetState)    
  }
  
  def fleetState: StateFunction = (tokens, input) => (input, tokens) match {
    case (`fightersOnly1`, PartyToken(prefix) :: xs) => (Fighter1Token :: tokens, fleetState) 
    case (`fightersOnly2`, Fighter1Token :: xs) => (FighterToken :: xs, fleetState)
    case (`shipClassRegex`(cls), PartyToken(prefix) :: xs) => 
      (ShipClassToken(cls) :: tokens, fleetState)
    case (`nameRegex`(name), ShipClassToken(cls) :: xs) =>
      (ShipToken(name) :: tokens, fleetState)
    case (`shipNameTerm`(name), ShipClassToken(cls) :: xs) =>
      (ShipToken(name) :: xs, countState)         
    case (`shipNameTerm`(name), ShipToken(prefix) :: xs) =>
      (ShipToken(s"$prefix $name") :: xs, countState)      
  }  
  
  def countState: StateFunction = (tokens, input) => (input, tokens) match {  
    case (`decimalRegex`(num), ShipToken(name) :: xs) => 
      (FleetToken(name, num.toInt) :: xs, decisionState)
  }
    
  def decisionState: StateFunction = (tokens, input) => (input, tokens) match {  
    case (`shipClassRegex`(cls), FleetToken(name, count) :: xs) => 
      (ShipClassToken(cls) :: tokens, fleetState)    
    case (`relationsRegex`(ours, theirs), FleetToken(name, count) :: xs) => 
      (RelationsToken(ours, theirs) :: tokens, partyState)
    case (`nameRegex`(name), FleetToken(ship, count) :: xs) => 
      (SectorToken(name) :: tokens, sectorState) 
  }

  stateMachine(loadFragments.flatten) foreach println
  
  def semantics(tokens: List[Token]): Unit = tokens match {
    case SectorToken(sector) :: xs => println("addSector"); semantics(xs)
    case FleetToken(name, count) :: xs => (Ships.byName(name), count); semantics(xs)
  }
  
  case class SectorInfo(val sectorName: String, val shortcut: String, val isFight: Boolean)
  case class PartyInfo(val partyName: String, ours: String, theirs: String) {
    def isAlly = ours == "-" || ours == "A"
  }
//  
//  def sectorAnalysis: SemanticAnalysis = {
//    case SectorToken(sector) :: ShortcutToken(short) :: xs => 
//      val (tokens, node) = partyAnalysis(xs)
//      SemanticNode(children)semantics(xs)
//  }
//  
//  def partyAnalysis: SemanticAnalysis = (tokens) => tokens match {
//    case SectorToken(sector) :: ShortcutToken(short) :: xs => 
//      SemanticNode(children)semantics(xs)
//  }  
  
//  def ignoreToken(token: Token): Boolean = token match {
//    case FighterToken => true
//    case FightToken => true
//    case RelationToken(_,_) => true
//    case UnknownToken(_) => true
//    case _ => false
//  }
//  
//  def unknownToken(token: Token): Boolean = token match {
//    case UnknownToken(_) => true
//    case _ => false
//  }  
  

  
  

}