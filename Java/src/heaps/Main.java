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
package heaps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Main {
	public static boolean compareMinRemovals(List<Integer> insertions, FibHeap heap) {
		Collections.sort(insertions);

		System.out.println("Done sorting the insertions list");
		for(int element : insertions) {
			if(element != heap.removeMin().key()) {
				return false;
			}
		}

		return true;
	}
	
	public static void test6(String[] args) {
		FibHeap heap = new FibHeap();
		Random rand = new Random();
		
		for(int i = 0; i < 11; ++i) {
			final int key = rand.nextInt(101) - 50;
			
			System.out.println("Inserting: " + key);
			heap.insert(new HeapNode(key));
		}
		System.out.println();
		
		System.out.println("Heap as a string:");
		System.out.println(heap);
		System.out.println();

		while(heap.size() > 0) {
			System.out.println("Removed: " + heap.removeMin());
			System.out.println();
			
			System.out.println("Heap as a string:");
			System.out.println(heap);
			System.out.println();
			
			for(HeapNode node : heap) {
				System.out.println("Found: " + node.key());
			}
			System.out.println();
		}
	}
	
	private static final int MAX_INSERTIONS_test_5 = 10000000/2;
	public static void test5(String[] args) {
		Random rand = new Random();
		
		final int insertions_1 = rand.nextInt(MAX_INSERTIONS_test_5),
				  insertions_2 = rand.nextInt(MAX_INSERTIONS_test_5);
				
		System.out.println("Decided to perform " + insertions_1 + " insertions on the first heap");
		
		FibHeap heap1 = new FibHeap();
		List<Integer> insertions_list = new ArrayList<>(insertions_1);
		
		for(int i = 0; i < insertions_1; ++i) {
			final int elem = rand.nextInt(MAX_INSERTIONS_test_5/500) - MAX_INSERTIONS_test_5/1000;

			insertions_list.add(elem);
			heap1.insert(new HeapNode(elem));
		}
				
		System.out.println("Decided to perform " + insertions_2 + " insertions on the second heap");
		
		FibHeap heap2 = new FibHeap();
		
		for(int i = 0; i < insertions_2; ++i) {
			final int elem = rand.nextInt(MAX_INSERTIONS_test_5/500) - MAX_INSERTIONS_test_5/1000;

			insertions_list.add(elem);
			heap2.insert(new HeapNode(elem));
		}
		
		System.out.println(compareMinRemovals(insertions_list, heap1.union(heap2)));
	}
	
	private static final int MAX_INSERTIONS = 10000000;
	public static void test4(String[] args) {
		Random rand = new Random();
		
		final int insertions = rand.nextInt(MAX_INSERTIONS);
		System.out.println("Decided to perform " + insertions + " insertions");
		
		FibHeap heap = new FibHeap();
		List<Integer> insertions_list = new ArrayList<>(insertions);
		
		for(int i = 0; i < insertions; ++i) {
			final int elem = rand.nextInt(insertions);

			insertions_list.add(elem);
			heap.insert(new HeapNode(elem));
		}

		System.out.println(compareMinRemovals(insertions_list, heap));
	}
	
	public static void test3(String[] args) {
		FibHeap heap = new FibHeap();

		for(int i = (Integer.MAX_VALUE - 13924000/10); i < Integer.MAX_VALUE; ++i) {
			heap.insert(new HeapNode(i));
		}
		
		for(int i = (Integer.MAX_VALUE - 13924000/10); i < Integer.MAX_VALUE - 100; ++i) {
			heap.removeMin();
		}

		System.out.println(heap);
	}
	
	public static void main(String[] args) {
		System.out.println(Integer.MAX_VALUE + " -> " + FibHeap.phi_log(Integer.MAX_VALUE));
		System.out.println(Long.MAX_VALUE + " -> " + FibHeap.phi_log(Long.MAX_VALUE));
		System.out.println((Double.MAX_VALUE) + " -> " + FibHeap.phi_log(Double.MAX_VALUE));

		FibHeap heap = new FibHeap();

		heap.insert(new HeapNode(6));
		heap.insert(new HeapNode(2));		
		heap.insert(new HeapNode(3));
		heap.insert(new HeapNode(-14));
		heap.insert(new HeapNode(3));
		heap.insert(new HeapNode(19));
		heap.insert(new HeapNode(0));

		Iterator<HeapNode> it = heap.iterator();
		while(it.hasNext()) {
			System.out.println(it.next().toString());
		}

		heap.insert(new HeapNode(-99));
		heap.removeMin();
		System.out.println("--");

		it = heap.iterator();
		while(it.hasNext()) {
			System.out.println(it.next().toString());
		}
		
		System.out.println("Heap size: " + heap.size());
		System.out.println(heap.toString());
		System.out.println();
		
		while(heap.size() > 0) {
			System.out.println("A[] will go from 0 to " + FibHeap.maximum_root_degree(heap.size()) + " (inclusive) [log result would be " + FibHeap.phi_log(heap.size()) + "]");
			System.out.println("Removing: " + heap.removeMin());

			System.out.println("Heap size afterwards: " + heap.size());
			System.out.println(heap.toString());
			System.out.println();
		}
	}

	public static String repeat(final String s, int times) {
		StringBuilder sb = new StringBuilder(s.length() * times);
		
		for(int i = 0; i < times; ++i) {
			sb.append(s);
		}
		
		return sb.toString();
	}
	
	public static void test1(String[] args) {
		FibHeap heap = new FibHeap();

		System.out.println("size: " + heap.size());
		System.out.println("min: " + heap.min());
		System.out.println();

		heap.insert(new HeapNode(6));
	
		for(HeapNode n : heap.root_list) {
			System.out.println(n.key);
		}
		System.out.println("size: " + heap.size);
		System.out.println("min: " + heap.min());
		System.out.println();
		
		heap.insert(new HeapNode(2));
		
		for(HeapNode n : heap.root_list) {
			System.out.println(n.key);
		}
		System.out.println("size: " + heap.size);
		System.out.println("min: " + heap.min());
		System.out.println();
		
		heap.insert(new HeapNode(3));
		
		for(HeapNode n : heap.root_list) {
			System.out.println(n.key);
		}
		System.out.println("size: " + heap.size);
		System.out.println("min: " + heap.min());
		System.out.println();

		heap.insert(new HeapNode(-14));
		
		for(HeapNode n : heap.root_list) {
			System.out.println(n.key);
		}
		System.out.println("size: " + heap.size);
		System.out.println("min: " + heap.min());
		System.out.println();

		heap.insert(new HeapNode(3));
		
		for(HeapNode n : heap.root_list) {
			System.out.println(n.key);
		}
		System.out.println("size: " + heap.size);
		System.out.println("min: " + heap.min());
		System.out.println();

		heap.insert(new HeapNode(19));
		
		for(HeapNode n : heap.root_list) {
			System.out.println(n.key);
		}
		System.out.println("size: " + heap.size);
		System.out.println("min: " + heap.min());
		System.out.println();

		heap.insert(new HeapNode(0));
		
		for(HeapNode n : heap.root_list) {
			System.out.println(n.key);
		}
		System.out.println("size: " + heap.size);
		System.out.println("min: " + heap.min());
		System.out.println();

		System.out.println("removed: " + heap.removeMin());
		
		for(HeapNode n : heap.root_list) {
			System.out.println(n.key);
		}
		System.out.println("size: " + heap.size);
		System.out.println("min: " + heap.min());
		System.out.println();

		System.out.println("removed: " + heap.removeMin());
		
		for(HeapNode n : heap.root_list) {
			System.out.println(n.key);
		}
		System.out.println("size: " + heap.size);
		System.out.println("min: " + heap.min());
		System.out.println();

		System.out.println("removed: " + heap.removeMin());
		
		for(HeapNode n : heap.root_list) {
			System.out.println(n.key);
		}
		System.out.println("size: " + heap.size);
		System.out.println("min: " + heap.min());
		System.out.println();

		System.out.println("removed: " + heap.removeMin());
		
		for(HeapNode n : heap.root_list) {
			System.out.println(n.key);
		}
		System.out.println("size: " + heap.size);
		System.out.println("min: " + heap.min());
		System.out.println();

		System.out.println("removed: " + heap.removeMin());
		
		for(HeapNode n : heap.root_list) {
			System.out.println(n.key);
		}
		System.out.println("size: " + heap.size);
		System.out.println("min: " + heap.min());
		System.out.println();

		System.out.println("removed: " + heap.removeMin());
		
		for(HeapNode n : heap.root_list) {
			System.out.println(n.key);
		}
		System.out.println("size: " + heap.size);
		System.out.println("min: " + heap.min());
		System.out.println();

		System.out.println("removed: " + heap.removeMin());
		
		for(HeapNode n : heap.root_list) {
			System.out.println(n.key);
		}
		System.out.println("size: " + heap.size);
		System.out.println("min: " + heap.min());
		System.out.println();

		System.out.println("removed: " + heap.removeMin());
		
		for(HeapNode n : heap.root_list) {
			System.out.println(n.key);
		}
		System.out.println("size: " + heap.size);
		System.out.println("min: " + heap.min());
		System.out.println();
	}

}
