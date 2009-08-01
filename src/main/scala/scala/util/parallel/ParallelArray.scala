package scala.util.parallel

import _root_.jsr166y.{RecursiveAction, ForkJoinPool, ForkJoinTask}

object ParallelArray{
  val fjPool = new ForkJoinPool
  val SEQUENTIAL_THRESHOLD = 2
}

case class ParallelArray[A](data: Array[A]){

  def map[B](f: A => B): ParallelArray[B] = {
    val mapper = Mapper(f, new Array[B](data.size), 0, data.length-1)
    ParallelArray.fjPool.invoke(mapper)
    ParallelArray(mapper.get)
  }

  case class Mapper[B](f: A => B, map: Array[B], start: Int, end: Int) extends RecursiveAction {

    def size = end - start
    def midpoint = size / 2
    def get = map

    override def compute {
      if (size < ParallelArray.SEQUENTIAL_THRESHOLD) {
        for(i <- start to end) map(i) = f(data(i))
      } else {
        ForkJoinTask.invokeAll(Mapper(f, map, start, start+midpoint), Mapper(f, map, start+midpoint, end))
      }
    }
  }
}

