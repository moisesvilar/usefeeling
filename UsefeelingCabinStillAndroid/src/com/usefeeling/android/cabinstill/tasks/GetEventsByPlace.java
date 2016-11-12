package com.usefeeling.android.cabinstill.tasks;

import java.util.ArrayList;

import android.app.Activity;

import com.usefeeling.android.cabinstill.api.Event;
import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;

/**
 * Tarea asíncrona para obtención de los eventos vigentes de un local.<br>
 * En su método execute, se debe establecer como parámetro de entrada el identificador del local, de tipo Long.
 * La tarea, al finalizar, llamará al método <font face="courier new">onTaskCompleted</font> del listener pasado como parámetro en su constructor,
 * que será una matriz de Objects con dos elementos:
 * <li>En su posición 0, el resultado de la operación, de tipo <font face="courier new">Result</font></li>
 * <li>En su posición 1, un <font face="courier new">ArrayList</font> de objetos <font face="courier new">Event</font> que contendrá la información
 * de los eventos vigentes del local.</li>
 * @author Moisés Vilar.
 *
 */
public class GetEventsByPlace extends AbstractTask {
	private ArrayList<Event> events;
	
	/**
	 * Constructor.
	 * @param activity Activity que ejecuta la tarea.
	 * @param listener Listener de escucha de resultados.
	 */
	public GetEventsByPlace(Activity activity, OnTaskCompleted listener, Object...params) {
		super(activity, listener, params);
	}
	
	@Override
	public void run() {
		try {
			Long placeid = (Long)this.mParams[0];
			this.events = this.mUsefeeling.getEventsByPlace(placeid);
			result = this.mUsefeeling.getLastResult();
		} catch (Throwable t) {
			result = new Result(ResultCodes.Exception, t.getMessage());
			return;
		} finally {
			this.onPostExecute(new Object[]{result, events});
		}
	}
}
