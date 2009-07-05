/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */

import java.util.*;
import java.math.*;

/**
 * A micro-benchmark with key types and operation mixes roughly
 * corresponding to some real programs.
 * 
 * The main results are a table of approximate nanoseconds per
 * element-operation (averaged across get, put etc) for each type,
 * across a range of map sizes.
 *
 * The program includes a bunch of microbenchmarking safeguards that
 * might underestimate typical performance. For example, by using many
 * different key types and exercising them in warmups it disables most
 * dynamic type specialization.  Some test classes, like Float and
 * BigDecimal are included not because they are commonly used as keys,
 * but because they can be problematic for some map implementations.
 * 
 * By default, it creates and inserts in order dense numerical keys
 * and searches for keys in scrambled order. Use "r" as second arg to
 * instead use random numerical values, and "s" as third arg to search
 * in insertion order.
 */
public class MapMicroBenchmark {
    static Class mapClass;
    static boolean randomSearches = true;
    static boolean randomKeys = false;

    static final long NANOS_PER_JOB = 8L * 1000L*1000L*1000L;

    // map operations per item per iteration -- change if job.work changed
    static final int OPS_PER_ITER = 11;
    static final int MIN_ITERS_PER_TEST = 2;
    static final int MAX_ITERS_PER_TEST = 1000000;

    // sizes are at halfway points for HashMap default resizes
    static final int firstSize = 36;
    static final int sizeStep = 4; // each size 4X last
    static final int nsizes = 8;
    static final int[] sizes = new int[nsizes];

    static final class Missing {} // class for guaranteed non-matches
    static final Object MISSING = new Missing();

    static Map newMap() {
        try {
            return (Map)mapClass.newInstance();
        } catch(Exception e) {
            throw new RuntimeException("Can't instantiate " + mapClass + ": " + e);
        }
    }

    public static void main(String[] args) throws Throwable {
        if (args.length == 0) {
            System.out.println("Usage: java MapMicroBenchmark className [r|s]keys [r|s]searches");
            return;
        }
            
        mapClass = Class.forName(args[0]);

        if (args.length > 1) {
            if (args[1].startsWith("s"))
                randomKeys = false;
            else if (args[1].startsWith("r"))
                randomKeys = true;
        }
        if (args.length > 2) {
            if (args[2].startsWith("s"))
                randomSearches = false;
            else if (args[2].startsWith("r"))
                randomSearches = true;
        }

        System.out.print("Class " + mapClass.getName());
        if (randomKeys)
            System.out.print(" random keys");
        else
            System.out.print(" sequential keys");
        if (randomSearches)
            System.out.print(" randomized searches");
        else
            System.out.print(" sequential searches");

        System.out.println();

        int n = firstSize;
        for (int i = 0; i < nsizes - 1; ++i) {
            sizes[i] = n;
            n *= sizeStep;
        }
        sizes[nsizes - 1] = n;

        Object[] ss = new Object[n];
        Object[] os = new Object[n];
        Object[] is = new Object[n];
        Object[] ls = new Object[n];
        Object[] fs = new Object[n];
        Object[] ds = new Object[n];
        Object[] bs = new Object[n];
        Object[] es = new Object[n];

        // To guarantee uniqueness, use xorshift for "random" versions
        int j = randomKeys? 1234567 : 0;
        for (int i = 0; i < n; i++) {
            ss[i] = String.valueOf(j);
            os[i] = new Object();
            is[i] = Integer.valueOf(j);
            ls[i] = Long.valueOf((long)j);
            fs[i] = Float.valueOf((float)i); // can't use random for float
            ds[i] = Double.valueOf((double)j);
            bs[i] = BigInteger.valueOf(j);
            es[i] = BigDecimal.valueOf(j);
            j = randomKeys? xorshift(j) : j + 1;
        }

	List<Job> list = new ArrayList<Job>();
        list.add(new Job("BigDecimal", es));
        list.add(new Job("BigInteger", bs));
        list.add(new Job("String    ", ss));
        list.add(new Job("Double    ", ds));
        list.add(new Job("Float     ", fs));
        list.add(new Job("Long      ", ls));
        list.add(new Job("Integer   ", is));
        list.add(new Job("Object    ", os));

        Job[] jobs = list.toArray(new Job[0]);
        warmup(jobs);
        warmup(jobs);
	time(jobs);
    }

    static void runWork(Job[] jobs, int maxIters) throws Throwable {
        for (int k = 0; k <  nsizes; ++k) {
            int len = sizes[k];
            for (int i = 0; i < jobs.length; i++) {
                jobs[i].setup(len);
                Thread.sleep(50);
                jobs[i].nanos[k] = jobs[i].work(len, maxIters);
                System.out.print(".");
            }
        }
        System.out.println();
    }

    static void warmup(Job[] jobs) throws Throwable {
        System.out.print("warm up");
        runWork(jobs, MIN_ITERS_PER_TEST);
        long ck = jobs[0].checkSum;
        for (int i = 1; i < jobs.length; i++) {
            if (jobs[i].checkSum != ck)
                throw new Error("CheckSum");
        }
    }

