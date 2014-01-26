package com.areahomeschoolers.baconbits.client.content.article;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.event.ConfirmHandler;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleService;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.ConfirmDialog;
import com.areahomeschoolers.baconbits.client.widgets.Fader;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.shared.dto.Document;
import com.areahomeschoolers.baconbits.shared.dto.NewsBulletinComment;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CommentWidget extends Composite {
	private final ArticleServiceAsync newsService = (ArticleServiceAsync) ServiceCache.getService(ArticleService.class);

	public CommentWidget(final NewsBulletinComment comment) {
		PaddedPanel leftMugPanel = new PaddedPanel(5);
		Image userPhoto = new Image();
		int thumbNailPixelHeight = 60;
		int thumbNailPixelWidth = 60;

		int imageId = comment.getImageDocumentId();
		if (imageId > 0) {
			userPhoto.setUrl(Document.toUrl(imageId));
		} else {
			userPhoto.setResource(MainImageBundle.INSTANCE.pixel());
		}
		userPhoto.setWidth(thumbNailPixelWidth - 10 + "px");
		userPhoto.setHeight(thumbNailPixelHeight - 10 + "px");
		leftMugPanel.add(userPhoto);

		VerticalPanel mugVP = new VerticalPanel();
		leftMugPanel.add(mugVP);

		VerticalPanel vp = new VerticalPanel();
		vp.getElement().getStyle().setMarginBottom(10, Unit.PX);

		PaddedPanel pp = new PaddedPanel();
		String header = Formatter.formatDateTime(comment.getAddedDate());
		Label headerLabel = new Label(header);
		headerLabel.getElement().getStyle().setColor("#333333");
		InlineHyperlink userLink = new InlineHyperlink(comment.getAddedBy(), PageUrl.user(comment.getUserId()));
		headerLabel.addStyleName("bold");
		userLink.addStyleName("bold");
		userLink.getElement().getStyle().setColor("#333333");
		pp.add(userLink);
		pp.add(headerLabel);
		mugVP.add(pp);

		HTML html = new HTML(comment.getComment());
		html.setWidth(NewsItemWidget.NEWS_ITEM_WIDTH + "px");
		mugVP.add(html);

		if (Application.administratorOfCurrentOrg()) {
			ClickLabel x = new ClickLabel("x", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					ConfirmDialog.confirm("Really remove this comment?", new ConfirmHandler() {
						@Override
						public void onConfirm() {
							Fader.fadeObjectOut(CommentWidget.this, new Command() {
								@Override
								public void execute() {
									CommentWidget.this.removeFromParent();
								}
							});
							newsService.hideComment(comment.getId(), new Callback<Void>(false) {
								@Override
								protected void doOnSuccess(Void result) {
								}
							});
						}
					});
				}
			});
			leftMugPanel.add(x);
		}

		initWidget(leftMugPanel);
	}
}
