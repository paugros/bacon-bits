package com.areahomeschoolers.baconbits.shared.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.areahomeschoolers.baconbits.shared.Common;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ArgMap<A extends Arg> implements IsSerializable {
	public enum Status implements IsSerializable {
		ACTIVE, ALL, INACTIVE
	}

	private Status status = Status.ALL;
	private HashMap<Arg, String> params = new HashMap<Arg, String>();
	private HashMap<Arg, Date> dateParams = new HashMap<Arg, Date>();

	public ArgMap() {
	}

	public ArgMap(A arg) {
		this(arg, true);
	}

	public ArgMap(A arg, boolean b) {
		if (b) {
			params.put(arg, "1");
		} else {
			params.remove(arg);
		}
	}

	public ArgMap(A arg, int i) {
		params.put(arg, String.valueOf(i));
	}

	public <T> ArgMap(A arg, List<T> list) {
		put(arg, list);
	}

	public ArgMap(A arg, String s) {
		put(arg, s);
	}

	public <T> ArgMap(A arg, T[] array) {
		put(arg, array);
	}

	public ArgMap(Status status) {
		this.status = status;
	}

	public ArgMap<A> copy() {
		ArgMap<A> copy = new ArgMap<A>();

		copy.params = new HashMap<Arg, String>(params);
		copy.dateParams = new HashMap<Arg, Date>(dateParams);

		copy.setStatus(status);

		return copy;
	}

	public boolean getBoolean(A arg) {
		return params.containsKey(arg);
	}

	public Date getDate(A arg) {
		if (!dateParams.containsKey(arg)) {
			return null;
		}
		return dateParams.get(arg);
	}

	public int getInt(A arg) {
		if (!params.containsKey(arg)) {
			return 0;
		}
		return Integer.parseInt(params.get(arg));
	}

	public ArrayList<Integer> getIntList(A arg) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		String csv = getString(arg);

		if (csv.length() > 0) {
			String[] ints = csv.split(",( +|)");
			for (String s : ints) {
				list.add(Integer.parseInt(s));
			}
		}
		return list;
	}

	public long getLong(A arg) {
		if (!params.containsKey(arg)) {
			return 0;
		}
		return Long.parseLong(params.get(arg));
	}

	public Status getStatus() {
		return status;
	}

	public String getString(A arg) {
		String ret = params.get(arg);

		if (ret != null) {
			return ret;
		}

		return "";
	}

	public ArrayList<String> getStringList(A arg) {
		ArrayList<String> list = new ArrayList<String>();
		String csv = getString(arg);

		String[] ints = csv.split(",");
		for (String s : ints) {
			list.add(s);
		}

		return list;
	}

	public ArgMap<A> put(A arg) {
		params.put(arg, "1");
		return this;
	}

	public ArgMap<A> put(A arg, boolean b) {
		if (b) {
			put(arg);
		} else {
			params.remove(arg);
		}
		return this;
	}

	public ArgMap<A> put(A arg, Date date) {
		dateParams.put(arg, date);
		return this;
	}

	public ArgMap<A> put(A arg, int i) {
		params.put(arg, String.valueOf(i));
		return this;
	}

	public <T> ArgMap<A> put(A arg, List<T> list) {
		if (list != null && list.size() > 0) {
			params.put(arg, Common.join(list, ","));
		}
		return this;
	}

	public ArgMap<A> put(A arg, long i) {
		params.put(arg, String.valueOf(i));
		return this;
	}

	public ArgMap<A> put(A arg, String s) {
		if (s != null) {
			params.put(arg, s);
		}
		return this;
	}

	public <T> ArgMap<A> put(A arg, T[] array) {
		if (array != null && array.length > 0) {
			params.put(arg, Common.join(array, ", "));
		}
		return this;
	}

	public String remove(Arg key) {
		dateParams.remove(key);
		return params.remove(key);
	}

	public ArgMap<A> setStatus(Status status) {
		this.status = status;
		return this;
	}

	@Override
	public String toString() {
		ArrayList<String> parts = new ArrayList<String>();
		for (Arg a : params.keySet()) {
			parts.add(a.toString() + "(" + params.get(a) + ")");
		}
		for (Arg a : dateParams.keySet()) {
			parts.add(a.toString() + "(" + dateParams.get(a) + ")");
		}
		return "[" + Common.join(parts, ", ") + "]";
	}
}
