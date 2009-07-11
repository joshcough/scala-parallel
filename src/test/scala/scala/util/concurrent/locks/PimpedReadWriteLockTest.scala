package scala.util.concurrent.locks

/**
 * How this test works:
 *
 * 15 reader threads are created, 5 named, 10 anonymous
 *  1 writer thread is created
 *
 * ticks can only happen if ALL threads are blocked
 *
 * tick 1 happens because:
 *   all reader threads are blocked waiting for tick 1
 *   writer is also explicitly blocked waiting for tick 1
 *
 * tick 2 happens because:
 *   all reader threads are blocked waiting for tick 2
 *   writer thread must be blocked waiting for lock
 *
 * @author Josh Cough
 * Date: Jun 30, 2009
 * Time: 8:42:43 AM
 */
class PimpedReadWriteLockTest extends ConcurrentTest {

  val lock = new java.util.concurrent.locks.ReentrantReadWriteLock
  import Implicits.RichReentrantReadWriteLock

  logLevel = everything

  test("demonstrate my pimpness") {
    5.threads("reader thread") {
      lock.read {
        logger.debug.around("using read lock") {waitForTick(2)}
      }
    }

    10 threads {
      lock.read {
        logger.debug.around("using read lock") {waitForTick(2)}
      }
    }

    thread("writer thread") {
      waitForTick(1)
      lock.write {
        logger.debug.around("using write lock") {tick mustBe 2}
      }
    }
  }
}




