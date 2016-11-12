package com.usefeeling.android.cabinstill.receivers;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.XmlResourceParser;

import com.usefeeling.android.cabinstill.listeners.WakefulAlarmListener;
import com.usefeeling.android.cabinstill.services.WakefulIntentService;

/**
 * Receptor de alarmas wakeful.<br>
 * Hereda de BroadcastReceiver y sobreescribe su método onReceive, donde indica al Listener
 * que realice las tareas correspondientes.
 * @author Luisma Morán.
 *
 */
public class WakefulAlarmReceiver extends BroadcastReceiver {

	private static final String WAKEFUL_META_DATA="com.commonsware.cwac.wakeful";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		WakefulAlarmListener listener = this.getListener(context);
		if (listener != null) {
			if (intent.getAction() == null) {
				SharedPreferences prefs = context.getSharedPreferences(WakefulIntentService.NAME, 0);
				prefs.edit().putLong(WakefulIntentService.LAST_ALARM, System.currentTimeMillis()).commit();
				listener.sendWakefulWork(context);
			} else {
				WakefulIntentService.scheduleAlarms(listener, context, true);
			}
		}
	}
	
	/**
	 * Obtiene el listener de escucha de alarmas.
	 * @param ctxt Contexto desde el que se requiere el listener.
	 * @return El listener.
	 */
	@SuppressWarnings("unchecked")
	private WakefulAlarmListener getListener(Context ctxt) {
		PackageManager pm = ctxt.getPackageManager();
		ComponentName cn = new ComponentName(ctxt, getClass());
		try {
			ActivityInfo ai = pm.getReceiverInfo(cn, PackageManager.GET_META_DATA);
			XmlResourceParser xpp = ai.loadXmlMetaData(pm, WAKEFUL_META_DATA);
			while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
				if (xpp.getEventType() == XmlPullParser.START_TAG) {
					if (xpp.getName().equals("WakefulIntentService")) {
						String clsName = xpp.getAttributeValue(null, "listener");
						Class<WakefulAlarmListener> cls = (Class<WakefulAlarmListener>) Class.forName(clsName);
						return (cls.newInstance());
					}
				}
				xpp.next();
			}
		} catch (NameNotFoundException e) {
			throw new RuntimeException("Cannot find own info???", e);
		} catch (XmlPullParserException e) {
			throw new RuntimeException("Malformed metadata resource XML", e);
		} catch (IOException e) {
			throw new RuntimeException("Could not read resource XML", e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Listener class not found", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Listener is not public or lacks public constructor", e);
		} catch (InstantiationException e) {
			throw new RuntimeException("Could not create instance of listener",	e);
		}
		return (null);
	}

}
