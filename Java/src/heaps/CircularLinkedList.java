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

import java.util.Iterator;

public class CircularLinkedList<T extends CircularLinkedListNode<T>> implements Iterable<T> {
	T node = null;
	int size = 0;
	
	public void append(CircularLinkedList<T> root_list) {
		if(root_list.node == null) {
			return;
		}
		
		if(node == null) {
			node = root_list.node;
			size = root_list.size;
		} else {
			T a = root_list.node;
			T b = root_list.node.prev();

			node.prev().next(a);
			a.prev(node.prev());

			b.next(node);
			node.prev(b);
			
			size += root_list.size;
		}
	}

	public void add(T child) {
		if(node == null) {
			
			node = child;
			node.prev(node);
			node.next(node);
		} else {			
			T p = node.prev();

			p.next(child);
			child.prev(p);

			node.prev(child);
			child.next(node);
		}
		
		++size;
	}

	public void remove(T min) {
		T prev = min.prev();
		T next = min.next();
		
		if(min == next) {	//1 node
			node = null;
		} else {	//>= 2 nodes
			node = next;
			prev.next(next);
			next.prev(prev);
		}
		
		--size;
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			int steps = size;
			T current = node;
			
			@Override
			public boolean hasNext() {
				return steps != 0;
			}

			@Override
			public T next() {
				T r = current;
				
				current = current.next();
				--steps;

				return r;
			}
			
		};
	}

	public String string(int i) {
		StringBuilder sb = new StringBuilder();
		
		Iterator<T> it = this.iterator();
		while(it.hasNext()) {
			T elem = it.next();
			
			sb.append(elem.string(i));
			
			if(it.hasNext()) {
				sb.append("," + System.lineSeparator());
			}
		}

		return sb.toString();
	}

	public int size() {
		return size;
	}

}
