package com.areahomeschoolers.baconbits.shared.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class GenList implements Serializable {
	private static final long serialVersionUID = 1L;
	private HashMap<Integer, String> colNames;
	private ArrayList<ArrayList<String>> rowData; // BRET make theses java arrays not ArrayLists for efficiency

	public ArrayList<String> getColumns() {
		// Returns the column list in the correct order
		return new ArrayList<String>(new TreeMap<Integer, String>(colNames).values());
	}

	public ArrayList<Data> getListData() {
		ArrayList<Data> list = new ArrayList<Data>();

		for (ArrayList<String> listRow : rowData) {
			Data genRow = new Data();

			for (int offset = 1; offset <= listRow.size(); offset++) {
				genRow.put(colNames.get(offset), listRow.get(offset - 1));
			}

			list.add(genRow);
		}

		return list;
	}

	public ArrayList<ArrayList<String>> getRawData() {
		return rowData;
	}

	public void setColumnOffsets(HashMap<Integer, String> offsets) {
		colNames = offsets;
	}

	public void setData(ArrayList<ArrayList<String>> data) {
		this.rowData = data;
	}
}
