package com.usefeeling.android.cabinstill.helpers;

/**
 * Ayudante para operaciones con la API de Google Static Maps.
 * @author Moisés Vilar.
 *
 */
public abstract class GoogleStaticMapsHelper {

	private final static String base_url = "http://maps.googleapis.com/maps/api/staticmap?";
	private final static String center = "center";
	private final static String zoom = "zoom=14";
	private final static String size = "size=200x200";
	private final static String markers = "markers=color:0x9DBB59%7C";
	private final static String sensor = "&sensor=false";
	
	/**
	 * Construye la URL para la obtención del mapa especificado.
	 * @param lat Latitud del centro del mapa.
	 * @param lon Longitud del centro del mapa.
	 * @param withMarker <b>true</b> si se desea un marcador en el centro del mapa.
	 * @return La URL construída.
	 */
	public static String buildUrl(Double lat, Double lon, Boolean withMarker) {
		if (lat == null || lon == null) return "";
		String result = base_url;
		result += center + "=" + lat + "," + lon;
		result += "&" + zoom;
		result += "&" + size;
		if (withMarker) result += "&" + markers + lat +"," + lon;
		result += sensor;
		return result;
	}
	
	/**
	 * Construye la URL para la obtención del mapa especificado.
	 * @param lat Latitud del centro del mapa.
	 * @param lon Longitud del centro del mapa.
	 * @param withMarker <b>true</b> si se desea un marcador en el centro del mapa.
	 * @param w Ancho del mapa en pixeles.
	 * @param h Alto del mapa en pixeles.
	 * @return La URL construída.
	 */
	public static String buildUrl(Double lat, Double lon, Boolean withMarker, int w, int h) {
		if (lat == null || lon == null) return "";
		String result = base_url;
		result += center + "=" + lat + "," + lon;
		result += "&" + zoom;
		result += "&" + "size=" + w + "x" + h;
		if (withMarker) result += "&" + markers + lat +"," + lon;
		result += sensor;
		return result;
	}
	
}
