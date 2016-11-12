package com.usefeeling.android.cabinstill.facades;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.usefeeling.android.R;
import com.usefeeling.android.cabinstill.activities.AboutActivity;
import com.usefeeling.android.cabinstill.activities.EventPageActivity;
import com.usefeeling.android.cabinstill.activities.EventsActivity;
import com.usefeeling.android.cabinstill.activities.PlacePageActivity;
import com.usefeeling.android.cabinstill.activities.PlacesActivity;
import com.usefeeling.android.cabinstill.activities.ProfileActivity;
import com.usefeeling.android.cabinstill.activities.PromosActivity;
import com.usefeeling.android.cabinstill.activities.SettingsActivity;
import com.usefeeling.android.cabinstill.api.Event;
import com.usefeeling.android.cabinstill.api.FacebookGraphData;
import com.usefeeling.android.cabinstill.api.Place;
import com.usefeeling.android.cabinstill.api.User;
import com.usefeeling.android.cabinstill.dao.AbstractDao;
import com.usefeeling.android.cabinstill.listeners.CheckinListener;
import com.usefeeling.android.cabinstill.services.WakefulIntentService;
import com.usefeeling.android.cabinstill.tasks.UpdateGcmId;
import com.usefeeling.android.cabinstill.values.Extras;

/**
 * Fachada de operaciones sobre la aplicación.
 * @author Moisés Vilar.
 *
 */
public class ApplicationFacade {
	
	public static final String GCM_SENDER_ID = "372731586653";
	
	/**
	 * Obtiene el identificador del recurso a partir de su nombre.
	 * @param variableName Nombre del recurso.
	 * @param context Contexto desde el que se requiere el recurso.
	 * @return
	 */
	public static int getResId(String variableName, String packageName, String typeName, Context context) {
	    try {
	    	variableName = variableName.replace(".png", "");
	    	return context.getResources().getIdentifier(variableName, typeName, packageName);
	    } catch (Exception e) {
	    	Log.e("ApplicationFacade", e.getMessage());
	        return -1;
	    } 
	}
	
