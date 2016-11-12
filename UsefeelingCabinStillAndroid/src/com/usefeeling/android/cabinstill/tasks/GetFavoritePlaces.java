package com.usefeeling.android.cabinstill.tasks;

import java.util.ArrayList;

import android.app.Activity;

import com.usefeeling.android.cabinstill.api.Place;
import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;

/**
 * Tarea asíncrona de obtención de los locales favoritos de un usuario.<br>
 * En su método <font face="courier new">execute</font> se le debe pasar como parámetro un objecto de tipo <font face="courier new">Long</font>
 * que contenga el identificador del usuario.<br>
 * La tarea, al finalizar, llamará al método <font face="courier new">onTaskCompleted</font> del listener pasado como parámetro en su constructor,
 * que será una matriz de Objects con dos elementos:
 * <li>En su posición 0, el resultado de la operación, de tipo <font face="courier new">Result</font></li>
 * <li>En su posición 1, un <font face="courier new">ArrayList</font> de objetos <font face="courier new">Place</font> que contendrá la información
 * de los locales favoritos del usuario</li>
 * @author Moisés Vilar.
 *
 */
public class GetFavoritePlaces extends AbstractTask {
	private ArrayList<Place> places;
	
	/**
	 * Constructor.
	 * @param activity Activity que ejecuta la tarea.
	 * @param listener Listener de escucha de resultados.
	 */
	public GetFavoritePlaces(Activity activity, OnTaskCompleted listener, Object...params) {
		super(activity, listener, params);
	}
	
	@Override
	public void run() {
		try {
			Long userid = (Long)this.mParams[0];
			this.places = this.mUsefeeling.getFavPlaces(userid);
			result = this.mUsefeeling.getLastResult();
		} catch (Throwable t) {
			result = new Result(ResultCodes.Exception, t.getMessage());
			return;
		} finally {
			this.onPostExecute(new Object[]{result, places});
		}
	}
}
