package com.areahomeschoolers.baconbits.client.widgets.cellview;

import com.areahomeschoolers.baconbits.shared.dto.EntityDto;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Widget;

public abstract class WidgetCell<T extends EntityDto<T>> extends AbstractCell<T> {

	protected ValueGetter<Widget, T> widgetCreator;

	public WidgetCell(ValueGetter<Widget, T> widgetCreator) {
		super("click", "change", "mouseout", "mouseover", "dblclick");
		this.widgetCreator = widgetCreator;
	}

	@Override
	public abstract void onBrowserEvent(Context context, Element parent, T value, NativeEvent event, ValueUpdater<T> valueUpdater);

	@Override
	public abstract void render(Context context, T value, SafeHtmlBuilder sb);
}
