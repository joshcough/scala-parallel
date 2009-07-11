package org.scalatest.concurrent

import java.util.concurrent.atomic.AtomicReference

/**
 * Date: Jun 16, 2009
 * Time: 7:25:34 PM
 * @author Josh Cough
 */
trait ConductorMethods extends Suite with Logger{ thisSuite =>

  private var conductor: AtomicReference[Conductor] = new AtomicReference(null)

  protected def thread[T](f: => T): Thread = conductor.get.thread(f _)
  protected def thread[T](name: String)(f: => T): Thread = conductor.get.thread(name)( f _ )
  protected def waitForTick(tick:Int) = conductor.get.waitForTick(tick)
  protected def tick = conductor.get.tick
  protected implicit def addThreadsMethodToInt(nrThreads:Int) = conductor.get.addThreadsMethodToInt(nrThreads)

  /**
   * 
   */
  abstract override def runTest(testName: String, reporter: Reporter, stopper: Stopper, properties: Map[String, Any]) {

    def buildReport(ex:Option[Throwable]) = {
      new Report(getClass.getName, getClass.getName, ex, None)
    }

    reporter.testStarting(buildReport(None))

    conductor.compareAndSet(conductor.get, new Conductor(this))

    super.runTest(testName, reporter, stopper, properties)

    try{
      conductor.get.execute()
    }finally{
      val errors = conductor.get.errors
      if( errors.isEmpty ) reporter.testSucceeded(buildReport(None))
      else {
        reporter.testFailed(buildReport(Some(errors.take)))
        while(!errors.isEmpty){ reporter.infoProvided(buildReport(Some(errors.take))) }
      }
    }
  }
}
