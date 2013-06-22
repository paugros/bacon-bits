package com.areahomeschoolers.baconbits.client.content.user;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.content.user.UserGroupTable.UserGroupColumn;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserGroupArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
		if (Application.isSystemAdministrator()) {
			table.getTitleBar().addLink(new ClickLabel("Add", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					UserGroupEditDialog dialog = new UserGroupEditDialog(table);
					dialog.setText("Add Group");
					dialog.center(new UserGroup());
				}
			}));
		}

		table.getTitleBar().addExcelControl();
		table.getTitleBar().addSearchControl();
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
