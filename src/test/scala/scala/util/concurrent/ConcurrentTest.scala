package scala.util.concurrent

import org.scalatest.matchers.{MustMatchers, MustBeSugar}
import org.scalatest.multi.MultiThreadedSuite

/**
 *
 * @author Josh Cough
 * Date: July 4, 2009
 * Time: 11:11 PM
 */
trait ConcurrentTest extends MultiThreadedSuite with MustMatchers with MustBeSugar