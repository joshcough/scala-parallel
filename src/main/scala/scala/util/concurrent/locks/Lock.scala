package scala.util.concurrent.locks

import java.util.concurrent.locks.{Lock => JLock, Condition => JCondition}

object Lock {
  def apply(lock: JLock): Lock = {
    val l = lock
    new Lock { override val underlying = l }
  }
}

trait Lock {
  trait Condition {
    val underlying: JCondition = Lock.this.underlying.newCondition
  }

  val underlying: JLock

  def lock() = underlying.lock
  def unlock() = underlying.unlock
  def tryLock() = underlying.tryLock

  lazy val interruptible: Lock = new Lock {
    override val underlying = Lock.this.underlying
    override def lock() = underlying.lockInterruptibly
    override lazy val interruptible = this
    override val uninterruptible = Lock.this
  }
  val uninterruptible: Lock = this

  def map[T](f: this.type => T): T = {
    this.lock()
    try {
      f(this)
    } finally {
      this.unlock()
    }
  }

  def flatMap[T](f: this.type => T): T = map(f)

  def apply[T](f: => T): T = map(_ => f)
}
