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
import com.areahomeschoolers.baconbits.client.util.ClientUtils;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.widgets.AddLink;
import com.areahomeschoolers.baconbits.client.widgets.TilePanel;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.TagArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Tag;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
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
						uc.setImage(new Image(ClientUtils.createDocumentUrl(tag.getImageId(), tag.getImageExtension())));
					} else {
						uc.setImage(new Image(MainImageBundle.INSTANCE.citrusGirl()));
					}
					String url = null;
					String extras = "&tagId=" + tag.getId() + "&tn=" + URL.encode(tag.getName());
					switch (type) {
					case ARTICLE:
						url = PageUrl.articleList() + extras;
						break;
					case BOOK:
						url = PageUrl.bookList() + extras;
						break;
					case EVENT:
						url = PageUrl.eventList() + extras;
						break;
					case RESOURCE:
						url = PageUrl.resourceList() + extras;
						break;
					case USER:
						url = PageUrl.userList() + extras;
						break;
					default:
						break;
					}
					uc.setUrl(url);
					uc.setColor(type.getColor());
					Tile tile = new Tile(uc);

					fp.add(tile);
				}
			}
		});

		Hyperlink home = new Hyperlink("Home", PageUrl.home());
		String typeText = type.equals(TagMappingType.USER) ? "Interests" : "Type";
		String ccText = home.toString() + "&nbsp;>&nbsp;" + type.getName() + " By " + typeText;

		HTML cc = new HTML(ccText);
		cc.addStyleName("largeText");
		page.add(cc);

		if (Application.isAuthenticated()) {
			String url = null;
			switch (type) {
			case ARTICLE:
				if (Application.memberOf(33)) {
					url = PageUrl.article(0);
				}
				break;
			case BOOK:
				break;
			case EVENT:
				url = PageUrl.event(0);
				break;
			case RESOURCE:
				url = PageUrl.resource(0);
				break;
			case USER:
				if (Application.isSystemAdministrator()) {
					url = PageUrl.user(0);
				}
				break;
			default:
				break;
			}

			if (url != null) {
				String name = Common.ucWords(type.getName()).substring(0, type.getName().length() - 1);
				AddLink link = new AddLink("Add " + name, url);
				link.getElement().getStyle().setMarginLeft(10, Unit.PX);
				page.add(link);
			}
		}

		page.add(fp);

		Application.getLayout().setPage(title, page);
	}
}
