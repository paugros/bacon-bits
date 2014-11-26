package com.areahomeschoolers.baconbits.client.content.tag;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.resource.Tile;
import com.areahomeschoolers.baconbits.client.content.resource.TileConfig;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.TagService;
import com.areahomeschoolers.baconbits.client.rpc.service.TagServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.widgets.TilePanel;
import com.areahomeschoolers.baconbits.shared.dto.Arg.TagArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Tag;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;

import com.google.gwt.user.client.ui.VerticalPanel;

public final class TagGroupPage implements Page {
	private ArgMap<TagArg> args = new ArgMap<TagArg>();
	private TagServiceAsync tagService = (TagServiceAsync) ServiceCache.getService(TagService.class);
	private TilePanel fp = new TilePanel();
	private TagMappingType type = null;

	public TagGroupPage(final VerticalPanel page) {

		try {
			type = TagMappingType.valueOf(Url.getParameter("type"));
		} catch (Exception e) {
		}

		if (type == null) {
			new ErrorPage(PageError.PAGE_NOT_FOUND);
			return;
		}

		final String title = "Categories";
		fp.setWidth("100%");

		args.put(TagArg.MAPPING_TYPE, type.toString());
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
					String url = null;
					switch (type) {
					case ARTICLE:
						url = PageUrl.articleList() + "&tagIds=" + tag.getId();
						break;
					case BOOK:
						url = PageUrl.bookList() + "&tagIds=" + tag.getId();
						break;
					case EVENT:
						url = PageUrl.eventList() + "&tagIds=" + tag.getId();
						break;
					case RESOURCE:
						url = PageUrl.resourceManagement() + "&tagIds=" + tag.getId();
						break;
					case USER:
						url = PageUrl.userList() + "&tagIds=" + tag.getId();
						break;
					default:
						break;
					}
					uc.setUrl(url);
					uc.setColor(0xf28e76);
					Tile tile = new Tile(uc);

					fp.add(tile);
				}
			}
		});

		page.add(fp);

		Application.getLayout().setPage(title, page);
	}
}
