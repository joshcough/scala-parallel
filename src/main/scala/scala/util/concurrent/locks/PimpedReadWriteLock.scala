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
   * Locks the given lock
   * Executes the given function, holding the result
   * Unlocks the lock
   * Returns the result
   *
   * @param lock the lock to be locked while executing the given function
   * @param f the function to be executed while the lock is locked
   */
  def withLock[T](lock: Lock)(f: => T): T = {
    lock.lock
    val t = f
    lock.unlock
    t
  }
}

/**
 * Adds withReadLock and withWriteLock functions to ReadWriteLock.
 */
class PimpedReadWriteLock(lock: ReadWriteLock) {

  import PimpedReadWriteLock._

  /**
   * Locks the read lock
   * Executes the given function, holding the result
   * Unlocks the read lock
   * 
   * @param f the function to be executed while the read lock is locked
   */
  def withReadLock[T](f: => T): T = withLock(lock.readLock){ f }

  /**
   * Alternate name for withReadLock
   */
  def read[T](f: => T): T = withLock(lock.readLock){ f }

  /**
   * Locks the write lock
   * Executes the given function, holding the result
   * Unlocks the write lock
   * Returns the result
   *
   * @param f the function to be executed while the read lock is locked
   */
  def withWriteLock[T](f: => T): T = withLock(lock.writeLock){ f }

  /**
   * Alternate name for withWriteLock
   */
  def write[T](f: => T): T = withLock(lock.writeLock){ f }

}
