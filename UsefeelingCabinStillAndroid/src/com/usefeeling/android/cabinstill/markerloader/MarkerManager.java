package com.usefeeling.android.cabinstill.markerloader;

import java.util.concurrent.ArrayBlockingQueue;

import org.osmdroid.views.overlay.OverlayItem;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.usefeeling.android.R;
import com.usefeeling.android.cabinstill.api.UseFeeling;
import com.usefeeling.android.cabinstill.facades.ApplicationFacade;
import com.usefeeling.android.cabinstill.facades.SharedPreferencesFacade;
import com.usefeeling.android.cabinstill.imageloader.ImageManager;

/**
 * Gestor de imágenes a mostrar sobre mapa.
 * @author Moisés Vilar.
 *
 */
public class MarkerManager {

	private static ArrayBlockingQueue<MarkerRef> queue = new ArrayBlockingQueue<MarkerRef>(100);
	private Activity mContext;
	private static Drawable checkinDrawable;
	private static Drawable eventDrawable;
	private static Drawable promoDrawable;
	private static Drawable placeDrawable;
	private static Drawable userDrawable;
	private SharedPreferencesFacade prefs;
	private UseFeeling usefeeling;
	private Thread thread = new Thread(new Looper());
	
	/**
	 * Constructor.
	 * @param ctx Contexto desde el que se instancia el gestor.
	 */
	public MarkerManager(Activity ctx) {
		this.mContext = ctx;
		MarkerManager.eventDrawable = this.mContext.getResources().getDrawable(R.drawable.star_event);
		MarkerManager.promoDrawable = this.mContext.getResources().getDrawable(R.drawable.dollar_promo);
		MarkerManager.checkinDrawable = this.mContext.getResources().getDrawable(R.drawable.point_checkin);
		MarkerManager.placeDrawable = this.mContext.getResources().getDrawable(R.drawable.red_bar);
		MarkerManager.placeDrawable.setBounds(-MarkerManager.placeDrawable.getIntrinsicWidth() / 2, -MarkerManager.placeDrawable.getIntrinsicHeight(), MarkerManager.placeDrawable.getIntrinsicWidth() / 2, 0);
		MarkerManager.userDrawable = this.mContext.getResources().getDrawable(R.drawable.map_marker);
		MarkerManager.userDrawable.setBounds(-MarkerManager.userDrawable.getIntrinsicWidth() / 2, -MarkerManager.userDrawable.getIntrinsicHeight(), MarkerManager.userDrawable.getIntrinsicWidth() / 2, 0);
		this.prefs = new SharedPreferencesFacade(this.mContext);
		this.usefeeling = new UseFeeling(this.prefs.getUserId(), this.prefs.getPassword());
		this.thread.setPriority(Thread.MAX_PRIORITY);
		this.thread.start();
	}
	
