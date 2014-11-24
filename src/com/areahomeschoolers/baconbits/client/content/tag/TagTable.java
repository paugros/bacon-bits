package com.areahomeschoolers.baconbits.client.content.tag;

import java.util.Date;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.tag.TagTable.TagColumn;
import com.areahomeschoolers.baconbits.client.event.ConfirmHandler;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.event.UploadCompleteHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.TagService;
import com.areahomeschoolers.baconbits.client.rpc.service.TagServiceAsync;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.ConfirmDialog;
import com.areahomeschoolers.baconbits.client.widgets.EditableImage;
import com.areahomeschoolers.baconbits.client.widgets.EntityEditDialog;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable.LabelColumnWidth;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTableColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.client.widgets.cellview.WidgetCellCreator;
import com.areahomeschoolers.baconbits.shared.dto.Arg.TagArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Document.DocumentLinkType;
import com.areahomeschoolers.baconbits.shared.dto.Tag;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public final class TagTable extends EntityCellTable<Tag, TagArg, TagColumn> {
	public enum TagColumn implements EntityCellTableColumn<TagColumn> {
		IMAGE(""), NAME("Name"), ADDED_DATE("Added"), USES("Uses"), DELETE("Delete");

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
			case DELETE:
				addCompositeWidgetColumn(col, new WidgetCellCreator<Tag>() {
					@Override
					protected Widget createWidget(final Tag item) {
						ClickLabel w = new ClickLabel("X", new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								ConfirmDialog.confirm("Really delete the " + item.getName() + " tag? This will remove it from " + item.getCount() + " items.",
										new ConfirmHandler() {
											@Override
											public void onConfirm() {
												removeItem(item, true, false);
												tagService.delete(item.getId(), new Callback<Void>(false) {
													@Override
													protected void doOnSuccess(Void result) {
													}
												});
											}
										});
							}
						});

						return w;
					}
				});
				break;
			case USES:
				addNumberColumn(col, new ValueGetter<Number, Tag>() {
					@Override
					public Number get(Tag item) {
						return item.getCount();
					}
				});
				break;
			case NAME:
				addCompositeWidgetColumn(col, new WidgetCellCreator<Tag>() {
					@Override
					protected Widget createWidget(final Tag item) {
						final ClickLabel cl = new ClickLabel(item.getName(), new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								final EntityEditDialog<Tag> dialog = new EntityEditDialog<Tag>() {
									@Override
									protected Widget createContent() {
										FieldTable ft = new FieldTable();
										ft.setLabelColumnWidth(LabelColumnWidth.NONE);

										final TextBox nameInput = new TextBox();
										nameInput.setMaxLength(50);
										nameInput.addKeyDownHandler(new KeyDownHandler() {
											@Override
											public void onKeyDown(KeyDownEvent event) {
												if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
													form.submit();
												}
											}
										});
										FormField nameField = form.createFormField("Name:", nameInput);
										nameField.setInitializer(new Command() {
											@Override
											public void execute() {
												nameInput.setText(item.getName());
											}
										});
										nameField.setDtoUpdater(new Command() {
											@Override
											public void execute() {
												item.setName(nameInput.getText());
											}
										});
										ft.addField(nameField);

										Scheduler.get().scheduleDeferred(new ScheduledCommand() {
											@Override
											public void execute() {
												nameInput.setFocus(true);
												nameInput.setCursorPos(item.getName().length());
											}
										});

										return ft;
									}
								};
								dialog.addFormSubmitHandler(new FormSubmitHandler() {
									@Override
									public void onFormSubmit(FormField formField) {
										Tag tag = dialog.getEntity();
										addItem(tag);
										redraw();
										tagService.save(tag, new Callback<Void>(false) {
											@Override
											protected void doOnSuccess(Void result) {
											}
										});
									}
								});
								dialog.setText("Edit Tag");
								dialog.show(item);
								int y = event.getY() + 15;
								if (y + 128 > Window.getClientHeight()) {
									y -= 128;
								}
								dialog.setPopupPosition(event.getX() - 15, y);
							}
						});
						return cl;
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
