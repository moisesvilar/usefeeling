package com.usefeeling.android.cabinstill.receivers;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.usefeeling.android.cabinstill.facades.ApplicationFacade;

/**
 * Receptor que comprueba y lanza si es necesario el servicio de 
 * registro automático de visitas.
 * @author Moisés Vilar.
 *
 */
public class CheckCheckinService extends BroadcastReceiver {

	public static final int ID = 0;
	public static final long PERIOD = AlarmManager.INTERVAL_HALF_HOUR; // 30 minutos

	@Override
	public void onReceive(Context context, Intent intent) {
		ApplicationFacade.startCheckinService(context);
	}

}
