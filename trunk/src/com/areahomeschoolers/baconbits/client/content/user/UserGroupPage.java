package com.areahomeschoolers.baconbits.client.content.user;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.Sidebar;
import com.areahomeschoolers.baconbits.client.content.Sidebar.MiniModule;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.content.user.UserTable.UserColumn;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.EmailTextBox;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.Form;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.RequestMembershipLink;
import com.areahomeschoolers.baconbits.client.widgets.RequiredTextBox;
import com.areahomeschoolers.baconbits.client.widgets.TabPage;
import com.areahomeschoolers.baconbits.client.widgets.TabPage.TabPageCommand;
import com.areahomeschoolers.baconbits.client.widgets.ValidatorDateBox;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserGroupArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class UserGroupPage implements Page {
	private Form form = new Form(new FormSubmitHandler() {
		@Override
		public void onFormSubmit(FormField formField) {
			save(formField);
		}
	});
	private VerticalPanel page;
	private FieldTable fieldTable = new FieldTable();
	private UserGroup userGroup = new UserGroup();
	private UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);
	private FormField subDomainField;
	private FormField domainField;
	private FormField shortNameField;
	private FormField payPalEmailField;
	private TabPage tabPanel;

	public UserGroupPage(final VerticalPanel page) {
		int userGroupId = Url.getIntegerParameter("userGroupId");

		if (!Application.isAuthenticated()) {
			new ErrorPage(PageError.NOT_AUTHORIZED);
			return;
		}

		if (!Application.hasRole(AccessLevel.ORGANIZATION_ADMINISTRATORS) && userGroupId < 0) {
			new ErrorPage(PageError.NOT_AUTHORIZED);
			return;
		}

		this.page = page;

		if (userGroupId > 0) {
			userService.listGroups(new ArgMap<UserGroupArg>(UserGroupArg.ID, userGroupId), new Callback<ArrayList<UserGroup>>() {
				@Override
				protected void doOnSuccess(ArrayList<UserGroup> result) {
					if (result == null || result.isEmpty()) {
						new ErrorPage(PageError.PAGE_NOT_FOUND);
						return;
					}

					VerticalPanel head = new VerticalPanel();
					userGroup = result.get(0);
					Label heading = new Label(userGroup.getGroupName());
					heading.addStyleName("hugeText");
					head.add(heading);

					if (Application.isAuthenticated() && Application.getCurrentUser().getGroups().get(userGroup.getId()) == null) {
						head.add(new RequestMembershipLink(userGroup));
					}

					page.add(head);
					initializePage();
				}
			});
		} else {
			initializePage();
		}
	}

	private void createFieldTable() {
		fieldTable.setWidth("100%");

		final RequiredTextBox nameInput = new RequiredTextBox();
		nameInput.setMaxLength(50);
		final Label nameDisplay = new Label();
		FormField nameField = form.createFormField("Name:", nameInput, nameDisplay);
		nameField.setInitializer(new Command() {
			@Override
			public void execute() {
				nameDisplay.setText(userGroup.getGroupName());
				nameInput.setText(userGroup.getGroupName());
			}
		});
		nameField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				userGroup.setGroupName(nameInput.getText());
			}
		});
		fieldTable.addField(nameField);

		if (Application.isSystemAdministrator()) {
			final CheckBox orgInput = new CheckBox("This group is an organization");

			if (!userGroup.isSaved()) {
				orgInput.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
					@Override
					public void onValueChange(ValueChangeEvent<Boolean> event) {
						toggleOrgFields(event.getValue());
					}
				});
				FormField orgField = form.createFormField("Organization:", orgInput, null);
				orgField.setDtoUpdater(new Command() {
					@Override
					public void execute() {
						boolean isOrg = orgInput.getValue();
						userGroup.setOrganization(isOrg);
						userGroup.setOwningOrgId(isOrg ? 0 : Application.getCurrentOrgId());
					}
				});
				fieldTable.addField(orgField);
			}

			final RequiredTextBox subDomainInput = new RequiredTextBox();
			subDomainInput.setMaxLength(255);
			final Label subDomainDisplay = new Label();
			subDomainField = form.createFormField("Sub-domain:", subDomainInput, subDomainDisplay);
			subDomainField.setInitializer(new Command() {
				@Override
				public void execute() {
					subDomainDisplay.setText(userGroup.getOrgSubDomain());
					subDomainInput.setText(userGroup.getOrgSubDomain());
				}
			});
			subDomainField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					userGroup.setOrgSubDomain(subDomainInput.getText());
				}
			});
			fieldTable.addField(subDomainField);

			final RequiredTextBox domainInput = new RequiredTextBox();
			domainInput.setMaxLength(255);
			domainInput.setVisibleLength(30);
			final Label domainDisplay = new Label();
			domainField = form.createFormField("Domain:", domainInput, domainDisplay);
			domainField.setInitializer(new Command() {
				@Override
				public void execute() {
					domainDisplay.setText(userGroup.getOrgDomain());
					domainInput.setText(userGroup.getOrgDomain());
				}
			});
			domainField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					userGroup.setOrgDomain(domainInput.getText());
				}
			});
			fieldTable.addField(domainField);

			final RequiredTextBox shortNameInput = new RequiredTextBox();
			shortNameInput.setMaxLength(10);
			shortNameInput.setVisibleLength(10);
			final Label shortNameDisplay = new Label();
			shortNameField = form.createFormField("Short name:", shortNameInput, shortNameDisplay);
			shortNameField.setInitializer(new Command() {
				@Override
				public void execute() {
					shortNameDisplay.setText(userGroup.getShortName());
					shortNameInput.setText(userGroup.getShortName());
				}
			});
			shortNameField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					userGroup.setShortName(shortNameInput.getText());
				}
			});
			fieldTable.addField(shortNameField);

			final EmailTextBox payPalEmailInput = new EmailTextBox();
			payPalEmailInput.setRequired(true);
			payPalEmailInput.setMaxLength(255);
			payPalEmailInput.setVisibleLength(30);
			final Label payPalEmailDisplay = new Label();
			payPalEmailField = form.createFormField("PayPal email:", payPalEmailInput, payPalEmailDisplay);
			payPalEmailField.setInitializer(new Command() {
				@Override
				public void execute() {
					payPalEmailDisplay.setText(userGroup.getPayPalEmail());
					payPalEmailInput.setText(userGroup.getPayPalEmail());
				}
			});
			payPalEmailField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					userGroup.setPayPalEmail(payPalEmailInput.getText());
				}
			});
			fieldTable.addField(payPalEmailField);

			if (!userGroup.isSaved()) {
				orgInput.setValue(userGroup.isOrganization());
			}
			toggleOrgFields(userGroup.isOrganization());
		}

		final TextBox descriptionInput = new TextBox();
		descriptionInput.setVisibleLength(50);
		descriptionInput.setMaxLength(100);
		final Label descriptionDisplay = new Label();
		FormField descriptionField = form.createFormField("Description:", descriptionInput, descriptionDisplay);
		descriptionField.setInitializer(new Command() {
			@Override
			public void execute() {
				descriptionDisplay.setText(Common.getDefaultIfNull(userGroup.getDescription()));
				descriptionInput.setText(userGroup.getDescription());
			}
		});
		descriptionField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				userGroup.setDescription(descriptionInput.getText());
			}
		});
		fieldTable.addField(descriptionField);

		final ValidatorDateBox startInput = new ValidatorDateBox();
		final Label startDateDisplay = new Label();
		FormField startField = form.createFormField("Start date:", startInput, startDateDisplay);
		startField.setInitializer(new Command() {
			@Override
			public void execute() {
				startDateDisplay.setText(Formatter.formatDate(userGroup.getStartDate()));
				startInput.setValue(userGroup.getStartDate());
			}
		});
		startField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				userGroup.setStartDate(startInput.getValue());
			}
		});
		fieldTable.addField(startField);

		// end date
		final ValidatorDateBox endInput = new ValidatorDateBox();
		final Label endDateDisplay = new Label();
		FormField endField = form.createFormField("End date:", endInput, endDateDisplay);
		endField.setInitializer(new Command() {
			@Override
			public void execute() {
				endDateDisplay.setText(Common.getDefaultIfNull(Formatter.formatDate(userGroup.getEndDate())));
				endInput.setValue(userGroup.getEndDate());
			}
		});
		endField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				userGroup.setEndDate(endInput.getValue());
			}
		});
		fieldTable.addField(endField);
	}

	private void initializePage() {
		final String title = userGroup.isSaved() ? userGroup.getGroupName() : "New Group";
		createFieldTable();
		form.initialize();

		if (!userGroup.isSaved()) {
			form.configureForAdd(fieldTable);
			page.add(WidgetFactory.newSection(title, fieldTable, ContentWidth.MAXWIDTH900PX));
		} else {
			tabPanel = new TabPage();
			form.emancipate();
			tabPanel.add("Group", new TabPageCommand() {
				@Override
				public void execute(VerticalPanel tabBody) {
					tabBody.add(WidgetFactory.newSection(title, fieldTable, ContentWidth.MAXWIDTH900PX));

					tabPanel.selectTabNow(tabBody);
				}
			});

			tabPanel.add("Members", new TabPageCommand() {
				@Override
				public void execute(final VerticalPanel tabBody) {
					ArgMap<UserArg> args = new ArgMap<UserArg>(Status.ACTIVE);
					args.put(UserArg.GROUP_ID, userGroup.getId());
					final UserTable userTable = new UserTable(args);
					userTable.setUserGroup(userGroup);
					userTable.disablePaging();
					userTable.setTitle("Members");
					userTable.getTitleBar().addSearchControl();
					userTable.getTitleBar().addExcelControl();
					userTable.setDisplayColumns(UserColumn.PICTURE, UserColumn.ACTIVITY, UserColumn.NAME, UserColumn.EMAIL, UserColumn.PHONE,
							UserColumn.ADMINISTRATOR, UserColumn.APPROVAL, UserColumn.DELETE);
					userTable.addDataReturnHandler(new DataReturnHandler() {
						@Override
						public void onDataReturn() {
							tabBody.add(WidgetFactory.newSection(userTable, ContentWidth.MAXWIDTH1100PX));
							tabPanel.selectTabNow(tabBody);
						}
					});
					userTable.populate();
				}
			});

			if (!Application.administratorOf(userGroup)) {
				form.setEnabled(false);
			}
			page.add(tabPanel);
		}

		Sidebar sb = Sidebar.create(MiniModule.ACTIVE_USERS, MiniModule.NEW_USERS, MiniModule.UPCOMING_EVENTS, MiniModule.CITRUS);
		Application.getLayout().setPage(title, sb, page);
	}

	private void save(final FormField field) {
		userService.saveUserGroup(userGroup, new Callback<UserGroup>() {
			@Override
			protected void doOnSuccess(UserGroup a) {
				if (!Url.isParamValidId("userGroupId")) {
					HistoryToken.set(PageUrl.userGroup(a.getId()));
				} else {
					userGroup = a;
					form.setDto(a);
					field.setInputVisibility(false);
				}
			}
		});
	}

	private void toggleOrgFields(boolean visible) {
		fieldTable.setFieldVisibility(subDomainField, visible);
		fieldTable.setFieldVisibility(domainField, visible);
		fieldTable.setFieldVisibility(shortNameField, visible);
		fieldTable.setFieldVisibility(payPalEmailField, visible);

		if (!userGroup.isSaved() && visible) {
			form.configureForAdd();
		}
	}
}
