package com.areahomeschoolers.baconbits.client.content.user;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.user.UserTable.UserColumn;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.widgets.EntityEditDialog;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable.LabelColumnWidth;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.GroupListBox;
import com.areahomeschoolers.baconbits.client.widgets.RequiredTextBox;
import com.areahomeschoolers.baconbits.client.widgets.ValidatorDateBox;
import com.areahomeschoolers.baconbits.client.widgets.cellview.VariableSizePager.PageSize;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class UserGroupEditDialog extends EntityEditDialog<UserGroup> {
	private UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);
	private UserGroupTable table;

	public UserGroupEditDialog(UserGroupTable groupTable) {
		this.table = groupTable;

		addFormSubmitHandler(new FormSubmitHandler() {
			@Override
			public void onFormSubmit(FormField formField) {
				userService.saveUserGroup(entity, new Callback<UserGroup>() {
					@Override
					protected void doOnSuccess(UserGroup result) {
						hide();
						table.addItem(result);
						table.refresh();
					}
				});
			}
		});
	}

	@Override
	protected Widget createContent() {
		final VerticalPanel vp = new VerticalPanel();

		FieldTable ft = new FieldTable();
		ft.setLabelColumnWidth(LabelColumnWidth.NARROW);
		ft.setWidth("600px");

		if (!entity.isSaved()) {
			VerticalPanel ovp = new VerticalPanel();
			ovp.setSpacing(2);
			final GroupListBox orgInput = new GroupListBox();
			if (Application.isSystemAdministrator()) {
				CheckBox cb = new CheckBox("This group is an organization");
				cb.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
					@Override
					public void onValueChange(ValueChangeEvent<Boolean> event) {
						entity.setOrganization(event.getValue());
						orgInput.setEnabled(!event.getValue());
						entity.setOrganizationId(null);
					}
				});
				ovp.add(cb);
			}
			ovp.add(orgInput);
			orgInput.showOnlyOrganizations();
			FormField orgField = form.createFormField("Organization:", ovp, null);
			orgField.setInitializer(new Command() {
				@Override
				public void execute() {
					orgInput.setValue(entity.getOrganizationId());
				}
			});
			orgField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					entity.setOrganizationId(orgInput.getIntValue());
				}
			});
			ft.addField(orgField);
		}

		final RequiredTextBox nameInput = new RequiredTextBox();
		nameInput.setMaxLength(50);
		FormField nameField = form.createFormField("Name:", nameInput, null);
		nameField.setInitializer(new Command() {
			@Override
			public void execute() {
				nameInput.setText(entity.getGroupName());
			}
		});
		nameField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setGroupName(nameInput.getText());
			}
		});
		ft.addField(nameField);

		final TextBox descriptionInput = new TextBox();
		descriptionInput.setVisibleLength(50);
		descriptionInput.setMaxLength(100);
		FormField descriptionField = form.createFormField("Description:", descriptionInput, null);
		descriptionField.setInitializer(new Command() {
			@Override
			public void execute() {
				descriptionInput.setText(entity.getDescription());
			}
		});
		descriptionField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setDescription(descriptionInput.getText());
			}
		});
		ft.addField(descriptionField);

		final ValidatorDateBox startInput = new ValidatorDateBox();
		FormField startField = form.createFormField("Start date:", startInput, null);
		startField.setInitializer(new Command() {
			@Override
			public void execute() {
				startInput.setValue(entity.getStartDate());
			}
		});
		startField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setStartDate(startInput.getValue());
			}
		});
		ft.addField(startField);

		// end date
		final ValidatorDateBox endInput = new ValidatorDateBox();
		FormField endField = form.createFormField("End date:", endInput, null);
		endField.setInitializer(new Command() {
			@Override
			public void execute() {
				endInput.setValue(entity.getEndDate());
			}
		});
		endField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setEndDate(endInput.getValue());
			}
		});
		ft.addField(endField);

		vp.add(ft);

		if (entity.isSaved()) {
			ArgMap<UserArg> args = new ArgMap<UserArg>(Status.ACTIVE);
			args.put(UserArg.GROUP_ID, entity.getId());
			final UserTable userTable = new UserTable(args);
			userTable.setPagerPageSize(PageSize.P_010);
			userTable.getTitleBar().getPager().setPageResizingEnabled(false);
			userTable.setDisplayColumns(UserColumn.NAME, UserColumn.EMAIL, UserColumn.PHONE);
			userTable.setTitle("Members");
			userTable.getTitleBar().addExcelControl();
			userTable.getTitleBar().addSearchControl();
			userTable.addDataReturnHandler(new DataReturnHandler() {
				@Override
				public void onDataReturn() {
					vp.add(WidgetFactory.newSection(userTable));

					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							center();
						}
					});
				}
			});
			userTable.populate();

		}
		return vp;
	}

}
