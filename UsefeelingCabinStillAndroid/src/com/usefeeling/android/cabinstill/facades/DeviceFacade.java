package com.usefeeling.android.cabinstill.facades;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;

import com.usefeeling.android.R;
import com.usefeeling.android.cabinstill.api.Place;

/**
 * Fachada de operaciones con el dispositivo.<br><br>
 * Contiene implementaciones de métodos para obtener las cuentas de Google asociadas al dispositivo.<br><br>
 * @author Moisés Vilar.
 *
 */
public abstract class DeviceFacade {
	
	/**
	 * Obtiene todas las cuentas de Google vinculadas al dispositivo.
	 * @return Una lista con todas las cuentas de Google vinculadas al dispositivo.
	 */
	public static ArrayList<String> getGoogleAccounts(Context context) {
		ArrayList<String> result = new ArrayList<String>();
		AccountManager manager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account[] list = manager.getAccounts();
        for (Account account : list) {
        	if (account.type.contains("com.google")) {
        		result.add(account.name);
        	}
        }
        return result;
	}

	/**
	 * Obtiene la primera cuenta encontrada de Google vinculada al dispositivo.
	 * @return La primera cuenta encontrada de Google vinculada al dispositivo.
	 */
	public static String getGoogleAccount(Context context) {
		String result = "";
		AccountManager manager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account[] list = manager.getAccounts();
        for (Account account : list) {
        	if (account.type.contains("com.google")) {
        		result = account.name;
        	}
        }
        return result;
	}
	
	/**
	 * Obtiene todas las cuentas de Whatsapp vinculadas al dispositivo.
	 * @return Una lista con todas las cuentas de Whatsapp vinculadas al dispositivo.
	 */
	public static ArrayList<String> getWhatsappAccounts(Context context) {
		ArrayList<String> result = new ArrayList<String>();
		AccountManager manager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account[] list = manager.getAccounts();
        for (Account account : list) {
        	if (account.type.contains("com.whatsapp")) {
        		result.add(account.name);
        	}
        }
        return result;
	}
	
	/**
	 * Obtiene primera cuenta de Whatsapp vinculada al dispositivo.
	 * @return La primera cuenta encontrada de Whatsapp vinculada al dispositivo.
	 */
	public static String getWhatsappAccount(Context context) {
		String result = "";
		AccountManager manager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account[] list = manager.getAccounts();
        for (Account account : list) {
        	if (account.type.contains("com.whatsapp")) {
        		result = account.name;
        	}
        }
        return result;
	}
	
	/**
	 * Obtiene el nivel actual de batería del dispositivo.
	 * @param context Contexto desde el que se invoca el método.
	 * @return El nivel actual de batería
	 */
	public static float checkBatteryLevel(Context context) {
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = context.registerReceiver(null, ifilter);
		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		float batteryPct = level / (float) scale;
		return batteryPct;
	}
		
