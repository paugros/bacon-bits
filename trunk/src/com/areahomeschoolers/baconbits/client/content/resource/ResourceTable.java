package com.areahomeschoolers.baconbits.client.content.resource;

import java.util.Date;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.resource.ResourceTable.ResourceColumn;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.service.ResourceService;
import com.areahomeschoolers.baconbits.client.rpc.service.ResourceServiceAsync;
import com.areahomeschoolers.baconbits.client.util.ClientUtils;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTableColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.client.widgets.cellview.WidgetCellCreator;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ResourceArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Resource;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public final class ResourceTable extends EntityCellTable<Resource, ResourceArg, ResourceColumn> {
	public enum ResourceColumn implements EntityCellTableColumn<ResourceColumn> {
		IMAGE("Image"), NAME("Title"), ADDED_DATE("Added"), END_DATE("End Date"), START_DATE("Start Date"), CLICK_COUNT("Click Count"), IS_AD("Ad");

		private String title;

		ResourceColumn(String title) {
			this.title = title;
		}

		@Override
		public String getTitle() {
			return title;
		}
	}

	private ResourceServiceAsync resourceService = (ResourceServiceAsync) ServiceCache.getService(ResourceService.class);

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
		resourceService.list(getArgMap(), getCallback());
	}

	@Override
	protected void setColumns() {
		for (ResourceColumn col : getDisplayColumns()) {
			switch (col) {
			case IS_AD:
				addTextColumn(col, new ValueGetter<String, Resource>() {
					@Override
					public String get(Resource item) {
						return item.getShowInAds() ? "Yes" : "No";
					}
				});
				break;
			case IMAGE:
				addWidgetColumn(col, new WidgetCellCreator<Resource>() {
					@Override
					protected Widget createWidget(Resource item) {
						Image image = new Image(MainImageBundle.INSTANCE.defaultSmall());
						if (item.getSmallImageId() != null) {
							image = new Image(ClientUtils.createDocumentUrl(item.getSmallImageId(), item.getImageExtension()));
						}

						return image;
					}
				});
				break;
			case ADDED_DATE:
				addDateColumn(col, new ValueGetter<Date, Resource>() {
					@Override
					public Date get(Resource item) {
						return item.getAddedDate();
					}
				});
				break;
			case NAME:
				addWidgetColumn(col, new WidgetCellCreator<Resource>() {
					@Override
					protected Widget createWidget(Resource item) {
						return new Hyperlink(item.getName(), PageUrl.resource(item.getId()));
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
			case START_DATE:
				addDateColumn(col, new ValueGetter<Date, Resource>() {
					@Override
					public Date get(Resource item) {
						return item.getStartDate();
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
