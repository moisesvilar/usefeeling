package com.usefeeling.android.cabinstill.tasks;

import android.app.Activity;

import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;

/**
 * Tarea asíncrona de recordatorio de contraseña a través del correo electrónico dado por el usuario en el momento de crear la cuenta.<br>
 * En su método <font face="courier new">execute</font>, se debe especificar un único parámetro: la dirección de correo electrónico, de tipo
 * <font face="courier new">String</font>.
 * @author Moisés Vilar.
 *
 */
public class RememberPassword extends AbstractTask {
	
	public RememberPassword (Activity activity, OnTaskCompleted listener, Object...params) {
		super(activity, listener, params);
	}
	
	@Override
	public void run() {
		this.onPreExecute();
		try {
			String email = (String)this.mParams[0];
			result = this.mUsefeeling.rememberPassword(email);
		} catch (Throwable t) {
			result = new Result(ResultCodes.Exception, t.getMessage());
			return;
		} finally {
			this.onPostExecute(result);
		}
	}
}