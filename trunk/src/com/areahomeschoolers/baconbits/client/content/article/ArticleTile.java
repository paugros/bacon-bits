package com.areahomeschoolers.baconbits.client.content.article;

import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.shared.dto.Article;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;

public class ArticleTile extends Composite {

	public ArticleTile(final Article item) {
		addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HistoryToken.set(PageUrl.article(item.getId()));
			}
		}, ClickEvent.getType());

		HorizontalPanel hp = new HorizontalPanel();
		int textWidth = 200;
		hp.setWidth((textWidth + 80) + "px");
		hp.setHeight("117px");
		hp.addStyleName("itemTile");
		hp.getElement().getStyle().setBackgroundColor(TagMappingType.ARTICLE.getColor());

		// Image i = new Image(Constants.DOCUMENT_URL_PREFIX + 1222);
		Image i = new Image(MainImageBundle.INSTANCE.logo());
		// i.getElement().getStyle().setBorderColor("#6b48f");
		// i.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		// i.getElement().getStyle().setBorderWidth(1, Unit.PX);

		hp.add(new HTML("<div style=\"width: 80px; margin-right: 10px; text-align: center;\">" + i.toString() + "</div>"));

		Hyperlink link = new Hyperlink(item.getTitle(), PageUrl.article(item.getId()));
		link.addStyleName("bold");
		// title/link
		String text = "<div style=\"overflow: hidden; width: " + textWidth + "px;\">" + link.toString() + "</div>";

		// description
		String d = new HTML(item.getArticle()).getText();
		text += "<div class=smallText style=\"overflow: hidden; max-height: 57px; width: " + textWidth + "px;\">" + d + "</div>";

		HTML h = new HTML(text);

		hp.add(h);
		hp.setCellHorizontalAlignment(h, HasHorizontalAlignment.ALIGN_LEFT);

		initWidget(hp);
	}

}