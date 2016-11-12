package com.usefeeling.android.cabinstill.tasks;

import android.app.Activity;

import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;

/**
 * Tarea asíncrona de cambio de contraseña.<br>
 * En su método <font face="courier new">execute</font> se deben establecer, en el orden exacto, los siguientes parámetros de entrada:
 * <li>La antigua contraseña del usuario.</li>
 * <li>La nueva contraseña del usuario.</li>
 * @author Moisés Vilar.
 *
 */
public class ChangePassword extends AbstractTask {
	
	/**
	 * Constructor.
	 * @param activity Activity que ha ejecutado la tarea. 
	 * @param listener Objeto a la escucha de resultados.
	 */
	public ChangePassword(Activity activity, OnTaskCompleted listener, Object...params) {
		super(activity, listener, params);
	}
	
	
	@Override
	public void run() {
		this.onPreExecute();
		try {
			String oldPassword = (String)this.mParams[0];
			String newPassword = (String)this.mParams[1];
			this.result = this.mUsefeeling.changePassword(oldPassword, newPassword);
		} catch (Throwable t) {
			result = new Result(ResultCodes.Exception, t.getMessage());
			return;
		} finally {
			this.onPostExecute(result);
		}
	}
}
