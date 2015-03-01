package com.areahomeschoolers.baconbits.server.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.areahomeschoolers.baconbits.client.rpc.service.ReviewService;
import com.areahomeschoolers.baconbits.server.dao.ReviewDao;
import com.areahomeschoolers.baconbits.server.spring.GwtController;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ReviewArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Review;

@Controller
@RequestMapping("/review")
public class ReviewServiceImpl extends GwtController implements ReviewService {

	private static final long serialVersionUID = 1L;
	private final ReviewDao dao;

	@Autowired
	public ReviewServiceImpl(ReviewDao dao) {
		this.dao = dao;
	}

	@Override
	public ArrayList<Review> list(ArgMap<ReviewArg> args) {
		return dao.list(args);
	}

	@Override
	public Review save(Review review) {
		return dao.save(review);
	}

}
