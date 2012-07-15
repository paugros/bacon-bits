package com.areahomeschoolers.baconbits.client.content.event;

import java.util.HashMap;
import java.util.Map;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.EntityEditDialog;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.NumericTextBox;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.client.widgets.RequiredTextBox;
import com.areahomeschoolers.baconbits.client.widgets.TitleBar;
import com.areahomeschoolers.baconbits.client.widgets.TitleBar.TitleBarStyle;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.EventPageData;
import com.areahomeschoolers.baconbits.shared.dto.EventRegistration;
import com.areahomeschoolers.baconbits.shared.dto.EventVolunteerPosition;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class EventRegistrationDialog extends EntityEditDialog<EventRegistration> {
	private EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);
	private EventPageData pageData;

	public EventRegistrationDialog(EventPageData pd) {
		setText("Register For Event");
		pageData = pd;

		addFormSubmitHandler(new FormSubmitHandler() {
			@Override
			public void onFormSubmit(FormField formField) {
				eventService.saveRegistration(entity, new Callback<ServerResponseData<EventRegistration>>() {
					@Override
					protected void doOnSuccess(ServerResponseData<EventRegistration> result) {
						hide();
					}
				});
			}
		});
	}

	@Override
	protected Widget createContent() {
		VerticalPanel vp = new VerticalPanel();
		vp.setWidth("350px");
		vp.setSpacing(3);

		if (!Common.isNullOrEmpty(pageData.getVolunteerPositions())) {
			HorizontalPanel pp = new PaddedPanel();
			vp.add(new TitleBar("Volunteer?", TitleBarStyle.SUBSECTION));
			final DefaultListBox lb = new DefaultListBox();
			lb.addItem("", 0);
			final Map<Integer, EventVolunteerPosition> vMap = new HashMap<Integer, EventVolunteerPosition>();
			for (EventVolunteerPosition p : pageData.getVolunteerPositions()) {
				lb.addItem(p.getJobTitle() + ": " + p.getOpenPositionCount(), p.getId());
				vMap.put(p.getId(), p);
			}
			pp.add(lb);

			final NumericTextBox tb = new NumericTextBox();
			tb.setVisibleLength(1);
			tb.setMaxLength(1);
			pp.add(tb);
			Label l = new Label("people");
			l.addStyleName("smallText");
			pp.add(tb);
			pp.add(l);
			pp.setCellVerticalAlignment(l, HasVerticalAlignment.ALIGN_MIDDLE);

			final VerticalPanel positionPanel = new VerticalPanel();

			final Label description = new Label();
			description.addStyleName("mediumPadding grayText");

			Button b = new Button("Add", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					int positionId = lb.getIntValue();

					if (positionId > 0) {
						EventVolunteerPosition position = vMap.get(positionId);
						int count = tb.getInteger();

						if (count == 0) {
							count = 1;
						}

						positionPanel.add(new Label(position.getJobTitle() + ": " + count));
						lb.removeItem(lb.getSelectedIndex());

						position.setRegisterPositionCount(count);
						entity.getVolunteerPositions().add(position);
						lb.setSelectedIndex(0);
						tb.setText("");
						description.setText("");
					}
				}
			});
			pp.add(b);

			lb.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					int positionId = lb.getIntValue();

					if (positionId > 0) {
						tb.setValue(1);
						description.setText(vMap.get(positionId).getDescription());
					} else {
						tb.setText("");
						description.setText("");
					}
				}
			});

			vp.add(pp);
			vp.add(description);
			vp.add(positionPanel);
		}

		vp.add(new TitleBar("Enter Attendees", TitleBarStyle.SUBSECTION));

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
		VerticalPanel ap = new VerticalPanel();
		Label al = new Label("Age");
		al.addStyleName("smallText");

		NumericTextBox ai = new NumericTextBox();
		ai.setVisibleLength(2);
		ai.setRequired(true);
		ai.setMaxLength(2);
		ap.add(al);
		ap.add(ai);

		hp.add(ap);

		Button b = new Button("Add", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {

			}
		});

		hp.add(b);
		hp.setCellVerticalAlignment(b, HasVerticalAlignment.ALIGN_BOTTOM);

		vp.add(hp);

		form.getSubmitButton().setText("Register");
		getButtonPanel().getCloseButton().setText("Cancel");
		return vp;
	}

}
