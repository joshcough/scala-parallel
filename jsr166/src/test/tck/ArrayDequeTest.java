/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */

import junit.framework.*;
import java.util.*;
import java.util.concurrent.*;

public class ArrayDequeTest extends JSR166TestCase {
    public static void main(String[] args) {
	junit.textui.TestRunner.run (suite());	
    }

    public static Test suite() {
	return new TestSuite(ArrayDequeTest.class);
    }

    /**
     * Create a queue of given size containing consecutive
     * Integers 0 ... n.
     */
    private ArrayDeque populatedDeque(int n) {
        ArrayDeque q = new ArrayDeque();
        assertTrue(q.isEmpty());
	for(int i = 0; i < n; ++i)
	    assertTrue(q.offerLast(new Integer(i)));
        assertFalse(q.isEmpty());
	assertEquals(n, q.size());
        return q;
    }
 
    /**
     * new queue is empty
     */
    public void testConstructor1() {
        assertEquals(0, new ArrayDeque().size());
    }

    /**
     * Initializing from null Collection throws NPE
     */
    public void testConstructor3() {
        try {
            ArrayDeque q = new ArrayDeque((Collection)null);
            shouldThrow();
        }
        catch (NullPointerException success) {}
    }

    /**
     * Queue contains all elements of collection used to initialize

     */
    public void testConstructor6() {
        try {
            Integer[] ints = new Integer[SIZE];
            for (int i = 0; i < SIZE; ++i)
                ints[i] = new Integer(i);
            ArrayDeque q = new ArrayDeque(Arrays.asList(ints));
            for (int i = 0; i < SIZE; ++i)
                assertEquals(ints[i], q.pollFirst());
        }
        finally {}
    }

