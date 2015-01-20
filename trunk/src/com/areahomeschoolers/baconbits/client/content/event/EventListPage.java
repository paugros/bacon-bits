package com.areahomeschoolers.baconbits.client.content.event;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.tag.FriendlyTextWidget;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.util.ClientDateUtils;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.widgets.AddLink;
import com.areahomeschoolers.baconbits.client.widgets.CookieCrumb;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.LocationFilterInput;
import com.areahomeschoolers.baconbits.client.widgets.MonthPicker;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.client.widgets.TilePanel;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Event;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.areahomeschoolers.baconbits.client.widgets.DefaultHyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public final class EventListPage implements Page {
	private MonthPicker monthBox;
	private DefaultListBox ageBox;
	private static final String NEWLY_ADDED_TOKEN = "newlyAdded";
	private boolean showCommunity = Url.getBooleanParameter("showCommunity");
	private boolean newlyAdded = Url.getBooleanParameter(NEWLY_ADDED_TOKEN);
	private EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);
	private TilePanel fp = new TilePanel();
	private ArgMap<EventArg> args = new ArgMap<EventArg>(Status.ACTIVE);
	private ArrayList<Event> events;
	private TextBox searchControl;

	public EventListPage(final VerticalPanel page) {
		if (Application.hasLocation()) {
			args.put(EventArg.LOCATION_FILTER, true);
		}
		if (showCommunity) {
			args.put(EventArg.ONLY_COMMUNITY);
		}
		if (newlyAdded) {
			args.put(EventArg.NEWLY_ADDED);
		}
		if (!Common.isNullOrBlank(Url.getParameter("tagId"))) {
			args.put(EventArg.HAS_TAGS, Url.getIntListParameter("tagId"));
		}

		final String title = showCommunity ? "Community Events" : "Events";

		CookieCrumb cc = new CookieCrumb();
		cc.add(new DefaultHyperlink("Events By Type", PageUrl.tagGroup("EVENT")));
		if (!Common.isNullOrBlank(Url.getParameter("tagId"))) {
			cc.add(URL.decode(Url.getParameter("tn")));
		} else {
			cc.add("Events");
		}
		page.add(cc);

		if (Application.isAuthenticated()) {
			AddLink link = new AddLink("Add Event", PageUrl.event(0));
			link.getElement().getStyle().setMarginLeft(10, Unit.PX);
			page.add(link);
			page.setCellWidth(link, "1%");
		}

		if (!showCommunity) {
			VerticalPanel vp = new VerticalPanel();
			vp.setWidth("100%");
			HorizontalPanel hp = new HorizontalPanel();

			PaddedPanel pp = new PaddedPanel(10);
			VerticalPanel vpp = new VerticalPanel();
			vpp.setSpacing(8);
			vpp.add(pp);
			vp.add(vpp);

			ageBox = new DefaultListBox();
			ageBox.addItem("all ages", 0);
			for (int age = 1; age < 19; age++) {
				ageBox.addItem(Integer.toString(age) + " year-olds", age);
			}

			ageBox.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					applyTableFilter();
				}
			});

			monthBox = new MonthPicker();
			monthBox.getListBox().setItemText(0, "any month");
			monthBox.getListBox().setSelectedIndex(0);
			monthBox.addValueChangeCommand(new Command() {
				@Override
				public void execute() {
					applyTableFilter();
				}
			});

			pp.add(new Label("Show "));
			pp.add(new Label(" events for "));
			pp.add(ageBox);

			Label in = new Label("in");
			pp.add(in);
			pp.add(monthBox);

			for (int i = 0; i < pp.getWidgetCount(); i++) {
				pp.setCellVerticalAlignment(pp.getWidget(i), HasVerticalAlignment.ALIGN_MIDDLE);
			}

			PaddedPanel bottom = new PaddedPanel(15);

			// within miles
			final LocationFilterInput locationInput = new LocationFilterInput();
			if (Application.hasLocation()) {
				locationInput.setText(Application.getCurrentLocation());
			}

			locationInput.setClearCommand(new Command() {
				@Override
				public void execute() {
					args.remove(EventArg.LOCATION_FILTER);
					populate();
				}
			});

			locationInput.setChangeCommand(new Command() {
				@Override
				public void execute() {
					args.put(EventArg.LOCATION_FILTER, true);
					populate();
				}
			});

			bottom.add(locationInput);

			for (int i = 0; i < bottom.getWidgetCount(); i++) {
				bottom.setCellVerticalAlignment(bottom.getWidget(i), HasVerticalAlignment.ALIGN_MIDDLE);
			}
			vpp.add(bottom);

			PaddedPanel searchPanel = new PaddedPanel(15);
			searchPanel.add(new Label("with text"));
			searchControl = new TextBox();
			searchControl.setVisibleLength(45);
			searchPanel.add(searchControl);
			vpp.add(searchPanel);

			searchControl.addBlurHandler(new BlurHandler() {
				@Override
				public void onBlur(BlurEvent event) {
					applyTableFilter();
				}
			});

			searchControl.addKeyDownHandler(new KeyDownHandler() {
				@Override
				public void onKeyDown(KeyDownEvent event) {
					if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
						applyTableFilter();
					}
				}
			});

			CheckBox cb = new CheckBox("Only show recently added events");
			cb.setValue(newlyAdded, false);

			cb.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					if (event.getValue()) {
						args.put(EventArg.NEWLY_ADDED);
						HistoryToken.append(NEWLY_ADDED_TOKEN + "=true", false);
					} else {
						args.remove(EventArg.NEWLY_ADDED);
						HistoryToken.removeToken(NEWLY_ADDED_TOKEN, false);
					}
					populate();
				}
			});
			vpp.add(cb);

			vpp.addStyleName("boxedBlurb");

			hp.add(vp);

			FriendlyTextWidget fw = new FriendlyTextWidget(TagMappingType.EVENT);
			fw.getElement().getStyle().setMarginLeft(10, Unit.PX);
			hp.add(fw);

			page.add(hp);
		}

		page.add(fp);
		Application.getLayout().setPage(title, page);

		populate();
	}

	private void applyTableFilter() {
		// first
		int month = monthBox.getMonth();
		int age = ageBox.getIntValue();
		String text = searchControl.getValue().toLowerCase();

		if (month == 0 && age == 0 && text.isEmpty()) {
			fp.showAll();
			return;
		}

		for (Event e : events) {
			boolean monthMatch = false;
			boolean ageMatch = false;
			boolean textMatch = false;

			// month
			monthMatch = month == 0 || (ClientDateUtils.getMonth(e.getStartDate()) == month);

			// text
			String eventDescription = new HTML(e.getDescription()).getText().toLowerCase();
			textMatch = text.isEmpty() || e.getTitle().toLowerCase().contains(text) || eventDescription.contains(text);

			// age
			String rangeText = e.getAgeRanges();

			if (age == 0 || rangeText == null) {
				ageMatch = true;
			} else {
				String[] ranges = rangeText.split(",");

				for (int i = 0; i < ranges.length; i++) {
					String[] limits = ranges[i].split("-");

					int min = Integer.parseInt(limits[0]);
					int max = Integer.parseInt(limits[1]);

					ageMatch = ((min == 0 || (min <= age)) && (max == 0 || (max >= age)));
					if (ageMatch) {
						break;
					}
				}
			}

			fp.setVisible(e, monthMatch && ageMatch && textMatch);
		}

	}

	private void populate() {
		eventService.list(args, new Callback<ArrayList<Event>>() {
			@Override
			protected void doOnSuccess(ArrayList<Event> result) {
				events = result;
				fp.clear();

				for (Event e : result) {
					fp.add(new EventTile(e), e.getId());
				}
			}
		});
	}
}
