package com.areahomeschoolers.baconbits.server.service;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.server.spring.GWTController;

@Controller
@RequestMapping("/event")
public class EventServiceImpl extends GWTController implements EventService {

	private static final long serialVersionUID = 1L;

	public EventServiceImpl() {

	}

}
