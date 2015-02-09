package com.areahomeschoolers.baconbits.client.content.user;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.content.tag.FriendlyTextWidget;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.widgets.AddLink;
import com.areahomeschoolers.baconbits.client.widgets.CookieCrumb;
import com.areahomeschoolers.baconbits.client.widgets.DefaultHyperlink;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.LocationFilterInput;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.client.widgets.TilePanel;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;
import com.areahomeschoolers.baconbits.shared.dto.User;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public final class UserListPage implements Page {
	private VerticalPanel optionsPanel = new VerticalPanel();
	private ArgMap<UserArg> args;
	private TilePanel fp = new TilePanel();
	private UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);
	private TextBox searchControl;
	private ArrayList<User> users;

	public UserListPage(final VerticalPanel page) {
		fp.setWidth("100%");
		if (!Application.isAuthenticated()) {
			new ErrorPage(PageError.NOT_AUTHORIZED);
			return;
		}

		final String title = "Directory";

		CookieCrumb cc = new CookieCrumb();
		cc.add(new DefaultHyperlink("Homeschoolers By Interests", PageUrl.tagGroup("USER")));
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
			page.setCellWidth(link, "1%");
		}

		PaddedPanel pp = new PaddedPanel(10);
		pp.add(optionsPanel);
		pp.add(new FriendlyTextWidget(TagMappingType.USER));

		page.add(pp);

		populateOptionsPanel();

		args = getDefaultArgs();

		page.add(fp);
		Application.getLayout().setPage(title, page);

		populate();
	}

	private void applyTextFilter(String text) {
		if (text == null || text.isEmpty()) {
			fp.showAll();
			return;
		}

		text = text.toLowerCase();

		for (User u : users) {
			String email = u.getEmail() == null ? "" : u.getEmail();
			fp.setVisible(u, u.getFullName().toLowerCase().contains(text) || email.toLowerCase().contains(text));
		}
	}

	private ArgMap<UserArg> getDefaultArgs() {
		ArgMap<UserArg> defaultArgs = new ArgMap<UserArg>(Status.ACTIVE);
		if (Application.hasLocation()) {
			defaultArgs.put(UserArg.LOCATION_FILTER, true);
		}
		defaultArgs.put(UserArg.PARENTS);
		if (!Common.isNullOrBlank(Url.getParameter("tagId"))) {
			defaultArgs.put(UserArg.HAS_TAGS, Url.getIntListParameter("tagId"));
		}
		return defaultArgs;
	}

	private void populate() {
		userService.list(args, new Callback<ArrayList<User>>() {
			@Override
			protected void doOnSuccess(ArrayList<User> result) {
				users = result;
				fp.clear();

				for (User u : result) {
					fp.add(new UserTile(u), u.getId());
				}
			}
		});
	}

	// private void resetFilter() {
	// optionsPanel.clear();
	// populateOptionsPanel();
	// args = getDefaultArgs();
	// populate();
	// }

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

		optionsPanel.add(top);

		PaddedPanel middle = new PaddedPanel(10);
		// within miles
		final LocationFilterInput locationInput = new LocationFilterInput();
		if (Application.hasLocation()) {
			locationInput.setText(Application.getCurrentLocation());
		}

		locationInput.setClearCommand(new Command() {
			@Override
			public void execute() {
				args.remove(UserArg.LOCATION_FILTER);
				populate();
			}
		});

		locationInput.setChangeCommand(new Command() {
			@Override
			public void execute() {
				args.put(UserArg.LOCATION_FILTER, true);
				populate();
			}
		});

		top.add(memberInput);
		top.add(new Label("of"));
		top.add(ageInput);
		middle.add(locationInput);

		for (int i = 0; i < middle.getWidgetCount(); i++) {
			middle.setCellVerticalAlignment(middle.getWidget(i), HasVerticalAlignment.ALIGN_MIDDLE);
		}

		optionsPanel.add(middle);

		PaddedPanel sp = new PaddedPanel();
		searchControl = new TextBox();
		searchControl.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				applyTextFilter(searchControl.getText());
			}
		});

		searchControl.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					applyTextFilter(searchControl.getText());
				}
			}
		});
		searchControl.setVisibleLength(35);
		sp.add(new Label("Search text"));
		sp.add(searchControl);

		optionsPanel.add(sp);

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

		// Button resetButton = new Button("Reset", new ClickHandler() {
		// @Override
		// public void onClick(ClickEvent event) {
		// resetFilter();
		// }
		// });
		// optionsPanel.add(resetButton);
		// optionsPanel.setCellHorizontalAlignment(resetButton, HasHorizontalAlignment.ALIGN_CENTER);
	}
}
