package com.usefeeling.android.cabinstill.tasks;

import android.app.Activity;

import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.api.User;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;

/**
 * Tarea asíncrona de descarga de datos de un usuario.<br>
 * Al invocar su método execute se le debe pasar como único parámetro un objecto de tipo Long que contenga el identificador del usuario
 * del que se van a descargar sus datos<br>
 * La tarea, al finalizar, llamará al método <font face="courier new">onTaskCompleted</font> del listener pasado como parámetro en su constructor,
 * que será una matriz de Objects con dos elementos:
 * <li>En su posición 0, el resultado de la operación, de tipo <font face="courier new">Result</font></li>
 * <li>En su posición 1, un <font face="courier new">User</font> que contendrá la información del usuario.</li>
 * @author Moisés Vilar.
 *
 */
public class GetUser extends AbstractTask {
	private User user;
	
	/**
	 * Constructor.
	 * @param activity Activity que ejecuta la tarea.
	 * @param listener Listener de escucha de resultados.
	 */
	public GetUser(Activity activity, OnTaskCompleted listener, Object...params) {
		super(activity, listener, params);
	}
	
	@Override
	public void run() {
		this.onPreExecute();
		try {
			Long friendid = (Long)this.mParams[0];
			this.user = this.mUsefeeling.getFriend(friendid);
			result = this.mUsefeeling.getLastResult();
		} catch (Throwable t) {
			result = new Result(ResultCodes.Exception, t.getMessage());
			return;
		} finally {
			this.onPostExecute(new Object[]{result, user});
		}
	}
}
