package scala.util.concurrent.locks

import java.util.concurrent.locks.{Lock => JLock, ReadWriteLock => JReadWriteLock, 
  ReentrantReadWriteLock => JReentrantReadWriteLock}

object Implicits extends Implicits

trait Implicits {
  implicit def RichLock(lock: JLock): Lock = Lock(lock)
  implicit def RichReadWriteLock(lock: JReadWriteLock): ReadWriteLock = ReadWriteLock(lock)
  implicit def RichReentrantReadWriteLock(lock: JReentrantReadWriteLock): ReentrantReadWriteLock = ReentrantReadWriteLock(lock)
}
