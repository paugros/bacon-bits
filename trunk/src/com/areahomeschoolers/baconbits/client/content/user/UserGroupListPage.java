package com.areahomeschoolers.baconbits.client.content.user;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.content.user.UserGroupTable.UserGroupColumn;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.util.ClientUtils;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.DefaultHyperlink;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserGroupArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

import com.google.gwt.user.client.ui.VerticalPanel;

public final class UserGroupListPage implements Page {
	public UserGroupListPage(final VerticalPanel page) {
		if (!Application.hasRole(AccessLevel.GROUP_ADMINISTRATORS)) {
			new ErrorPage(PageError.NOT_AUTHORIZED);
			return;
		}

		ArgMap<UserGroupArg> args = new ArgMap<UserGroupArg>();
		if (!Application.isCitrus()) {
			args.put(UserGroupArg.ORGANIZATION_ID, Application.getCurrentOrgId());
		}
		final String title = "Groups";
		final UserGroupTable table = new UserGroupTable(args);
		if (!ClientUtils.isMobileBrowser()) {
			table.setWidth("900px");
		}
		table.disablePaging();
		table.setTitle(title);
		table.setDisplayColumns(UserGroupColumn.GROUP, UserGroupColumn.ORGANIZATION, UserGroupColumn.DESCRIPTION, UserGroupColumn.START_DATE,
				UserGroupColumn.END_DATE);
		if (Application.isAuthenticated()) {
			table.getTitleBar().addLink(new DefaultHyperlink("Add", PageUrl.userGroup(0)));
			table.getTitleBar().addLink(new DefaultHyperlink("Add Sub-group", PageUrl.userGroup(0) + "&type=sub"));
		}

		table.getTitleBar().addExcelControl();
		table.addStatusFilterBox();
		page.add(WidgetFactory.newSection(table, ContentWidth.MAXWIDTH900PX));

		table.addDataReturnHandler(new DataReturnHandler() {
			@Override
			public void onDataReturn() {
				Application.getLayout().setPage(title, page);
			}
		});

		table.populate();
	}
}
