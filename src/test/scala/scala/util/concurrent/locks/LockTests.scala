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



/**
 * Simply demonstrate using the Duration methods on Int
 */
class UseIntDurationApi extends FunSuite with MustMatchers with MustBeSugar{

  test("demo"){
    val lock = new JReentrantLock
    
    lock.attemptFor(5.nanos)   { /*nothing*/ } getOrElse {explode}
    lock.attemptFor(5.micros)  { /*nothing*/ } getOrElse {explode}
    lock.attemptFor(5.millis)  { /*nothing*/ } getOrElse {explode}
    lock.attemptFor(5.seconds) { /*nothing*/ } getOrElse {explode}
    lock.attemptFor(5.minutes) { /*nothing*/ } getOrElse {explode}
    lock.attemptFor(5.hours)   { /*nothing*/ } getOrElse {explode}
    lock.attemptFor(5.days)    { /*nothing*/ } getOrElse {explode}
  }

  def explode = fail("should have gotten lock!")
}


/**
 * 
 */
class MultiLockTest extends FunSuite with MustMatchers with MustBeSugar{

  val lockA,lockB,lockC = new JReentrantLock

//  test("compose two locks"){
//    val answer: Int = lockA lockB { 42 }
//    answer mustBe 42
//  }
//
//  test("compose three locks"){
//    val answer: Int = lockA lockB lockC { 42 }
//    answer mustBe 42
//  }

  test("lock two locks with and"){
    val answer: Int = (lockA and lockB) { 42 }
    answer mustBe 42
  }

  test("lock two locks with for"){
    val answer: Int = for( a <- lockA; b <- lockB ) yield 42
    answer mustBe 42
  }

  test("lock three locks with and"){
    val answer: Int = (lockA and lockB and lockC) { 42 }
    answer mustBe 42
  }

  test("lock three locks with for"){
    val answer: Int = for( a <- lockA; b <- lockB; c <- lockC ) yield { 42 }
    answer mustBe 42
  }
}