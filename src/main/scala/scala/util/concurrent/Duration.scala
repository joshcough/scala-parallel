package scala.util.concurrent

import java.util.concurrent.TimeUnit

sealed abstract class Duration(val length: Long, val timeUnit: TimeUnit)

object Duration {
  case class Nanoseconds(override val length: Long) extends Duration(length, TimeUnit.NANOSECONDS)
  case class Microseconds(override val length: Long) extends Duration(length, TimeUnit.MICROSECONDS)
  case class Milliseconds(override val length: Long) extends Duration(length, TimeUnit.MILLISECONDS)
  case class Seconds(override val length: Long) extends Duration(length, TimeUnit.SECONDS)
  case class Minutes(override val length: Long) extends Duration(length, TimeUnit.MINUTES)
  case class Hours(override val length: Long) extends Duration(length, TimeUnit.HOURS)
  case class Days(override val length: Long) extends Duration(length, TimeUnit.DAYS)
}
