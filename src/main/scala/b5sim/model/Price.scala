package b5sim.model

case class Price(ti: Long, q40: Long, cr: Long) {

  def *(count: Int) = Price(ti * count, q40 * count, cr * count)
    def *(ratio: Double) = Price(
        (ti * ratio).toLong, 
        (q40 * ratio).toLong, 
        (cr * ratio).toLong)
}