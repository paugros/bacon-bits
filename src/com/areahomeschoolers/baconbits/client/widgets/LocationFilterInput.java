package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.ApplicationData;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.maps.gwt.client.Geocoder;
import com.google.maps.gwt.client.GeocoderRequest;
import com.google.maps.gwt.client.GeocoderResult;
import com.google.maps.gwt.client.GeocoderStatus;

public class LocationFilterInput extends Composite {
	private TextBox locationInput = new TextBox();
	private String lastLocationText = "";
	private double lat;
	private double lng;
	private Command clearCommand;
	private Command changeCommand;
	private UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);
	private HorizontalPanel hp = new PaddedPanel();
	private DefaultListBox stateInput = new DefaultListBox();
	private DefaultListBox milesInput = new DefaultListBox();

	public LocationFilterInput() {
		milesInput.addItem("5", 5);
		milesInput.addItem("10", 10);
		milesInput.addItem("25", 25);
		milesInput.addItem("50", 50);
		milesInput.setValue(Constants.DEFAULT_SEARCH_RADIUS);
		milesInput.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				updateLocation(locationInput.getText(), lat, lng, milesInput.getIntValue());
			}
		});

		locationInput.setVisibleLength(30);
		locationInput.getElement().setAttribute("placeholder", "Address, city, or zip");

		reset();

		locationInput.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					locationInput.setFocus(false);
				}
			}
		});

		locationInput.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				if (lastLocationText.equals(locationInput.getText())) {
					return;
				}

				if (locationInput.getText().isEmpty()) {
					updateLocation("", 0, 0, 0);
					reset();

					clearCommand.execute();
				}

				GoogleMapWidget.runMapsCommand(new Command() {
					@Override
					public void execute() {
						GeocoderRequest request = GeocoderRequest.create();
						request.setAddress(locationInput.getText());
						GoogleMapWidget.getGeoCoder().geocode(request, new Geocoder.Callback() {
							@Override
							public void handle(JsArray<GeocoderResult> results, GeocoderStatus status) {
								if (status == GeocoderStatus.OK) {
									GeocoderResult location = results.get(0);

									lat = location.getGeometry().getLocation().lat();
									lng = location.getGeometry().getLocation().lng();

									updateLocation(location.getFormattedAddress(), lat, lng, milesInput.getIntValue());
								}
							}
						});
					}
				});
			}
		});

		stateInput.addItem("");
		for (int i = 0; i < Constants.STATE_NAMES.length; i++) {
			stateInput.addItem(Constants.STATE_NAMES[i]);
		}

		stateInput.setValue(Application.getCurrentLocation());

		stateInput.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				updateLocation(stateInput.getValue(), 0, 0, 0);
				updateEnabled();
			}
		});

		hp.add(new Label("within"));
		hp.add(milesInput);
		hp.add(new Label("miles of"));
		hp.add(locationInput);
		hp.add(new Label("in"));
		hp.add(stateInput);

		updateEnabled();

		initWidget(hp);
	}

	public void clearLocation() {
		updateLocation("", 0, 0, 0);
	}

	public double getLat() {
		return lat;
	}

	public double getLng() {
		return lng;
	}

	public int getRadius() {
		return milesInput.getIntValue();
	}

	public String getState() {
		return stateInput.getValue();
	}

	public String getText() {
		return locationInput.getText();
	}

	public void populate(String text) {
		lastLocationText = "0000000000";
		locationInput.setFocus(true);
		locationInput.setText(text);
		locationInput.setFocus(false);
	}

	public void setChangeCommand(Command command) {
		changeCommand = command;
	}

	public void setClearCommand(Command command) {
		clearCommand = command;
	}

	public void setText(String text) {
		locationInput.setText(text);
		lastLocationText = text;
	}

	private void reset() {
		if (!locationInput.getText().isEmpty()) {
			return;
		}
		locationInput.setText("");
	}

	private void updateEnabled() {
		String loc = Application.getCurrentLocation();
		if (!Common.isNullOrBlank(loc) && Application.getCurrentLat() == 0 && loc.length() == 2) {
			locationInput.setEnabled(false);
			milesInput.setEnabled(false);
			lastLocationText = "";
		} else {
			milesInput.setEnabled(true);
			locationInput.setEnabled(true);
		}

		reset();
	}

	private void updateLocation(String location, double lat, double lng, int radius) {
		ApplicationData ad = Application.getApplicationData();
		lastLocationText = location;
		locationInput.setText(location);
		ad.setCurrentLocation(location);
		ad.setCurrentLat(lat);
		ad.setCurrentLng(lng);
		userService.setCurrentLocation(location, lat, lng, radius, new Callback<Void>(false) {
			@Override
			protected void doOnSuccess(Void result) {
				changeCommand.execute();
			}
		});
	}

}
