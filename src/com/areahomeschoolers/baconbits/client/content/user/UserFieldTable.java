package com.areahomeschoolers.baconbits.client.content.user;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.event.ConfirmHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;
import com.areahomeschoolers.baconbits.client.widgets.AddressField;
import com.areahomeschoolers.baconbits.client.widgets.AlertDialog;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.ConfirmDialog;
import com.areahomeschoolers.baconbits.client.widgets.DefaultHyperlink;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.EmailTextBox;
import com.areahomeschoolers.baconbits.client.widgets.FieldDisplayLink;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.Form;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.MonthYearPicker;
import com.areahomeschoolers.baconbits.client.widgets.PhoneTextBox;
import com.areahomeschoolers.baconbits.client.widgets.RequiredListBox;
import com.areahomeschoolers.baconbits.client.widgets.RequiredTextBox;
import com.areahomeschoolers.baconbits.client.widgets.ResetPasswordDialog;
import com.areahomeschoolers.baconbits.client.widgets.ServerResponseDialog;
import com.areahomeschoolers.baconbits.client.widgets.ValidatorDateBox;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.PrivacyPreferenceType;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;
import com.areahomeschoolers.baconbits.shared.dto.User;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

public class UserFieldTable extends FieldTable {
	private User user;
	private final UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);
	private final Label passwordLabel = new Label();

	private final Form form;

	public UserFieldTable(Form f, User u) {
		form = f;
		user = u;
		setWidth("100%");

		final Label firstNameDisplay = new Label();
		final RequiredTextBox firstNameInput = new RequiredTextBox();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				if (firstNameInput.isVisible()) {
					firstNameInput.setFocus(true);
				}
			}
		});
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
				String value = firstNameInput.getText().trim();
				String test = Common.stripNonAlphaChars(firstNameInput.getText());
				if (Common.isAllLowerCase(test) || Common.isAllUpperCase(test)) {
					value = Common.ucWords(value);
				}
				user.setFirstName(value);
			}
		});
		addField(firstNameField);

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
				String value = lastNameInput.getText().trim();
				String test = Common.stripNonAlphaChars(lastNameInput.getText());
				if (Common.isAllLowerCase(test) || Common.isAllUpperCase(test)) {
					value = Common.ucWords(value);
				}
				user.setLastName(value);
			}
		});
		addField(lastNameField);

		if (user.userCanSee(Application.getCurrentUser(), PrivacyPreferenceType.EMAIL)) {
			final FieldDisplayLink emailDisplay = new FieldDisplayLink();
			final EmailTextBox emailInput = new EmailTextBox();
			if (user.getParentId() == null || user.getEmail() != null) {
				emailInput.setRequired(true);
			}
			emailInput.setMaxLength(100);
			emailInput.setVisibleLength(40);
			FormField emailField = form.createFormField("Email:", emailInput, emailDisplay);
			emailField.setInitializer(new Command() {
				@Override
				public void execute() {
					emailDisplay.setText(Common.getDefaultIfNull(user.getEmail()));
					emailDisplay.setHref("mailto:" + user.getEmail());
					emailInput.setText(user.getEmail());
				}
			});
			emailField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					user.setEmail(emailInput.getText().toLowerCase().trim());
				}
			});
			addField(emailField);

			if (user.isSaved()) {
				final EmailTextBox payPalEmailInput = new EmailTextBox();
				payPalEmailInput.setMaxLength(255);
				payPalEmailInput.setVisibleLength(30);
				final Label payPalEmailDisplay = new Label();
				FormField payPalEmailField = form.createFormField("PayPal email:", payPalEmailInput, payPalEmailDisplay);
				payPalEmailField.setInitializer(new Command() {
					@Override
					public void execute() {
						payPalEmailDisplay.setText(Common.getDefaultIfNull(user.getPayPalEmail()));
						payPalEmailInput.setText(user.getPayPalEmail());
					}
				});
				payPalEmailField.setDtoUpdater(new Command() {
					@Override
					public void execute() {
						user.setPayPalEmail(payPalEmailInput.getText().trim());
					}
				});
				addField(payPalEmailField);
			}
		}

		if (!user.isChild() && user.isSaved() && Application.getUserActivity().get(user.getId()) != null) {
			UserStatusIndicator st = new UserStatusIndicator();
			st.setShowWeeksAndMonths(true);
			st.setUserId(user.getId());
			addField("Last activity:", st);
		}

		if (Application.isAuthenticated()) {
			final Label sexDisplay = new Label();
			final RequiredListBox sexInput = new RequiredListBox();
			sexInput.addItem("Female", "f");
			sexInput.addItem("Male", "m");
			FormField sexField = f.createFormField("Gender:", sexInput, sexDisplay);
			sexField.setInitializer(new Command() {
				@Override
				public void execute() {
					sexDisplay.setText(user.getSexyText());
					sexInput.setValue(user.getSex());
				}
			});
			sexField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					user.setSex(sexInput.getValue());
				}
			});
			addField(sexField);

			final MonthYearPicker birthDateInput = new MonthYearPicker();
			birthDateInput.setRequired(true);
			birthDateInput.setEarliestMonth(Application.getApplicationData().getAdultBirthYear(), 1);
			birthDateInput.getYearPicker().getListBox().insertItem("Adult", Integer.toString(Application.getApplicationData().getAdultBirthYear() - 1), 1);

			final Label birthDateDisplay = new Label();

			final FormField birthDateField = form.createFormField("Birth month / year:", birthDateInput, birthDateDisplay);
			birthDateField.setValidator(birthDateInput.getValidator());
			birthDateField.setRequired(true);
			birthDateField.setInitializer(new Command() {
				@Override
				public void execute() {
					String text = "";
					if (user.getBirthDate() != null) {
						text = Formatter.formatDate(user.getBirthDate(), "MMMM");
						if (!user.isChild()) {
							text += " (Adult)";
						} else {
							text += " " + Formatter.formatDate(user.getBirthDate(), "yyyy");
						}
					}
					birthDateDisplay.setText(text);
					birthDateInput.setValue(user.getBirthDate());
				}
			});
			birthDateField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					user.setBirthDate(birthDateInput.getValue());
				}
			});
			addField(birthDateField);
		}

		if (!user.isChild() && Application.isSystemAdministrator()) {
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
			addField(adminField);
		}

		if (user.isSaved() && !user.isChild() && Application.administratorOf(user)) {
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
			if (UserPage.canEditUser(user)) {
				if (user.equals(Application.getCurrentUser()) && user.getEmail() != null) {
					passwordField.getLinkPanel().add(new ClickLabel("Change password", new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							new ResetPasswordDialog(true).center();
						}
					}));
				} else {
					passwordField.getLinkPanel().add(new ClickLabel("Reset password", new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							if (user.getEmail() == null) {
								AlertDialog.alert("Please specify an email address before resetting the password.");
								return;
							}
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
			}
			addField(passwordField);
		}

		if (!Application.isAuthenticated() && !user.isSaved()) {
			PasswordInputs inputs = new PasswordInputs(new Command() {
				@Override
				public void execute() {
					form.submit();
				}
			});

			final PasswordTextBox passwordInput = inputs.getPasswordInput();
			FormField passwordField = form.createFormField("Password:", passwordInput, null);
			passwordField.setInitializer(new Command() {
				@Override
				public void execute() {
					passwordInput.setText(user.getPassword());
				}
			});
			passwordField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					user.setPassword(passwordInput.getText());
				}
			});
			addField(passwordField);

			Validator v = new Validator(passwordInput, new ValidatorCommand() {
				@Override
				public void validate(Validator validator) {
					validator.setError(Common.isNullOrBlank(passwordInput.getText()));
				}
			});
			passwordField.setValidator(v);
			passwordField.setRequired(true);

			final PasswordTextBox confirmInput = inputs.getConfirmPasswordInput();
			FormField confirmField = form.createFormField("Confirm password:", confirmInput, null);
			confirmField.setInitializer(new Command() {
				@Override
				public void execute() {
					confirmInput.setText(user.getPassword());
				}
			});
			confirmField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
				}
			});
			addField(confirmField);

			Validator vv = new Validator(confirmInput, new ValidatorCommand() {
				@Override
				public void validate(Validator validator) {
					validator.setError(Common.isNullOrBlank(confirmInput.getText()));
				}
			});
			confirmField.setValidator(vv);
			confirmField.setRequired(true);
		}

		if (user.userCanSee(Application.getCurrentUser(), PrivacyPreferenceType.ADDRESS)) {
			FormField addressField = new AddressField(user).getFormField();
			form.addField(addressField);
			addField(addressField);
		} else if (!Common.isNullOrBlank(user.getCity())) {
			String address = user.getCity();
			if (!Common.isNullOrBlank(user.getState())) {
				address += ", " + user.getState();
			}
			addField("Address:", address);
		}

		if (Application.isAuthenticated()) {
			if (user.userCanSee(Application.getCurrentUser(), PrivacyPreferenceType.HOME_PHONE)) {
				final Label homePhoneDisplay = new Label();
				final PhoneTextBox homePhoneInput = new PhoneTextBox();
				FormField homePhoneField = form.createFormField("Home phone:", homePhoneInput, homePhoneDisplay);
				homePhoneField.setInitializer(new Command() {
					@Override
					public void execute() {
						homePhoneDisplay.setText(Common.getDefaultIfNull(user.getHomePhone()));
						homePhoneInput.setText(user.getHomePhone());
					}
				});
				homePhoneField.setDtoUpdater(new Command() {
					@Override
					public void execute() {
						user.setHomePhone(homePhoneInput.getText());
					}
				});
				addField(homePhoneField);
			}

			if (user.userCanSee(Application.getCurrentUser(), PrivacyPreferenceType.MOBILE_PHONE)) {
				final Label mobilePhoneDisplay = new Label();
				final PhoneTextBox mobilePhoneInput = new PhoneTextBox();
				FormField mobilePhoneField = form.createFormField("Mobile phone:", mobilePhoneInput, mobilePhoneDisplay);
				mobilePhoneField.setInitializer(new Command() {
					@Override
					public void execute() {
						mobilePhoneDisplay.setText(Common.getDefaultIfNull(user.getMobilePhone()));
						mobilePhoneInput.setText(user.getMobilePhone());
					}
				});
				mobilePhoneField.setDtoUpdater(new Command() {
					@Override
					public void execute() {
						user.setMobilePhone(mobilePhoneInput.getText());
					}
				});
				addField(mobilePhoneField);
			}

			final FieldDisplayLink facebookDisplay = new FieldDisplayLink();
			final TextBox facebookInput = new TextBox();
			facebookInput.setMaxLength(200);
			facebookInput.setVisibleLength(40);
			FormField facebookField = form.createFormField("Facebook URL:", facebookInput, facebookDisplay);
			facebookField.setValidator(new Validator(facebookInput, new ValidatorCommand() {
				@Override
				public void validate(Validator validator) {
					String url = facebookInput.getText();
					if (Common.isNullOrBlank(url)) {
						return;
					}

					if (!url.matches("https?://(www.)?facebook.com/?.*")) {
						validator.setError(true);
					}
				}
			}));
			facebookField.setInitializer(new Command() {
				@Override
				public void execute() {
					facebookDisplay.setText(Common.getDefaultIfNull(user.getFacebookUrl()));
					if (user.getFacebookUrl() != null) {
						facebookDisplay.setHref(user.getFacebookUrl());
						facebookDisplay.setText("Click to view");
					}
					facebookInput.setText(user.getFacebookUrl());
					if (user.getFacebookUrl() == null) {
						facebookInput.setText("https://www.facebook.com/");
					}
				}
			});
			facebookField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					user.setFacebookUrl(facebookInput.getText());
				}
			});
			addField(facebookField);
		}

		if (user.isSaved() && user.getParentId() != null && user.getParentId() > 0) {
			addField("Parent:", new DefaultHyperlink(user.getParentFirstName() + " " + user.getParentLastName(), PageUrl.user(user.getParentId())));
		}

		if (!user.isChild()) {
			if (Application.administratorOf(user)) {
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
				addField(startDateField);
			}

			if (Application.isAuthenticated() && (Application.administratorOf(user) || user.childOf(Application.getCurrentUser()))) {
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
				addField(endDateField);
			}
		}

		if (user.isSaved()) {
			if (Application.administratorOf(user)) {
				addField("View count:", Integer.toString(user.getViewCount()));
			}
			if (!user.isChild()) {
				addField("Last login:", Common.getDefaultIfNull(Formatter.formatDateTime(user.getLastLoginDate())));
			}
			addField("Date added:", Formatter.formatDateTime(user.getAddedDate()));
		}
	}

	public void setUser(User u) {
		user = u;
		form.setDto(u);
	}

	private void updatePasswordLabel() {
		String pwd;

		if (user.getResetPassword()) {
			pwd = "Sent to user, will be reset upon first login.";
		} else if (user.getPasswordDigest() == null) {
			pwd = "Has not been set.";
		} else {
			pwd = "Has been set by the user.";
		}

		passwordLabel.setText(pwd);
	}
}
