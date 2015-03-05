package com.areahomeschoolers.baconbits.client.content;

import java.util.EnumSet;

import com.areahomeschoolers.baconbits.client.event.ParameterHandler;
import com.areahomeschoolers.baconbits.client.widgets.HtmlSuggestion;
import com.areahomeschoolers.baconbits.client.widgets.SearchBox;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagType;

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;

public final class HeaderSearchBox extends Composite {
	private SearchBox searchBox = new SearchBox(new ParameterHandler<HtmlSuggestion>() {
		@Override
		public void execute(HtmlSuggestion item) {
			loadEntityViewPage(item);
		}
	}, EnumSet.allOf(TagType.class));
	private Command selectionHandler;

	public HeaderSearchBox() {
		HorizontalPanel hPanel = new HorizontalPanel();
		initWidget(hPanel);
		hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hPanel.setSpacing(4);

		hPanel.add(searchBox);
	}

	public void addBlurHandler(BlurHandler bh) {
		searchBox.addBlurHandler(bh);
	}

	public void setFocus(boolean focus) {
		searchBox.setFocus(focus);
	}

	public void setSelectionHandler(Command selectionHandler) {
		this.selectionHandler = selectionHandler;
	}

	private void loadEntityViewPage(HtmlSuggestion suggestion) {
		if (selectionHandler != null) {
			selectionHandler.execute();
		}
		String entityType = suggestion.getEntityType();

		String url = "page=" + entityType + "&" + entityType.substring(0, 1).toLowerCase() + entityType.substring(1) + "Id=" + suggestion.getEntityId();
		History.newItem(url);
	}
}
