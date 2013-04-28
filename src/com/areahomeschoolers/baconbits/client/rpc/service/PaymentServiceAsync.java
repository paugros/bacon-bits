package com.areahomeschoolers.baconbits.client.rpc.service;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Arg.PaymentArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.Payment;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PaymentServiceAsync {

	void getAdjustments(ArgMap<PaymentArg> args, AsyncCallback<ArrayList<Data>> callback);

	void getById(int paymentId, AsyncCallback<Payment> callback);

	void list(ArgMap<PaymentArg> args, AsyncCallback<ArrayList<Payment>> callback);

	void save(Payment payment, AsyncCallback<Payment> callback);
}
