package com.areahomeschoolers.baconbits.client.content.event;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.shared.dto.Data;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class EventBalanceBox extends Composite {
	private PaddedPanel pp = new PaddedPanel();
	private EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);

	public EventBalanceBox() {
		initWidget(pp);
	}

	public void populate() {
		pp.clear();

		eventService.getUnpaidBalance(Application.getCurrentUserId(), new Callback<Data>() {
			@Override
			protected void doOnSuccess(Data result) {
				if (result.getDouble("balance") == 0) {
					return;
				}

				String message = "You have " + result.getInt("itemCount") + " unpaid items totaling ";
				message += Formatter.formatCurrency(result.getDouble("balance"));
				Label l = new Label(message);
				l.addStyleName("largeText");
				pp.add(l);

				Image logo = new Image(MainImageBundle.INSTANCE.paypalButton());
				logo.getElement().getStyle().setCursor(Cursor.POINTER);
				String h = "<a href=\"" + GWT.getHostPageBaseURL() + "#" + PageUrl.eventPayment() + "\">";
				h += logo.toString() + "</a>";
				HTML html = new HTML(h);

				pp.add(html);
			}
		});
	}
}
