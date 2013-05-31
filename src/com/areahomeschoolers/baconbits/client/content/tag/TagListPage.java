package com.areahomeschoolers.baconbits.client.content.tag;

import java.util.ArrayList;
import java.util.List;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.TagService;
import com.areahomeschoolers.baconbits.client.rpc.service.TagServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.widgets.AlertDialog;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.Arg.TagArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Tag;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TagListPage {
	private VerticalPanel page = Application.getLayout().getNewPagePanel();
	private TagServiceAsync tagService = (TagServiceAsync) ServiceCache.getService(TagService.class);
	private TagMappingType tagMappingType;
	private int entityId = Url.getIntegerParameter("entityId");
	private List<Tag> selectedTags;
	private List<Tag> allTags;

	public TagListPage(TagMappingType tagMappingType, int entityId) {
		this.tagMappingType = tagMappingType;
		this.entityId = entityId;

		ArgMap<TagArg> selectedArgs = new ArgMap<TagArg>(TagArg.MAPPING_TYPE, tagMappingType.toString());
		selectedArgs.put(TagArg.ENTITY_ID, entityId);
		tagService.list(selectedArgs, new Callback<ArrayList<Tag>>() {
			@Override
			protected void doOnSuccess(ArrayList<Tag> result) {
				selectedTags = result;
				if (allTags != null) {
					initialize();
				}
			}
		});

		tagService.list(new ArgMap<TagArg>(), new Callback<ArrayList<Tag>>() {
			@Override
			protected void doOnSuccess(ArrayList<Tag> result) {
				allTags = result;
				if (selectedTags != null) {
					initialize();
				}
			}
		});

	}

	private void initialize() {
		VerticalPanel header = new VerticalPanel();
		Label title = new Label("All Tags / Interests");
		title.addStyleName("hugeText");
		String item = tagMappingType.toString().toLowerCase();
		String txt = "Select all that apply ";
		if (tagMappingType != TagMappingType.USER) {
			txt += "to the " + item + " you were viewing";
		}
		Label sub = new Label(txt);
		sub.getElement().getStyle().setMarginLeft(10, Unit.PX);
		ClickLabel back = new ClickLabel("Return to previous page", new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				History.back();
			}
		});
		back.getElement().getStyle().setMarginLeft(10, Unit.PX);
		header.add(title);
		header.add(sub);
		header.add(back);
		page.add(header);

		FlexTable ft = new FlexTable();
		ft.addStyleName("boxedBlurb");
		ft.setCellPadding(3);

		int cols = 5;
		allTags.removeAll(selectedTags);
		allTags.addAll(selectedTags);

		for (int i = 0; i < allTags.size(); i++) {
			final Tag tag = allTags.get(i);
			CheckBox cb = new CheckBox(tag.getName());
			if (tag.getMappingId() > 0) {
				cb.setValue(true);
			}

			cb.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					if (event.getValue()) {
						if (selectedTags.size() == Constants.MAXIMUM_TAG_COUNT) {
							AlertDialog.alert("The maximum of " + Constants.MAXIMUM_TAG_COUNT + " tags are already selected.");
							return;
						}
						tag.setMappingType(tagMappingType);
						tag.setEntityId(entityId);
						tagService.addMapping(tag, new Callback<Tag>(false) {
							@Override
							protected void doOnSuccess(Tag result) {
								selectedTags.add(result);
							}
						});
					} else {
						tagService.deleteMapping(tag, new Callback<Void>(false) {
							@Override
							protected void doOnSuccess(Void result) {
								selectedTags.remove(tag);
							}
						});
					}
				}
			});

			if (i % cols == 0) {
				ft.insertRow(ft.getRowCount());
			}

			int row = ft.getRowCount() - 1;
			int cell = ft.getCellCount(row);

			ft.setWidget(row, cell, cb);
		}

		page.add(ft);

		Application.getLayout().setPage("View All Tags", page);
	}
}
