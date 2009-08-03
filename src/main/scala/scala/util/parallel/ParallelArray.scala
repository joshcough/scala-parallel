package scala.util.parallel

import _root_.jsr166y.{RecursiveAction, ForkJoinPool, ForkJoinTask}

case class ParallelArray[A](data: Array[A]){

  val seq_threshold = 16

  // the mastermind of this operation.
  def mapreduce[B](id: B)(m: A => B, r: (B, B) => B): B = {
    run(new FunctionalRecursiveAction(r, (s, e) => (s to e).foldLeft(id) {(b, i) => r(b, m(data(i)))}))
  }

  // these all work off mapreduce
  def find (p: A => Boolean): Option[A] = {
    mapreduce[Option[A]](None)(
      a => if(p(a)) Some(a) else None,
      (l, r) => l match{ case Some(_) => l; case None => r } )
  }
  def exists(p: A => Boolean): Boolean = find(p).isDefined
  def forall(p: A => Boolean): Boolean = mapreduce(true)(p, _ && _ )
  def reduce(id: A)(r: (A, A) => A): A = mapreduce(id)(a=>a, r)
  def count(p: A => Boolean): Int = mapreduce[Int](0)(a => if(p(a)) 1 else 0, (l, r) => l + r )

  // these three require a new array, and populate mutably.
  // the do not work off mapreduce!
  def map[B](f: A => B): ParallelArray[B] = MapWorker.map(f)
  def filter(p: A => Boolean): ParallelArray[A] = MapWorker.filter(p)
  def remove(p: A => Boolean): ParallelArray[A] = MapWorker.filter(!p(_))

  private class FunctionalRecursiveAction[T](reduce:(T,T)=>T, executeSequentially:(Int, Int)=>T,
                          start: Int, end: Int) extends RecursiveAction {

    def this(reduce:(T,T)=>T, seq:(Int, Int) => T) = this(reduce, seq, 0, data.length-1)

    lazy val result = Some(if (size < seq_threshold) innerExecuteSequentially else executeInParallel)
    private def size = end - start
    private def midpoint = size / 2

    private def innerExecuteSequentially = {
      //println(currentThread)
      executeSequentially(start, end)
    }

    private def executeInParallel: T = {
      val left = new FunctionalRecursiveAction(reduce, executeSequentially, start, start+midpoint)
      val right = new FunctionalRecursiveAction(reduce, executeSequentially, start+midpoint+1, end)
      ForkJoinTask.invokeAll(left, right)
      reduce( left.result.get, right.result.get )
    }

    override def compute { result }
  }

  private object MapWorker{
    def sequentially[T](newData:Array[T], f: A => T, p : A => Boolean)(start:Int, end:Int) = {
      for (i <- start to end; if(p(data(i)))) newData(i) = f(data(i)); newData
    }
    def newMapper[B](f: A => B) = {
      new FunctionalRecursiveAction[Array[B]]((l,r)=>l, sequentially[B](new Array[B](data.size), f, t => true))
    }
    def newFilterer(p: A => Boolean) = {
      new FunctionalRecursiveAction[Array[A]]((l,r)=>l, sequentially[A](new Array[A](data.size), t => t, p))
    }

    def map[B](f: A => B) = ParallelArray(run(MapWorker.newMapper(f)))
    def filter(p: A => Boolean) = ParallelArray(run(newFilterer(p)).filter(_!=null))
  }

  private def run[T](worker:FunctionalRecursiveAction[T]): T = {
    ParallelArray.fjPool.invoke(worker)
    worker.result.get
  }
}

object ParallelArray{
  val fjPool = new ForkJoinPool
  def time[T]( f: => T ) = {
    val start = System.currentTimeMillis
    val t = f
    println("time: " + (System.currentTimeMillis - start))
    t
  }
}