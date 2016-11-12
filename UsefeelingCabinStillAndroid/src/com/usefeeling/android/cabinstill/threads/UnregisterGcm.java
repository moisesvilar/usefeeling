package com.usefeeling.android.cabinstill.threads;

import android.content.Context;

import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.api.UseFeeling;
import com.usefeeling.android.cabinstill.facades.SharedPreferencesFacade;

/**
 * Hilo que se encarga de desregistrar el dispositivo del sistema GCM.
 * @author Moisés Vilar.
 *
 */
public class UnregisterGcm extends Thread {
	
	private Context context;
	private SharedPreferencesFacade prefs;
	private UseFeeling usefeeling;
	
	/**
	 * Constructor.
	 * @param context Contexto desde el que se ejecuta el hilo.
	 */
	public UnregisterGcm(Context context) {
		this.context = context;
		this.prefs = new SharedPreferencesFacade(this.context);
		this.usefeeling = new UseFeeling(this.prefs.getUserId(), this.prefs.getPassword());
	}
	
	@Override
	public void run() {
		Result result = this.usefeeling.unregisterGcm();
		if (result.getCode() == ResultCodes.Ok) {
			this.prefs.setGcmId("");
			this.prefs.setGcmIdInServer(false);
		}
	}

}
