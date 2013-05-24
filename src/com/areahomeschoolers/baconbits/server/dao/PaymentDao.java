package com.areahomeschoolers.baconbits.server.dao;

import java.util.ArrayList;

import org.springframework.security.access.prepost.PreAuthorize;

import com.areahomeschoolers.baconbits.shared.dto.Adjustment;
import com.areahomeschoolers.baconbits.shared.dto.Arg.PaymentArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.Payment;

public interface PaymentDao {
	@PreAuthorize("hasRole('GROUP_ADMINISTRATORS')")
	public void deleteAdjustment(int adjustmentId);

	@PreAuthorize("hasRole('SITE_MEMBERS')")
	public ArrayList<Adjustment> getAdjustments(ArgMap<PaymentArg> args);

	@PreAuthorize("hasRole('SITE_MEMBERS')")
	public Payment getById(int paymentId);

	public Data getUnpaidBalance(int userId);

	@PreAuthorize("hasRole('SITE_MEMBERS')")
	public ArrayList<Payment> list(ArgMap<PaymentArg> args);

	@PreAuthorize("hasRole('SITE_MEMBERS')")
	public Payment save(Payment payment);

	@PreAuthorize("hasRole('GROUP_ADMINISTRATORS')")
	public Adjustment saveAdjustment(Adjustment adjustment);
}
