/*
The MIT License:
Copyright (c) 2018, �lvaro Ant�nio Santos

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package heaps.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Test;

import heaps.FibHeap;
import heaps.HeapNode;

public class FibHeapTests {
	private static final int MAX_INSERTIONS = 500000;	//Should be at least 1 (it's used in Random#nextInt(int bound), which requires bound > 0)
	private static final int MAX_KEY = 50000;
	private static final int MIN_KEY = -25000;

	private static final int generateElement(final Random rand) {
		return MIN_KEY + rand.nextInt(MAX_KEY + Math.abs(MIN_KEY) + 1);
	}

	/**
	 * Makes sure that the heap returns all, and only,
	 * the elements in the list#, and in the correct
	 * (increasing) order.
	 * 
	 * Also makes sure that the heap's size doesn't
	 * suddenly become incorrect upon a removal,
	 * and that the removed element's key was indeed
	 * the one that was reported to be the minimum.
	 * 
	 * # - Only the heap's elements' keys are verified.
	 * A buggy heap that altered its nodes' values would
	 * also pass this test (but this is not worrying,
	 * because there's no reason for the heap to even
	 * attempt to modify its nodes' values).
	 */
	private static final boolean compareMinRemovals(final List<Integer> insertions, final FibHeap heap) {
		Collections.sort(insertions);

		int expected_size = insertions.size();
		for(final int element : insertions) {
			if(heap.size() != expected_size) {
				return false;
			} else {
				--expected_size;
			}

			final int min_key = heap.min().key();
			final int removed_key = heap.removeMin().key();

			if(min_key != removed_key || removed_key != element) {
				return false;
			}
		}

		if(heap.size() != 0 || heap.min() != null || heap.removeMin() != null) {
			return false;
		}

		return true;
	}

	/**
	 * Guarantees that the heap, supposedly empty,
	 * is in a valid state (has size 0, etc).
	 */
	private void validEmptyState(final FibHeap heap) {
		assertEquals(heap.size(), 0);
		final HeapNode reported_min = heap.min();
		assertEquals(reported_min, null);
		assertEquals(reported_min, heap.removeMin());
	}

	/**
	 * Checks that the heap's initial state is and
	 * behaves as expected. Also tests some basic
	 * properties of the heap (e.g. iterating over
	 * it does not alter it, etc).
	 */
	@Test
	public void constructorTest() {
		FibHeap heap = new FibHeap();
		validEmptyState(heap);

		for(@SuppressWarnings("unused") HeapNode node : heap) {
			fail("There should be nothing to iterate over");
		}

		final int elem = generateElement(new Random());
		heap.insert(new HeapNode(elem));

		assertEquals(heap.size(), 1);
		HeapNode reported_min = heap.min();
		assertEquals(reported_min.key(), elem);

		final Iterator<HeapNode> it = heap.iterator();

		assertTrue(it.hasNext());
		final HeapNode first_it = it.next();
		assertEquals(first_it.key(), elem);
		assertFalse(it.hasNext());

		assertEquals(heap.size(), 1);
		reported_min = heap.min();
		assertEquals(reported_min.key(), elem);
		assertEquals(reported_min, heap.removeMin());

		validEmptyState(heap);

		heap = heap.union(new FibHeap());
		validEmptyState(heap);

		heap = (new FibHeap()).union(heap);
		validEmptyState(heap);
	}

	/**
	 * Checks the iterator (making sure all elements
	 * are returned by it).
	 * Checks the ordering guarantee (the first element
	 * returned by the iterator will be the least
	 * element in the heap) as well.
	 */
	@Test
	public void iteratorTest() {
		final Random rand = new Random();

		final int insertions = rand.nextInt(MAX_INSERTIONS) + 1;

		//Creates a map of <elements, number of times they appear>,
		//and puts them into the heap.
		final Map<Integer, Integer> map = new HashMap<>(Math.abs(MAX_KEY) + Math.abs(MIN_KEY));
		final FibHeap heap = new FibHeap();
		for(int i = 0; i < insertions; ++i) {
			final int elem = generateElement(rand);

			if(!map.containsKey(elem)) {
				map.put(elem, 1);
			} else {
				map.put(elem, map.get(elem) + 1);
			}

			heap.insert(new HeapNode(elem));
		}

		//Checks that each element appears the correct
		//amount of times in the heap.
		int iterations = heap.size();
		for(final HeapNode node : heap) {
			if(iterations == 0) {
				fail("Shouldn't have found more than " + heap.size() + " nodes in the iterator");
			}

			//Checks that the iterator's first element really is
			//the "least" one in the heap.
			//(This is the only order guarantee provided by the
			//Fibonacci Heap's iterator).
			if(iterations == heap.size()) {
				assertTrue(node == heap.min());
			}

			final int key = node.key();
			final Integer times = map.get(key);

			if(times == null) {
				fail("Shouldn't have found " + key + " again");
			} else if(times == 1) {
				map.remove(key);
			} else {
				map.put(key, times - 1);
			}

			--iterations;
		}

		assertEquals(iterations, 0);
		assertEquals(map.size(), 0);
	}

	/**
	 * Inserts things into the heap and checks that
	 * its size increases as expected. Afterwards,
	 * removes some things and checks that the size
	 * is still coherent.
	 */
	@Test
	public void sizeTest() {
		final Random rand = new Random();

		final int insertions = rand.nextInt(Math.max(1, MAX_INSERTIONS)) + 1;

		final FibHeap heap = new FibHeap();
		for(int i = 0; i < insertions; ++i) {
			heap.insert(new HeapNode(generateElement(rand)));
		}

		final int max_size = heap.size();
		assertEquals(insertions, max_size);

		final int removals = rand.nextInt(insertions) + 1;
		for(int i = 0; i < removals; ++i) {
			heap.removeMin();
		}

		assertEquals(heap.size(), insertions - removals);
	}

	/**
	 * Basically the same as {@link FibHeapTests#insertionRemovalTest()}, except
	 * it inserts things into 2 heaps, joins them and then checks that it
	 * the resulting heap had what it was expected to have.
	 */
	@Test
	public void unionTest() {
		final Random rand = new Random();

		final int insertions1 = rand.nextInt(Math.max(MAX_INSERTIONS/2, 1)) + 1,
				  insertions2 = rand.nextInt(Math.max(MAX_INSERTIONS/2, 1)) + 1;

		final FibHeap heap1 = new FibHeap(),
					  heap2 = new FibHeap();
		final List<Integer> insertions_list = new ArrayList<>(insertions1);

		for(int i = 0; i < insertions1; ++i) {
			final int elem = generateElement(rand);

			insertions_list.add(elem);
			heap1.insert(new HeapNode(elem));
		}

		for(int i = 0; i < insertions2; ++i) {
			final int elem = generateElement(rand);

			insertions_list.add(elem);
			heap2.insert(new HeapNode(elem));
		}

		final FibHeap heap = heap1.union(heap2);
		assertTrue(compareMinRemovals(insertions_list, heap));
	}

	/**
	 * Adds a few elements to the heap, while keeping
	 * track of them in a list, and then checks that
	 * all the removals match up with the information
	 * in the list.
	 */
	@Test
	public void insertionRemovalTest() {
		final Random rand = new Random();

		final int insertions = rand.nextInt(Math.max(MAX_INSERTIONS, 1)) + 1;

		final FibHeap heap = new FibHeap();
		final List<Integer> insertions_list = new ArrayList<>(insertions);

		for(int i = 0; i < insertions; ++i) {
			final int elem = generateElement(rand);

			insertions_list.add(elem);
			heap.insert(new HeapNode(elem));
		}

		assertTrue(compareMinRemovals(insertions_list, heap));
	}

	/**
	 * This is a pretty complex test. It tries out
	 * every operation, basically.
	 */
	@Test
	public void complexTest() {
		final int complex_insertions = Math.max(5, ((MAX_INSERTIONS)/5) * 5);	//Guarantees that the number of insertions will be divisible by 5 and > 0

		final Random rand = new Random();

		List<Integer> insertions_list = new ArrayList<>(complex_insertions);

		final int first_wave_maximum_insertions = (complex_insertions * 8)/5;	//4 heaps get 8/5 of the elements

		final FibHeap heap1 = new FibHeap(),
					  heap2 = new FibHeap(),
					  heap3 = new FibHeap(),
					  heap4 = new FibHeap();

		for(int i = 0; i < first_wave_maximum_insertions/4; ++i) {
			final int elem = generateElement(rand);

			insertions_list.add(elem);
			heap1.insert(new HeapNode(elem));
		}

		for(int i = 0; i < first_wave_maximum_insertions/8; ++i) {
			final int elem = generateElement(rand);

			insertions_list.add(elem);
			heap2.insert(new HeapNode(elem));
		}

		for(int i = 0; i < first_wave_maximum_insertions/2; ++i) {
			final int elem = generateElement(rand);

			insertions_list.add(elem);
			heap3.insert(new HeapNode(elem));
		}

		for(int i = 0; i < first_wave_maximum_insertions/8; ++i) {
			final int elem = generateElement(rand);

			insertions_list.add(elem);
			heap4.insert(new HeapNode(elem));
		}

		assertEquals(heap1.size() + heap2.size() + heap3.size() + heap4.size(), first_wave_maximum_insertions);

		FibHeap heap = heap1.union(heap2).union(heap3).union(heap4);

		assertEquals(heap.size(), first_wave_maximum_insertions);

		final FibHeap heap5 = new FibHeap();

		for(int i = 0; i < (complex_insertions * 2)/5; ++i) {	//1 heap gets 2/5 of the elements
			final int elem = generateElement(rand);

			insertions_list.add(elem);
			heap5.insert(new HeapNode(elem));
		}

		heap = heap.union(heap5);

		assertEquals(heap.size(), 2 * complex_insertions);	//In total, there are 8/5 + 2/5 elements
		assertEquals(heap.size(), insertions_list.size());

		Collections.sort(insertions_list);

		int expected_size1 = insertions_list.size();
		final int removals1 = (int) (2 * complex_insertions * Math.random());	//Note that this always leaves at least 1 elements unremoved (although that's just a quirk - the test would work even if this happened to remove all the elements)
		for(int i = 0; i < removals1; ++i) {
			assertEquals(heap.size(), expected_size1);

			--expected_size1;

			final int min_key = heap.min().key();
			final int removed_key = heap.removeMin().key();
			
			assertEquals(min_key, removed_key);
			assertEquals(removed_key, insertions_list.get(i).intValue());
		}

		assertEquals(heap.size(), expected_size1);

		final List<Integer> temp1 = new ArrayList<>(expected_size1);
		for(int i = removals1; i < insertions_list.size(); ++i) {
			temp1.add(insertions_list.get(i));
		}
		insertions_list = temp1;

		final int insertions1 = (int) (removals1 * Math.random());
		for(int i = 0; i < insertions1; ++i) {
			final int elem = generateElement(rand);

			insertions_list.add(elem);
			heap.insert(new HeapNode(elem));
		}

		assertEquals(heap.size(), expected_size1 + insertions1);
		assertEquals(heap.size(), insertions_list.size());

		Collections.sort(insertions_list);

		int expected_size2 = insertions_list.size();
		final int removals2 = (int) (expected_size2 * Math.random());
		for(int i = 0; i < removals2; ++i) {
			assertEquals(heap.size(), expected_size2);

			--expected_size2;

			final int min_key = heap.min().key();
			final int removed_key = heap.removeMin().key();
			
			assertEquals(min_key, removed_key);
			assertEquals(removed_key, insertions_list.get(i).intValue());
		}

		assertEquals(heap.size(), expected_size2);

		final List<Integer> temp2 = new ArrayList<>(expected_size2);
		for(int i = removals2; i < insertions_list.size(); ++i) {
			temp2.add(insertions_list.get(i));
		}
		insertions_list = temp2;

		final FibHeap heap6 = new FibHeap();
		final int insertions2 = (int) (removals2 * Math.random());
		for(int i = 0; i < insertions2; ++i) {
			final int elem = generateElement(rand);

			insertions_list.add(elem);
			heap6.insert(new HeapNode(elem));
		}

		heap = heap6.union(heap);

		assertEquals(heap.size(), expected_size2 + insertions2);
		assertEquals(heap.size(), insertions_list.size());

		assertTrue(compareMinRemovals(insertions_list, heap));
	}
}
