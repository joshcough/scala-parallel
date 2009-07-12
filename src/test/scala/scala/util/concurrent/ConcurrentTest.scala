package scala.util.concurrent

import org.scalatest.concurrent.{PrintlnLogger, ConductorMethods}
import org.scalatest.FunSuite
import org.scalatest.matchers.{MustMatchers, MustBeSugar}

/**
 *
 * @author Josh Cough
 * Date: July 4, 2009
 * Time: 11:11 PM
 */
trait ConcurrentTest extends FunSuite with ConductorMethods with MustMatchers with MustBeSugar with PrintlnLogger