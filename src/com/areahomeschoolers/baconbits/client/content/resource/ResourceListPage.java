package com.areahomeschoolers.baconbits.client.content.resource;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.tag.FriendlyTextWidget;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.ResourceService;
import com.areahomeschoolers.baconbits.client.rpc.service.ResourceServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.widgets.AddLink;
import com.areahomeschoolers.baconbits.client.widgets.CookieCrumb;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.GeocoderTextBox;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.client.widgets.TilePanel;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ResourceArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Resource;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public final class ResourceListPage implements Page {
	private ResourceServiceAsync resourceService = (ResourceServiceAsync) ServiceCache.getService(ResourceService.class);
	private ArgMap<ResourceArg> args = new ArgMap<ResourceArg>(Status.ACTIVE);
	private TilePanel fp = new TilePanel();
	private ArrayList<Resource> resources;
	private VerticalPanel page;

	public ResourceListPage(final VerticalPanel p) {
		final String title = "Resources";
		page = p;

		if (!Common.isNullOrBlank(Url.getParameter("tagId"))) {
			args.put(ResourceArg.HAS_TAGS, Url.getIntListParameter("tagId"));
		}

		if (Application.hasLocation()) {
			args.put(ResourceArg.WITHIN_LAT, Double.toString(Application.getCurrentLat()));
			args.put(ResourceArg.WITHIN_LNG, Double.toString(Application.getCurrentLng()));
			args.put(ResourceArg.WITHIN_MILES, Constants.DEFAULT_SEARCH_RADIUS);
		}

		CookieCrumb cc = new CookieCrumb();
		cc.add(new Hyperlink("Resources By Type", PageUrl.tagGroup("RESOURCE")));
		if (!Common.isNullOrBlank(Url.getParameter("tagId"))) {
			cc.add(URL.decode(Url.getParameter("tn")));
		} else {
			cc.add("Resources");
		}
		page.add(cc);

		if (Application.isAuthenticated()) {
			AddLink link = new AddLink("Add Resource", PageUrl.resource(0));
			link.getElement().getStyle().setMarginLeft(10, Unit.PX);
			page.add(link);
			page.setCellWidth(link, "1%");
		}

		createSearchBox();

		page.add(fp);
		Application.getLayout().setPage(title, page);
		populate();
	}

	private void createSearchBox() {
		VerticalPanel vvp = new VerticalPanel();
		vvp.setSpacing(4);
		PaddedPanel searchBox = new PaddedPanel();
		vvp.addStyleName("boxedBlurb");
		searchBox.setSpacing(4);
		searchBox.add(new Label("Search:"));
		final TextBox searchInput = new TextBox();
		searchInput.setVisibleLength(35);
		searchBox.add(searchInput);
		searchInput.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				search(searchInput.getText());
			}
		});

		searchInput.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					search(searchInput.getText());
				}
			}
		});

		vvp.add(searchBox);

		final GeocoderTextBox locationInput = new GeocoderTextBox();
		if (Application.hasLocation()) {
			locationInput.setText(Application.getCurrentLocation());
		}

		final DefaultListBox milesInput = new DefaultListBox();
		milesInput.addItem("5", 5);
		milesInput.addItem("10", 10);
		milesInput.addItem("25", 25);
		milesInput.addItem("50", 50);
		milesInput.setValue(25);
		milesInput.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				args.put(ResourceArg.WITHIN_MILES, milesInput.getIntValue());
				if (!locationInput.getText().isEmpty()) {
					populate();
				}
			}
		});

		locationInput.setClearCommand(new Command() {
			@Override
			public void execute() {
				args.remove(EventArg.WITHIN_LAT);
				args.remove(EventArg.WITHIN_LNG);
				populate();
			}
		});

		locationInput.setChangeCommand(new Command() {
			@Override
			public void execute() {
				args.put(ResourceArg.WITHIN_LAT, Double.toString(locationInput.getLat()));
				args.put(ResourceArg.WITHIN_LNG, Double.toString(locationInput.getLng()));
				args.put(ResourceArg.WITHIN_MILES, milesInput.getIntValue());
				populate();
			}
		});

		final DefaultListBox stateInput = new DefaultListBox();
		stateInput.addItem("");
		for (int i = 0; i < Constants.STATE_NAMES.length; i++) {
			stateInput.addItem(Constants.STATE_NAMES[i]);
		}
		stateInput.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				args.put(ResourceArg.STATE, stateInput.getValue());
				populate();
			}
		});

		PaddedPanel bottom = new PaddedPanel();
		bottom.setSpacing(4);

		bottom.add(new Label("within"));
		bottom.add(milesInput);
		bottom.add(new Label("miles of"));
		bottom.add(locationInput);
		bottom.add(new Label("in"));
		bottom.add(stateInput);

		for (int i = 0; i < bottom.getWidgetCount(); i++) {
			bottom.setCellVerticalAlignment(bottom.getWidget(i), HasVerticalAlignment.ALIGN_MIDDLE);
		}
		vvp.add(bottom);

		PaddedPanel pp = new PaddedPanel(10);
		pp.add(vvp);
		pp.add(new FriendlyTextWidget(TagMappingType.RESOURCE));

		page.add(pp);
	}

	private void populate() {
		resourceService.list(args, new Callback<ArrayList<Resource>>() {
			@Override
			protected void doOnSuccess(ArrayList<Resource> result) {
				resources = result;
				fp.clear();

				for (Resource r : result) {
					fp.add(new ResourceTile(r), r.getId());
				}
			}
		});
	}

	private void search(String text) {
		if (text == null || text.isEmpty()) {
			fp.showAll();
			return;
		}

		text = text.toLowerCase();

		for (Resource r : resources) {
			String descriptionText = new HTML(r.getDescription()).getText().toLowerCase();
			fp.setVisible(r, r.getName().toLowerCase().contains(text) || descriptionText.contains(text));
		}
	}

}
