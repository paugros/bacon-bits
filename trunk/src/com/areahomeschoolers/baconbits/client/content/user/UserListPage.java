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
import com.areahomeschoolers.baconbits.client.widgets.GoogleMap;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.maps.client.geocode.LocationCallback;
import com.google.gwt.maps.client.geocode.Placemark;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public final class UserListPage implements Page {
	private VerticalPanel optionsPanel = new VerticalPanel();
	private VerticalPanel page;
	private ArgMap<UserArg> args = new ArgMap<UserArg>(Status.ACTIVE);
	private UserTable table;
	private String lastLocationText;

	public UserListPage(final VerticalPanel page) {
		if (!Application.isAuthenticated()) {
			new ErrorPage(PageError.NOT_AUTHORIZED);
			return;
		}

		final String title = "Directory";
		this.page = page;

		populateOptionsPanel();

		args.put(UserArg.PARENTS);
		table = new UserTable(args);
		table.removeColumn(UserColumn.STATUS);
		table.removeColumn(UserColumn.SEX);

		table.setTitle(title);
		if (Application.hasRole(AccessLevel.GROUP_ADMINISTRATORS)) {
			Hyperlink addLink = new Hyperlink("Add", PageUrl.user(0));
			table.getTitleBar().addLink(addLink);
			table.addStatusFilterBox();
		} else {
			table.removeColumn(UserColumn.GROUP);
		}

		table.getTitleBar().addExcelControl();
		table.getTitleBar().addSearchControl();
		page.add(WidgetFactory.newSection(table, ContentWidth.MAXWIDTH1200PX));

		table.addDataReturnHandler(new DataReturnHandler() {
			@Override
			public void onDataReturn() {
				Application.getLayout().setPage(title, page);
			}
		});

		table.populate();
	}

	private void populateOptionsPanel() {
		PaddedPanel top = new PaddedPanel(10);

		optionsPanel.addStyleName("boxedBlurb");
		Label label = new Label("Show");
		top.add(label);

		final DefaultListBox peopleInput = new DefaultListBox();
		peopleInput.addItem("everyone");
		peopleInput.addItem("children");
		peopleInput.addItem("parents of boys or girls");
		peopleInput.addItem("parents of boys");
		peopleInput.addItem("parents of girls");
		peopleInput.setSelectedIndex(2);
		peopleInput.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				args.remove(UserArg.PARENTS);
				args.remove(UserArg.PARENTS_OF_BOYS);
				args.remove(UserArg.PARENTS_OF_GIRLS);
				args.remove(UserArg.CHILDREN);

				switch (peopleInput.getSelectedIndex()) {
				case 1:
					args.put(UserArg.CHILDREN);
					break;
				case 2:
					args.put(UserArg.PARENTS);
					break;
				case 3:
					args.put(UserArg.PARENTS_OF_BOYS);
					break;
				case 4:
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
				args.put(UserArg.PARENTS_OF_AGES, ageInput.getValue());
				table.populate();
			}
		});

		final DefaultListBox milesInput = new DefaultListBox();
		final TextBox locationInput = new TextBox();
		locationInput.setVisibleLength(30);
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

		locationInput.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					locationInput.setFocus(false);
				}
			}
		});
		locationInput.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				if (lastLocationText != null && lastLocationText.equals(locationInput.getText())) {
					return;
				}

				if (locationInput.getText().isEmpty()) {
					if (lastLocationText == null) {
						return;
					}
					args.remove(UserArg.WITHIN_LAT);
					args.remove(UserArg.WITHIN_LNG);
					table.populate();
				}

				lastLocationText = locationInput.getText();

				GoogleMap.runMapsCommand(new Command() {
					@Override
					public void execute() {
						GoogleMap.getGeoCoder().getLocations(locationInput.getText(), new LocationCallback() {
							@Override
							public void onFailure(int statusCode) {

							}

							@Override
							public void onSuccess(JsArray<Placemark> locations) {
								args.put(UserArg.WITHIN_LAT, Double.toString(locations.get(0).getPoint().getLatitude()));
								args.put(UserArg.WITHIN_LNG, Double.toString(locations.get(0).getPoint().getLongitude()));
								args.put(UserArg.WITHIN_MILES, milesInput.getIntValue());
								table.populate();
							}
						});
					}
				});
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

		page.add(optionsPanel);
	}
}
