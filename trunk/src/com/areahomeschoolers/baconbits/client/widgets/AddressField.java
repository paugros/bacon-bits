package com.areahomeschoolers.baconbits.client.widgets;

import java.util.HashMap;
import java.util.Map;

import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.HasAddress;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.maps.gwt.client.Geocoder;
import com.google.maps.gwt.client.GeocoderAddressComponent;
import com.google.maps.gwt.client.GeocoderRequest;
import com.google.maps.gwt.client.GeocoderResult;
import com.google.maps.gwt.client.GeocoderStatus;

public class AddressField {
	public static void validateAddress(final HasAddress item, final Command saveCommand) {
		if (!item.getAddressChanged()) {
			saveCommand.execute();
			return;
		}

		item.setAddressChanged(false);

		if (Common.isNullOrBlank(item.getAddress()) && Common.isNullOrBlank(item.getStreet()) && Common.isNullOrBlank(item.getZip())) {
			saveCommand.execute();
			return;
		}

		GoogleMapWidget.runMapsCommand(new Command() {
			@Override
			public void execute() {
				String address = item.getAddress();
				if (Common.isNullOrBlank(address)) {
					item.setAddress("");
					item.setCity(null);
					item.setLat(0);
					item.setLng(0);
					item.setState(null);
					item.setStreet(null);
					item.setZip(null);
					item.setAddressChanged(true);
					saveCommand.execute();
					return;
				}

				GeocoderRequest request = GeocoderRequest.create();
				request.setAddress(address);
				GoogleMapWidget.getGeoCoder().geocode(request, new Geocoder.Callback() {
					@Override
					public void handle(JsArray<GeocoderResult> results, GeocoderStatus status) {
						if (status == GeocoderStatus.OK) {
							GeocoderResult location = results.get(0);
							Map<String, String> parts = new HashMap<String, String>();
							for (int i = 0; i < location.getAddressComponents().length(); i++) {
								GeocoderAddressComponent c = location.getAddressComponents().get(i);
								parts.put(c.getTypes().get(0), c.getShortName());
							}
							// https://developers.google.com/maps/documentation/geocoding/#Types
							item.setAddress(location.getFormattedAddress());
							String street = verifyBlank(parts.get("street_number"));
							String route = verifyBlank(parts.get("route"));
							if (!route.isEmpty()) {
								street += " " + route;
							}
							item.setStreet(street);
							item.setCity(parts.get("locality"));
							item.setState(parts.get("administrative_area_level_1"));
							item.setZip(parts.get("postal_code"));
							item.setLat(location.getGeometry().getLocation().lat());
							item.setLng(location.getGeometry().getLocation().lng());
						}

						saveCommand.execute();
					}
				});
			}
		});
	}

	private static String verifyBlank(String input) {
		if (input == null) {
			return "";
		}
		if (input.equals("null") || input.equals("undefined")) {
			return "";
		}

		return input;
	}

	private VerticalPanel vp = new VerticalPanel();

	private FormField addressField;

	public AddressField(final HasAddress address) {
		final FieldDisplayLink addressDisplay = new FieldDisplayLink();
		addressDisplay.setTarget("_blank");
		// street input
		final TextBox addressInput = new TextBox();
		addressInput.setVisibleLength(50);
		addressInput.setMaxLength(200);

		vp.add(addressInput);

		addressField = new FormField("Address:", vp, addressDisplay);
		addressField.setInitializer(new Command() {
			@Override
			public void execute() {
				addressDisplay.setText(Common.getDefaultIfNull(address.getAddress()));
				addressDisplay.setHref("http://maps.google.com/maps?q=" + address.getAddress());
				addressInput.setText(address.getAddress());
			}
		});
		addressField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				address.setAddress(addressInput.getText());
				address.setAddressChanged(true);
			}
		});
		addressField.setValidator(new Validator(addressInput, new ValidatorCommand() {
			@Override
			public void validate(Validator validator) {
				validator.setError(addressField.isRequired() && addressInput.getText().isEmpty());
			}
		}));
	}

	public FormField getFormField() {
		return addressField;
	}

	public VerticalPanel getInputPanel() {
		return vp;
	}

}
