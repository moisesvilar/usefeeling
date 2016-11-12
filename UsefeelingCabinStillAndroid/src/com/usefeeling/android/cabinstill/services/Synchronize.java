package com.usefeeling.android.cabinstill.services;

import java.util.ArrayList;

import android.app.IntentService;
import android.content.Intent;

import com.usefeeling.android.cabinstill.api.Place;
import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.api.ResultMessages;
import com.usefeeling.android.cabinstill.api.UseFeeling;
import com.usefeeling.android.cabinstill.dao.PlacesDao;
import com.usefeeling.android.cabinstill.facades.SharedPreferencesFacade;

/**
 * Servicio que se encarga de la sincronización de la caché local con el servidor.
 * @author Moisés Vilar.
 *
 */
public class Synchronize extends IntentService {
	
	private UseFeeling mUseFeeling;
	private SharedPreferencesFacade mPrefs;
	
	/**
	 * Constructor.
	 * @param name Nombre del servicio.
	 */
	public Synchronize(String name) {
		super(name);
	}
	
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		this.mPrefs = new SharedPreferencesFacade(this);
		this.mUseFeeling = new UseFeeling(this.mPrefs.getUserId(), this.mPrefs.getPassword());
	}



	@Override
	public void onDestroy() {
		this.mPrefs = null;
		this.mUseFeeling = null;
		System.gc();
		super.onDestroy();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Result result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
		Long lastSyncTs = this.mPrefs.getLastSyncTs();
		PlacesDao dao = new PlacesDao(this);
		ArrayList<Place> places = new ArrayList<Place>();
		try {
			places = this.mUseFeeling.synchronize(lastSyncTs);
			result = this.mUseFeeling.getLastResult();
			if (result != null && result.getCode() != null && result.getCode() != ResultCodes.Ok) {
				for (Place place : places) {
					if (dao.exists(place.getPlaceId())) {
						if (!dao.update(place)) {
							result = new Result(ResultCodes.CacheError, "Error al actualizar el local " + place.getPlaceId());
							return;
						}
					} else {
						if (!dao.insert(place)) {
							result = new Result(ResultCodes.CacheError, "Error al insertar el local " + place.getPlaceId());
							return;
						}
					}
				}
			}
		} catch (Throwable t) {
			result = new Result(ResultCodes.Exception, t.getMessage());
			return;
		} finally {
			if (result.getCode() == ResultCodes.Ok) {
				this.mPrefs.setLastSyncTs(System.currentTimeMillis());
			}
			result = null;
			lastSyncTs = null;
			dao = null;
			places = null;
		}
	}

}
