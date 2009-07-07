package scala.util.concurrent.locks

import java.util.concurrent.locks.{Lock => JLock}

object Lock {
  def apply(lock: JLock): JavaLock = new JavaLock(lock)
}

trait AbstractLock {
  def unlock(): Unit
  def newCondition(condition: => Boolean): Condition

  def attemptFor(duration: Duration): TryingLock
  def attempt: TryingLock
  def interruptible: Lock
  def uninterruptible: Lock
}

trait Lock extends AbstractLock {
  def lock(): Unit

  def map[T](f: this.type => T): T = {
    this.lock()
    try {
      f(this)
    } finally {
      this.unlock()
    }
  }
  def flatMap[T](f: this.type => T): T = map(f)
  def foreach(f: this.type => Unit): Unit = map(f)
  def apply[T](f: => T): T = map(_ => f)
}

trait TryingLock extends AbstractLock {
  def lock(): Boolean

  def map[T](f: this.type => T): Option[T] = {
    if (this.lock()) {
      try {
        Some(f(this))
      } finally {
        this.unlock()
      }
    } else {
      None
    }
  }
  def flatMap[T](f: this.type => T): Option[T] = map(f)
  def foreach(f: this.type => Unit): Unit = map(f)
  def apply[T](f: => T): Option[T] = map(_ => f)
}
