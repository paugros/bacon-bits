package com.areahomeschoolers.baconbits.client.content.tag;

import java.util.Date;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.tag.TagTable.TagColumn;
import com.areahomeschoolers.baconbits.client.event.UploadCompleteHandler;
import com.areahomeschoolers.baconbits.client.rpc.service.TagService;
import com.areahomeschoolers.baconbits.client.rpc.service.TagServiceAsync;
import com.areahomeschoolers.baconbits.client.widgets.EditableImage;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTableColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.client.widgets.cellview.WidgetCellCreator;
import com.areahomeschoolers.baconbits.shared.dto.Arg.TagArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Document.DocumentLinkType;
import com.areahomeschoolers.baconbits.shared.dto.Tag;

import com.google.gwt.user.client.ui.Widget;

public final class TagTable extends EntityCellTable<Tag, TagArg, TagColumn> {
	public enum TagColumn implements EntityCellTableColumn<TagColumn> {
		IMAGE(""), NAME("Name"), ADDED_DATE("Added");

		private String title;

		TagColumn(String title) {
			this.title = title;
		}

		@Override
		public String getTitle() {
			return title;
		}
	}

	private TagServiceAsync tagService = (TagServiceAsync) ServiceCache.getService(TagService.class);

	public TagTable(ArgMap<TagArg> args) {
		this();
		setArgMap(args);
	}

	private TagTable() {
		setTitle("Tags");

		setDefaultSortColumn(TagColumn.ADDED_DATE, SortDirection.SORT_DESC);
		setDisplayColumns(TagColumn.values());

		disablePaging();
	}

	@Override
	protected void fetchData() {
		tagService.list(getArgMap(), getCallback());
	}

	@Override
	protected void setColumns() {
		for (TagColumn col : getDisplayColumns()) {
			switch (col) {
			case IMAGE:
				addCompositeWidgetColumn(col, new WidgetCellCreator<Tag>() {
					@Override
					protected Widget createWidget(final Tag item) {
						final EditableImage image = new EditableImage(DocumentLinkType.TAG, item.getId(), item.getImageId(), true);
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
			case NAME:
				addTextColumn(col, new ValueGetter<String, Tag>() {
					@Override
					public String get(Tag item) {
						return item.getName();
					}
				});
				break;
			case ADDED_DATE:
				addDateColumn(col, new ValueGetter<Date, Tag>() {
					@Override
					public Date get(Tag item) {
						return item.getAddedDate();
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