    static void time(Job[] jobs) throws Throwable {
        System.out.print("running");
        runWork(jobs, MAX_ITERS_PER_TEST);

        System.out.print("Type/Size:");
        for (int k = 0; k < nsizes; ++k)
            System.out.printf("%8d", sizes[k]);
        System.out.println();

        long[] aves = new long[nsizes];
        int njobs = jobs.length;

	for (int i = 0; i < njobs; i++) {
            System.out.print(jobs[i].name);
            for (int k = 0; k < nsizes; ++k) {
                long nanos = jobs[i].nanos[k];
                System.out.printf("%8d", nanos);
                aves[k] += nanos;
            }
            System.out.println();
        }

        System.out.println();
        System.out.print("average   ");
        for (int k = 0; k < nsizes; ++k)
            System.out.printf("%8d", (aves[k] / njobs));
        System.out.println();
    }


    static final class Job {
	final String name;
        long[] nanos = new long[nsizes];
        final Object[] items;
        final Object[] dups;
        Object[] reordered;
        volatile long checkSum;
        volatile int lastSum;
        Job(String name, Object[] items) {
            this.name = name;
            this.items = items;
            if (randomSearches)
                this.dups = new Object[items.length];
            else
                this.dups = items;
        }

        public void setup(int len) {
            if (randomSearches) {
                System.arraycopy(items, 0, dups, 0, len);
                shuffle(dups, len);
                reordered = dups;
            }
            else
                reordered = items;
        }

        public long work(int len, int maxIters) {
            Object[] ins = items;
            Object[] keys = reordered;

            if (ins.length < len || keys.length < len)
                throw new Error(name);
            int half = len / 2;
            Class eclass = ins[0].getClass();
            int sum = lastSum;
            long startTime = System.nanoTime();
            long elapsed;
            Map m = newMap();
            int j = 0;
            for (;;) {
                for (int i = 0; i < half; ++i) {
                    Object x = ins[i];
                    if (m.put(x, x) == null)
                        ++sum;
                }
                checkSum += sum ^ (sum << 1); // help avoid loop merging
                for (int i = 0; i < len; ++i) {
                    Object x = keys[i];
                    Object v = m.get(x); 
                    if (v != null && v.getClass() == eclass) // touch v
                        sum += 2;
                }
                checkSum += sum ^ (sum << 2);
                for (int i = half; i < len; ++i) {
                    Object x = ins[i];
                    if (m.put(x, x) == null)
                        ++sum;
                }
                checkSum += sum ^ (sum << 3);
                for (Object e : m.keySet()) {
                    if (e.getClass() == eclass)
                        ++sum;
                }
                checkSum += sum ^ (sum << 4);
                for (Object e : m.values()) {
                    if (e.getClass() == eclass)
                        ++sum;
                }
                checkSum += sum ^ (sum << 5);
                for (int i = len - 1; i >= 0; --i) {
                    Object x = keys[i];
                    Object v = m.get(x);
                    if (v != null && v.getClass() == eclass)
                        ++sum;
                }
                checkSum += sum ^ (sum << 6);
                for (int i = 0; i < len; ++i) {
                    Object x = ins[i];
                    Object v = m.get(x);
                    if (v != null && v.getClass() == eclass)
                        ++sum;
                }
                checkSum += sum ^ (sum << 7);
                for (int i = 0; i < len; ++i) {
                    Object x = keys[i];
                    Object v = ins[i];
                    if (m.put(x, v) == x)
                        ++sum;
                }
                checkSum += sum ^ (sum << 8);
                for (int i = 0; i < len; ++i) {
                    Object x = keys[i];
                    Object v = ins[i];
                    if (v.equals(m.get(x)))
                        ++sum;
                }
                checkSum += sum ^ (sum << 9);
                for (int i = len - 1; i >= 0; --i) {
                    Object x = ins[i];
                    if (m.get(x) != MISSING)
                        ++sum;
                }
                checkSum += sum ^ (sum << 10);
                for (int i = len - 1; i >= 0; --i) {
                    Object x = keys[i];
                    Object v = ins[i];
                    if (v.equals(m.get(x)))
                        ++sum;
                }
                checkSum += sum ^ (sum << 11);
                if ((j & 1) == 1) { // lower remove rate
                    for (int i = 0; i < len; ++i) {
                        Object x = keys[i];
                        if (m.remove(x) != null)
                            ++sum;
                    }
                }
                else {
                    m.clear();
                    sum += len;
                }
                checkSum += sum ^ (sum << 12);

                elapsed = System.nanoTime() - startTime;
                ++j;
                if (j >= MIN_ITERS_PER_TEST &&
                    (j >= maxIters || elapsed >= NANOS_PER_JOB))
                    break;
            }
            long ops = ((long)j) * len * OPS_PER_ITER;
            if (sum != lastSum + (int)ops)
                throw new Error(name);
            lastSum = sum;
            return elapsed / ops;
        }

    }

    static final int xorshift(int seed) { 
        seed ^= seed << 1; 
        seed ^= seed >>> 3; 
        seed ^= seed << 10;
        return seed;
    }


    static final Random rng = new Random(3152688);

    static void shuffle(Object[] a, int size) {
        for (int i= size; i>1; i--) {
            Object t = a[i-1];
            int r = rng.nextInt(i);
            a[i-1] = a[r];
            a[r] = t;
        }
    }

}

