package com.areahomeschoolers.baconbits.client.content.event;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.event.ConfirmHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.ConfirmDialog;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.EventFormField;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.LinkPanel;
import com.areahomeschoolers.baconbits.client.widgets.TitleBar;
import com.areahomeschoolers.baconbits.client.widgets.TitleBar.TitleBarStyle;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.EventAgeGroup;
import com.areahomeschoolers.baconbits.shared.dto.EventField;
import com.areahomeschoolers.baconbits.shared.dto.EventPageData;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

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
		for (EventAgeGroup g : pageData.getAgeGroups()) {
			ageGroupListBox.addItem(
					Formatter.formatNumberRange(g.getMinimumAge(), g.getMaximumAge()) + " yrs / "
							+ Formatter.formatNumberRange(g.getMinimumParticipants(), g.getMaximumParticipants()) + " participants", g.getId());
		}

		if (!Common.isNullOrEmpty(pageData.getAgeGroups())) {
			vp.add(ageGroupListBox);
		}

		TitleBar tb = new TitleBar("Fields", TitleBarStyle.SECTION);
		tb.addLink(new ClickLabel("Add", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				EventField e = new EventField();
				e.setEventAgeGroupId(ageGroupListBox.getIntValue());
				e.setEventId(pageData.getEvent().getId());
				dialog.center(e);
			}
		}));
		vp.add(WidgetFactory.newSection(tb, fieldTable, ContentWidth.MAXWIDTH750PX));

		ageGroupListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				refreshTable();
			}
		});

		refreshTable();

		initWidget(vp);
	}

	private void refreshTable() {
		ArgMap<EventArg> args = new ArgMap<EventArg>();
		int ageGroupId = ageGroupListBox.getIntValue();
		if (ageGroupId == 0) {
			args.put(EventArg.EVENT_ID, pageData.getEvent().getId());
		} else {
			args.put(EventArg.AGE_GROUP_ID, ageGroupId);
		}

		eventService.getFields(args, new Callback<ArrayList<EventField>>() {
			@Override
			protected void doOnSuccess(ArrayList<EventField> result) {
				fieldTable.removeAllRows();

				for (final EventField f : result) {
					EventFormField ff = new EventFormField(f);
					Widget previewWidget = ff.getFormField().getInputWidget();
					HorizontalPanel hp = new HorizontalPanel();
					hp.setWidth("400px");
					hp.add(previewWidget);
					LinkPanel lp = new LinkPanel();
					lp.add(new ClickLabel("Edit", new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							dialog.center(f);
						}
					}));
					lp.add(new ClickLabel("X", new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							ConfirmDialog.confirm("Delete " + f.getName() + "? Any filled-out values for this field will also be deleted.",
									new ConfirmHandler() {
										@Override
										public void onConfirm() {
											eventService.deleteEventField(f.getId(), new Callback<Void>() {
												@Override
												protected void doOnSuccess(Void result) {
													refreshTable();
												}
											});
										}
									});
						}
					}));
					hp.add(lp);
					hp.setCellHorizontalAlignment(lp, HasHorizontalAlignment.ALIGN_RIGHT);
					String label = ff.getFormField().getLabelText();
					if (f.getRequired()) {
						label = "*" + label;
					}
					fieldTable.addField(label, hp);
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
