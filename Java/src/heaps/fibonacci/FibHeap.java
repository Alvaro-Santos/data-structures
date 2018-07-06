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

/**
 * This is an implementation of a min Fibonacci Heap (it has operations to
 * retrieve (one of) the node(s) whose key is minimal). This implementation
 * is based on the one found in the 19th chapter of
 * "Introduction to Algorithms, Third Edition", by Thomas H. Cormen, Charles
 * E. Leiserson, Ronald L. Rivest, and Clifford Stein,  (ISBN 978-0-262-03384-8
 * or 978-0-262-53305-8).<br>
 * <br>
 * Note that {@link #delete(HeapNode)} and {@link #decreaseKey(HeapNode, Comparable)}
 * both require the nodes to be operated upon to be passed directly to them.
 * This may be inconvenient in some cases, where it would be desirable to
 * decrease the key of/remove an unknown node, based on its key. Since there
 * is no (obvious, at least) way of implementing such operations efficiently,
 * the users of this class may instead implement it themselves, by maintaining
 * a Map<K, HeapNode<K, V>>, or similar.<br>
 * <br>
 * Additionally, it must be noted that this data structure does not support
 * null keys, and that it supports having multiple nodes with the same key.
 * 
 * @param <K> The type of keys the nodes of this heap will hold.
 * @param <V> The type of values the nodes of this heap will hold.
 */
public class FibHeap<K extends Comparable<? super K>, V> implements Iterable<HeapNode<K, V>> {
	private static final double golden_ratio = (1 + Math.sqrt(5))/2;
	private static final double ln_of_golden_ratio = Math.log(golden_ratio);

	/**
	 * @param value The value whose logarithm we wish to
	 * compute.
	 * 
	 * @return The base phi logarithm of value.
	 */
	public static final double phi_log(final double value) {	//Empirically tested for up to Double.MAX_VALUE
		return Math.log(value)/ln_of_golden_ratio;
	}

	/**
	 * @param size The size of the current heap.
	 * 
	 * @return The highest degree a node may have when it
	 * belongs to a heap with size nodes.
	 */
	public static final int maximum_node_degree(final int size) {
		return (int) (Math.floor(phi_log(size)));
	}

	private int size;
	private HeapNode<K, V> min;
	private CircularLinkedList<K, V> root_list;

	/**
	 * Constructs an empty Fibonacci Heap.
	 */
	public FibHeap() {
		this.size = 0;
		this.min = null;
		this.root_list = new CircularLinkedList<>();
	}

	/**
	 * @return The number of nodes in this heap.
	 */
	public int size() {
		return this.size;
	}

	/** 
	 * Creates a node holding the key and value passed in,
	 * inserts it into the Fibonacci Heap, and returns a
	 * reference to it.<br>
	 * <br>
	 * The key must not be null, but this heap allows for
	 * duplicate (triplicate, ..., n-plicate) keys.<br>
	 * <br>
	 * Nodes returned by this operation <b>may</b> be
	 * validly passed to {@link #delete(HeapNode)} and
	 * {@link #decreaseKey(HeapNode, Comparable)}.
	 * 
	 * @param key The key to insert into the heap.
	 * @param value The value to insert into the heap.
	 * @return The inserted node.
	 */
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

	/**
	 * Nodes returned by this operation <b>may not</b>
	 * be passed to {@link #delete(HeapNode)}, nor to
	 * {@link #decreaseKey(HeapNode, Comparable)}.
	 * 
	 * @return A node whose key is no greater than the
	 * key of any other node in the heap.
	 */
	public HeapNode<K, V> min() {
		return this.min;
	}

	/**
	 * This operation "destroys" this heap, as well as the
	 * one passed in as an argumment (this is due to reusing
	 * their nodes to create their union, meaning that any
	 * operations on one of the 3 heaps may interfere with
	 * another's).
	 * 
	 * @param other The heap to join with this one.
	 * @return A new heap, resulting from joining this with
	 * other.
	 */
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

	/**
	 * Removes a node with minimum key from the heap.<br>
	 * <br>
	 * Nodes returned by this operation <b>may not</b>
	 * be passed to {@link #delete(HeapNode)}, nor to
	 * {@link #decreaseKey(HeapNode, Comparable)}.
	 * 
	 * @return The removed node.
	 */
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
				this.min = min.next();	//Yet another line of code which I'm pretty sure could be ellided
										//(and again, my hypothesis is backed by the tests passing even if
										//the line is commented out): consolidate() never uses the value of
										//this.min, and eventually sets it to the actual new min. Regardless,
										//I've kept the line, since I'm basing myself on the book.

