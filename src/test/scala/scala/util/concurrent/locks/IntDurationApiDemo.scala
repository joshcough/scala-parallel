package scala.util.concurrent.locks

import Implicits._
import Duration._
import org.scalatest.matchers.{MustMatchers, MustBeSugar}
import java.util.concurrent.locks.{ReentrantLock=>JReentrantLock}
import org.scalatest.{DemoSuite}

/**
 * Simply demonstrate using the Duration methods on Int
 * User: joshcough
 * Date: Jul 12, 2009
 * Time: 12:20:35 AM
 */
class IntDurationApiDemo extends DemoSuite with MustMatchers with MustBeSugar{

  demo("duration api addons for int"){
    val lock = new JReentrantLock

    def explode = fail("should have gotten lock!")

    lock.attemptFor(5.nanos)   { /*nothing*/ } getOrElse {explode}
    lock.attemptFor(5.micros)  { /*nothing*/ } getOrElse {explode}
    lock.attemptFor(5.millis)  { /*nothing*/ } getOrElse {explode}
    lock.attemptFor(5.seconds) { /*nothing*/ } getOrElse {explode}
    lock.attemptFor(5.minutes) { /*nothing*/ } getOrElse {explode}
    lock.attemptFor(5.hours)   { /*nothing*/ } getOrElse {explode}
    lock.attemptFor(5.days)    { /*nothing*/ } getOrElse {explode}
  }
}

