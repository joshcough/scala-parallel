package org.scalatest.multi

import java.util.concurrent._
import Thread.State._
import scala.util.concurrent.PimpedThreadGroup._
import scala.collection.jcl.Conversions.convertList

/**
 *
 */
trait Conductor extends PrintlnLogger {
  logLevel = nothing

  type Tick = Int

  /**
   * The metronome used to coordinate between threads. This clock
   * is advanced by the clock thread..
   * The clock will not advance if it is frozen.
   */
  private val clock = new Clock

  /**
   * a BlockingQueue containing the first Error/Exception that occured
   * in thread methods or that are thrown by the clock thread
   */
  protected val errors = new ArrayBlockingQueue[Throwable](20)

  /////////////////////// thread management start //////////////////////////////

  // place all threads in a new thread group
  protected val threadGroup = new ThreadGroup("MTC-Threads")

  // all the threads in this test
  // TODO: Potential problem with should only be accessed by main thread, but not enforcing. Should
  // enforce it by throwing an exception if accessed (Set or read) by any other thread. Also, should
  // make sure it is only set and possibly accessed during construction
  protected val threads = new CopyOnWriteArrayList[Thread]()

  // the main test thread
  protected val mainThread = currentThread

  /**
   *
   */
  def thread[T](f: => T): Thread = thread("thread" + threads.size) {f}

  /**
   *
   */
  def thread[T](desc: String)(f: => T): Thread = {
    val t = createTestThread(desc, f _)
    threads add t
    t
  }

  /**
   *
   */
  private def createTestThread[T](name: String, f: () => T) =
    new Thread(threadGroup, new Runnable() {
      def run() {
        try {
          threadStartLatch.await // Wait for the test framework to say its ok to go.
          f()
        } catch {
          // The reason this is a catch Throwable is because you want to let ThreadDeath through
          // without signalling errors. Otherwise the signalError could have been in a finally.
          // If the simulation is aborted, then stop will be called,
          // which will cause ThreadDeath, so just die and do nothing
          case e: ThreadDeath =>
          case t: Throwable => signalError(t)
        }
      }
    }, name)


  /////////////////////// thread management end //////////////////////////////

  /////////////////////// finish handler end //////////////////////////////

  /**
   * Register a function to be executed after the simulation has finished.
   */
  def finish(f: => Unit) {finishFunction = Some(f _)}

  /**
   * An option that might contain a function to run after all threads have finished.
   * By default, there is no finish function. A user must call finish  {...}
   * in order to have a function executed. If the user does call finish  {...}
   * then that function gets saved in this Option, as Some(f)
   */
  // TODO: Ensure this is set and called by the main thread, and if not, it gets an exception
  private var finishFunction: Option[() => Unit] = None

  /**
   * This method is invoked in a test after after all test threads have
   * finished.
   *
   */
  private def runFinishFunction() = finishFunction match {
    case Some(f) => f()
    case _ =>
  }

  /////////////////////// finish handler end //////////////////////////////

  /////////////////////// clock management start //////////////////////////////

  /**
   * Force this thread to block until the thread clock reaches the
   * specified value, at which point the thread is unblocked.
   *
   * @param c the tick value to wait for
   */
  def waitForTick(t: Tick) {clock waitForTick t}

  /**
   * Gets the current value of the thread clock. Primarily useful in
   * assert statements.
   *
   * @return the current tick value
   */
  def tick: Tick = clock.time

  /**
   * This runs the passed function, and while it runs it, the clock cannot advance.
   */
  def withClockFrozen[T](f: => T) = clock.withClockFrozen(f _)

  /**
   * Check if the clock has been frozen by any threads. (The only way a thread
   * can freeze the clock is by calling withClockFrozen.)
   */
  def isClockFrozen: Boolean = clock.isFrozen

  /////////////////////// clock management end //////////////////////////////

  // ===============================
  // -- Customized Wait Functions --
  // - - - - - - - - - - - - - - - -

  /**
   * Calling this method from one of the test threads may cause the
   * thread to yield. Use this between statements to generate more
   * interleavings.
   */
  def possiblyYield() {possiblyYield(0.5)}
  // TODO: possibly remove, because it is inconsistent with trying to test interleavings
  // deterministically

  /**
   * Calling this method from one of the test threads may cause the
   * thread to yield. Use this between statements to generate more
   * interleavings.
   *
   * @param probability
   *             (a number between 0 and 1) the likelihood that Thread.yield() is called
   */
  def possiblyYield(probability: Double) {
    if (new java.util.Random().nextDouble() < probability) Thread.`yield`
  }

  //////////////////////////////// run methods start ////////////////////////////////////////

  /**
   *
   */
  private val threadStartLatch = new CountDownLatch(1)

  /**
   * Run multithreaded test with the default parameters,
   * or the parameters set at the command line.
   */
  def start() {
    val DEFAULT_CLOCKPERIOD = 10
    val DEFAULT_RUNLIMIT = 5
    start(DEFAULT_CLOCKPERIOD, DEFAULT_RUNLIMIT)
  }

