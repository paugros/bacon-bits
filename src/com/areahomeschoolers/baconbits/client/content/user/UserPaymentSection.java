package com.areahomeschoolers.baconbits.client.content.user;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.payments.AdjustmentTable;
import com.areahomeschoolers.baconbits.client.content.payments.AdjustmentTable.AdjustmentColumn;
import com.areahomeschoolers.baconbits.client.content.payments.AdjustmentDialog;
import com.areahomeschoolers.baconbits.client.content.payments.PaymentTable;
import com.areahomeschoolers.baconbits.client.content.payments.PaymentTable.PaymentColumn;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.PaymentService;
import com.areahomeschoolers.baconbits.client.rpc.service.PaymentServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.shared.dto.Adjustment;
import com.areahomeschoolers.baconbits.shared.dto.Arg.PaymentArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.User;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class UserPaymentSection {
	private User user;
	private AdjustmentTable adjustmentTable;
	private PaymentTable paymentTable;
	private AdjustmentDialog dialog = new AdjustmentDialog();
	private PaymentServiceAsync paymentService = (PaymentServiceAsync) ServiceCache.getService(PaymentService.class);

	public UserPaymentSection(User u, VerticalPanel tabPanel) {
		user = u;

		final Label balanceLabel = new Label();
		balanceLabel.addStyleName("largeText");
		paymentService.getUnpaidBalance(user.getId(), new Callback<Data>() {
			@Override
			protected void doOnSuccess(Data result) {
				balanceLabel.setText("Outstanding balance: " + Formatter.formatCurrency(result.getDouble("balance")));
			}
		});

		ArgMap<PaymentArg> adjustmentArgs = new ArgMap<PaymentArg>(PaymentArg.USER_ID, user.getId());
		adjustmentTable = new AdjustmentTable(adjustmentArgs);
		adjustmentTable.setTitle("Adjustments");
		adjustmentTable.removeColumn(AdjustmentColumn.USER);

		if (Application.hasRole(AccessLevel.GROUP_ADMINISTRATORS)) {
			adjustmentTable.setDialog(dialog);
			adjustmentTable.getTitleBar().addLink(new ClickLabel("Add", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					dialog.center(new Adjustment());
				}
			}));

			dialog.addFormSubmitHandler(new FormSubmitHandler() {
				@Override
				public void onFormSubmit(FormField formField) {
					Adjustment a = dialog.getEntity();
					if (!a.isSaved()) {
						a.setStatusId(1);
						a.setAdjustmentTypeId(1);
						a.setUserId(user.getId());
					}

					paymentService.saveAdjustment(a, new Callback<Adjustment>() {
						@Override
						protected void doOnSuccess(Adjustment result) {
							adjustmentTable.addItem(result);
						}
					});
				}
			});
		}

		ArgMap<PaymentArg> paymentArgs = new ArgMap<PaymentArg>(PaymentArg.USER_ID, user.getId());
		paymentTable = new PaymentTable(paymentArgs);
		paymentTable.setTitle("Payments");
		paymentTable.removeColumn(PaymentColumn.USER);

		tabPanel.add(balanceLabel);
		tabPanel.add(WidgetFactory.newSection(adjustmentTable, ContentWidth.MAXWIDTH1000PX));
		tabPanel.add(WidgetFactory.newSection(paymentTable, ContentWidth.MAXWIDTH1000PX));
	}

	public void populate() {
		adjustmentTable.populate();
		paymentTable.populate();
	}
}
