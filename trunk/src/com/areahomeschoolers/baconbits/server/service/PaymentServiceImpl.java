package com.areahomeschoolers.baconbits.server.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.areahomeschoolers.baconbits.client.rpc.service.PaymentService;
import com.areahomeschoolers.baconbits.server.dao.PaymentDao;
import com.areahomeschoolers.baconbits.server.spring.GwtController;
import com.areahomeschoolers.baconbits.shared.dto.Adjustment;
import com.areahomeschoolers.baconbits.shared.dto.Arg.PaymentArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Payment;

@Controller
@RequestMapping("/payment")
public class PaymentServiceImpl extends GwtController implements PaymentService {

	private static final long serialVersionUID = 1L;

	private PaymentDao dao;

	@Autowired
	public PaymentServiceImpl(PaymentDao dao) {
		this.dao = dao;
	}

	@Override
	public void deleteAdjustment(int adjustmentId) {
		dao.deleteAdjustment(adjustmentId);
	}

	@Override
	public ArrayList<Adjustment> getAdjustments(ArgMap<PaymentArg> args) {
		return dao.getAdjustments(args);
	}

	@Override
	public Payment getById(int paymentId) {
		return dao.getById(paymentId);
	}

	@Override
	public ArrayList<Payment> list(ArgMap<PaymentArg> args) {
		return dao.list(args);
	}

	@Override
	public Payment save(Payment payment) {
		return dao.save(payment);
	}

	@Override
	public Adjustment saveAdjustment(Adjustment adjustment) {
		return dao.saveAdjustment(adjustment);
	}

}
