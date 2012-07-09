package com.areahomeschoolers.baconbits.client.content.event;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.EventFormField;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.shared.dto.EventAgeGroup;
import com.areahomeschoolers.baconbits.shared.dto.EventField;
import com.areahomeschoolers.baconbits.shared.dto.EventPageData;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EventFieldsTab extends Composite {
	private EventPageData pageData;
	private VerticalPanel vp = new VerticalPanel();
	private final EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);
	private FieldTable fieldTable = new FieldTable();

	public EventFieldsTab(EventPageData data) {
		this.pageData = data;
		vp.setSpacing(10);
		vp.setWidth("100%");

		final DefaultListBox lb = new DefaultListBox();
		lb.addItem("", 0);
		for (EventAgeGroup group : pageData.getAgeGroups()) {
			lb.addItem(group.getMinimumAge() + "-" + group.getMaximumAge() + " yrs / " + group.getMinimumParticipants() + "-" + group.getMaximumParticipants()
					+ " participants", group.getId());
		}

		vp.add(lb);

		vp.add(WidgetFactory.newSection("Fields", fieldTable));

		lb.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				eventService.getFieldsForAgeGroup(lb.getIntValue(), new Callback<ArrayList<EventField>>() {
					@Override
					protected void doOnSuccess(ArrayList<EventField> result) {
						fieldTable.removeAllRows();

						for (EventField f : result) {
							EventFormField ff = new EventFormField(f);

							fieldTable.addField(ff.getFormField());
						}

						if (result.isEmpty()) {
							showEmptyTable();
						}
					}
				});
			}
		});

		showEmptyTable();

		initWidget(vp);
	}

	private void showEmptyTable() {
		fieldTable.addField("No fields", "");
	}

}
