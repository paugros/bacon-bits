package com.areahomeschoolers.baconbits.client.widgets.cellview;

import java.util.HashMap;

import com.areahomeschoolers.baconbits.shared.dto.EntityDto;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Widget;

public abstract class WidgetCellCreator<T extends EntityDto<T>> implements ValueGetter<Widget, T> {

	protected HashMap<T, Widget> widgets = new HashMap<T, Widget>();

	public void flushCachce() {
		widgets.clear();
	}

	@Override
	public Widget get(T item) {
		if (widgets.containsKey(item)) {
			return widgets.get(item);
		}
		Widget widget = createWidget(item);
		widgets.put(item, widget);
		return widget;
	}

	public Column<T, T> getColumn(final EntityCellTable<T, ?, ?> table) {
		final Column<T, T> col = new Column<T, T>(createWidgetCell()) {
			@Override
			public T getValue(T item) {
				return item;
			}
		};

		col.setSortable(true);

		col.setFieldUpdater(new FieldUpdater<T, T>() {
			@Override
			public void update(int index, T object, T value) {
				table.getRowElement(index - table.getVisibleRange().getStart()).getCells().getItem(table.getColumnIndex(col))
						.setInnerHTML(get(value).toString());
			}
		});

		return col;
	}

	protected abstract Widget createWidget(T item);

	protected WidgetCell<T> createWidgetCell() {
		return new BasicWidgetCell<T>(this);
	}
}
