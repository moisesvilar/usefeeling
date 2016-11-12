package com.usefeeling.android.cabinstill.threads;

import android.content.Context;

import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.api.UseFeeling;
import com.usefeeling.android.cabinstill.facades.SharedPreferencesFacade;

/**
 * Hilo que se encarga de actualizar el identificador GCM del dispositivo.
 * @author Moisés Vilar.
 *
 */
public class UpdateGcmId extends Thread {

	private Context context;
	private SharedPreferencesFacade prefs;
	private UseFeeling usefeeling;
	private String regId;
	
	/**
	 * Constructor.
	 * @param context Contexto desde el que se ejecuta el hilo.
	 */
	public UpdateGcmId(Context context, String regId) {
		this.context = context;
		this.regId = regId;
		this.prefs = new SharedPreferencesFacade(this.context);
		this.usefeeling = new UseFeeling(this.prefs.getUserId(), this.prefs.getPassword());
	}
	
	@Override
	public void run() {
		Result result = this.usefeeling.updateGcmId(this.regId);
		if (result.getCode() == ResultCodes.Ok) {
			this.prefs.setGcmId(this.regId);
			this.prefs.setGcmIdInServer(true);
		}
	}
	
}
