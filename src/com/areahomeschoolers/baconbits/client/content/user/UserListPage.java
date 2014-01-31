package com.areahomeschoolers.baconbits.client.content.user;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.content.user.UserTable.UserColumn;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.GeocoderTextBox;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable.SortDirection;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public final class UserListPage implements Page {
	private VerticalPanel optionsPanel = new VerticalPanel();
	private ArgMap<UserArg> args = getDefaultArgs();
	private UserTable table;
	private TextBox searchControl;

	public UserListPage(final VerticalPanel page) {
		if (!Application.isAuthenticated()) {
			new ErrorPage(PageError.NOT_AUTHORIZED);
			return;
		}

		final String title = "Directory";

		Label heading = new Label("Find People");
		heading.addStyleName("hugeText");
		page.add(heading);

		table = new UserTable(args);
		table.removeColumn(UserColumn.STATUS);
		table.removeColumn(UserColumn.SEX);
		table.setDefaultSortColumn(UserColumn.ACTIVITY, SortDirection.SORT_DESC);

		table.setTitle(title);
		table.getTitleBar().addExcelControl();
		table.getTitleBar().addSearchControl();

		searchControl = table.getTitleBar().extractSearchControl();
		searchControl.setVisibleLength(30);

		// this is done after adding the search control, so we can move it into the options panel
		optionsPanel.addStyleName("boxedBlurb");
		optionsPanel.setSpacing(8);
		page.add(optionsPanel);

		populateOptionsPanel();

		ContentWidth cw = null;
		if (Application.hasRole(AccessLevel.GROUP_ADMINISTRATORS)) {
			Hyperlink addLink = new Hyperlink("Add", PageUrl.user(0));
			table.getTitleBar().addLink(addLink);
			table.addStatusFilterBox();
			cw = ContentWidth.MAXWIDTH1200PX;
		} else {
			table.removeColumn(UserColumn.GROUP);
			table.removeColumn(UserColumn.EMAIL);
			table.removeColumn(UserColumn.PHONE);
			cw = ContentWidth.MAXWIDTH1000PX;
		}

		page.add(WidgetFactory.newSection(table, cw));

		table.addDataReturnHandler(new DataReturnHandler() {
			@Override
			public void onDataReturn() {
				Application.getLayout().setPage(title, page);
			}
		});

		table.populate();
	}

	private ArgMap<UserArg> getDefaultArgs() {
		ArgMap<UserArg> args = new ArgMap<UserArg>(Status.ACTIVE);
		args.put(UserArg.PARENTS);
		return args;
	}

	private void populateOptionsPanel() {
		PaddedPanel top = new PaddedPanel(10);

		Label label = new Label("Show");
		top.add(label);

		final DefaultListBox peopleInput = new DefaultListBox();
		peopleInput.addItem("everyone");
		peopleInput.addItem("children");
		peopleInput.addItem("children (boys)");
		peopleInput.addItem("children (girls)");
		peopleInput.addItem("parents of boys or girls");
		peopleInput.addItem("parents of boys");
		peopleInput.addItem("parents of girls");
		peopleInput.setSelectedIndex(4);
		peopleInput.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				args.remove(UserArg.PARENTS);
				args.remove(UserArg.PARENTS_OF_BOYS);
				args.remove(UserArg.PARENTS_OF_GIRLS);
				args.remove(UserArg.CHILDREN);
				args.remove(UserArg.SEX);

				switch (peopleInput.getSelectedIndex()) {
				case 1:
					args.put(UserArg.CHILDREN);
					break;
				case 2:
					args.put(UserArg.CHILDREN);
					args.put(UserArg.SEX, "m");
					break;
				case 3:
					args.put(UserArg.CHILDREN);
					args.put(UserArg.SEX, "f");
					break;
				case 4:
					args.put(UserArg.PARENTS);
					break;
				case 5:
					args.put(UserArg.PARENTS_OF_BOYS);
					break;
				case 6:
					args.put(UserArg.PARENTS_OF_GIRLS);
					break;
				}

				table.populate();
			}
		});

		final DefaultListBox ageInput = new DefaultListBox();
		ageInput.addItem("any age", "0");
		ageInput.addItem("ages 0-2", "0,2");
		ageInput.addItem("ages 3-5", "3,5");
		ageInput.addItem("ages 6-8", "6,8");
		ageInput.addItem("ages 9-11", "9,11");
		ageInput.addItem("ages 12-14", "12,14");
		ageInput.addItem("ages 15+", "15,18");
		ageInput.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				args.put(UserArg.AGES, ageInput.getValue());
				table.populate();
			}
		});

		// within miles
		final DefaultListBox milesInput = new DefaultListBox();
		final GeocoderTextBox locationInput = new GeocoderTextBox();
		milesInput.addItem("5", 5);
		milesInput.addItem("10", 10);
		milesInput.addItem("25", 25);
		milesInput.addItem("50", 50);
		milesInput.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				args.put(UserArg.WITHIN_MILES, milesInput.getIntValue());
				if (!locationInput.getText().isEmpty()) {
					table.populate();
				}
			}
		});

		locationInput.setClearCommand(new Command() {
			@Override
			public void execute() {
				args.remove(UserArg.WITHIN_LAT);
				args.remove(UserArg.WITHIN_LNG);
				table.populate();
			}
		});

		locationInput.setChangeCommand(new Command() {
			@Override
			public void execute() {
				args.put(UserArg.WITHIN_LAT, Double.toString(locationInput.getLat()));
				args.put(UserArg.WITHIN_LNG, Double.toString(locationInput.getLng()));
				args.put(UserArg.WITHIN_MILES, milesInput.getIntValue());
				table.populate();
			}
		});

		top.add(peopleInput);
		top.add(new Label("of"));
		top.add(ageInput);
		top.add(new Label("within"));
		top.add(milesInput);
		top.add(new Label("miles of"));
		top.add(locationInput);

		for (int i = 0; i < top.getWidgetCount(); i++) {
			top.setCellVerticalAlignment(top.getWidget(i), HasVerticalAlignment.ALIGN_MIDDLE);
		}

		optionsPanel.add(top);

		PaddedPanel sp = new PaddedPanel();
		sp.add(new Label("Search text"));

		sp.add(searchControl);

		optionsPanel.add(sp);

		if (Application.isAuthenticated()) {
			CheckBox cb = new CheckBox("Only show people with whom I have interests in common");
			cb.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					if (event.getValue()) {
						args.put(UserArg.ONLY_COMMON_INTERESTS);
					} else {
						args.remove(UserArg.ONLY_COMMON_INTERESTS);
					}

					table.populate();
				}
			});
			optionsPanel.add(cb);
		}

		Button resetButton = new Button("Reset", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				resetFilter();
			}
		});

		optionsPanel.add(resetButton);
		optionsPanel.setCellHorizontalAlignment(resetButton, HasHorizontalAlignment.ALIGN_CENTER);
	}

	private void resetFilter() {
		optionsPanel.clear();
		populateOptionsPanel();
		args = getDefaultArgs();
		table.setArgMap(args);
		table.populate();
	}
}
