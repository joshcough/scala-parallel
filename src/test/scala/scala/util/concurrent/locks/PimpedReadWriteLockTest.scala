package scala.util.concurrent.locks

import org.scalatest.FunSuite
import org.scalatest.matchers.{MustMatchers, MustBeSugar}
import org.scalatest.multi.MultiThreadedSuite
import Implicits._

/**
 *
 * @author Josh Cough
 * Date: Jun 30, 2009
 * Time: 8:42:43 AM
 */
class PimpedReadWriteLockTest extends FunSuite with MustMatchers with MustBeSugar{

  val lock = new java.util.concurrent.locks.ReentrantReadWriteLock

  test("use read lock on ReadWriteLock"){  lock.read{ 1 } mustBe 1 }
  test("use write lock on ReadWriteLock"){ lock.write{ 2 } mustBe 2 }
}

class PimpedReadWriteLockMultiThreadedTest extends MultiThreadedSuite with MustMatchers with MustBeSugar{

  val lock = new java.util.concurrent.locks.ReentrantReadWriteLock

  // uncomment me for some debugging!
  //logLevel = debug

  thread("reader gets lock first"){
    lock.read{
      logger.debug.around("using read lock"){ waitForTick(2) }
    }
  }

  thread("writer must wait for reader to give up lock"){
    waitForTick(1)
    lock.write{
      logger.debug.around("using write lock"){ tick mustBe 2 }
    }
  }
}



