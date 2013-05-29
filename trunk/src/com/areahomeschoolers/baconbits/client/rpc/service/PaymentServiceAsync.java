package com.areahomeschoolers.baconbits.client.rpc.service;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Adjustment;
import com.areahomeschoolers.baconbits.shared.dto.Arg.PaymentArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.Payment;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PaymentServiceAsync {
	public void deleteAdjustment(int adjustmentId, AsyncCallback<Void> callback);

	public void getAdjustments(ArgMap<PaymentArg> args, AsyncCallback<ArrayList<Adjustment>> callback);

	public void getById(int paymentId, AsyncCallback<Payment> callback);

	public void getUnpaidBalance(int userId, AsyncCallback<Data> callback);

	public void list(ArgMap<PaymentArg> args, AsyncCallback<ArrayList<Payment>> callback);

	public void save(Payment payment, AsyncCallback<Payment> callback);

	public void saveAdjustment(Adjustment adjustment, AsyncCallback<Adjustment> callback);
}
