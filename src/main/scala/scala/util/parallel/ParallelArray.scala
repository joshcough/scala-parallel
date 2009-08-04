package scala.util.parallel

import jsr166y.{RecursiveAction,ForkJoinTask, ForkJoinPool}

trait FunctionalRecursiveAction[T] extends RecursiveAction {
  def getResult: T
  val seqentialThreshold: Int
}

trait Pool{
  def invokeAndGet[T](action:FunctionalRecursiveAction[T]): T
}

object ParallelArray{
  val defaultSequentialThreshold = 16

  object DefaultPool extends Pool{
    private val fjPool = new ForkJoinPool
    def invokeAndGet[T](action: FunctionalRecursiveAction[T]): T = {
      fjPool.invoke(action)
      action.getResult
    }
  }
}

case class ParallelArray[A](data: Array[A], pool:Pool, val seqentialThreshold: Int){

  // all these go away with 2.8 default args
  def this(data: Array[A]) = this(data, ParallelArray.DefaultPool, ParallelArray.defaultSequentialThreshold)
  def this(data: Array[A], pool:Pool) = this(data, pool, ParallelArray.defaultSequentialThreshold)
  def this(data: Array[A], seqentialThreshold: Int) = this(data, ParallelArray.DefaultPool, seqentialThreshold)

  // the mastermind of this operation.
  def mapreduce[B](id: B)(map: A => B, reduce: (B, B) => B): B = {
    def sequentialReduce(s:Int, e:Int) = (s to e).foldLeft(id) {(b, i) => reduce(b, map(data(i)))}
    pool.invokeAndGet(new BinaryRecursiveAction(reduce, sequentialReduce))
  }

  // these all work off mapreduce
  def find (p: A => Boolean): Option[A] = {
    mapreduce[Option[A]](None)(
      a => if(p(a)) Some(a) else None,
      (l, r) => l match{ case Some(_) => l; case None => r } )
  }
  def exists(p: A => Boolean): Boolean = mapreduce(false)(p, _ || _ )
  def forall(p: A => Boolean): Boolean = mapreduce(true )(p, _ && _ )
  def reduce(id: A)(r: (A, A) => A): A = mapreduce(id)(a=>a, r)
  def count (p: A => Boolean): Int     = mapreduce[Int](0)(a => if(p(a)) 1 else 0, (l, r) => l + r )

  // these three require a new array, and populate mutably.
  // the do not work off mapreduce!
  def map[B](f: A => B) = {
    val sequentialMapper = sequentialMutator[B](new Array[B](data.size), f, t => true) _
    val mapAction = new BinaryRecursiveAction[Array[B]]((l,r)=>l, sequentialMapper)
    ParallelArray(pool.invokeAndGet(mapAction), pool, seqentialThreshold)
  }

  def filter(p: A => Boolean) = {
    val sequentialFilter = sequentialMutator[A](new Array[A](data.size), t => t, p) _
    val filterAction = new BinaryRecursiveAction[Array[A]]((l,r)=>l, sequentialFilter)
    ParallelArray(pool.invokeAndGet(filterAction).filter(_!=null), pool, seqentialThreshold)
  }

  def remove(p: A => Boolean): ParallelArray[A] = filter(!p(_))

  private def sequentialMutator[T](newData:Array[T], map: A => T,
                                   pred: A => Boolean)(start:Int, end:Int):Array[T] = {
    for (i <- start to end; if(pred(data(i)))) newData(i) = map(data(i))
    newData
  }

  class BinaryRecursiveAction[T](val reduce: (T, T) => T, val executeSequentially: (Int, Int) => T,
                                 val start: Int, val end: Int) extends FunctionalRecursiveAction[T] {

    def this(reduce: (T, T) => T, executeSequentially: (Int, Int) => T) = {
      this( reduce, executeSequentially, 0, data.length-1)
    }

    val seqentialThreshold = ParallelArray.this.seqentialThreshold

    private var result: Option[T] = None
    private def size = end - start
    private def midpoint = size / 2

    def getResult = result.get

    private def executeInParallel: T = {
      val left = new BinaryRecursiveAction(reduce, executeSequentially, start, start + midpoint)
      val right = new BinaryRecursiveAction(reduce, executeSequentially, start + midpoint + 1, end)
      ForkJoinTask.invokeAll(left, right)
      reduce(left.result.get, right.result.get)
    }

    override def compute {
      result = Some(if (size < seqentialThreshold) executeSequentially(start, end) else executeInParallel)
    }
  }
}
