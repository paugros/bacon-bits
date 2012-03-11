package com.areahomeschoolers.baconbits.shared.dto;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Pair<A, B> implements IsSerializable {
	private A a;
	private B b;

	public Pair() {
	}

	public Pair(A l, B r) {
		a = l;
		b = r;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Pair<?, ?>)) {
			return false;
		}
		Pair<?, ?> other = (Pair<?, ?>) o;

		boolean ret = a.equals(other.a) && b.equals(other.b);
		if (ret == true) {
			return true;
		}
		return ret;
	}

	public A getA() {
		return a;
	}

	public B getB() {
		return b;
	}

	@Override
	public int hashCode() {
		return a.hashCode() * 13 + b.hashCode() * 7;
	}

	public void setA(A left) {
		this.a = left;
	}

	public void setB(B right) {
		this.b = right;
	}

	@Override
	public String toString() {
		return a + ", " + b;
	}
}
