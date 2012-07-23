package com.areahomeschoolers.baconbits.client.content.event;

import java.util.HashMap;
import java.util.Map;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.event.ConfirmHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.ConfirmDialog;
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
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EventRegistrationSection extends Composite {
	private VerticalPanel vp = new VerticalPanel();
	private EventPageData pageData;
	private final EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);
	private EventRegistration registration;
	private FlexTable positionTable;
	private FlexTable participantTable;
	private Command refreshParticipants = new Command() {
		@Override
		public void execute() {
			populateParticipants();
		}
	};

	public EventRegistrationSection(EventPageData pd) {
		initWidget(vp);
		vp.setWidth("700px");
		pageData = pd;
		registration = pageData.getRegistration();

		vp.setSpacing(3);

		loadSection();

	}

	private void addParticipantSection() {
		TitleBar tb = new TitleBar("Participants", TitleBarStyle.SUBSECTION);

		if (pageData.getEvent().allowRegistrations()) {
			final ClickLabel cancel = new ClickLabel(getCancelRegistrationLabelText());
			cancel.addMouseDownHandler(new MouseDownHandler() {
				@Override
				public void onMouseDown(MouseDownEvent event) {
					String text = "";
					if (registration.getCanceled()) {
						text = "Restore registration?";
					} else {
						text = "Really cancel your registration for this event?";
					}
					ConfirmDialog.confirm(text, new ConfirmHandler() {
						@Override
						public void onConfirm() {
							registration.setCanceled(!registration.getCanceled());

							eventService.saveRegistration(registration, new Callback<ServerResponseData<EventRegistration>>() {
								@Override
								protected void doOnSuccess(ServerResponseData<EventRegistration> result) {
									if (registration.getCanceled()) {
										registration.getVolunteerPositions().clear();
									}
									loadSection();
								}
							});
						}
					});
				}
			});
			tb.addLink(cancel);

			tb.addLink(new ClickLabel("Add", new MouseDownHandler() {
				@Override
				public void onMouseDown(MouseDownEvent event) {
					EventRegistrationParticipant rp = new EventRegistrationParticipant();
					rp.setEventRegistrationId(registration.getId());
					new ParticipantEditDialog(pageData, refreshParticipants).center(rp);
				}
			}));
		}

		vp.add(tb);

		participantTable = new FlexTable();
		participantTable.setWidth("300px");
		populateParticipants();
		vp.add(participantTable);
	}

	private void addVolunteerSection() {
		if (registration.getCanceled() || Common.isNullOrEmpty(pageData.getVolunteerPositions())
				|| (!pageData.getEvent().allowRegistrations() && Common.isNullOrEmpty(registration.getVolunteerPositions()))) {
			return;
		}

		vp.add(new TitleBar("Volunteer?", TitleBarStyle.SUBSECTION));
		if (pageData.getEvent().allowRegistrations()) {
			HorizontalPanel pp = new PaddedPanel();
			final DefaultListBox lb = new DefaultListBox();
			lb.addStyleName("RequiredListBox");
			lb.addItem("", 0);
			final Map<Integer, EventVolunteerPosition> vMap = new HashMap<Integer, EventVolunteerPosition>();
			for (EventVolunteerPosition p : pageData.getVolunteerPositions()) {
				if (!registration.getVolunteerPositions().contains(p)) {
					lb.addItem(p.getJobTitle() + ": " + p.getOpenPositionCount() + " needed", p.getId());
					vMap.put(p.getId(), p);
				}
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

			final Label description = new Label();
			description.addStyleName("mediumPadding grayText");

			Button b = new Button("Add", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					int positionId = lb.getIntValue();

					if (positionId > 0) {
						final EventVolunteerPosition position = vMap.get(positionId);
						int count = tb.getInteger();

						if (count == 0) {
							count = 1;
						}
						position.setRegisterPositionCount(count);

						if (!registration.isSaved()) {
							registration.setEventId(pageData.getEvent().getId());

							eventService.saveRegistration(registration, new Callback<ServerResponseData<EventRegistration>>() {
								@Override
								protected void doOnSuccess(ServerResponseData<EventRegistration> result) {
									registration = result.getData();
									saveVolunteerPosition(position);
								}
							});
						} else {
							saveVolunteerPosition(position);
						}

						lb.removeItem(lb.getSelectedIndex());
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
		}

		positionTable = new FlexTable();
		positionTable.setWidth("300px");

		populateVolunteerPositions();

		vp.add(positionTable);
	}

	private String getCancelRegistrationLabelText() {
		return registration.getCanceled() ? "Restore" : "Cancel";
	}

	private void loadSection() {
		vp.clear();

		addParticipantSection();

		addVolunteerSection();
	}

	private void populateParticipants() {
		participantTable.removeAllRows();
		double totalPrice = 0;
		for (int i = 0; i < registration.getParticipants().size(); i++) {
			final EventRegistrationParticipant p = registration.getParticipants().get(i);
			String text = p.getFirstName() + " " + p.getLastName();
			ClickLabel editLabel = new ClickLabel(text, new MouseDownHandler() {
				@Override
				public void onMouseDown(MouseDownEvent event) {
					new ParticipantEditDialog(pageData, refreshParticipants).center(p);
				}
			});

			String editText = "";
			if (p.getCanceled() || registration.getCanceled()) {
				editLabel.addStyleName("strikeText");
				editText = "Restore";
			} else {
				editText = "Remove";
			}
			participantTable.setWidget(i, 0, editLabel);

			participantTable.setText(i, 1, Formatter.formatCurrency(p.getPrice()));
			if (p.getCanceled() || registration.getCanceled()) {
				participantTable.getCellFormatter().addStyleName(i, 1, "strikeText");
			} else {
				totalPrice += p.getPrice();
			}

			if (!registration.getCanceled() && pageData.getEvent().allowRegistrations()) {
				ClickLabel cl = new ClickLabel(editText, new MouseDownHandler() {
					@Override
					public void onMouseDown(MouseDownEvent event) {
						String confirmText = "";
						if (p.getCanceled()) {
							confirmText = "Restore registration for " + p.getFirstName() + "?";
						} else {
							confirmText = "Really remove " + p.getFirstName() + " from the attendee list?";
						}
						ConfirmDialog.confirm(confirmText, new ConfirmHandler() {
							@Override
							public void onConfirm() {
								p.setCanceled(!p.getCanceled());

								eventService.saveParticipant(p, new Callback<EventRegistrationParticipant>() {
									@Override
									protected void doOnSuccess(EventRegistrationParticipant result) {
										populateParticipants();
									}
								});
							}
						});
					}
				});

				participantTable.setWidget(i, 2, cl);
				participantTable.getCellFormatter().setHorizontalAlignment(i, 2, HasHorizontalAlignment.ALIGN_RIGHT);
			}
		}

		if (registration.getParticipants().isEmpty()) {
			participantTable.setWidget(0, 0, new Label("You haven't registered anyone yet."));
		} else {
			int row = participantTable.getRowCount();
			participantTable.setText(row, 1, Formatter.formatCurrency(totalPrice));
			participantTable.getCellFormatter().addStyleName(row, 1, "totalCell");
		}
	}

	private void populateVolunteerPositions() {
		positionTable.removeAllRows();
		for (int i = 0; i < registration.getVolunteerPositions().size(); i++) {
			final EventVolunteerPosition p = registration.getVolunteerPositions().get(i);
			positionTable.setWidget(i, 0, new Label(p.getJobTitle() + ": " + p.getRegisterPositionCount()));

			if (pageData.getEvent().allowRegistrations()) {
				ClickLabel cl = new ClickLabel("X", new MouseDownHandler() {
					@Override
					public void onMouseDown(MouseDownEvent event) {
						ConfirmDialog.confirm("Don't want to volunteer for this role?", new ConfirmHandler() {
							@Override
							public void onConfirm() {
								eventService.deleteVolunteerPositionMapping(p, new Callback<Void>() {
									@Override
									protected void doOnSuccess(Void result) {
										registration.getVolunteerPositions().remove(p);
										populateVolunteerPositions();
									}
								});
							}
						});
					}
				});

				positionTable.setWidget(i, 1, cl);
				positionTable.getCellFormatter().setHorizontalAlignment(i, 1, HasHorizontalAlignment.ALIGN_RIGHT);
			}
		}
	}

	private void saveVolunteerPosition(EventVolunteerPosition position) {
		position.setEventRegistrationId(registration.getId());

		eventService.saveVolunteerPosition(position, new Callback<EventVolunteerPosition>() {
			@Override
			protected void doOnSuccess(EventVolunteerPosition result) {
				registration.getVolunteerPositions().add(result);
				populateVolunteerPositions();
			}
		});
	}
}
