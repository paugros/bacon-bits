package com.areahomeschoolers.baconbits.client.content.user;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.content.user.UserGroupCellTable.UserGroupColumn;
import com.areahomeschoolers.baconbits.client.event.ConfirmHandler;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.widgets.AlertDialog;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.ConfirmDialog;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.EmailTextBox;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.Form;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.PhoneTextBox;
import com.areahomeschoolers.baconbits.client.widgets.RequiredTextBox;
import com.areahomeschoolers.baconbits.client.widgets.ServerResponseDialog;
import com.areahomeschoolers.baconbits.client.widgets.ValidatorDateBox;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;
import com.areahomeschoolers.baconbits.shared.dto.User;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;
import com.areahomeschoolers.baconbits.shared.dto.UserPageData;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class UserPage implements Page {
	private final Form form = new Form(new FormSubmitHandler() {
		@Override
		public void onFormSubmit(FormField formField) {
			save(formField);
		}
	});
	private VerticalPanel page;
	private final FieldTable fieldTable = new FieldTable();
	private User user = new User();
	private final UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);
	private final Label passwordLabel = new Label();

	public UserPage(VerticalPanel page) {
		int userId = Url.getIntegerParameter("userId");
		if (!Application.isAuthenticated() && userId < 0) {
			new ErrorPage(PageError.NOT_AUTHORIZED);
			return;
		}

		this.page = page;

		userService.getPageData(userId, new Callback<UserPageData>() {
			@Override
			protected void doOnSuccess(UserPageData result) {
				if (result == null) {
					new ErrorPage(PageError.PAGE_NOT_FOUND);
					return;
				}

				user = result.getUser();
				initializePage();
			}
		});
	}

	private void createFieldTable() {
		fieldTable.setWidth("100%");

		final Label firstNameDisplay = new Label();
		final RequiredTextBox firstNameInput = new RequiredTextBox();
		firstNameInput.setMaxLength(50);
		FormField firstNameField = form.createFormField("First name:", firstNameInput, firstNameDisplay);
		firstNameField.setInitializer(new Command() {
			@Override
			public void execute() {
				firstNameDisplay.setText(user.getFirstName());
				firstNameInput.setText(user.getFirstName());
			}
		});
		firstNameField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				user.setFirstName(firstNameInput.getText());
			}
		});
		fieldTable.addField(firstNameField);

		final Label lastNameDisplay = new Label();
		final RequiredTextBox lastNameInput = new RequiredTextBox();
		lastNameInput.setMaxLength(50);
		FormField lastNameField = form.createFormField("Last name:", lastNameInput, lastNameDisplay);
		lastNameField.setInitializer(new Command() {
			@Override
			public void execute() {
				lastNameDisplay.setText(user.getLastName());
				lastNameInput.setText(user.getLastName());
			}
		});
		lastNameField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				user.setLastName(lastNameInput.getText());
			}
		});
		fieldTable.addField(lastNameField);

		final Label emailDisplay = new Label();
		final EmailTextBox emailInput = new EmailTextBox();
		emailInput.setRequired(true);
		emailInput.setMaxLength(100);
		FormField emailField = form.createFormField("Email:", emailInput, emailDisplay);
		emailField.setInitializer(new Command() {
			@Override
			public void execute() {
				emailDisplay.setText(user.getEmail());
				emailInput.setText(user.getEmail());
			}
		});
		emailField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				user.setEmail(emailInput.getText());
			}
		});
		fieldTable.addField(emailField);

		final Label adminDisplay = new Label();
		final DefaultListBox adminInput = new DefaultListBox();
		adminInput.addItem("Yes");
		adminInput.addItem("No");
		FormField adminField = form.createFormField("System administrator:", adminInput, adminDisplay);
		adminField.setInitializer(new Command() {
			@Override
			public void execute() {
				adminDisplay.setText(Common.yesNo(user.getSystemAdministrator()));
				adminInput.setValue(Common.yesNo(user.getSystemAdministrator()));
			}
		});
		adminField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				user.setSystemAdministrator("Yes".equals(adminInput.getValue()));
			}
		});
		fieldTable.addField(adminField);

		if (user.isSaved()) {
			FormField passwordField = new FormField("Password:", passwordLabel, passwordLabel);
			passwordField.setInitializer(new Command() {
				@Override
				public void execute() {
					updatePasswordLabel();
				}
			});
			passwordField.initialize();
			passwordField.emancipate();
			passwordField.getLinkPanel().clear();
			passwordField.setEnabled(false);
			if (Application.isAuthenticated()) {
				passwordField.getLinkPanel().add(new ClickLabel("Reset password", new MouseDownHandler() {
					@Override
					public void onMouseDown(MouseDownEvent event) {
						String msg = "Reset the password for this user? Login information will be sent to their email address.";
						ConfirmDialog.confirm(msg, new ConfirmHandler() {
							@Override
							public void onConfirm() {
								user.setGeneratePassword(true);
								userService.save(user, new Callback<ServerResponseData<User>>() {
									@Override
									protected void doOnSuccess(ServerResponseData<User> result) {
										if (result.hasErrors()) {
											new ServerResponseDialog(result).center();
											return;
										}
										user = result.getData();
										updatePasswordLabel();
										AlertDialog.alert("Login Information Sent", new Label("Login information has been emailed to the user."));
									}
								});
							}
						});
					}
				}));
			}
			fieldTable.addField(passwordField);
		}

		final Label homePhoneDisplay = new Label();
		final PhoneTextBox homePhoneInput = new PhoneTextBox();
		FormField homePhoneField = form.createFormField("Home phone:", homePhoneInput, homePhoneDisplay);
		homePhoneField.setInitializer(new Command() {
			@Override
			public void execute() {
				homePhoneDisplay.setText(user.getHomePhone());
				homePhoneInput.setText(user.getHomePhone());
			}
		});
		homePhoneField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				user.setHomePhone(homePhoneInput.getText());
			}
		});
		fieldTable.addField(homePhoneField);

		final Label mobilePhoneDisplay = new Label();
		final PhoneTextBox mobilePhoneInput = new PhoneTextBox();
		FormField mobilePhoneField = form.createFormField("Mobile phone:", mobilePhoneInput, mobilePhoneDisplay);
		mobilePhoneField.setInitializer(new Command() {
			@Override
			public void execute() {
				mobilePhoneDisplay.setText(user.getMobilePhone());
				mobilePhoneInput.setText(user.getMobilePhone());
			}
		});
		mobilePhoneField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				user.setMobilePhone(mobilePhoneInput.getText());
			}
		});
		fieldTable.addField(mobilePhoneField);

		final Label startDateDisplay = new Label();
		final ValidatorDateBox startDateInput = new ValidatorDateBox();
		FormField startDateField = form.createFormField("Start date:", startDateInput, startDateDisplay);
		startDateField.setInitializer(new Command() {
			@Override
			public void execute() {
				startDateDisplay.setText(Common.getDefaultIfNull(Formatter.formatDate(user.getStartDate())));
				startDateInput.setValue(user.getStartDate());
			}
		});
		startDateField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				user.setStartDate(startDateInput.getValue());
			}
		});
		fieldTable.addField(startDateField);

		final Label endDateDisplay = new Label();
		final ValidatorDateBox endDateInput = new ValidatorDateBox();
		FormField endDateField = form.createFormField("End date:", endDateInput, endDateDisplay);
		endDateField.setInitializer(new Command() {
			@Override
			public void execute() {
				endDateDisplay.setText(Common.getDefaultIfNull(Formatter.formatDate(user.getEndDate())));
				endDateInput.setValue(user.getEndDate());
			}
		});
		endDateField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				user.setEndDate(endDateInput.getValue());
			}
		});
		fieldTable.addField(endDateField);

		if (user.isSaved()) {
			fieldTable.addField("Last login:", Common.getDefaultIfNull(Formatter.formatDateTime(user.getLastLoginDate())));
			fieldTable.addField("Date added:", Formatter.formatDateTime(user.getAddedDate()));
		}
	}

	private void initializePage() {
		String title = user.isSaved() ? user.getFullName() : "New User";
		createFieldTable();
		form.initialize();

		page.add(WidgetFactory.newSection(title, fieldTable));

		if (user.isSaved()) {
			ArgMap<UserArg> args = new ArgMap<UserArg>();
			args.put(UserArg.USER_ID, user.getId());
			final UserGroupCellTable groupsTable = new UserGroupCellTable(args);
			groupsTable.setUser(user);
			groupsTable.setTitle("Group Membership");
			groupsTable.setDisplayColumns(UserGroupColumn.NAME, UserGroupColumn.DESCRIPTION, UserGroupColumn.ADMINISTRATOR);
			groupsTable.getTitleBar().addExcelControl();
			groupsTable.getTitleBar().addLink(new ClickLabel("Add", new MouseDownHandler() {
				@Override
				public void onMouseDown(MouseDownEvent event) {
					final UserGroupSelector selector = new UserGroupSelector(new ArgMap<UserArg>());
					selector.addSubmitCommand(new Command() {
						@Override
						public void execute() {
							final ArrayList<UserGroup> groups = new ArrayList<UserGroup>(selector.getSelectedItems());
							groups.removeAll(groupsTable.getFullList());
							if (groups.isEmpty()) {
								return;
							}

							for (UserGroup g : groups) {
								if (!groupsTable.getFullList().contains(g)) {
									groupsTable.addItem(g);
								}
							}
							userService.updateUserGroupRelation(user, groups, true, new Callback<Void>() {
								@Override
								protected void doOnSuccess(Void item) {
									groups.removeAll(groupsTable.getFullList());
								}
							});
							selector.clearSelection();
						}
					});

					selector.setMultiSelect(true);
					selector.setSelectedItems(groupsTable.getFullList());
					selector.center();
				}
			}));
			groupsTable.populate();
			page.add(WidgetFactory.newSection(groupsTable));
		}

		if (!user.isSaved()) {
			form.configureForAdd(fieldTable);
		} else {
			form.emancipate();

			if (!Application.isAuthenticated()) {
				form.setEnabled(false);
			}
		}

		Application.getLayout().setPage(title, page);
	}

	private void save(final FormField field) {
		userService.save(user, new Callback<ServerResponseData<User>>() {
			@Override
			protected void doOnSuccess(ServerResponseData<User> r) {
				if (!Url.isParamValidId("userId")) {
					HistoryToken.set(PageUrl.user(r.getData().getId()));
				} else {
					user = r.getData();
					form.setDto(r.getData());
					field.setInputVisibility(false);
				}
			}
		});
	}

	private void updatePasswordLabel() {
		String pwd;

		if (user.getResetPassword()) {
			pwd = "Sent to user, will be reset upon first login.";
		} else {
			pwd = "Has been set by the user.";
		}

		passwordLabel.setText(pwd);
	}
}
