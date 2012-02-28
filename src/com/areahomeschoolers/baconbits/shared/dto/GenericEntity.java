package com.areahomeschoolers.baconbits.shared.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import com.areahomeschoolers.baconbits.shared.Common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;

public final class GenericEntity extends EntityDto<GenericEntity> {
	private static final long serialVersionUID = 1L;
	private HashMap<String, String> data = new HashMap<String, String>();
	private HashMap<String, Date> dateData = new HashMap<String, Date>();
	private String sortValue;

	public GenericEntity() {

	}

	public GenericEntity(String key, boolean value) {
		put(key, value);
	}

	public GenericEntity(String key, Date value) {
		put(key, value);
	}

	public GenericEntity(String key, double value) {
		put(key, value);
	}

	public GenericEntity(String key, int value) {
		put(key, value);
	}

	public GenericEntity(String key, long value) {
		put(key, value);
	}

	public GenericEntity(String key, String value) {
		put(key, value);
	}

	@Override
	public int compareTo(GenericEntity that) {
		if (sortValue == null) {
			return super.compareTo(that);
		}

		return sortValue.compareTo(that.getSortValue());
	}

	public GenericEntity copy() {
		GenericEntity copy = new GenericEntity();
		copy.data = new HashMap<String, String>(this.data);
		copy.dateData = new HashMap<String, Date>(this.dateData);
		copy.sortValue = (this.sortValue == null) ? null : new String(this.sortValue);

		return copy;
	}

	@Override
	public boolean equals(Object that) {
		if (sortValue == null) {
			return super.equals(that);
		}

		return sortValue.equals(((GenericEntity) that).getSortValue());
	}

	public String get(String key) {
		return data.get(key);
	}

	public boolean getBoolean(String key) {
		checkKey(key);
		String value = data.get(key);
		if (value == null) {
			return false;
		}
		if (value.equals("0")) {
			value = "false";
		} else if (value.equals("1")) {
			value = "true";
		}
		return Boolean.parseBoolean(value);
	}

	public HashMap<String, String> getData() {
		return data;
	}

	public Date getDate(String key) {
		if (dateData.containsKey(key)) {
			return dateData.get(key);
		}

		checkKey(key);
		String value = data.get(key);
		if (value == null) {
			return null;
		}

		try {
			if (GWT.isClient()) {
				return DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss.S").parse(value);
			}
			return null;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public HashMap<String, Date> getDateData() {
		return dateData;
	}

	public double getDouble(String key) {
		checkKey(key);
		String value = data.get(key);
		if (value == null) {
			return 0;
		}
		return Double.parseDouble(data.get(key));
	}

	public int getInt(String key) {
		checkKey(key);
		String value = data.get(key);
		if (value == null) {
			return 0;
		}
		return Integer.parseInt(data.get(key));
	}

	public long getLong(String key) {
		checkKey(key);
		String value = data.get(key);
		if (value == null) {
			return 0;
		}
		return Long.parseLong(data.get(key));
	}

	public String getSortValue() {
		return sortValue;
	}

	public boolean isEmpty() {
		return data.isEmpty() && dateData.isEmpty();
	}

	public String put(String key, boolean value) {
		return put(key, Boolean.toString(value));
	}

	public Date put(String key, Date value) {
		return dateData.put(key, value);
	}

	public String put(String key, double value) {
		return put(key, Double.toString(value));
	}

	public String put(String key, int value) {
		return put(key, Integer.toString(value));
	}

	public String put(String key, long value) {
		return put(key, Long.toString(value));
	}

	public String put(String key, String value) {
		if (key.equals("ID") && Common.isInteger(value)) {
			setId(Integer.parseInt(value));
		}
		return data.put(key, value);
	}

	public void putAll(GenericEntity genericEntity) {
		data.putAll(genericEntity.getData());
		dateData.putAll(genericEntity.getDateData());
	}

	public void remove(String key) {
		data.remove(key);
		dateData.remove(key);
	}

	public void setSortValue(String value) {
		sortValue = value;
	}

	@Override
	public String toString() {
		return data.toString() + dateData.toString();
	}

	public String toStringVertical() {
		String ret = "{";

		for (Entry<String, String> entry : data.entrySet()) {
			ret += "\n" + entry.getKey() + ", " + entry.getValue();
		}

		for (Entry<String, Date> entry : dateData.entrySet()) {
			ret += "\n" + entry.getKey() + ", " + entry.getValue();
		}
		ret += "}\n\n";

		return ret;
	}

	private void checkKey(String key) {
		if (!data.containsKey(key)) {
			new RuntimeException("GenericEntity does not contain key: '" + key + "'!");
		}
	}
}
