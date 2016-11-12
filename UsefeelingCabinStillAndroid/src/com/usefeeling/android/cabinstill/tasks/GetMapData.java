package com.usefeeling.android.cabinstill.tasks;

import java.util.ArrayList;

import org.osmdroid.views.MapView;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import com.usefeeling.android.R;
import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.api.ResultMessages;
import com.usefeeling.android.cabinstill.facades.DataFacade;
import com.usefeeling.android.cabinstill.interfaces.Mappable;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;
import com.usefeeling.android.cabinstill.map.UfItemizedOverlay;
import com.usefeeling.android.cabinstill.map.UfOverlayItem;
import com.usefeeling.android.cabinstill.markerloader.MarkerManager;
import com.usefeeling.android.cabinstill.markerloader.OsmdroidMarkerRef;

/**
 * Tarea para recoger los locales de la caché y visualizarlos en el mapa.
 * @author Moisés Vilar.
 *
 */
public class GetMapData extends AbstractTask {

	private DataFacade dataFacade;
	private MapView mapView;
	private MarkerManager markerManager;
	private ArrayList<UfItemizedOverlay> overlays;
	
	public GetMapData(Activity context, OnTaskCompleted listener, Object... params) {
		super(context, listener, params);
		this.mapView = (MapView)params[0];
		this.markerManager = (MarkerManager)params[1];
		this.dataFacade = new DataFacade(this.mContext);
		this.overlays = new ArrayList<UfItemizedOverlay>();
	}

	@Override
	public void run() {
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		try {
			//NOW
			this.overlays.add(this.addOverlay(this.dataFacade.getNow(this.dataFacade.getProposalsLimit())));
			//TODAY
			this.overlays.add(this.addOverlay(this.dataFacade.getToday(this.dataFacade.getProposalsLimit())));
			//LATER
			this.overlays.add(this.addOverlay(this.dataFacade.getLater(this.dataFacade.getProposalsLimit())));
			//FAMOUS
			this.overlays.add(this.addOverlay(this.dataFacade.getFamous()));
			//EVERYTHING
			this.overlays.add(this.addOverlay(this.dataFacade.getEverything()));
		} catch (Throwable t) {
			result = new Result(ResultCodes.Exception, t.getMessage());
		} finally {
			this.onPostExecute(new Object[]{result, overlays});
		}
	}
	
	/**
	 * Añade una capa de elementos al mapa.
	 * @param mapView Mapa.
	 * @param activity Activity.
	 * @param markerManager Gestor de marcadores.
	 * @param items Elementos.
	 */
	private UfItemizedOverlay addOverlay(final ArrayList<?> items) {
		UfItemizedOverlay overlay = new UfItemizedOverlay(mapView, this.mContext);
		if (items != null && !items.isEmpty()) {
			for (int i=items.size()-1; i>=0; i--) {
				Mappable mappable = (Mappable) items.get(i);
				if (mappable.getTitle() == null) continue;
				UfOverlayItem item = new UfOverlayItem(this.mContext, mapView, mappable);
				overlay.addItem(item);
				markerManager.display(new OsmdroidMarkerRef(mappable.getMapImageUrl(), item));
			}
			UfOverlayItem item = new UfOverlayItem(this.mContext, mapView, this.dataFacade.getUserPosition());
			overlay.addItem(item);
			Drawable drw = this.mContext.getResources().getDrawable(R.drawable.map_marker);
			drw.setBounds(-drw.getIntrinsicWidth() / 2, -drw.getIntrinsicHeight(), drw.getIntrinsicWidth() / 2, 0);
			item.setMarker(drw);
			overlay.populateNow();
		}
		return overlay;
	}

}
