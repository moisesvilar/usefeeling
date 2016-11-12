package com.usefeeling.android.cabinstill.tasks;

import android.content.Context;

import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;

/**
 * Tarea asíncrona que actualiza el identificador GCM del usuario.<br>
 * Al invocar su método execute, se le debe pasar como único parámetro el identicador GCM como un objecto
 * de la clase String.
 * @author Moisés Vilar.
 *
 */
public class UpdateGcmId extends AbstractTask {
	private String regId;
	
	/**
	 * Constructor.
	 * @param context Contexto.
	 */
	public UpdateGcmId(Context context, OnTaskCompleted listener, Object...params) {
		super(context, listener, params);
	}

	@Override
	public void run() {
		try {
			this.regId = (String)this.mParams[0];
			result = this.mUsefeeling.updateGcmId(this.regId);
			if (result != null && result.getCode() == ResultCodes.Ok) {
				this.mPrefs.setGcmIdInServer(true);
				this.mPrefs.setGcmId(regId);	
			}
		} catch (Throwable t) {
			result = new Result(ResultCodes.Exception, t.getMessage());
			return;
		} finally {
			this.onPostExecute(result);
		}
	}
	
}
