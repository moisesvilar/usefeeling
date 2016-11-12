package com.usefeeling.android.cabinstill.facades;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.usefeeling.android.R;
import com.usefeeling.android.cabinstill.api.Account;
import com.usefeeling.android.cabinstill.api.City;
import com.usefeeling.android.cabinstill.api.Notification;
import com.usefeeling.android.cabinstill.api.UseFeeling;
import com.usefeeling.android.cabinstill.api.User;
import com.usefeeling.android.cabinstill.helpers.DateTimeHelper;
import com.usefeeling.android.cabinstill.helpers.Position;

/**
 * Fachada de operaciones con las preferencias compartidas de Android.
 * @author Moisés Vilar.
 *
 */
public final class SharedPreferencesFacade {
	
	private final static String JSON_CITIES = "JSONCITIES";
	private final static String IS_SESSION_STARTED = "ISSESSIONSTARTED";
	private final static String FACEBOOK_ACCESS_TOKEN = "FACEBOOKACCESSTOKEN";
	private final static String FACEBOOK_EXPIRES = "FACEBOOKEXPIRES";
	private final static String USERID = "USERID";
	private final static String FACEBOOKID = "FACEBOOKID";
	private final static String PASSWORD = "PASSWORD";
	private final static String CITY = "CITY";
	private final static String GCMID = "GCMID";
	private final static String GCM_ID_IN_SERVER = "GCMIDINSERVER";
	private final static String USER_POSITION = "USERPOSITION";
	private static final String LAST_SYNC_TS = "LASTSYNCTS";
	private static final String ACCOUNT = "ACCOUNT";
	private static final String NOTIFICATIONS = "NOTIFICATIONS";
	private static final String USER = "USER";
	private static final String FIRST_RUNNING = "FIRSTRUNNING";
	private static final String LOCATION_MONITORING_CONFIRMED = "LOCATIONMONITORINGCONFIRMED";
	
	private Context context;
	private SharedPreferences preferences;
	private final Gson gson = new Gson();
	
	/**
	 * Constructor.
	 * @param ctx Contexto desde donde se requiere la fachada.
	 */
	public SharedPreferencesFacade(Context ctx) {
		this.context = ctx;
		this.preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
	}
	
	/**
	 * Guarda las ciudades dadas de alta en UseFeeling como un objeto JSON en las preferencias compartidas.
	 * @param cities La lista con las ciudades.
	 */
	public void saveCities(ArrayList<City> cities) {
		String citiesStr = this.gson.toJson(cities);
		Editor edit = this.preferences.edit();
		edit.putString(SharedPreferencesFacade.JSON_CITIES, citiesStr);
		edit.commit();
	}
	
	/**
	 * Obtiene si hay una sesión abierta en el teléfono con una cuenta de UseFeeling.
	 * @return Si hay una sesión abierta.
	 */
	public boolean isSessionStarted() {
		return this.preferences.getBoolean(SharedPreferencesFacade.IS_SESSION_STARTED, false);
	}
	
	/**
	 * Obtiene las ciudades dadas de alta en UseFeeling.
	 * @return Las ciudades dadas de alta.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<City> getCities() {
		String json = this.preferences.getString(SharedPreferencesFacade.JSON_CITIES, "");
		if (json.equals("")) return null;
		Type type = new TypeToken<ArrayList<City>>(){}.getType();
		JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
		return (ArrayList<City>)this.gson.fromJson(reader, type);
	}

	/**
	 * Obtiene el listado de ciudades separados por comas.
	 * @return Una cadena con el nombre de las ciudades separados por comas.
	 */
	public String getCitiesToString() {
		ArrayList<City> cities = this.getCities();
		if (cities == null || cities.isEmpty()) return this.context.getString(R.string.default_cities);
		String result = "";
		for (City city : cities) {
			result += city.getName() + ", ";
		}
		result = result.substring(0, result.length()-2);
		return result;
	}

	/**
	 * Obtiene el testigo de acceso a Facebook para el usuario.
	 * @return El testigo de acceso a Facebook.
	 */
	public String getFacebookAccessToken() {
		return this.preferences.getString(SharedPreferencesFacade.FACEBOOK_ACCESS_TOKEN, null);
	}

	/**
	 * Obtiene la fecha de caducidad del testigo de acceso a Facebook para el usuario.
	 * @return La fecha de caducidad del testigo de acceso a Facebook.
	 */
	public long getFacebookExpires() {
		return this.preferences.getLong(SharedPreferencesFacade.FACEBOOK_EXPIRES, 0);
	}

