package com.usefeeling.android.cabinstill.imageloader;

import android.widget.ImageView;

import com.usefeeling.android.cabinstill.markerloader.MarkerRef;

/**
 * Guarda las referencias a las imágenes.
 * @author Moisés Vilar.
 *
 */
public class ImageRef implements MarkerRef {
	
	private String url;
	private ImageView view;
	
	/**
	 * Constructor.
	 * @param _url URL de la imagen.
	 * @param _overlayItem Widget donde se mostrará la imagen.
	 */
	public ImageRef(String _url, ImageView _view) {
		this.url = _url;
		this.view = _view;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public ImageView getItem() {
		return this.view;
	}
}
