package com.usefeeling.android.cabinstill.api;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.usefeeling.android.cabinstill.helpers.Position;

/**
 * Interfaz de aplicación para el acceso a los servicios de la plataforma UseFeeling
 * desde una aplicación Android.<br/><br/>
 * Las operaciones que devuelvan como resultado un objeto de una clase distinta a <font face="courier new">Result</font> 
 * (tipicamente <font face="courier new">ArrayList</font> o POJO's definidos en la API), devolverán <font face="courier new">null</font>
 * si ocurre algún error durante la ejecución de la operación requerida. En este caso, el atributo <font face="courier new">lastResult</font>
 * de la clase <font face="courier new">UseFeeling</font> contendrá el resultado de la operación, con la descripción del error detectado.
 * @author Moisés Vilar.
 *
 */
public final class UseFeeling {
	private final static String scheme = "http";
	private final static String host = "android.usefeeling.com";   
	private final static int port = 18080;
	private final static String path = "/usefeelingcabinstillservices/";
	
	private final static int CONNECTION_TIMEOUT = 30000;
	private final static int SOCKET_TIMEOUT = 30000;
	private final static int MAX_NUM_ATTEMPS = 1;
	
	public static final String SUGGESTION = "SUGERENCIA";
	public static final String EVENT = "EVENTO";
	public static final String PROMO = "PROMO";
	public static final String CHECKIN = "VISITA";
	public static final String GPS = "GPS";
	public static final String TOS_URL = "http://android.usefeeling.com:18080/usefeelingbullietservices/usefeelingtos.html";
	public static final Float THRESHOLD_YELLOW = 0.3f;
	public static final Float THRESHOLD_GREEN = 0.5f;
	public static final Double DEFAULT_LAT = 42.8782;
	public static final Double DEFAULT_LON = -8.5448;
	public static final CharSequence HAS_EVENTS = "hasevents";
	public static final CharSequence HAS_PROMOS = "haspromos";
	public static final CharSequence CHECKINS = "checkins";
	public static final String USERID = "userid";
	public static final String PLACEID = "placeid";
	public static final CharSequence RED = "red";
	public static final CharSequence YELLOW = "yellow";
	public static final CharSequence GREEN = "green";
	public static final String DEFAULT_ICON = "defaulticon";
	
	private Long userid = -1L;
	private String password = "";
	private String timestamp = "";
	
	private HttpClient client = null;
	private HttpPost request = null;
	private HttpResponse response = null;
	private HttpEntity entity = null;
	private InputStream instream = null;
	private Gson gson = null;
	private Result lastResult;
	private int contador;
	
	/**
	 * Constructor.
	 * @param userid Identificador del usuario.
	 * @param password Contraseña de acceso.
	 */
	public UseFeeling(long userid, String password) {
		this.userid = userid;
		this.password = password;
		this.gson = new Gson();
		this.contador = 0;
		this.setHttpClient();
	}
	
	/**
	 * Constructor por defecto.
	 */
	public UseFeeling(){
		this.gson = new Gson();
		this.contador = 0;
		this.setHttpClient();
	}
	
	/**
	 * Obtiene el resultado de la última operación ejecutada por esta instancia.
	 * @return El último resultado registrado.
	 */
	public Result getLastResult() {
		if (this.lastResult == null) this.lastResult = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
		return this.lastResult;
	}
	
	/**
	 * Establece el identificador del usuario.
	 * @param userid Identificador del usuario.
	 */
	public void setUserId(Long userid) {
		this.userid = userid;
	}
	
