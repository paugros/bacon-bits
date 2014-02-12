package com.areahomeschoolers.baconbits.client.widgets;

import java.util.HashMap;
import java.util.Map;

import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.HasAddress;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Label;
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
				if (!Common.isNullOrBlank(item.getStreet()) || !Common.isNullOrBlank(item.getZip())) {
					address = item.getStreet() + " " + item.getZip();
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
							item.setStreet(parts.get("street_number") + " " + parts.get("route"));
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

	private FormField addressField;

	public AddressField(final HasAddress address) {
		final FieldDisplayLink addressDisplay = new FieldDisplayLink();
		addressDisplay.setTarget("_blank");
		PaddedPanel addressInput = new PaddedPanel();
		// street input
		final TextBox street = new TextBox();
		street.setMaxLength(100);
		Label streetLabel = new Label("Number and street");
		streetLabel.addStyleName("smallText");
		VerticalPanel streetPanel = new VerticalPanel();
		streetPanel.add(streetLabel);
		streetPanel.add(street);

		// zip input
		final NumericTextBox zip = new NumericTextBox();
		zip.setMaxLength(5);
		zip.setVisibleLength(5);
		Label zipLabel = new Label("Zip");
		zipLabel.addStyleName("smallText");
		VerticalPanel zipPanel = new VerticalPanel();
		zipPanel.add(zipLabel);
		zipPanel.add(zip);

		addressInput.add(streetPanel);
		addressInput.add(zipPanel);

		addressField = new FormField("Address:", addressInput, addressDisplay);
		addressField.setInitializer(new Command() {
			@Override
			public void execute() {
				addressDisplay.setText(Common.getDefaultIfNull(address.getAddress()));
				addressDisplay.setHref("http://maps.google.com/maps?q=" + address.getAddress());
				street.setText(address.getStreet());
				zip.setText(address.getZip());
			}
		});
		addressField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				address.setStreet(street.getText());
				address.setZip(zip.getText());
				address.setAddressChanged(true);
			}
		});
		addressField.setValidator(new Validator(street, new ValidatorCommand() {
			@Override
			public void validate(Validator validator) {
				validator.setError(addressField.isRequired() && street.getText().isEmpty() && zip.getText().isEmpty());
			}
		}));
	}

	public FormField getFormField() {
		return addressField;
	}

}
