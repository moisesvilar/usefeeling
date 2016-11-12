package com.usefeeling.android.cabinstill.tasks;

import android.app.Activity;

import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;

/**
 * Tarea asíncrona que marca o desmarca un local como favorito.<br>
 * En su método execute se le debe pasar como parámetros de entrada en el siguiente orden:
 * <li>El identificador del local, de tipo Long.</li>
 * <li>El valor a establecer, de tipo Boolean.</li>
 * @author Moisés Vilar.
 *
 */
public class MarkPlaceAsFav extends AbstractTask {
	
	/**
	 * Constructor.
	 * @param activity Activity que ha ejecutado la tarea. 
	 * @param listener Objeto a la escucha de resultados.
	 */
	public MarkPlaceAsFav(Activity activity, OnTaskCompleted listener, Object...params) {
		super(activity, listener, params);
	}
	
	@Override
	public void run() {
		try {
			Long placeid = (Long) this.mParams[0];
			Boolean value = (Boolean) this.mParams[1];
			if (value) result = this.mUsefeeling.markPlaceAsFav(placeid);
			else result = this.mUsefeeling.unmarkPlaceAsFav(placeid);
		} catch (Throwable t) {
			result = new Result(ResultCodes.Exception, t.getMessage());
			return;
		} finally {
			this.onPostExecute(result);
		}
	}
}
