package com.areahomeschoolers.baconbits.client.content.resource;

import java.util.ArrayList;
import java.util.List;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.ViewMode;
import com.areahomeschoolers.baconbits.client.content.resource.ResourceTable.ResourceColumn;
import com.areahomeschoolers.baconbits.client.content.tag.SearchSection;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.ResourceService;
import com.areahomeschoolers.baconbits.client.rpc.service.ResourceServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.AddLink;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.CookieCrumb;
import com.areahomeschoolers.baconbits.client.widgets.DefaultHyperlink;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.LocationFilterInput;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.client.widgets.TilePanel;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable.SortDirection;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ResourceArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Resource;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagType;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public final class ResourceListPage implements Page {
	private ResourceServiceAsync resourceService = (ResourceServiceAsync) ServiceCache.getService(ResourceService.class);
	private ArgMap<ResourceArg> args = new ArgMap<ResourceArg>(Status.ACTIVE);
	private TilePanel fp = new TilePanel();
	private ArrayList<Resource> resources;
	private VerticalPanel page;
	private ResourceTable table = new ResourceTable(args);
	private ViewMode viewMode = ViewMode.GRID;
	private SimplePanel sp = new SimplePanel();
	private TextBox searchInput;

	public ResourceListPage(final VerticalPanel p) {
		String title = "Resources";
		page = p;
		page.setWidth("100%");
		page.getElement().getStyle().setMarginLeft(15, Unit.PX);
		table.setDisplayColumns(ResourceColumn.IMAGE, ResourceColumn.NAME, ResourceColumn.DESCRIPTION, ResourceColumn.LOCATION, ResourceColumn.TAGS,
				ResourceColumn.PRICE, ResourceColumn.AGES);
		table.addStyleName(ContentWidth.MAXWIDTH1100PX.toString());
		table.setDefaultSortColumn(ResourceColumn.NAME, SortDirection.SORT_ASC);
		table.disablePaging();

		if (Application.hasLocation()) {
			args.put(ResourceArg.LOCATION_FILTER, true);
		}

		if (!Common.isNullOrBlank(Url.getParameter("tagId"))) {
			args.put(ResourceArg.HAS_TAGS, Url.getIntListParameter("tagId"));
		}

		CookieCrumb cc = new CookieCrumb();
		cc.add(new DefaultHyperlink("Resources By Type", PageUrl.tagGroup("RESOURCE")));
		if (!Common.isNullOrBlank(Url.getParameter("tagId"))) {
			String tag = URL.decode(Url.getParameter("tn"));
			cc.add(tag);
			title = tag + " " + title;
		} else {
			cc.add("Resources");
		}
		page.add(cc);

		AddLink link = new AddLink("Add Resource", PageUrl.resource(0));
		link.getElement().getStyle().setMarginLeft(10, Unit.PX);
		page.add(link);
		page.setCellWidth(link, "1%");

		createSearchBox();

		DefaultListBox lb = new DefaultListBox();
		lb.getElement().getStyle().setMarginLeft(10, Unit.PX);
		lb.addItem("Grid view");
		lb.addItem("List view");
		lb.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				if (viewMode == ViewMode.GRID) {
					viewMode = ViewMode.LIST;
					sp.setWidget(table);
				} else {
					viewMode = ViewMode.GRID;
					sp.setWidget(fp);
				}
				populate(resources);
				applyFilter();
			}
		});

		page.add(lb);

		sp.setWidget(fp);
		page.add(sp);
		Application.getLayout().setPage(title, page);
		populate();
	}

	private void applyFilter() {
		String text = searchInput.getText();
		if (text == null || text.isEmpty()) {
			if (viewMode == ViewMode.GRID) {
				fp.showAll();
			} else {
				table.showAllItems();
			}
			return;
		}

		text = text.toLowerCase();

		for (Resource r : resources) {
			String descriptionText = new HTML(r.getDescription()).getText().toLowerCase();
			boolean visible = false;
			if (r.getName().toLowerCase().contains(text)) {
				visible = true;
			} else if (descriptionText.contains(text)) {
				visible = true;
			} else if (r.getAddress() != null && r.getAddress().contains(text)) {
				visible = true;
			} else if (r.getTags() != null && r.getTags().contains(text)) {
				visible = true;
			}
			if (viewMode == ViewMode.GRID) {
				fp.setVisible(r, visible);
			} else {
				table.setItemVisible(r, visible);
			}
		}
	}

	private void createSearchBox() {
		VerticalPanel vvp = new VerticalPanel();
		vvp.setSpacing(4);
		PaddedPanel searchBox = new PaddedPanel();
		vvp.addStyleName("boxedBlurb");
		searchBox.setSpacing(4);
		searchBox.add(new Label("Search:"));
		searchInput = new TextBox();
		searchInput.setVisibleLength(35);
		searchBox.add(searchInput);
		searchInput.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				applyFilter();
			}
		});

		searchInput.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					applyFilter();
				}
			}
		});

		vvp.add(searchBox);

		final LocationFilterInput locationInput = new LocationFilterInput();
		if (Application.hasLocation()) {
			locationInput.setText(Application.getCurrentLocation());
		}

		locationInput.setClearCommand(new Command() {
			@Override
			public void execute() {
				args.remove(ResourceArg.LOCATION_FILTER);
				populate();
			}
		});

		locationInput.setChangeCommand(new Command() {
			@Override
			public void execute() {
				args.put(ResourceArg.LOCATION_FILTER, true);
				populate();
			}
		});

		PaddedPanel bottom = new PaddedPanel();
		bottom.setSpacing(4);

		bottom.add(locationInput);

		for (int i = 0; i < bottom.getWidgetCount(); i++) {
			bottom.setCellVerticalAlignment(bottom.getWidget(i), HasVerticalAlignment.ALIGN_MIDDLE);
		}
		vvp.add(bottom);

		VerticalPanel cp = new VerticalPanel();

		ClickLabel reset = new ClickLabel("Reset search", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				locationInput.clearLocation();
				Application.reloadPage();
			}
		});

		cp.add(reset);

		vvp.add(cp);
		vvp.setCellHorizontalAlignment(cp, HasHorizontalAlignment.ALIGN_RIGHT);

		page.add(new SearchSection(TagType.RESOURCE, vvp));
	}

	private void populate() {
		resourceService.list(args, new Callback<ArrayList<Resource>>() {
			@Override
			protected void doOnSuccess(ArrayList<Resource> result) {
				resources = result;

				populate(resources);
			}
		});
	}

	private void populate(List<Resource> resources) {
		if (viewMode == ViewMode.GRID) {
			fp.clear();

			for (Resource r : resources) {
				fp.add(new ResourceTile(r), r.getId());
			}
		} else {
			table.populate(resources);
		}
	}

}
