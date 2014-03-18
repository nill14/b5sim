package b5sim.processor

object StateMachine {

  trait Token
  class ParsingException(str: String) extends Exception(str)
  
  type StateFunction = (List[Token], String) => (List[Token], StateFunctionWrapper)
  implicit class StateFunctionWrapper(val f: StateFunction ) extends StateFunction {
  	
    def apply(tokens: List[Token], input: String) = f(tokens, input)
  }

  lazy val initialState: StateFunction = (tokens, input) => input match {
    case "Ahoj" => (new Token{} :: tokens, initialState)
  }  
  
  def stateMachine(initialState: StateFunction, fragments: Iterator[String], exitStates: Set[StateFunction]): List[Token] = {
    stateMachine(initialState, List(), fragments)(exitStates)
  }
  
  def stateMachine(initialState: StateFunction, fragments: Iterator[String]): List[Token] = {
    stateMachine(initialState, List(), fragments)(Set(initialState))
  }
  
  private def stateMachine(state: StateFunction, tokens: List[Token], fragments: Iterator[String])
  		(implicit exitStates: Set[StateFunction]): List[Token] = {
    
    if (fragments.hasNext) {
      val fragment = fragments.next
//      println(fragment)
      val (newTokens, newState) = state(tokens, fragment)
//      print(newTokens.head);identify(newState.f)
      stateMachine(newState, newTokens, fragments)
    }
//    else if (!exitStates.contains(state)) {
//      identify(state)
//      exitStates.foreach(identify)
//      state(null, null)
//      throw new ParsingException("Unfinished stream of data!")
//    }
//    else tokens
    else tokens
  }  
  
  private def identify(state: StateFunction) = println(System.identityHashCode(state))
  
//  object Shortcut {
//    def apply(str: String) = s"[$str]"
//    private val shortcutRegex = "\\[(\\w+)\\]".r
//    def unapply(shortcut: String): Option[String] = shortcut match {
//      case `shortcutRegex`(str) => Some(str)
//      case _ => None
//    }
//  }
 
  type SemanticAnalysis = (List[Token]) => (List[Token], SemanticNode)
  case class SemanticNode(children: SemanticNode*)
  
  
  
}