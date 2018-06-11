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
package heaps.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Test;

import heaps.FibHeap;
import heaps.HeapNode;

public class FibHeapTests {
	private static final int MAX_INSERTIONS = 5000000;
	private static final int MAX_KEY = 50000;
	private static final int MIN_KEY = -25000;
	
	private static final int generateElement(final Random rand) {
		return MIN_KEY + rand.nextInt(MAX_KEY + Math.abs(MIN_KEY) + 1);
	}
	
	private static final boolean compareMinRemovals(final List<Integer> insertions, final FibHeap heap) {
		Collections.sort(insertions);

		int expected_size = insertions.size();
		for(int element : insertions) {
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
	
	@Test
	public void iteratorTest() {
		Random rand = new Random();
		
		final int insertions = rand.nextInt(MAX_INSERTIONS);
		
		final Map<Integer, Integer> map = new HashMap<>(MAX_KEY + Math.abs(MIN_KEY));
		FibHeap heap = new FibHeap();
		for(int i = 0; i < insertions; ++i) {
			final int elem = generateElement(rand);

			if(!map.containsKey(elem)) {
				map.put(elem, 1);
			} else {
				map.put(elem, map.get(elem) + 1);
			}

			heap.insert(new HeapNode(elem));
		}

		{
			final int key = heap.removeMin().key();
			final Integer times = map.get(key);
	
			if(times == null) {
				fail("Shouldn't have found " + key);
			} else if(times == 1) {
				map.remove(key);
			} else {
				map.put(key, times - 1);
			}
		}

		int iterations = heap.size();
		for(HeapNode node : heap) {
			if(iterations == 0) {
				fail("Shouldn't have found more than " + heap.size() + " nodes in the iterator");
			}

			--iterations;

			final int key = node.key();

			final Integer times = map.get(key);

			if(times == null) {
				fail("Shouldn't have found " + key + " again");
			} else if(times == 1) {
				map.remove(key);
			} else {
				map.put(key, times - 1);
			}
		}
		
		assertEquals(iterations, 0);
		assertEquals(map.size(), 0);
	}
	
	@Test
	public void insertionTest() {
		Random rand = new Random();
		
		final int insertions = rand.nextInt(MAX_INSERTIONS);
		
		FibHeap heap = new FibHeap();
		for(int i = 0; i < insertions; ++i) {
			heap.insert(new HeapNode(generateElement(rand)));
		}

		assertEquals(insertions, heap.size());
	}
	
	@Test
	public void unionTest() {
		Random rand = new Random();

		final int insertions1 = rand.nextInt(MAX_INSERTIONS/20),
				  insertions2 = rand.nextInt(MAX_INSERTIONS/20);

		FibHeap heap1 = new FibHeap(),
				heap2 = new FibHeap();
		List<Integer> insertions_list = new ArrayList<>(insertions1);

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

		FibHeap heap = heap1.union(heap2);

		assertTrue(compareMinRemovals(insertions_list, heap));
	}

	@Test
	public void removeMinTest() {
		Random rand = new Random();
		
		final int insertions = rand.nextInt(MAX_INSERTIONS/10);
		
		FibHeap heap = new FibHeap();
		List<Integer> insertions_list = new ArrayList<>(insertions);
		
		for(int i = 0; i < insertions; ++i) {
			final int elem = generateElement(rand);

			insertions_list.add(elem);
			heap.insert(new HeapNode(elem));
		}

		assertTrue(compareMinRemovals(insertions_list, heap));
	}

	@Test
	public void complexTest() {
		Random rand = new Random();

		List<Integer> insertions_list = new ArrayList<>(MAX_INSERTIONS);

		final int first_wave_maximum_insertions = (MAX_INSERTIONS * 4)/5;

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

		for(int i = 0; i < MAX_INSERTIONS/5; ++i) {
			final int elem = generateElement(rand);

			insertions_list.add(elem);
			heap5.insert(new HeapNode(elem));
		}

		heap = heap.union(heap5);

		assertEquals(heap.size(), MAX_INSERTIONS);
		assertEquals(heap.size(), insertions_list.size());
		
		Collections.sort(insertions_list);

		int expected_size1 = insertions_list.size();
		final int removals1 = (int) (MAX_INSERTIONS * Math.random());
		for(int i = 0; i < removals1; ++i) {
			assertEquals(heap.size(), expected_size1);

			--expected_size1;

			final int min_key = heap.min().key();
			final int removed_key = heap.removeMin().key();
			
			assertEquals(min_key, removed_key);
			assertEquals(removed_key, insertions_list.get(i).intValue());
		}
		
		assertEquals(heap.size(), expected_size1);

		List<Integer> temp1 = new ArrayList<>(expected_size1);
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
		
		List<Integer> temp2 = new ArrayList<>(expected_size2);
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
