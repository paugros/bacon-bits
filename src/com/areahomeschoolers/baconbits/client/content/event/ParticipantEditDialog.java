package com.areahomeschoolers.baconbits.client.content.event;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.widgets.EntityEditDialog;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.MaxHeightScrollPanel;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.client.widgets.RequiredListBox;
import com.areahomeschoolers.baconbits.client.widgets.RequiredTextBox;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.EventAgeGroup;
import com.areahomeschoolers.baconbits.shared.dto.EventField;
import com.areahomeschoolers.baconbits.shared.dto.EventPageData;
import com.areahomeschoolers.baconbits.shared.dto.EventRegistrationParticipant;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ParticipantEditDialog extends EntityEditDialog<EventRegistrationParticipant> {
	private EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);
	private EventPageData pageData;
	private int dialogWidth = 600;

	public ParticipantEditDialog(EventPageData pd) {
		setText("Register Attendee");
		pageData = pd;

		addFormSubmitHandler(new FormSubmitHandler() {
			@Override
			public void onFormSubmit(FormField formField) {
			}
		});
	}

	@Override
	protected Widget createContent() {
		VerticalPanel vp = new VerticalPanel();
		vp.setWidth(dialogWidth + "px");
		HorizontalPanel hp = new PaddedPanel();

		// first name
		VerticalPanel fp = new VerticalPanel();
		Label fl = new Label("First name");
		fl.addStyleName("smallText");

		RequiredTextBox fi = new RequiredTextBox();
		fi.setMaxLength(50);
		fp.add(fl);
		fp.add(fi);

		hp.add(fp);

		// last name
		VerticalPanel lp = new VerticalPanel();
		Label ll = new Label("Last name");
		ll.addStyleName("smallText");

		RequiredTextBox li = new RequiredTextBox();
		li.setMaxLength(50);
		lp.add(ll);
		lp.add(li);

		hp.add(lp);

		// age
		final MaxHeightScrollPanel fieldsPanel = new MaxHeightScrollPanel(200);
		VerticalPanel ap = new VerticalPanel();
		Label al = new Label("Age group");
		al.addStyleName("smallText");

		final RequiredListBox ai = new RequiredListBox();
		for (EventAgeGroup a : pageData.getAgeGroups()) {
			ai.addItem(a.getMinimumAge() + ((a.getMaximumAge() == 0) ? "+" : "-" + a.getMaximumAge()) + " yrs", a.getId());
		}
		ap.add(al);
		ap.add(ai);

		ai.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				eventService.getFields(new ArgMap<EventArg>(EventArg.AGE_GROUP_ID, ai.getIntValue()), new Callback<ArrayList<EventField>>() {
					@Override
					protected void doOnSuccess(ArrayList<EventField> result) {
						EventFieldTable ft = new EventFieldTable(result);
						ft.setWidth(dialogWidth + "px");
						fieldsPanel.setWidget(ft);
						centerDeferred();
					}
				});
			}
		});

		hp.add(ap);

		vp.add(hp);
		vp.add(fieldsPanel);

		return vp;
	}

}