  /**
   * Run multithreaded test.
   * @param clockPeriod The period (in ms) between checks for the clock 
   * @param runLimit The limit to run the test in seconds
   * @throws Throwable The first error or exception that is thrown by one of the threads
   */
  // TODO: Only allow this to be called once per instance.
  def start(clockPeriod: Int, runLimit: Int) {

    // start each test thread
    threads.foreach(startThread)

    // wait for all the test threads to start before starting the clock
    threadStartLatch.countDown()

    // start the clock thread
    val clockThread = startThread(ClockThread(clockPeriod, runLimit))

    // wait until all threads have ended
    threads foreach waitForThread
    waitForThread(clockThread)

    // if there are any errors, get out and dont run the finish function
    if (!errors.isEmpty) {
      logger.error("errors: " + errors)      
      throw errors.peek
    }

    // invoke finish at the end of each run
    runFinishFunction()
  }

  private def startThread(t: Thread): Thread = {
    logger.trace.around("starting: " + t) {t.start(); t}
  }

  /**
   * Wait for all of the test case threads to complete, or for one
   * of the threads to throw an exception, or for the clock thread to
   * interrupt this (main) thread of execution. When the clock thread
   * or other threads fail, the error is placed in the shared error array
   * and thrown by this method.
   *
   * @param threads
   *             List of all the test case threads and the clock thread
   */
  // Explain how we understand it works: if the thread that's been joined already dies with an exception
  // that will go into errors, and this thread the join will return. If the thread returns and doesn't
  // die, that means all went well, and join will return and it can loop to the next one.
  // There should be no race condition between the last thread being waited on by join, it dies, join
  // returns, and after that the error gets into the errors. Because if you look in run() in the
  // thread inside createTestThread, the signalling error happens in a catch Throwable block before the thread
  // returns.
  def waitForThread(t: Thread) {
    logger.trace("waiting for: " + t.getName + " which is in state:" + t.getState)
    try {
      if (t.isAlive && !errors.isEmpty) logger.trace.around("stopping: " + t) {t.stop()}
      else logger.trace.around("joining: " + t) {t.join()}
    } catch {
      case e: InterruptedException => {
        logger.trace("killed waiting for threads. probably deadlock or timeout.")
        errors offer new AssertionError(e)
      }
    }
  }

  /**
   * Stop all test case threads and clock thread, except the thread from
   * which this method is called. This method is used when a thread is
   * ready to end in failure and it wants to make sure all the other
   * threads have ended before throwing an exception.
   * Clock thread will return normally when no threads are running.
   * //
   */
  def signalError(t: Throwable) {
    logger.error(t)
    errors offer t
    for (t <- threadGroup.getThreads; if (t != currentThread)) {
      logger.error("signaling error to " + t.getName)
      val assertionError = new AssertionError(t.getName + " killed by " + currentThread.getName)
      assertionError setStackTrace t.getStackTrace
      t stop assertionError
    }
  }

  /**
   * A Clock manages the current tick in a MultiThreadedTest.
   * Several duties stem from that responsibility.
   *
   * The clock will:
   *
   * <ol>
   * <li>Block a thread until the tick has reached a particular time.</li>
   * <li>Report the current time</li>
   * <li>Run operations with the clock frozen.</li>
   * </ol>
   *
   * Date: Jun 20, 2009
   * Time: 8:56:54 AM
   * @author Josh Cough
   */
  class Clock { // TODO: figure out why the compiler won't let us make this private

    import java.util.concurrent.locks.ReentrantReadWriteLock
    import scala.util.concurrent.locks.Implicits._

    // clock starts at time 0
    private var currentTime = 0
    private val lock = new AnyRef

    /**
     * Read locks are acquired when clock is frozen and must be
     * released before the clock can advance in a waitForTick().
     */
    private val rwLock = new ReentrantReadWriteLock

    private var highestTickCountBeingWaitedOn = 0

    /**
     * Advance the current tick. In order to do so, the clock will wait
     * until it has become unfrozen.
     *
     * All threads waiting for the clock to tick will be notified after the advance.
     *
     * Only the clock thread should be calling this.
     *
     * If the clock has been frozen by a thread, then that thread will own the readLock. Write
     * lcok can only be acquired when there are no readers, so ticks won't progress while someone
     * has the clock frozen. Other methods also grab the read lock, like time (which gets
     * the current tick.)
     */
    // TODO: rename time() to tick or currentTick, and tick to incrementTick? Maybe not. Maybe OK.
    def tick() {
      lock.synchronized {
        rwLock.write {
          logger.trace("tick! from: " + currentTime + " to: " + (currentTime + 1))
          currentTime += 1
        }
        lock.notifyAll()
      }
    }

    /**
     * The current time.
     */
    // TODO: Maybe currentTime is a better name for the method, but...
    def time: Tick = rwLock read currentTime

