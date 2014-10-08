package com.areahomeschoolers.baconbits.client.content.minimodules;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleService;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleServiceAsync;
import com.areahomeschoolers.baconbits.shared.dto.Ad;
import com.areahomeschoolers.baconbits.shared.dto.Arg.AdArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Document;

import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AdsMiniModule extends Composite {
	private ArticleServiceAsync articleService = (ArticleServiceAsync) ServiceCache.getService(ArticleService.class);

	public AdsMiniModule() {
		final VerticalPanel sp = new VerticalPanel();

		ArgMap<AdArg> args = new ArgMap<AdArg>(Status.ACTIVE);
		args.put(AdArg.RANDOM);
		args.put(AdArg.LIMIT, 1);
		args.put(AdArg.OWNING_ORG_ID, Application.getCurrentOrgId());

		articleService.getAds(args, new Callback<ArrayList<Ad>>() {
			@Override
			protected void doOnSuccess(ArrayList<Ad> result) {
				if (result.isEmpty()) {
					setVisible(false);
					removeFromParent();
					return;
				}

				final Ad ad = result.get(0);
				sp.setSpacing(8);
				VerticalPanel vp = new VerticalPanel();
				vp.setWidth("100%");
				sp.setStyleName("module");
				sp.add(vp);

				if (ad.getDocumentId() != null) {
					Image image = new Image();
					image.setUrl(Document.toUrl(ad.getDocumentId()));

					Anchor link = new Anchor(SafeHtmlUtils.fromTrustedString(image.toString()), ad.getUrl());
					link.addDomHandler(new LoadHandler() {
						@Override
						public void onLoad(LoadEvent event) {
							System.out.println("I'm so loaded!");
						}
					}, LoadEvent.getType());

					link.addMouseDownHandler(new MouseDownHandler() {
						@Override
						public void onMouseDown(MouseDownEvent event) {
							// if (event.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
							// return;
							// }

							articleService.clickAd(ad.getId(), new Callback<Void>(false) {
								@Override
								protected void doOnSuccess(Void result) {

								}
							});
						}
					});
					vp.add(link);
					vp.setCellHorizontalAlignment(link, HasHorizontalAlignment.ALIGN_CENTER);
				}

				Label label = new Label(ad.getDescription());
				vp.add(label);
			}
		});

		initWidget(sp);
	}

}
