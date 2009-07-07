package scala.util.concurrent.locks

import java.util.concurrent.locks.{ReadWriteLock => JReadWriteLock}

object ReadWriteLock {
  def apply(lock: JReadWriteLock): ReadWriteLock = new JavaReadWriteLock(lock)
}

trait ReadWriteLock {
  def read: Lock
  def write: Lock
}

class JavaReadWriteLock(val underlying: JReadWriteLock) extends ReadWriteLock {
  object read extends JavaLock(underlying.readLock)
  object write extends JavaLock(underlying.writeLock)
}
