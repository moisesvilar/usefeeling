package com.usefeeling.android.cabinstill.imageloader;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class ImageDisplayer implements Runnable {
	private Bitmap mBitmap;
	private ImageView mItem;
	
	/**
	 * Constructor.
	 * @param context Contexto.
	 * @param _bitmap Imagen.
	 * @param _overlayItem Widget donde se visualizará la imagen.
	 */
	public ImageDisplayer(Bitmap _bitmap, ImageView _overlayItem) {
		this.mBitmap = _bitmap;
		this.mItem = _overlayItem;
	}
	

	@Override
	public void run() {
		if (this.mBitmap != null && this.mItem != null) {
			this.mItem.setImageBitmap(this.mBitmap);
		}
	}
}
