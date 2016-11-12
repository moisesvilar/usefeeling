package com.usefeeling.android.cabinstill.tasks;

import android.app.Activity;

import com.usefeeling.android.cabinstill.api.Promo;
import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;

/**
 * Tarea de obtención de datos de promoción.
 * @author Moisés Vilar.
 *
 */
public class GetPromo extends AbstractTask {

	private Promo mPromo;
	
	/**
	 * Constructor.
	 * @param context Contexto desde el que se ejecuta la tarea.
	 * @param listener Listener de llamada cuando la tarea finalice su ejecución.
	 * @param params Parámetros de entrada para la tarea.
	 */
	public GetPromo(Activity context, OnTaskCompleted listener, Object... params) {
		super(context, listener, params);
	}
	
	@Override
	public void run() {
		try {
			this.onPreExecute();
			Long promoid = (Long)this.mParams[0];
			this.mPromo = this.mUsefeeling.getPromo(promoid);
			result = this.mUsefeeling.getLastResult();
		} catch (Throwable t) {
			result = new Result(ResultCodes.Exception, t.getMessage());
			return;
		} finally {
			this.onPostExecute(new Object[]{result, mPromo});
		}
	}
}	
