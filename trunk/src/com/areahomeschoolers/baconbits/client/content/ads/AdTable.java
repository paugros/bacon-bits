package com.areahomeschoolers.baconbits.client.content.ads;

import java.util.Date;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.ads.AdTable.AdColumn;
import com.areahomeschoolers.baconbits.client.event.UploadCompleteHandler;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleService;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.EditableImage;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTableColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.client.widgets.cellview.WidgetCellCreator;
import com.areahomeschoolers.baconbits.shared.dto.Ad;
import com.areahomeschoolers.baconbits.shared.dto.Arg.AdArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Document.DocumentLinkType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

public final class AdTable extends EntityCellTable<Ad, AdArg, AdColumn> {
	public enum AdColumn implements EntityCellTableColumn<AdColumn> {
		TITLE("Title"), ADDED_DATE("Added"), ADDED_BY("Added By"), END_DATE("End Date"), START_DATE("Start Date"), IMAGE("Image"), CLICK_COUNT("Click Count"), URL(
				"URL");

		private String title;

		AdColumn(String title) {
			this.title = title;
		}

		@Override
		public String getTitle() {
			return title;
		}
	}

	private ArticleServiceAsync articleService = (ArticleServiceAsync) ServiceCache.getService(ArticleService.class);

	public AdTable(ArgMap<AdArg> args) {
		this();
		setArgMap(args);
	}

	private AdTable() {
		setTitle("Ads");

		setDefaultSortColumn(AdColumn.ADDED_DATE, SortDirection.SORT_DESC);
		setDisplayColumns(AdColumn.values());

		disablePaging();
	}

	@Override
	protected void fetchData() {
		articleService.getAds(getArgMap(), getCallback());
	}

	@Override
	protected void setColumns() {
		for (AdColumn col : getDisplayColumns()) {
			switch (col) {
			case ADDED_BY:
				addCompositeWidgetColumn(col, new WidgetCellCreator<Ad>() {
					@Override
					protected Widget createWidget(Ad item) {
						return new Hyperlink(item.getAddedByFirstName() + " " + item.getAddedByLastName(), PageUrl.user(item.getAddedById()));
					}
				});
				break;
			case ADDED_DATE:
				addDateColumn(col, new ValueGetter<Date, Ad>() {
					@Override
					public Date get(Ad item) {
						return item.getAddedDate();
					}
				});
				break;
			case TITLE:
				addCompositeWidgetColumn(col, new WidgetCellCreator<Ad>() {
					@Override
					protected Widget createWidget(final Ad item) {
						return new ClickLabel(item.getTitle(), new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								new AdEditDialog(item, new Command() {
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
				addDateColumn(col, new ValueGetter<Date, Ad>() {
					@Override
					public Date get(Ad item) {
						return item.getEndDate();
					}
				});
				break;
			case CLICK_COUNT:
				addNumberColumn(col, new ValueGetter<Number, Ad>() {
					@Override
					public Number get(Ad item) {
						return item.getClickCount();
					}
				}).setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
				break;
			case IMAGE:
				addCompositeWidgetColumn(col, new WidgetCellCreator<Ad>() {
					@Override
					protected Widget createWidget(final Ad item) {
						final EditableImage image = new EditableImage(DocumentLinkType.AD, item.getId(), item.getDocumentId(), true);
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
				addDateColumn(col, new ValueGetter<Date, Ad>() {
					@Override
					public Date get(Ad item) {
						return item.getStartDate();
					}
				});
				break;
			case URL:
				addWidgetColumn(col, new WidgetCellCreator<Ad>() {
					@Override
					protected Widget createWidget(Ad item) {
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
