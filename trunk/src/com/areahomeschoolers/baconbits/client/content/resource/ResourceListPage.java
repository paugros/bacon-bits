package com.areahomeschoolers.baconbits.client.content.resource;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.ResourceService;
import com.areahomeschoolers.baconbits.client.rpc.service.ResourceServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.widgets.TilePanel;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ResourceArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Resource;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.VerticalPanel;

public final class ResourceListPage implements Page {
	private ResourceServiceAsync resourceService = (ResourceServiceAsync) ServiceCache.getService(ResourceService.class);
	private ArgMap<ResourceArg> args = new ArgMap<ResourceArg>();
	private TilePanel fp = new TilePanel();

	public ResourceListPage(final VerticalPanel page) {
		final String title = "Resources";

		if (!Common.isNullOrBlank(Url.getParameter("tagId"))) {
			args.put(ResourceArg.HAS_TAGS, Url.getIntListParameter("tagId"));
		}

		Hyperlink home = new Hyperlink("Home", PageUrl.home());
		Hyperlink cat = new Hyperlink("Resources By Type", PageUrl.tagGroup("RESOURCE"));
		String ccText = home.toString() + "&nbsp;>&nbsp;" + cat.toString();
		if (!Common.isNullOrBlank(Url.getParameter("tagId"))) {
			ccText += "&nbsp;>&nbsp;" + URL.decode(Url.getParameter("tn"));
		} else {
			ccText += "&nbsp;>&nbsp;Resources";
		}
		HTML cc = new HTML(ccText);
		cc.addStyleName("largeText");
		page.add(cc);

		page.add(fp);
		Application.getLayout().setPage(title, page);
		populate();
	}

	private void populate() {
		resourceService.list(args, new Callback<ArrayList<Resource>>() {
			@Override
			protected void doOnSuccess(ArrayList<Resource> result) {
				fp.clear();

				for (Resource r : result) {
					fp.add(new ResourceTile(r), r.getId());
				}
			}
		});
	}

}
