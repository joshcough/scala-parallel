package org.scalatest.matchers

/**
 * Date: Jun 20, 2009
 * Time: 8:54:27 AM
 * @author Josh Cough
 */
trait MustBeSugar { this: MustMatchers =>

  implicit def anyToMustBe(a: Any) = new {
    def mustBe(b: Any) {
      a must be(b)
    }

    def must_be(b: Any) {
      a must be(b)
    }
  }
}
