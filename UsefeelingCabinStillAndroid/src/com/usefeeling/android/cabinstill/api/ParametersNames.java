package com.usefeeling.android.cabinstill.api;

/**
 * Nombres de parámetros para las operaciones en el sistema UseFeeling.
 * @author Moisés Vilar.
 *
 */
public class ParametersNames {

	/**
	 * Identificador del usuario.
	 */
	public static final String userid = "userid";
	/**
	 * Sello temporal de la operación.
	 */
	public static final String timestamp = "timestamp";
	/**
	 * Firma de la operación.
	 */
	public static final String signature = "signature";
	/**
	 * Código de la operación.
	 */
	public static final String opcode = "opcode";
	/**
	 * Dirección de correo electrónico.
	 */
	public static final String email = "email";
	/**
	 * IMEI del dispositivo desde el que se solicita la operación.
	 */
	public static final String imei = "imei";
	/**
	 * Parte de cliente para la generación de clave por el algoritmo Diffie-Hellman
	 */
	public static final String gclient = "gclient";
	/**
	 * Nombre de usuario.
	 */
	public static final String name = "name";
	/**
	 * Apellidos.
	 */
	public static final String lastname = "lastname";
	/**
	 * Nombre de la empresa.
	 */
	public static final String company = "company";
	/**
	 * Número de Identificación Fiscal de la empresa.
	 */
	public static final String nif = "nif";
	/**
	 * Identificador del usuario en Facebook.
	 */
	public static final String facebook = "facebook";
	/**
	 * Contraseña (encriptada) de acceso.
	 */
	public static final String password = "pwd";
	/**
	 * Sexo del usuario.
	 */
	public static final String genre = "genre";
	/**
	 * Fecha de nacimiento del usuario.
	 */
	public static final String birthdate = "birthdate";
	/**
	 * Número de teléfono móvil del usuario.
	 */
	public static final String cellnumber = "cellnumber";
	/**
	 * Teléfono de contacto con propietario.
	 */
	public static final String phone = "phone";
	/**
	 * Foto de perfil del usuario.
	 */
	public static final String picture = "picture";
	/**
	 * Instante silencio de posición.
	 */
	public static final String silence = "silence";
	/**
	 * Latitud.
	 */
	public static final String latitude = "lat";
	/**
	 * Longitud.
	 */
	public static final String longitude = "lon";
	/**
	 * Sello temporal de la operación desde el dispositivo (en UTC).
	 */
	public static final String tsphone = "tsphone";
	/**
	 * Dirección MAC del punto wifi detectado (en hash MD5).
	 */
	public static final String macaddress = "macaddr";
	/**
	 * Potencia de señal recibida desde el punto wifi.
	 */
	public static final String signalpower = "signalpwr";
	/**
	 * Lista de datos de usuarios.
	 */
	public static final String users = "users";
	/**
	 * Identificador de amigo.
	 */
	public static final String friendid = "friendid";
	/**
	 * Visibilidad del checkin.
	 */
	public static final String privcheckin = "pcheckin";
	/**
	 * Visibilidad de la posición GPS.
	 */
	public static final String privposgps = "pposgps";
	/**
	 * Sello temporal de última actualización de foto de perfil.
	 */
	public static final String tspicture = "tspicture";
	/**
	 * Identificador de local.
	 */
	public static final String placeid = "placeid";
	/**
	 * Número total de niveles de zoom en el dispositivo.
	 */
	public static final String numzooms = "nzooms";
	/**
	 * Nivel de zoom actual en el mapa del dispositivo.
	 */
	public static final String zoom = "zoom";
	/**
	 * Latitud de la esquina superior izquierda del mapa visualizado en el dispositivo.
	 */
	public static final String x1 = "x1";
	/**
	 * Longitud de la esquina superior izquierda del mapa visualizado en el dispostivo.
	 */
	public static final String y1 = "y1";
	/**
	 * Latitud de la esquina inferior derecha del mapa visualizado en el dispositivo.
	 */
	public static final String x2 = "x2";
	/**
	 * Longitud de la esquina inferior derecha del mapa visualizado en el dispositivo.
	 */
	public static final String y2 = "y2";
	/**
	 * Nombre del local.
	 */
	public static final String nombrelocal = "nombrelocal";
	/**
	 * Tipo de local.
	 */
	public static final String tipolocal = "tipolocal";
	/**
	 * Calle.
	 */
	public static final String calle = "calle";
	/**
	 * Portal.
	 */
	public static final String portal = "portal";
	/**
	 * Ciudad.
	 */
	public static final String ciudad = "ciudad";
	/**
	 * Código postal.
	 */
	public static final String codigopostal = "codigopostal";
	/**
	 * Provincia.
	 */
	public static final String provincia = "provincia";
	/**
	 * Lista de direcciones MAC.
	 */
	public static final String macaddrs = "macaddrs";
	/**
	 * Vector de inicialización AES-128.
	 */
	public static final String iv = "iv";
	/**
	 * Tipo de filtro aplicado a los locales a visualizar.
	 */
	public static final String filter = "filter";
	/**
	 * Si el local es favorito o no para el usuario.
	 */
	public static final String isfav = "isfav";
	/**
	 * Identificador del evento.
	 */
	public static final String eventid = "eventid";
	/**
	 * Identificador de la promoción.
	 */
	public static final String promoid = "promoid";
	/**
	 * Indica si la posición es visible para el resto de usuarios
	 */
	public static final String visible = "visible";
	/**
	 * Identificador de visita.
	 */
	public static final String checkinid = "checkinid";
	/**
	 * Activo o inactivo.
	 */
	public static final String active = "active";
	/**
	 * Sello temporal de la notificación
	 */
	public static final String tsnotification = "tsnotification";
	/**
	 * Identificador de la notificación
	 */
	public static final String notificationid = "notificationid";
	/**
	 * Descripción.
	 */
	public static final String descripcion = "descripcion";
	/**
	 * Nombre de usuario de propietario
	 */
	public static final String propietario = "propietario";
	/**
	 * Página web
	 */
	public static final String web = "web";
	/**
	 * Icono de local.
	 */
	public static final String icono = "icono";
	/**
	 * Identificador de superetiqueta
	 */
	public static final String supertagid = "supertagid";
	/**
	 * Nombre de etiqueta
	 */
	public static final String tagname = "tagname";
	/**
	 * Identificador de etiqueta.
	 */
	public static final String tagid = "tagid";
	/**
	 * Hora de inicio de mañana.
	 */
	public static final String initmorningtime = "initmorningtime";
	/**
	 * Hora de fin de mañana.
	 */
	public static final String endmorningtime = "endmorningtime";
	/**
	 * Hora de inicio de tarde.
	 */
	public static final String initafternoottime = "initafternoontime";
	/**
	 * Hora de fin de tarde.
	 */
	public static final String endafternoontime = "endafternoontime";
	/**
	 * Hora de inicio de noche.
	 */
	public static final String initnighttime = "initnighttime";
	/**
	 * Hora de fin de noche.
	 */
	public static final String endnighttime = "endnighttime";
	/**
	 * Días de la semana en los que una etiqueta es válida.
	 */
	public static final String days = "days";
	/**
	 * Identificador GCM.
	 */
	public static final String gcmid = "gcmid";
	/**
	 * Nombre de la versión de la aplicación del usuario
	 */
	public static final String appversion = "appversion";
	/**
	 * Código de versión de la aplicación del usuario
	 */
	public static final String appversioncode = "appversioncode";
	/**
	 * Instante de última sincronización.
	 */
	public static final String lastSyncTs = "lastsyncts";
	/**
	 * Direcciones de correo electrónico.
	 */
	public static final String emails = "emails";
	/**
	 * Números de teléfono.
	 */
	public static final String phones = "phones";
	/**
	 * Sello temporal de la visita
	 */
	public static final String tsvisit = "tsvisit";
}
