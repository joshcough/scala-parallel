package scala.util.concurrent

sealed abstract class Duration {
  def toJavaNanos: Long
}

object Duration {
  import Math.{MAX_LONG}
  import DurationHelpers._
  
  case class Nanoseconds(length: Long) extends Duration {
    def toJavaNanos = length
  }
  case class Microseconds(length: Long) extends Duration {
    def toJavaNanos = x(length, C1/C0, MAX_LONG/(C1/C0))
  }
  case class Milliseconds(length: Long) extends Duration {
    def toJavaNanos = x(length, C2/C0, MAX_LONG/(C2/C0))
  }
  case class Seconds(length: Long) extends Duration {
    def toJavaNanos = x(length, C3/C0, MAX_LONG/(C3/C0))
  }
  case class Minutes(length: Long) extends Duration {
    def toJavaNanos = x(length, C4/C0, MAX_LONG/(C4/C0))
  }
  case class Hours(length: Long) extends Duration {
    def toJavaNanos = x(length, C5/C0, MAX_LONG/(C5/C0))
  }
  case class Days(length: Long) extends Duration {
    def toJavaNanos = x(length, C6/C0, MAX_LONG/(C6/C0))
  }

  implicit def intWithDurationMethods(i: Int) = new {
    def nanos   = Nanoseconds(i)
    def micros  = Microseconds(i)
    def millis  = Milliseconds(i)
    def seconds = Seconds(i)
    def minutes = Minutes(i)
    def hours   = Hours(i)
    def days    = Days(i)
  }
}


object DurationHelpers {

  import Math.{MAX_LONG, MIN_LONG}

  // Handy constants for conversion methods
  //   from TimeUnit.java
  val C0 = 1L
  val C1 = C0 * 1000
  val C2 = C1 * 1000
  val C3 = C2 * 1000
  val C4 = C3 * 60
  val C5 = C4 * 60
  val C6 = C5 * 24

  def x(d: Long, m: Long, over: Long): Long = {
    if (d > over) MAX_LONG
    else if (d < -over) MIN_LONG
    else d * m
  }
}
