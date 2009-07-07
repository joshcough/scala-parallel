package scala.util.concurrent.locks

trait AbstractCondition {
  def condition: Boolean

  def signal(): Unit
  def signalAll(): Unit
  
  def attemptFor(duration: Duration): TryingCondition
  def interruptible: Condition
  def uninterruptible: Condition
}

trait Condition extends AbstractCondition {
  def await(): Unit

  def apply[T](f: => T): T = {
    while(condition) await()
    f
  }
}

trait TryingCondition extends AbstractCondition {
  def await(): Boolean

  def apply[T](f: => T): Option[T] = {
    var withinDeadline = true

    while(condition && withinDeadline) {
      withinDeadline = await()
    }

    if (condition && withinDeadline)
      Some(f)
    else
      None
  }
}
