package scala.util.concurrent.locks

import java.util.concurrent.locks.{Lock, ReadWriteLock}

/**
 * Provides implicit def for pimping a ReadWriteLock.
 * Provides a nice withLock method that has probably been written a million times.
 *
 * @author Josh Cough
 * Date: Jun 24, 2009
 * Time: 9:42:43 PM
 */
object PimpedReadWriteLock{
  
  implicit def pimpMyReadWriteLock( lock: ReadWriteLock ) = new PimpedReadWriteLock(lock)

  def withLock[T](lock: Lock)(f: => T): T = {
    lock.lock
    val t = f
    lock.unlock
    t
  }
}

class PimpedReadWriteLock(lock: ReadWriteLock) {

  def withReadLock[T](f: => T): T = PimpedReadWriteLock.withLock(lock.readLock){ f }
  def withWriteLock[T](f: => T): T = PimpedReadWriteLock.withLock(lock.writeLock){ f }
}
