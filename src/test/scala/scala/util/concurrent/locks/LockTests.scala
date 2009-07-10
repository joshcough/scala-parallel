package scala.util.concurrent.locks

import Implicits._
import Duration._

/**
 * Created by IntelliJ IDEA.
 * User: joshcough
 * Date: Jul 10, 2009
 * Time: 12:07:31 AM
 * To change this template use File | Settings | File Templates.
 */

class LockTest extends ConcurrentTest{
  val lock = new java.util.concurrent.locks.ReentrantLock  
}

/**
 * In this test, the attempter should not be able to get the lock
 * because the holder holds it at the time of the attempt.
 * Because the attempter can't get it, the getOrElse should be executed.
 */
class LockAttemptFailsTest extends LockTest {

  thread("lock holder"){ lock{ waitForTick(2) } }

  thread("lock attempter"){
    waitForTick(1)
    val gotLock = lock.attempt { true } getOrElse { false }
    gotLock mustBe false
  }
}

/**
 * In this test, the attempter should not be able to get the lock
 * because the holder holds it at the time of the attempt.
 * Because the attempter can't get it, the function inside attempt
 * should not be evaluated.
 */
class AttemptFunctionShouldntBeEvaluatedOnFailureTest extends LockTest {

  var attemptBlockEvaluated = false
  var getOrElseBlockEvaluated = false

  thread("lock holder"){ lock{ waitForTick(2) } }

  thread("lock attempter"){
    waitForTick(1)
    lock.attempt { attemptBlockEvaluated = true } getOrElse { getOrElseBlockEvaluated = true }
    attemptBlockEvaluated mustBe false
    getOrElseBlockEvaluated mustBe true
  }
}


/**
 * Simply demonstrate using the Duration methods on Int
 */
class UseIntDurationApi extends LockTest{

  thread("lock holder"){
    lock.attemptFor(5.nanos)   { /*nothing*/ } getOrElse { explode }
    lock.attemptFor(5.micros)  { /*nothing*/ } getOrElse { explode }
    lock.attemptFor(5.millis)  { /*nothing*/ } getOrElse { explode }
    lock.attemptFor(5.seconds) { /*nothing*/ } getOrElse { explode }
    lock.attemptFor(5.minutes) { /*nothing*/ } getOrElse { explode }
    lock.attemptFor(5.hours)   { /*nothing*/ } getOrElse { explode }
    lock.attemptFor(5.days)    { /*nothing*/ } getOrElse { explode }
  }

  def explode = fail("should have gotten lock!")
}