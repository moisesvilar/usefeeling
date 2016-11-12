package com.usefeeling.android.cabinstill.tasks;

import android.app.Activity;

import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;

/**
 * Tarea asíncrona de comprobación de credenciales de acceso a UseFeeling.<br>
 * En su método <font face="courier new">execute</font>, se deben especificar, en orden, los siguientes parámetros:
 * <li>La dirección de correo electrónico, de tipo <font face="courier new">String</font>.</li>
 *  <li>La contraseña de acceso, de tipo <font face="courier new">String</font>.</li>
 * @author Moisés Vilar.
 *
 */
public class Login extends AbstractTask {
	
	/**
	 * Constructor.
	 * @param activity Activity que ha ejecutado la tarea. 
	 * @param listener Objeto a la escucha de resultados.
	 */
	public Login(Activity activity, OnTaskCompleted listener, Object... params) {
		super(activity, listener, params);
	}
	
	@Override
	public void run() {
		this.onPreExecute();
		try {
			String email = (String) this.mParams[0];
			String password = (String) this.mParams[1];
			this.mUsefeeling.setPassword(password);
			this.result = this.mUsefeeling.login(email);
		} catch (Throwable t) {
			result = new Result(ResultCodes.Exception, t.getMessage());
			return;
		} finally {
			this.onPostExecute(result);
		}
	}
}