	/**
	 * Muestra una imagen en un item.
	 * @param ref Referencia que contiene la URL de la imagen y el item donde visualizarla.
	 * @return La referencia o null si ha habido algún error.
	 */
	public MarkerRef display(MarkerRef ref) {
		try {
			if (ref.getUrl().startsWith(UseFeeling.USERID)) {
				((OverlayItem)ref.getItem()).setMarker(MarkerManager.userDrawable);
			}
			else if (ref.getUrl().startsWith(UseFeeling.PLACEID)){
				((OverlayItem)ref.getItem()).setMarker(MarkerManager.placeDrawable);
				MarkerManager.queue.put(ref);
			}
			else if (ref.getUrl().startsWith(UseFeeling.DEFAULT_ICON)) {
				((OverlayItem)ref.getItem()).setMarker(MarkerManager.placeDrawable);
				MarkerManager.queue.put(ref);
			}
			return ref;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Gestionará las descargas de la cola de marcadores.
	 * @author Moisés Vilar.
	 *
	 */
	private class Looper implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					MarkerRef ref = MarkerManager.queue.take();
					Bitmap bitmap = this.getBitmap(ref.getUrl());
					MarkerDisplayer displayer = new MarkerDisplayer(MarkerManager.this.mContext, ImageManager.createImageIcon(MarkerManager.this.mContext, bitmap, ref.getUrl()), (OverlayItem) ref.getItem());
					MarkerManager.this.mContext.runOnUiThread(displayer);
				} catch (Throwable t) {}
			}
		}
		
		/**
		 * Obtiene la imagen del icono
		 * @param url URL de la imagen.
		 * @return La imagen o null.
		 */
		private Bitmap getBitmap(String url) {
			//Inicializamos variables
			Bitmap bitmap = null;
			Boolean hasEvents = false;
			Boolean hasPromos = false;
			Boolean checkins = false;
			//Comprobamos si el icono tiene eventos, promos y/o checkins
			if (url.contains(UseFeeling.HAS_EVENTS)) hasEvents = true;
			if (url.contains(UseFeeling.HAS_PROMOS)) hasPromos = true;
			if (url.contains(UseFeeling.CHECKINS)) checkins = true;
			url = url.replace("&" + UseFeeling.HAS_EVENTS, "");
			url = url.replace("&" + UseFeeling.HAS_PROMOS, "");
			url = url.replace("&" + UseFeeling.CHECKINS, "");
			//La imagen es una foto de perfil, la descargamos de UF
			if (url.startsWith(UseFeeling.USERID)) {
				Long userid = Long.parseLong(url.substring(url.indexOf('=')+1));
				bitmap = MarkerManager.this.usefeeling.getProfilePicture(userid);
			}
			//La imagen es el icono personalizado del un local, lo descargamos de UF
			else if (url.startsWith(UseFeeling.PLACEID)) {
				Long placeid = Long.parseLong(url.substring(url.indexOf('=')+1));
				bitmap = MarkerManager.this.usefeeling.getVenueIcon(placeid);
				//Añadimos el borde
				if (url.contains("&" + UseFeeling.RED)) bitmap = ImageManager.addStroke(bitmap, Color.red(MarkerManager.this.mContext.getResources().getColor(R.color.stroke_red)));
				else if (url.contains("&" + UseFeeling.YELLOW)) bitmap = ImageManager.addStroke(bitmap, Color.red(MarkerManager.this.mContext.getResources().getColor(R.color.stroke_yellow)));
				else if (url.contains("&" + UseFeeling.GREEN)) bitmap = ImageManager.addStroke(bitmap, Color.red(MarkerManager.this.mContext.getResources().getColor(R.color.stroke_green)));
			}
			//La imagen es el icono por defecto del local, lo cogemos de los recursos
			else if (url.startsWith(UseFeeling.DEFAULT_ICON)) {
				String name = url.substring(url.indexOf('=')+1);
		    	String packageName = MarkerManager.this.mContext.getResources().getResourcePackageName(R.drawable.green_big_bowling);
		    	String typeName = MarkerManager.this.mContext.getResources().getResourceTypeName(R.drawable.green_big_bowling);
				int resId = ApplicationFacade.getResId(name, packageName, typeName, MarkerManager.this.mContext);
				if (resId < 1) return null;
				bitmap = ((BitmapDrawable)(MarkerManager.this.mContext.getResources().getDrawable(resId))).getBitmap();
			}
			//Añadimos extras, si son necesarios
			if (hasEvents) bitmap = ImageManager.overlayBitmaps(bitmap, ((BitmapDrawable)eventDrawable).getBitmap(), 0, 0);
			if (hasPromos) bitmap = ImageManager.overlayBitmaps(bitmap, ((BitmapDrawable)MarkerManager.promoDrawable).getBitmap(), bitmap.getWidth() - ((BitmapDrawable)MarkerManager.promoDrawable).getBitmap().getWidth() , 0);
			if (checkins) bitmap = ImageManager.overlayBitmaps(bitmap, ((BitmapDrawable)MarkerManager.checkinDrawable).getBitmap(), 0, ((BitmapDrawable)MarkerManager.checkinDrawable).getBitmap().getHeight());
			return bitmap;
		}
		
	}
}
