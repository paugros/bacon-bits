package com.areahomeschoolers.baconbits.client.content.tag;

import java.util.HashSet;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.TagService;
import com.areahomeschoolers.baconbits.client.rpc.service.TagServiceAsync;
import com.areahomeschoolers.baconbits.client.util.ClientUtils;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable.SelectionPolicy;
import com.areahomeschoolers.baconbits.shared.dto.Arg.TagArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Tag;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;

public final class TagManagementPage implements Page {
	private ArgMap<TagArg> args = new ArgMap<TagArg>();
	private TagServiceAsync tagService = (TagServiceAsync) ServiceCache.getService(TagService.class);

	public TagManagementPage(final VerticalPanel page) {
		if (!Application.hasRole(AccessLevel.GROUP_ADMINISTRATORS)) {
			new ErrorPage(PageError.NOT_AUTHORIZED);
			return;
		}

		final String title = "Tags";
		args.put(TagArg.GET_ALL_COUNTS);

		final TagTable table = new TagTable(args);
		if (!ClientUtils.isMobileBrowser()) {
			table.setWidth("800px");
		}
		table.setSelectionPolicy(SelectionPolicy.MULTI_ROW);
		table.getTitleBar().addPagingControl();
		table.getTitleBar().addSearchControl();
		table.getTitleBar().addExcelControl();
		table.getTitleBar().addLink(new ClickLabel("Add", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final TagEditDialog dialog = new TagEditDialog();
				dialog.setText("Add Tag");
				dialog.addFormSubmitHandler(new FormSubmitHandler() {
					@Override
					public void onFormSubmit(FormField formField) {
						tagService.save(dialog.getEntity(), new Callback<Tag>() {
							@Override
							protected void doOnSuccess(Tag result) {
								table.addItem(result);
							}
						});
					}
				});
				dialog.center(new Tag());
			}
		}));

		table.getTitleBar().addLink(new ClickLabel("Merge", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final TagEditDialog dialog = new TagEditDialog();
				dialog.setText("Merge Selected Tags");
				dialog.addFormSubmitHandler(new FormSubmitHandler() {
					@Override
					public void onFormSubmit(FormField formField) {
						HashSet<Integer> ids = new HashSet<>(table.getSelectedItemIds());
						tagService.merge(dialog.getEntity(), ids, new Callback<Tag>() {
							@Override
							protected void doOnSuccess(Tag result) {
								table.removeItems(table.getSelectedItems());
								table.addItem(result);
								table.clearSelection();
							}
						});
					}
				});
				dialog.center(new Tag());
			}
		}));

		table.setTitle(title);
		page.add(WidgetFactory.newSection(table, ContentWidth.MAXWIDTH1000PX));

		table.addDataReturnHandler(new DataReturnHandler() {
			@Override
			public void onDataReturn() {
				Application.getLayout().setPage(title, page);
			}
		});

		table.populate();
	}
}
