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

/**
 * This class represents a heap node. <br>
 * <br>
 * Since some of the heap's operations require that
 * the user pass them heap nodes directly ({@link FibHeap#delete(HeapNode)}
 * and {@link FibHeap#decreaseKey(HeapNode, Comparable)}),
 * objects of this class have to be exposed to the user. The approach
 * chosen to do this was to simply have some of the methods be public,
 * which can be used by anyone, and some of them default, intended for
 * use within FibHeap (or auxiliary classes) itself.<br>
 * <br>
 * From the user's point of view, all that this class does is hold a 
 * key and a value. It also implements the equals() method.<br>
 * <br>
 * From the Fibonacci Heap's point of view, this class also holds a
 * reference to this node's parents, a list of its children, references
 * to its direct brothers (previous and next node, in the context of its
 * parent's list - or the root list if the parent is null) and the node's
 * mark attribute.<br>
 * <br>
 * Its function is basically that of a C struct: its methods are basically
 * getters/setters, implementing (nearly) no logic.<br>
 * <br>
 * Some of its operations could be better integrated with {@link CircularLinkedList}
 * (e.g. {@link #simpleNext(HeapNode)} feels clunky), but I chose to keep
 * this class simple because Java's type system didn't allow me to write this at the
 * level of abstraction that I wanted to.
 * 
 * @param K The type of keys HeapNode may hold.
 * @param V The type of values HeapNode may hold.
 */
public class HeapNode<K, V> {
	private K key;
	private V value;

	private HeapNode<K, V> parent;
	private CircularLinkedList<K, V> children;
	private boolean mark;
	
	private HeapNode<K, V> prev, next;

	/**
	 * Builds a new heap node, with the following
	 * initial state:
	 * <ul>
	 *   <li>Both its prev and next are itself</li>
	 *   <li>Its parent is null</li>
	 *   <li>It is unmarked (mark == false)</li>
	 *   <li>Its children list is empty</li>
	 * </ul>
	 * 
	 * This method is dumb, in that it does not check
	 * whether key is null or not (FibHeap's semantics
	 * require that the nodes' keys not be null).
	 * 
	 * @param key The node's key.
	 * @param value The node's value.
	 */
	HeapNode(final K key, final V value) {
		this.key = key;
		this.value = value;

		this.prev = this;
		this.next = this;

		this.parent = null;
		this.children = new CircularLinkedList<>();
		this.mark = false;
	}

	/**
	 * @return The node's next (i.e. the node following
	 * this one, in the list they're inserted in).
	 */
	HeapNode<K, V> next() {
		return this.next;
	}

	/**
	 * Sets this node's next to the argument passed in.
	 * This method is "simple", in that it performs no
	 * operations to guarantee the coherency of the list
	 * the node belongs to (i.e. this.simpleNext(node) will
	 * not update the CircularLinkedList this belongs to).
	 * 
	 * @param next The node's new next node.
	 */
	void simpleNext(final HeapNode<K, V> next) {
		this.next = next;
	}

	/**
	 * @return The node's next (i.e. the node preceeding
	 * this one, in the list they're inserted in).
	 */
	HeapNode<K, V> prev() {
		return this.prev;
	}

	/**
	 * Sets this node's prev to the argument passed in.
	 * This method is "simple", in that it performs no
	 * operations to guarantee the coherency of the list
	 * the node belongs to (i.e. this.simplePrev(node) will
	 * not update the CircularLinkedList this belongs to).
	 * 
	 * @param prev The node's new prev node.
	 */
	void simplePrev(final HeapNode<K, V> prev) {
		this.prev = prev;
	}

	/**
	 * @return The node's key
	 */
	public K key() {
		return this.key;
	}

	/**
	 * Sets the node's key.
	 * This method performs no checks on the validity
	 * of the key passed in (i.e. doesn't guarantee it's
	 * smaller than the current key, and doesn't guarantee
	 * it's not null).
	 * 
	 * @param key The new key.
	 */
	void key(final K key) {
		this.key = key;
	}

	/**
	 * @return The node's value.
	 */
	public V value() {
		return this.value;
	}

	/**
	 * @return The node's degree. This is equivalent
	 * to the number of nodes in this node's children
	 * list.
	 */
	int degree() {
		return this.children.size();
	}

	/**
	 * Sets the node's parent to the node passed in as
	 * an argument. Similarly to the other node operations,
	 * this method is dumb, in that it performs no checks or
	 * updates on status of the node in the list it belongs
	 * to (i.e. it doesn't check nor guarantee that this really
	 * belongs to parent's children list).
	 * 
	 * @param parent The node's new parent.
	 */
	void parent(final HeapNode<K, V> parent) {
		this.parent = parent;
	}

	/**
	 * @return The node's parent (the node whom this is a
	 * child of).
	 */
	HeapNode<K, V> parent() {
		return this.parent;
	}

	/**
	 * Adds a child to this node's children list. This is
	 * one of the few methods which are not entriely dumb:
	 * aside from adding the node to the list, it also sets
	 * the child's parent to be this node. Other than this,
	 * no other "smart" things are performed.
	 * 
	 * @param child This node's new child.
	 */
	void addChild(final HeapNode<K, V> child) {
		this.children.addNode(child);
		child.parent(this);
	}

	/**
	 * Performs the inverse of {@link #addChild(HeapNode)}:
	 * this operation removes child from node's linked list, and
	 * updates child's parent to be null as well. Other than this,
	 * the method is dumb.
	 * 
	 * @param child The child to be removed from this node's list
	 * of children.
	 */
	void removeChild(final HeapNode<K, V> child) {
		this.children.remove(child);
		child.parent(null);
	}

	/**
	 * @return This node's list of children.
	 */
	CircularLinkedList<K, V> children() {
		return this.children;
	}

	/**
	 * Sets this node's mark to the value passed in.
	 * 
	 * @param mark This node's new marked status.
	 */
	void mark(final boolean mark) {
		this.mark = mark;
	}

	/**
	 * @return The marked status of the node.
	 */
	boolean mark() {
		return this.mark;
	}

	/**
	 * Performs an equality check between this node's
	 * key and obj's key (assuming that it is a HeapNode -
	 * if this is not the case, then the method simply
	 * returns false).
	 */
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

	/**
	 * @return The result of {@link #toString()}, with
	 * {@link #children}'s {@link CircularLinkedList#toString()} appended to it.
	 */
	String toStringWithChildren() {
		return this.toString() + ": " + children.toString();
	}
}
