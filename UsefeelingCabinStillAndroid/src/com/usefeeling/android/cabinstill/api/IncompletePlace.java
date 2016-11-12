package com.usefeeling.android.cabinstill.api;

/**
 * POJO para datos sobre locales incompletos.
 * @author Moisés Vilar.
 *
 */
public class IncompletePlace extends Place {
	private static final long serialVersionUID = -222645473367970847L;
	private Boolean hasCoordinates;
	private Boolean hasWifiPoints;
	/**
	 * @return the hasCoordinates
	 */
	public Boolean getHasCoordinates() {
		return hasCoordinates;
	}
	/**
	 * @param hasCoordinates the hasCoordinates to set
	 */
	public void setHasCoordinates(Boolean hasCoordinates) {
		this.hasCoordinates = hasCoordinates;
	}
	/**
	 * @return the hasWifiPoints
	 */
	public Boolean getHasWifiPoints() {
		return hasWifiPoints;
	}
	/**
	 * @param hasWifiPoints the hasWifiPoints to set
	 */
	public void setHasWifiPoints(Boolean hasWifiPoints) {
		this.hasWifiPoints = hasWifiPoints;
	}
	
	

}
