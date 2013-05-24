package com.areahomeschoolers.baconbits.client.rpc.service;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Adjustment;
import com.areahomeschoolers.baconbits.shared.dto.Arg.PaymentArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.Payment;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("service/payment")
public interface PaymentService extends RemoteService {
	public void deleteAdjustment(int adjustmentId);

	public ArrayList<Adjustment> getAdjustments(ArgMap<PaymentArg> args);

	public Payment getById(int paymentId);

	public Data getUnpaidBalance(int userId);

	public ArrayList<Payment> list(ArgMap<PaymentArg> args);

	public Payment save(Payment payment);

	public Adjustment saveAdjustment(Adjustment adjustment);
}
