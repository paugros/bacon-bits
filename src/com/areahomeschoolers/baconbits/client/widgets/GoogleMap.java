package com.areahomeschoolers.baconbits.client.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.areahomeschoolers.baconbits.client.images.MainImageBundle;

import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.control.MapTypeControl;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.geocode.Geocoder;
import com.google.gwt.maps.client.geocode.LatLngCallback;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Composite widget composed of a {@link Grid} containing a {@link MapWidget} and a {@link VerticalPanel} (for errors).
 */
public class GoogleMap extends Composite {
	private static Map<String, String> apiKeys = new HashMap<String, String>();

	public static void loadMapsApi(Runnable runnable) {
		Maps.loadMapsApi(apiKeys.get("dash.dscicorp.com"), "2", false, runnable);
	}

	private MapWidget map;
	private LatLngBounds bounds;
	private Geocoder geocoder;
	private List<String> failed = new ArrayList<String>();
	private List<String> succeeded = new ArrayList<String>();
	private List<String> addresses = new ArrayList<String>();
	private Grid grid = new Grid(2, 1);
	private VerticalPanel errorPanel = new VerticalPanel();
	private List<Command> queueCommands = new ArrayList<Command>();

	static {
		apiKeys.put("dash.dscicorp.com", "ABQIAAAAMN3S9MR9TXzzAEhTW6gmzBSTmqdp7D9DJW6fmwPkrCTk6dT-lhTwoUMRaus7mc2fSspRly19YUFQsA");
	}

	public GoogleMap() {
		initWidget(grid);
		addStyleName("GoogleMap");
		grid.setWidth("100%");
		grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);

		loadMapsApi(new Runnable() {
			@Override
			public void run() {
				map = new MapWidget();
				map.setHeight("500px");
				map.setWidth("99%");
				// Add some controls for the zoom level
				map.addControl(new LargeMapControl());
				map.addControl(new MapTypeControl());
				// map.addControl(new OverviewMapControl());
				map.setStyleName("mapWidget");
				geocoder = new Geocoder();

				if (!queueCommands.isEmpty()) {
					for (Command command : queueCommands) {
						command.execute();
					}
					queueCommands.clear();
				}
			}
		});
	}

	/**
	 * A new map with the specified addresses marked.
	 * 
	 * @param addresses
	 */
	public GoogleMap(List<String> addresses) {
		this();

		for (final String address : addresses) {
			addAddressToMap(address);
		}
	}

	/**
	 * A new map with the specified address marked.
	 * 
	 * @param address
	 */
	public GoogleMap(String address) {
		this();
		addAddressToMap(address);
	}

	public void addAddressToMap(final String address) {
		if (address == null) {
			return;
		}
		if (map == null) {
			queueCommands.add(new Command() {
				@Override
				public void execute() {
					addAddressToMap(address);
				}
			});

			return;
		}

		addresses.add(address);
		geocoder.getLatLng(address, new LatLngCallback() {
			@Override
			public void onFailure() {
				failed.add(address);
				processPoint(null, address);
			}

			@Override
			public void onSuccess(LatLng point) {
				succeeded.add(address);
				processPoint(point, address);
			}
		});
	}

	public void addPointToMap(LatLng point, String html) {
		addPointToMap(point, html, null);
	}

	public void addPointToMap(final LatLng point, final String html, final ImageResource ir) {
		if (map == null) {
			queueCommands.add(new Command() {
				@Override
				public void execute() {
					addPointToMap(point, html, ir);
				}
			});

			return;
		}

		// Add to bounds
		if (bounds == null) {
			bounds = LatLngBounds.newInstance(point, point);
			map.setCenter(point, 12);
			grid.setWidget(0, 0, map);
		} else {
			bounds.extend(point);
		}

		// Add a marker
		final Marker marker;
		if (ir != null) {
			Icon icon = Icon.newInstance(ir.getSafeUri().asString());
			icon.setShadowURL(MainImageBundle.INSTANCE.mapShadow().getSafeUri().asString());
			icon.setIconSize(Size.newInstance(20, 34));
			icon.setShadowSize(Size.newInstance(37, 34));
			icon.setIconAnchor(Point.newInstance(9, 34));
			icon.setInfoWindowAnchor(Point.newInstance(9, 2));
			MarkerOptions options = MarkerOptions.newInstance(icon);
			marker = new Marker(point, options);
		} else {
			marker = new Marker(point);
		}

		// Content of the info window
		final InfoWindowContent content = new InfoWindowContent("<div style=\"text-align: left;\">" + html + "</div>");

		marker.addMarkerClickHandler(new MarkerClickHandler() {
			@Override
			public void onClick(MarkerClickEvent event) {
				map.getInfoWindow().open(marker.getLatLng(), content);
			}
		});

		map.addOverlay(marker);
	}

	/**
	 * Add a command to be executed upon the successful load of the Google Maps API.
	 * 
	 * @param command
	 */
	public void addQueueCommand(Command command) {
		if (map == null) {
			command.execute();
			return;
		}

		queueCommands.add(command);
	}

	public void centerOnPoints() {
		if (map == null) {
			queueCommands.add(new Command() {
				@Override
				public void execute() {
					centerOnPoints();
				}
			});

			return;
		}

		int backOffZoom = 1;
		if (succeeded.size() == 1) {
			backOffZoom = 6;
		}
		map.setZoomLevel(map.getBoundsZoomLevel(bounds) - backOffZoom);
		map.setCenter(bounds.getCenter());
	}

	/**
	 * @return The contained {@link MapWidget}
	 */
	public MapWidget getMap() {
		return map;
	}

	private int getCurrentCount() {
		return succeeded.size() + failed.size();
	}

	private String getDirectionsHtml(String address) {
		String html;
		html = "<b>Directions from:</b>";
		html += "<form target=_blank action=\"http://maps.google.com/maps\" method=get>";
		html += "<input type=text name=saddr size=11 id=saddr>&nbsp;<input type=submit value=Go>";
		html += "<input type=hidden name=daddr value=\"" + address + "\">";
		return html;
	}

	private void lastPointActions() {
		if (succeeded.size() > 0) {
			centerOnPoints();
		}

		if (failed.size() > 0) {
			String intro;
			if (addresses.size() == 1) {
				intro = "This address";
			} else {
				intro = "The following address" + ((failed.size() == 1) ? "" : "es");
			}
			intro += " could not be mapped:";
			errorPanel.add(new Label(intro));

			for (String address : failed) {
				HorizontalPanel hp = new HorizontalPanel();
				Anchor googleLink = new Anchor("Google it", "http://maps.google.com/maps?f=q&hl=en&geocode=&q=" + address, "_blank");
				hp.add(new Label(address));
				hp.add(new HTML("&nbsp;&nbsp;&nbsp;("));
				if (addresses.size() > 1) {
					hp.add(new HTML("&nbsp;|&nbsp;"));
				}
				hp.add(googleLink);
				hp.add(new HTML(")"));

				errorPanel.setStyleName("mapErrors");
				errorPanel.add(hp);
			}

			grid.setWidget(1, 0, errorPanel);
		}
	}

	private void processPoint(LatLng point, String address) {
		if (point == null) {
			if (getCurrentCount() == addresses.size()) {
				lastPointActions();
			}
			return;
		}

		addPointToMap(point, address.replaceAll("\n", "<br>") + "<br>" + getDirectionsHtml(address));

		if (getCurrentCount() == addresses.size()) {
			lastPointActions();
		}
	}
}
