package com.areahomeschoolers.baconbits.client.content;

import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.HtmlSuggestion;
import com.areahomeschoolers.baconbits.client.widgets.ServerSuggestOracle;
import com.areahomeschoolers.baconbits.shared.Common;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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
	private final DefaultListBox searchTypeListBox;
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

		searchTypeListBox = new DefaultListBox();
		searchTypeListBox.addItem("Address");
		searchTypeListBox.addItem("Billing service", "BillingService");
		searchTypeListBox.addItem("Circuit");
		searchTypeListBox.addItem("Contact");
		searchTypeListBox.addItem("Customer / Account", "Customer");
		searchTypeListBox.addItem("Domain", "DnsRecord");
		// searchTypeListBox.addItem("Employee", PageUrl.userList() + "&search=");
		// searchTypeListBox.addItem("Helpdesk ticket number", PageUrl.hdt(0) + "&hdtId=");
		// searchTypeListBox.addItem("IP address", PageUrl.ipBlockList() + "&ip=");
		// searchTypeListBox.addItem("KB article", "Kb");
		// searchTypeListBox.addItem("NPA-NXX", "NpaNxx");
		// searchTypeListBox.addItem("Order number", PageUrl.order(0) + "&orderId=");
		// searchTypeListBox.addItem("Partner", PageUrl.vendorList() + "&search=");
		// searchTypeListBox.addItem("Project number", PageUrl.orderProject(0) + "&orderProjectId=");
		// searchTypeListBox.addItem("QAR number", PageUrl.qar(0) + "&qarId=");
		// searchTypeListBox.addItem("Quote number", PageUrl.quote(0) + "&quoteId=");
		// searchTypeListBox.addItem("Request number", PageUrl.request(0) + "&requestId=");
		// searchTypeListBox.addItem("Sales forecast number", PageUrl.salesForecast(0) + "&salesForecastId=");
		// searchTypeListBox.addItem("Telephone number", PageUrl.tnList() + "&search=");
		// searchTypeListBox.addItem("Trouble ticket number", PageUrl.tt(0) + "&ttId=");
		// searchTypeListBox.addItem("Vendor trouble ticket number", PageUrl.ttList() + "&vendorTtId=");
		// searchTypeListBox.addItem("Work ticket number", PageUrl.wt(0) + "&wtId=");

		// default to customer search
		searchTypeListBox.setValue("Customer");

		searchTypeListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				String value = searchTypeListBox.getValue();

				if (isOracleOption(value)) {
					oracle.setEnabled(true);
					oracle.setSuggestType(value);
				} else {
					oracle.setEnabled(false);
				}
			}
		});
		searchTypeListBox.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				int keyCode = event.getNativeKeyCode();
				if (keyCode == KeyCodes.KEY_ENTER) {
					loadSearchResults(event.isControlKeyDown());
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
		hPanel.add(searchTypeListBox);
		hPanel.add(submitButton);
	}

	public void reset() {
		searchSuggestBox.getTextBox().setText("");
	}

	private void hideSuggestions() {
		DefaultSuggestionDisplay display = (DefaultSuggestionDisplay) searchSuggestBox.getSuggestionDisplay();
		display.hideSuggestions();
	}

	private boolean isOracleOption(String value) {
		return Common.isIn(value, new String[] { "Account", "Address", "Customer", "Contact" });
	}

	private boolean isSearchOption(String value) {
		return isOracleOption(value) || Common.isIn(value, new String[] { "Asset", "Circuit", "Kb", "BillingService", "NpaNxx", "DnsRecord" });
	}

	private void loadEntityViewPage(HtmlSuggestion suggestion) {
		String entityType = suggestion.getEntityType();
		if (entityType == null) {
			entityType = searchTypeListBox.getValue();
		}

		String url = "page=" + entityType + "&" + entityType.substring(0, 1).toLowerCase() + entityType.substring(1) + "Id=" + suggestion.getEntityId();
		History.newItem(url);
	}

	private void loadSearchResults(boolean newTab) {
		String value = searchTypeListBox.getValue();
		String searchText = searchSuggestBox.getText().trim();
		// String url;

		if (!Common.isNullOrBlank(searchText)) {
			if (isSearchOption(value)) {
				String typeText = searchTypeListBox.getValue();
				// String newUrl = "page=" + typeText;

				// These load search report pages
				if (Common.isIn(typeText, new String[] { "Asset", "Circuit", "Kb", "Customer", "BillingService", "NpaNxx", "DnsRecord" })) {
					if (typeText.equals("Customer")) {
						// newUrl = PageUrl.customerSearchReport() + "&autoSubmit=true";
					}

					// newUrl += "SearchReport&autoSubmit=true";
				} else {
					// newUrl += "List";
				}

				// searchText = Url.encode(searchText);
				// url = newUrl + "&search=" + searchText;
			} else {
				// url = value + searchText;
			}

			if (newTab) {
				// HistoryToken.setNewTab(url);
			} else {
				// HistoryToken.set(url);
			}
		}
	}
}
