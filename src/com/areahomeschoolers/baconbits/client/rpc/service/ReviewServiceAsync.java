package com.areahomeschoolers.baconbits.client.rpc.service;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Arg.ReviewArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Review;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ReviewServiceAsync {

	void list(ArgMap<ReviewArg> args, AsyncCallback<ArrayList<Review>> callback);

	void save(Review review, AsyncCallback<Review> callback);
}
