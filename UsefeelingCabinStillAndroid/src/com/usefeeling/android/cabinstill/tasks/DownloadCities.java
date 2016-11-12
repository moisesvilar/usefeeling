package com.usefeeling.android.cabinstill.tasks;

import java.util.ArrayList;

import android.app.Activity;

import com.usefeeling.android.cabinstill.api.City;
import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.helpers.Position;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;

/**
 * Descarga los datos sobre ciudades y los guarda en las preferencias compartidas.
 * @author Moisés Vilar.
 *
 */
public class DownloadCities extends AbstractTask {
	
	private ArrayList<City> cities;
	private Position position;
	
	/**
	 * Constructor.
	 * @param listener Objeto a la escucha de resultados.
	 */
	public DownloadCities(Activity activity, OnTaskCompleted listener, Object... params) {
		super(activity, listener, params);
	}
	
	@Override
	public void run() {
		this.onPreExecute();
		try {
			if (this.mParams != null && this.mParams[0] != null && (Boolean)this.mParams[0]) {
				this.position = this.mUsefeeling.getDefaultPosition();
				result = this.mUsefeeling.getLastResult();
				if (result.getCode() == ResultCodes.Ok) this.mDataFacade.setUserPosition(this.position.getLatitude(), this.position.getLongitude());
				else return;
			}
			cities = this.mUsefeeling.getCities();
			result = this.mUsefeeling.getLastResult();
			if (result.getCode() == ResultCodes.Ok) this.mPrefs.saveCities(cities);
		} catch (Throwable t) {
			result = new Result(ResultCodes.Exception, t.getMessage());
			return;
		} finally {
			this.onPostExecute(result);
		}
	}

}