    /**
     * When wait for tick is called, the current thread will block until
     * the given tick is reached by the clock.
     */
    // TODO: Could just notify in the tick() method the folks that are waiting on that
    // particular tick, but then that's more complicated. Not a big deal.
    def waitForTick(t: Tick) {
      lock.synchronized {
        if (t > highestTickCountBeingWaitedOn) highestTickCountBeingWaitedOn = t
        while (time < t) {
          try {
            logger.trace.around(currentThread.getName + " is waiting for time " + t) {
              lock.wait()
            }
          } catch {
            case e: InterruptedException => throw new AssertionError(e)
          }
        }
      }
    }
    // The reason there's no race condition between calling time() in the while and calling
    // lock.wait() later (between that) and some other thread incrementing the tick and doing
    // a notify that this thread would miss (which it would want to know about if that's the
    // new time that it's waiting for) is becauswe both this and the tick method are synchronized
    // on the lock.

    /**
     * Returns true if any thread is waiting for a tick in the future ( greater than the current time )
     */
    def anyThreadWaitingForATick_? = {
      lock.synchronized {highestTickCountBeingWaitedOn > currentTime}
    }

    /**
     * When the clock is frozen, it will not advance even when all threads
     * are blocked. Use this to block the current thread with a time limit,
     * but prevent the clock from advancing due to a    { @link # waitForTick ( int ) } in
     * another thread.
     */
    def withClockFrozen[T](f: => T): T = rwLock read f

    /**
     * Check if the clock has been frozen by any threads.
     */
    def isFrozen: Boolean = rwLock.getReadLockCount > 0
  }

  /**
   * The clock thread is the manager of the MultiThreadedTest.
   * Periodically checks all the test case threads and regulates them.
   * If all the threads are blocked and at least one is waiting for a tick,
   * the clock advances to the next tick and the waiting thread is notified.
   * If none of the threads are waiting for a tick or in timed waiting,
   * a deadlock is detected. The clock thread times out if a thread is in runnable
   * or all are blocked and one is in timed waiting for longer than the runLimit.
   *
   * Algorithm in detail:
   *
   * While there are threads alive
   *
   *    If there are threads RUNNING
   *
   *       If they have been running too long
   *
   *          stop the test with a timeout error
   *
   *    else if there are threads waiting for a clock tick
   *
   *       advance the clock
   *
   *    else if there are threads in TIMED_WAITING
   *
   *       increment the deadlock counter
   *
   *       if the deadlock counter has reached a threadshold
   *
   *          stop the test due to potential deadlock
   *
   *    sleep clockPeriod ms
   *
   *
   * @param mainThread The main test thread. This thread will be waiting
   * for all the test threads to finish. It will be interrupted if the
   * ClockThread detects a deadlock or timeout.
   *
   * @param clockPeriod The period (in ms) between checks for the clock
   *
   * @param maxRunTime The limit to run the test in seconds
   */
  case class ClockThread(clockPeriod: Int, maxRunTime: Int) extends Thread("Clock") {
    this setDaemon true // TODO: Why is this a daemon thread? If no good reason, drop it.

    // used in detecting timeouts
    private var lastProgress = System.currentTimeMillis

    // used in detecting deadlocks
    private var deadlockCount = 0
    private val MAX_DEADLOCK_DETECTIONS_BEFORE_DEADLOCK = 50

    /**
     * Runs the steps described above.
     */
    override def run {
      while (threadGroup.anyThreadsAlive_?) {
        if (threadGroup.anyThreadsRunning_?) {
          if (runningTooLong_?) timeout()
        }
        else if (clock.anyThreadWaitingForATick_?) {
          clock.tick()
          deadlockCount = 0
          lastProgress = System.currentTimeMillis
        }
        else if (!threadGroup.anyThreadsInTimedWaiting_?) {
          detectDeadlock()
        }
        Thread sleep clockPeriod
      }
    }

    /**
     * Threads have been running too long (timeout) if
     * The number of seconds since the last progress are more
     * than the allowed maximum run time.
     */
    private def runningTooLong_? = System.currentTimeMillis - lastProgress > 1000L * maxRunTime

    /**
     * Stop the test tue to a timeout.
     */
    private def timeout() {
      val errorMessage = "Timeout! Test ran longer than " + maxRunTime + " seconds."
      signalError(new IllegalStateException(errorMessage))
      mainThread.interrupt()
    }

    /**
     * Determine if there is a deadlock and if so, stop the test.
     */
    private def detectDeadlock() {
      if (deadlockCount == MAX_DEADLOCK_DETECTIONS_BEFORE_DEADLOCK) {
        val errorMessage = "Apparent Deadlock! Threads waiting 50 clock periods (" + (clockPeriod * 50) + "ms)"
        signalError(new IllegalStateException(errorMessage))
        mainThread.interrupt()
      }
      else deadlockCount += 1
    }
  }
}