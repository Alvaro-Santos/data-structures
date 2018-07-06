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

import java.util.Iterator;

/**
 * This is a simple implementation of a circular doubly linked list.
 * This implementation is tied to {@link FibHeap}: its nodes are of
 * type HeapNode, and it is not meant to be used by other classes.
 * It could just as well be a private static class in FibHeap - the
 * only reason for this not to be the case is that it would make the
 * code harder to follow, due to its length.<br>
 * <br>
 * This list can be basically thought of as a regular doubly linked
 * list whose head points to its tail (and vice-versa).
 *
 * @param <K> The type of keys the list's nodes will hold.
 * @param <V> The type of values the list's nodes will hold.
 */
class CircularLinkedList<K, V> implements Iterable<HeapNode<K, V>> {
	private HeapNode<K, V> head;
	private int size ;

	/**
	 * Constructs an empty CircularLinkedList.
	 */
	public CircularLinkedList() {
		this.head = null;
		this.size = 0;
	}

	/**
	 * @return The head of the list. The head of
	 * the list is defined to be one of its nodes,
	 * <i>possibly</i> picked at random (in constant time).
	 */
	public HeapNode<K, V> head() {
		return this.head;
	}

	/**
	 * @return The number of nodes this list is currently
	 * managing.
	 */
	public int size() {
		return this.size;
	}

	/**
	 * Appends another CircularLinkedList to this one, in constant
	 * time. The other list will also be altered, such that it also
	 * contains all the nodes in this list (effectively, they both
	 * hold the same collection of nodes, although invocations of their
	 * {@link #head()} method will most likely not return the same value).
	 * 
	 * @param other_list The list to append to this one.
	 */
	public void append(final CircularLinkedList<K, V> other_list) {
		if(other_list.head == null) {
			return;
		}

		if(this.head == null) {
			this.head = other_list.head;
			this.size = other_list.size;

			return;
		}

		linkNodes(other_list.head.prev(), this.head);

		//Updating other_list's isn't really needed for anything,
		//I just find it to be a pleasant thing to do.
		final int old_other_size = other_list.size;
		other_list.size += this.size;
		this.size += old_other_size;
	}

	/**
	 * Functions similarly to {@link #append(CircularLinkedList)}, except
	 * that the node being added to the list is stripped of its prev and
	 * next (this can be thought of as creating a circular doubly linked
	 * list where node is the only element).
	 * 
	 * @param node The node to add to this list.
	 */
	public void addNode(final HeapNode<K, V> node) {
		node.simpleNext(node);
		node.simplePrev(node);

		if(this.head == null) {
			this.head = node;
		} else {
			linkNodes(node, this.head);
		}
		
		++this.size;
	}

	/**
	 * Removes the node passed in as an argument from
	 * this list.
	 * 
	 * @param node The node to be removed.
	 */
	public void remove(final HeapNode<K, V> node) {
		final HeapNode<K, V> prev = node.prev();
		final HeapNode<K, V> next = node.next();

		prev.simpleNext(next);
		next.simplePrev(prev);

		if(this.head == node) {
			this.head = this.head.next();
		}

		if(this.size == 1) {
			this.head = null;
		}

		--this.size;
	}

	@Override
	public Iterator<HeapNode<K, V>> iterator() {
		return new Iterator<>() {
			private int steps = size;
			private HeapNode<K, V> current = head;
			
			@Override
			public boolean hasNext() {
				return this.steps != 0;
			}

			@Override
			public HeapNode<K, V> next() {
				final HeapNode<K, V> node = this.current;
				
				this.current = this.current.next();
				--this.steps;

				return node;
			}
			
		};
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("[");

		final Iterator<HeapNode<K, V>> it = this.iterator();
		while(it.hasNext()) {
			final HeapNode<K, V> node = it.next();
			
			sb.append(node.toStringWithChildren());
			
			if(it.hasNext()) {
				sb.append(", ");
			}
		}

		return sb.append("]").toString();
	}

	/**
	 * Takes two nodes, back and front, and performs the
	 * following steps:
	 * <ol>
	 * 	<li>Get back's next (let this be back_n)</li>
	 * 	<li>Get front's prev (let this be front_p)</li>
	 * 	<li>Link the nodes together such that an iterator
	 * over this list would return front_p <b>--></b> back_n
	 * <b>--></b> {the nodes between back and back_n} <b>--></b>
	 * back <b>--></b> front</li>
	 * </ol>
	 * 
	 * @param back The new node to be farthest from front
	 * (when iterating forward, i.e. to be front's prev).
	 * @param front The node we're considering to be in
	 * front of back.
	 */
	private final void linkNodes(final HeapNode<K, V> back, final HeapNode<K, V> front) {
		final HeapNode<K, V> front_prev = front.prev();
		final HeapNode<K, V> back_next = back.next();

		back.simpleNext(front);
		front.simplePrev(back);

		front_prev.simpleNext(back_next);
		back_next.simplePrev(front_prev);
	}
}
