package com.usefeeling.android.cabinstill.interfaces;

import java.io.Serializable;

import android.app.Activity;


/**
 * Intezfaz para elementos a visualizar sobre el mapa.
 * @author Moisés Vilar.
 *
 */
public interface Mappable extends Serializable {
	public Long getId();
	public String getTitle();
	public String getSubtitle();
	public String getMapDescription(Activity context);
	public String getImageUrl();
	public String getMapImageUrl();
	public Double getLatitude();
	public Double getLongitude();
	public Float getAffinity();
	public String toString();
}
