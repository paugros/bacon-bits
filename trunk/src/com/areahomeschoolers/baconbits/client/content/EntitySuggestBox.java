package com.areahomeschoolers.baconbits.client.content;

import com.areahomeschoolers.baconbits.client.event.ParameterHandler;
import com.areahomeschoolers.baconbits.client.widgets.HtmlSuggestion;
import com.areahomeschoolers.baconbits.client.widgets.ServerSuggestOracle;
import com.areahomeschoolers.baconbits.shared.dto.Data;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
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
	private ParameterHandler<HtmlSuggestion> selectionHandler;
	private ParameterHandler<SuggestBox> resetHandler;
	private Command submitWithoutSelectionCommand;

	public EntitySuggestBox(String suggestType) {
		oracle = new ServerSuggestOracle(suggestType);

		suggestBox = new SuggestBox(oracle);
		initWidget(suggestBox);
		suggestBox.setWidth("170px");

		final ValueBoxBase<String> textBox = suggestBox.getValueBox();
		suggestBox.addSelectionHandler(new SelectionHandler<Suggestion>() {
			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				selecting = true;
				HtmlSuggestion suggestion = (HtmlSuggestion) event.getSelectedItem();

				if (selectionHandler != null) {
					selectionHandler.execute(suggestion);
				}

				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						selecting = false;
					}
				});
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

		suggestBox.getValueBox().addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				switch (event.getNativeKeyCode()) {
				case KeyCodes.KEY_ENTER:
					if (!selecting) {
						submitWithoutSelectionCommand.execute();
					}

					// reset selecting status
					selecting = false;
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

	public String getValue() {
		return suggestBox.getValue();
	}

	public boolean isClearOnFocus() {
		return clearOnFocus;
	}

	public void reset() {
		DefaultSuggestionDisplay display = (DefaultSuggestionDisplay) suggestBox.getSuggestionDisplay();
		display.hideSuggestions();
		suggestBox.getValueBox().setText("");

		if (resetHandler != null) {
			resetHandler.execute(suggestBox);
		}
	}

	public void setAutoSelectEnabled(boolean enabled) {
		suggestBox.setAutoSelectEnabled(enabled);
	}

	public void setClearOnFocus(boolean clearOnFocus) {
		this.clearOnFocus = clearOnFocus;
	}

	public void setDisplayLimit(int limit) {
		suggestBox.setLimit(limit);
	}

	public void setFontSize(int fontSize) {
		oracle.setFontSize(fontSize);
	}

	public void setOptions(Data options) {
		oracle.setOptions(options);
	}

	public void setRequestLimit(int limit) {
		oracle.setNumberOfServerSuggestions(limit);
	}

	public void setResetHandler(ParameterHandler<SuggestBox> handler) {
		resetHandler = handler;
		reset();
	}

	public void setSelectionHandler(ParameterHandler<HtmlSuggestion> handler) {
		selectionHandler = handler;
	}

	public void setSubmitWithoutSelectionCommand(Command command) {
		submitWithoutSelectionCommand = command;
	}

	public void setText(String text) {
		suggestBox.setText(text);
	}

}
