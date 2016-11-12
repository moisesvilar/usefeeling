package com.usefeeling.android.cabinstill.markerloader;

import org.osmdroid.views.overlay.OverlayItem;

/**
 * Implementación de la interfaz MarkerRef para marcadores de OsmDroid.
 * @author Moisés Vilar.
 *
 */
public class OsmdroidMarkerRef implements MarkerRef {

	private String url;
	private OverlayItem item;
	
	/**
	 * Constructor.
	 * @param url URL de la imagen.
	 * @param item OverlayItem onde se mostrará la imagen.
	 */
	public OsmdroidMarkerRef(String url, OverlayItem item) {
		this.url = url;
		this.item = item;
	}
	
	@Override
	public String getUrl() {
		return this.url;
	}

	@Override
	public OverlayItem getItem() {
		return this.item;
	}

}
