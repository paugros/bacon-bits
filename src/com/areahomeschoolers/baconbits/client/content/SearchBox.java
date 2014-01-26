package com.areahomeschoolers.baconbits.client.content;

import java.util.ArrayList;
import java.util.List;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.widgets.HtmlSuggestion;
import com.areahomeschoolers.baconbits.client.widgets.ServerSuggestOracle;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestBox.DefaultSuggestionDisplay;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.ValueBoxBase;

public final class SearchBox extends Composite {
	private final SuggestBox searchSuggestBox;
	private HtmlSuggestion currentSuggestion;

	public SearchBox() {
		HorizontalPanel hPanel = new HorizontalPanel();
		initWidget(hPanel);
		hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hPanel.setSpacing(4);

		List<String> types = new ArrayList<String>();
		if (Application.isAuthenticated()) {
			types.add("User");
		}
		types.add("Event");
		types.add("Article");
		types.add("Book");
		final ServerSuggestOracle oracle = new ServerSuggestOracle(types);
		searchSuggestBox = new SuggestBox(oracle);
		searchSuggestBox.setWidth("200px");
		searchSuggestBox.setAutoSelectEnabled(false);

		final ValueBoxBase<String> textBox = searchSuggestBox.getValueBox();
		// textBox.getElement().getStyle().setPadding(3, Unit.PX);
		textBox.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				currentSuggestion = null;
			}
		});
		searchSuggestBox.addSelectionHandler(new SelectionHandler<Suggestion>() {
			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				textBox.setFocus(true);
				HtmlSuggestion suggestion = (HtmlSuggestion) event.getSelectedItem();
				currentSuggestion = suggestion;
				loadEntityViewPage(suggestion);
			}
		});

		textBox.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				switch (event.getNativeKeyCode()) {
				case KeyCodes.KEY_ENTER:
					Scheduler.get().scheduleDeferred(new Command() {
						@Override
						public void execute() {
							if (currentSuggestion != null) {
								loadEntityViewPage(currentSuggestion);
							}
						}
					});
					break;
				case KeyCodes.KEY_ESCAPE:
					hideSuggestions();
					currentSuggestion = null;
					break;
				case KeyCodes.KEY_TAB:
					hideSuggestions();
					break;
				}
			}
		});

		Image icon = new Image(MainImageBundle.INSTANCE.search());
		hPanel.add(icon);
		icon.getElement().getStyle().setMarginTop(4, Unit.PX);
		hPanel.add(searchSuggestBox);
		hPanel.setCellVerticalAlignment(icon, HasVerticalAlignment.ALIGN_MIDDLE);
	}

	public void reset() {
		searchSuggestBox.setText("");
	}

	private void hideSuggestions() {
		DefaultSuggestionDisplay display = (DefaultSuggestionDisplay) searchSuggestBox.getSuggestionDisplay();
		display.hideSuggestions();
	}

	private void loadEntityViewPage(HtmlSuggestion suggestion) {
		String entityType = suggestion.getEntityType();

		String url = "page=" + entityType + "&" + entityType.substring(0, 1).toLowerCase() + entityType.substring(1) + "Id=" + suggestion.getEntityId();
		History.newItem(url);
	}
}