	/**
	 * Establece la contraseña de acceso.
	 * @param password La contraseña de acceso.
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * Establece el cliente HTTP.
	 */
	private void setHttpClient() {
		BasicHttpParams params = new BasicHttpParams();
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme(scheme, PlainSocketFactory.getSocketFactory(), port));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
		HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT);
		this.client = new DefaultHttpClient(cm, params);
	}
	
	/**
	 * Establece el cliente HTTP.
	 */
	private void setHttpClientWithoutTimeout() {
		BasicHttpParams params = new BasicHttpParams();
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme(scheme, PlainSocketFactory.getSocketFactory(), port));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
		this.client = new DefaultHttpClient(cm, params);
	}
	
	/**
	 * Crea una petición HTTP con los parámetros adecuados para la operación especificada.
	 * @param opcode Código del servicio invocado.
	 * @param paramsStr Lista de parámetros a añadir.
	 * @return La petición HTTP.
	 * @throws URISyntaxException 
	 * @throws UnsupportedEncodingException 
	 */
 	private HttpPost createRequest(int opcode, String... paramsStr) throws URISyntaxException, UnsupportedEncodingException {
		URI uri = this.createUri(opcode, paramsStr);
		return new HttpPost(uri);
	}
	
	/**
	 * Crea el URI adecuado para la operación especificada.
	 * @param opcode Código de la operación (valor de la clase OperationsCodes).
	 * @param paramsStr Lista de parámetros a añadir.
	 * @return El URI.
	 * @throws URISyntaxException 
	 * @throws UnsupportedEncodingException 
	 */
	public URI createUri(int opcode, String... paramsStr) throws URISyntaxException, UnsupportedEncodingException {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String service = "";
		
		//Parameters
		switch (opcode) {
			case OperationsCodes.GetNotifications:
				service = ServicesNames.GetNotifications;
				break;
			case OperationsCodes.GetNotificationsByDate:
				service = ServicesNames.GetNotificationsByDate;
				params.add(new BasicNameValuePair(ParametersNames.tsnotification, paramsStr[0]));
				break;
			case OperationsCodes.GetAccount:
				service = ServicesNames.GetAccount;
				params.add(new BasicNameValuePair(ParametersNames.appversion, paramsStr[0]));
				params.add(new BasicNameValuePair(ParametersNames.appversioncode, paramsStr[1]));
				break;
			case OperationsCodes.GetCities:
				service = ServicesNames.GetCities;
				break;
			case OperationsCodes.GetDefaultPosition:
				service = ServicesNames.GetDefaultPosition;
				break;
			case OperationsCodes.CreateUser:
				service = ServicesNames.CreateUser;
				params.add(new BasicNameValuePair(ParametersNames.email, paramsStr[0]));
				params.add(new BasicNameValuePair(ParametersNames.password, paramsStr[1]));
				params.add(new BasicNameValuePair(ParametersNames.name, URLEncoder.encode(paramsStr[2], "UTF-8")));
				params.add(new BasicNameValuePair(ParametersNames.birthdate, paramsStr[3]));
				params.add(new BasicNameValuePair(ParametersNames.genre, paramsStr[4]));
				params.add(new BasicNameValuePair(ParametersNames.cellnumber, paramsStr[5]));
				params.add(new BasicNameValuePair(ParametersNames.facebook, paramsStr[6]));
				break;
			case OperationsCodes.UpdateUser:
				service = ServicesNames.UpdateUser;
				params.add(new BasicNameValuePair(ParametersNames.email, paramsStr[0]));
				params.add(new BasicNameValuePair(ParametersNames.name, URLEncoder.encode(paramsStr[1], "UTF-8")));
				params.add(new BasicNameValuePair(ParametersNames.birthdate, paramsStr[2]));
				params.add(new BasicNameValuePair(ParametersNames.genre, paramsStr[3]));
				params.add(new BasicNameValuePair(ParametersNames.cellnumber, paramsStr[4]));
				break;
			case OperationsCodes.RemoveUser:
				service = ServicesNames.RemoveUser;
				break;
			case OperationsCodes.SetProfilePicture:
				service = ServicesNames.SetProfilePicture;
				break;
			case OperationsCodes.GetProfilePicture:
				service = ServicesNames.GetProfilePicture;
				params.add(new BasicNameValuePair(ParametersNames.friendid, paramsStr[0]));
				break;
			case OperationsCodes.GetPlaceIcon:
				service = ServicesNames.GetPlaceIcon;
				params.add(new BasicNameValuePair(ParametersNames.placeid, paramsStr[0]));
				break;
			case OperationsCodes.GetVisitsAndFavorites:
				service = ServicesNames.GetVisitsAndFavorites;
				params.add(new BasicNameValuePair(ParametersNames.placeid, paramsStr[0]));
				break;
			case OperationsCodes.GetEventIcon:
				service = ServicesNames.GetEventIcon;
				params.add(new BasicNameValuePair(ParametersNames.eventid, paramsStr[0]));
				break;
			case OperationsCodes.GetPromoIcon:
				service = ServicesNames.GetPromoIcon;
				params.add(new BasicNameValuePair(ParametersNames.promoid, paramsStr[0]));
				break;
			case OperationsCodes.Login:
				service = ServicesNames.Login;
				params.add(new BasicNameValuePair(ParametersNames.email, paramsStr[0]));
				break;
			case OperationsCodes.RememberPassword:
				service = ServicesNames.RememberPassword;
				params.add(new BasicNameValuePair(ParametersNames.email, paramsStr[0]));
				break;
			case OperationsCodes.ChangePassword:
				service = ServicesNames.ChangePassword;
				params.add(new BasicNameValuePair(ParametersNames.password, paramsStr[1]));
				break;
			case OperationsCodes.ActivateFavPlacesNotifications:
				service = ServicesNames.ActivateFavPlacesNotifications;
				break;
			case OperationsCodes.DeactivateFavPlacesNotifications:
				service = ServicesNames.DeactivateFavPlacesNotifications;
				break;
			case OperationsCodes.ActivateSuggestedProposalsNotifications:
				service = ServicesNames.ActivateSuggestedProposalsNotifications;
				break;
			case OperationsCodes.DeactivateSuggestedProposalsNotifications:
				service = ServicesNames.DeactivateSuggestedProposalsNotifications;
				break;
			case OperationsCodes.GetFavPlaces:
				service = ServicesNames.GetFavPlaces;
				params.add(new BasicNameValuePair(ParametersNames.friendid, paramsStr[0]));
				break;
			case OperationsCodes.GetAffinedPlaces:
				service = ServicesNames.GetAffinedPlaces;
				params.add(new BasicNameValuePair(ParametersNames.friendid, paramsStr[0]));
				break;
			case OperationsCodes.MarkAsFav:
				service = ServicesNames.MarkAsFav;
				params.add(new BasicNameValuePair(ParametersNames.placeid, paramsStr[0]));
				break;
			case OperationsCodes.UnmarkPlaceAsFav:
				service = ServicesNames.UnmarkPlaceAsFav;
				params.add(new BasicNameValuePair(ParametersNames.placeid, paramsStr[0]));
				break;
			case OperationsCodes.GetEventsByPlace:
				service = ServicesNames.GetEventsByPlace;
				params.add(new BasicNameValuePair(ParametersNames.placeid, paramsStr[0]));
				break;
			case OperationsCodes.GetEvents:
				service = ServicesNames.GetEvents;
				params.add(new BasicNameValuePair(ParametersNames.latitude, paramsStr[0]));
				params.add(new BasicNameValuePair(ParametersNames.longitude, paramsStr[1]));
				break;
			case OperationsCodes.GetEvent:
				service = ServicesNames.GetEvent;
				params.add(new BasicNameValuePair(ParametersNames.eventid, paramsStr[0]));
				break;
			case OperationsCodes.GetPromosByPlace:
				service = ServicesNames.GetPromosByPlace;
				params.add(new BasicNameValuePair(ParametersNames.placeid, paramsStr[0]));
				break;
			case OperationsCodes.GetPromos:
				service = ServicesNames.GetPromos;
				params.add(new BasicNameValuePair(ParametersNames.latitude, paramsStr[0]));
				params.add(new BasicNameValuePair(ParametersNames.longitude, paramsStr[1]));
				break;
			case OperationsCodes.GetPromo:
				service = ServicesNames.GetPromo;
				params.add(new BasicNameValuePair(ParametersNames.promoid, paramsStr[0]));
				break;
			case OperationsCodes.UpdateGcm:
				service = ServicesNames.UpdateGcm;
				params.add(new BasicNameValuePair(ParametersNames.gcmid, paramsStr[0]));
				break;
			case OperationsCodes.UnregisterGcm:
				service = ServicesNames.UnregisterGcm;
				break;
			case OperationsCodes.SetGpsPosition:
				service = ServicesNames.SetGpsPosition;
				params.add(new BasicNameValuePair(ParametersNames.latitude, paramsStr[0]));
				params.add(new BasicNameValuePair(ParametersNames.longitude, paramsStr[1]));
				params.add(new BasicNameValuePair(ParametersNames.tsphone, paramsStr[2]));
				params.add(new BasicNameValuePair(ParametersNames.visible, paramsStr[3]));
				break;
			case OperationsCodes.SendWifiPoints:
				service = ServicesNames.SendWifiPoints;
				params.add(new BasicNameValuePair(ParametersNames.macaddrs, paramsStr[0]));
				params.add(new BasicNameValuePair(ParametersNames.tsphone, paramsStr[1]));
				params.add(new BasicNameValuePair(ParametersNames.visible, paramsStr[2]));
				break;
			case OperationsCodes.Synchronize:
				service = ServicesNames.Synchronize;
				params.add(new BasicNameValuePair(ParametersNames.lastSyncTs, paramsStr[0]));
				break;
			case OperationsCodes.GetActivites:
				service = ServicesNames.GetActivites;
				params.add(new BasicNameValuePair(ParametersNames.lastSyncTs, paramsStr[0]));
				break;
		}
		
		//Common Parameters
		params.add(new BasicNameValuePair(ParametersNames.userid, String.valueOf(this.userid)));
		params.add(new BasicNameValuePair(ParametersNames.timestamp, this.generateTimestamp()));
		params.add(new BasicNameValuePair(ParametersNames.signature, this.createSignature(params)));
		
		//Create URI
		URI uri = URIUtils.createURI(scheme, host, port, path + service, URLEncodedUtils.format(params, "UTF-8"), null);
		return uri;
	}
	
	/**
	 * Genera un sello temporal de la fecha y hora actuales en UTC. La almacena en el atributo <i>timestamp</i>.
	 */
	private String generateTimestamp(){
		this.timestamp = String.valueOf(System.currentTimeMillis());
		return this.timestamp;
	}
	
	/**
	 * Crea la firma de la petición HTTP.
	 * @param params Lista de parámetros URL de la petición.
	 * @return La firma de la petición HTTP.
	 * @throws URISyntaxException 
	 */
	private String createSignature(List<NameValuePair> params) throws URISyntaxException {
		URI uriAux = URIUtils.createURI(scheme, host, port, path, URLEncodedUtils.format(params, "UTF-8"), null);
		return  UseFeeling.hash(uriAux.getRawQuery(), this.password);
	}
	
	/**
	 * Calcula el hash SHA256 de un mensaje
	 * @param msg Mensaje a codificar.
	 * @param salt Clave con la que calcular el hash.
	 * @return El hash SHA256 del mensaje
	 */
	private static String hash(String msg, String salt){
		try {
			String input = msg + salt;
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
	        digest.reset();
	        byte[] byteData = digest.digest(input.getBytes("UTF-8"));
	        StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < byteData.length; i++){
	            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
	         }
	         return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			return null;
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	
	/**
	 * Convierte un flujo de entrada en una cadena de caracteres.
	 * @param is El flujo de entrada.
	 * @return Una cadena de caracteres que se corresponde con el flujo de entrada.
	 */
	private String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
	}	
	
	/**
	 * Ejecuta la petición HTTP.
	 * @return El resultado de la ejecución como un objecto JSON.
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private String execute() throws ClientProtocolException, IOException {
		this.response = this.client.execute(this.request);	
		this.entity = this.response.getEntity();
		this.instream = this.entity.getContent();
		String json = this.convertStreamToString(instream);
		return json;
	}
	
	/**
	 * Ejecuta la petición HTTP sin el temporizador de conexión.
	 * @return El resultado de la ejecución como un objecto JSON.
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private String executeNoTimeOut() throws ClientProtocolException, IOException {
		this.setHttpClientWithoutTimeout();
		this.response = this.client.execute(this.request);	
		this.entity = this.response.getEntity();
		this.instream = this.entity.getContent();
		String json = this.convertStreamToString(instream);
		this.setHttpClient();
		return json;
	}
	
	/**
	 * Ejecuta la petición HTTP.
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private void executeNoConvert() throws ClientProtocolException, IOException {
		this.response = this.client.execute(this.request);
		this.entity = this.response.getEntity();
		this.instream = this.entity.getContent();
	}
	
	/**
	 * Obtiene las ciudades dadas de alta en UseFeeling.
	 * @return Una lista con las ciudades requeridas o null si ha ocurrido algún error.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<City> getCities() {
		ArrayList<City> cities = null;
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		String json = "";
		try {
			this.request = this.createRequest(OperationsCodes.GetCities, "");
			json = this.execute();
			Type type = new TypeToken<ArrayList<City>>(){}.getType();
			JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
			cities = (ArrayList<City>)this.gson.fromJson(reader, type);
			if (cities == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
		} catch (JsonParseException e) {
			try { 
				JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
				result = (Result)this.gson.fromJson(reader, Result.class);
				if (result.getCode() == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
			} catch (Exception e2) {
				result.setCode(ResultCodes.Exception);
				result.setMessage(ResultMessages.Exception(e2));
			}
		} catch (Exception e) {
			try { 
				if (++contador % MAX_NUM_ATTEMPS !=0) return this.getCities();
				JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
				result = (Result)this.gson.fromJson(reader, Result.class);
			} catch (Exception e2){
				result.setCode(ResultCodes.Exception);
				result.setMessage(ResultMessages.Exception(e2));
			}
		}
		this.lastResult = result;
		return cities;
	}
	
	/**
	 * Crea un nuevo usuario en UseFeeling.
	 * @param email Dirección de correo electrónico.
	 * @param password Contraseña de acceso. 
	 * @param name Nombre y apellidos del usuario.
	 * @param birthdate Fecha de nacimiento.
	 * @param gender Sexo del usuario.
	 * @param phone Número de móvil del usuario. Si no se especifica, dejar como cadena vacía.
	 * @param facebook Identificador de Facebook del usuario. Si no se especifica, dejar como cadena vacía.
	 * @return El resultado de la operación.
	 */
	public Result createUser(String email, String password, String name, Long birthdate, String gender, String phone, String facebook, byte[] picture) {
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		try {
			this.request = this.createRequest(OperationsCodes.CreateUser, email, password, name, birthdate.toString(), gender, phone, facebook);
			if (picture != null) {
				MultipartEntity mpe = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
				ByteArrayBody bab = new ByteArrayBody(picture, "");
				mpe.addPart(ParametersNames.picture, bab);
				this.request.setEntity(mpe);
			}
			String json = this.executeNoTimeOut(); JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
			result = (Result)(this.gson.fromJson(reader, Result.class));
			if (result == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());	
		} catch (Exception e){
			if (++contador % MAX_NUM_ATTEMPS !=0) return this.createUser(email, password, name, birthdate, gender, phone, facebook, picture);
			result.setCode(ResultCodes.Exception);
			result.setMessage(ResultMessages.Exception(e));
		}
		this.lastResult = result;
		return result;
	}
	
	/**
	 * Establece la foto de perfil del usuario.
	 * @param picture Foto de perfil del usuario.
	 * @return El resultado de la operación.
	 */
	public Result setProfilePicture(byte[] picture) {
		Result result = new Result();
		try {
			MultipartEntity mpe = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			ByteArrayBody bab = new ByteArrayBody(picture, String.valueOf(this.userid));
			mpe.addPart(ParametersNames.picture, bab);
			this.request = this.createRequest(OperationsCodes.SetProfilePicture, "");
			this.request.setEntity(mpe);
			String json = this.execute(); JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
			result = (Result)(this.gson.fromJson(reader, Result.class));
			if (result == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
		} catch (Exception e){
			if (++contador % MAX_NUM_ATTEMPS !=0) return this.setProfilePicture(picture);
			result.setCode(ResultCodes.Exception);
			result.setMessage(ResultMessages.Exception(e));
		}
		this.lastResult = result;
		return result;
	}

	/**
	 * Comprueba las credenciales de acceso del usuario.
	 * @param email Dirección de correo electrónico.
	 * @return El resultado de la operación.
	 */
	public Result login(String email) {
		Result result = new Result();
		try {
			this.request = this.createRequest(OperationsCodes.Login, email);
			String json = this.execute(); JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
			result = (Result)(this.gson.fromJson(reader, Result.class));
			if (result == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
		} catch (Exception e){
			if (++contador % MAX_NUM_ATTEMPS !=0) return this.login(email);
			result.setCode(ResultCodes.Exception);
			result.setMessage(ResultMessages.Exception(e));
		}
		this.lastResult = result;
		return result;
	}

	/**
	 * Comprueba si existe un usuario con la dirección de correo electrónico especificada. De ser así, envía
	 * a dicha dirección un correo con la contraseña de acceso del usuario.
	 * @param email Dirección de correo electrónico.
	 * @return El resultado de la operación.
	 */
	public Result rememberPassword(String email) {
		Result result = new Result();
		try {
			this.request = this.createRequest(OperationsCodes.RememberPassword, email);
			String json = this.execute(); JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
			result = (Result)(this.gson.fromJson(reader, Result.class));
			if (result == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
		} catch (Exception e){
			if (++contador % MAX_NUM_ATTEMPS !=0) return this.login(email);
			result.setCode(ResultCodes.Exception);
			result.setMessage(ResultMessages.Exception(e));
		}
		this.lastResult = result;
		return result;
	}

	/**
	 * Obtiene los datos de la cuenta del usuario.
	 * @param version 
	 * @return Los datos de la cuenta del usuario.
	 */
	public Account getAccount(String versionName, Integer versionCode) {
		Account account = null;
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		String json = "";
		try {
			this.request = this.createRequest(OperationsCodes.GetAccount, versionName, versionCode.toString());
			json = this.execute();
			account = (Account)gson.fromJson(json, Account.class);
			if (account == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
		} catch (JsonParseException e) {
			try { 
				JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
				result = (Result)this.gson.fromJson(reader, Result.class);
				if (result.getCode() == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
			} catch (Exception e2) {
				result.setCode(ResultCodes.Exception);
				result.setMessage(ResultMessages.Exception(e2));
			}
		} catch (Exception e) {
			try { 
				if (++contador % MAX_NUM_ATTEMPS !=0) return this.getAccount(versionName, versionCode);
				JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
				result = (Result)this.gson.fromJson(reader, Result.class);
			} catch (Exception e2){
				result.setCode(ResultCodes.Exception);
				result.setMessage(ResultMessages.Exception(e2));
			}
		}
		this.lastResult = result;
		return account;
	}
	
	/**
	 * Obtiene las últimas notificaciones del usuario.
	 * @return Una lista con las últimas notificaciones del usuario.
	 */

	@SuppressWarnings("unchecked")
	public ArrayList<Notification> getNotifications() {
		ArrayList<Notification> notifications = null;
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		String json = "";
		try {
			this.request = this.createRequest(OperationsCodes.GetNotifications, "");
			json = this.execute();
			Type type = new TypeToken<ArrayList<Notification>>(){}.getType();
			JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
			notifications = (ArrayList<Notification>)this.gson.fromJson(reader, type);
			if (notifications == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
		} catch (JsonParseException e) {
			try { 
				JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
				result = (Result)this.gson.fromJson(reader, Result.class);
				if (result.getCode() == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
			} catch (Exception e2) {
				result.setCode(ResultCodes.Exception);
				result.setMessage(ResultMessages.Exception(e2));
			}
		} catch (Exception e) {
			try { 
				if (++contador % MAX_NUM_ATTEMPS !=0) return this.getNotifications();
				JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
				result = (Result)this.gson.fromJson(reader, Result.class);
			} catch (Exception e2){
				result.setCode(ResultCodes.Exception);
				result.setMessage(ResultMessages.Exception(e2));
			}
		}
		this.lastResult = result;
		return notifications;
	}

	/**
	 * Obtiene la foto de perfil del usuario especificado.
	 * @param friendid Identificador del usuario.
	 * @return La foto de perfil del usuario.
	 */
	public Bitmap getProfilePicture(long friendid) {
		byte[] picture = null;
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		if (friendid < 0) friendid = this.userid;
		try {
			this.request = this.createRequest(OperationsCodes.GetProfilePicture, String.valueOf(friendid));
			this.executeNoConvert();
			try {
				picture = org.apache.commons.io.IOUtils.toByteArray(instream);
				if (picture == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
			} catch (Exception e) {
				result = new Result(ResultCodes.Exception, ResultMessages.Exception(e));
			} finally {
				if (this.instream != null) this.instream.close();
			}
		} catch (Exception e) {
			if (++contador % MAX_NUM_ATTEMPS !=0) return this.getProfilePicture(friendid);
			result.setCode(ResultCodes.Exception);
			result.setMessage(ResultMessages.Exception(e));
			return null;
		} finally {
			this.lastResult = result;
		}
		try {
			return BitmapFactory.decodeByteArray(picture , 0, picture.length);
		} catch (OutOfMemoryError e) {
			this.lastResult = new Result(ResultCodes.OutOfMemoryError, ResultMessages.OutOfMemoryError(e));
			return null;
		}
	}
	
	/**
	 * Obtiene la foto de perfil del usuario especificado.
	 * @param friendid Identificador del usuario.
	 * @return La foto de perfil del usuario.
	 */
	public byte[] getProfilePicture2(long friendid) {
		byte[] picture = null;
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		try {
			this.request = this.createRequest(OperationsCodes.GetProfilePicture, String.valueOf(friendid));
			this.executeNoConvert();
			try {
				picture = org.apache.commons.io.IOUtils.toByteArray(instream);
				this.instream.close();
			} catch (Exception e) {
				result = new Result(ResultCodes.Exception, ResultMessages.Exception(e));
			}
			if (picture == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
		} catch (Exception e){
			if (++contador % MAX_NUM_ATTEMPS !=0) return this.getProfilePicture2(friendid);
			result.setCode(ResultCodes.Exception);
			result.setMessage(ResultMessages.Exception(e));
			return null;
		} finally {
			this.lastResult = result;
		}
		return picture;
	}

	/**
	 * Obtiene el icono del local.
	 * @param placeid Identificador del local.
 	 * @return El icono del local.
	 */
	public Bitmap getVenueIcon(Long placeid) {
		byte[] picture = null;
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		try {
			this.request = this.createRequest(OperationsCodes.GetPlaceIcon, String.valueOf(placeid));
			this.executeNoConvert();
			try {
				picture = org.apache.commons.io.IOUtils.toByteArray(instream);
				this.instream.close();
			} catch (Exception e) {
				result = new Result(ResultCodes.Exception, ResultMessages.Exception(e));
			}
			if (picture == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
		} catch (Exception e){
			if (++contador % MAX_NUM_ATTEMPS !=0) return this.getVenueIcon(placeid);
			result.setCode(ResultCodes.Exception);
			result.setMessage(ResultMessages.Exception(e));
			return null;
		} finally {
			this.lastResult = result;
		}
		return BitmapFactory.decodeByteArray(picture , 0, picture.length);
	}

	/**
	 * Modifica la contraseña del usuario.
	 * @param oldPassword Antigua contraseña del usuario.
	 * @param newPassword Nueva contraseña del usuario.
	 * @return El resultado de la operación.
	 */
	public Result changePassword(String oldPassword, String newPassword) {
		Result result = new Result();
		try {
			this.request = this.createRequest(OperationsCodes.ChangePassword, oldPassword, newPassword);
			String json = this.execute(); JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
			result = (Result)(this.gson.fromJson(reader, Result.class));
			if (result == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
		} catch (Exception e){
			if (++contador % MAX_NUM_ATTEMPS !=0) return this.changePassword(oldPassword, newPassword);
			result.setCode(ResultCodes.Exception);
			result.setMessage(ResultMessages.Exception(e));
		}
		this.lastResult = result;
		return result;
	}

	/**
	 * Actualiza los datos personales del usuario.
	 * @param email Nueva dirección de correo electrónico.
	 * @param name Nuevo nombre del usuario.
	 * @param birthdate Nueva fecha de nacimiento.
	 * @param gender Nuevo sexo.
	 * @param phone Nuevo número de móvil.
	 * @return El resultado de la operación.
	 */
	public Result updateUser(String email, String name, Long birthdate, String gender, String phone) {
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		try {
			this.request = this.createRequest(OperationsCodes.UpdateUser, email, name, birthdate.toString(), gender, phone);
			String json = this.execute(); JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
			result = (Result)(this.gson.fromJson(reader, Result.class));
			if (result == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());	
		} catch (Exception e){
			if (++contador % MAX_NUM_ATTEMPS !=0) return this.updateUser(email, name, birthdate, gender, phone);
			result.setCode(ResultCodes.Exception);
			result.setMessage(ResultMessages.Exception(e));
		}
		this.lastResult = result;
		return result;
	}

	/**
	 * Elimina el usuario de UseFeeling.
	 * @return El resultado de la operación.
	 */
	public Result removeUser() {
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		try {
			this.request = this.createRequest(OperationsCodes.RemoveUser, "");
			String json = this.execute(); JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
			result = (Result)(this.gson.fromJson(reader, Result.class));
			if (result == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());	
		} catch (Exception e){
			if (++contador % MAX_NUM_ATTEMPS !=0) return this.removeUser();
			result.setCode(ResultCodes.Exception);
			result.setMessage(ResultMessages.Exception(e));
		}
		this.lastResult = result;
		return result;
	}

	/**
	 * Activa la recepción de notificaciones de los locales favoritos del usuario.
	 * @return El resultado de la operación.
	 */
	public Result activateFavPlacesNotifications() {
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		try {
			this.request = this.createRequest(OperationsCodes.ActivateFavPlacesNotifications, "");
			String json = this.execute(); JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
			result = (Result)(this.gson.fromJson(reader, Result.class));
			if (result == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());	
		} catch (Exception e){
			if (++contador % MAX_NUM_ATTEMPS !=0) return this.activateFavPlacesNotifications();
			result.setCode(ResultCodes.Exception);
			result.setMessage(ResultMessages.Exception(e));
		}
		this.lastResult = result;
		return result;
	}

	/**
	 * Desactiva la recepción de notificaciones de los locales favoritos del usuario.
	 * @return El resultado de la operación.
	 */
	public Result deactivateFavPlacesNotifications() {
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		try {
			this.request = this.createRequest(OperationsCodes.DeactivateFavPlacesNotifications, "");
			String json = this.execute(); JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
			result = (Result)(this.gson.fromJson(reader, Result.class));
			if (result == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());	
		} catch (Exception e){
			if (++contador % MAX_NUM_ATTEMPS !=0) return this.deactivateFavPlacesNotifications();
			result.setCode(ResultCodes.Exception);
			result.setMessage(ResultMessages.Exception(e));
		}
		this.lastResult = result;
		return result;
	}

	/**
	 * Activa la recepción de notificaciones de las propuestas sugeridas al usuario.
	 * @return El resultado de la operación.
	 */
	public Result activateSuggestedProposalsNotifications() {
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		try {
			this.request = this.createRequest(OperationsCodes.ActivateSuggestedProposalsNotifications, "");
			String json = this.execute(); JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
			result = (Result)(this.gson.fromJson(reader, Result.class));
			if (result == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());	
		} catch (Exception e){
			if (++contador % MAX_NUM_ATTEMPS !=0) return this.activateSuggestedProposalsNotifications();
			result.setCode(ResultCodes.Exception);
			result.setMessage(ResultMessages.Exception(e));
		}
		this.lastResult = result;
		return result;
	}

	/**
	 * Desactiva la recepción de notificaciones de las propuestas sugeridas al usuario.
	 * @return El resultado de la operación.
	 */
	public Result deactivateSuggestedProposalsNotifications() {
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		try {
			this.request = this.createRequest(OperationsCodes.DeactivateSuggestedProposalsNotifications, "");
			String json = this.execute(); JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
			result = (Result)(this.gson.fromJson(reader, Result.class));
			if (result == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());	
		} catch (Exception e){
			if (++contador % MAX_NUM_ATTEMPS !=0) return this.deactivateSuggestedProposalsNotifications();
			result.setCode(ResultCodes.Exception);
			result.setMessage(ResultMessages.Exception(e));
		}
		this.lastResult = result;
		return result;
	}

	/**
	 * Obtiene los locales favoritos del usuario especificado.
	 * @param userid2 Identificador del usuario.
	 * @return Una lista con los locales favoritos del usuario.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Place> getFavPlaces(Long userid2) {
		ArrayList<Place> places = null;
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		String json = "";
		try {
			this.request = this.createRequest(OperationsCodes.GetFavPlaces, userid2.toString());
			json = this.execute();
			Type type = new TypeToken<ArrayList<Place>>(){}.getType();
			JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
			places = (ArrayList<Place>)this.gson.fromJson(reader, type);
			if (places == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
		} catch (JsonParseException e) {
			try { 
				JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
				result = (Result)this.gson.fromJson(reader, Result.class);
				if (result.getCode() == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
			} catch (Exception e2) {
				result.setCode(ResultCodes.Exception);
				result.setMessage(ResultMessages.Exception(e2));
			}
		} catch (Exception e) {
			try { 
				if (++contador % MAX_NUM_ATTEMPS !=0) return this.getFavPlaces(userid2);
				JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
				result = (Result)this.gson.fromJson(reader, Result.class);
			} catch (Exception e2){
				result.setCode(ResultCodes.Exception);
				result.setMessage(ResultMessages.Exception(e2));
			}
		}
		this.lastResult = result;
		return places;
	}

	/**
	 * Marca un local como favorito del usuario.
	 * @param placeid Identificador del local.
	 * @return El resultado de la operación.
	 */
	public Result markPlaceAsFav(Long placeid) {
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		try {
			this.request = this.createRequest(OperationsCodes.MarkAsFav, placeid.toString());
			String json = this.execute(); JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
			result = (Result)(this.gson.fromJson(reader, Result.class));
			if (result == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());	
		} catch (Exception e){
			if (++contador % MAX_NUM_ATTEMPS !=0) return this.markPlaceAsFav(placeid);
			result.setCode(ResultCodes.Exception);
			result.setMessage(ResultMessages.Exception(e));
		}
		this.lastResult = result;
		return result;
	}
	
	/**
	 * Desmarca un local como favorito del usuario.
	 * @param placeid Identificador del local.
	 * @return El resultado de la operación.
	 */
	public Result unmarkPlaceAsFav(Long placeid) {
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		try {
			this.request = this.createRequest(OperationsCodes.UnmarkPlaceAsFav, placeid.toString());
			String json = this.execute(); JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
			result = (Result)(this.gson.fromJson(reader, Result.class));
			if (result == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());	
		} catch (Exception e){
			if (++contador % MAX_NUM_ATTEMPS !=0) return this.unmarkPlaceAsFav(placeid);
			result.setCode(ResultCodes.Exception);
			result.setMessage(ResultMessages.Exception(e));
		}
		this.lastResult = result;
		return result;
	}

	/**
	 * Obtiene los eventos vigentes del local especificado.
	 * @param placeid Identificador del local.
	 * @return La lista de los eventos vigentes del local especificado.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Event> getEventsByPlace(Long placeid) {
		ArrayList<Event> events = null;
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		String json = "";
		try {
			this.request = this.createRequest(OperationsCodes.GetEventsByPlace, placeid.toString());
			json = this.execute();
			Type type = new TypeToken<ArrayList<Event>>(){}.getType();
			JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
			events = (ArrayList<Event>)this.gson.fromJson(reader, type);
			if (events == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
		} catch (JsonParseException e) {
			try { 
				JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
				result = (Result)this.gson.fromJson(reader, Result.class);
				if (result.getCode() == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
			} catch (Exception e2) {
				result.setCode(ResultCodes.Exception);
				result.setMessage(ResultMessages.Exception(e2));
			}
		} catch (Exception e) {
			try { 
				if (++contador % MAX_NUM_ATTEMPS !=0) return this.getEventsByPlace(placeid);
				JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
				result = (Result)this.gson.fromJson(reader, Result.class);
			} catch (Exception e2){
				result.setCode(ResultCodes.Exception);
				result.setMessage(ResultMessages.Exception(e2));
			}
		}
		this.lastResult = result;
		return events;
	}

	/**
	 * Obtiene las promociones vigentes del local especificado.
	 * @param placeid Identificador del local.
	 * @return La lista de las promociones vigentes del local especificado.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Promo> getPromosByPlace(Long placeid) {
		ArrayList<Promo> promos = null;
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		String json = "";
		try {
			this.request = this.createRequest(OperationsCodes.GetPromosByPlace, placeid.toString());
			json = this.execute();
			Type type = new TypeToken<ArrayList<Promo>>(){}.getType();
			JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
			promos = (ArrayList<Promo>)this.gson.fromJson(reader, type);
			if (promos == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
		} catch (JsonParseException e) {
			try { 
				JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
				result = (Result)this.gson.fromJson(reader, Result.class);
				if (result.getCode() == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
				if (result.getCode() == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
			} catch (Exception e2) {
				result.setCode(ResultCodes.Exception);
				result.setMessage(ResultMessages.Exception(e2));
			}
		} catch (Exception e) {
			try { 
				if (++contador % MAX_NUM_ATTEMPS !=0) return this.getPromosByPlace(placeid);
				JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
				result = (Result)this.gson.fromJson(reader, Result.class);
			} catch (Exception e2){
				result.setCode(ResultCodes.Exception);
				result.setMessage(ResultMessages.Exception(e2));
			}
		}
		this.lastResult = result;
		return promos;
	}

	/**
	 * Obtiene las notificaciones que el usuario haya recibido hasta la fecha indicada.
	 * @param timestamp2 Fecha.
	 * @return La lista de notificaciones.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Notification> getNotificationsByDate(Long timestamp2) {
		ArrayList<Notification> notifications = null;
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		String json = "";
		try {
			this.request = this.createRequest(OperationsCodes.GetNotificationsByDate, timestamp2.toString());
			json = this.execute();
			Type type = new TypeToken<ArrayList<Notification>>(){}.getType();
			JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
			notifications = (ArrayList<Notification>)this.gson.fromJson(reader, type);
			if (notifications == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
		} catch (JsonParseException e) {
			try { 
				JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
				result = (Result)this.gson.fromJson(reader, Result.class);
				if (result.getCode() == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
				if (result.getCode() == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
			} catch (Exception e2) {
				result.setCode(ResultCodes.Exception);
				result.setMessage(ResultMessages.Exception(e2));
			}
		} catch (Exception e) {
			try { 
				if (++contador % MAX_NUM_ATTEMPS !=0) return this.getNotificationsByDate(timestamp2);
				JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
				result = (Result)this.gson.fromJson(reader, Result.class);
			} catch (Exception e2){
				result.setCode(ResultCodes.Exception);
				result.setMessage(ResultMessages.Exception(e2));
			}
		}
		this.lastResult = result;
		return notifications;
	}

	/**
	 * Devuelve los datos personales del amigo identificado por el parámetro friendid. Si friendid es igual a userid, se devuelven los datos personales del usuario.
	 * @param friendid Identificador del amigo.
	 * @return Los datos del amigo.
	 */
	public User getFriend(Long friendid) {
		User friend = null;
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		String json = "";
		try {
			this.request = this.createRequest(OperationsCodes.GetFriend, friendid.toString());
			json = this.execute();
			friend = (User)gson.fromJson(json, User.class);
			if (friend == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
			if (friend.getUserid() == null) throw new JsonParseException("");
		} catch (JsonParseException e) {
			try { 
				JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
				result = (Result)this.gson.fromJson(reader, Result.class);
				if (result.getCode() == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
			} catch (Exception e2) {
				result.setCode(ResultCodes.Exception);
				result.setMessage(ResultMessages.Exception(e2));
			}
		} catch (Exception e) {
			try { 
				if (++contador % MAX_NUM_ATTEMPS !=0) return this.getFriend(friendid);
				JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
				result = (Result)this.gson.fromJson(reader, Result.class);
			} catch (Exception e2){
				result.setCode(ResultCodes.Exception);
				result.setMessage(ResultMessages.Exception(e2));
			}
		}
		this.lastResult = result;
		return friend;
	}

	/**
	 * Actualiza el identificador GCM del usuario.
	 * @param regId Identificador GCM.
	 * @return El resultado de la operación.
	 */
	public Result updateGcmId(String regId) {
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		try {
			this.request = this.createRequest(OperationsCodes.UpdateGcm, regId);
			String json = this.execute(); JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
			result = (Result)(this.gson.fromJson(reader, Result.class));
			if (result == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());	
		} catch (Exception e){
			if (++contador % MAX_NUM_ATTEMPS !=0) return this.updateGcmId(regId);
			result.setCode(ResultCodes.Exception);
			result.setMessage(ResultMessages.Exception(e));
		}
		this.lastResult = result;
		return result;
	}

	/**
	 * Elimina el identificador GCM del usuario.
	 * @return El resultado de la operación.
	 */
	public Result unregisterGcm() {
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		try {
			this.request = this.createRequest(OperationsCodes.UnregisterGcm, "");
			String json = this.execute(); JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
			result = (Result)(this.gson.fromJson(reader, Result.class));
			if (result == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());	
		} catch (Exception e){
			if (++contador % MAX_NUM_ATTEMPS !=0) return this.unregisterGcm();
			result.setCode(ResultCodes.Exception);
			result.setMessage(ResultMessages.Exception(e));
		}
		this.lastResult = result;
		return result;
	}

	/**
	 * Envía las direcciones MAC de los puntos wifi detectados.
	 * @param macs Lista de direcciones MAC.
	 * @param timestamp Sello temporal del escaneo.
	 * @param visible Visibilidad del usuario.
	 * @return El resultado de la operación.
	 */
	public Result sendWifiPoints(ArrayList<String> macs, Long timestamp, Boolean visible) {
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		Gson gson = new Gson();
		try {
			this.request = this.createRequest(OperationsCodes.SendWifiPoints, gson.toJson(macs), timestamp.toString(), visible.toString());
			String json = this.execute(); JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
			result = (Result)(this.gson.fromJson(reader, Result.class));
			if (result == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());	
		} catch (Exception e){
			if (++contador % MAX_NUM_ATTEMPS !=0) return this.sendWifiPoints(macs, timestamp, visible);
			result.setCode(ResultCodes.Exception);
			result.setMessage(ResultMessages.Exception(e));
		}
		this.lastResult = result;
		return result;
	}

	/**
	 * Obtiene los datos de un evento.
	 * @param eventid Identificador del evento.
	 * @return El evento.
	 */
	public Event getEvent(Long eventid) {
		Event event = null;
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		String json = "";
		try {
			this.request = this.createRequest(OperationsCodes.GetEvent, eventid.toString());
			json = this.execute();
			event = (Event)gson.fromJson(json, Event.class);
			if (event == null || event.getEventId() == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
		} catch (JsonParseException e) {
			try { 
				JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
				result = (Result)this.gson.fromJson(reader, Result.class);
				if (result.getCode() == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
			} catch (Exception e2) {
				result.setCode(ResultCodes.Exception);
				result.setMessage(ResultMessages.Exception(e2));
			}
		} catch (Exception e) {
			try { 
				if (++contador % MAX_NUM_ATTEMPS !=0) return this.getEvent(eventid);
				JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
				result = (Result)this.gson.fromJson(reader, Result.class);
			} catch (Exception e2){
				result.setCode(ResultCodes.Exception);
				result.setMessage(ResultMessages.Exception(e2));
			}
		}
		this.lastResult = result;
		return event;
	}

	/**
	 * Obtiene los datos de una promoción a partir de su identificador.
	 * @param promoid Identificador de la promoción.
	 * @return La promoción.
	 */
	public Promo getPromo(Long promoid) {
		Promo promo = null;
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		String json = "";
		try {
			this.request = this.createRequest(OperationsCodes.GetPromo, promoid.toString());
			json = this.execute();
			promo = (Promo)gson.fromJson(json, Promo.class);
			if (promo == null || promo.getPromoId() == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
		} catch (JsonParseException e) {
			try { 
				JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
				result = (Result)this.gson.fromJson(reader, Result.class);
				if (result.getCode() == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
			} catch (Exception e2) {
				result.setCode(ResultCodes.Exception);
				result.setMessage(ResultMessages.Exception(e2));
			}
		} catch (Exception e) {
			try { 
				if (++contador % MAX_NUM_ATTEMPS !=0) return this.getPromo(promoid);
				JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
				result = (Result)this.gson.fromJson(reader, Result.class);
			} catch (Exception e2){
				result.setCode(ResultCodes.Exception);
				result.setMessage(ResultMessages.Exception(e2));
			}
		}
		this.lastResult = result;
		return promo;
	}

	/**
	 * Obtiene los locales a sincronizar con la caché local.
	 * @param lastSyncTs Instante de la última sincronización.
	 * @return La lista de locales a sincronizar.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Place> synchronize(Long lastSyncTs) {
		ArrayList<Place> places = null;
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		String json = "";
		try {
			this.request = this.createRequest(OperationsCodes.Synchronize, lastSyncTs.toString());
			json = this.execute();
			Type type = new TypeToken<ArrayList<Place>>(){}.getType();
			JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
			places = (ArrayList<Place>)this.gson.fromJson(reader, type);
			if (places == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
		} catch (JsonParseException e) {
			try { 
				JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
				result = (Result)this.gson.fromJson(reader, Result.class);
				if (result.getCode() == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
			} catch (Exception e2) {
				result.setCode(ResultCodes.Exception);
				result.setMessage(ResultMessages.Exception(e2));
			}
		} catch (Exception e) {
			try { 
				if (++contador % MAX_NUM_ATTEMPS !=0) return this.synchronize(lastSyncTs);
				JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
				result = (Result)this.gson.fromJson(reader, Result.class);
			} catch (Exception e2){
				result.setCode(ResultCodes.Exception);
				result.setMessage(ResultMessages.Exception(e2));
			}
		}
		this.lastResult = result;
		return places;
	}

	/**
	 * Obtiene la imagen de un evento.
	 * @param eventid Identificador del evento.
	 * @return La imagen del evento.
	 */
	public byte[] getEventIcon(Long eventid) {
		byte[] picture = null;
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		try {
			this.request = this.createRequest(OperationsCodes.GetEventIcon, String.valueOf(eventid));
			this.executeNoConvert();
			try {
				picture = org.apache.commons.io.IOUtils.toByteArray(instream);
				this.instream.close();
			} catch (Exception e) {
				result = new Result(ResultCodes.Exception, ResultMessages.Exception(e));
			}
			if (picture == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
		} catch (Exception e){
			if (++contador % MAX_NUM_ATTEMPS !=0) return this.getEventIcon(eventid);
			result.setCode(ResultCodes.Exception);
			result.setMessage(ResultMessages.Exception(e));
			return null;
		} finally {
			this.lastResult = result;
		}
		return picture;
	}
	
	/**
	 * Obtiene la imagen de una promoción.
	 * @param promoid Identificador de la promoción.
	 * @return La imagen de la promoción.
	 */
	public byte[] getPromoIcon(Long promoid) {
		byte[] picture = null;
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		try {
			this.request = this.createRequest(OperationsCodes.GetPromoIcon, String.valueOf(promoid));
			this.executeNoConvert();
			try {
				picture = org.apache.commons.io.IOUtils.toByteArray(instream);
				this.instream.close();
			} catch (Exception e) {
				result = new Result(ResultCodes.Exception, ResultMessages.Exception(e));
			}
			if (picture == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
		} catch (Exception e){
			if (++contador % MAX_NUM_ATTEMPS !=0) return this.getPromoIcon(promoid);
			result.setCode(ResultCodes.Exception);
			result.setMessage(ResultMessages.Exception(e));
			return null;
		} finally {
			this.lastResult = result;
		}
		return picture;
	}

	/**
	 * Obtiene el número de visitas y de favoritos del local especificado.
	 * @param placeid Identificador del local.
	 * @return El resultado de la operación. En su campo payload contiene la información solicitada según el formato VISITAS;FAVORITOS.
	 */
	public Result getVisitsAndFavorites(Long placeid) {
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		try {
			this.request = this.createRequest(OperationsCodes.GetVisitsAndFavorites, placeid.toString());
			String json = this.execute(); JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
			result = (Result)(this.gson.fromJson(reader, Result.class));
			if (result == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());	
		} catch (Exception e){
			if (++contador % MAX_NUM_ATTEMPS !=0) return this.getVisitsAndFavorites(placeid);
			result.setCode(ResultCodes.Exception);
			result.setMessage(ResultMessages.Exception(e));
		}
		this.lastResult = result;
		return result;
	}

	/**
	 * Obtiene los eventos cercanos a la posición indicada.
	 * @param latitude Latitud.
	 * @param longitude Longitud.
	 * @return La lista de eventos.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Event> getEvents(Double latitude, Double longitude) {
		ArrayList<Event> events = null;
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		String json = "";
		try {
			this.request = this.createRequest(OperationsCodes.GetEvents, latitude.toString(), longitude.toString());
			json = this.execute();
			Type type = new TypeToken<ArrayList<Event>>(){}.getType();
			JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
			events = (ArrayList<Event>)this.gson.fromJson(reader, type);
			if (events == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
		} catch (JsonParseException e) {
			try { 
				JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
				result = (Result)this.gson.fromJson(reader, Result.class);
				if (result.getCode() == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
			} catch (Exception e2) {
				result.setCode(ResultCodes.Exception);
				result.setMessage(ResultMessages.Exception(e2));
			}
		} catch (Exception e) {
			try { 
				if (++contador % MAX_NUM_ATTEMPS !=0) return this.getEvents(latitude, longitude);
				JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
				result = (Result)this.gson.fromJson(reader, Result.class);
			} catch (Exception e2){
				result.setCode(ResultCodes.Exception);
				result.setMessage(ResultMessages.Exception(e2));
			}
		}
		this.lastResult = result;
		return events;
	}

	/**
	 * Obtiene las promociones próximas a la posición indicada.
	 * @param latitude Latitud.
	 * @param longitude Longitud.
	 * @return La lista de promociones.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Promo> getPromos(Double latitude, Double longitude) {
		ArrayList<Promo> promos = null;
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		String json = "";
		try {
			this.request = this.createRequest(OperationsCodes.GetPromos, latitude.toString(), longitude.toString());
			json = this.execute();
			Type type = new TypeToken<ArrayList<Promo>>(){}.getType();
			JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
			promos = (ArrayList<Promo>)this.gson.fromJson(reader, type);
			if (promos == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
		} catch (JsonParseException e) {
			try { 
				JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
				result = (Result)this.gson.fromJson(reader, Result.class);
				if (result.getCode() == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
			} catch (Exception e2) {
				result.setCode(ResultCodes.Exception);
				result.setMessage(ResultMessages.Exception(e2));
			}
		} catch (Exception e) {
			try { 
				if (++contador % MAX_NUM_ATTEMPS !=0) return this.getPromos(latitude, longitude);
				JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
				result = (Result)this.gson.fromJson(reader, Result.class);
			} catch (Exception e2){
				result.setCode(ResultCodes.Exception);
				result.setMessage(ResultMessages.Exception(e2));
			}
		}
		this.lastResult = result;
		return promos;
	}

	/**
	 * Obtiene los locales más afines del usuario especificado.
	 * @param friendid Identificador del usuario.
	 * @return Lista con los locales más afines del usuario.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Place> getAffinedPlaces(Long friendid) {
		ArrayList<Place> places = null;
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		String json = "";
		try {
			this.request = this.createRequest(OperationsCodes.GetAffinedPlaces, friendid.toString());
			json = this.execute();
			Type type = new TypeToken<ArrayList<Place>>(){}.getType();
			JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
			places = (ArrayList<Place>)this.gson.fromJson(reader, type);
			if (places == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
		} catch (JsonParseException e) {
			try { 
				JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
				result = (Result)this.gson.fromJson(reader, Result.class);
				if (result.getCode() == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
			} catch (Exception e2) {
				result.setCode(ResultCodes.Exception);
				result.setMessage(ResultMessages.Exception(e2));
			}
		} catch (Exception e) {
			try { 
				if (++contador % MAX_NUM_ATTEMPS !=0) return this.getAffinedPlaces(friendid);
				JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
				result = (Result)this.gson.fromJson(reader, Result.class);
			} catch (Exception e2){
				result.setCode(ResultCodes.Exception);
				result.setMessage(ResultMessages.Exception(e2));
			}
		}
		this.lastResult = result;
		return places;
	}

	/**
	 * Obtiene las nuevas actividades creadas a partir del sello temporal especificado.
	 * @param lastSyncTs Sello temporal.
	 * @return La lista de las actividades.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Activity> getActivities(Long lastSyncTs) {
		ArrayList<Activity> places = null;
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		String json = "";
		try {
			this.request = this.createRequest(OperationsCodes.GetActivites, lastSyncTs.toString());
			json = this.execute();
			Type type = new TypeToken<ArrayList<Activity>>(){}.getType();
			JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
			places = (ArrayList<Activity>)this.gson.fromJson(reader, type);
			if (places == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
		} catch (JsonParseException e) {
			try { 
				JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
				result = (Result)this.gson.fromJson(reader, Result.class);
				if (result.getCode() == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
			} catch (Exception e2) {
				result.setCode(ResultCodes.Exception);
				result.setMessage(ResultMessages.Exception(e2));
			}
		} catch (Exception e) {
			try { 
				if (++contador % MAX_NUM_ATTEMPS !=0) return this.getActivities(lastSyncTs);
				JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
				result = (Result)this.gson.fromJson(reader, Result.class);
			} catch (Exception e2){
				result.setCode(ResultCodes.Exception);
				result.setMessage(ResultMessages.Exception(e2));
			}
		}
		this.lastResult = result;
		return places;
	}

	/**
	 * Retrieves the default position for server.
	 * @return The default position.
	 */
	public Position getDefaultPosition() {
		Position position = null;
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		String json = "";
		try {
			this.request = this.createRequest(OperationsCodes.GetDefaultPosition, "");
			json = this.execute();
			position = (Position)gson.fromJson(json, Position.class);
			if (position == null || position.getLatitude() == null || position.getLongitude() == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
		} catch (JsonParseException e) {
			try { 
				JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
				result = (Result)this.gson.fromJson(reader, Result.class);
				if (result.getCode() == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
			} catch (Exception e2) {
				result.setCode(ResultCodes.Exception);
				result.setMessage(ResultMessages.Exception(e2));
			}
		} catch (Exception e) {
			try { 
				if (++contador % MAX_NUM_ATTEMPS !=0) return this.getDefaultPosition();
				JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
				result = (Result)this.gson.fromJson(reader, Result.class);
			} catch (Exception e2){
				result.setCode(ResultCodes.Exception);
				result.setMessage(ResultMessages.Exception(e2));
			}
		}
		this.lastResult = result;
		return position;
	}

	/**
	 * Sets the user position.
	 * @param latitude The latitude.
	 * @param longitude The longitude.
	 * @param time The timestamp.
	 * @param visible The visibility.
	 * @return The operation's result.
	 */
	public Result setGpsPosition(Double latitude, Double longitude, Long time, Boolean visible) {
		Result result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		try {
			this.request = this.createRequest(OperationsCodes.SetGpsPosition, latitude.toString(), longitude.toString(), time.toString(), visible.toString());
			String json = this.execute(); JsonReader reader = new JsonReader(new StringReader(json)); reader.setLenient(true);
			result = (Result)(this.gson.fromJson(reader, Result.class));
			if (result == null) result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());	
		} catch (Exception e){
			if (++contador % MAX_NUM_ATTEMPS !=0) return this.setGpsPosition(latitude, longitude, time, visible);
			result.setCode(ResultCodes.Exception);
			result.setMessage(ResultMessages.Exception(e));
		}
		this.lastResult = result;
		return result;
	}
}
