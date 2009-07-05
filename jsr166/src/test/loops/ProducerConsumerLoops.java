/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */

import java.util.concurrent.*;

public class ProducerConsumerLoops {
    static final int CAPACITY =      100;

    static final ExecutorService pool = Executors.newCachedThreadPool();
    static boolean print = false;
    static int producerSum;
    static int consumerSum;
    static synchronized void addProducerSum(int x) {
        producerSum += x;
    }

    static synchronized void addConsumerSum(int x) {
        consumerSum += x;
    }

    static synchronized void checkSum() {
        if (producerSum != consumerSum)
            throw new Error("CheckSum mismatch");
    }

    public static void main(String[] args) throws Exception {
        int maxPairs = 100;
        int iters = 100000;

        if (args.length > 0) 
            maxPairs = Integer.parseInt(args[0]);

        print = false;
        System.out.println("Warmup...");
        oneTest(1, 10000);
        Thread.sleep(100);
        oneTest(2, 10000);
        Thread.sleep(100);
        oneTest(2, 10000);
        Thread.sleep(100);
        print = true;
        
        int k = 1;
        for (int i = 1; i <= maxPairs;) {
            System.out.println("Pairs:" + i);
            oneTest(i, iters);
            Thread.sleep(100);
            if (i == k) {
                k = i << 1;
                i = i + (i >>> 1);
            } 
            else 
                i = k;
        }
        pool.shutdown();
   }

    static void oneTest(int pairs, int iters) throws Exception {
        int fairIters = iters/20;
        if (print)
            System.out.print("ArrayBlockingQueue      ");
        oneRun(new ArrayBlockingQueue<Integer>(CAPACITY), pairs, iters);

        if (print)
            System.out.print("LinkedBlockingQueue     ");
        oneRun(new LinkedBlockingQueue<Integer>(CAPACITY), pairs, iters);

        if (print)
            System.out.print("LinkedBlockingDeque     ");
        oneRun(new LinkedBlockingDeque<Integer>(CAPACITY), pairs, iters);

        if (print)
            System.out.print("SynchronousQueue        ");
        oneRun(new SynchronousQueue<Integer>(), pairs, iters);

        if (print)
            System.out.print("SynchronousQueue(fair)  ");
        oneRun(new SynchronousQueue<Integer>(true), pairs, fairIters);

        if (print)
            System.out.print("PriorityBlockingQueue   ");
        oneRun(new PriorityBlockingQueue<Integer>(), pairs, fairIters);

        if (print)
            System.out.print("ArrayBlockingQueue(fair)");
        oneRun(new ArrayBlockingQueue<Integer>(CAPACITY, true), pairs, fairIters);

    }
    
    static abstract class Stage implements Runnable {
        final int iters;
        final BlockingQueue<Integer> queue;
        final CyclicBarrier barrier;
        Stage (BlockingQueue<Integer> q, CyclicBarrier b, int iters) {
            queue = q; 
            barrier = b;
            this.iters = iters;
        }
    }

    static class Producer extends Stage {
        Producer(BlockingQueue<Integer> q, CyclicBarrier b, int iters) {
            super(q, b, iters);
        }

        public void run() {
            try {
                barrier.await();
                int s = 0;
                int l = hashCode();
                for (int i = 0; i < iters; ++i) {
                    l = LoopHelpers.compute4(l);
                    queue.put(new Integer(l));
                    s += LoopHelpers.compute4(l);
                }
                addProducerSum(s);
                barrier.await();
            }
            catch (Exception ie) { 
                ie.printStackTrace(); 
                return; 
            }
        }
    }

    static class Consumer extends Stage {
        Consumer(BlockingQueue<Integer> q, CyclicBarrier b, int iters) { 
            super(q, b, iters);
        }

        public void run() {
            try {
                barrier.await();
                int l = 0;
                int s = 0;
                for (int i = 0; i < iters; ++i) {
                    l = LoopHelpers.compute4(queue.take().intValue());
                    s += l;
                }
                addConsumerSum(s);
                barrier.await();
            }
            catch (Exception ie) { 
                ie.printStackTrace(); 
                return; 
            }
        }

    }

    static void oneRun(BlockingQueue<Integer> q, int npairs, int iters) throws Exception {
        LoopHelpers.BarrierTimer timer = new LoopHelpers.BarrierTimer();
        CyclicBarrier barrier = new CyclicBarrier(npairs * 2 + 1, timer);
        for (int i = 0; i < npairs; ++i) {
            pool.execute(new Producer(q, barrier, iters));
            pool.execute(new Consumer(q, barrier, iters));
        }
        barrier.await();
        barrier.await();
        long time = timer.getTime();
        checkSum();
        if (print)
            System.out.println("\t: " + LoopHelpers.rightJustify(time / (iters * npairs)) + " ns per transfer");
    }

}
