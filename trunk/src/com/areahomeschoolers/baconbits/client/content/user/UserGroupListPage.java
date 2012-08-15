package com.areahomeschoolers.baconbits.client.content.user;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.content.user.UserGroupCellTable.UserGroupColumn;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.VerticalPanel;

public final class UserGroupListPage implements Page {
	public UserGroupListPage(final VerticalPanel page) {
		ArgMap<UserArg> args = new ArgMap<UserArg>();
		final String title = "Groups";
		final UserGroupCellTable table = new UserGroupCellTable(args);
		table.setTitle(title);
		table.setDisplayColumns(UserGroupColumn.NAME, UserGroupColumn.DESCRIPTION, UserGroupColumn.START_DATE, UserGroupColumn.END_DATE);
		if (Application.isAuthenticated()) {
			table.getTitleBar().addLink(new ClickLabel("Add", new MouseDownHandler() {
				@Override
				public void onMouseDown(MouseDownEvent event) {
					UserGroupEditDialog dialog = new UserGroupEditDialog(table);
					dialog.setText("Add Group");
					dialog.center(new UserGroup());
				}
			}));
		}

		table.getTitleBar().addExcelControl();
		table.getTitleBar().addSearchControl();
		page.add(WidgetFactory.newSection(table, ContentWidth.MAXWIDTH750PX));

		table.addDataReturnHandler(new DataReturnHandler() {
			@Override
			public void onDataReturn() {
				Application.getLayout().setPage(title, page);
			}
		});

		table.populate();
	}
}
