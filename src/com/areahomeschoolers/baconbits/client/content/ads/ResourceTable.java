package com.areahomeschoolers.baconbits.client.content.ads;

import java.util.Date;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.ads.ResourceTable.ResourceColumn;
import com.areahomeschoolers.baconbits.client.event.UploadCompleteHandler;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleService;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleServiceAsync;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.EditableImage;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTableColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.client.widgets.cellview.WidgetCellCreator;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ResourceArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Document.DocumentLinkType;
import com.areahomeschoolers.baconbits.shared.dto.Resource;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Widget;

public final class ResourceTable extends EntityCellTable<Resource, ResourceArg, ResourceColumn> {
	public enum ResourceColumn implements EntityCellTableColumn<ResourceColumn> {
		NAME("Title"), ADDED_DATE("Added"), END_DATE("End Date"), START_DATE("Start Date"), IMAGE("Image"), CLICK_COUNT("Click Count"), URL("URL");

		private String title;

		ResourceColumn(String title) {
			this.title = title;
		}

		@Override
		public String getTitle() {
			return title;
		}
	}

	private ArticleServiceAsync articleService = (ArticleServiceAsync) ServiceCache.getService(ArticleService.class);

	public ResourceTable(ArgMap<ResourceArg> args) {
		this();
		setArgMap(args);
	}

	private ResourceTable() {
		setTitle("Resources");

		setDefaultSortColumn(ResourceColumn.ADDED_DATE, SortDirection.SORT_DESC);
		setDisplayColumns(ResourceColumn.values());

		disablePaging();
	}

	@Override
	protected void fetchData() {
		articleService.getResources(getArgMap(), getCallback());
	}

	@Override
	protected void setColumns() {
		for (ResourceColumn col : getDisplayColumns()) {
			switch (col) {
			case ADDED_DATE:
				addDateColumn(col, new ValueGetter<Date, Resource>() {
					@Override
					public Date get(Resource item) {
						return item.getAddedDate();
					}
				});
				break;
			case NAME:
				addCompositeWidgetColumn(col, new WidgetCellCreator<Resource>() {
					@Override
					protected Widget createWidget(final Resource item) {
						return new ClickLabel(item.getName(), new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								new ResourceEditDialog(item, new Command() {
									@Override
									public void execute() {
										populate();
									}
								}).center();
							}
						});
					}
				});
				break;
			case END_DATE:
				addDateColumn(col, new ValueGetter<Date, Resource>() {
					@Override
					public Date get(Resource item) {
						return item.getEndDate();
					}
				});
				break;
			case CLICK_COUNT:
				addNumberColumn(col, new ValueGetter<Number, Resource>() {
					@Override
					public Number get(Resource item) {
						return item.getClickCount();
					}
				}).setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
				break;
			case IMAGE:
				addCompositeWidgetColumn(col, new WidgetCellCreator<Resource>() {
					@Override
					protected Widget createWidget(final Resource item) {
						final EditableImage image = new EditableImage(DocumentLinkType.RESOURCE, item.getId(), item.getDocumentId(), true);
						image.setUploadCompleteHandler(new UploadCompleteHandler() {
							@Override
							public void onUploadComplete(int documentId) {
								populate();
							}
						});

						return image.getImage();
					}
				});
				break;
			case START_DATE:
				addDateColumn(col, new ValueGetter<Date, Resource>() {
					@Override
					public Date get(Resource item) {
						return item.getStartDate();
					}
				});
				break;
			case URL:
				addWidgetColumn(col, new WidgetCellCreator<Resource>() {
					@Override
					protected Widget createWidget(Resource item) {
						Anchor link = new Anchor(item.getUrl(), item.getUrl());
						return link;
					}
				});
				break;
			default:
				new AssertionError();
				break;
			}
		}
	}

}
