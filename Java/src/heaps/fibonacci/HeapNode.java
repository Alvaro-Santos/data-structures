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

public class HeapNode<K, V> {
	private K key;
	private V value;

	private HeapNode<K, V> parent;
	private CircularLinkedList<K, V> children;
	private boolean mark;
	
	private HeapNode<K, V> prev, next;
	
	public HeapNode(final K key, final V value) {
		this.key = key;
		this.value = value;

		this.prev = this;
		this.next = this;

		this.parent = null;
		this.children = new CircularLinkedList<>();
		this.mark = false;
	}

	HeapNode<K, V> next() {
		return this.next;
	}

	void simpleNext(final HeapNode<K, V> next) {
		this.next = next;
	}

	HeapNode<K, V> prev() {
		return this.prev;
	}

	void simplePrev(final HeapNode<K, V> prev) {
		this.prev = prev;
	}

	public K key() {
		return this.key;
	}

	void key(final K key) {
		this.key = key;
	}

	public V value() {
		return this.value;
	}

	int degree() {
		return this.children.size();
	}

	void parent(final HeapNode<K, V> parent) {
		this.parent = parent;
	}

	HeapNode<K, V> parent() {
		return this.parent;
	}

	void addChild(final HeapNode<K, V> child) {
		this.children.addNode(child);
		child.parent(this);
	}

	void removeChild(final HeapNode<K, V> child) {
		this.children.remove(child);
		child.parent(null);
	}
	
	CircularLinkedList<K, V> children() {
		return this.children;
	}

	void mark(final boolean mark) {
		this.mark = mark;
	}

	boolean mark() {
		return this.mark;
	}

	@Override
	public boolean equals(final Object obj) {
		if(obj == this) {
			return true;
		}

		if(obj == null) {
			return false;
		}

		if(!(obj instanceof HeapNode)) {
			return false;
		}

		@SuppressWarnings("rawtypes")
		final HeapNode node = (HeapNode) obj;

		return this.key.equals(node.key) && this.value.equals(node.value);
	}

	@Override
	public String toString() {
		return "<" + key + ", " + value + ">";
	}

	String toStringWithChildren() {
		return this.toString() + ": " + children.toString();
	}
}
