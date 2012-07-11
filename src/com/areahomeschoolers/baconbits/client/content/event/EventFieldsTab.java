package com.areahomeschoolers.baconbits.client.content.event;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.EventFormField;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.TitleBar;
import com.areahomeschoolers.baconbits.client.widgets.TitleBar.TitleBarStyle;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.EventAgeGroup;
import com.areahomeschoolers.baconbits.shared.dto.EventField;
import com.areahomeschoolers.baconbits.shared.dto.EventPageData;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EventFieldsTab extends Composite {
	private EventPageData pageData;
	private VerticalPanel vp = new VerticalPanel();
	private final EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);
	private FieldTable fieldTable = new FieldTable();
	private FieldEditDialog dialog = new FieldEditDialog(new Command() {
		@Override
		public void execute() {
			refreshTable();
		}
	});
	private DefaultListBox ageGroupListBox;

	public EventFieldsTab(EventPageData data) {
		this.pageData = data;
		vp.setSpacing(10);
		vp.setWidth("100%");

		eventService.getEventFieldTypes(new Callback<ArrayList<Data>>() {
			@Override
			protected void doOnSuccess(ArrayList<Data> result) {
				dialog.setFieldTypes(result);
			}
		});

		ageGroupListBox = new DefaultListBox();
		ageGroupListBox.addItem("", 0);
		for (EventAgeGroup group : pageData.getAgeGroups()) {
			ageGroupListBox.addItem(
					group.getMinimumAge() + "-" + group.getMaximumAge() + " yrs / " + group.getMinimumParticipants() + "-" + group.getMaximumParticipants()
							+ " participants", group.getId());
		}

		vp.add(ageGroupListBox);

		TitleBar tb = new TitleBar("Fields", TitleBarStyle.SECTION);
		tb.addLink(new ClickLabel("Add", new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				EventField e = new EventField();
				e.setEventAgeGroupId(ageGroupListBox.getIntValue());
				e.setEventId(pageData.getEvent().getId());
				dialog.center(e);
			}
		}));
		vp.add(WidgetFactory.newSection(tb, fieldTable));

		ageGroupListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				refreshTable();
			}
		});

		showEmptyTable();

		initWidget(vp);
	}

	private void refreshTable() {
		eventService.getFieldsForAgeGroup(ageGroupListBox.getIntValue(), new Callback<ArrayList<EventField>>() {
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

	private void showEmptyTable() {
		fieldTable.addField("No fields", "");
	}

}
