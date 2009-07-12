package org.scalatest.concurrent

import java.util.concurrent.atomic.AtomicReference

/**
 * Date: Jun 16, 2009
 * Time: 7:25:34 PM
 * @author Josh Cough
 */
trait ConductorMethods extends Suite with Logger{ thisSuite =>

  private val conductor = new AtomicReference[Conductor]()

  protected def thread[T](f: => T): Thread = conductor.get.thread{ f }
  protected def thread[T](name: String)(f: => T): Thread = conductor.get.thread(name){ f }
  protected def waitForTick(tick:Int) = conductor.get.waitForTick(tick)
  protected def tick = conductor.get.tick
  protected implicit def addThreadsMethodToInt(nrThreads:Int) = conductor.get.addThreadsMethodToInt(nrThreads)

  /**
   * 
   */
  abstract override def runTest(testName: String, reporter: Reporter, stopper: Stopper, properties: Map[String, Any]) {

    conductor.compareAndSet(conductor.get, new Conductor(this))

    val interceptor = new PassFailInterceptor(reporter)

    super.runTest(testName, interceptor, stopper, properties)

    interceptor.failReport match {
      case Some(r) => reporter testFailed r
      case None => runConductor( testName, reporter, interceptor )
    }
  }

  /**
   *
   */
  private def runConductor(testName:String, reporter:Reporter, interceptor:PassFailInterceptor){
    try {
      conductor.get.execute()
    } catch {
      case e => reporter testFailed new Report(testName, testName, Some(e), None)
    } finally {
      val errors = conductor.get.errors
      def errorReport = new Report(testName, testName, Some(errors.take), None)

      if (errors.isEmpty) {
        reporter.testSucceeded(new Report(testName, testName))
      } else {
        reporter testFailed errorReport
        // all of a sudden the compiler broke on a while loop?!? had to use recursion instead
        def addInfo(): Unit = if( ! errors.isEmpty ){
          reporter infoProvided errorReport
          addInfo()
        }
        addInfo()
      }
    }
  }

  /**
   * 
   */
  private class PassFailInterceptor(original: Reporter) extends Reporter {

    var successReport: Option[Report] = None
    override def testSucceeded(r: Report) = successReport = Some(r)

    var failReport: Option[Report] = None
    override def testFailed(r: Report) = failReport = Some(r)

    // just delegate on the test
    override def runStarting(i: Int) = original.runStarting(i)
    override def testStarting(r: Report) = original.testStarting(r)
    override def testIgnored(r: Report) = original.testIgnored(r)
    override def suiteStarting(r: Report) = original.suiteStarting(r)
    override def suiteCompleted(r: Report) = original.suiteCompleted(r)
    override def suiteAborted(r: Report) = original.suiteAborted(r)
    override def infoProvided(r: Report) = original.infoProvided(r)
    override def runStopped = original.runStopped
    override def runAborted(r: Report) = original.runAborted(r)
    override def runCompleted = original.runCompleted
  }
}