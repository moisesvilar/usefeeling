package com.usefeeling.android.cabinstill.listeners;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.SystemClock;

import com.usefeeling.android.cabinstill.services.CheckinService;
import com.usefeeling.android.cabinstill.services.WakefulIntentService;

/**
 * Clase que hereda de AlarmListener y es la encargada de escuchar las alarmas
 * para el registro automático de visita.
 * @author Luisma Morán.
 *
 */
public class CheckinListener implements WakefulAlarmListener {

	private final static long PERIODO_ALARMA= AlarmManager.INTERVAL_FIFTEEN_MINUTES; // 15 minutos
	//private final static long PERIODO_ALARMA= 2;
	private final static int RETARDO_INICIAL = 1 * 10 *1000; // 10 segundos
	
	@Override
	public void scheduleAlarms(AlarmManager mgr, PendingIntent pi, Context ctxt) {
		mgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + RETARDO_INICIAL, PERIODO_ALARMA, pi);
	}

	@Override
	public void sendWakefulWork(Context ctxt) {
		WakefulIntentService.sendWakefulWork(ctxt, CheckinService.class);
	}

	@Override
	public long getMaxAge() {
		return (2 * PERIODO_ALARMA);
	}

}
