package com.areahomeschoolers.baconbits.client.widgets;

import java.util.HashMap;
import java.util.Map;

import com.areahomeschoolers.baconbits.shared.dto.EntityDto;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class TilePanel extends Composite {
	private FlowPanel fp = new FlowPanel();
	private Map<Integer, Widget> widgetMap = new HashMap<>();

	public TilePanel() {
		initWidget(fp);
	}

	public void add(Widget tile) {
		add(tile, null);
	}

	public void add(Widget tile, Integer itemId) {
		SimplePanel sp = new SimplePanel(tile);
		sp.getElement().getStyle().setMargin(8, Unit.PX);
		sp.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

		if (itemId != null) {
			widgetMap.put(itemId, sp);
		}
		fp.add(sp);
	}

	public void clear() {
		fp.clear();
	}

	public void hide(EntityDto<?> dto) {
		if (widgetMap.get(dto.getId()) != null) {
			widgetMap.get(dto.getId()).getElement().getStyle().setDisplay(Display.NONE);
		}
	}

	public void show(EntityDto<?> dto) {
		if (widgetMap.get(dto.getId()) != null) {
			widgetMap.get(dto.getId()).getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		}
	}

	public void showAll() {
		for (Widget w : widgetMap.values()) {
			w.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		}
	}

}
