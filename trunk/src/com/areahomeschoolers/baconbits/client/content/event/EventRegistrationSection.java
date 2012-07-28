package com.areahomeschoolers.baconbits.client.content.event;

import java.util.HashMap;
import java.util.Map;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.event.ConfirmHandler;
import com.areahomeschoolers.baconbits.client.event.ParameterHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.rpc.service.LoginService;
import com.areahomeschoolers.baconbits.client.rpc.service.LoginServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.ConfirmDialog;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.LoginDialog;
import com.areahomeschoolers.baconbits.client.widgets.LoginDialog.LoginHandler;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.client.widgets.TitleBar;
import com.areahomeschoolers.baconbits.client.widgets.TitleBar.TitleBarStyle;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.ApplicationData;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
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
	private ParameterHandler<EventRegistration> refreshParticipants = new ParameterHandler<EventRegistration>() {
		@Override
		public void execute(EventRegistration item) {
			registration = item;
			pageData.setRegistration(item);
			populateParticipants();
		}
	};
	private Button volunteerAddButton;

	public EventRegistrationSection(EventPageData pd) {
		initWidget(vp);
		vp.setWidth("700px");
		pageData = pd;
		registration = pageData.getRegistration();

		vp.setSpacing(3);

		loadSection();

	}

	private void addParticipantSection() {
		TitleBar tb = new TitleBar("Registered Participants", TitleBarStyle.SUBSECTION);

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
										loadSection();
									} else {
										Application.reloadPage();
									}
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
					if (!Application.isAuthenticated()) {
						showLogin();
						return;
					}

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

		vp.add(new TitleBar("Register to Volunteer", TitleBarStyle.SUBSECTION));
		if (pageData.getEvent().allowRegistrations()) {
			HorizontalPanel pp = new PaddedPanel();
			final DefaultListBox lb = new DefaultListBox();
			lb.addStyleName("RequiredListBox");
			lb.addItem("", 0);
			final Map<Integer, EventVolunteerPosition> vMap = new HashMap<Integer, EventVolunteerPosition>();
			for (EventVolunteerPosition p : pageData.getVolunteerPositions()) {
				if (p.getOpenPositionCount() == 0) {
					continue;
				}
				if (!registration.getVolunteerPositions().contains(p)) {
					lb.addItem(p.getJobTitle(), p.getId());
					vMap.put(p.getId(), p);
				}
			}
			pp.add(lb);

			final Label description = new Label();
			description.addStyleName("mediumPadding");

			volunteerAddButton = new Button("Volunteer!");
			volunteerAddButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (!Application.isAuthenticated()) {
						showLogin();
						return;
					}

					int positionId = lb.getIntValue();

					if (positionId > 0) {
						volunteerAddButton.setEnabled(false);
						final EventVolunteerPosition position = vMap.get(positionId);
						position.setRegisterPositionCount(1);

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
						description.setText("");
					}
				}
			});
			pp.add(volunteerAddButton);

			lb.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					int positionId = lb.getIntValue();

					if (positionId > 0) {
						description.setText(vMap.get(positionId).getDescription());
					} else {
						description.setText("");
					}
				}
			});

			vp.add(pp);
			vp.add(description);
		}

		positionTable = new FlexTable();
		positionTable.setWidth("250px");

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
			if (p.isCanceled() || registration.getCanceled()) {
				editLabel.addStyleName("strikeText");
				editText = "Restore";
			} else {
				editText = "X";
			}
			participantTable.setWidget(i, 0, editLabel);

			participantTable.setText(i, 1, Formatter.formatCurrency(p.getPrice()));
			if (p.isCanceled() || registration.getCanceled()) {
				participantTable.getCellFormatter().addStyleName(i, 1, "strikeText");
			} else {
				totalPrice += p.getPrice();
			}

			if (!registration.getCanceled() && pageData.getEvent().allowRegistrations()) {
				ClickLabel cl = new ClickLabel(editText, new MouseDownHandler() {
					@Override
					public void onMouseDown(MouseDownEvent event) {
						String confirmText = "";
						if (p.isCanceled()) {
							confirmText = "Restore registration for " + p.getFirstName() + "?";
						} else {
							confirmText = "Really remove " + p.getFirstName() + " from the attendee list?";
						}
						ConfirmDialog.confirm(confirmText, new ConfirmHandler() {
							@Override
							public void onConfirm() {
								// TODO do wait list check here
								p.setStatusId(p.isCanceled() ? 1 : 5);

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
			positionTable.setWidget(i, 0, new Label(p.getJobTitle()));

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
				volunteerAddButton.setEnabled(true);
			}
		});
	}

	private void showLogin() {
		LoginServiceAsync loginService = (LoginServiceAsync) ServiceCache.getService(LoginService.class);
		final LoginDialog ld = new LoginDialog(loginService);
		ld.setLoginHandler(new LoginHandler() {
			@Override
			public void onLogin(ApplicationData ap) {
				Window.Location.reload();
			}
		});
		ld.center();
	}
}