/*
The MIT License:
Copyright (c) 2018, Álvaro António Santos

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
package heaps.fibonacci.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.RepeatedTest;

import heaps.fibonacci.FibHeap;
import heaps.fibonacci.HeapNode;

//There's a bit of repetition between some of the tests,
//but refactoring feels like a waste of time (since
//there won't be any new tests anyway, and this is
//already written/reasonably understandable).
public class FibHeapTests {
	private static final int TEST_REPETITIONS = 1;	//Tested with a value of 100 (and it took around 22 minutes)

	private static final int MAX_INSERTIONS = 500000;	//Should be at least 1 (it's used in Random#nextInt(int bound), which requires bound > 0)
	private static final int MAX_KEY = 50000;
	private static final int MIN_KEY = -25000;	//The tests are written assuming that this is < 0.

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
	private static final <K extends Comparable<? super K>, V> boolean compareMinRemovals(final List<K> insertions, final FibHeap<K, V> heap) {
		Collections.sort(insertions);

		int expected_size = insertions.size();
		for(final K element : insertions) {
			if(heap.size() != expected_size) {
				return false;
			} else {
				--expected_size;
			}

			final K min_key = heap.min().key();
			final K removed_key = heap.removeMin().key();

			if(!min_key.equals(removed_key) || !removed_key.equals(element)) {
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
	private static final <K extends Comparable<? super K>, V> void validEmptyState(final FibHeap<K, V> heap) {
		assertEquals(heap.size(), 0);
		final HeapNode<K, V> reported_min = heap.min();
		assertEquals(reported_min, null);
		assertTrue(reported_min == heap.removeMin());
	}

	/**
	 * Checks that the heap's initial state is and
	 * behaves as expected. Also tests some basic
	 * properties of the heap (e.g. iterating over
	 * it does not alter it, etc).
	 */
	@RepeatedTest(TEST_REPETITIONS)
	public void constructorTest() {
		FibHeap<Integer, Integer> heap = new FibHeap<>();
		validEmptyState(heap);

		for(@SuppressWarnings("unused") HeapNode<Integer, Integer> node : heap) {
			fail("There should be nothing to iterate over");
		}

		final int elem = generateElement(new Random());
		heap.insert(elem, elem);

		assertEquals(heap.size(), 1);
		HeapNode<Integer, Integer> reported_min = heap.min();
		assertEquals((int) reported_min.key(), elem);

		final Iterator<HeapNode<Integer, Integer>> it = heap.iterator();

		assertTrue(it.hasNext());
		final HeapNode<Integer, Integer> first_it = it.next();
		assertEquals((int) first_it.key(), elem);
		assertFalse(it.hasNext());

		assertEquals(heap.size(), 1);
		reported_min = heap.min();
		assertEquals((int) reported_min.key(), elem);
		assertTrue(reported_min == heap.removeMin());

		validEmptyState(heap);

		heap = heap.union(new FibHeap<>());
		validEmptyState(heap);

		heap = (new FibHeap<Integer, Integer>()).union(heap);
		validEmptyState(heap);
	}

	/**
	 * Checks the iterator (making sure all elements
	 * are returned by it).
	 * Checks the ordering guarantee (the first element
	 * returned by the iterator will be the least
	 * element in the heap) as well.
	 */
	@RepeatedTest(TEST_REPETITIONS)
	public void iteratorTest() {
		final Random rand = new Random();

		final int insertions = rand.nextInt(MAX_INSERTIONS) + 1;

		//Creates a map of <elements, number of times they appear>,
		//and puts them into the heap.
		final Map<Integer, Integer> map = new HashMap<>(Math.abs(MAX_KEY) + Math.abs(MIN_KEY));
		final FibHeap<Integer, Integer> heap = new FibHeap<>();
		for(int i = 0; i < insertions; ++i) {
			final int elem = generateElement(rand);

			if(!map.containsKey(elem)) {
				map.put(elem, 1);
			} else {
				map.put(elem, map.get(elem) + 1);
			}

			heap.insert(elem, elem);
		}

		//Checks that each element appears the correct
		//amount of times in the heap.
		int iterations = heap.size();
		for(final HeapNode<Integer, Integer> node : heap) {
			if(iterations == 0) {
				fail("Shouldn't have found more than " + heap.size() + " nodes in the iterator");
			}

			//Checks that the iterator's first element really is
			//the "least" one in the heap.
			//(This is the only order guarantee provided by the
			//Fibonacci Heap's iterator).
			if(iterations == heap.size()) {
				assertEquals(node, heap.min());
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
	@RepeatedTest(TEST_REPETITIONS)
	public void sizeTest() {
		final Random rand = new Random();

		final int insertions = rand.nextInt(MAX_INSERTIONS) + 1;

		final FibHeap<Integer, Integer> heap = new FibHeap<>();
		for(int i = 0; i < insertions; ++i) {
			final int elem = generateElement(rand);

			heap.insert(elem, elem);
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
	@RepeatedTest(TEST_REPETITIONS)
	public void unionTest() {
		final Random rand = new Random();

		final int insertions1 = rand.nextInt(Math.max(MAX_INSERTIONS/2, 1)) + 1,
				  insertions2 = rand.nextInt(Math.max(MAX_INSERTIONS/2, 1)) + 1;

		final FibHeap<Integer, Integer> heap1 = new FibHeap<>(),
					  					heap2 = new FibHeap<>();
		final List<Integer> insertions_list = new ArrayList<>(insertions1);

		for(int i = 0; i < insertions1; ++i) {
			final int elem = generateElement(rand);

			insertions_list.add(elem);
			heap1.insert(elem, elem);
		}

		for(int i = 0; i < insertions2; ++i) {
			final int elem = generateElement(rand);

			insertions_list.add(elem);
			heap2.insert(elem, elem);
		}

		final FibHeap<Integer, Integer> heap = heap1.union(heap2);
		assertTrue(compareMinRemovals(insertions_list, heap));
	}

	/**
	 * Adds a few elements to the heap, while keeping
	 * track of them in a list, and then checks that
	 * all the removals match up with the information
	 * in the list.
	 */
	@RepeatedTest(TEST_REPETITIONS)
	public void insertionRemovalTest() {
		final Random rand = new Random();

		final int insertions = rand.nextInt(MAX_INSERTIONS) + 1;

		final FibHeap<Integer, Integer> heap = new FibHeap<>();
		final List<Integer> insertions_list = new ArrayList<>(insertions);

		for(int i = 0; i < insertions; ++i) {
			final int elem = generateElement(rand);

			insertions_list.add(elem);
			heap.insert(elem, elem);
		}

		assertTrue(compareMinRemovals(insertions_list, heap));
	}

	/**
	 * This is a pretty complex test. It tries out
	 * every* operation except for decreaseKey and delete.
	 * 
	 * *Not including things like toString(), iterator(), etc.
	 */
	@RepeatedTest(TEST_REPETITIONS)
	public void complexTest() {
		final int complex_insertions = Math.max(5, ((MAX_INSERTIONS)/5) * 5);	//Guarantees that the number of insertions will be divisible by 5 and > 0

		final Random rand = new Random();

		List<Integer> insertions_list = new ArrayList<>(complex_insertions);

		final int first_wave_maximum_insertions = (complex_insertions * 8)/5;	//4 heaps get 8/5 of the elements

		final FibHeap<Integer, Integer> heap1 = new FibHeap<>(),
										heap2 = new FibHeap<>(),
										heap3 = new FibHeap<>(),
										heap4 = new FibHeap<>();

		for(int i = 0; i < first_wave_maximum_insertions/4; ++i) {
			final int elem = generateElement(rand);

			insertions_list.add(elem);
			heap1.insert(elem, elem);
		}

		for(int i = 0; i < first_wave_maximum_insertions/8; ++i) {
			final int elem = generateElement(rand);

			insertions_list.add(elem);
			heap2.insert(elem, elem);
		}

		for(int i = 0; i < first_wave_maximum_insertions/2; ++i) {
			final int elem = generateElement(rand);

			insertions_list.add(elem);
			heap3.insert(elem, elem);
		}

		for(int i = 0; i < first_wave_maximum_insertions/8; ++i) {
			final int elem = generateElement(rand);

			insertions_list.add(elem);
			heap4.insert(elem, elem);
		}

		assertEquals(heap1.size() + heap2.size() + heap3.size() + heap4.size(), first_wave_maximum_insertions);

		FibHeap<Integer, Integer> heap = heap1.union(heap2).union(heap3).union(heap4);

		assertEquals(heap.size(), first_wave_maximum_insertions);

		final FibHeap<Integer, Integer> heap5 = new FibHeap<>();

		for(int i = 0; i < (complex_insertions * 2)/5; ++i) {	//1 heap gets 2/5 of the elements
			final int elem = generateElement(rand);

			insertions_list.add(elem);
			heap5.insert(elem, elem);
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

			final HeapNode<Integer, Integer> min = heap.min();

			assertEquals(min, heap.removeMin());
			assertEquals((int) min.key(), insertions_list.get(i).intValue());
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
			heap.insert(elem, elem);
		}

		assertEquals(heap.size(), expected_size1 + insertions1);
		assertEquals(heap.size(), insertions_list.size());

		Collections.sort(insertions_list);

		int expected_size2 = insertions_list.size();
		final int removals2 = (int) (expected_size2 * Math.random());
		for(int i = 0; i < removals2; ++i) {
			assertEquals(heap.size(), expected_size2);

			--expected_size2;

			final HeapNode<Integer, Integer> min = heap.min();

			assertEquals(min, heap.removeMin());
			assertEquals((int) min.key(), insertions_list.get(i).intValue());
		}

		assertEquals(heap.size(), expected_size2);

		final List<Integer> temp2 = new ArrayList<>(expected_size2);
		for(int i = removals2; i < insertions_list.size(); ++i) {
			temp2.add(insertions_list.get(i));
		}
		insertions_list = temp2;

		final FibHeap<Integer, Integer> heap6 = new FibHeap<>();
		final int insertions2 = (int) (removals2 * Math.random());
		for(int i = 0; i < insertions2; ++i) {
			final int elem = generateElement(rand);

			insertions_list.add(elem);
			heap6.insert(elem, elem);
		}

		heap = heap6.union(heap);

		assertEquals(heap.size(), expected_size2 + insertions2);
		assertEquals(heap.size(), insertions_list.size());

		assertTrue(compareMinRemovals(insertions_list, heap));
	}

	//TODO: Add a comment describing the test
	@RepeatedTest(TEST_REPETITIONS)
	public void decreaseKeyTest() {
		final Random rand = new Random();

		final int insertions = rand.nextInt(MAX_INSERTIONS) + 1;

		final FibHeap<Integer, Integer> heap = new FibHeap<>();
		final Map<Integer, List<HeapNode<Integer, Integer>>> insertions_map = new HashMap<>();
		final Set<Integer> key_set = new HashSet<>();

		for(int i = 0; i < insertions; ++i) {
			final int elem = generateElement(rand);

			final HeapNode<Integer, Integer> node = heap.insert(elem, elem);

			if(!insertions_map.containsKey(elem)) {
				insertions_map.put(elem, new ArrayList<>(1));
			}
			insertions_map.get(elem).add(node);

			key_set.add(elem);
		}
		final List<Integer> key_list = new ArrayList<>(key_set);

		final int decreases = rand.nextInt(insertions) + 1;

		for(int i = 0; i < decreases; ++i) {
			if(rand.nextDouble() < 0.01) {	//Sometimes, instead of a key decrease, a min removal will be done instead. Why? To force the heap to consolidate() and, hopefully, test the execution of cascadingCut() (otherwise, the heap would always just be a root_list of nodes with no children)
				final HeapNode<Integer, Integer> min = heap.removeMin();

				final List<HeapNode<Integer, Integer>> nodes_with_min_key = insertions_map.get(min.key());
				//Despite what it may seem like, the following lines CANNOT simply be replaced by
				//"nodes_with_min_key.remove(min)", because HeapNode reimplements equals(),
				//meaning that, sometimes, we'd just be removing a node with the same key as mean, but
				//NOT min itself.
				final Iterator<HeapNode<Integer, Integer>> min_nodes_it = nodes_with_min_key.iterator();
				while(min_nodes_it.hasNext()) {
					if(min_nodes_it.next() == min) {
						min_nodes_it.remove();
					}
				}

				if(nodes_with_min_key.size() == 0) {
					insertions_map.remove(min.key());
					key_list.remove(min.key());
				}
			} else {
				final int key_index = rand.nextInt(key_list.size());
				final int old_key = key_list.get(key_index);
				final int new_key = MIN_KEY + rand.nextInt(old_key + Math.abs(MIN_KEY) + 1);
	
				final List<HeapNode<Integer, Integer>> nodes_with_key = insertions_map.get(old_key);
				final HeapNode<Integer, Integer> node = nodes_with_key.remove(rand.nextInt(nodes_with_key.size()));
	
				heap.decreaseKey(node, new_key);
	
				if(nodes_with_key.size() == 0) {
					insertions_map.remove(old_key);
					key_list.remove(key_index);
				}
	
				if(!insertions_map.containsKey(new_key)) {
					insertions_map.put(new_key, new ArrayList<>(1));
					key_list.add(new_key);
				}
	
				insertions_map.get(new_key).add(node);
			}
		}

		final List<Integer> keys_repeated = new ArrayList<>(insertions_map.size());
		for(final Map.Entry<Integer, ? extends List<?>> entry : insertions_map.entrySet()) {
			for(int i = 0; i < entry.getValue().size(); ++i) {
				keys_repeated.add(entry.getKey());
			}
		}

		assertTrue(compareMinRemovals(keys_repeated, heap));
	}

	//TODO: Add a comment describing the test
	@RepeatedTest(TEST_REPETITIONS)
	public void deleteTest() {
		final Random rand = new Random();

		final int insertions = rand.nextInt(MAX_INSERTIONS) + 1;

		final FibHeap<Integer, Integer> heap = new FibHeap<>();
		final Map<Integer, List<HeapNode<Integer, Integer>>> insertions_map = new HashMap<>();
		final Set<Integer> key_set = new HashSet<>();

		for(int i = 0; i < insertions; ++i) {
			final int elem = generateElement(rand);

			final HeapNode<Integer, Integer> node = heap.insert(elem, elem);

			if(!insertions_map.containsKey(elem)) {
				insertions_map.put(elem, new ArrayList<>(1));
			}
			insertions_map.get(elem).add(node);

			key_set.add(elem);
		}
		final List<Integer> key_list = new ArrayList<>(key_set);

		final int deletions = rand.nextInt(insertions) + 1;

		for(int i = 0; i < deletions; ++i) {
			final int key_index = rand.nextInt(key_list.size());
			final int key = key_list.get(key_index);

			final List<HeapNode<Integer, Integer>> nodes_with_key = insertions_map.get(key);
			final HeapNode<Integer, Integer> node = nodes_with_key.remove(rand.nextInt(nodes_with_key.size()));

			heap.delete(node);

			if(nodes_with_key.size() == 0) {
				insertions_map.remove(key);
				key_list.remove(key_index);
			}
		}

		final List<Integer> keys_repeated = new ArrayList<>(insertions_map.size());
		for(final Map.Entry<Integer, ? extends List<?>> entry : insertions_map.entrySet()) {
			for(int i = 0; i < entry.getValue().size(); ++i) {
				keys_repeated.add(entry.getKey());
			}
		}

		assertTrue(compareMinRemovals(keys_repeated, heap));		
	}
}
