package com.areahomeschoolers.baconbits.server.dao;

import java.util.ArrayList;

import org.springframework.security.access.prepost.PreAuthorize;

import com.areahomeschoolers.baconbits.shared.dto.Arg.ReviewArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Review;

public interface ReviewDao {
	public ArrayList<Review> list(ArgMap<ReviewArg> args);

	@PreAuthorize("hasRole('SITE_MEMBERS')")
	public Review save(Review review);
}
