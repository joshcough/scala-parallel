package org.scalatest.multi

import java.io.{StringWriter, PrintWriter}
import java.util.concurrent.locks.ReentrantReadWriteLock
import java.util.concurrent.{ArrayBlockingQueue, Semaphore, CountDownLatch, TimeUnit}

/**
 *
 * 
 * Date: Jun 16, 2009
 * Time: 7:25:34 PM
 * @author Josh Cough
 */
trait MultiThreadedSuite extends Suite with Conductor{ thisSuite =>

  /**
   * 
   */
  override def execute(testName: Option[String], reporter: Reporter, stopper: Stopper, includes: Set[String],
        excludes: Set[String], properties: Map[String, Any], distributor: Option[Distributor]) {  

    def buildReport(ex:Option[Throwable]) = {
      new Report(getClass.getName, getClass.getName, ex, None)
    }

    reporter.testStarting(buildReport(None))

    try{
      start()
    }finally{
      if( errors.isEmpty ) reporter.testSucceeded(buildReport(None))
      else {
        reporter.testFailed(buildReport(Some(errors.take)))
        while(!errors.isEmpty){ reporter.infoProvided(buildReport(Some(errors.take))) }
      }
    }
  }
}
