package com.usefeeling.android.cabinstill.map;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import com.usefeeling.android.cabinstill.interfaces.Mappable;

/**
 * Clase a la que pertenecen cada uno de los elementos que se visualiza sobre el mapa.<br>
 * Posee como un atributo un objeto Mappable que contiene los datos específicos del elemento. 
 * @author Moisés Vilar.
 *
 */
public class UfOverlayItem extends OverlayItem {
	
	private Mappable mappable;
	private MapView mMapView;
	
	/**
	 * Constructor.
	 * @param aTitle Título del elemento.
	 * @param aDescription Descripción del elemento.
	 * @param aGeoPoint Coordenadas del elemento.
	 * @param mappable Objeto Mappable del elemento.
	 */
	public UfOverlayItem(Activity activity, MapView mapview, Mappable mappable) {
		super (mappable.getTitle(), mappable.getMapDescription(activity), new GeoPoint(mappable.getLatitude(), mappable.getLongitude()));
		this.mappable = mappable;
		this.mMapView = mapview;
	}
	
	/**
	 * Obtiene el objeto Mappable de este OverlayItem.
	 * @return El mappable asociado.
	 */
	public Mappable getMappable() {
		return this.mappable;
	}
	
	@Override
	public void setMarker(Drawable marker) {
		super.setMarker(marker);
		if (this.mMapView != null && this.mMapView != null) this.mMapView.postInvalidate(); 
	}

}