    /**
     * isEmpty is true before add, false after
     */
    public void testEmpty() {
        ArrayDeque q = new ArrayDeque();
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
        ArrayDeque q = populatedDeque(SIZE);
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
     * push(null) throws NPE
     */
    public void testPushNull() {
	try {
            ArrayDeque q = new ArrayDeque(1);
            q.push(null);
            shouldThrow();
        } catch (NullPointerException success) { }   
    }

    /**
     * peekFirst returns element inserted with push
     */
    public void testPush() {
        ArrayDeque q = populatedDeque(3);
        q.pollLast();
	q.push(four);
	assertEquals(four,q.peekFirst());
    }	

    /**
     *  pop removes next element, or throws NSEE if empty
     */
    public void testPop() {
        ArrayDeque q = populatedDeque(SIZE);
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
     * offer(null) throws NPE
     */
    public void testOfferFirstNull() {
	try {
            ArrayDeque q = new ArrayDeque();
            q.offerFirst(null);
            shouldThrow();
        } catch (NullPointerException success) { 
        }   
    }

    /**
     * OfferFirst succeeds 
     */
    public void testOfferFirst() {
        ArrayDeque q = new ArrayDeque();
        assertTrue(q.offerFirst(new Integer(0)));
        assertTrue(q.offerFirst(new Integer(1)));
    }

    /**
     * OfferLast succeeds 
     */
    public void testOfferLast() {
        ArrayDeque q = new ArrayDeque();
        assertTrue(q.offerLast(new Integer(0)));
        assertTrue(q.offerLast(new Integer(1)));
    }

    /**
     * add succeeds
     */
    public void testAdd() {
        ArrayDeque q = new ArrayDeque();
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(i, q.size());
            assertTrue(q.add(new Integer(i)));
        }
    }

    /**
     * addAll(null) throws NPE
     */
    public void testAddAll1() {
        try {
            ArrayDeque q = new ArrayDeque();
            q.addAll(null);
            shouldThrow();
        }
        catch (NullPointerException success) {}
    }

    /**
     * Queue contains all elements, in traversal order, of successful addAll
     */
    public void testAddAll5() {
        try {
            Integer[] empty = new Integer[0];
            Integer[] ints = new Integer[SIZE];
            for (int i = 0; i < SIZE; ++i)
                ints[i] = new Integer(i);
            ArrayDeque q = new ArrayDeque();
            assertFalse(q.addAll(Arrays.asList(empty)));
            assertTrue(q.addAll(Arrays.asList(ints)));
            for (int i = 0; i < SIZE; ++i)
                assertEquals(ints[i], q.pollFirst());
        }
        finally {}
    }

    /**
     *  pollFirst succeeds unless empty
     */
    public void testPollFirst() {
        ArrayDeque q = populatedDeque(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(i, ((Integer)q.pollFirst()).intValue());
        }
	assertNull(q.pollFirst());
    }

    /**
     *  pollLast succeeds unless empty
     */
    public void testPollLast() {
        ArrayDeque q = populatedDeque(SIZE);
        for (int i = SIZE-1; i >= 0; --i) {
            assertEquals(i, ((Integer)q.pollLast()).intValue());
        }
	assertNull(q.pollLast());
    }

    /**
     *  poll succeeds unless empty
     */
    public void testPoll() {
        ArrayDeque q = populatedDeque(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(i, ((Integer)q.poll()).intValue());
        }
	assertNull(q.poll());
    }

    /**
     *  remove removes next element, or throws NSEE if empty
     */
    public void testRemove() {
        ArrayDeque q = populatedDeque(SIZE);
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
     *  peekFirst returns next element, or null if empty
     */
    public void testPeekFirst() {
        ArrayDeque q = populatedDeque(SIZE);
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
        ArrayDeque q = populatedDeque(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertEquals(i, ((Integer)q.peek()).intValue());
            q.poll();
            assertTrue(q.peek() == null ||
                       i != ((Integer)q.peek()).intValue());
        }
	assertNull(q.peek());
    }

    /**
     *  peekLast returns next element, or null if empty
     */
    public void testPeekLast() {
        ArrayDeque q = populatedDeque(SIZE);
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
        ArrayDeque q = populatedDeque(SIZE);
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
        ArrayDeque q = populatedDeque(SIZE);
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
        ArrayDeque q = populatedDeque(SIZE);
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
     * removeFirstOccurrence(x) removes x and returns true if present
     */
    public void testRemoveFirstOccurrence() {
        ArrayDeque q = populatedDeque(SIZE);
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
        ArrayDeque q = populatedDeque(SIZE);
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
     * contains(x) reports true when elements added but not yet removed
     */
    public void testContains() {
        ArrayDeque q = populatedDeque(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            assertTrue(q.contains(new Integer(i)));
            q.pollFirst();
            assertFalse(q.contains(new Integer(i)));
        }
    }

    /**
     * clear removes all elements
     */
    public void testClear() {
        ArrayDeque q = populatedDeque(SIZE);
        q.clear();
        assertTrue(q.isEmpty());
        assertEquals(0, q.size());
        q.add(new Integer(1));
        assertFalse(q.isEmpty());
        q.clear();
        assertTrue(q.isEmpty());
    }

    /**
     * containsAll(c) is true when c contains a subset of elements
     */
    public void testContainsAll() {
        ArrayDeque q = populatedDeque(SIZE);
        ArrayDeque p = new ArrayDeque();
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
        ArrayDeque q = populatedDeque(SIZE);
        ArrayDeque p = populatedDeque(SIZE);
        for (int i = 0; i < SIZE; ++i) {
            boolean changed = q.retainAll(p);
            if (i == 0)
                assertFalse(changed);
            else
                assertTrue(changed);

            assertTrue(q.containsAll(p));
            assertEquals(SIZE-i, q.size());
            p.removeFirst();
        }
    }

    /**
     * removeAll(c) removes only those elements of c and reports true if changed
     */
    public void testRemoveAll() {
        for (int i = 1; i < SIZE; ++i) {
            ArrayDeque q = populatedDeque(SIZE);
            ArrayDeque p = populatedDeque(i);
            assertTrue(q.removeAll(p));
            assertEquals(SIZE-i, q.size());
            for (int j = 0; j < i; ++j) {
                Integer I = (Integer)(p.removeFirst());
                assertFalse(q.contains(I));
            }
        }
    }

    /**
     *  toArray contains all elements
     */
    public void testToArray() {
        ArrayDeque q = populatedDeque(SIZE);
	Object[] o = q.toArray();
        Arrays.sort(o);
	for(int i = 0; i < o.length; i++)
	    assertEquals(o[i], q.pollFirst());
    }

    /**
     *  toArray(a) contains all elements
     */
    public void testToArray2() {
        ArrayDeque q = populatedDeque(SIZE);
	Integer[] ints = new Integer[SIZE];
	ints = (Integer[])q.toArray(ints);
        Arrays.sort(ints);
        for(int i = 0; i < ints.length; i++)
            assertEquals(ints[i], q.pollFirst());
    }

    /**
     * toArray(null) throws NPE
     */
    public void testToArray_BadArg() {
	try {
	    ArrayDeque l = new ArrayDeque();
	    l.add(new Object());
	    Object o[] = l.toArray(null);
	    shouldThrow();
	} catch(NullPointerException success){}
    }

    /**
     * toArray with incompatable aray type throws CCE
     */
    public void testToArray1_BadArg() {
	try {
	    ArrayDeque l = new ArrayDeque();
	    l.add(new Integer(5));
	    Object o[] = l.toArray(new String[10] );
	    shouldThrow();
	} catch(ArrayStoreException  success){}
    }
    
    /**
     *  iterator iterates through all elements
     */
    public void testIterator() {
        ArrayDeque q = populatedDeque(SIZE);
        int i = 0;
	Iterator it = q.iterator();
        while(it.hasNext()) {
            assertTrue(q.contains(it.next()));
            ++i;
        }
        assertEquals(i, SIZE);
    }

    /**
     *  iterator ordering is FIFO
     */
    public void testIteratorOrdering() {
        final ArrayDeque q = new ArrayDeque();
        q.add(new Integer(1));
        q.add(new Integer(2));
        q.add(new Integer(3));
        int k = 0;
        for (Iterator it = q.iterator(); it.hasNext();) {
            int i = ((Integer)(it.next())).intValue();
            assertEquals(++k, i);
        }

        assertEquals(3, k);
    }

    /**
     * iterator.remove removes current element
     */
    public void testIteratorRemove () {
        final ArrayDeque q = new ArrayDeque();
        final Random rng = new Random();
        for (int iters = 0; iters < 100; ++iters) {
            int max = rng.nextInt(5) + 2;
            int split = rng.nextInt(max-1) + 1;
            for (int j = 1; j <= max; ++j)
                q.add(new Integer(j));
            Iterator it = q.iterator();
            for (int j = 1; j <= split; ++j) 
                assertEquals(it.next(), new Integer(j));
            it.remove();
            assertEquals(it.next(), new Integer(split+1));
            for (int j = 1; j <= split; ++j) 
                q.remove(new Integer(j));
            it = q.iterator();
            for (int j = split+1; j <= max; ++j) {
                assertEquals(it.next(), new Integer(j));
                it.remove();
            }
            assertFalse(it.hasNext());
            assertTrue(q.isEmpty());
        }
    }

    /**
     *  Descending iterator iterates through all elements
     */
    public void testDescendingIterator() {
        ArrayDeque q = populatedDeque(SIZE);
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
        final ArrayDeque q = new ArrayDeque();
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
        final ArrayDeque q = new ArrayDeque();
        final Random rng = new Random();
        for (int iters = 0; iters < 100; ++iters) {
            int max = rng.nextInt(5) + 2;
            int split = rng.nextInt(max-1) + 1;
            for (int j = max; j >= 1; --j)
                q.add(new Integer(j));
            Iterator it = q.descendingIterator();
            for (int j = 1; j <= split; ++j) 
                assertEquals(it.next(), new Integer(j));
            it.remove();
            assertEquals(it.next(), new Integer(split+1));
            for (int j = 1; j <= split; ++j) 
                q.remove(new Integer(j));
            it = q.descendingIterator();
            for (int j = split+1; j <= max; ++j) {
                assertEquals(it.next(), new Integer(j));
                it.remove();
            }
            assertFalse(it.hasNext());
            assertTrue(q.isEmpty());
        }
    }


    /**
     * toString contains toStrings of elements
     */
    public void testToString() {
        ArrayDeque q = populatedDeque(SIZE);
        String s = q.toString();
        for (int i = 0; i < SIZE; ++i) {
            assertTrue(s.indexOf(String.valueOf(i)) >= 0);
        }
    }        

    /**
     * peekFirst returns element inserted with addFirst
     */
    public void testAddFirst() {
        ArrayDeque q = populatedDeque(3);
	q.addFirst(four);
	assertEquals(four,q.peekFirst());
    }	

    /**
     * peekLast returns element inserted with addLast
     */
    public void testAddLast() {
        ArrayDeque q = populatedDeque(3);
	q.addLast(four);
	assertEquals(four,q.peekLast());
    }	

}