	/**
	 * Guarda las credenciales de acceso a Facebook en las preferencias compartidas.
	 * @param accessToken Testigo de acceso a Facebook.
	 * @param accessExpires Caducidad del testigo.
	 */
	public void setFacebookAccessToken(String accessToken, long accessExpires) {
		Editor edit = this.preferences.edit();
		edit.putString(FACEBOOK_ACCESS_TOKEN, accessToken);
		edit.putLong(FACEBOOK_EXPIRES, accessExpires);
		edit.commit();
	}

	/**
	 * Guarda el identificador de usuario en UseFeeling en las preferencias compartidas.
	 * @param userid Identificador del usuario.
	 */
	public void setUserId(Long userid) {
		Editor edit = this.preferences.edit();
		edit.putLong(USERID, userid);
		edit.commit();
	}

	/**
	 * Guarda el identificador de usuario en Facebook en las preferencias compartidas.
	 * @param facebookid Identificador del usuario en Facebook.
	 */
	public void setFacebookId(String facebookid) {
		Editor edit = this.preferences.edit();
		edit.putString(FACEBOOKID, facebookid);
		edit.commit();		
	}

	/**
	 * Guarda la contraseña de acceso a UseFeeling en las preferencias compartidas.
	 * @param password Contraseña de acceso.
	 */
	public void setPassword(String password) {
		Editor edit = this.preferences.edit();
		edit.putString(PASSWORD, password);
		edit.commit();
	}

	/**
	 * Obtiene el identificador del usuario.
	 * @return El identificador del usuario.
	 */
	public Long getUserId() {
		return this.preferences.getLong(USERID, 0);
	}
	
	/**
	 * Obtiene la contraseña de acceso del usuario.
	 * @return La contraseña de acceso.
	 */
	public String getPassword() {
		return this.preferences.getString(PASSWORD, "");
	}

	/**
	 * Obtiene la ciudad actual donde se encuentra el usuario.
	 * @return La ciudad del usuario.
	 */
	public String getCity() {
		String result = this.preferences.getString(CITY, "");
		if (result.equals("")) return "Santiago de Compostela";
		return result;
	}

	/**
	 * Establece la ciudad actual donde se encuentra el usuario.
	 * @param city La ciudad del usuario.
	 */
	public void setCity(String city) {
		Editor edit = this.preferences.edit();
		edit.putString(CITY, city);
		edit.commit();
	}

	/**
	 * Establece el identificador GCM.
	 * @param regId Identificador GCM.
	 */
	public void setGcmId(String regId) {
		Editor edit = this.preferences.edit();
		edit.putString(GCMID, regId);
		edit.commit();
	}
	
	/**
	 * Devuelve el identificador GCM almacenado en el dispositivo.
	 * @return El identificador GCM
	 */
	public String getGcmId() {
		return this.preferences.getString(GCMID, "");
	}

	/**
	 * Elimina el identificador GCM.
	 */
	public void clearGcmId() {
		Editor edit = this.preferences.edit();
		edit.putString(GCMID, "");
		edit.commit();
	}

	/**
	 * Establece si una sesión ha sido iniciada en el dispositivo.
	 * @param value Valor.
	 */
	public void setSessionStarted(boolean value) {
		Editor edit = this.preferences.edit();
		edit.putBoolean(IS_SESSION_STARTED, value);
		edit.commit();
	}

	/**
	 * Borra todas las preferencias de la aplicación (cierra sesión).
	 */
	public void clear() {
		Editor edit = this.preferences.edit();
		edit.clear();
		edit.commit();
	}

	/**
	 * Establace el valor de la bandera de que el identificador GCM se ha establecido en el servidor de UseFeeling.
	 * @param value Valor de la bandera.
	 */
	public void setGcmIdInServer(boolean value) {
		Editor edit = this.preferences.edit();
		edit.putBoolean(GCM_ID_IN_SERVER, value);
		edit.commit();
	}

	/**
	 * Obtiene el valor de la bandera de que el identificador GCM se ha establecido en el servidor de UseFeeling.
	 * @return El valor de la bandera.
	 */
	public boolean isGcmInServer() {
		return this.preferences.getBoolean(GCM_ID_IN_SERVER, false);
	}

