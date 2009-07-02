package scala.util.concurrent.locks

import java.util.concurrent.locks.{ReadWriteLock => JReadWriteLock}

object ReadWriteLock {
  def apply(lock: JReadWriteLock): ReadWriteLock = new ReadWriteLock {
    override val underlying = lock
  }
}

trait ReadWriteLock {
  val underlying: JReadWriteLock
  
  object read extends Lock {
    override val underlying = ReadWriteLock.this.underlying.readLock
  }
  object write extends Lock {
    override val underlying = ReadWriteLock.this.underlying.writeLock
  }
}
