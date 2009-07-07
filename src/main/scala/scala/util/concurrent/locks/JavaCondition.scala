package scala.util.concurrent.locks

import java.util.concurrent.locks.{Lock => JLock, Condition => JCondition}

class AbstractJavaCondition(cond: => Boolean, val underlying: JCondition)
extends AbstractCondition {
  override def condition = cond

  override def signal() = underlying.signal
  override def signalAll() = underlying.signalAll

  override lazy val interruptible: Condition =
    new JavaCondition(cond, underlying) with InnerCondition {
      override lazy val interruptible = this
    }
  override lazy val uninterruptible: Condition =
    new UninterruptibleJavaCondition(cond, underlying) with InnerCondition {
      override lazy val uninterruptible = this
    }
  override def attemptFor(duration: Duration): TryingCondition =
    new TryingJavaCondition(duration, cond, underlying) with InnerCondition

  trait InnerCondition extends AbstractJavaCondition {
    override lazy val interruptible: Condition =
      AbstractJavaCondition.this.interruptible
    override lazy val uninterruptible: Condition =
      AbstractJavaCondition.this.uninterruptible
  }
}

class JavaCondition(cond: => Boolean, override val underlying: JCondition)
extends AbstractJavaCondition(cond, underlying) with Condition {
  override def await() = underlying.await
  override lazy val interruptible = this
}

class UninterruptibleJavaCondition(cond: => Boolean,
  override val underlying: JCondition)
extends AbstractJavaCondition(cond, underlying) with Condition {
  override def await() = underlying.awaitUninterruptibly
  override lazy val uninterruptible = this
}

class TryingJavaCondition(duration: Duration, cond: => Boolean,
  override val underlying: JCondition)
extends AbstractJavaCondition(cond, underlying) with TryingCondition {
  override def await() = underlying.await(duration.length, duration.timeUnit)
}
