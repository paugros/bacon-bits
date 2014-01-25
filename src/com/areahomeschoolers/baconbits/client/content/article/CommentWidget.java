package com.areahomeschoolers.baconbits.client.content.article;

import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.shared.dto.Document;
import com.areahomeschoolers.baconbits.shared.dto.NewsBulletinComment;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CommentWidget extends Composite {
	public CommentWidget(NewsBulletinComment comment) {
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

		String header = comment.getAddedBy() + " " + Formatter.formatDateTime(comment.getAddedDate());
		Label headerLabel = new Label(header);
		headerLabel.getElement().getStyle().setColor("#14457a");
		headerLabel.addStyleName("bold");
		mugVP.add(headerLabel);

		HTML html = new HTML(comment.getComment());
		html.setWidth(NewsItemWidget.NEWS_ITEM_WIDTH + "px");
		mugVP.add(html);

		initWidget(leftMugPanel);
	}
}
