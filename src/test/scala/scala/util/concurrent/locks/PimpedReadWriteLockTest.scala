package scala.util.concurrent.locks

import java.util.concurrent.locks.{Lock, ReadWriteLock}
import org.scalatest.FunSuite
import org.scalatest.matchers.{MustMatchers, MustBeSugar}

/**
 *
 * @author Josh Cough
 * Date: Jun 30, 2009
 * Time: 8:42:43 AM
 */
class PimpedReadWriteLockTest extends FunSuite with MustMatchers with MustBeSugar{
  test("nothing"){ 
    println("hey") 
    666 mustBe 666
  }
}
