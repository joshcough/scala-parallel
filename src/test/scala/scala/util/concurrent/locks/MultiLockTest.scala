package scala.util.concurrent.locks

import Implicits._
import org.scalatest.FunSuite
import org.scalatest.matchers.{MustMatchers, MustBeSugar}
import java.util.concurrent.locks.{ReentrantLock=>JReentrantLock}

/**
 * User: joshcough
 * Date: Jul 12, 2009
 * Time: 12:16:55 AM
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