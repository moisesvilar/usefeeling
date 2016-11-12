package com.usefeeling.android.cabinstill.map;

import java.util.List;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.usefeeling.android.R;
import com.usefeeling.android.cabinstill.api.UseFeeling;

/**
 * Capa de iconos a visualizar sobre el mapa.<br>
 * Sobreescribe el método draw de ItemizedIconOverlay para dibujar el margen que rodea a la imagen del elemento.<br>
 * Contiene una lista de objetos UfOverlayItem, que son los elementos que se han de mostrar sobre el mapa.
 * @author Moisés Vilar.
 *
 */
public class UfItemizedIconOverlay extends ItemizedIconOverlay<UfOverlayItem> {
	
	private static final int TITLE_MARGIN = 2;
	private static final int OFFSET = 3;
	
	/**
	 * Constructor-
	 * @param _context Contexto desde el que se instancia el objeto.
	 * @param _List Lista de elementos.
	 * @param _OnItemGestureListener Listener de escucha de gestos sobre los elementos.
	 */
	public UfItemizedIconOverlay (Context _context, List<UfOverlayItem> _List, OnItemGestureListener<UfOverlayItem> _OnItemGestureListener) {
		super(_context, _List, _OnItemGestureListener);
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		try {
			if (this.size()>0) {
				for (int i= 0; i<this.size(); i++) {
					UfOverlayItem item = this.getItem(i);
					if (item == null) return;
					if (item.getDrawable() == null) return;
					if (((BitmapDrawable)item.getDrawable()).getBitmap() == null) return;
					GeoPoint in = item.getPoint();
					Point out = new Point();
					mapView.getProjection().toPixels(in, out);
					int markerHeight = ((BitmapDrawable)item.getDrawable()).getBitmap().getHeight();
					Paint paintRect = new Paint(Paint.ANTI_ALIAS_FLAG);
					Rect rect = new Rect();
					rect.inset(-TITLE_MARGIN, -TITLE_MARGIN);
					rect.offsetTo(out.x - rect.width() / 2, out.y - markerHeight - rect.height() - OFFSET);
					int color = this.findColor(mapView.getContext(), item.getMappable().getAffinity());
					paintRect.setColor(color);
					canvas.drawRoundRect(new RectF(rect), 4, 4, paintRect);
				}
			}
		} catch (Exception e) {
			Log.e("UseFeeling.MapActivity.UfItemizedIconOverlay.draw", e.getMessage());
		}
	}
	
	/**
	 * Encuentra el color correcto del margen a dibujar en función del valor de afinidad del elemento con el usuario.
	 * @param context Contexto desde el que se invoca el método.
	 * @param affinity Afinidad del elemento con el usuario.
	 * @return Si no hay afinidad (null) o su valor es muy bajo (menor que 0.3), devuelve un color transparente.<br>
	 * Si la afinidad es superior a 0.3 e inferior a 0.5, devuelve un color verde.<br>
	 * Si la afinidad es superior a 0.5 e inferior a 0.75, devulve un color amarillo.<br>
	 * Si la afinidad es superior a 0.75, devuelve un color rojo.
	 */
	private int findColor(Context context, Float affinity) {
		if (affinity == null || affinity < 0.3) return Color.TRANSPARENT;
		if (affinity < UseFeeling.THRESHOLD_YELLOW) return context.getResources().getColor(R.color.stroke_red);
		else if (affinity < UseFeeling.THRESHOLD_GREEN) return context.getResources().getColor(R.color.green_yellow);
		else return context.getResources().getColor(R.color.stroke_green);
	}
	
	public void populateNow() {
		this.populate();
	}
}
