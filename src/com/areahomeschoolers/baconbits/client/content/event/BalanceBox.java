package com.areahomeschoolers.baconbits.client.content.event;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.shared.dto.Data;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

public class BalanceBox extends Composite {
	private HTML label = new HTML();
	private EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);

	public BalanceBox() {
		initWidget(label);
	}

	public void populate() {
		label.setText("");

		eventService.getUnpaidBalance(Application.getCurrentUserId(), new Callback<Data>() {
			@Override
			protected void doOnSuccess(Data result) {
				if (result.getDouble("balance") == 0) {
					return;
				}

				String message = "<span style=\"font-weight: bold; font-size: 11px; text-decoration: underline;\">Your shopping cart</span><br>";
				message += result.getInt("itemCount") + " items / ";
				message += Formatter.formatCurrency(result.getDouble("balance"));
				label.setStyleName("BalanceBox");
				label.setHTML(message);
				label.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						HistoryToken.set(PageUrl.payment());
					}
				});
			}
		});
	}
}
