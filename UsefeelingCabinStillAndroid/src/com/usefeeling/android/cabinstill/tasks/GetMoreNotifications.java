package com.usefeeling.android.cabinstill.tasks;

import java.util.ArrayList;

import android.app.Activity;

import com.usefeeling.android.cabinstill.api.Notification;
import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;

/**
 * Tarea asíncrona de descarga de notificaciones adicionales.<br>
 * Al invocar su método execute se le debe pasar como único parámetro un objecto de tipo Long que contenga el sello temporal desde el que
 * se van a obtener notificaciones.<br>
 * La tarea, al finalizar, llamará al método <font face="courier new">onTaskCompleted</font> del listener pasado como parámetro en su constructor,
 * que será una matriz de Objects con dos elementos:
 * <li>En su posición 0, el resultado de la operación, de tipo <font face="courier new">Result</font></li>
 * <li>En su posición 1, un <font face="courier new">ArrayList</font> de objetos <font face="courier new">Notification</font> que contendrá la información
 * de las notificaciones descargadas.</li>
 * @author Moisés Vilar.
 *
 */
public class GetMoreNotifications extends AbstractTask {

	private ArrayList<Notification> notifications;
	
	/**
	 * Constructor.
	 * @param activity Activity que ejecuta la tarea.
	 * @param listener Listener de escucha de resultados.
	 */
	public GetMoreNotifications(Activity activity, OnTaskCompleted listener, Object...params) {
		super(activity, listener, params);
	}
	
	@Override
	public void run() {
		try {
			Long timestamp = (Long)this.mParams[0];
			this.notifications = this.mUsefeeling.getNotificationsByDate(timestamp);
			result = this.mUsefeeling.getLastResult();
		} catch (Throwable t) {
			result = new Result(ResultCodes.Exception, t.getMessage());
			return;
		} finally {
			this.onPostExecute(new Object[]{result, notifications});
		}
	}
}
