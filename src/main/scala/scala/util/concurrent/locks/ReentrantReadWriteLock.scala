package scala.util.concurrent.locks

import java.util.concurrent.locks.{ReentrantReadWriteLock => JReentrantReadWriteLock}

object ReentrantReadWriteLock {
  def apply(lock: JReentrantReadWriteLock): ReentrantReadWriteLock =
    new ReentrantReadWriteLock {
      override val underlying = lock
    }
}

class ReentrantReadWriteLock(fair: Boolean /* = false */) extends ReadWriteLock {
  def this() = this(false) /* remove for 2.8 */

  val underlying: JReentrantReadWriteLock = new JReentrantReadWriteLock(fair)
}
