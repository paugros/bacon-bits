package com.areahomeschoolers.baconbits.client.widgets.cellview;

import com.areahomeschoolers.baconbits.shared.dto.Arg;
import com.areahomeschoolers.baconbits.shared.dto.EntityDto;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Header;

public class CheckboxHeader<T extends EntityDto<T>, U extends Arg> extends Header<Boolean> {

	boolean value = false;
	boolean valueSet = false;
	EntityCellTable<T, ?, ?> table;

	public CheckboxHeader(CheckboxCell cbCell, EntityCellTable<T, U, ?> table) {
		super(cbCell);
		this.table = table;
	}

	@Override
	public Boolean getValue() {
		return value;
	}

	@Override
	public void onBrowserEvent(Context context, Element elem, NativeEvent event) {
		if (!valueSet) {
			value = !value;
			CheckboxCell cell = (CheckboxCell) getCell();
			for (T item : table.getList()) {
				table.setItemSelected(item, cell.isEnabled(item) && value);
			}
		}
	}

	@Override
	public void render(Context context, SafeHtmlBuilder sb) {
		super.render(context, sb);
	}

	public void setValue(boolean value) {
		this.value = value;
		valueSet = true;
		onBrowserEvent(new Context(0, 0, null), null, null);
		valueSet = false;
	}
}
