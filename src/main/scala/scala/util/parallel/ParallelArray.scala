package scala.util.parallel

import _root_.jsr166y.{RecursiveAction, ForkJoinPool, ForkJoinTask}

object ParallelArray{ val fjPool = new ForkJoinPool }

case class ParallelArray[A](data: Array[A]){

  val seq_threshold = 2  

  def map[B](f: A => B): ParallelArray[B] = {
    ParallelArray(run(new MapWorker(f, x => true, new Array[B](data.size), 0, data.length-1)))
  }

  def filter(p: A => Boolean): ParallelArray[A] = {
    ParallelArray(run(new MapWorker(x => x, p, new Array[A](data.size), 0, data.length-1)).filter(_ != null))
  }

  def remove(p : A => Boolean) : ParallelArray[A] ={
    ParallelArray(run(new MapWorker(x => x, ! p(_), new Array[A](data.size), 0, data.length - 1)).filter(_ != null))
  }

  def exists(p : (A) => Boolean) : Boolean = count(p) > 0

  def forall(p : (A) => Boolean) : Boolean = count(p) == data.size

  def count(p: A => Boolean): Int = {
    class CountWorker(s: Int, e: Int) extends Worker[Int](s,e) {
      def reduce(x: Int, y: Int) = x + y
      def executeSequentially = (for (i <- start to end; if (p(data(i)))) yield data(i)).size
      def apply(start: Int, end: Int) = new CountWorker(start,end)
    }
    run(new CountWorker(0, data.length - 1))
  }

  def find (p : A => Boolean) : Option[A] = {
    class FindWorker(s: Int, e: Int) extends Worker[Option[A]](s,e) {
      def reduce(left:Option[A],right:Option[A]): Option[A] = {
        left match{ case Some(_) => left; case None => right }
      }
      def executeSequentially = (start to end).find{ i => p(data(i)) } match {
        case Some(i) => Some(data(i))
        case None => None
      }
      def apply(start: Int, end: Int) = new FindWorker(start,end)
    }
    run(new FindWorker(0, data.length - 1))
  }

  private def run[T](worker:Worker[T]): T = {
    ParallelArray.fjPool.invoke(worker)
    worker.getResult
  }

  private abstract class Worker[T](val start: Int, val end: Int) extends RecursiveAction {

    def executeSequentially: T
    def apply(start: Int, end: Int): Worker[T]
    def reduce(t1:T,t2:T): T

    def getResult = result.get
    protected var result: Option[T] = None

    def executeInParallel: T = {
      val left = apply(start, start+midpoint)
      val right = apply(start+midpoint+1, end)
      ForkJoinTask.invokeAll(left, right)
      reduce( left.result.get, right.result.get )
    }

    private def size = end - start
    private def midpoint = size / 2

    override def compute {
      result = Some(if (size < seq_threshold) executeSequentially else executeInParallel)
    }
  }

  private class MapWorker[B](f: A => B, p: A => Boolean, newData: Array[B], s: Int, e: Int)
          extends Worker[Array[B]](s, e) {
    def reduce(left:Array[B],right:Array[B]): Array[B] = left
    def executeSequentially = { for (i <- start to end; if(p(data(i)))) newData(i) = f(data(i)); newData }
    def apply(start: Int, end: Int) = new MapWorker(f, p, newData, start, end)
  }
}