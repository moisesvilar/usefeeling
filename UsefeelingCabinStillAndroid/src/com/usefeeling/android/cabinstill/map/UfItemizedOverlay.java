package com.usefeeling.android.cabinstill.map;

import java.util.ArrayList;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.usefeeling.android.R;
import com.usefeeling.android.cabinstill.activities.MapActivity;
import com.usefeeling.android.cabinstill.widgets.TransparentPanel;

/**
 * Capa a visualizar sobre el mapa.<br>
 * Contiene un objeto UfItemizedIconOverlay con los iconos a visualizar en la capa.<br>
 * Implementa los métodos necesarios para manejar los eventos de pulsación sobre cualquier elemento de la capa,
 * concretamente, visualizar la información del elemento en el panel transparente o situar el ListView en el
 * elemento correspondiente, en el caso de que estos widgets estén disponibles en la interfaz de usuario.
 * @author Moisés Vilar
 *
 */
public class UfItemizedOverlay {

	private UfItemizedIconOverlay iconOverlay;
	private MapView mMapView;
	private Activity mActivity;
	private final OnItemGestureListener<UfOverlayItem> mOnItemGestureListener = new OnItemGestureListener<UfOverlayItem>() {
		@Override
		public boolean onItemLongPress(int index, UfOverlayItem item) {
			return true;
		}

		@Override
		public boolean onItemSingleTapUp(int index, UfOverlayItem item) {
			return onSingleTapUpHelper(index, item);
		}
	};

	/**
	 * Constructor. 
	 * @param context Contexto desde el que se instancia el objeto.
	 * @param defaultMarker Marcador por defecto para la capa.
	 * @param type Tipo de capa: locales o usuarios.
	 */
	public UfItemizedOverlay(MapView mapView, Activity activity) {
		this.mMapView = mapView;	
		this.mActivity = activity;
		this.iconOverlay = new UfItemizedIconOverlay(this.mActivity, new ArrayList<UfOverlayItem>(), this.mOnItemGestureListener);
	}

	/**
	 * Manejador del evento onTap sobre un elemento del mapa.<br>
	 * Si el panel transparente está disponible, muestra la información del elemento sobre él.<br>
	 * Si el ListView está disponible, establece su posición en el elemento correspondiente.
	 * @param index Índice del elemento.
	 * @param item Elemento.
	 * @return Siempre true.
	 */
	public boolean onSingleTapUpHelper(int index, UfOverlayItem item) {
		MapActivity.currentItem = item;
		TransparentPanel panel = (TransparentPanel)this.mActivity.findViewById(R.id.transparentPanel);
		if (panel != null) {
			TextView tvTitle = (TextView)panel.findViewById(R.id.tvTitle);
			TextView tvSubtitle = (TextView)panel.findViewById(R.id.tvSubtitle);
			TextView tvDescription = (TextView)panel.findViewById(R.id.tvDescription);
			tvTitle.setText(item.getMappable().getTitle());
			tvSubtitle.setText(item.getMappable().getSubtitle());
			tvDescription.setText(item.getMappable().getMapDescription(this.mActivity));				
			Animation animShow = AnimationUtils.loadAnimation(this.mActivity, R.anim.popup_show);
			this.mMapView.getController().animateTo(new GeoPoint(item.getMappable().getLatitude(), item.getMappable().getLongitude()));
			panel.setVisibility(View.VISIBLE);
			panel.startAnimation(animShow);
		}
		return true;
	}
	
	/**
	 * Añade un elemento a la capa de iconos.
	 * @param item El elemento.
	 */
	public void addItem(UfOverlayItem item){
		this.iconOverlay.addItem(item);
	}
	
	/**
	 * Devuelve la capa de iconos.
	 * @return La capa de iconos.
	 */
	public UfItemizedIconOverlay getOverlay(){
		return this.iconOverlay;
	}
	
	public void populateNow() {
		this.iconOverlay.populateNow();
	}
}
