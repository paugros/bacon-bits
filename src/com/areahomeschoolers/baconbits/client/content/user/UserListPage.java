package com.areahomeschoolers.baconbits.client.content.user;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;

import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.VerticalPanel;

public final class UserListPage implements Page {
	public UserListPage(final VerticalPanel page) {
		ArgMap<UserArg> args = new ArgMap<UserArg>();
		final String title = "Users";
		UserCellTable userListTable = new UserCellTable(args);
		userListTable.setTitle(title);
		if (Application.isAuthenticated()) {
			Hyperlink addLink = new Hyperlink("Add", PageUrl.user(0));
			userListTable.getTitleBar().addLink(addLink);
		}

		userListTable.getTitleBar().addSearchControl();
		userListTable.getTitleBar().addExcelControl();
		page.add(WidgetFactory.newSection(userListTable));

		userListTable.addDataReturnHandler(new DataReturnHandler() {
			@Override
			public void onDataReturn() {
				Application.getLayout().setPage(title, page);
			}
		});

		userListTable.populate();
	}
}
