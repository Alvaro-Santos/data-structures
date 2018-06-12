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
package dependencies.java_utilities.tuples;

public interface Tuple {
	public static <A> Single<A> of(final A first) {
		return new SingleClass<>(first);
	}

	public static <A, B> Pair<A, B> of(final A first, final B second) {
		return new PairClass<>(first, second);
	}

	public static <A, B, C> Triple<A, B, C> of(final A first, final B second, final C third) {
		return new TripleClass<>(first, second, third);
	}

	public static <A, B, C, D> Quadruple<A, B, C, D> of(final A first, final B second, final C third, final D fourth) {
		return new QuadrupleClass<>(first, second, third, fourth);
	}
}
