package com.areahomeschoolers.baconbits.client.content.article;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ArticleArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.VerticalPanel;

public final class ArticleListPage implements Page {
	public ArticleListPage(final VerticalPanel page) {
		if (!Application.hasRole(AccessLevel.GROUP_ADMINISTRATORS)) {
			new ErrorPage(PageError.NOT_AUTHORIZED);
			return;
		}

		ArgMap<ArticleArg> args = new ArgMap<ArticleArg>();
		final String title = "Organizations and Groups";
		final ArticleTable table = new ArticleTable(args);
		table.setTitle(title);
		if (Application.isSystemAdministrator()) {
			table.getTitleBar().addLink(new Hyperlink("Add", PageUrl.article(0)));
		}

		table.getTitleBar().addExcelControl();
		table.getTitleBar().addSearchControl();
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
