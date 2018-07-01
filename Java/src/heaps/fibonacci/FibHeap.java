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
package heaps.fibonacci;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

//TODO: Explain that this is a min heap
//TODO: Note that null keys are not permissible
public class FibHeap<K extends Comparable<? super K>, V> implements Iterable<HeapNode<K, V>> {
	private static final double golden_ratio = (1 + Math.sqrt(5))/2;
	private static final double ln_of_golden_ratio = Math.log(golden_ratio);

	public static final double phi_log(final double value) {	//Empirically tested for up to Double.MAX_VALUE
		return Math.log(value)/ln_of_golden_ratio;
	}

	public static final int maximum_node_degree(final int size) {
		return (int) (Math.floor(phi_log(size)));
	}

	private int size;
	private HeapNode<K, V> min;
	private CircularLinkedList<K, V> root_list;

	public FibHeap() {
		this.size = 0;
		this.min = null;
		this.root_list = new CircularLinkedList<>();
	}

	public int size() {
		return this.size;
	}

	public HeapNode<K, V> insert(final K key, final V value) {
		final HeapNode<K, V> node = new HeapNode<>(key, value);

		if(this.min == null) {
			this.root_list = new CircularLinkedList<>();
			this.root_list.addNode(node);

			this.min = node;
		} else {
			this.root_list.addNode(node);

			if(node.key().compareTo(this.min.key()) < 0) {
				this.min = node;
			}
		}

		this.size = this.size + 1;

		return node;
	}

	public HeapNode<K, V> min() {
		return this.min;
	}

	public FibHeap<K, V> union(final FibHeap<K, V> other) {
		final FibHeap<K, V> heap = new FibHeap<>();

		heap.min = this.min;

		heap.root_list.append(this.root_list);
		heap.root_list.append(other.root_list);

		if((this.min == null) || (other.min != null && other.min.key().compareTo(this.min.key()) < 0)) {
			heap.min = other.min;
		}

		heap.size = this.size + other.size;

		return heap;
	}

	public HeapNode<K, V> removeMin() {
		final HeapNode<K, V> min = this.min;

		if(min != null) {
			for(HeapNode<K, V> child : min.children()) {
				this.root_list.addNode(child);

				child.parent(null);
			}

			this.root_list.remove(min);

			if(this.root_list.size() == 0) {	//Equivalent to checking that min's next is itself
				this.min = null;
			} else {
				this.min = min.next();

				consolidate();
			}

			--this.size;
		}

		return min;
	}

	private void consolidate() {
		@SuppressWarnings("unchecked")
		final HeapNode<K, V>[] A = new HeapNode[maximum_node_degree(this.size) + 1];	//The notation A[0..D(n)] used in the book means that
																						//D(n) is inclusive (so, if maximum_root_degree(this.size) is 0,
																						//for instance, that means we must have 1 index, the 0th index,
																						//available - so we require a size of 1 + 0).
																						//(I'm pretty sure this could be maximum_root_degree(this.size - 1) + 1,
																						//since this.size is only updated after consolidate() is called, but at this point
																						//we won't be dealing with this.min any longer).

		for(final HeapNode<K, V> root : this.root_list) {
			HeapNode<K, V> new_root = root;
			int new_degree = new_root.degree();

			while(A[new_degree] != null) {
				HeapNode<K, V> other_root = A[new_degree];

				if(new_root.key().compareTo(other_root.key()) > 0) {
					final HeapNode<K, V> temp = new_root;
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

		for(final HeapNode<K, V> new_root : A) {
			if(new_root != null) {
				if(this.min == null) {
					this.root_list = new CircularLinkedList<>();
					this.root_list.addNode(new_root);

					this.min = new_root;
				} else {
					this.root_list.addNode(new_root);

					if(new_root.key().compareTo(this.min.key()) < 0) {
						this.min = new_root;
					}
				}
			}
		}
	}

	private void link(final HeapNode<K, V> other_root, final HeapNode<K, V> new_root) {
		this.root_list.remove(other_root);	//I'm pretty sure that this line is not needed:
											//All it does is remove new_root from the root_list.
											//But, when this method is called, new_root is guaranteed
											//to already have been traversed (so that it could be found and placed in array A)
											//- so it won't be traversed again (because we only visit each node
											//of the root_list once, and at the end build a new root_list
											//independent of the old one) and it being left in the root_list is irrelevant.
											//(My guess is that the authors simply described a "tidy" algorithm that
											//doesn't leave behind useless trash, even if it could, in some implementations).
											//Regardless, since it's a relatively inexpensive operation, and I have no
											//"credentials" to alter the algorithm, I'm leaving it in.
											//The fact that the tests pass with or without this line lends credence to my
											//hypothesis, though.

		new_root.addChild(other_root);

		other_root.mark(false);
	}

	public void decreaseKey(final HeapNode<K, V> node, final K newKey) {
		if(newKey.compareTo(node.key()) > 0) {
			throw new IllegalArgumentException("The new key must not be greater than the old key.");
		}

		node.key(newKey);
		final HeapNode<K, V> node_parent = node.parent();

		if(node_parent != null && newKey.compareTo(node_parent.key()) < 0) {
			cut(node, node_parent);
			cascadingCut(node_parent);
		}

		if(newKey.compareTo(this.min.key()) < 0) {
			this.min = node;
		}
	}

	private void cut(final HeapNode<K, V> child, final HeapNode<K, V> parent) {
		parent.removeChild(child);

		child.mark(false);
		this.root_list.addNode(child);
	}

	private void cascadingCut(final HeapNode<K, V> node) {
		final HeapNode<K, V> node_parent = node.parent();

		if(node_parent != null) {
			if(!node.mark()) {
				node.mark(true);
			} else {
				cut(node, node_parent);
				cascadingCut(node_parent);
			}
		}
	}

	@Override
 	public String toString() {
		return this.root_list.toString();
	}

	@Override
	public Iterator<HeapNode<K, V>> iterator() {
		return new Iterator<>() {	//Basically, iterates the nodes by searching for the next one depth-first
			private Deque<Integer> node_degrees = new LinkedList<>();	//Used as a stack
			private HeapNode<K, V> node = FibHeap.this.min;
			private int elems = FibHeap.this.size;

			{
				if(FibHeap.this.size > 0) {
					this.node_degrees.push(FibHeap.this.root_list.size() - 1);
				}
			}

			@Override
			public boolean hasNext() {
				return this.elems != 0;
			}

			@Override
			public HeapNode<K, V> next() {
				final HeapNode<K, V> node = this.node;

				final CircularLinkedList<K, V> children = node.children();
				if(children.size() > 0) {
					this.node = children.head();
					this.node_degrees.push(children.size() - 1);
				} else {					
					while(this.node_degrees.size() > 0 && this.node_degrees.peek() == 0) {
						this.node_degrees.pop();
						this.node = this.node.parent();
					}

					if(this.node_degrees.size() > 0) {
						final int size = this.node_degrees.pop();
						this.node_degrees.push(size - 1);
						this.node = this.node.next();
					}
				}

				--this.elems;
				return node;
			}
			
		};
	}
}
