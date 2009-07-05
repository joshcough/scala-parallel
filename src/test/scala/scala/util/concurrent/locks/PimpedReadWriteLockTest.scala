package scala.util.concurrent.locks

import Implicits._

/**
 *
 * @author Josh Cough
 * Date: Jun 30, 2009
 * Time: 8:42:43 AM
 */
class PimpedReadWriteLockTest extends ConcurrentTest{

  val lock = new java.util.concurrent.locks.ReentrantReadWriteLock

  logLevel = debug

  for (i <- 1 to 5) {
    thread("reader("+i+") gets lock first") {
      lock.read {
        // ticks can only happen if all threads are blocked
        // tick 1 happens because writer is explicitly blocked waiting for tick
        // for tick 2 to happen, writer thread must be blocked waiting for lock
        logger.debug.around("using read lock") {waitForTick(2)}
      }
    }
  }

  thread("writer must wait for reader to give up lock"){
    waitForTick(1)
    lock.write{
      logger.debug.around("using write lock"){ tick mustBe 2 }
    }
  }
}




