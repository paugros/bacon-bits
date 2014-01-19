package com.areahomeschoolers.baconbits.shared;

public interface HasAddress {
	public String getAddress();

	public boolean getAddressChanged();

	public String getCity();

	public double getLat();

	public double getLng();

	public String getState();

	public String getStreet();

	public String getZip();

	public void setAddress(String address);

	public void setAddressChanged(boolean changed);

	public void setCity(String city);

	public void setLat(double lat);

	public void setLng(double lng);

	public void setState(String state);

	public void setStreet(String street);

	public void setZip(String zip);
}
