/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */

import junit.framework.*;
import java.util.*;
import java.util.concurrent.*;
import java.io.*;

public class LinkedBlockingDequeTest extends JSR166TestCase {
    public static void main(String[] args) {
	junit.textui.TestRunner.run (suite());	
    }

    public static Test suite() {
	return new TestSuite(LinkedBlockingDequeTest.class);
    }

    /**
     * Create a deque of given size containing consecutive
     * Integers 0 ... n.
     */
    private LinkedBlockingDeque populatedDeque(int n) {
        LinkedBlockingDeque q = new LinkedBlockingDeque(n);
        assertTrue(q.isEmpty());
	for(int i = 0; i < n; i++)
	    assertTrue(q.offer(new Integer(i)));
        assertFalse(q.isEmpty());
        assertEquals(0, q.remainingCapacity());
	assertEquals(n, q.size());
        return q;
    }

    /**
     * isEmpty is true before add, false after
     */
    public void testEmpty() {
        LinkedBlockingDeque q = new LinkedBlockingDeque();
        assertTrue(q.isEmpty());
        q.add(new Integer(1));
        assertFalse(q.isEmpty());
        q.add(new Integer(2));
        q.removeFirst();
        q.removeFirst();
        assertTrue(q.isEmpty());
    }

    /**
     * size changes when elements added and removed
     */
    public void testSize() {
        LinkedBlockingDeque q = populatedDeque(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(SIZE-i, q.size());
            q.removeFirst();
        }
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(i, q.size());
            q.add(new Integer(i));
        }
    }

    /**
     * offer(null) throws NPE
     */
    public void testOfferFirstNull() {
	try {
            LinkedBlockingDeque q = new LinkedBlockingDeque();
            q.offerFirst(null);
            shouldThrow();
        } catch (NullPointerException success) { 
        }   
    }

    /**
     * OfferFirst succeeds 
     */
    public void testOfferFirst() {
        LinkedBlockingDeque q = new LinkedBlockingDeque();
        assertTrue(q.offerFirst(new Integer(0)));
        assertTrue(q.offerFirst(new Integer(1)));
    }

    /**
     * OfferLast succeeds 
     */
    public void testOfferLast() {
        LinkedBlockingDeque q = new LinkedBlockingDeque();
        assertTrue(q.offerLast(new Integer(0)));
        assertTrue(q.offerLast(new Integer(1)));
    }

    /**
     *  pollFirst succeeds unless empty
     */
    public void testPollFirst() {
        LinkedBlockingDeque q = populatedDeque(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(i, ((Integer)q.pollFirst()).intValue());
        }
	assertNull(q.pollFirst());
    }

    /**
     *  pollLast succeeds unless empty
     */
    public void testPollLast() {
        LinkedBlockingDeque q = populatedDeque(SIZE);
        for (int i = SIZE-1; i >= 0; --i) {
            assertEquals(i, ((Integer)q.pollLast()).intValue());
        }
	assertNull(q.pollLast());
    }

    /**
     *  peekFirst returns next element, or null if empty
     */
    public void testPeekFirst() {
        LinkedBlockingDeque q = populatedDeque(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(i, ((Integer)q.peekFirst()).intValue());
            q.pollFirst();
            assertTrue(q.peekFirst() == null ||
                       i != ((Integer)q.peekFirst()).intValue());
        }
	assertNull(q.peekFirst());
    }

    /**
     *  peek returns next element, or null if empty
     */
    public void testPeek() {
        LinkedBlockingDeque q = populatedDeque(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(i, ((Integer)q.peek()).intValue());
            q.pollFirst();
            assertTrue(q.peek() == null ||
                       i != ((Integer)q.peek()).intValue());
        }
	assertNull(q.peek());
    }

    /**
     *  peekLast returns next element, or null if empty
     */
    public void testPeekLast() {
        LinkedBlockingDeque q = populatedDeque(SIZE);
        for (int i = SIZE-1; i >= 0; --i) {
            assertEquals(i, ((Integer)q.peekLast()).intValue());
            q.pollLast();
            assertTrue(q.peekLast() == null ||
                       i != ((Integer)q.peekLast()).intValue());
        }
	assertNull(q.peekLast());
    }

    /**
     * getFirst returns next getFirst, or throws NSEE if empty
     */
    public void testFirstElement() {
        LinkedBlockingDeque q = populatedDeque(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(i, ((Integer)q.getFirst()).intValue());
            q.pollFirst();
        }
        try {
            q.getFirst();
            shouldThrow();
        }
        catch (NoSuchElementException success) {}
    }

    /**
     *  getLast returns next element, or throws NSEE if empty
     */
    public void testLastElement() {
        LinkedBlockingDeque q = populatedDeque(SIZE);
        for (int i = SIZE-1; i >= 0; --i) {
            assertEquals(i, ((Integer)q.getLast()).intValue());
            q.pollLast();
        }
        try {
            q.getLast();
            shouldThrow();
        }
        catch (NoSuchElementException success) {}
	assertNull(q.peekLast());
    }

    /**
     *  removeFirst removes next element, or throws NSEE if empty
     */
    public void testRemoveFirst() {
        LinkedBlockingDeque q = populatedDeque(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(i, ((Integer)q.removeFirst()).intValue());
        }
        try {
            q.removeFirst();
            shouldThrow();
        } catch (NoSuchElementException success){
	}   
    }

    /**
     *  remove removes next element, or throws NSEE if empty
     */
    public void testRemove() {
        LinkedBlockingDeque q = populatedDeque(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(i, ((Integer)q.remove()).intValue());
        }
        try {
            q.remove();
            shouldThrow();
        } catch (NoSuchElementException success){
	}   
    }

    /**
     * removeFirstOccurrence(x) removes x and returns true if present
     */
    public void testRemoveFirstOccurrence() {
        LinkedBlockingDeque q = populatedDeque(SIZE);
        for (int i = 1; i < SIZE; i+=2) {
            assertTrue(q.removeFirstOccurrence(new Integer(i)));
        }
        for (int i = 0; i < SIZE; i+=2) {
            assertTrue(q.removeFirstOccurrence(new Integer(i)));
            assertFalse(q.removeFirstOccurrence(new Integer(i+1)));
        }
        assertTrue(q.isEmpty());
    }

    /**
     * removeLastOccurrence(x) removes x and returns true if present
     */
    public void testRemoveLastOccurrence() {
        LinkedBlockingDeque q = populatedDeque(SIZE);
        for (int i = 1; i < SIZE; i+=2) {
            assertTrue(q.removeLastOccurrence(new Integer(i)));
        }
        for (int i = 0; i < SIZE; i+=2) {
            assertTrue(q.removeLastOccurrence(new Integer(i)));
            assertFalse(q.removeLastOccurrence(new Integer(i+1)));
        }
        assertTrue(q.isEmpty());
    }

    /**
     * peekFirst returns element inserted with addFirst
     */
    public void testAddFirst() {
        LinkedBlockingDeque q = populatedDeque(3);
        q.pollLast();
	q.addFirst(four);
	assertEquals(four,q.peekFirst());
    }	

    /**
     * peekLast returns element inserted with addLast
     */
    public void testAddLast() {
        LinkedBlockingDeque q = populatedDeque(3);
        q.pollLast();
	q.addLast(four);
	assertEquals(four,q.peekLast());
    }	


    /**
     * A new deque has the indicated capacity, or Integer.MAX_VALUE if
     * none given
     */
    public void testConstructor1() {
        assertEquals(SIZE, new LinkedBlockingDeque(SIZE).remainingCapacity());
        assertEquals(Integer.MAX_VALUE, new LinkedBlockingDeque().remainingCapacity());
    }

    /**
     * Constructor throws IAE if  capacity argument nonpositive
     */
    public void testConstructor2() {
        try {
            LinkedBlockingDeque q = new LinkedBlockingDeque(0);
            shouldThrow();
        }
        catch (IllegalArgumentException success) {}
    }

    /**
     * Initializing from null Collection throws NPE
     */
    public void testConstructor3() {
        try {
            LinkedBlockingDeque q = new LinkedBlockingDeque(null);
            shouldThrow();
        }
        catch (NullPointerException success) {}
    }

    /**
     * Initializing from Collection of null elements throws NPE
     */
    public void testConstructor4() {
        try {
            Integer[] ints = new Integer[SIZE];
            LinkedBlockingDeque q = new LinkedBlockingDeque(Arrays.asList(ints));
            shouldThrow();
        }
        catch (NullPointerException success) {}
    }

    /**
     * Initializing from Collection with some null elements throws NPE
     */
    public void testConstructor5() {
        try {
            Integer[] ints = new Integer[SIZE];
            for (int i = 0; i < SIZE-1; ++i)
                ints[i] = new Integer(i);
            LinkedBlockingDeque q = new LinkedBlockingDeque(Arrays.asList(ints));
            shouldThrow();
        }
        catch (NullPointerException success) {}
    }

    /**
     * Deque contains all elements of collection used to initialize
     */
    public void testConstructor6() {
        try {
            Integer[] ints = new Integer[SIZE];
            for (int i = 0; i < SIZE; ++i)
                ints[i] = new Integer(i);
            LinkedBlockingDeque q = new LinkedBlockingDeque(Arrays.asList(ints));
            for (int i = 0; i < SIZE; ++i)
                assertEquals(ints[i], q.poll());
        }
        finally {}
    }

    /**
     * Deque transitions from empty to full when elements added
     */
    public void testEmptyFull() {
        LinkedBlockingDeque q = new LinkedBlockingDeque(2);
        assertTrue(q.isEmpty());
        assertEquals("should have room for 2", 2, q.remainingCapacity());
        q.add(one);
        assertFalse(q.isEmpty());
        q.add(two);
        assertFalse(q.isEmpty());
        assertEquals(0, q.remainingCapacity());
        assertFalse(q.offer(three));
    }

    /**
     * remainingCapacity decreases on add, increases on remove
     */
    public void testRemainingCapacity() {
        LinkedBlockingDeque q = populatedDeque(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(i, q.remainingCapacity());
            assertEquals(SIZE-i, q.size());
            q.remove();
        }
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(SIZE-i, q.remainingCapacity());
            assertEquals(i, q.size());
            q.add(new Integer(i));
        }
    }

    /**
     * offer(null) throws NPE
     */
    public void testOfferNull() {
	try {
            LinkedBlockingDeque q = new LinkedBlockingDeque(1);
            q.offer(null);
            shouldThrow();
        } catch (NullPointerException success) { }   
    }

    /**
     * add(null) throws NPE
     */
    public void testAddNull() {
	try {
            LinkedBlockingDeque q = new LinkedBlockingDeque(1);
            q.add(null);
            shouldThrow();
        } catch (NullPointerException success) { }   
    }

    /**
     * push(null) throws NPE
     */
    public void testPushNull() {
	try {
            LinkedBlockingDeque q = new LinkedBlockingDeque(1);
            q.push(null);
            shouldThrow();
        } catch (NullPointerException success) { }   
    }

    /**
     * push succeeds if not full; throws ISE if full
     */
    public void testPush() {
	try {
            LinkedBlockingDeque q = new LinkedBlockingDeque(SIZE);
            for (int i = 0; i < SIZE; ++i) {
                Integer I = new Integer(i);
                q.push(I);
                assertEquals(I, q.peek());
            }
            assertEquals(0, q.remainingCapacity());
            q.push(new Integer(SIZE));
        } catch (IllegalStateException success){
	}   
    }

    /**
     * peekFirst returns element inserted with push
     */
    public void testPushWithPeek() {
        LinkedBlockingDeque q = populatedDeque(3);
        q.pollLast();
	q.push(four);
	assertEquals(four,q.peekFirst());
    }	


    /**
     *  pop removes next element, or throws NSEE if empty
     */
    public void testPop() {
        LinkedBlockingDeque q = populatedDeque(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(i, ((Integer)q.pop()).intValue());
        }
        try {
            q.pop();
            shouldThrow();
        } catch (NoSuchElementException success){
	}   
    }


    /**
     * Offer succeeds if not full; fails if full
     */
    public void testOffer() {
        LinkedBlockingDeque q = new LinkedBlockingDeque(1);
        assertTrue(q.offer(zero));
        assertFalse(q.offer(one));
    }

    /**
     * add succeeds if not full; throws ISE if full
     */
    public void testAdd() {
	try {
            LinkedBlockingDeque q = new LinkedBlockingDeque(SIZE);
            for (int i = 0; i < SIZE; ++i) {
                assertTrue(q.add(new Integer(i)));
            }
            assertEquals(0, q.remainingCapacity());
            q.add(new Integer(SIZE));
        } catch (IllegalStateException success){
	}   
    }

    /**
     * addAll(null) throws NPE
     */
    public void testAddAll1() {
        try {
            LinkedBlockingDeque q = new LinkedBlockingDeque(1);
            q.addAll(null);
            shouldThrow();
        }
        catch (NullPointerException success) {}
    }

    /**
     * addAll(this) throws IAE
     */
    public void testAddAllSelf() {
        try {
            LinkedBlockingDeque q = populatedDeque(SIZE);
            q.addAll(q);
            shouldThrow();
        }
        catch (IllegalArgumentException success) {}
    }

    /**
     * addAll of a collection with null elements throws NPE
     */
    public void testAddAll2() {
        try {
            LinkedBlockingDeque q = new LinkedBlockingDeque(SIZE);
            Integer[] ints = new Integer[SIZE];
            q.addAll(Arrays.asList(ints));
            shouldThrow();
        }
        catch (NullPointerException success) {}
    }
    /**
     * addAll of a collection with any null elements throws NPE after
     * possibly adding some elements
     */
    public void testAddAll3() {
        try {
            LinkedBlockingDeque q = new LinkedBlockingDeque(SIZE);
            Integer[] ints = new Integer[SIZE];
            for (int i = 0; i < SIZE-1; ++i)
                ints[i] = new Integer(i);
            q.addAll(Arrays.asList(ints));
            shouldThrow();
        }
        catch (NullPointerException success) {}
    }
    /**
     * addAll throws ISE if not enough room
     */
    public void testAddAll4() {
        try {
            LinkedBlockingDeque q = new LinkedBlockingDeque(1);
            Integer[] ints = new Integer[SIZE];
            for (int i = 0; i < SIZE; ++i)
                ints[i] = new Integer(i);
            q.addAll(Arrays.asList(ints));
            shouldThrow();
        }
        catch (IllegalStateException success) {}
    }
    /**
     * Deque contains all elements, in traversal order, of successful addAll
     */
    public void testAddAll5() {
        try {
            Integer[] empty = new Integer[0];
            Integer[] ints = new Integer[SIZE];
            for (int i = 0; i < SIZE; ++i)
                ints[i] = new Integer(i);
            LinkedBlockingDeque q = new LinkedBlockingDeque(SIZE);
            assertFalse(q.addAll(Arrays.asList(empty)));
            assertTrue(q.addAll(Arrays.asList(ints)));
            for (int i = 0; i < SIZE; ++i)
                assertEquals(ints[i], q.poll());
        }
        finally {}
    }


    /**
     * put(null) throws NPE
     */
     public void testPutNull() {
	try {
            LinkedBlockingDeque q = new LinkedBlockingDeque(SIZE);
            q.put(null);
            shouldThrow();
        } 
        catch (NullPointerException success){
	}   
        catch (InterruptedException ie) {
	    unexpectedException();
        }
     }

    /**
     * all elements successfully put are contained
     */
     public void testPut() {
         try {
             LinkedBlockingDeque q = new LinkedBlockingDeque(SIZE);
             for (int i = 0; i < SIZE; ++i) {
                 Integer I = new Integer(i);
                 q.put(I);
                 assertTrue(q.contains(I));
             }
             assertEquals(0, q.remainingCapacity());
         }
        catch (InterruptedException ie) {
	    unexpectedException();
        }
    }

    /**
     * put blocks interruptibly if full
     */
    public void testBlockingPut() {
        Thread t = new Thread(new Runnable() {
                public void run() {
                    int added = 0;
                    try {
                        LinkedBlockingDeque q = new LinkedBlockingDeque(SIZE);
                        for (int i = 0; i < SIZE; ++i) {
                            q.put(new Integer(i));
                            ++added;
                        }
                        q.put(new Integer(SIZE));
                        threadShouldThrow();
                    } catch (InterruptedException ie){
                        threadAssertEquals(added, SIZE);
                    }   
                }});
        t.start();
        try { 
           Thread.sleep(SHORT_DELAY_MS); 
           t.interrupt();
           t.join();
        }
        catch (InterruptedException ie) {
	    unexpectedException();
        }
    }

    /**
     * put blocks waiting for take when full
     */
    public void testPutWithTake() {
        final LinkedBlockingDeque q = new LinkedBlockingDeque(2);
        Thread t = new Thread(new Runnable() {
                public void run() {
                    int added = 0;
                    try {
                        q.put(new Object());
                        ++added;
                        q.put(new Object());
                        ++added;
                        q.put(new Object());
                        ++added;
                        q.put(new Object());
                        ++added;
			threadShouldThrow();
                    } catch (InterruptedException e){
                        threadAssertTrue(added >= 2);
                    }
                }
            });
        try {
            t.start();
            Thread.sleep(SHORT_DELAY_MS);
            q.take();
            t.interrupt();
            t.join();
        } catch (Exception e){
            unexpectedException();
        }
    }

    /**
     * timed offer times out if full and elements not taken
     */
    public void testTimedOffer() {
        final LinkedBlockingDeque q = new LinkedBlockingDeque(2);
        Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        q.put(new Object());
                        q.put(new Object());
                        threadAssertFalse(q.offer(new Object(), SHORT_DELAY_MS, TimeUnit.MILLISECONDS));
                        q.offer(new Object(), LONG_DELAY_MS, TimeUnit.MILLISECONDS);
			threadShouldThrow();
                    } catch (InterruptedException success){}
                }
            });
        
        try {
            t.start();
            Thread.sleep(SMALL_DELAY_MS);
            t.interrupt();
            t.join();
        } catch (Exception e){
            unexpectedException();
        }
    }

    /**
     * take retrieves elements in FIFO order
     */
    public void testTake() {
	try {
            LinkedBlockingDeque q = populatedDeque(SIZE);
            for (int i = 0; i < SIZE; ++i) {
                assertEquals(i, ((Integer)q.take()).intValue());
            }
        } catch (InterruptedException e){
	    unexpectedException();
	}   
    }

    /**
     * take blocks interruptibly when empty
     */
    public void testTakeFromEmpty() {
        final LinkedBlockingDeque q = new LinkedBlockingDeque(2);
        Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        q.take();
			threadShouldThrow();
                    } catch (InterruptedException success){ }                
                }
            });
        try {
            t.start();
            Thread.sleep(SHORT_DELAY_MS);
            t.interrupt();
            t.join();
        } catch (Exception e){
            unexpectedException();
        }
    }

    /**
     * Take removes existing elements until empty, then blocks interruptibly
     */
    public void testBlockingTake() {
        Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        LinkedBlockingDeque q = populatedDeque(SIZE);
                        for (int i = 0; i < SIZE; ++i) {
                            assertEquals(i, ((Integer)q.take()).intValue());
                        }
                        q.take();
                        threadShouldThrow();
                    } catch (InterruptedException success){
                    }   
                }});
        t.start();
        try { 
           Thread.sleep(SHORT_DELAY_MS); 
           t.interrupt();
           t.join();
        }
        catch (InterruptedException ie) {
	    unexpectedException();
        }
    }


    /**
     * poll succeeds unless empty
     */
    public void testPoll() {
        LinkedBlockingDeque q = populatedDeque(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(i, ((Integer)q.poll()).intValue());
        }
	assertNull(q.poll());
    }

    /**
     * timed poll with zero timeout succeeds when non-empty, else times out
     */
    public void testTimedPoll0() {
        try {
            LinkedBlockingDeque q = populatedDeque(SIZE);
            for (int i = 0; i < SIZE; ++i) {
                assertEquals(i, ((Integer)q.poll(0, TimeUnit.MILLISECONDS)).intValue());
            }
            assertNull(q.poll(0, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e){
	    unexpectedException();
	}   
    }

    /**
     * timed poll with nonzero timeout succeeds when non-empty, else times out
     */
    public void testTimedPoll() {
        try {
            LinkedBlockingDeque q = populatedDeque(SIZE);
            for (int i = 0; i < SIZE; ++i) {
                assertEquals(i, ((Integer)q.poll(SHORT_DELAY_MS, TimeUnit.MILLISECONDS)).intValue());
            }
            assertNull(q.poll(SHORT_DELAY_MS, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e){
	    unexpectedException();
	}   
    }

    /**
     * Interrupted timed poll throws InterruptedException instead of
     * returning timeout status
     */
    public void testInterruptedTimedPoll() {
        Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        LinkedBlockingDeque q = populatedDeque(SIZE);
                        for (int i = 0; i < SIZE; ++i) {
                            threadAssertEquals(i, ((Integer)q.poll(SHORT_DELAY_MS, TimeUnit.MILLISECONDS)).intValue());
                        }
                        threadAssertNull(q.poll(SHORT_DELAY_MS, TimeUnit.MILLISECONDS));
                    } catch (InterruptedException success){
                    }   
                }});
        t.start();
        try { 
           Thread.sleep(SHORT_DELAY_MS); 
           t.interrupt();
           t.join();
        }
        catch (InterruptedException ie) {
	    unexpectedException();
        }
    }

    /**
     *  timed poll before a delayed offer fails; after offer succeeds;
     *  on interruption throws
     */
    public void testTimedPollWithOffer() {
        final LinkedBlockingDeque q = new LinkedBlockingDeque(2);
        Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        threadAssertNull(q.poll(SHORT_DELAY_MS, TimeUnit.MILLISECONDS));
                        q.poll(LONG_DELAY_MS, TimeUnit.MILLISECONDS);
                        q.poll(LONG_DELAY_MS, TimeUnit.MILLISECONDS);
			threadShouldThrow();
                    } catch (InterruptedException success) { }                
                }
            });
        try {
            t.start();
            Thread.sleep(SMALL_DELAY_MS);
            assertTrue(q.offer(zero, SHORT_DELAY_MS, TimeUnit.MILLISECONDS));
            t.interrupt();
            t.join();
        } catch (Exception e){
            unexpectedException();
        }
    }  


    /**
     * putFirst(null) throws NPE
     */
     public void testPutFirstNull() {
	try {
            LinkedBlockingDeque q = new LinkedBlockingDeque(SIZE);
            q.putFirst(null);
            shouldThrow();
        } 
        catch (NullPointerException success){
	}   
        catch (InterruptedException ie) {
	    unexpectedException();
        }
     }

    /**
     * all elements successfully putFirst are contained
     */
     public void testPutFirst() {
         try {
             LinkedBlockingDeque q = new LinkedBlockingDeque(SIZE);
             for (int i = 0; i < SIZE; ++i) {
                 Integer I = new Integer(i);
                 q.putFirst(I);
                 assertTrue(q.contains(I));
             }
             assertEquals(0, q.remainingCapacity());
         }
        catch (InterruptedException ie) {
	    unexpectedException();
        }
    }

    /**
     * putFirst blocks interruptibly if full
     */
    public void testBlockingPutFirst() {
        Thread t = new Thread(new Runnable() {
                public void run() {
                    int added = 0;
                    try {
                        LinkedBlockingDeque q = new LinkedBlockingDeque(SIZE);
                        for (int i = 0; i < SIZE; ++i) {
                            q.putFirst(new Integer(i));
                            ++added;
                        }
                        q.putFirst(new Integer(SIZE));
                        threadShouldThrow();
                    } catch (InterruptedException ie){
                        threadAssertEquals(added, SIZE);
                    }   
                }});
        t.start();
        try { 
           Thread.sleep(SHORT_DELAY_MS); 
           t.interrupt();
           t.join();
        }
        catch (InterruptedException ie) {
	    unexpectedException();
        }
    }

    /**
     * putFirst blocks waiting for take when full
     */
    public void testPutFirstWithTake() {
        final LinkedBlockingDeque q = new LinkedBlockingDeque(2);
        Thread t = new Thread(new Runnable() {
                public void run() {
                    int added = 0;
                    try {
                        q.putFirst(new Object());
                        ++added;
                        q.putFirst(new Object());
                        ++added;
                        q.putFirst(new Object());
                        ++added;
                        q.putFirst(new Object());
                        ++added;
			threadShouldThrow();
                    } catch (InterruptedException e){
                        threadAssertTrue(added >= 2);
                    }
                }
            });
        try {
            t.start();
            Thread.sleep(SHORT_DELAY_MS);
            q.take();
            t.interrupt();
            t.join();
        } catch (Exception e){
            unexpectedException();
        }
    }

    /**
     * timed offerFirst times out if full and elements not taken
     */
    public void testTimedOfferFirst() {
        final LinkedBlockingDeque q = new LinkedBlockingDeque(2);
        Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        q.putFirst(new Object());
                        q.putFirst(new Object());
                        threadAssertFalse(q.offerFirst(new Object(), SHORT_DELAY_MS, TimeUnit.MILLISECONDS));
                        q.offerFirst(new Object(), LONG_DELAY_MS, TimeUnit.MILLISECONDS);
			threadShouldThrow();
                    } catch (InterruptedException success){}
                }
            });
        
        try {
            t.start();
            Thread.sleep(SMALL_DELAY_MS);
            t.interrupt();
            t.join();
        } catch (Exception e){
            unexpectedException();
        }
    }

    /**
     * take retrieves elements in FIFO order
     */
    public void testTakeFirst() {
	try {
            LinkedBlockingDeque q = populatedDeque(SIZE);
            for (int i = 0; i < SIZE; ++i) {
                assertEquals(i, ((Integer)q.takeFirst()).intValue());
            }
        } catch (InterruptedException e){
	    unexpectedException();
	}   
    }

    /**
     * takeFirst blocks interruptibly when empty
     */
    public void testTakeFirstFromEmpty() {
        final LinkedBlockingDeque q = new LinkedBlockingDeque(2);
        Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        q.takeFirst();
			threadShouldThrow();
                    } catch (InterruptedException success){ }                
                }
            });
        try {
            t.start();
            Thread.sleep(SHORT_DELAY_MS);
            t.interrupt();
            t.join();
        } catch (Exception e){
            unexpectedException();
        }
    }

    /**
     * TakeFirst removes existing elements until empty, then blocks interruptibly
     */
    public void testBlockingTakeFirst() {
        Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        LinkedBlockingDeque q = populatedDeque(SIZE);
                        for (int i = 0; i < SIZE; ++i) {
                            assertEquals(i, ((Integer)q.takeFirst()).intValue());
                        }
                        q.takeFirst();
                        threadShouldThrow();
                    } catch (InterruptedException success){
                    }   
                }});
        t.start();
        try { 
           Thread.sleep(SHORT_DELAY_MS); 
           t.interrupt();
           t.join();
        }
        catch (InterruptedException ie) {
	    unexpectedException();
        }
    }


    /**
     * timed pollFirst with zero timeout succeeds when non-empty, else times out
     */
    public void testTimedPollFirst0() {
        try {
            LinkedBlockingDeque q = populatedDeque(SIZE);
            for (int i = 0; i < SIZE; ++i) {
                assertEquals(i, ((Integer)q.pollFirst(0, TimeUnit.MILLISECONDS)).intValue());
            }
            assertNull(q.pollFirst(0, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e){
	    unexpectedException();
	}   
    }

    /**
     * timed pollFirst with nonzero timeout succeeds when non-empty, else times out
     */
    public void testTimedPollFirst() {
        try {
            LinkedBlockingDeque q = populatedDeque(SIZE);
            for (int i = 0; i < SIZE; ++i) {
                assertEquals(i, ((Integer)q.pollFirst(SHORT_DELAY_MS, TimeUnit.MILLISECONDS)).intValue());
            }
            assertNull(q.pollFirst(SHORT_DELAY_MS, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e){
	    unexpectedException();
	}   
    }

    /**
     * Interrupted timed pollFirst throws InterruptedException instead of
     * returning timeout status
     */
    public void testInterruptedTimedPollFirst() {
        Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        LinkedBlockingDeque q = populatedDeque(SIZE);
                        for (int i = 0; i < SIZE; ++i) {
                            threadAssertEquals(i, ((Integer)q.pollFirst(SHORT_DELAY_MS, TimeUnit.MILLISECONDS)).intValue());
                        }
                        threadAssertNull(q.pollFirst(SHORT_DELAY_MS, TimeUnit.MILLISECONDS));
                    } catch (InterruptedException success){
                    }   
                }});
        t.start();
        try { 
           Thread.sleep(SHORT_DELAY_MS); 
           t.interrupt();
           t.join();
        }
        catch (InterruptedException ie) {
	    unexpectedException();
        }
    }

    /**
     *  timed pollFirst before a delayed offerFirst fails; after offerFirst succeeds;
     *  on interruption throws
     */
    public void testTimedPollFirstWithOfferFirst() {
        final LinkedBlockingDeque q = new LinkedBlockingDeque(2);
        Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        threadAssertNull(q.pollFirst(SHORT_DELAY_MS, TimeUnit.MILLISECONDS));
                        q.pollFirst(LONG_DELAY_MS, TimeUnit.MILLISECONDS);
                        q.pollFirst(LONG_DELAY_MS, TimeUnit.MILLISECONDS);
			threadShouldThrow();
                    } catch (InterruptedException success) { }                
                }
            });
        try {
            t.start();
            Thread.sleep(SMALL_DELAY_MS);
            assertTrue(q.offerFirst(zero, SHORT_DELAY_MS, TimeUnit.MILLISECONDS));
            t.interrupt();
            t.join();
        } catch (Exception e){
            unexpectedException();
        }
    }  

    /**
     * putLast(null) throws NPE
     */
     public void testPutLastNull() {
	try {
            LinkedBlockingDeque q = new LinkedBlockingDeque(SIZE);
            q.putLast(null);
            shouldThrow();
        } 
        catch (NullPointerException success){
	}   
        catch (InterruptedException ie) {
	    unexpectedException();
        }
     }

    /**
     * all elements successfully putLast are contained
     */
     public void testPutLast() {
         try {
             LinkedBlockingDeque q = new LinkedBlockingDeque(SIZE);
             for (int i = 0; i < SIZE; ++i) {
                 Integer I = new Integer(i);
                 q.putLast(I);
                 assertTrue(q.contains(I));
             }
             assertEquals(0, q.remainingCapacity());
         }
        catch (InterruptedException ie) {
	    unexpectedException();
        }
    }

    /**
     * putLast blocks interruptibly if full
     */
    public void testBlockingPutLast() {
        Thread t = new Thread(new Runnable() {
                public void run() {
                    int added = 0;
                    try {
                        LinkedBlockingDeque q = new LinkedBlockingDeque(SIZE);
                        for (int i = 0; i < SIZE; ++i) {
                            q.putLast(new Integer(i));
                            ++added;
                        }
                        q.putLast(new Integer(SIZE));
                        threadShouldThrow();
                    } catch (InterruptedException ie){
                        threadAssertEquals(added, SIZE);
                    }   
                }});
        t.start();
        try { 
           Thread.sleep(SHORT_DELAY_MS); 
           t.interrupt();
           t.join();
        }
        catch (InterruptedException ie) {
	    unexpectedException();
        }
    }

    /**
     * putLast blocks waiting for take when full
     */
    public void testPutLastWithTake() {
        final LinkedBlockingDeque q = new LinkedBlockingDeque(2);
        Thread t = new Thread(new Runnable() {
                public void run() {
                    int added = 0;
                    try {
                        q.putLast(new Object());
                        ++added;
                        q.putLast(new Object());
                        ++added;
                        q.putLast(new Object());
                        ++added;
                        q.putLast(new Object());
                        ++added;
			threadShouldThrow();
                    } catch (InterruptedException e){
                        threadAssertTrue(added >= 2);
                    }
                }
            });
        try {
            t.start();
            Thread.sleep(SHORT_DELAY_MS);
            q.take();
            t.interrupt();
            t.join();
        } catch (Exception e){
            unexpectedException();
        }
    }

    /**
     * timed offerLast times out if full and elements not taken
     */
    public void testTimedOfferLast() {
        final LinkedBlockingDeque q = new LinkedBlockingDeque(2);
        Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        q.putLast(new Object());
                        q.putLast(new Object());
                        threadAssertFalse(q.offerLast(new Object(), SHORT_DELAY_MS, TimeUnit.MILLISECONDS));
                        q.offerLast(new Object(), LONG_DELAY_MS, TimeUnit.MILLISECONDS);
			threadShouldThrow();
                    } catch (InterruptedException success){}
                }
            });
        
        try {
            t.start();
            Thread.sleep(SMALL_DELAY_MS);
            t.interrupt();
            t.join();
        } catch (Exception e){
            unexpectedException();
        }
    }

    /**
     * takeLast retrieves elements in FIFO order
     */
    public void testTakeLast() {
	try {
            LinkedBlockingDeque q = populatedDeque(SIZE);
            for (int i = 0; i < SIZE; ++i) {
                assertEquals(SIZE-i-1, ((Integer)q.takeLast()).intValue());
            }
        } catch (InterruptedException e){
	    unexpectedException();
	}   
    }

    /**
     * takeLast blocks interruptibly when empty
     */
    public void testTakeLastFromEmpty() {
        final LinkedBlockingDeque q = new LinkedBlockingDeque(2);
        Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        q.takeLast();
			threadShouldThrow();
                    } catch (InterruptedException success){ }                
                }
            });
        try {
            t.start();
            Thread.sleep(SHORT_DELAY_MS);
            t.interrupt();
            t.join();
        } catch (Exception e){
            unexpectedException();
        }
    }

    /**
     * TakeLast removes existing elements until empty, then blocks interruptibly
     */
    public void testBlockingTakeLast() {
        Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        LinkedBlockingDeque q = populatedDeque(SIZE);
                        for (int i = 0; i < SIZE; ++i) {
                            assertEquals(SIZE-i-1, ((Integer)q.takeLast()).intValue());
                        }
                        q.takeLast();
                        threadShouldThrow();
                    } catch (InterruptedException success){
                    }   
                }});
        t.start();
        try { 
           Thread.sleep(SHORT_DELAY_MS); 
           t.interrupt();
           t.join();
        }
        catch (InterruptedException ie) {
	    unexpectedException();
        }
    }


    /**
     * timed pollLast with zero timeout succeeds when non-empty, else times out
     */
    public void testTimedPollLast0() {
        try {
            LinkedBlockingDeque q = populatedDeque(SIZE);
            for (int i = 0; i < SIZE; ++i) {
                assertEquals(SIZE-i-1, ((Integer)q.pollLast(0, TimeUnit.MILLISECONDS)).intValue());
            }
            assertNull(q.pollLast(0, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e){
	    unexpectedException();
	}   
    }

    /**
     * timed pollLast with nonzero timeout succeeds when non-empty, else times out
     */
    public void testTimedPollLast() {
        try {
            LinkedBlockingDeque q = populatedDeque(SIZE);
            for (int i = 0; i < SIZE; ++i) {
                assertEquals(SIZE-i-1, ((Integer)q.pollLast(SHORT_DELAY_MS, TimeUnit.MILLISECONDS)).intValue());
            }
            assertNull(q.pollLast(SHORT_DELAY_MS, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e){
	    unexpectedException();
	}   
    }

    /**
     * Interrupted timed pollLast throws InterruptedException instead of
     * returning timeout status
     */
    public void testInterruptedTimedPollLast() {
        Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        LinkedBlockingDeque q = populatedDeque(SIZE);
                        for (int i = 0; i < SIZE; ++i) {
                            threadAssertEquals(SIZE-i-1, ((Integer)q.pollLast(SHORT_DELAY_MS, TimeUnit.MILLISECONDS)).intValue());
                        }
                        threadAssertNull(q.pollLast(SHORT_DELAY_MS, TimeUnit.MILLISECONDS));
                    } catch (InterruptedException success){
                    }   
                }});
        t.start();
        try { 
           Thread.sleep(SHORT_DELAY_MS); 
           t.interrupt();
           t.join();
        }
        catch (InterruptedException ie) {
	    unexpectedException();
        }
    }

    /**
     *  timed poll before a delayed offerLast fails; after offerLast succeeds;
     *  on interruption throws
     */
    public void testTimedPollWithOfferLast() {
        final LinkedBlockingDeque q = new LinkedBlockingDeque(2);
        Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        threadAssertNull(q.poll(SHORT_DELAY_MS, TimeUnit.MILLISECONDS));
                        q.poll(LONG_DELAY_MS, TimeUnit.MILLISECONDS);
                        q.poll(LONG_DELAY_MS, TimeUnit.MILLISECONDS);
			threadShouldThrow();
                    } catch (InterruptedException success) { }                
                }
            });
        try {
            t.start();
            Thread.sleep(SMALL_DELAY_MS);
            assertTrue(q.offerLast(zero, SHORT_DELAY_MS, TimeUnit.MILLISECONDS));
            t.interrupt();
            t.join();
        } catch (Exception e){
            unexpectedException();
        }
    }  


    /**
     * element returns next element, or throws NSEE if empty
     */
    public void testElement() {
        LinkedBlockingDeque q = populatedDeque(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(i, ((Integer)q.element()).intValue());
            q.poll();
        }
        try {
            q.element();
            shouldThrow();
        }
        catch (NoSuchElementException success) {}
    }

    /**
     * remove(x) removes x and returns true if present
     */
    public void testRemoveElement() {
        LinkedBlockingDeque q = populatedDeque(SIZE);
        for (int i = 1; i < SIZE; i+=2) {
            assertTrue(q.remove(new Integer(i)));
        }
        for (int i = 0; i < SIZE; i+=2) {
            assertTrue(q.remove(new Integer(i)));
            assertFalse(q.remove(new Integer(i+1)));
        }
        assertTrue(q.isEmpty());
    }
	
    /**
     * contains(x) reports true when elements added but not yet removed
     */
    public void testContains() {
        LinkedBlockingDeque q = populatedDeque(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertTrue(q.contains(new Integer(i)));
            q.poll();
            assertFalse(q.contains(new Integer(i)));
        }
    }

    /**
     * clear removes all elements
     */
    public void testClear() {
        LinkedBlockingDeque q = populatedDeque(SIZE);
        q.clear();
        assertTrue(q.isEmpty());
        assertEquals(0, q.size());
        assertEquals(SIZE, q.remainingCapacity());
        q.add(one);
        assertFalse(q.isEmpty());
        assertTrue(q.contains(one));
        q.clear();
        assertTrue(q.isEmpty());
    }

    /**
     * containsAll(c) is true when c contains a subset of elements
     */
    public void testContainsAll() {
        LinkedBlockingDeque q = populatedDeque(SIZE);
        LinkedBlockingDeque p = new LinkedBlockingDeque(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertTrue(q.containsAll(p));
            assertFalse(p.containsAll(q));
            p.add(new Integer(i));
        }
        assertTrue(p.containsAll(q));
    }

    /**
     * retainAll(c) retains only those elements of c and reports true if changed
     */
    public void testRetainAll() {
        LinkedBlockingDeque q = populatedDeque(SIZE);
        LinkedBlockingDeque p = populatedDeque(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            boolean changed = q.retainAll(p);
            if (i == 0)
                assertFalse(changed);
            else
                assertTrue(changed);

            assertTrue(q.containsAll(p));
            assertEquals(SIZE-i, q.size());
            p.remove();
        }
    }

    /**
     * removeAll(c) removes only those elements of c and reports true if changed
     */
    public void testRemoveAll() {
        for (int i = 1; i < SIZE; ++i) {
            LinkedBlockingDeque q = populatedDeque(SIZE);
            LinkedBlockingDeque p = populatedDeque(i);
            assertTrue(q.removeAll(p));
            assertEquals(SIZE-i, q.size());
            for (int j = 0; j < i; ++j) {
                Integer I = (Integer)(p.remove());
                assertFalse(q.contains(I));
            }
        }
    }

    /**
     * toArray contains all elements
     */
    public void testToArray() {
        LinkedBlockingDeque q = populatedDeque(SIZE);
	Object[] o = q.toArray();
	try {
	for(int i = 0; i < o.length; i++)
	    assertEquals(o[i], q.take());
	} catch (InterruptedException e){
	    unexpectedException();
	}    
    }

    /**
     * toArray(a) contains all elements
     */
    public void testToArray2() {
        LinkedBlockingDeque q = populatedDeque(SIZE);
	Integer[] ints = new Integer[SIZE];
	ints = (Integer[])q.toArray(ints);
	try {
	    for(int i = 0; i < ints.length; i++)
		assertEquals(ints[i], q.take());
	} catch (InterruptedException e){
	    unexpectedException();
	}    
    }

    /**
     * toArray(null) throws NPE
     */
    public void testToArray_BadArg() {
	try {
            LinkedBlockingDeque q = populatedDeque(SIZE);
	    Object o[] = q.toArray(null);
	    shouldThrow();
	} catch(NullPointerException success){}
    }

    /**
     * toArray with incompatible array type throws CCE
     */
    public void testToArray1_BadArg() {
	try {
            LinkedBlockingDeque q = populatedDeque(SIZE);
	    Object o[] = q.toArray(new String[10] );
	    shouldThrow();
	} catch(ArrayStoreException  success){}
    }

    
    /**
     * iterator iterates through all elements
     */
    public void testIterator() {
        LinkedBlockingDeque q = populatedDeque(SIZE);
	Iterator it = q.iterator();
	try {
	    while(it.hasNext()){
		assertEquals(it.next(), q.take());
	    }
	} catch (InterruptedException e){
	    unexpectedException();
	}    
    }

    /**
     * iterator.remove removes current element
     */
    public void testIteratorRemove () {
        final LinkedBlockingDeque q = new LinkedBlockingDeque(3);
        q.add(two);
        q.add(one);
        q.add(three);

        Iterator it = q.iterator();
        it.next();
        it.remove();
        
        it = q.iterator();
        assertEquals(it.next(), one);
        assertEquals(it.next(), three);
        assertFalse(it.hasNext());
    }


    /**
     * iterator ordering is FIFO
     */
    public void testIteratorOrdering() {
        final LinkedBlockingDeque q = new LinkedBlockingDeque(3);
        q.add(one);
        q.add(two);
        q.add(three);
        assertEquals(0, q.remainingCapacity());
        int k = 0;
        for (Iterator it = q.iterator(); it.hasNext();) {
            int i = ((Integer)(it.next())).intValue();
            assertEquals(++k, i);
        }
        assertEquals(3, k);
    }

    /**
     * Modifications do not cause iterators to fail
     */
    public void testWeaklyConsistentIteration () {
        final LinkedBlockingDeque q = new LinkedBlockingDeque(3);
        q.add(one);
        q.add(two);
        q.add(three);
        try {
            for (Iterator it = q.iterator(); it.hasNext();) {
                q.remove();
                it.next();
            }
        }
        catch (ConcurrentModificationException e) {
            unexpectedException();
        }
        assertEquals(0, q.size());
    }


    /**
     *  Descending iterator iterates through all elements
     */
    public void testDescendingIterator() {
        LinkedBlockingDeque q = populatedDeque(SIZE);
        int i = 0;
	Iterator it = q.descendingIterator();
        while(it.hasNext()) {
            assertTrue(q.contains(it.next()));
            ++i;
        }
        assertEquals(i, SIZE);
        assertFalse(it.hasNext());
        try {
            it.next();
        } catch(NoSuchElementException success) {
        }
    }

    /**
     *  Descending iterator ordering is reverse FIFO
     */
    public void testDescendingIteratorOrdering() {
        final LinkedBlockingDeque q = new LinkedBlockingDeque();
        for (int iters = 0; iters < 100; ++iters) {
            q.add(new Integer(3));
            q.add(new Integer(2));
            q.add(new Integer(1));
            int k = 0;
            for (Iterator it = q.descendingIterator(); it.hasNext();) {
                int i = ((Integer)(it.next())).intValue();
                assertEquals(++k, i);
            }
            
            assertEquals(3, k);
            q.remove();
            q.remove();
            q.remove();
        }
    }

    /**
     * descendingIterator.remove removes current element
     */
    public void testDescendingIteratorRemove () {
        final LinkedBlockingDeque q = new LinkedBlockingDeque();
        for (int iters = 0; iters < 100; ++iters) {
            q.add(new Integer(3));
            q.add(new Integer(2));
            q.add(new Integer(1));
            Iterator it = q.descendingIterator();
            assertEquals(it.next(), new Integer(1));
            it.remove();
            assertEquals(it.next(), new Integer(2));
            it = q.descendingIterator();
            assertEquals(it.next(), new Integer(2));
            assertEquals(it.next(), new Integer(3));
            it.remove();
            assertFalse(it.hasNext());
            q.remove();
        }
    }


    /**
     * toString contains toStrings of elements
     */
    public void testToString() {
        LinkedBlockingDeque q = populatedDeque(SIZE);
        String s = q.toString();
        for (int i = 0; i < SIZE; ++i) {
            assertTrue(s.indexOf(String.valueOf(i)) >= 0);
        }
    }        


    /**
     * offer transfers elements across Executor tasks
     */
    public void testOfferInExecutor() {
        final LinkedBlockingDeque q = new LinkedBlockingDeque(2);
        q.add(one);
        q.add(two);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(new Runnable() {
            public void run() {
                threadAssertFalse(q.offer(three));
                try {
                    threadAssertTrue(q.offer(three, MEDIUM_DELAY_MS, TimeUnit.MILLISECONDS));
                    threadAssertEquals(0, q.remainingCapacity());
                }
                catch (InterruptedException e) {
                    threadUnexpectedException();
                }
            }
        });

        executor.execute(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(SMALL_DELAY_MS);
                    threadAssertEquals(one, q.take());
                }
                catch (InterruptedException e) {
                    threadUnexpectedException();
                }
            }
        });
        
        joinPool(executor);
    }

    /**
     * poll retrieves elements across Executor threads
     */
    public void testPollInExecutor() {
        final LinkedBlockingDeque q = new LinkedBlockingDeque(2);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(new Runnable() {
            public void run() {
                threadAssertNull(q.poll());
                try {
                    threadAssertTrue(null != q.poll(MEDIUM_DELAY_MS, TimeUnit.MILLISECONDS));
                    threadAssertTrue(q.isEmpty());
                }
                catch (InterruptedException e) {
                    threadUnexpectedException();
                }
            }
        });

        executor.execute(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(SMALL_DELAY_MS);
                    q.put(one);
                }
                catch (InterruptedException e) {
                    threadUnexpectedException();
                }
            }
        });
        
        joinPool(executor);
    }

    /**
     * A deserialized serialized deque has same elements in same order
     */
    public void testSerialization() {
        LinkedBlockingDeque q = populatedDeque(SIZE);

        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream(10000);
            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(bout));
            out.writeObject(q);
            out.close();

            ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
            ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(bin));
            LinkedBlockingDeque r = (LinkedBlockingDeque)in.readObject();
            assertEquals(q.size(), r.size());
            while (!q.isEmpty()) 
                assertEquals(q.remove(), r.remove());
        } catch(Exception e){
            unexpectedException();
        }
    }

    /**
     * drainTo(null) throws NPE
     */ 
    public void testDrainToNull() {
        LinkedBlockingDeque q = populatedDeque(SIZE);
        try {
            q.drainTo(null);
            shouldThrow();
        } catch(NullPointerException success) {
        }
    }

    /**
     * drainTo(this) throws IAE
     */ 
    public void testDrainToSelf() {
        LinkedBlockingDeque q = populatedDeque(SIZE);
        try {
            q.drainTo(q);
            shouldThrow();
        } catch(IllegalArgumentException success) {
        }
    }

    /**
     * drainTo(c) empties deque into another collection c
     */ 
    public void testDrainTo() {
        LinkedBlockingDeque q = populatedDeque(SIZE);
        ArrayList l = new ArrayList();
        q.drainTo(l);
        assertEquals(q.size(), 0);
        assertEquals(l.size(), SIZE);
        for (int i = 0; i < SIZE; ++i) 
            assertEquals(l.get(i), new Integer(i));
        q.add(zero);
        q.add(one);
        assertFalse(q.isEmpty());
        assertTrue(q.contains(zero));
        assertTrue(q.contains(one));
        l.clear();
        q.drainTo(l);
        assertEquals(q.size(), 0);
        assertEquals(l.size(), 2);
        for (int i = 0; i < 2; ++i) 
            assertEquals(l.get(i), new Integer(i));
    }

    /**
     * drainTo empties full deque, unblocking a waiting put.
     */ 
    public void testDrainToWithActivePut() {
        final LinkedBlockingDeque q = populatedDeque(SIZE);
        Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        q.put(new Integer(SIZE+1));
                    } catch (InterruptedException ie){ 
                        threadUnexpectedException();
                    }
                }
            });
        try {
            t.start();
            ArrayList l = new ArrayList();
            q.drainTo(l);
            assertTrue(l.size() >= SIZE);
            for (int i = 0; i < SIZE; ++i) 
                assertEquals(l.get(i), new Integer(i));
            t.join();
            assertTrue(q.size() + l.size() >= SIZE);
        } catch(Exception e){
            unexpectedException();
        }
    }

    /**
     * drainTo(null, n) throws NPE
     */ 
    public void testDrainToNullN() {
        LinkedBlockingDeque q = populatedDeque(SIZE);
        try {
            q.drainTo(null, 0);
            shouldThrow();
        } catch(NullPointerException success) {
        }
    }

    /**
     * drainTo(this, n) throws IAE
     */ 
    public void testDrainToSelfN() {
        LinkedBlockingDeque q = populatedDeque(SIZE);
        try {
            q.drainTo(q, 0);
            shouldThrow();
        } catch(IllegalArgumentException success) {
        }
    }

    /**
     * drainTo(c, n) empties first max {n, size} elements of deque into c
     */ 
    public void testDrainToN() {
        LinkedBlockingDeque q = new LinkedBlockingDeque();
        for (int i = 0; i < SIZE + 2; ++i) {
            for(int j = 0; j < SIZE; j++)
                assertTrue(q.offer(new Integer(j)));
            ArrayList l = new ArrayList();
            q.drainTo(l, i);
            int k = (i < SIZE)? i : SIZE;
            assertEquals(l.size(), k);
            assertEquals(q.size(), SIZE-k);
            for (int j = 0; j < k; ++j) 
                assertEquals(l.get(j), new Integer(j));
            while (q.poll() != null) ;
        }
    }

}
