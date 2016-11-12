package com.usefeeling.android;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.gson.Gson;
import com.usefeeling.android.cabinstill.activities.EventPageActivity;
import com.usefeeling.android.cabinstill.activities.PlacePageActivity;
import com.usefeeling.android.cabinstill.activities.PromoPageActivity;
import com.usefeeling.android.cabinstill.activities.UrlPageActivity;
import com.usefeeling.android.cabinstill.api.Notification;
import com.usefeeling.android.cabinstill.api.Place;
import com.usefeeling.android.cabinstill.facades.ApplicationFacade;
import com.usefeeling.android.cabinstill.facades.SharedPreferencesFacade;
import com.usefeeling.android.cabinstill.threads.UnregisterGcm;
import com.usefeeling.android.cabinstill.threads.UpdateGcmId;
import com.usefeeling.android.cabinstill.values.Extras;

public class GCMIntentService extends GCMBaseIntentService {
	
	/**
	 * Constructor
	 */
	public GCMIntentService() {
		super();
	}
	
	@Override
	protected void onError(Context context, String errorId) {
		Log.e("UseFeeling.GCMIntentService", errorId);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onMessage(final Context context, Intent inIntent) {
		try {
			//Forzamos reinicio de servicio de registro automático de visita
			ApplicationFacade.startCheckinService(context);
			//Obtenemos el objeto JSON de la notificación
			String json = inIntent.getStringExtra(Extras.NOTIFICATION);
			Gson gson = new Gson();
			final Notification notification = (Notification)gson.fromJson(json, Notification.class);
			//Creamos la notificación
			NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
			.setSmallIcon(R.drawable.status_bar_icon)
			.setWhen(notification.getTimestamp() != null ? notification.getTimestamp() : new java.util.Date().getTime())
			.setContentTitle("UseFeeling")
			.setContentText(notification.getTitle(this) != null ? notification.getTitle(this).substring(0, Math.min(notification.getTitle(this).length(), 50)) : "");
			//Creamos el intent para la activity que se abrirá tras pulsar en la notificación
			Intent outIntent = null;
			Class<?> clazz = null;
			switch (notification.getNotificationType()) {
				case Notification.NotificationTypes.EVENT_NOTIFICATION: {
					outIntent = new Intent(context, EventPageActivity.class);
					clazz = EventPageActivity.class;
					Long eventid = notification.getActivityId();
					outIntent.putExtra(Extras.EVENTID, eventid);
					break;
				}
				case Notification.NotificationTypes.PLACE_NOTIFICATION: {
					outIntent = new Intent(context, PlacePageActivity.class);
					clazz = PlacePageActivity.class;
					Place place = (Place)gson.fromJson(notification.getPayload(), Place.class);
					outIntent.putExtra(Extras.PLACE, place);
					break;
				}
				case Notification.NotificationTypes.PROMO_NOTIFICATION: {
					outIntent = new Intent(context, PromoPageActivity.class);
					clazz = PromoPageActivity.class;
					Long promoid = notification.getActivityId();
					outIntent.putExtra(Extras.PROMOID, promoid);
					break;
				}
				case Notification.NotificationTypes.URL_NOTIFICATION: {
					outIntent = new Intent(context, UrlPageActivity.class);
					clazz = UrlPageActivity.class;
					String url = notification.getPayload();
					outIntent.putExtra(Extras.URL, url);
					break;
				}
				case Notification.NotificationTypes.CHECK_CHECKIN_SERVICE: {
					return;
				}
			}
			//Si la notificación no es de ningún tipo conocido, salimos
			if (outIntent == null) return;
			//Establecemos título de notificación como extra del intent de salida
			if (notification.getTitle(this) != null && !notification.getTitle(this).trim().equals("")) outIntent.putExtra(Extras.NOTIFICATION_TITLE, notification.getTitle(this).trim());
			//El objeto stackBuilder contiene una pila artificial de "llamadas hacia atrás" para la nueva activity.
			//Esto permitirá navegar hacia atrás desde la activity a la pantalla inicial de la aplicación.
			TaskStackBuilder stackBuilder = TaskStackBuilder.from(context);
			//Añadimos al fondo de la pila la clase de la activity que manejará la notificación
			stackBuilder.addParentStack(clazz);
			//Añadimos en la cima de la pila el intent que inicia la activity
			stackBuilder.addNextIntent(outIntent);
			PendingIntent pIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(pIntent);
			//Mostramos la notificación en la ActionBar del dispositivo
			NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			android.app.Notification notif = builder.getNotification();
			notif.flags = android.app.Notification.FLAG_AUTO_CANCEL;
			notificationManager.notify(notification.getNotificationid().intValue(), notif);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	protected void onRegistered(Context context, String regId) {
		SharedPreferencesFacade prefs = new SharedPreferencesFacade(context);
		prefs.setGcmId(regId);
		UpdateGcmId t = new UpdateGcmId(this, regId);
		t.start();
	}

	@Override
	protected void onUnregistered(Context context, String regId) {
		SharedPreferencesFacade prefs = new SharedPreferencesFacade(context);
		prefs.clearGcmId();
		UnregisterGcm t = new UnregisterGcm(this);
		t.start();
	}
}
