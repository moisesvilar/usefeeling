package com.usefeeling.android.cabinstill.listeners;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;

/**
 * Interfaz que deben implementar todos los Listeners de alarmas que se deseen sean Wakeful.
 * @author Luisma Morán.
 *
 */
public interface WakefulAlarmListener {
	void scheduleAlarms(AlarmManager mgr, PendingIntent pi, Context ctxt);

	void sendWakefulWork(Context ctxt);

	long getMaxAge();
}
