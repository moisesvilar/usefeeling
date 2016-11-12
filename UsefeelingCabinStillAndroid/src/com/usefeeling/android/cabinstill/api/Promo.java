package com.usefeeling.android.cabinstill.api;

import java.io.Serializable;

import android.app.Activity;

import com.usefeeling.android.cabinstill.interfaces.Mappable;

/**
 * POJO para datos sobre promociones.
 * @author Moisés Vilar.
 *
 */
public class Promo implements Serializable, Mappable {

	private static final long serialVersionUID = -4046372392195895060L;
	private Long promoId;
	private String name;
	private String description;
	private Long initTime;
	private Long endTime;
	private Float affinity;
	private Boolean isForEating;
	private Boolean isForDrinking;
	private Boolean isForPartying;
	private Place place;
	private Boolean hasImage;
	private String affinityStr;

	
	public Boolean getHasImage() {
		return hasImage;
	}

	public void setHasImage(Boolean hasImage) {
		this.hasImage = hasImage;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the isForEating
	 */
	public Boolean isForEating() {
		return isForEating;
	}

	/**
	 * @return the isForDrinking
	 */
	public Boolean isForDrinking() {
		return isForDrinking;
	}

	/**
	 * @return the isForPartying
	 */
	public Boolean isForPartying() {
		return isForPartying;
	}

	/**
	 * @return the promoId.
	 */
	public Long getPromoId() {
		return promoId;
	}

	/**
	 * @param promoId the eventId to set
	 */
	public void setPromoId(Long eventId) {
		this.promoId = eventId;
	}

	/**
	 * @return the initTime
	 */
	public Long getInitTime() {
		return initTime;
	}

	/**
	 * @param initTime the initTime to set
	 */
	public void setInitTime(Long initTime) {
		this.initTime = initTime;
	}

	/**
	 * @return the endTime
	 */
	public Long getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return the place
	 */
	public Place getPlace() {
		return place;
	}

	/**
	 * @param place the place to set
	 */
	public void setPlace(Place place) {
		this.place = place;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @param isForEating the isForEating to set
	 */
	public void setIsForEating(Boolean isForEating) {
		this.isForEating = isForEating;
	}

	/**
	 * @param isForDrinking the isForDrinking to set
	 */
	public void setIsForDrinking(Boolean isForDrinking) {
		this.isForDrinking = isForDrinking;
	}

	/**
	 * @param isForPartying the isForPartying to set
	 */
	public void setIsForPartying(Boolean isForPartying) {
		this.isForPartying = isForPartying;
	}

	@Override
	public Float getAffinity() {
		return this.affinity;
	}

	@Override
	public Double getLatitude() {
		if (this.place == null) return null;
		return this.place.getLatitude();
	}

	@Override
	public Double getLongitude() {
		if (this.place == null) return null;
		return this.place.getLongitude();
	}

	/**
	 * @param affinity the affinity to set
	 */
	public void setAffinity(Float affinity) {
		this.affinity = affinity;
	}

	@Override
	public String getTitle() {
		return this.name;
	}

	@Override
	public String getSubtitle() {
		if (this.place == null) return "";
		String result = "";
		if (this.place.getPlaceType() != null && !this.place.getPlaceType().trim().equals("")) result = this.place.getPlaceType() + " ";
		result = result + this.place.getName();
		return result;
	}

	@Override
	public String getMapDescription(Activity context) {
		if (this.place == null) return null;
		return this.name + " " + (this.description.length() < 50 ? this.description : this.description.substring(0, 50) + "...");
	}

	@Override
	public String getImageUrl() {
		if (!this.hasImage) return "portrait=default_portrait.png";
		else return "promoid=" + this.getPromoId();
	}
	
	@Override
	public String getMapImageUrl() {
		if (this.place == null) return "";
		String prefix = "";
		if (this.affinityStr.equalsIgnoreCase(UseFeeling.GREEN.toString())) prefix = "green_";
		else if (this.affinityStr.equalsIgnoreCase(UseFeeling.YELLOW.toString())) prefix = "yellow_";
		else prefix = "red_";
		String result = "defaulticon=" + prefix + this.place.getIcon();
		if (this.place.hasEvents()) result += "&hasevents";
		result += "&haspromos";
		return result;		
	}
	
	@Override
	public String toString() {
		return this.getTitle();
	}

	/**
	 * @return the affinityStr
	 */
	public String getAffinityStr() {
		if (affinityStr == null || affinityStr.trim().equals("")) {
			if (this.affinity < 0.3) this.affinityStr = UseFeeling.RED.toString();
			else if (this.affinity < 0.6) this.affinityStr = UseFeeling.YELLOW.toString();
			else this.affinityStr = UseFeeling.GREEN.toString();
		}
		return affinityStr;
	}

	/**
	 * @param affinityStr the affinityStr to set
	 */
	public void setAffinityStr(String affinityStr) {
		this.affinityStr = affinityStr;
	}

	@Override
	public Long getId() {
		return this.promoId;
	}

}
