package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.shared.dto.ApplicationData;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.maps.gwt.client.Geocoder;
import com.google.maps.gwt.client.GeocoderRequest;
import com.google.maps.gwt.client.GeocoderResult;
import com.google.maps.gwt.client.GeocoderStatus;

public class GeocoderTextBox extends Composite {
	private TextBox input = new TextBox();
	private String lastLocationText;
	private double lat;
	private double lng;
	private Command clearCommand;
	private Command changeCommand;
	private static final String defaultSearchText = "Address, city, or zip";
	private UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);

	public GeocoderTextBox() {
		input.setVisibleLength(30);

		input.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				if (!input.getText().equals(defaultSearchText)) {
					return;
				}
				input.setText("");
				setNormalStyle();
			}
		});

		reset();

		input.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					input.setFocus(false);
				}
			}
		});

		input.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				if (lastLocationText != null && lastLocationText.equals(input.getText())) {
					return;
				}

				if (input.getText().isEmpty()) {
					reset();

					if (lastLocationText == null) {
						return;
					}

					clearCommand.execute();
				}

				lastLocationText = input.getText();

				GoogleMapWidget.runMapsCommand(new Command() {
					@Override
					public void execute() {
						GeocoderRequest request = GeocoderRequest.create();
						request.setAddress(input.getText());
						GoogleMapWidget.getGeoCoder().geocode(request, new Geocoder.Callback() {
							@Override
							public void handle(JsArray<GeocoderResult> results, GeocoderStatus status) {
								if (status == GeocoderStatus.OK) {
									GeocoderResult location = results.get(0);

									lat = location.getGeometry().getLocation().lat();
									lng = location.getGeometry().getLocation().lng();

									ApplicationData ad = Application.getApplicationData();
									input.setText(location.getFormattedAddress());
									ad.setCurrentLocation(location.getFormattedAddress());
									ad.setCurrentLat(lat);
									ad.setCurrentLng(lng);

									userService.setCurrentLocation(location.getFormattedAddress(), lat, lng, new Callback<Void>(false) {
										@Override
										protected void doOnSuccess(Void result) {
										}
									});

									changeCommand.execute();
								}
							}
						});
					}
				});
			}
		});

		initWidget(input);
	}

	public double getLat() {
		return lat;
	}

	public double getLng() {
		return lng;
	}

	public String getText() {
		return input.getText();
	}

	public void populate(String text) {
		lastLocationText = "0000000000";
		input.setFocus(true);
		input.setText(text);
		input.setFocus(false);
	}

	public void setChangeCommand(Command command) {
		changeCommand = command;
	}

	public void setClearCommand(Command command) {
		clearCommand = command;
	}

	public void setText(String text) {
		setNormalStyle();
		input.setText(text);
		lastLocationText = text;
	}

	private void reset() {
		if (!input.getText().isEmpty()) {
			return;
		}
		input.setText(defaultSearchText);
		input.getElement().getStyle().setColor("#666666");
		input.getElement().getStyle().setFontStyle(FontStyle.ITALIC);
	}

	private void setNormalStyle() {
		input.getElement().getStyle().setColor("#000000");
		input.getElement().getStyle().setFontStyle(FontStyle.NORMAL);
	}

}