	/**
	 * Establece la posición del usuario.
	 * @param userPosition La posición del usuario.
	 */
	public void setUserPosition(Position userPosition) {
		Editor edit = this.preferences.edit();
		edit.putString(USER_POSITION, (new Gson()).toJson(userPosition));
		edit.commit();
	}

	/**
	 * Obtiene la posición del usuario.
	 * @return La posición del usuario.
	 */
	public Position getUserPosition() {
		try {
			String json = this.preferences.getString(USER_POSITION, "");
			Position position = (new Gson()).fromJson(json, Position.class);
			return position;
		} catch (Exception e) {
			return new Position(UseFeeling.DEFAULT_LAT, UseFeeling.DEFAULT_LON);
		}
		//return (new Gson()).fromJson(this.preferences.getString(USER_POSITION, ""), Position.class);
	}

	/**
	 * Obtiene el sello temporal de la última sincronización de la caché.
	 * @return El sello temporal.
	 */
	public long getLastSyncTs() {
		return this.preferences.getLong(LAST_SYNC_TS, DateTimeHelper.getTime(2012, 1, 1));
	}

	/**
	 * Establece el sello temporal de la última sincronización de la caché.
	 * @param time El sello temporal.
	 */
	public void setLastSyncTs(long time) {
		Editor edit = this.preferences.edit();
		edit.putLong(LAST_SYNC_TS, time);
		edit.commit();
	}

	/**
	 * Obtiene los datos de la cuenta del usuario.
	 * @return La cuenta del usuario.
	 */
	public Account getAccount() {
		String json = this.preferences.getString(ACCOUNT, "");
		if (json.equals("")) return null;
		Account account = (Account)this.gson.fromJson(json, Account.class);
		if (account.getEmail().trim().equals("")) return null;
		return account;
	}

	/**
	 * Establece los datos de la cuenta del usuario.
	 * @param account Los datos de la cuenta.
	 */
	public void setAccount(Account account) {
		Editor edit = this.preferences.edit();
		edit.putString(ACCOUNT, this.gson.toJson(account));
		edit.commit();
	}

	/**
	 * Obtiene las notificaciones del usuario.
	 * @return Las notificaciones
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Notification> getNotifications() {
		String json = this.preferences.getString(NOTIFICATIONS, "");
		if (json.equals("")) return new ArrayList<Notification>();
		Type type = new TypeToken<ArrayList<Notification>>(){}.getType();
		JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
		return (ArrayList<Notification>)this.gson.fromJson(reader, type);
	}

	/**
	 * Establece las notificaciones del usuario.
	 * @param notifications Las notificaciones.
	 */
	public void setNotifications(ArrayList<Notification> notifications) {
		Editor edit = this.preferences.edit();
		edit.putString(NOTIFICATIONS, this.gson.toJson(notifications));
		edit.commit();
	}

	/**
	 * Obtiene los datos del usuario actual de la aplicación.
	 * @return Los datos del usuario actual de la aplicación.
	 */
	public User getCurrentUser() {
		String json = this.preferences.getString(USER, "");
		if (json.equals("")) return null;
		User user = (User)this.gson.fromJson(json, User.class);
		if (user.getEmail().trim().equals("")) return null;
		return user;
	}

	/**
	 * Establece los datos del usuario actural de la aplicación.
	 * @param user Los datos del usuario actual de la aplicación.
	 */
	public void setCurrentUser(User user) {
		Editor edit = this.preferences.edit();
		edit.putString(USER, this.gson.toJson(user));
		edit.commit();
	}

	/**
	 * Set first running falg value.
	 * @param b The value.
	 */
	public void setFirstRunning(boolean b) {
		Editor edit = this.preferences.edit();
		edit.putBoolean(FIRST_RUNNING, b);
		edit.commit();
	}

	/**
	 * Retrieves the first running flag.
	 * @return The first running flag.
	 */
	public boolean isFirstRunning() {
		return this.preferences.getBoolean(FIRST_RUNNING, true);
	}

	/**
	 * Checks if the user has confirmed the location monitoring.
	 * @return The value.
	 */
	public boolean isLocationMonitoringConfirmed() {
		return this.preferences.getBoolean(LOCATION_MONITORING_CONFIRMED, false);
	}

	/**
	 * Sets the location monitoring confirmation to true.
	 */
	public void setLocationMonitoringConfirmation() {
		Editor edit = this.preferences.edit();
		edit.putBoolean(LOCATION_MONITORING_CONFIRMED, true);
		edit.commit();
	}
}
