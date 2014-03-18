package b5sim.processor

import b5sim.model.ShipClass
import b5sim.model.Strategy
import b5sim.model.SpaceShip
import b5sim.model.Ships
import b5sim.model.FleetItem

class AttackReporter(allies: Seq[FleetClass], enemies: Seq[FleetClass], 
    bonusAlly: Boolean, bonusEnemy: Boolean) {

	def report() {
	  val shipAllies = groupByShips(allies)
	  val shipEnemies = groupByShips(enemies)
	      
    val totalAllies = FleetClass.create("Grand Total - allies", shipAllies)
    val totalEnemies = FleetClass.create("Grand Total - enemies", shipEnemies)
    
	  val classAllies = groupByClasses(shipAllies)
	  val classEnemies = groupByClasses(shipEnemies)
	  val attacks = groupByTargets(allies, availableTargets(classEnemies))
	  
    println("Ships allies")
	  println("=======================================================================")
	  shipsReport(shipAllies)
	  println
	  shipsReport(classAllies.filter(_.ship.isEmpty))
	  println
	  
	  println("Ships enemies")
	  println("=======================================================================")
	  shipsReport(shipEnemies)
	  println
	  shipsReport(classEnemies.filter(_.ship.isEmpty))
	  println   
	  
	  println("Summary")
	  println("=======================================================================")
	  shipsReport(Array(totalAllies, totalEnemies))
	  println(priceReport("allies", Array(totalAllies)))
	  println(priceReport("enemies", Array(totalEnemies)))
	  println
	  
	  println("attack report")
	  println("=======================================================================")  
	  attackReport(attacks, classEnemies, totalEnemies)
	  println
	  
	  println("Simulation (ally | enemy)")
	  println("=======================================================================")
	  simReport(totalAllies, totalEnemies)
	}
  
  def fleetClassReport(item: FleetClass, detail: Boolean = true): String = {
	  val hp = numFmt(item.hp)
	  val us = numFmt(item.us)
	  val ul = numFmt(item.ul)
	  val exp = ratioFmt(item.exp)
	  val status = pctFmt(item.status)
	  val rs = ratioFmt(item.ratioUs)
	  val rl = ratioFmt(item.ratioUl)
	  
	  val append = if (detail) f" exp: $exp, $status%4s," else ""
		
	  f"${item.count}%3d ${item.name}%-22s : $hp%12s HP, ($us%10s / $ul%10s),$append $rs / $rl"
  }  
  
  
  def priceReport(label: String, items: Seq[FleetClass]): String = {
    val ti = numFmt(items.map(x => Math.round(x.price.ti * x.status)).sum)
    val q40 = numFmt(items.map(x => Math.round(x.price.q40 * x.status)).sum)
    val cr = numFmt(items.map(x => Math.round(x.price.cr * x.status)).sum)
    f"$label%-25s $ti Ti, $q40 Q40, $cr Cr"
  }    
  
  def priceReport(label: String, item: FleetClass, status: Double): String = {
    val ti = Math.round(item.price.ti * status)
    val q40 = Math.round(item.price.q40 * status)
    val cr = Math.round(item.price.cr * status)
    f"$label%-25s ${numFmt(ti)} Ti, ${numFmt(q40)} Q40, ${numFmt(cr)} Cr"
  }    
  
  def simReport(ally: FleetClass, enemy: FleetClass) {
    def fmt(x: FleetClass) = f"${numFmt(x.hp)}%10s hp, ${numFmt(x.ul)}%10s ul, [${pctFmt(x.status)}%4s]"
    def print(i: Int, a: FleetClass, b: FleetClass) = println(f"$i%2d. ${fmt(a)} | ${fmt(b)}")
    
    var a = ally
    var e = enemy
    var i = 0
    
    print(i, a, e)
    
    while (a.hp > 0 && e.hp > 0) {
      val hpE = if (a.ul > e.hp) 0 else (e.hp - a.ul)
      val hpA = if (e.ul > a.hp) 0 else (a.hp - e.ul)
      
      a = a.copy( status = 1.0 * hpA / a.fullHp )
      e = e.copy( status = 1.0 * hpE / e.fullHp )
      i += 1
      
      print(i, a, e)
    }
    
    val alliesPrice = priceReport("Damage allies", a, ally.status - a.status)
    val enemiesPrice = priceReport("Damage enemies", e, enemy.status - e.status)
//    println(f"$alliesPrice%15s | $enemiesPrice%15s")
    println(alliesPrice)
    println(enemiesPrice)
  }
  
//  def bonus: Double = if (isDefenseStation) 1.2 else 1.0
  
  def groupByClasses(items: Seq[FleetClass]): Seq[FleetClass] = {
    val groups = collectGroupBySorted(items, (x: FleetClass) => x.shipClass)(ShipClass.ValueOrdering)
    val res = for {
      (group, seq) <- groups
//      if seq.size > 1
    } yield {
      val name = if (seq.size > 1) s"[${ShipClass.label(group)}] (sum) ${group.toString}"
      else seq.head.name
      
      FleetClass.create(name, seq)
    }     
    res
  }  
  
  def shipsReport(items: Seq[FleetClass]) = items foreach {x => println(fleetClassReport(x, true)) }
  
  def attackReport(attacks0: Array[(ShipClass.Value, Long)], classEnemies: Seq[FleetClass], totalEnemies: FleetClass) {
    val enemies = classEnemies :+ totalEnemies
    val groups = enemies.groupBy(_.shipClass)
//      collectAndGroupBy(classEnemies, (x: FleetClass) => x.shipClass)
		val attacks1: Vector[Long] = ShipClass.groupItems(attacks0.toMap, 0L).map(_._2).toVector
		val attacks = attacks1 :+ attacks1.sum
    
    val (fullHps, hps, status0) = (for {
      cls <- ShipClass.values.toVector :+ null
      group = groups.get(Option(cls))
    } yield {
      group match {
        case Some(item :: `Nil`) => (item.fullHp, item.hp, item.status)
        case Some(seq) => 
          val item = FleetClass.create(null, seq)
          (item.fullHp, item.hp, item.status)
        case None => (0L, 0L, 0.0)
      }
    }).unzip3
    
    
  	val pairs = attacks.zip(hps)
  	val wasted0 = pairs.map{ case (attack, hp) => if (hp > attack) 0L else attack - hp }
  	val remaining0 = pairs.map{ case (attack, hp) => if (attack > hp) 0L else hp - attack  }
  	val wasted = wasted0.init :+ wasted0.init.sum
  	val remaining = remaining0.init :+ remaining0.init.sum
  	
    val status1 = remaining.zip(fullHps).map{ 
  	  case (part, total) => if (total != 0) 1.0 * part / total else 0.0
  	} 
  	
    printColumns("HP enemies", "[S/K/L/D]", hps, longFmt)
    printColumns("Attack ally", "[S/K/L/D]", attacks, longFmt)
    
    printColumns("Wasted", "[S/K/L/D]", wasted, longFmt)
    printColumns("Remaining", "[S/K/L/D]", remaining, longFmt)
    
    printColumns("Status0", "[S/K/L/D]", status0, pctFmt)
    printColumns("Status1", "[S/K/L/D]", status1, pctFmt)
  }  
  
  
  def printColumns[A](label: String, suffix: String, seq: Seq[A], fmt: A => String) {
    println(f"${label}%-11s ${suffix} : ${mkString(seq.map(fmt), " | ", 10)}") 
  }  
  
  def availableTargets(items: Seq[FleetClass]): Set[ShipClass.Value] = {
    items.map(_.shipClass).flatten.toSet
  }
  
  def getTarget(strategy: Strategy, availableTargets: Set[ShipClass.Value]): ShipClass.Value = {
    strategy.targets.find(availableTargets.contains).get
  }  
  
	def groupByTargets(items: Seq[FleetClass], availableTargets: Set[ShipClass.Value]): Array[(ShipClass.Value, Long)] = {
    val groups = items.map{
      item => 
        val target = getTarget(item.strategy.get, availableTargets)
        val attack = target match {
          case ShipClass.Fighter => item.us
          case _ => item.ul
        }
        target -> attack
    }.groupBy(_._1)
    
    for {
      (cls, attacks) <- ShipClass.groupItems(groups, Vector.empty)
    } yield { (cls, attacks.map(_._2).sum) }
  }  
	
  def groupByShips(items: Seq[FleetClass]): Seq[FleetClass] = {
    val groups = collectGroupBySorted(items, (x: FleetClass) => x.ship)(Ships.ShipsOrdering)
    for {
      (ship, seq) <- groups
      name = SpaceShip.label(ship)
    } yield {
      FleetClass.create(name, seq)
    }
  }  	
}