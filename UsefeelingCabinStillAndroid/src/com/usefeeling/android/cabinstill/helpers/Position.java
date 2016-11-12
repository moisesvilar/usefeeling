package com.usefeeling.android.cabinstill.helpers;

import android.app.Activity;

import com.usefeeling.android.cabinstill.interfaces.Mappable;

public class Position implements Mappable {

	private static final long serialVersionUID = 3614640705253643920L;
	private String title;
	private String subtitle;
	private String description;
	private String imageUrl;
	private Double latitude;
	private Double longitude;
	
	/**
	 * Constructor por defecto.
	 */
	public Position() {
		this.title = "";
		this.subtitle = "";
		this.description = "";
		this.imageUrl = "";
	}
	
	/**
	 * Constructor.	
	 * @param title
	 * @param subtitle
	 * @param description
	 * @param imageUrl
	 * @param latitude
	 * @param longitude
	 */
	public Position(String title, String subtitle, String description, String imageUrl,	Double latitude, Double longitude) {
		super();
		this.title = title;
		this.subtitle = subtitle;
		this.description = description;
		this.imageUrl = imageUrl;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	/**
	 * Constructor
	 * @param title
	 * @param latitude
	 * @param longitude
	 * @param imageUrl
	 */
	public Position(String title, Double latitude, Double longitude, String imageUrl) {
		super();
		this.title = title;
		this.latitude = latitude;
		this.longitude = longitude;
		this.imageUrl = imageUrl;
	}
	
	/**
	 * Constructor
	 * @param title
	 * @param latitude
	 * @param longitude
	 */
	public Position(String title, Double latitude, Double longitude) {
		super();
		this.title = title;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * Constructor
	 * @param latitude
	 * @param longitude
	 */
	public Position(Double latitude, Double longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	@Override
	public String getSubtitle() {
		return this.subtitle;
	}

	@Override
	public String getMapDescription(Activity context) {
		return this.description;
	}

	@Override
	public String getImageUrl() {
		return this.imageUrl;
	}

	@Override
	public String getMapImageUrl() {
		return this.imageUrl;
	}

	@Override
	public Double getLatitude() {
		return this.latitude;
	}

	@Override
	public Double getLongitude() {
		return this.longitude;
	}

	@Override
	public Float getAffinity() {
		return 1.0f;
	}

	@Override
	public Long getId() {
		return null;
	}

}
