package com.usefeeling.android.cabinstill.tasks;

import java.util.ArrayList;

import android.app.Activity;

import com.usefeeling.android.cabinstill.api.Place;
import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.dao.PlacesDao;
import com.usefeeling.android.cabinstill.facades.SharedPreferencesFacade;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;

/**
 * Tarea para sincronizar la caché local con el servidor.
 * @author Moisés Vilar.
 *
 */
public class Synchronize extends AbstractTask {
	private ArrayList<Place> places;
	
	/**
	 * Constructor.
	 * @param activity Activity que ha ejecutado la tarea. 
	 * @param listener Objeto a la escucha de resultados.
	 */
	public Synchronize(Activity activity, OnTaskCompleted listener, Object...params) {
		super(activity, listener, params);
	}
	
	@Override
	public void run() {
		SharedPreferencesFacade prefs = new SharedPreferencesFacade(this.mContext);
		Long lastSyncTs = prefs.getLastSyncTs();
		PlacesDao dao = new PlacesDao(this.mContext);
		try {
			this.places = this.mUsefeeling.synchronize(lastSyncTs);
			this.result = this.mUsefeeling.getLastResult();
			if (this.result != null && this.result.getCode() != null && this.result.getCode() != ResultCodes.Ok) {
				for (Place place : places) {
					if (dao.exists(place.getPlaceId())) {
						if (!dao.update(place)) {
							this.result = new Result(ResultCodes.CacheError, "Error al actualizar el local " + place.getPlaceId());
							return;
						}
					} else {
						if (!dao.insert(place)) {
							this.result = new Result(ResultCodes.CacheError, "Error al insertar el local " + place.getPlaceId());
							return;
						}
					}
				}
			}
		} catch (Throwable t) {
			result = new Result(ResultCodes.Exception, t.getMessage());
			return;
		} finally {
			if (this.result.getCode() == ResultCodes.Ok) {
				prefs.setLastSyncTs(System.currentTimeMillis());
			}
		}
	}
}
