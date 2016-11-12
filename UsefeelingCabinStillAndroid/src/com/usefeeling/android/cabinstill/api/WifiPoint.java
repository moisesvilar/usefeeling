package com.usefeeling.android.cabinstill.api;

import java.io.Serializable;

/**
 * POJO para datos sobre punto wifi.
 * @author Moisés Vilar.
 *
 */
public class WifiPoint implements Serializable {
	private static final long serialVersionUID = 4186957537895461049L;
	private String mac;
	private Long placeId;
	private String place;
	private String placeType;
	private Boolean hasIcon;
	private Double latitude;
	private Double longitude;
	
	/**
	 * @return the mac
	 */
	public String getMac() {
		return mac;
	}
	/**
	 * @param mac the mac to set
	 */
	public void setMac(String mac) {
		this.mac = mac;
	}
	/**
	 * @return the placeId
	 */
	public Long getPlaceId() {
		return placeId;
	}
	/**
	 * @param placeId the placeId to set
	 */
	public void setPlaceId(Long placeId) {
		this.placeId = placeId;
	}
	/**
	 * @return the place
	 */
	public String getPlace() {
		return place;
	}
	/**
	 * @param place the place to set
	 */
	public void setPlace(String place) {
		this.place = place;
	}
	/**
	 * @return the placeType
	 */
	public String getPlaceType() {
		return placeType;
	}
	/**
	 * @param placeType the placeType to set
	 */
	public void setPlaceType(String placeType) {
		this.placeType = placeType;
	}
	/**
	 * @return the hasIcon
	 */
	public Boolean hasIcon() {
		return hasIcon;
	}
	/**
	 * @param hasIcon the hasIcon to set
	 */
	public void setHasIcon(Boolean hasIcon) {
		this.hasIcon = hasIcon;
	}
	/**
	 * @return the latitude
	 */
	public Double getLatitude() {
		return latitude;
	}
	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	/**
	 * @return the longitude
	 */
	public Double getLongitude() {
		return longitude;
	}
	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	
	
}