	/**
	 * Comprueba si el dispositivo está contectado a internet.
	 * @param context Contexto desde el que se invoca el método.
	 * @return <b>true</b> si el dispositivo está conectado a internet o <b>false</b> en caso contrario.
	 */
	public static boolean checkInternetConnection(Context context) {
	    ConnectivityManager conMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
	        return true;
	    } 
	    else {
	        return false;
	    }
	}
	
	/**
	 * Comprueba la conexión a internet y si el dispositivo está en modo Roaming.
	 * @param context El contexto desde el que se invoca el método.
	 * @return <b>true</b> si el dispositivo está conectado a internet y no está en modo roaming o <b>false</b> en caso contrario.
	 */
	public static boolean isConnAvailAndNotRoaming(Context context) {
	    ConnectivityManager conMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
	        if(!conMgr.getActiveNetworkInfo().isRoaming()) return true;
	        else return false;
	    } 
	    else {
	        return false;
	    }
	}
	
	/**
	 * Comprueba si el dispositivo está en modo roaming.
	 * @param context Contexto desde el que se invoca el método.
	 * @return <b>true</b> si el dispositivo está en modo roaming o <b>false</b> en caso contrario.
	 */
	public static boolean isRoaming(Context context) {
	    ConnectivityManager conMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    return (conMgr.getActiveNetworkInfo()!=null && conMgr.getActiveNetworkInfo().isRoaming());
	}
	
	/**
	 * Comprueba si el dispositivo está conectado a una red WiFi.
	 * @param context Contexto desde el que se invoca el método.
	 * @return <b>true</b> si el dispositivo está conectado a una red WiFi, o <b>false</b> en caso contrario.
	 */
	public static boolean isWifiConnected(Context context) {
		ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return mWifi.isConnected();
	}

	/**
	 * Comprueba si la cobertura de la conexión es aceptable.
	 * @param context Contexto desde el que se ejecuta el método.
	 * @return <b>true</b> si la cobertura de la conexión es aceptable, es decir, si el dispositivo
	 * está conectado a una red Wifi, o su tipo de red móvil es alguna de las siguientes:
	 * 1xRTT, EVDO, CDMA, HSPA, LTE o UMTS. <b>false</b> en caso contrario.
	 */
	public static boolean isCurrentNetworkTypeAcceptable(Context context) {
		if (!DeviceFacade.checkInternetConnection(context)) return false;
		if (DeviceFacade.isWifiConnected(context)) return true;
		TelephonyManager phone = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		int type = phone.getNetworkType();
		boolean result = false;
		switch (type) {
			case TelephonyManager.NETWORK_TYPE_1xRTT:
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
			case TelephonyManager.NETWORK_TYPE_EVDO_B:
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
			case TelephonyManager.NETWORK_TYPE_CDMA:
			case TelephonyManager.NETWORK_TYPE_EHRPD:
			case TelephonyManager.NETWORK_TYPE_HSDPA:
			case TelephonyManager.NETWORK_TYPE_HSPA:
			case TelephonyManager.NETWORK_TYPE_HSPAP:
			case TelephonyManager.NETWORK_TYPE_HSUPA:
			case TelephonyManager.NETWORK_TYPE_LTE:
			case TelephonyManager.NETWORK_TYPE_UMTS:
				result = true;
				break;
		}
		return result;
	}

	/**
	 * Comprueba si el proveedor de localización indicado está disponible.
	 * @param context Contexto desde el que se invoca el método.
	 * @param networkProvider Proveedor de localización.
	 * @return <b>true</b> si el proveedor está disponible o <b>false</b> en caso contrario.
	 */
	public static boolean checkLocationProvider(Context context, String networkProvider) {
		LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}

	/**
	 * Comprueba si el dispositivo está en modo tethering.
	 * @param context Contexto desde el que se ejecuta el método.
	 * @return <b>true</b> si el dispositivo está en modo tethering o <b>false</b> en caso contrario.
	 */
	public static boolean checkTethering(Context context) {
		WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		Method[] wmMethods = wifi.getClass().getDeclaredMethods();
		for(Method method: wmMethods){
			if(method.getName().equals("isWifiApEnabled")) {
				try {
				  return !((Boolean) method.invoke(wifi));
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				} catch (InvocationTargetException e) {}
			}
		}
		return false;
	}

	/**
	 * Comprueba si la localización por redes inalámbricas está activada.
	 * @param context Contexto desde el que se invoca el método.
	 * @return <b>true</b> si la localización por redes inalámbricas está activada o <b>false</b> en caso contrario.
	 */
	public static boolean isWirelessLocationEnable(Context context) {
		try {
			LocationManager location = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
			if (location == null) return false;
			return location.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Throwable t) {
			return false;
		}
	}
	
	/**
	 * Obtiene los números de teléfono asociados a los contactos del usuario en el dispositivo.
	 * @param context Contexto desde el que se invoca el método.
	 * @return Los números de teléfono de los contactos.
	 */
	public static ArrayList<String> getPhoneContacts (Context context) {
		ArrayList<String> result = new ArrayList<String>();
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String[] projection = new String[] {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
		Cursor people = context.getContentResolver().query(uri, projection, null, null, null);
		int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
		people.moveToFirst();
		do {
			result.add(people.getString(indexNumber));
		} while (people.moveToNext());
		people.close();
		return result;
	}
	
	/**
	 * Obtiene las direcciones de correo electrónico asociadas a los contactos del usuario en el dispositivo.
	 * @param context Contexto desde el que se invoca el método.
	 * @return Las direcciones de correo electrónico de los contactos.
	 */
	public static ArrayList<String> getEmailContacts(Context context) {
		ArrayList<String> result = new ArrayList<String>();
		ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cur.getCount() > 0) {
        	while (cur.moveToNext()){
        		String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
        		Cursor emails = context.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + id, null, null);
        		while (emails.moveToNext()) {
        			String email = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
        			result.add(email);
        		}
        		emails.close();
        	}
    		cur.close();
        }
		return result;
	}
	
	/**
	 * Comparte la información de un local entre las aplicaciones instaladas en el dispositivo.
	 * @param context Contexto.
	 * @param place Datos del local.
	 */
	public static void shareVenue(Activity context, Place place) {
		String message = context.getString(R.string.share_venue_text);
		message = message.replace("[$PLACENAME]", place.getName()).replace("[$ADDRESS]", place.getAddress()).replace("[$DESCRIPTION]", place.getDescription());
		String title = context.getString(R.string.share_title);
		title = title.replace("[$NAME]", place.getName());
		DeviceFacade.share(context, title, message);
	}
	
	/**
	 * Comparte el mensaje entre las aplicaciones instaladas en el dispositivo.
	 * @param context Contexto.
	 * @param title Título de la ventana.
	 * @param msg Mensaje a compartir.
	 */
	private static void share(Activity context, String title, String msg) {
		Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("text/plain");
		share.putExtra(Intent.EXTRA_TEXT, msg);
		context.startActivity(Intent.createChooser(share, title));
	}
	
	/**
	 * Abre la aplicación de Google Maps centrada en la posición especificada.
	 * @param context Contexto.
	 * @param lat Latitud.
	 * @param lon Longitud.
	 */
	public static void openGMaps(Activity context, Double slat, Double slon, Double dlat, Double dlon) {
		String url = "http://maps.google.com/maps?saddr=$SLAT,$SLON&daddr=$DLAT,$DLON"
				.replace("$SLAT", slat.toString())
				.replace("$SLON", slon.toString())
				.replace("$DLAT", dlat.toString())
				.replace("$DLON", dlon.toString());
		Intent navIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		context.startActivity(navIntent);
	}
}
