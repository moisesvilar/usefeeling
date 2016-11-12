package com.usefeeling.android.cabinstill.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;

import com.usefeeling.android.cabinstill.listeners.WakefulAlarmListener;
import com.usefeeling.android.cabinstill.receivers.WakefulAlarmReceiver;

/**
 * Clase abstracta de la que deben heredar todos los IntentService que se desee que sean Wakeful.
 * @author Luisma Morán
 *
 */
public abstract class WakefulIntentService extends IntentService {

	/**
	 * Ejecuta las tareas del servicio.
	 * @param intent Intent de entrada
	 */
	protected abstract void doWakefulWork(Intent intent);
	
	public static final String NAME = "com.commonsware.cwac.wakeful.WakefulIntentService";
	public static final String LAST_ALARM="lastAlarm";
	private static volatile PowerManager.WakeLock wakeLockStatic = null;
	
	/**
	 * Constructor.
	 * @param name Nombre del servicio.
	 */
	public WakefulIntentService(String name) {
		super(name);
		this.setIntentRedelivery(true);
	}
	
	/**
	 * Obtiene el cerrojo de bloqueo para usar mientras el servicio está en ejecución y
	 * evitar que el sistema lo suspenda.
	 * @param context Contexto desde el que se invoca el método.
	 * @return El cerrojo de bloqueo.
	 */
	private synchronized static PowerManager.WakeLock getWakeLock(Context context) {
		if (WakefulIntentService.wakeLockStatic == null) {
			PowerManager mgr = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			WakefulIntentService.wakeLockStatic = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, NAME);
			WakefulIntentService.wakeLockStatic.setReferenceCounted(true);
		}
		return (WakefulIntentService.wakeLockStatic);
	}
	
	/**
	 * Inicia el servicio especificado sin que el sistema pueda suspenderlo mientras se
	 * ejecuta.
	 * @param ctxt Contexto desde el que se invoca el método.
	 * @param i Intent del servicio a iniciar.
	 */
	public static void sendWakefulWork(Context ctxt, Intent i) {
		WakefulIntentService.getWakeLock(ctxt.getApplicationContext()).acquire();
		ctxt.startService(i);
	}
	
	/**
	 * Inicia el servicio especificado sin que el sistema pueda suspenderlo mientras
	 * se ejecuta.
	 * @param ctxt Contexto desde el que se invoca el método.
	 * @param clsService Clase a la que pertenece el servicio a iniciar.
	 */
	public static void sendWakefulWork(Context ctxt, Class<?> clsService) {
		WakefulIntentService.sendWakefulWork(ctxt, new Intent(ctxt, clsService));
	}
	
	/**
	 * Planifica las alarmas especificadas.
	 * @param listener Listener de escucha de alarmas.
	 * @param ctxt Contexto desde el que se invoca el método.
	 */
	public static void scheduleAlarms(WakefulAlarmListener listener, Context ctxt) {
		WakefulIntentService.scheduleAlarms(listener, ctxt, true);
	}
	
	/**
	 * Planifica las alarmas especificadas.
	 * @param listener Listener de escucha de alarmas.
	 * @param ctxt Contexto desde el que se invoca el método.
	 * @param force <b>true</b> si se desea forzar el reinicio de las alarmas aunque ya se estén ejecutando.
	 */
	public static void scheduleAlarms(WakefulAlarmListener listener, Context ctxt, boolean force) {
		SharedPreferences prefs = ctxt.getSharedPreferences(NAME, 0);
		long lastAlarm = prefs.getLong(LAST_ALARM, 0);
		if (lastAlarm == 0 || force	|| (System.currentTimeMillis() > lastAlarm && System.currentTimeMillis() - lastAlarm > listener.getMaxAge())) {
			AlarmManager mgr = (AlarmManager) ctxt.getSystemService(Context.ALARM_SERVICE);
			Intent i = new Intent(ctxt, WakefulAlarmReceiver.class);
			PendingIntent pi = PendingIntent.getBroadcast(ctxt, 0, i, 0);
			listener.scheduleAlarms(mgr, pi, ctxt);
		}
	}
	
	/**
	 * Cancela las alarmas planificadas.
	 * @param ctxt Contexto desde el que se invoca el método.
	 */
	public static void cancelAlarms(Context ctxt) {
		AlarmManager mgr = (AlarmManager) ctxt.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(ctxt, WakefulAlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(ctxt, 0, i, 0);
		mgr.cancel(pi);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		PowerManager.WakeLock lock = WakefulIntentService.getWakeLock(this.getApplicationContext());
		if (!lock.isHeld() || (flags & START_FLAG_REDELIVERY) != 0) lock.acquire();
		super.onStartCommand(intent, flags, startId);
		return (Service.START_REDELIVER_INTENT);
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			doWakefulWork(intent);
		} finally {
			PowerManager.WakeLock lock = WakefulIntentService.getWakeLock(this.getApplicationContext());
			if (lock.isHeld()) lock.release();
		}
	}

}
