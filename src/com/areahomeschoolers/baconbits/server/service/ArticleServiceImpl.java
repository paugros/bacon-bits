package com.areahomeschoolers.baconbits.server.service;

import java.util.ArrayList;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.areahomeschoolers.baconbits.client.rpc.service.ArticleService;
import com.areahomeschoolers.baconbits.server.spring.GWTController;
import com.areahomeschoolers.baconbits.server.util.ServerUtils;
import com.areahomeschoolers.baconbits.server.util.SpringWrapper;
import com.areahomeschoolers.baconbits.shared.dto.Data;

@Controller
@RequestMapping("/article")
public class ArticleServiceImpl extends GWTController implements ArticleService {

	private static final long serialVersionUID = 1L;
	private SpringWrapper wrapper;

	@Autowired
	public ArticleServiceImpl(DataSource ds) {
		wrapper = new SpringWrapper(ds);
	}

	@Override
	public ArrayList<Data> getArticles() {
		String sql = "select * from articles";
		ArrayList<Data> data = wrapper.query(sql, ServerUtils.getGenericRowMapper());

		return data;
	}

}
