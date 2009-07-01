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

  /**
   * Executes the given function while holding the given lock
   *
   * @param lock the lock to be locked while executing the given function
   * @param f the function to be executed while the lock is locked
   * @return the result yielded by executing the given function
   */
  def withLock[T](lock: Lock)(f: => T): T = {
    lock.lock
    val t = f
    lock.unlock
    t
  }
}

/**
 * Pimps a ReadWriteLock by adding these instance methods: 
 * withReadLock, withWriteLock, read and write.
 */
class PimpedReadWriteLock(lock: ReadWriteLock) {

  import PimpedReadWriteLock.withLock

  /**
   * Executes the given function, with the read lock locked
   *
   * @param f the function to be executed while the read lock is locked
   * @return the result yielded by the given function
   */
  def withReadLock[T](f: => T): T = withLock(lock.readLock){ f }

  /**
   * Alternate name for withReadLock
   */
  def read[T](f: => T): T = withLock(lock.readLock){ f }

  /**
   * Executes the given function, with the write lock locked
   * 
   * @param f the function to be executed while the write lock is locked
   * @return the result yielded by the given function
   */
  def withWriteLock[T](f: => T): T = withLock(lock.writeLock){ f }

  /**
   * Alternate name for withWriteLock
   */
  def write[T](f: => T): T = withLock(lock.writeLock){ f }
}
