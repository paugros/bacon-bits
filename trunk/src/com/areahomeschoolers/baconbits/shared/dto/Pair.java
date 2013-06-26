package com.areahomeschoolers.baconbits.shared.dto;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Pair<A, B> implements IsSerializable, Serializable {
	private static final long serialVersionUID = 1L;
	private A left;
	private B right;

	public Pair() {
	}

	public Pair(A l, B r) {
		left = l;
		right = r;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Pair<?, ?>)) {
			return false;
		}
		Pair<?, ?> other = (Pair<?, ?>) o;

		boolean ret = left.equals(other.left) && right.equals(other.right);
		if (ret == true) {
			return true;
		}
		return ret;
	}

	public A getLeft() {
		return left;
	}

	public B getRight() {
		return right;
	}

	@Override
	public int hashCode() {
		return left.hashCode() * 13 + right.hashCode() * 7;
	}

	public void setLeft(A left) {
		this.left = left;
	}

	public void setRight(B right) {
		this.right = right;
	}

	@Override
	public String toString() {
		return left + ", " + right;
	}
}
