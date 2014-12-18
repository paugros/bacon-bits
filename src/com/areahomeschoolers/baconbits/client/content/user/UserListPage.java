package com.areahomeschoolers.baconbits.client.content.user;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.widgets.AddLink;
import com.areahomeschoolers.baconbits.client.widgets.CookieCrumb;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.GeocoderTextBox;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.client.widgets.TilePanel;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.User;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public final class UserListPage implements Page {
	private VerticalPanel optionsPanel = new VerticalPanel();
	private ArgMap<UserArg> args;
	private TilePanel fp = new TilePanel();
	private UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);

	public UserListPage(final VerticalPanel page) {
		fp.setWidth("100%");
		if (!Application.isAuthenticated()) {
			new ErrorPage(PageError.NOT_AUTHORIZED);
			return;
		}

		final String title = "Directory";

		CookieCrumb cc = new CookieCrumb();
		cc.add(new Hyperlink("Homeschoolers By Interests", PageUrl.tagGroup("USER")));
		if (!Common.isNullOrBlank(Url.getParameter("tagId"))) {
			cc.add(URL.decode(Url.getParameter("tn")));
		} else {
			cc.add("Homeschoolers");
		}
		page.add(cc);

		if (Application.isSystemAdministrator()) {
			AddLink link = new AddLink("Add User", PageUrl.user(0));
			link.getElement().getStyle().setMarginLeft(10, Unit.PX);
			page.add(link);
		}

		page.add(optionsPanel);

		populateOptionsPanel();

		args = getDefaultArgs();

		page.add(fp);
		Application.getLayout().setPage(title, page);

		populate();
	}

	private ArgMap<UserArg> getDefaultArgs() {
		ArgMap<UserArg> args = new ArgMap<UserArg>(Status.ACTIVE);
		args.put(UserArg.PARENTS);
		if (Application.hasLocation()) {
			args.put(UserArg.WITHIN_LAT, Double.toString(Application.getCurrentLat()));
			args.put(UserArg.WITHIN_LNG, Double.toString(Application.getCurrentLng()));
			args.put(UserArg.WITHIN_MILES, Constants.defaultSearchRadius);
		}
		if (!Common.isNullOrBlank(Url.getParameter("tagId"))) {
			args.put(UserArg.HAS_TAGS, Url.getIntListParameter("tagId"));
		}
		return args;
	}

	private void populate() {
		userService.list(args, new Callback<ArrayList<User>>() {
			@Override
			protected void doOnSuccess(ArrayList<User> result) {
				fp.clear();

				for (User u : result) {
					fp.add(new UserTile(u), u.getId());
				}
			}
		});
	}

	private void populateOptionsPanel() {
		optionsPanel.addStyleName("boxedBlurb");
		optionsPanel.setSpacing(8);
		PaddedPanel top = new PaddedPanel(10);

		Label label = new Label("Show");
		top.add(label);

		final DefaultListBox memberInput = new DefaultListBox();
		memberInput.addItem("everyone");
		memberInput.addItem("children");
		memberInput.addItem("children (boys)");
		memberInput.addItem("children (girls)");
		memberInput.addItem("parents of boys or girls");
		memberInput.addItem("parents of boys");
		memberInput.addItem("parents of girls");
		memberInput.setSelectedIndex(4);
		memberInput.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				args.remove(UserArg.PARENTS);
				args.remove(UserArg.PARENTS_OF_BOYS);
				args.remove(UserArg.PARENTS_OF_GIRLS);
				args.remove(UserArg.CHILDREN);
				args.remove(UserArg.SEX);

				switch (memberInput.getSelectedIndex()) {
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

				populate();
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
				populate();
			}
		});

		// within miles
		final GeocoderTextBox locationInput = new GeocoderTextBox();
		if (Application.hasLocation()) {
			locationInput.setText(Application.getCurrentLocation());
		}

		final DefaultListBox milesInput = new DefaultListBox();
		milesInput.addItem("5", 5);
		milesInput.addItem("10", 10);
		milesInput.addItem("25", 25);
		milesInput.addItem("50", 50);
		milesInput.setValue(Constants.defaultSearchRadius);
		milesInput.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				args.put(UserArg.WITHIN_MILES, milesInput.getIntValue());
				if (!locationInput.getText().isEmpty()) {
					populate();
				}
			}
		});

		locationInput.setClearCommand(new Command() {
			@Override
			public void execute() {
				args.remove(UserArg.WITHIN_LAT);
				args.remove(UserArg.WITHIN_LNG);
				populate();
			}
		});

		locationInput.setChangeCommand(new Command() {
			@Override
			public void execute() {
				args.put(UserArg.WITHIN_LAT, Double.toString(locationInput.getLat()));
				args.put(UserArg.WITHIN_LNG, Double.toString(locationInput.getLng()));
				args.put(UserArg.WITHIN_MILES, milesInput.getIntValue());
				populate();
			}
		});

		top.add(memberInput);
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

		// PaddedPanel sp = new PaddedPanel();
		// sp.add(new Label("Search text"));

		// sp.add(searchControl);

		// optionsPanel.add(sp);

		if (Application.isAuthenticated()) {
			CheckBox cb = new CheckBox("Only show members with whom I have interests in common");
			cb.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					if (event.getValue()) {
						args.put(UserArg.ONLY_COMMON_INTERESTS);
					} else {
						args.remove(UserArg.ONLY_COMMON_INTERESTS);
					}

					populate();
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
		populate();
	}
}
