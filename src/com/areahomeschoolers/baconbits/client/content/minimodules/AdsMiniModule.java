package com.areahomeschoolers.baconbits.client.content.minimodules;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.resource.AdTile;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.ResourceService;
import com.areahomeschoolers.baconbits.client.rpc.service.ResourceServiceAsync;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ResourceArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Resource;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AdsMiniModule extends Composite {
	public enum AdDirection {
		VERTICAL, HORIZONTAL
	}

	private ResourceServiceAsync resourceService = (ResourceServiceAsync) ServiceCache.getService(ResourceService.class);
	private VerticalPanel vp = new VerticalPanel();
	private HorizontalPanel hp = new HorizontalPanel();
	private ArgMap<ResourceArg> args = new ArgMap<ResourceArg>(Status.ACTIVE);
	private AdDirection direction;

	public AdsMiniModule(AdDirection direction) {
		this.direction = direction;
		vp.setSpacing(10);
		hp.setSpacing(10);

		hp.getElement().getStyle().setMarginTop(50, Unit.PX);

		args.put(ResourceArg.RANDOM);
		args.put(ResourceArg.LIMIT, 3);
		args.put(ResourceArg.AD);
		if (Application.hasLocation()) {
			args.put(ResourceArg.LOCATION_FILTER, true);
		}

		populate();

		initWidget(direction == AdDirection.VERTICAL ? vp : hp);
	}

	private void populate() {
		final ArrayList<Integer> ids = new ArrayList<>();

		resourceService.list(args, new Callback<ArrayList<Resource>>(false) {
			@Override
			protected void doOnSuccess(ArrayList<Resource> result) {
				for (Resource ad : result) {
					AdTile tile = new AdTile(ad);

					if (direction == AdDirection.VERTICAL) {
						vp.add(tile);
					} else {
						hp.add(tile);
					}
					ids.add(ad.getId());
				}

				resourceService.incrementImpressions(ids, new Callback<Void>(false) {
					@Override
					protected void doOnSuccess(Void result) {
					}
				});
			}
		});

	}

}
