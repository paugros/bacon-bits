package com.areahomeschoolers.baconbits.client.content.tag;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.resource.Tile;
import com.areahomeschoolers.baconbits.client.content.resource.TileConfig;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.TagService;
import com.areahomeschoolers.baconbits.client.rpc.service.TagServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.shared.dto.Arg.TagArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Tag;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public final class TagGroupPage implements Page {
	private ArgMap<TagArg> args = new ArgMap<TagArg>();
	private TagServiceAsync tagService = (TagServiceAsync) ServiceCache.getService(TagService.class);
	private FlowPanel fp = new FlowPanel();

	public TagGroupPage(final VerticalPanel page) {
		final String title = "Categories";
		fp.setWidth("100%");

		args.put(TagArg.MAPPING_TYPE, TagMappingType.RESOURCE.toString());
		args.put(TagArg.GET_COUNTS);

		tagService.list(args, new Callback<ArrayList<Tag>>() {
			@Override
			protected void doOnSuccess(ArrayList<Tag> result) {
				for (Tag tag : result) {
					TileConfig uc = new TileConfig().setText(tag.getName()).setCount(tag.getCount());
					if (tag.getImageId() != null) {
						uc.setImageId(tag.getImageId());
					} else {
						uc.setImageResource(MainImageBundle.INSTANCE.defaultImage());
					}
					uc.setUrl(PageUrl.resourceManagement());
					uc.setColor(0xf28e76);
					Tile tile = new Tile(uc);

					// wrapper
					SimplePanel sp = new SimplePanel(tile);
					sp.getElement().getStyle().setMargin(10, Unit.PX);
					sp.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

					fp.add(sp);
				}
			}
		});

		page.add(fp);

		Application.getLayout().setPage(title, page);
	}
}
