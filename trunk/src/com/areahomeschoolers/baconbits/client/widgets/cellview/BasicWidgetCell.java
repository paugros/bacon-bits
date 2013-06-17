package com.areahomeschoolers.baconbits.client.widgets.cellview;

import java.util.HashMap;

import com.areahomeschoolers.baconbits.shared.dto.EntityDto;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Widget;

public class BasicWidgetCell<T extends EntityDto<T>> extends WidgetCell<T> {

	public abstract class CellClickEvent extends ClickEvent {
		@Override
		public abstract T getSource();

		public void updateCell() {
			getCellUpdater().update(getSource());
		}

		public void updateCell(T item) {
			getCellUpdater().update(item);
		}

		protected abstract ValueUpdater<T> getCellUpdater();
	}

	protected HashMap<T, Widget> widgets = new HashMap<T, Widget>();

	public BasicWidgetCell(ValueGetter<Widget, T> widgetCreator) {
		super(widgetCreator);
	}

	@Override
	public void onBrowserEvent(Context context, final Element parent, final T value, NativeEvent event, final ValueUpdater<T> valueUpdater) {
		if ("click".equals(event.getType())) {
			if (widgets.containsKey(value)) {
				Widget widget = widgets.get(value);
				CellClickEvent cmde = new CellClickEvent() {
					@Override
					public ValueUpdater<T> getCellUpdater() {
						return valueUpdater;
					}

					@Override
					public T getSource() {
						return value;
					}
				};
				cmde.setNativeEvent(event);
				cmde.setRelativeElement(widget.getElement());
				widget.fireEvent(cmde);
			}
		} else if ("change".equals(event.getType())) {
			if (widgets.containsKey(value)) {
				final Widget widget = widgets.get(value);
				ChangeEvent ce = new ChangeEvent() {
					@Override
					public Object getSource() {
						return value;
					}
				};
				ce.setNativeEvent(event);
				ce.setRelativeElement(widget.getElement());
				widget.fireEvent(ce);
			}
		} else if ("mouseout".equals(event.getType())) {
			if (widgets.containsKey(value)) {
				final Widget widget = widgets.get(value);
				MouseOutEvent moe = new MouseOutEvent() {
					@Override
					public Object getSource() {
						return value;
					}
				};
				moe.setNativeEvent(event);
				moe.setRelativeElement(widget.getElement());
				widget.fireEvent(moe);
			}
		} else if ("mouseover".equals(event.getType())) {
			if (widgets.containsKey(value)) {
				final Widget widget = widgets.get(value);
				MouseOverEvent moe = new MouseOverEvent() {
					@Override
					public Object getSource() {
						return value;
					}
				};
				moe.setNativeEvent(event);
				moe.setRelativeElement(widget.getElement());
				widget.fireEvent(moe);
			}
		} else if ("dblclick".equals(event.getType())) {
			if (widgets.containsKey(value)) {
				final Widget widget = widgets.get(value);
				DoubleClickEvent dce = new DoubleClickEvent() {
					@Override
					public Object getSource() {
						return value;
					}
				};
				dce.setNativeEvent(event);
				dce.setRelativeElement(widget.getElement());
				widget.fireEvent(dce);
			}
		}
	}

	@Override
	public void render(Context context, T value, SafeHtmlBuilder sb) {
		if (value == null) {
			return;
		}
		Widget widget = widgetCreator.get(value);
		widgets.put(value, widget);
		sb.appendHtmlConstant(widget.toString());
	}
}
