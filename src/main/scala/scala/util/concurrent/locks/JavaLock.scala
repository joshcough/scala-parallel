package scala.util.concurrent.locks

import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.{Lock => JLock, Condition => JCondition}

class AbstractJavaLock(val underlying: JLock) extends AbstractLock {
  override def unlock() = underlying.unlock
  override def newCondition(condition: => Boolean): Condition =
    new JavaCondition(condition, underlying.newCondition)
  
  override def attemptFor(duration: Duration): TryingLock =
    new TimedTryingJavaLock(underlying, duration) with InnerLock
  override lazy val attempt: TryingLock =
    new TryingJavaLock(underlying) with InnerLock {
      override lazy val attempt = this
    }
  override lazy val interruptible: Lock =
    new ThrowingJavaLock(underlying) with InnerLock {
      override lazy val interruptible = this
    }
  override lazy val uninterruptible: Lock =
    new JavaLock(underlying) with InnerLock {
      override lazy val uninterruptible = this
    }

  trait InnerLock extends AbstractJavaLock {
    override lazy val uninterruptible = AbstractJavaLock.this.uninterruptible
    override lazy val attempt = AbstractJavaLock.this.attempt
    override lazy val interruptible = AbstractJavaLock.this.interruptible
  }
}

class JavaLock(override val underlying: JLock)
extends AbstractJavaLock(underlying) with Lock {
  override def lock() = underlying.lock
  override lazy val uninterruptible = this
}

class ThrowingJavaLock(override val underlying: JLock)
extends AbstractJavaLock(underlying) with Lock {
  override def lock() = underlying.lockInterruptibly
  override lazy val interruptible = this
}

class TryingJavaLock(override val underlying: JLock)
extends AbstractJavaLock(underlying) with TryingLock {
  override def lock() = underlying.tryLock
  override lazy val attempt = this
}

class TimedTryingJavaLock(override val underlying: JLock, duration: Duration)
extends AbstractJavaLock(underlying) with TryingLock {
  override def lock() = underlying.tryLock(duration.toJavaNanos, TimeUnit.NANOSECONDS)
}
