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

class CircularLinkedList<K, V> implements Iterable<HeapNode<K, V>> {
	private HeapNode<K, V> head;
	private int size ;

	public CircularLinkedList() {
		this.head = null;
		this.size = 0;
	}

	public HeapNode<K, V> head() {
		return this.head;
	}

	public int size() {
		return this.size;
	}

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
		this.size += other_list.size;
	}

	//TODO: When adding the javadoc comments, explain that the node is "stripped" of its next/prev
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

	private final void linkNodes(final HeapNode<K, V> back, final HeapNode<K, V> front) {
		final HeapNode<K, V> front_prev = front.prev();
		final HeapNode<K, V> back_next = back.next();

		back.simpleNext(front);
		front.simplePrev(back);

		front_prev.simpleNext(back_next);
		back_next.simplePrev(front_prev);
	}
}
