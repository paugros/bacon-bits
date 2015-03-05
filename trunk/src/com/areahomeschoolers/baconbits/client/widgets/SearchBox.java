package com.areahomeschoolers.baconbits.client.widgets;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.areahomeschoolers.baconbits.client.event.ParameterHandler;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagType;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestBox.DefaultSuggestionDisplay;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase;

public final class SearchBox extends Composite {
	private final SuggestBox searchSuggestBox;
	private HtmlSuggestion currentSuggestion;
	private TextBox textBox = new TextBox();
	private ParameterHandler<HtmlSuggestion> selectionHandler;

	public SearchBox(ParameterHandler<HtmlSuggestion> onSelection, EnumSet<TagType> types) {
		selectionHandler = onSelection;
		HorizontalPanel hPanel = new HorizontalPanel();
		initWidget(hPanel);
		hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hPanel.setSpacing(4);

		List<String> stringTypes = new ArrayList<>();
		for (TagType type : types) {
			stringTypes.add(Common.ucWords(type.toString()));
		}
		final ServerSuggestOracle oracle = new ServerSuggestOracle(stringTypes);

		textBox.setVisibleLength(20);
		searchSuggestBox = new SuggestBox(oracle, textBox);
		textBox.setStyleName("searchBox");
		textBox.getElement().setAttribute("placeholder", "Search...");

		searchSuggestBox.setAutoSelectEnabled(false);

		final ValueBoxBase<String> valueBox = searchSuggestBox.getValueBox();
		valueBox.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				currentSuggestion = null;
			}
		});
		searchSuggestBox.addSelectionHandler(new SelectionHandler<Suggestion>() {
			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				valueBox.setFocus(true);
				HtmlSuggestion suggestion = (HtmlSuggestion) event.getSelectedItem();
				currentSuggestion = suggestion;
				selectionHandler.execute(suggestion);
			}
		});

		valueBox.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				switch (event.getNativeKeyCode()) {
				case KeyCodes.KEY_ENTER:
					Scheduler.get().scheduleDeferred(new Command() {
						@Override
						public void execute() {
							if (currentSuggestion != null) {
								selectionHandler.execute(currentSuggestion);
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

		hPanel.add(searchSuggestBox);
	}

	public void addBlurHandler(BlurHandler bh) {
		textBox.addBlurHandler(bh);
	}

	public void setFocus(boolean focus) {
		textBox.setFocus(focus);
	}

	public void setSelectionHandler(ParameterHandler<HtmlSuggestion> handler) {
		selectionHandler = handler;
	}

	private void hideSuggestions() {
		DefaultSuggestionDisplay display = (DefaultSuggestionDisplay) searchSuggestBox.getSuggestionDisplay();
		display.hideSuggestions();
	}

}
