package com.usefeeling.android.cabinstill.tasks;

import android.app.Activity;

import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;

/**
 * Tarea asíncrona para establecer la recepción de notificaciones de los locales favoritos del usuario.<br>
 * En su método <font face="courier new">execute</font> se debe establecer, como parámetro de entrada, el valor
 * de la recepción de notificaciones de los locales favoritos del usuario, como un objecto <font face="courier new">Boolean</font>.
 * @author Moisés Vilar
 *
 */
public class SetFavPlacesNotifications extends AbstractTask {
	
	/**
	 * Constructor.
	 * @param activity Activity que ha ejecutado la tarea. 
	 * @param listener Objeto a la escucha de resultados.
	 */
	public SetFavPlacesNotifications(Activity activity, OnTaskCompleted listener, Object...params) {
		super(activity, listener, params);
	}
	
	@Override
	public void run() {
		try {
			Boolean value = (Boolean)this.mParams[0];
			if (value) result = this.mUsefeeling.activateFavPlacesNotifications();
			else result = this.mUsefeeling.deactivateFavPlacesNotifications();
		} catch (Throwable t) {
			result = new Result(ResultCodes.Exception, t.getMessage());
			return;
		} finally {
			this.onPostExecute(result);
		}
	}
}
