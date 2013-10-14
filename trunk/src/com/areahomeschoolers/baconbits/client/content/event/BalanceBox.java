package com.areahomeschoolers.baconbits.client.content.event;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.event.ParameterHandler;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.PollResponseData;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class BalanceBox extends Composite {
	private HTML html = new HTML();
	private double balance;
	private int count;

	public BalanceBox() {
		initWidget(html);
		html.setStyleName("BalanceBox");
		html.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HistoryToken.set(PageUrl.payment());
			}
		});

		setVisible(false);

		Application.addPollReturnHandler(new ParameterHandler<PollResponseData>() {
			@Override
			public void execute(PollResponseData item) {
				populate(item.getUnpaidBalance());
			}
		});

		populate(Application.getApplicationData().getUnpaidBalance());
	}

	public void populate(Data result) {
		if (result == null) {
			return;
		}

		if (result.getDouble("balance") == balance && result.getInt("itemCount") == count) {
			return;
		}

		balance = result.getDouble("balance");
		count = result.getInt("itemCount");

		count = 76;
		balance = 452.78;
		if (balance == 0) {
			setVisible(false);
			return;
		}

		setVisible(true);

		Image cart = new Image(MainImageBundle.INSTANCE.shoppingCart());
		String s = count > 1 ? "s" : "";
		String message = count + " item" + s + " / ";
		message += Formatter.formatCurrency(balance);

		PaddedPanel pp = new PaddedPanel();
		pp.add(cart);
		Label text = new Label(message);
		text.addStyleName("smallText");
		pp.add(text);

		pp.setCellVerticalAlignment(cart, HasVerticalAlignment.ALIGN_MIDDLE);
		pp.setCellVerticalAlignment(text, HasVerticalAlignment.ALIGN_MIDDLE);

		html.setHTML(pp.toString());
	}
}