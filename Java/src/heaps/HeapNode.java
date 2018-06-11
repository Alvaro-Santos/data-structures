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


public class HeapNode implements CircularLinkedListNode<HeapNode> {
	int key;
	int degree;
	HeapNode parent;
	CircularLinkedList<HeapNode> children;
	boolean mark;
	
	HeapNode prev, next;
	public HeapNode next() {
		return next;
	}
	public void next(HeapNode node) {
		next = node;
	}
	public HeapNode prev() {
		return prev;
	}
	public void prev(HeapNode node) {
		prev = node;
	}
	
	public HeapNode(int key) {
		this.key = key;
		prev = this;
		next = this;
	}

	public void degree(int i) {
		this.degree = i;
	}

	public void parent(HeapNode object) {
		this.parent = object;
	}

	public void child(HeapNode object) {
		if(object == null) {
			children = new CircularLinkedList<>();
		} else {
			children.add(object);
			object.parent(this);
		}
	}

	public void mark(boolean b) {
		mark = b;
	}

	public int key() {
		return key;
	}

	public Iterable<HeapNode> children() {
		return children;
	}

	public HeapNode right() {
		return next();
	}

	public int degree() {
		return degree;
	}

	public String toString() {
		return Integer.toString(key);
	}
	@Override
	public String string(int tabs) {
		String result;
		
		if(degree > 0) {
			result = Main.repeat("\t", tabs) + "{" + key + " (parent " + parent + ") " + ": " + System.lineSeparator() + children.string(tabs + 1) + System.lineSeparator() + Main.repeat("\t", tabs) + "}";
		} else {
			result = Main.repeat("\t", tabs) + "{" + key + " (parent " + parent + ") " + "}";
		}
		
		return result;
	}
}
