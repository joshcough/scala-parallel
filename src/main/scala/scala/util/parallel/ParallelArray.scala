package scala.util.parallel

import _root_.jsr166y.{RecursiveAction, ForkJoinPool, ForkJoinTask}

object ParallelArray{
  val fjPool = new ForkJoinPool
  val seq_threshold = 2
}

case class ParallelArray[A](data: Array[A]){

  def map[B](f: A => B): ParallelArray[B] = {
    run(MapWorker(f, new Array[B](data.size), 0, data.length-1))
  }

  def filter(f: A => Boolean): ParallelArray[A] = {
    run(FilterWorker(f, new Array[A](data.size), 0, data.length-1))
  }

  private def run[R,S](worker:Worker[R,S]): ParallelArray[S] = {
    ParallelArray.fjPool.invoke(worker)
    ParallelArray(worker.getData)
  }

  abstract class Worker[R,S](f: A => R, newData: Array[S], start: Int, end: Int) extends RecursiveAction {

    def executeSequentially: Unit
    def getData: Array[S]
    def apply(start: Int, end: Int): Worker[R,S]

    private def size = end - start
    private def midpoint = size / 2

    override def compute {
      if (size < ParallelArray.seq_threshold) {
        println("thread: " + currentThread)
        executeSequentially
      }
      else {
        println("fork: (" +start + "," + (start+midpoint) + "),(" +(start+midpoint+1) + "," + end + ")")
        ForkJoinTask.invokeAll(this(start, start+midpoint),this(start+midpoint+1, end))
      }
    }
  }

  case class MapWorker[R](f: A => R, newData: Array[R], start: Int, end: Int)
          extends Worker[R,R](f, newData, start, end) {
    def executeSequentially = for(i <- start to end) newData(i) = f(data(i))
    def getData: Array[R] = newData
    def apply(start: Int, end: Int) = MapWorker(f,newData,start,end)
  }

  case class FilterWorker(f: A => Boolean, newData: Array[A], start: Int, end: Int)
          extends Worker[Boolean, A](f, newData, start, end) {
    def executeSequentially = for(i <- start to end) if(f(data(i))) newData(i) = data(i)
    def getData: Array[A] = newData.filter(_ != null)
    def apply(start: Int, end: Int) = FilterWorker(f,newData,start,end)
  }
}