package scala.util.parallel

object Timer{
  def time[T]( f: => T ) = {
    val start = System.currentTimeMillis
    val t = f
    println("time: " + (System.currentTimeMillis - start))
    t
  }
}