package scala.util.concurrent.locks

import java.util.concurrent.locks.{Lock, ReadWriteLock, ReentrantReadWriteLock}
import org.scalatest.FunSuite
import org.scalatest.matchers.{MustMatchers, MustBeSugar}
import org.scalatest.multi.MultiThreadedSuite
import PimpedReadWriteLock._

/**
 *
 * @author Josh Cough
 * Date: Jun 30, 2009
 * Time: 8:42:43 AM
 */
class PimpedReadWriteLockTest extends FunSuite with MustMatchers with MustBeSugar{

  val lock = new ReentrantReadWriteLock

  test("use withReadLock method on ReadWriteLock"){  lock.withReadLock{ 1 } mustBe 1 }
  test("use withWriteLock method on ReadWriteLock"){ lock.withReadLock{ 2 } mustBe 2 }

  test("use read method on ReadWriteLock"){  lock.read{ 3 } mustBe 3 }
  test("use write method on ReadWriteLock"){ lock.write{ 4 } mustBe 4 }
}

class PimpedReadWriteLockMultiThreadedTest extends MultiThreadedSuite with MustMatchers with MustBeSugar{

  val lock = new ReentrantReadWriteLock

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



