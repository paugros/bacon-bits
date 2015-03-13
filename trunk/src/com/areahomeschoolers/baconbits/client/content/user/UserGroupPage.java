package com.areahomeschoolers.baconbits.client.content.user;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.event.MarkupField;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.content.user.UserTable.UserColumn;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.util.ClientUtils;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;
import com.areahomeschoolers.baconbits.client.widgets.AddressField;
import com.areahomeschoolers.baconbits.client.widgets.AlertDialog;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.EmailDialog;
import com.areahomeschoolers.baconbits.client.widgets.EmailTextBox;
import com.areahomeschoolers.baconbits.client.widgets.FieldDisplayLink;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.Form;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.NumericTextBox;
import com.areahomeschoolers.baconbits.client.widgets.RegexTextBox;
import com.areahomeschoolers.baconbits.client.widgets.RequestMembershipLink;
import com.areahomeschoolers.baconbits.client.widgets.RequiredTextBox;
import com.areahomeschoolers.baconbits.client.widgets.TabPage;
import com.areahomeschoolers.baconbits.client.widgets.TabPage.TabPageCommand;
import com.areahomeschoolers.baconbits.client.widgets.ValidatorDateBox;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserGroupArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class UserGroupPage implements Page {
	private UserGroup group = new UserGroup();
	private Form form = new Form(new FormSubmitHandler() {
		@Override
		public void onFormSubmit(final FormField formField) {
			AddressField.validateAddress(group, new Command() {
				@Override
				public void execute() {
					save(formField);
				}
			});
		}
	});
	private VerticalPanel page;
	private FieldTable fieldTable = new FieldTable();
	private UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);
	private FormField subDomainField;
	private FormField payPalEmailField;
	private FormField feeField;
	private FormField markupField;
	private TabPage tabPanel;

	public UserGroupPage(final VerticalPanel page) {
		int userGroupId = Url.getIntegerParameter("userGroupId");

		if (!Application.isAuthenticated()) {
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
					group = result.get(0);
					Label heading = new Label(group.getGroupName());
					heading.addStyleName("hugeText");
					head.add(heading);

					if (Application.isAuthenticated() && group.getOrganization() && Application.getCurrentUser().getGroups().get(group.getId()) == null) {
						head.add(new RequestMembershipLink(group));
					}

					page.add(head);
					initializePage();
				}
			});
		} else {
			String type = Url.getParameter("type");
			boolean isOrg = Common.isNullOrBlank(type);
			group.setOrganization(isOrg);
			group.setOwningOrgId(isOrg ? 0 : Application.getCurrentOrgId());
			group.setOrgDomain(Constants.CG_DOMAIN);
			if (isOrg) {
				group.setContactId(Application.getCurrentUserId());
			}

			initializePage();
		}
	}

	private void createFieldTable() {
		final RequiredTextBox nameInput = new RequiredTextBox();
		nameInput.setVisibleLength(30);
		nameInput.setMaxLength(50);
		final Label nameDisplay = new Label();
		FormField nameField = form.createFormField("Name:", nameInput, nameDisplay);
		nameField.setInitializer(new Command() {
			@Override
			public void execute() {
				nameDisplay.setText(group.getGroupName());
				nameInput.setText(group.getGroupName());
			}
		});
		nameField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				group.setGroupName(nameInput.getText());
			}
		});
		fieldTable.addField(nameField);

		if (group.isOrganization()) {
			HorizontalPanel dp = new HorizontalPanel();
			final RegexTextBox subDomainInput = new RegexTextBox("^(?:[A-Za-z0-9][A-Za-z0-9\\-]{0,61}[A-Za-z0-9]|[A-Za-z0-9])$");
			subDomainInput.getValidator().setErrorMessage("Site address: allowed characters include letters, numbers, and hyphens (not leading or trailing)");
			subDomainInput.setDirection(Direction.RTL);
			dp.add(subDomainInput);
			Label domainText = new Label("." + Constants.CG_DOMAIN);
			domainText.getElement().getStyle().setMarginBottom(3, Unit.PX);
			dp.add(domainText);
			dp.setCellVerticalAlignment(domainText, HasVerticalAlignment.ALIGN_BOTTOM);
			subDomainInput.setRequired(true);
			subDomainInput.setMaxLength(30);
			final Anchor subDomainDisplay = new Anchor();
			subDomainField = form.createFormField("Site address:", dp, subDomainDisplay);
			subDomainField.setValidator(subDomainInput.getValidator());
			subDomainField.setInitializer(new Command() {
				@Override
				public void execute() {
					subDomainDisplay.setText(group.getOrgSubDomain() + "." + Constants.CG_DOMAIN);
					subDomainDisplay.setHref("http://" + group.getOrgSubDomain() + "." + Constants.CG_DOMAIN);
					subDomainInput.setText(group.getOrgSubDomain());
				}
			});
			subDomainField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					group.setOrgSubDomain(subDomainInput.getText().toLowerCase());
				}
			});
			fieldTable.addField(subDomainField);

			final EmailTextBox payPalEmailInput = new EmailTextBox();
			payPalEmailInput.setMaxLength(255);
			payPalEmailInput.setVisibleLength(30);
			final Label payPalEmailDisplay = new Label();
			payPalEmailField = form.createFormField("PayPal email:", payPalEmailInput, payPalEmailDisplay);
			payPalEmailField.setInitializer(new Command() {
				@Override
				public void execute() {
					payPalEmailDisplay.setText(Common.getDefaultIfNull(group.getPayPalEmail()));
					payPalEmailInput.setText(group.getPayPalEmail());
				}
			});
			payPalEmailField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					group.setPayPalEmail(payPalEmailInput.getText());
				}
			});
			fieldTable.addField(payPalEmailField);

			final Label feeDisplay = new Label();
			final NumericTextBox feeInput = new NumericTextBox(2);
			feeInput.setMaxLength(50);
			feeField = form.createFormField("Membership fee:", feeInput, feeDisplay);
			feeField.setInitializer(new Command() {
				@Override
				public void execute() {
					String display = "None";
					if (group.getMembershipFee() > 0) {
						display = Formatter.formatCurrency(group.getMembershipFee());
						feeInput.setValue(group.getMembershipFee());
					}
					feeDisplay.setText(display);
				}
			});
			feeField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					group.setMembershipFee(feeInput.getDouble());
				}
			});
			fieldTable.addField(feeField);

			if (Application.isSystemAdministrator()) {
				markupField = new MarkupField(group).getFormField();
				form.addField(markupField);
				fieldTable.addField(markupField);
			}
		}

		final TextBox descriptionInput = new TextBox();
		descriptionInput.setVisibleLength(50);
		descriptionInput.setMaxLength(100);
		final Label descriptionDisplay = new Label();
		FormField descriptionField = form.createFormField("Description:", descriptionInput, descriptionDisplay);
		descriptionField.setInitializer(new Command() {
			@Override
			public void execute() {
				descriptionDisplay.setText(Common.getDefaultIfNull(group.getDescription()));
				descriptionInput.setText(group.getDescription());
			}
		});
		descriptionField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				group.setDescription(descriptionInput.getText());
			}
		});
		fieldTable.addField(descriptionField);

		FormField addressField = new AddressField(group).getFormField();
		form.addField(addressField);
		fieldTable.addField(addressField);

		final Label religiousDisplay = new Label();
		final DefaultListBox religiousInput = new DefaultListBox();
		religiousInput.addItem("- Select -");
		religiousInput.addItem("This is a religious group");
		religiousInput.addItem("This is a secular group");
		FormField religiousField = form.createFormField("Religious affiliation:", religiousInput, religiousDisplay);
		religiousField.setInitializer(new Command() {
			@Override
			public void execute() {
				if (group.isSaved()) {
					religiousInput.setSelectedIndex(group.getReligious() ? 1 : 2);
				}
				religiousDisplay.setText(religiousInput.getSelectedText());
			}
		});
		religiousField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				group.setReligious(religiousInput.getSelectedIndex() == 1);
			}
		});
		fieldTable.addField(religiousField);

		if (group.isSaved()) {
			ArgMap<UserArg> contactArgs = new ArgMap<UserArg>(Status.ACTIVE);
			contactArgs.put(UserArg.GROUP_ID, group.getId());
			contactArgs.put(UserArg.PARENTS);
			contactArgs.put(UserArg.HAS_EMAIL);
			final UserPicker contactInput = new UserPicker(contactArgs);
			contactInput.getSelector().getCellTable().setDisplayColumns(UserColumn.EMAIL, UserColumn.NAME, UserColumn.PHONE);
			final FieldDisplayLink contactDisplay = new FieldDisplayLink();
			FormField contactField = form.createFormField("Group contact:", contactInput, contactDisplay);
			contactField.setInitializer(new Command() {
				@Override
				public void execute() {
					contactDisplay.setText(Common.getDefaultIfNull(group.getContact()));
					if (group.getContactId() != null) {
						contactInput.setValueById(group.getContactId());
						contactInput.getTextBox().setText(group.getContact());
						contactDisplay.setHref(PageUrl.user(group.getContactId()));
					}
				}
			});
			contactField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					group.setContactId(contactInput.getValueId());
				}
			});
			fieldTable.addField(contactField);
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
				facebookDisplay.setText(Common.getDefaultIfNull(group.getFacebookUrl()));
				if (!Common.isNullOrBlank(group.getFacebookUrl())) {
					facebookDisplay.setHref(group.getFacebookUrl());
					facebookDisplay.setText(group.getFacebookUrl());
					facebookInput.setText(group.getFacebookUrl());
				} else {
					facebookInput.setText(Constants.FACEBOOK_URL);
				}
			}
		});
		facebookField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				String url = facebookInput.getText().trim();
				if (!url.equals(Constants.FACEBOOK_URL)) {
					group.setFacebookUrl(facebookInput.getText());
				}
			}
		});
		fieldTable.addField(facebookField);

		if (Application.isSystemAdministrator()) {
			final ValidatorDateBox startInput = new ValidatorDateBox();
			final Label startDateDisplay = new Label();
			FormField startField = form.createFormField("Start date:", startInput, startDateDisplay);
			startField.setInitializer(new Command() {
				@Override
				public void execute() {
					startDateDisplay.setText(Formatter.formatDate(group.getStartDate()));
					startInput.setValue(group.getStartDate());
				}
			});
			startField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					group.setStartDate(startInput.getValue());
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
					endDateDisplay.setText(Common.getDefaultIfNull(Formatter.formatDate(group.getEndDate())));
					endInput.setValue(group.getEndDate());
				}
			});
			endField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					group.setEndDate(endInput.getValue());
				}
			});
			fieldTable.addField(endField);
		}
	}

	private void initializePage() {
		final String title = group.isSaved() ? group.getGroupName() : "New Group";
		createFieldTable();
		form.initialize();

		if (!group.isSaved()) {
			if (Application.isCitrus() && group.isOrganization() && !Application.isSystemAdministrator()) {
				VerticalPanel help = new VerticalPanel();
				help.setSpacing(5);
				Label heading = new Label("Create Your New Group");
				heading.addStyleName("largeText");
				help.add(heading);
				String info = "After saving, you'll be redirected to your new site. You'll need to log in again using the ";
				info += "same user and password you use for this site.";
				HTML infoBlock = new HTML(info);
				infoBlock.addStyleName(ContentWidth.MAXWIDTH600PX.toString());
				help.add(infoBlock);

				page.add(help);
			} else if (!group.isOrganization()) {
				Label heading = new Label("New Group");
				heading.addStyleName("largeText");
				page.add(heading);
			}
			form.configureForAdd(fieldTable);
			page.add(fieldTable);
		} else {
			tabPanel = new TabPage();
			if (!ClientUtils.isMobileBrowser()) {
				tabPanel.setWidth("850px");
			}
			form.emancipate();
			tabPanel.add("Group", new TabPageCommand() {
				@Override
				public void execute(VerticalPanel tabBody) {
					tabBody.add(fieldTable);

					tabPanel.selectTabNow(tabBody);
				}
			});

			tabPanel.add("Members", new TabPageCommand() {
				@Override
				public void execute(final VerticalPanel tabBody) {
					ArgMap<UserArg> args = new ArgMap<UserArg>(Status.ACTIVE);
					args.put(UserArg.GROUP_ID, group.getId());
					final UserTable userTable = new UserTable(args);
					userTable.setUserGroup(group);
					userTable.disablePaging();
					userTable.setTitle("Members");
					userTable.getTitleBar().addLink(new ClickLabel("Email Members", new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							EmailDialog dialog = new EmailDialog();
							dialog.setShowSubjectBox(true);
							dialog.setAllowEditRecipients(true);
							dialog.addBcc(userTable.getFullList());
							dialog.center();
						}
					}));
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

			if (!Application.administratorOf(group)) {
				form.setEnabled(false);
			}
			page.add(tabPanel);
		}

		Application.getLayout().setPage(title, page);
	}

	private void save(final FormField field) {
		userService.saveUserGroup(group, new Callback<UserGroup>() {
			@Override
			protected void doOnSuccess(UserGroup a) {
				if (a == null) {
					AlertDialog.alert("Sorry, that site address is unavailable. Please try something different.");
					field.getSubmitButton().setEnabled(true);
					form.getSubmitButton().setEnabled(true);
					return;
				}

				if (!Url.isParamValidId("userGroupId")) {
					if (group.isOrganization() && !Application.isSystemAdministrator()) {
						Window.Location.assign("http://" + group.getOrgSubDomain() + "." + Constants.CG_DOMAIN);
					} else {
						HistoryToken.set(PageUrl.userGroup(a.getId()));
					}
				} else {
					group = a;
					form.setDto(a);
					field.setInputVisibility(false);
				}
			}
		});
	}
}
