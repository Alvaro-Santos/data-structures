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

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

public class FibHeap implements Iterable<HeapNode> {
	private static final double golden_ratio = (1 + Math.sqrt(5))/2;
	private static final double ln_of_golden_ratio = Math.log(golden_ratio);

	public static final double phi_log(final double value) {	//empirically tested for up to Double.MAX_VALUE
		return Math.log(value)/ln_of_golden_ratio;
	}

	public static final int maximum_root_degree(final int size) {
		return 1 + (int) (Math.ceil(phi_log(size)));	//1 and Math.ceil guarantee that this will never underestimate the maximum degree of a root node, unless phi_log deviates from the real value of log of base phi by more than one unit
	}

	int size;
	HeapNode min;
	CircularLinkedList<HeapNode> root_list;

	public FibHeap() {
		this.size = 0;
		this.min = null;
		this.root_list = new CircularLinkedList<>();
	}

	public void insert(HeapNode node) {
		node.degree(0);
		node.parent(null);
		node.child(null);
		node.mark(false);

		if(this.min == null) {
			this.root_list = new CircularLinkedList<>();
			this.root_list.add(node);

			this.min = node;
		} else {
			this.root_list.add(node);

			if(node.key() < this.min.key()) {
				this.min = node;
			}
		}

		this.size = this.size + 1;
	}

	public HeapNode min() {
		return min;
	}

	public FibHeap union(FibHeap other) {
		FibHeap heap = new FibHeap();

		heap.min = this.min;

		heap.root_list.append(this.root_list);
		heap.root_list.append(other.root_list);

		if((this.min == null) || (other.min != null && other.min.key() < this.min.key())) {
			heap.min = other.min;
		}

		heap.size = this.size + other.size;

		return heap;
	}

	public HeapNode removeMin() {
		HeapNode min = this.min;

		if(min != null) {
			for(HeapNode child : min.children()) {
				root_list.add(child);
				
				child.parent(null);
			}

			//well, at this point min's degree should be 0
			root_list.remove(min);

			if(min.equals(min.right())) {
				this.min = null;
			} else {
				this.min = min.right();
				consolidate();
			}

			this.size = this.size - 1;
		}

		return min;
	}

	private void consolidate() {
		HeapNode[] A = new HeapNode[maximum_root_degree(this.size) + 1];

		for(HeapNode root : this.root_list) {			
			HeapNode new_root = root;
			int new_degree = new_root.degree();

			while(A[new_degree] != null) {				
				HeapNode other_root = A[new_degree];

				if(new_root.key() > other_root.key()) {
					HeapNode temp = new_root;
					new_root = other_root;
					other_root = temp;
				}

				link(other_root, new_root);

				A[new_degree] = null;
				new_degree = new_degree + 1;
			}

			A[new_degree] = new_root;
		}

		this.min = null;
		
		for(HeapNode new_root : A) {			
			if(new_root != null) {
				if(this.min == null) {
					this.root_list = new CircularLinkedList<>();
					this.root_list.add(new_root);

					this.min = new_root;
				} else {
					this.root_list.add(new_root);

					if(new_root.key() < this.min.key()) {
						this.min = new_root;
					}
				}
			}
		}
	}

	private void link(HeapNode other_root, HeapNode new_root) {
//		this.root_list.remove(other_root);

		new_root.child(other_root);
		new_root.degree(new_root.degree() + 1);

		other_root.mark(false);
	}

	public int size() {
		return size;
	}

//	@Override
//	public String toString() {
//		return "[" + System.lineSeparator() + root_list.string(1) + System.lineSeparator() + "]";
//	}

	@Override
	public Iterator<HeapNode> iterator() {
		return new Iterator<HeapNode>() {
			Deque<Integer> sizes = new LinkedList<>();
			HeapNode node = min;//root_list.node;	//to guarantee that the first element returned by the iterator is the minimum element
			int elems = size;
			
			{
				if(size > 0) {
					sizes.push(root_list.size() - 1);
				}
			}
			
			@Override
			public boolean hasNext() {
				return elems != 0;
			}

			@Override
			public HeapNode next() {
				HeapNode r = node;

				CircularLinkedList<HeapNode> children = r.children;
				if(children.size() > 0) {
					node = children.node;
					sizes.push(children.size() - 1);
				} else {					
					while(sizes.size() > 0 && sizes.peek() == 0) {
						sizes.pop();
						node = node.parent;
					}

					if(sizes.size() > 0) {					
						int s = sizes.pop();
						sizes.push(s - 1);
						node = node.next;
					}
				}

				--elems;
				return r;
			}
			
		};
	}
}
