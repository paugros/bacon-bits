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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AdsMiniModule extends Composite {
	private ArticleServiceAsync articleService = (ArticleServiceAsync) ServiceCache.getService(ArticleService.class);
	private VerticalPanel sp = new VerticalPanel();
	private ArgMap<AdArg> args = new ArgMap<AdArg>(Status.ACTIVE);
	private Timer t = new Timer() {
		@Override
		public void run() {
			populate();
		}
	};

	public AdsMiniModule() {
		args.put(AdArg.RANDOM);
		args.put(AdArg.LIMIT, 2);
		args.put(AdArg.OWNING_ORG_ID, Application.getCurrentOrgId());

		populate();

		initWidget(sp);

	}

	private void populate() {
		articleService.getAds(args, new Callback<ArrayList<Ad>>(false) {
			@Override
			protected void doOnSuccess(ArrayList<Ad> result) {
				sp.clear();
				sp.setSpacing(8);
				sp.setStyleName("module");

				if (result.isEmpty()) {
					setVisible(false);
					removeFromParent();
					return;
				}

				VerticalPanel vp = new VerticalPanel();
				vp.setWidth("100%");
				sp.add(vp);
				Label linkLabel = new Label("HOMESCHOOL DEALS");
				linkLabel.addStyleName("moduleTitle");
				linkLabel.getElement().getStyle().setMarginBottom(8, Unit.PX);
				vp.add(linkLabel);

				for (final Ad ad : result) {
					if (ad.getDocumentId() != null) {
						Image image = new Image();
						image.setUrl(Document.toUrl(ad.getDocumentId()));

						Anchor link = new Anchor(SafeHtmlUtils.fromTrustedString(image.toString()), ad.getUrl());
						link.setTarget("_blank");
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

					HTML label = new HTML(ad.getDescription());
					label.getElement().getStyle().setMarginBottom(20, Unit.PX);
					label.getElement().getStyle().setPaddingLeft(10, Unit.PX);
					label.getElement().getStyle().setPaddingRight(10, Unit.PX);
					vp.add(label);
				}

				if (!t.isRunning()) {
					Application.scheduleRepeatingPageTimer(t, 10000);
				}
			}
		});
	}

}
