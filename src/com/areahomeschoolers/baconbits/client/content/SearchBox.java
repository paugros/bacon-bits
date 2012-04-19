package com.areahomeschoolers.baconbits.client.content;

import com.areahomeschoolers.baconbits.client.widgets.HtmlSuggestion;
import com.areahomeschoolers.baconbits.client.widgets.ServerSuggestOracle;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestBox.DefaultSuggestionDisplay;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBoxBase;

public final class SearchBox extends Composite {
	private final SuggestBox searchSuggestBox;
	private final Button submitButton;
	private HtmlSuggestion currentSuggestion;

	public SearchBox() {
		HorizontalPanel hPanel = new HorizontalPanel();
		initWidget(hPanel);
		hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hPanel.setSpacing(4);

		final ServerSuggestOracle oracle = new ServerSuggestOracle("Account");
		searchSuggestBox = new SuggestBox(oracle);
		searchSuggestBox.setWidth("200px");
		searchSuggestBox.setAutoSelectEnabled(false);

		final TextBoxBase textBox = searchSuggestBox.getTextBox();
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
			}
		});

		textBox.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				final boolean isControlKeyDown = event.isControlKeyDown();

				switch (event.getNativeKeyCode()) {
				case KeyCodes.KEY_ENTER:
					Scheduler.get().scheduleDeferred(new Command() {
						@Override
						public void execute() {
							if (currentSuggestion != null) {
								loadEntityViewPage(currentSuggestion);
							} else {
								loadSearchResults(isControlKeyDown);
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

		submitButton = new Button("Search", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				loadSearchResults(event.isControlKeyDown());
			}
		});

		hPanel.add(searchSuggestBox);
		hPanel.add(submitButton);
	}

	public void reset() {
		searchSuggestBox.getTextBox().setText("");
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

	private void loadSearchResults(boolean newTab) {
	}
}