	/**
	 * Cierra la aplicación.
	 */
	public static void kill(Activity activity) {
		activity.moveTaskToBack(true);
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	/**
	 * Comprueba si existe un asset definido para la aplicación.
	 * @param fileName Nombre del fichero que contiene el asset.
	 * @param path Ruta al asset.
	 * @param assetManager Gestor de assets.
	 * @return <b>true</b> si el asset existe o <b>false</b> en caso contrario.
	 * @throws IOException
	 */
    public static boolean existsAsset(String fileName, String path, AssetManager assetManager ) throws IOException  {
        for( String currentFileName : assetManager.list(path)) {
            if ( currentFileName.equals(fileName)) {
                return true ;
            }
        }
        return false ;
    }
    
    /**
     * Lista los assets que contiene la aplicación en una determinada ruta.
     * @param path Ruta.
     * @param assetManager Gestor de assets.
     * @return La lista de los assets encontrados en la ruta especificada.
     * @throws IOException
     */
    public static String[] listAssets( String path, AssetManager assetManager ) throws IOException {
        String[] files = assetManager.list(path);
        Arrays.sort( files );
        return files ;
    }
    
    /**
     * Obtiene el código de versión de la aplicación.
     * @param context Contexto desde el que se invoca el método.
     * @return El código de versión de la aplicación.
     */
    public static int getVersionCode(Context context) {
    	try {
	        PackageInfo manager= context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
	        return manager.versionCode;
    	} catch (NameNotFoundException e) {
    		return 0;
    	}
    }
    
    /**
     * Obtiene la versión de la aplicación.
     * @param context Contexto desde el que se invoca el método.
     * @return La versión de la aplicación.
     */
    public static String getVersionName(Context context) {
    	try {
	        PackageInfo manager= context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
	        return manager.versionName;
    	} catch (NameNotFoundException e) {
    		return "";
    	}
    }
    
    /**
     * Obtiene el nombre completo del paquete de la aplicación.
     * @return
     */
	public static String getPackageName(Context context) {
		try {
	        PackageInfo manager= context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
	        return manager.packageName;
    	} catch (NameNotFoundException e) {
    		return "";
    	}
	}

    /**
     * Abre el cliente de correo electrónico para envío de un email a la dirección de contacto de UseFeeling.
     * @param activity Activity desde la que se invoca este método.
     */
	public static void sendInfoEmail(Activity activity) {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@usefeeling.com"});
		try {
			activity.startActivity(Intent.createChooser(i, activity.getString(R.string.send_email)));
		} catch (android.content.ActivityNotFoundException e) {
			MessagesFacade.toastLong(activity, activity.getString(R.string.send_email_error));
		}
	}
	
	/**
	 * Registra el dispositivo en el sistema GCM.
	 * @param context Contexto desde el que se invoca el método.
	 */
	public static void registerGcm(Context context) {
		GCMRegistrar.checkDevice(context);
		try {
			GCMRegistrar.checkManifest(context);
		} catch (Exception e) {
			Log.e("", e.getMessage());
		}
		final String regId = GCMRegistrar.getRegistrationId(context);
		if (regId.equals("")) {
			GCMRegistrar.register(context, GCM_SENDER_ID);
		}
		else {
			new UpdateGcmId(context, null, regId).execute();
		}
	}

	/**
	 * Lanza el servicio de registro automático de visita.
	 * @param context Contexto desde el que se invoca el método.
	 */
	public static void startCheckinService(Context context) {
		//Lanzamos el servicio de registro automático de visita
		WakefulIntentService.scheduleAlarms(new CheckinListener(), context, false);
	}

	/**
	 * Detiene el servicio de registro automático de visita.
	 * @param context Contexto desde el que se invoca el método.
	 */
	public static void stopCheckinService(final Activity context) {
		new Runnable() {
			@Override
			public void run() {
				WakefulIntentService.cancelAlarms(context);
			}
		}.run();
	}

	/**
	 * Desregistra el dispositivo del sistema GCM.
	 * @param context Contexto desde el que se invoca el método.
	 */
	public static void unregisterGcm(final Activity context) {
		new Runnable() {
			@Override
			public void run() {
				GCMRegistrar.unregister(context);
			}
		}.run();

	}

	/**
	 * Copia la base de datos a la tardeja SD.
	 * @param context Contexto desde el que se invoca el método.
	 */
	public static void copyDatabaseToSdCard(Context context) {
		try {
	        File sd = Environment.getExternalStorageDirectory();
	        File data = Environment.getDataDirectory();
	
	        if (sd.canWrite()) {
	            String currentDBPath = "//data//" + ApplicationFacade.getPackageName(context) + "//databases//" + AbstractDao.DATABASE_NAME;
	            String backupDBPath = "backupname.db";
	            File currentDB = new File(data, currentDBPath);
	            File backupDB = new File(sd, backupDBPath);
	
	            if (currentDB.exists()) {
	            	FileInputStream fis = new FileInputStream(currentDB);
	            	FileOutputStream fos = new FileOutputStream(backupDB);
	                FileChannel src = fis.getChannel();
	                FileChannel dst = fos.getChannel();
	                dst.transferFrom(src, 0, src.size());
	                src.close();
	                dst.close();
	                fis.close();
	                fos.close();
	            }
	        }
	    } catch (Exception e) {}
	}
	
	/**
	 * Obtiene el Intent correcto para abrir una página de Facebook.
	 * @param context Contexto.
	 * @param id Identificador de la página de facebook.
	 * @param page Si es una página de local (true) o un perfil de usuario (false).
	 * @return Un Intent a la app de Facebook, si está instalada, o al browser si no lo está.
	 */
	public static Intent getOpenFacebookIntent(Context context, FacebookGraphData data, boolean page) {
		try {
			context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
			String url = "fb://";
			if (page) url += "page/";
			else url += "profile/";
			url += data.getId();
			return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		} catch (Exception e) {
			return new Intent(Intent.ACTION_VIEW, Uri.parse(data.getLink()));
		}
	}
	
	/**
	 * Obtiene el Intent correcto para abrir una página de Twitter
	 * @param context Contexto.
	 * @param url URL de la página a abrir.
	 * @return Un Intent a la app de Twitter, si está instalada, o al browser si no lo está.
	 */
	public static Intent getOpenTwitterIntent(Context context, String url) {
		try {
			String username = url.substring(url.lastIndexOf("/") + 1);
			return new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + username));
		} catch (Exception e) {
			return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		}
	}
	
	/**
	 * Muestra la ventana de perfil del propio usuario.
	 * @param activity
	 */
	public static void showUserProfileActivity(Activity activity, User user) {
		Intent i = new Intent(activity, ProfileActivity.class);
		i.putExtra(Extras.USER, user);
		activity.startActivity(i);
	}

	/**
	 * Muestra la ventana de ajustes.
	 * @param activity
	 */
	public static void showSettingsActivity(Activity activity) {
		Intent i = new Intent(activity, SettingsActivity.class);
		activity.startActivity(i);
	}

	/**
	 * Muestra la ventana de Acerca de
	 * @param activity
	 */
	public static void showAboutActivity(Activity activity) {
		Intent i = new Intent(activity, AboutActivity.class);
		activity.startActivity(i);
	}

	/**
	 * Muestra la pantalla de local.
	 * @param placesActivity
	 * @param place
	 */
	public static void showPlaceActivity(Activity activity, Place place) {
		Intent i = new Intent(activity, PlacePageActivity.class);
		i.putExtra(Extras.PLACE, place);
		activity.startActivity(i);
	}

	/**
	 * Muestra la ventana de lista de locales.
	 * @param activity
	 */
	public static void showPlacesActivity(Activity activity) {
		Intent i = new Intent(activity, PlacesActivity.class);
		activity.startActivity(i);
	}

	/**
	 * Inicia una activity nueva.
	 * @param clazz Clase de la activity a lanzar.
	 */
	public static void startActivity(Context context, Class<?> clazz) {
		Intent i = new Intent(context, clazz);
		context.startActivity(i);
	}

	/**
	 * Muestra la ventana de lista de eventos próximos al usuario.
	 * @param activity
	 */
	public static void showEventsActivity(Activity activity) {
		Intent i = new Intent(activity, EventsActivity.class);
		activity.startActivity(i);
	}

	/**
	 * Muestra la ventana de evento.
	 * @param eventsActivity
	 * @param itemAtPosition
	 */
	public static void showEventActivity(Activity activity, Event event) {
		Intent i = new Intent(activity, EventPageActivity.class);
		i.putExtra(Extras.EVENT, event);
		activity.startActivity(i);
	}

	/**
	 * Muestra la vetana de promos.
	 * @param activity
	 */
	public static void showPromosActivity(Activity activity) {
		Intent i = new Intent(activity, PromosActivity.class);
		activity.startActivity(i);		
	}

	/**
	 * Go home
	 * @param activity
	 * @param clazz
	 */
	public static void goHome(Activity activity, Class<?> clazz) {
		activity.finish();
	}
}
