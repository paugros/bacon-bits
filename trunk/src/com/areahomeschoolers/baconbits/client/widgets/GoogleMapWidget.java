package com.areahomeschoolers.baconbits.client.widgets;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.ajaxloader.client.AjaxLoader;
import com.google.gwt.ajaxloader.client.AjaxLoader.AjaxLoaderOptions;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.maps.gwt.client.Geocoder;
import com.google.maps.gwt.client.Geocoder.Callback;
import com.google.maps.gwt.client.GeocoderRequest;
import com.google.maps.gwt.client.GeocoderResult;
import com.google.maps.gwt.client.GeocoderStatus;
import com.google.maps.gwt.client.GoogleMap;
import com.google.maps.gwt.client.InfoWindow;
import com.google.maps.gwt.client.InfoWindowOptions;
import com.google.maps.gwt.client.LatLng;
import com.google.maps.gwt.client.LatLngBounds;
import com.google.maps.gwt.client.MapOptions;
import com.google.maps.gwt.client.MapTypeId;
import com.google.maps.gwt.client.Marker;
import com.google.maps.gwt.client.Marker.ClickHandler;
import com.google.maps.gwt.client.MarkerOptions;
import com.google.maps.gwt.client.MouseEvent;

public class GoogleMapWidget extends Composite {
	private static Geocoder geocoder;

	private static boolean apiIsLoaded = false;

	public static boolean apiIsloaded() {
		return apiIsLoaded;
	}

	public static Geocoder getGeoCoder() {
		return geocoder;
	}

	public static void runMapsCommand(final Command cmd) {
		if (apiIsLoaded) {
			cmd.execute();
			return;
		}

		AjaxLoaderOptions options = AjaxLoaderOptions.newInstance();
		options.setOtherParms("key=AIzaSyBPxbeFCFBNAUxprA4_FSRcJ6AOVAQJr9A&sensor=false");
		Runnable callback = new Runnable() {
			@Override
			public void run() {
				apiIsLoaded = true;
				geocoder = Geocoder.create();
				cmd.execute();
			}
		};
		AjaxLoader.loadApi("maps", "3", callback, options);
	}

	private GoogleMap map;
	private LatLngBounds bounds;
	private List<String> failed = new ArrayList<String>();
	private List<String> succeeded = new ArrayList<String>();
	private List<String> addresses = new ArrayList<String>();
	private HTML mapContainer = new HTML();
	private Grid grid = new Grid(2, 1);
	private VerticalPanel errorPanel = new VerticalPanel();
	private MapOptions options = MapOptions.create();

	public GoogleMapWidget(final String... addresses) {
		initWidget(grid);
		addStyleName("GoogleMap");
		grid.setWidth("100%");
		grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
		grid.setWidget(0, 0, mapContainer);

		mapContainer.setHeight("500px");
		mapContainer.setWidth("99%");
		mapContainer.setStyleName("mapWidget");

		if (addresses != null) {
			runMapsCommand(new Command() {
				@Override
				public void execute() {
					for (final String address : addresses) {
						addAddress(address);
					}
				}
			});
		}
	}

	private void addAddress(final String address) {
		if (address == null) {
			return;
		}

		addresses.add(address);

		GeocoderRequest request = GeocoderRequest.create();
		request.setAddress(address);

		geocoder.geocode(request, new Callback() {
			@Override
			public void handle(JsArray<GeocoderResult> results, GeocoderStatus status) {
				String text = address.replaceAll("\n", "<br>") + "<br>" + getDirectionsHtml(address);
				if (status == GeocoderStatus.OK) {
					GeocoderResult location = results.get(0);
					succeeded.add(address);
					addMarker(location.getGeometry().getLocation(), text);
				} else {
					failed.add(address);
				}

				if (succeeded.size() + failed.size() == addresses.size()) {
					lastPointActions();
				}
			}
		});

	}

	private void addMarker(final LatLng point, final String html) {
		if (map == null) {
			options.setCenter(point);
			options.setZoom(8.0);
			options.setMapTypeId(MapTypeId.ROADMAP);
			map = GoogleMap.create(mapContainer.getElement(), options);
		}

		// Add to bounds
		if (bounds == null) {
			bounds = LatLngBounds.create(point, point);
		} else {
			bounds.extend(point);
		}

		// Add a marker
		MarkerOptions mo = MarkerOptions.create();
		mo.setPosition(point);
		mo.setMap(map);
		final Marker marker = Marker.create(mo);

		// Content of the info window
		InfoWindowOptions wo = InfoWindowOptions.create();
		wo.setContent("<div style=\"text-align: left;\">" + html + "</div>");
		final InfoWindow content = InfoWindow.create(wo);

		marker.addClickListener(new ClickHandler() {
			@Override
			public void handle(MouseEvent event) {
				content.open(map, marker);
			}
		});
	}

	private double getBoundsZoomLevel(LatLngBounds bounds) {
		int ZOOM_MAX = 21;

		int height = 500;
		int width = 500;

		LatLng ne = bounds.getNorthEast();
		LatLng sw = bounds.getSouthWest();

		double latFraction = (latRad(ne.lat()) - latRad(sw.lat())) / Math.PI;

		double lngDiff = ne.lng() - sw.lng();
		double lngFraction = ((lngDiff < 0) ? (lngDiff + 360) : lngDiff) / 360;

		double latZoom = zoom(mapContainer.getOffsetHeight(), height, latFraction);
		double lngZoom = zoom(mapContainer.getOffsetWidth(), width, lngFraction);

		double zoom = Math.min(latZoom, lngZoom);
		return zoom > ZOOM_MAX ? ZOOM_MAX : zoom;
	}

	private String getDirectionsHtml(String address) {
		String html;
		html = "<b>Directions from:</b>";
		html += "<form target=_blank action=\"http://maps.google.com/maps\" method=get>";
		html += "<input type=text name=saddr size=20 id=saddr>&nbsp;<input type=submit value=Go>";
		html += "<input type=hidden name=daddr value=\"" + address + "\">";
		return html;
	}

	private void lastPointActions() {
		if (succeeded.size() > 0) {
			map.setCenter(bounds.getCenter());
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					double zoom = getBoundsZoomLevel(bounds);
					if (succeeded.size() == 1) {
						zoom -= 5;
					}
					map.setZoom(zoom);
				}
			});
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
				if (failed.size() > 1) {
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

	private double latRad(double lat) {
		double sin = Math.sin(lat * Math.PI / 180);
		double radX2 = Math.log((1 + sin) / (1 - sin)) / 2;
		return Math.max(Math.min(radX2, Math.PI), -Math.PI) / 2;
	}

	private double zoom(int mapPx, int worldPx, double fraction) {
		return Math.floor(Math.log(mapPx / worldPx / fraction) / 0.6931471805599453);
	}
}
