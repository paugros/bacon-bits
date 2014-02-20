package com.areahomeschoolers.baconbits.client.content.user;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.content.Sidebar;
import com.areahomeschoolers.baconbits.client.content.Sidebar.MiniModule;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.content.user.UserGroupTable.UserGroupColumn;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserGroupArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.VerticalPanel;

public final class UserGroupListPage implements Page {
	public UserGroupListPage(final VerticalPanel page) {
		if (!Application.hasRole(AccessLevel.GROUP_ADMINISTRATORS)) {
			new ErrorPage(PageError.NOT_AUTHORIZED);
			return;
		}

		ArgMap<UserGroupArg> args = new ArgMap<UserGroupArg>();
		final String title = "Organizations and Groups";
		final UserGroupTable table = new UserGroupTable(args);
		table.setTitle(title);
		table.setDisplayColumns(UserGroupColumn.GROUP, UserGroupColumn.ORGANIZATION, UserGroupColumn.DESCRIPTION, UserGroupColumn.START_DATE,
				UserGroupColumn.END_DATE);
		if (Application.hasRole(AccessLevel.ORGANIZATION_ADMINISTRATORS)) {
			table.getTitleBar().addLink(new Hyperlink("Add", PageUrl.userGroup(0)));
		}

		table.getTitleBar().addExcelControl();
		table.getTitleBar().addSearchControl();
		table.addStatusFilterBox();
		page.add(WidgetFactory.newSection(table, ContentWidth.MAXWIDTH900PX));

		table.addDataReturnHandler(new DataReturnHandler() {
			@Override
			public void onDataReturn() {
				Sidebar sb = Sidebar.create(MiniModule.CITRUS, MiniModule.ACTIVE_USERS, MiniModule.NEW_USERS, MiniModule.UPCOMING_EVENTS);
				Application.getLayout().setPage(title, sb, page);
			}
		});

		table.populate();
	}
}
