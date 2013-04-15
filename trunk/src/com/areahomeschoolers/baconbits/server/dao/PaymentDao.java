package com.areahomeschoolers.baconbits.server.dao;

import java.util.ArrayList;

import org.springframework.security.access.prepost.PreAuthorize;

import com.areahomeschoolers.baconbits.shared.dto.Arg.PaymentArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Payment;

public interface PaymentDao {
	public Payment getById(int paymentId);

	public ArrayList<Payment> list(ArgMap<PaymentArg> args);

	@PreAuthorize("hasRole('GROUP_ADMINISTRATORS')")
	public Payment save(Payment payment);
}
