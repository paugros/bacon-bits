package com.areahomeschoolers.baconbits.client.content.tag;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.resource.Tile;
import com.areahomeschoolers.baconbits.client.content.resource.TileConfig;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.TagService;
import com.areahomeschoolers.baconbits.client.rpc.service.TagServiceAsync;
import com.areahomeschoolers.baconbits.client.util.ClientUtils;
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
import com.areahomeschoolers.baconbits.shared.dto.Arg.TagArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Tag;
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
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public final class TagGroupPage implements Page {
	private ArgMap<TagArg> args = new ArgMap<TagArg>();
	private TagServiceAsync tagService = (TagServiceAsync) ServiceCache.getService(TagService.class);
	private TilePanel fp = new TilePanel();
	private TagMappingType type = null;
	private ArrayList<Tag> tags;
	private VerticalPanel page;

	public TagGroupPage(final VerticalPanel p) {
		page = p;
		try {
			type = TagMappingType.valueOf(Url.getParameter("type"));
		} catch (Exception e) {
		}

		if (type == null) {
			new ErrorPage(PageError.PAGE_NOT_FOUND);
			return;
		}

		final String title = "Categories";
		fp.setWidth("100%");

		args.put(TagArg.MAPPING_TYPE, type.toString());
		args.put(TagArg.GET_COUNTS);

		if (Application.hasLocation() && !type.equals(TagMappingType.ARTICLE)) {
			args.put(TagArg.WITHIN_LAT, Double.toString(Application.getCurrentLat()));
			args.put(TagArg.WITHIN_LNG, Double.toString(Application.getCurrentLng()));
			args.put(TagArg.WITHIN_MILES, Constants.DEFAULT_SEARCH_RADIUS);
		}

		populate();

		CookieCrumb cc = new CookieCrumb();
		String typeText = type.equals(TagMappingType.USER) ? "Interests" : "Type";
		cc.add(type.getName() + " By " + typeText);

		page.add(cc);

		if (Application.isAuthenticated()) {
			String url = null;
			switch (type) {
			case ARTICLE:
				if (Application.memberOf(33)) {
					url = PageUrl.article(0);
				}
				break;
			case BOOK:
				break;
			case EVENT:
				url = PageUrl.event(0);
				break;
			case RESOURCE:
				url = PageUrl.resource(0);
				break;
			case USER:
				if (Application.isSystemAdministrator()) {
					url = PageUrl.user(0);
				}
				break;
			default:
				break;
			}

			if (url != null) {
				String name = Common.ucWords(type.getName()).substring(0, type.getName().length() - 1);
				AddLink link = new AddLink("Add " + name, url);
				link.getElement().getStyle().setMarginLeft(10, Unit.PX);
				page.add(link);
				page.setCellWidth(link, "1%");
			}
		}

		createSearchBox();

		page.add(fp);

		Application.getLayout().setPage(title, page);
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

		// within miles
		if (!type.equals(TagMappingType.ARTICLE)) {
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
					args.put(TagArg.WITHIN_MILES, milesInput.getIntValue());
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
					args.put(TagArg.WITHIN_LAT, Double.toString(locationInput.getLat()));
					args.put(TagArg.WITHIN_LNG, Double.toString(locationInput.getLng()));
					args.put(TagArg.WITHIN_MILES, milesInput.getIntValue());
					populate();
				}
			});

			PaddedPanel bottom = new PaddedPanel();
			bottom.setSpacing(4);

			bottom.add(new Label("within"));
			bottom.add(milesInput);
			bottom.add(new Label("miles of"));
			bottom.add(locationInput);

			for (int i = 0; i < bottom.getWidgetCount(); i++) {
				bottom.setCellVerticalAlignment(bottom.getWidget(i), HasVerticalAlignment.ALIGN_MIDDLE);
			}
			vvp.add(bottom);
		}

		page.add(vvp);
	}

	private void populate() {
		tagService.list(args, new Callback<ArrayList<Tag>>() {
			@Override
			protected void doOnSuccess(ArrayList<Tag> result) {
				tags = result;
				String url = null;
				switch (type) {
				case ARTICLE:
					url = PageUrl.articleList();
					break;
				case BOOK:
					url = PageUrl.bookList();
					break;
				case EVENT:
					url = PageUrl.eventList();
					break;
				case RESOURCE:
					url = PageUrl.resourceList();
					break;
				case USER:
					url = PageUrl.userList();
					break;
				default:
					break;
				}

				TileConfig ac = new TileConfig().setText("All " + type.getName()).setUrl(url).setCenterText(true);
				ac.setImage(new Image(MainImageBundle.INSTANCE.earth())).setColor("#ffffff");
				Tile all = new Tile(ac);
				fp.add(all);

				for (Tag tag : result) {
					TileConfig uc = new TileConfig().setText(tag.getName()).setCount(tag.getCount());
					if (tag.getImageId() != null) {
						uc.setImage(new Image(ClientUtils.createDocumentUrl(tag.getImageId(), tag.getImageExtension())));
					} else {
						uc.setImage(new Image(MainImageBundle.INSTANCE.citrusGirl()));
					}
					String extras = "&tagId=" + tag.getId() + "&tn=" + URL.encode(tag.getName());
					uc.setUrl(url + extras);
					uc.setColor(type.getColor());
					Tile tile = new Tile(uc);

					fp.add(tile, tag.getId());
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

		for (Tag tag : tags) {
			fp.setVisible(tag, tag.getName().toLowerCase().contains(text));
		}
	}

}
