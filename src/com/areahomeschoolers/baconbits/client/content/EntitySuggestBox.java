package com.areahomeschoolers.baconbits.client.content;

import com.areahomeschoolers.baconbits.client.event.ParameterHandler;
import com.areahomeschoolers.baconbits.client.widgets.HtmlSuggestion;
import com.areahomeschoolers.baconbits.client.widgets.ServerSuggestOracle;
import com.areahomeschoolers.baconbits.shared.dto.Data;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestBox.DefaultSuggestionDisplay;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.ValueBoxBase;

public class EntitySuggestBox extends Composite {
	private final SuggestBox suggestBox;
	private final ServerSuggestOracle oracle;
	private boolean selecting, clearOnFocus;
	private ParameterHandler<Integer> selectionHandler;
	private ParameterHandler<SuggestBox> resetHandler;

	public EntitySuggestBox(String suggestType) {
		oracle = new ServerSuggestOracle(suggestType);

		suggestBox = new SuggestBox(oracle);
		initWidget(suggestBox);
		suggestBox.setWidth("170px");

		final ValueBoxBase<String> textBox = suggestBox.getValueBox();
		// textBox.getElement().getStyle().setPadding(3, Unit.PX);
		suggestBox.addSelectionHandler(new SelectionHandler<Suggestion>() {
			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				selecting = true;
				HtmlSuggestion suggestion = (HtmlSuggestion) event.getSelectedItem();

				if (selectionHandler != null) {
					selectionHandler.execute(suggestion.getEntityId());
				}
			}
		});

		textBox.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				if (!selecting && clearOnFocus) {
					textBox.setText("");
				}
			}
		});

		textBox.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				Timer t = new Timer() {
					@Override
					public void run() {
						if (!selecting) {
							reset();
						}
					}
				};

				t.schedule(100);
			}
		});

		suggestBox.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				switch (event.getNativeKeyCode()) {
				case KeyCodes.KEY_ENTER:
					break;
				case KeyCodes.KEY_ESCAPE:
					reset();
					break;
				}
			}
		});
	}

	public ValueBoxBase<String> getTextBox() {
		return suggestBox.getValueBox();
	}

	public boolean isClearOnFocus() {
		return clearOnFocus;
	}

	public void reset() {
		DefaultSuggestionDisplay display = (DefaultSuggestionDisplay) suggestBox.getSuggestionDisplay();
		display.hideSuggestions();

		if (resetHandler != null) {
			resetHandler.execute(suggestBox);
		}
	}

	public void setClearOnFocus(boolean clearOnFocus) {
		this.clearOnFocus = clearOnFocus;
	}

	public void setOptions(Data options) {
		oracle.setOptions(options);
	}

	public void setResetHandler(ParameterHandler<SuggestBox> handler) {
		resetHandler = handler;
		reset();
	}

	public void setSelectionHandler(ParameterHandler<Integer> handler) {
		selectionHandler = handler;
	}

	public void setText(String text) {
		suggestBox.setText(text);
	}

}
