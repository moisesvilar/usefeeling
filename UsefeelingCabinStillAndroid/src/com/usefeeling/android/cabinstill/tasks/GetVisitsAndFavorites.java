package com.usefeeling.android.cabinstill.tasks;

import android.app.Activity;

import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;

public class GetVisitsAndFavorites extends AbstractTask {

	/**
	 * Constructor.
	 * @param activity Activity que ha ejecutado la tarea. 
	 * @param listener Objeto a la escucha de resultados.
	 */
	public GetVisitsAndFavorites(Activity activity, OnTaskCompleted listener, Object...params) {
		super(activity, listener, params);
	}
	
	@Override
	public void run() {
		try {
			Long placeid = (Long) this.mParams[0];
			result = this.mUsefeeling.getVisitsAndFavorites(placeid);
		} catch (Throwable t) {
			result = new Result(ResultCodes.Exception, t.getMessage());
			return;
		} finally {
			this.onPostExecute(result);
		}
	}

}
