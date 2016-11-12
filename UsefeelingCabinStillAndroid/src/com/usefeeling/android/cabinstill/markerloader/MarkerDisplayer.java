package com.usefeeling.android.cabinstill.markerloader;

import org.osmdroid.views.overlay.OverlayItem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Muestra la imagen en el marcador del mapa en el hilo principal.
 * @author Moisés Vilar.
 *
 */
public class MarkerDisplayer implements Runnable {
	
	private Bitmap mBitmap;
	private OverlayItem mItem;
	private Context mContext;
	
	/**
	 * Constructor.
	 * @param context Contexto.
	 * @param _bitmap Imagen.
	 * @param _overlayItem Widget donde se visualizará la imagen.
	 */
	public MarkerDisplayer(Context _context, Bitmap _bitmap, OverlayItem _overlayItem) {
		this.mContext = _context;
		this.mBitmap = _bitmap;
		this.mItem = _overlayItem;
	}
	

	@Override
	public void run() {
		if (this.mBitmap != null) {
			Drawable drw = new BitmapDrawable(this.mContext.getResources(), this.mBitmap);
			drw.setBounds(-drw.getIntrinsicWidth() / 2, -drw.getIntrinsicHeight(), drw.getIntrinsicWidth() / 2, 0);
			mItem.setMarker(drw);
		}
	}
}
