package com.areahomeschoolers.baconbits.client.content.resource;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.content.resource.ResourceTable.ResourceColumn;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.DefaultHyperlink;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ResourceArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

import com.google.gwt.user.client.ui.VerticalPanel;

public final class ResourceManagementPage implements Page {
	private ArgMap<ResourceArg> args = new ArgMap<ResourceArg>();

	public ResourceManagementPage(final VerticalPanel page) {
		if (!Application.hasRole(AccessLevel.GROUP_ADMINISTRATORS)) {
			new ErrorPage(PageError.NOT_AUTHORIZED);
			return;
		}

		final String title = "Resources";

		final ResourceTable table = new ResourceTable(args);
		table.setDisplayColumns(ResourceColumn.NAME, ResourceColumn.DESCRIPTION, ResourceColumn.ADDED_DATE, ResourceColumn.LOCATION, ResourceColumn.TAGS,
				ResourceColumn.VIEW_COUNT, ResourceColumn.CLICK_COUNT, ResourceColumn.IMPRESSIONS);
		table.setTitle(title);
		table.getTitleBar().addLink(new DefaultHyperlink("Add", PageUrl.resource(0)));
		table.getTitleBar().addSearchControl();
		table.getTitleBar().addExcelControl();
		table.addStatusFilterBox();
		page.add(WidgetFactory.newSection(table, ContentWidth.MAXWIDTH1200PX));
		table.addDataReturnHandler(new DataReturnHandler() {
			@Override
			public void onDataReturn() {
				Application.getLayout().setPage(title, page);
			}
		});

		table.populate();
	}
}
