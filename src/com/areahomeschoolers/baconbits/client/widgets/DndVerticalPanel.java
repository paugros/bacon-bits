package com.areahomeschoolers.baconbits.client.widgets;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.VerticalPanelDropController;
import com.areahomeschoolers.baconbits.client.event.ParameterHandler;
import com.areahomeschoolers.baconbits.shared.BidiMap;
import com.areahomeschoolers.baconbits.shared.dto.HasOrdinal;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class DndVerticalPanel<T extends HasOrdinal> extends Composite {
	private VerticalPanel vp = new VerticalPanel();
	private VerticalPanelDropController dropController = new VerticalPanelDropController(vp);
	private AbsolutePanel ap = new AbsolutePanel();
	private PickupDragController dragController;
	private ParameterHandler<List<T>> saveHandler;
	private int preDropIndex;
	private BidiMap<Widget, T> hash = new BidiMap<Widget, T>();

	public DndVerticalPanel(ParameterHandler<List<T>> handler) {
		ap.add(vp);
		ap.setHeight("100%");
		initWidget(ap);

		dragController = new PickupDragController(ap, false);

		addStyleName("DndVerticalPanel");
		setSaveHandler(handler);

		dragController.setBehaviorConstrainedToBoundaryPanel(false);
		dragController.addDragHandler(new DragHandler() {
			@Override
			public void onDragEnd(DragEndEvent event) {
				if (saveHandler != null && vp.getWidgetIndex((Widget) event.getSource()) != preDropIndex) {
					List<T> orderedItems = new ArrayList<T>();
					for (int i = 0; i < vp.getWidgetCount(); i++) {
						T item = hash.get(vp.getWidget(i));
						if (item.getOrdinal() != i) {
							item.setOrdinal(i);
							orderedItems.add(item);
						}
					}
					saveHandler.execute(orderedItems);
				}
			}

			@Override
			public void onDragStart(DragStartEvent event) {
				preDropIndex = vp.getWidgetIndex((Widget) event.getSource());
			}

			@Override
			public void onPreviewDragEnd(DragEndEvent event) throws VetoDragException {
			}

			@Override
			public void onPreviewDragStart(DragStartEvent event) throws VetoDragException {
			}
		});

		dragController.registerDropController(dropController);
	}

	public void add(Widget widget, Widget handle, T item) {
		int index = vp.getWidgetCount();
		Widget existing = hash.reverseGet(item);
		if (existing != null) {
			index = vp.getWidgetIndex(existing);
			hash.remove(existing);
			vp.remove(existing);
		}

		dragController.makeDraggable(widget, handle);
		vp.insert(widget, index);
		hash.put(widget, item);
	}

	public int getWidgetCount() {
		return vp.getWidgetCount();
	}

	public int getWidgetIndex(Widget w) {
		return vp.getWidgetIndex(w);
	}

	public void insert(Widget w, int beforeIndex) {
		vp.insert(w, beforeIndex);
	}

	public void remove(int index) {
		hash.remove(vp.getWidget(index));
		vp.remove(index);
	}

	public void remove(Widget w) {
		hash.remove(w);
		vp.remove(w);
	}

	public void setSaveHandler(ParameterHandler<List<T>> saveHandler) {
		this.saveHandler = saveHandler;
	}

	// public void add(Widget widget, T item) {
	// Grid wrap = new Grid(1, 2);
	// wrap.addStyleName("item");
	// wrap.getCellFormatter().addStyleName(0, 0, "verticalDragHandle");
	// wrap.getCellFormatter().addStyleName(0, 1, "content");
	// FocusPanel dragHandle = new FocusPanel();
	// dragHandle.setWidth("100%");
	// dragHandle.setHeight("100%");
	// ImageResource dragImageResource = MainImageBundle.INSTANCE.verticalDragHandle();
	// dragHandle.setWidget(new Image(dragImageResource));
	// wrap.setWidget(0, 0, dragHandle);
	// wrap.setWidget(0, 1, widget);
	// dragController.makeDraggable(wrap, dragHandle);
	//
	// vp.add(wrap);
	// hash.put(wrap, item);
	// }

	public void setSpacing(int spacing) {
		vp.setSpacing(spacing);
	}

}
