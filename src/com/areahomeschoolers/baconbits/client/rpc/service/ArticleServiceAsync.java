package com.areahomeschoolers.baconbits.client.rpc.service;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Data;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ArticleServiceAsync {

	void getArticles(AsyncCallback<ArrayList<Data>> callback);
}