				consolidate();
			}

			--this.size;
		}

		return min;
	}

	/**
	 * This method is a straightforward implementation of
	 * its analogue in the book mentioned in this class'
	 * javadoc comment. Its purpose is to "reshape" ("consolidate")
	 * the heap, by joining root nodes.
	 */
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

	/**
	 * This method is a straightforward implementation of
	 * its analogue in the book mentioned in this class'
	 * javadoc comment. It's used by {@link #consolidate()}
	 * to perform the actual linking of 2 nodes.
	 * 
	 * @param other_root The root to be removed from the
	 * root list.
	 * @param new_root The root that other_root will be
	 * attached to (i.e. its new parent).
	 */
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

	/**
	 * Decreases node's key, altering the heap as needed
	 * to maintain its invariants. The node that this
	 * operation is used on <b>may</b> be passed again
	 * to other heap operations, such as
	 * {@link #decreaseKey(HeapNode, Comparable)} and
	 * {@link #delete(HeapNode)}.<br>
	 * <br>
	 * Note that guaranteeing that newKey is lesser than
	 * or equal to node's current key is the caller's
	 * responsibility. Failing to hone this contract will
	 * result in an exception being thrown.
	 * 
	 * @param node The node whose key is to be decreased.
	 * @param newKey The node's new key.
	 * 
	 * @throws IllegalArgumentException If newKey is greater
	 * than node's key.
	 */
	public void decreaseKey(final HeapNode<K, V> node, final K newKey) throws IllegalArgumentException {
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

	/**
	 * This method is a straightforward implementation of
	 * its analogue in the book mentioned in this class'
	 * javadoc comment. Its essentially an opposite of
	 * {@link #link(HeapNode, HeapNode)}: it removes child
	 * from parent's children list, and adds inserts it
	 * into the heap as a root.
	 * 
	 * @param child The node that'll become a new root.
	 * @param parent The node that'll lose a child.
	 */
	private void cut(final HeapNode<K, V> child, final HeapNode<K, V> parent) {
		parent.removeChild(child);

		child.mark(false);
		this.root_list.addNode(child);
	}

	/**
	 * This method is a straightforward implementation of
	 * its analogue in the book mentioned in this class'
	 * javadoc comment. It cuts node from node's parent,
	 * and "propagates" the cut upwards (i.e. cuts node's
	 * parent from its own parent) if it was marked as
	 * having already lost a child node since it was last
	 * cut from its parent or linked to a new parent.
	 * This method stops upon reaching a root node.
	 *
	 * @param node The node to (possibly) cut.
	 */
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

	/**
	 * Removes the given node from the heap, reshaping it
	 * if needed to guarantee its invariants. The node that
	 * this operation is used on <b>may not</b> be passed
	 * again to other heap operations, such as
	 * {@link #decreaseKey(HeapNode, Comparable)} and
	 * {@link #delete(HeapNode)}.<br>
	 * 
	 * @param node The node to remove from the heap.
	 */
	//Since the Comparable interface has no least element,
	//we can't use the book's implementation here.
	//Instead, we have to reimplement decreaseKey() and removeMin().
	public void delete(final HeapNode<K, V> node) {
		//decreaseKey() [simplified]
		final HeapNode<K, V> node_parent = node.parent();

		if(node_parent != null) {
			cut(node, node_parent);
			cascadingCut(node_parent);
		}

		this.min = node;	//I'm pretty sure this is useless, for the same reason as in the case below.

		//removeMin() [simplified]
		for(HeapNode<K, V> child : node.children()) {
			this.root_list.addNode(child);

			child.parent(null);
		}

		this.root_list.remove(node);

		if(this.root_list.size() == 0) {
			this.min = null;
		} else {
			this.min = node.next();	//(This is the case mentioned above).
									//I'm pretty sure this is useless (for reasons explained in removeMin()).

			consolidate();
		}

		--this.size;
	}

	@Override
 	public String toString() {
		return this.root_list.toString();
	}

	/**
	 * The iterator returned by this operation steps
	 * over the heap's nodes in an arbitrary, undefined,
	 * order. The only guarantee is that the first node
	 * returned by the iterator will correspond to the one
	 * that would be returned by an invocation to
	 *  {@link #removeMin()}.<br>
	 * <br>
	 * Nodes returned by the iterator returned by
	 * this operation <b>may</b> be validly passed
	 * to {@link #delete(HeapNode)} and
	 * {@link #decreaseKey(HeapNode, Comparable)}.
	 * 
	 * @return An iterator over the heap's nodes.
	 */
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
