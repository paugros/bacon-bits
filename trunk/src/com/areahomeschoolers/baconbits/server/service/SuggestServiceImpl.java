package com.areahomeschoolers.baconbits.server.service;

import java.util.ArrayList;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.areahomeschoolers.baconbits.client.rpc.service.SuggestService;
import com.areahomeschoolers.baconbits.server.dao.Suggestible;
import com.areahomeschoolers.baconbits.server.spring.GwtController;
import com.areahomeschoolers.baconbits.server.util.ServerContext;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.ServerSuggestionData;

@Controller
@RequestMapping("/names")
public final class SuggestServiceImpl extends GwtController implements SuggestService {
	private static final long serialVersionUID = 1L;

	@Override
	public ServerSuggestionData getSuggestionData(String token, ArrayList<String> suggestTypes, int limit, Data options) {
		ServerSuggestionData data = new ServerSuggestionData();

		for (String type : suggestTypes) {
			try {
				ApplicationContext ctx = ServerContext.getApplicationContext();
				Suggestible suggester = (Suggestible) ctx.getBean(type.substring(0, 1).toLowerCase() + type.substring(1) + "DaoImpl");

				data.getSuggestions().addAll(suggester.getSuggestionData(token, limit, options).getSuggestions());

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return data;
	}
}
