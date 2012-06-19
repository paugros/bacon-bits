package com.areahomeschoolers.baconbits.client.content.user;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.widgets.EmailTextBox;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.Form;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.PhoneTextBox;
import com.areahomeschoolers.baconbits.client.widgets.RequiredListBox;
import com.areahomeschoolers.baconbits.client.widgets.RequiredTextBox;
import com.areahomeschoolers.baconbits.client.widgets.ValidatorDateBox;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.User;
import com.areahomeschoolers.baconbits.shared.dto.UserPageData;

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
	private UserPageData pageData;

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
				pageData = result;
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

		final Label typeDisplay = new Label();
		final RequiredListBox typeInput = new RequiredListBox();
		for (Data type : pageData.getUserTypes()) {
			typeInput.addItem(type.get("type"), type.getId());
		}
		FormField typeField = form.createFormField("User type:", typeInput, typeDisplay);
		typeField.setInitializer(new Command() {
			@Override
			public void execute() {
				typeDisplay.setText(user.getUserType());
				typeInput.setValue(user.getUserTypeId());
			}
		});
		typeField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				user.setUserTypeId(typeInput.getIntValue());
			}
		});
		fieldTable.addField(typeField);

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
		userService.save(user, new Callback<User>() {
			@Override
			protected void doOnSuccess(User u) {
				if (!Url.isParamValidId("userId")) {
					HistoryToken.set(PageUrl.user(u.getId()));
				} else {
					user = u;
					form.setDto(u);
					field.setInputVisibility(false);
				}
			}
		});
	}
}
