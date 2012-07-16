package com.areahomeschoolers.baconbits.client.content.event;

import java.util.HashMap;
import java.util.Map;

import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.NumericTextBox;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.client.widgets.TitleBar;
import com.areahomeschoolers.baconbits.client.widgets.TitleBar.TitleBarStyle;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.EventPageData;
import com.areahomeschoolers.baconbits.shared.dto.EventRegistration;
import com.areahomeschoolers.baconbits.shared.dto.EventRegistrationParticipant;
import com.areahomeschoolers.baconbits.shared.dto.EventVolunteerPosition;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EventRegistrationSection extends Composite {
	private VerticalPanel vp = new VerticalPanel();
	private EventPageData pageData;
	// private final EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);
	private EventRegistration registration = new EventRegistration();

	public EventRegistrationSection(EventPageData pd) {
		initWidget(vp);
		vp.setWidth("700px");
		pageData = pd;

		vp.setSpacing(3);

		addParticipantSection();

		if (!Common.isNullOrEmpty(pageData.getVolunteerPositions())) {
			addVolunteerSection();
		}

		// form.getSubmitButton().setText("Register");
		// getButtonPanel().getCloseButton().setText("Cancel");

	}

	private void addParticipantSection() {
		TitleBar tb = new TitleBar("Enter Attendees", TitleBarStyle.SUBSECTION);
		tb.addLink(new ClickLabel("Add", new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				EventRegistrationParticipant rp = new EventRegistrationParticipant();
				rp.setEventRegistrationId(pageData.getRegistration().getId());
				new ParticipantEditDialog(pageData).center(rp);
			}
		}));
		vp.add(tb);

		VerticalPanel pp = new VerticalPanel();
		for (final EventRegistrationParticipant p : pageData.getRegistration().getParticipants()) {
			String text = p.getFirstName() + " " + p.getLastName();
			pp.add(new ClickLabel(text, new MouseDownHandler() {
				@Override
				public void onMouseDown(MouseDownEvent event) {
					new ParticipantEditDialog(pageData).center(p);
				}
			}));
		}

		if (pp.getWidgetCount() == 0) {
			pp.add(new Label("You haven't registered anyone yet."));
		}

		vp.add(pp);
	}

	private void addVolunteerSection() {
		HorizontalPanel pp = new PaddedPanel();
		vp.add(new TitleBar("Volunteer?", TitleBarStyle.SUBSECTION));
		final DefaultListBox lb = new DefaultListBox();
		lb.addStyleName("RequiredListBox");
		lb.addItem("", 0);
		final Map<Integer, EventVolunteerPosition> vMap = new HashMap<Integer, EventVolunteerPosition>();
		for (EventVolunteerPosition p : pageData.getVolunteerPositions()) {
			lb.addItem(p.getJobTitle() + ": " + p.getOpenPositionCount() + " needed", p.getId());
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
					registration.getVolunteerPositions().add(position);
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
}
