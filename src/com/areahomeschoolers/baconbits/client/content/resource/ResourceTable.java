package com.areahomeschoolers.baconbits.client.content.resource;

import java.util.Date;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.resource.ResourceTable.ResourceColumn;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.rpc.service.ResourceService;
import com.areahomeschoolers.baconbits.client.rpc.service.ResourceServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.DefaultHyperlink;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTableColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.client.widgets.cellview.WidgetCellCreator;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ResourceArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Resource;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Widget;

public final class ResourceTable extends EntityCellTable<Resource, ResourceArg, ResourceColumn> {
	public enum ResourceColumn implements EntityCellTableColumn<ResourceColumn> {
		NAME("Title"), ADDED_DATE("Added"), VIEW_COUNT("Views"), CLICK_COUNT("Clicks"), IMPRESSIONS("Impressions");

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

	public void addStatusFilterBox() {
		final DefaultListBox filterBox = getTitleBar().addFilterListControl();
		filterBox.addItem("Active");
		filterBox.addItem("Inactive");
		filterBox.addItem("All");
		filterBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				switch (filterBox.getSelectedIndex()) {
				case 0:
					for (Resource resource : getFullList()) {
						setItemVisible(resource, resource.isActive(), false, false, false);
					}
					refreshForCurrentState();
					break;
				case 1:
					for (Resource resource : getFullList()) {
						setItemVisible(resource, !resource.isActive(), false, false, false);
					}
					refreshForCurrentState();
					break;
				case 2:
					showAllItems();
					break;
				}
			}
		});
		filterBox.setSelectedIndex(0);

		addDataReturnHandler(new DataReturnHandler() {
			@Override
			public void onDataReturn() {
				filterBox.fireEvent(new ChangeEvent() {
				});
			}
		});
	}

	@Override
	protected void fetchData() {
		resourceService.list(getArgMap(), getCallback());
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
				addWidgetColumn(col, new WidgetCellCreator<Resource>() {
					@Override
					protected Widget createWidget(Resource item) {
						return new DefaultHyperlink(item.getName(), PageUrl.resource(item.getId()));
					}
				});
				break;
			case IMPRESSIONS:
				addNumberColumn(col, new ValueGetter<Number, Resource>() {
					@Override
					public Number get(Resource item) {
						return item.getImpressions();
					}
				});
				break;
			case VIEW_COUNT:
				addNumberColumn(col, new ValueGetter<Number, Resource>() {
					@Override
					public Number get(Resource item) {
						return item.getViewCount();
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
			default:
				new AssertionError();
				break;
			}
		}
	}

}
