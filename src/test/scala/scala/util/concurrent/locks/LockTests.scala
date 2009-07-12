package scala.util.concurrent.locks

import Implicits._
import Duration._
import org.scalatest.FunSuite
import org.scalatest.matchers.{MustMatchers, MustBeSugar}
import java.util.concurrent.locks.{ReentrantLock=>JReentrantLock}

/**
 * User: joshcough
 * Date: Jul 10, 2009
 * Time: 12:07:31 AM
 */
class LockTests extends ConcurrentTest {

  val lock = new JReentrantLock

  /**
   * In this test, the attempter should not be able to get the lock
   * because the holder holds it at the time of the attempt.
   * Because the attempter can't get it, the getOrElse should be executed.
   */
  test("Lock Attempt Fails") {

    thread("lock holder") {lock {waitForTick(2)}}

    thread("lock attempter") {
      waitForTick(1)
      val gotLock = lock.attempt {true} getOrElse {false}
      gotLock mustBe false
    }
  }

  /**
   * In this test, the attempter should not be able to get the lock
   * because the holder holds it at the time of the attempt.
   * Because the attempter can't get it, the function inside attempt
   * should not be evaluated.
   */
  test("Attempt Function Shouldnt Be Evaluated On Failure"){
    var attemptBlockEvaluated = false
    var getOrElseBlockEvaluated = false

    thread("lock holder") {lock {waitForTick(2)}}

    thread("lock attempter") {
      waitForTick(1)
      lock.attempt {attemptBlockEvaluated = true} getOrElse {getOrElseBlockEvaluated = true}
      attemptBlockEvaluated mustBe false
      getOrElseBlockEvaluated mustBe true
    }
  }
}
