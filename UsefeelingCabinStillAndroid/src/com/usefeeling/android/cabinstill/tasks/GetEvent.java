package com.usefeeling.android.cabinstill.tasks;

import android.app.Activity;

import com.usefeeling.android.cabinstill.api.Event;
import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;

/**
 * Runnable de obtención de evento.
 * @author Moisés Vilar.
 *
 */
public class GetEvent extends AbstractTask {

	private Event mEvent;
	
	/**
	 * Constructor.
	 * @param context Contexto desde el que se ejecuta la tarea.
	 * @param listener Listener de llamada cuando la tarea finalice su ejecución.
	 * @param params Parámetros de entrada para la tarea.
	 */
	public GetEvent(Activity context, OnTaskCompleted listener, Object... params) {
		super(context, listener, params);
	}
	
	@Override
	public void run() {
		try {
			this.onPreExecute();
			Long eventid = (Long)this.mParams[0];
			this.mEvent = this.mUsefeeling.getEvent(eventid);
			result = this.mUsefeeling.getLastResult();
		} catch (Throwable t) {
			result = new Result(ResultCodes.Exception, t.getMessage());
			return;
		} finally {
			this.onPostExecute(new Object[]{result, mEvent});
		}
	}
}
