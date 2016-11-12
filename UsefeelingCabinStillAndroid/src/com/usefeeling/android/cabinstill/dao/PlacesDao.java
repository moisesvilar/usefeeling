package com.usefeeling.android.cabinstill.dao;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.util.GeoPoint;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.PointF;

import com.usefeeling.android.cabinstill.api.Place;
import com.usefeeling.android.cabinstill.api.UseFeeling;
import com.usefeeling.android.cabinstill.helpers.GeoHelper;

/**
 * Data Access Object para la tabla LOCALES de la caché.
 * @author Moisés Vilar.
 *
 */
public class PlacesDao extends AbstractDao {

	private static final String TABLE_NAME = "LOCALES";
	private static final String ID = "IDLOCAL";
	private static final String FAMA = "FAMA";
	private static final String AFINIDAD = "AFINIDAD";
	private static final String AFINIDADSTR = "AFINIDADSTR";
	private static final String ESFAVORITO = "ESFAVORITO";
	private static final String PARASALIR = "PARASALIR";
	private static final String PARABEBER = "PARABEBER";
	private static final String PARACOMER = "PARACOMER";
	private static final String TIENEPROMOS = "TIENEPROMOS";
	private static final String TIENEEVENTOS = "TIENEEVENTOS";
	private static final String FAVORITOS = "FAVORITOS";
	private static final String VISITAS = "VISITAS";
	private static final String LATITUD = "LATITUD";
	private static final String LONGITUD = "LONGITUD";
	private static final String ICONODEFECTO = "ICONODEFECTO";
	private static final String TIENEICONO = "TIENEICONO";
	private static final String TELEFONO = "TELEFONO";
	private static final String EMAIL = "EMAIL";
	private static final String WEB_FACEBOOK = "WEB_FACEBOOK";
	private static final String WEB_TWITTER = "TWITTER";
	private static final String WEB = "WEB";
	private static final String DIRECCION = "DIRECCION";
	private static final String CIUDAD = "CIUDAD";
	private static final String DESCRIPCION = "DESCRIPCION";
	private static final String TIPOLOCAL = "TIPOLOCAL";
	private static final String NOMBRE = "NOMBRE";
	private static final String HAYCHECKIN = "HAYCHECKIN";
	private static final String FIELDS[] = {ID, NOMBRE, TIPOLOCAL, DESCRIPCION, DIRECCION, WEB, WEB_FACEBOOK, WEB_TWITTER, EMAIL, TELEFONO, TIENEICONO, ICONODEFECTO, LATITUD, LONGITUD, VISITAS, FAVORITOS, TIENEEVENTOS, TIENEPROMOS, PARACOMER, PARABEBER, PARASALIR, ESFAVORITO, AFINIDAD, AFINIDADSTR, FAMA, HAYCHECKIN};
	
	/**
	 * Constructor.
	 * @param context Contexto desde el que se instancia el DAO.
	 */
	public PlacesDao(Context context) {
		super(context);
	}
	
	/**
	 * Establece los valores del local en el ContentValues.
	 * @param place
	 * @return
	 */
	private static ContentValues setContentValues(Place place) {
		ContentValues cv = new ContentValues();
		cv.put(ID, place.getPlaceId());
		cv.put(FAMA, place.getFame());
		cv.put(AFINIDAD, place.getAffinity());
		cv.put(AFINIDADSTR, place.getAffinityStr());
		cv.put(ESFAVORITO, place.isFavorite());
		cv.put(PARASALIR, place.isForPartying());
		cv.put(PARACOMER, place.isForEating());
		cv.put(PARABEBER, place.isForDrinking());
		cv.put(TIENEPROMOS, place.hasPromos());
		cv.put(TIENEEVENTOS, place.hasEvents());
		cv.put(FAVORITOS, place.getFavorites());
		cv.put(VISITAS, place.getVisits());
		cv.put(LONGITUD, place.getLongitude());
		cv.put(LATITUD, place.getLatitude());
		cv.put(ICONODEFECTO, place.getIcon());
		cv.put(TIENEICONO, place.hasIcon());
		cv.put(TELEFONO, place.getPhone());
		cv.put(WEB_FACEBOOK, place.getFacebook());
		cv.put(WEB_TWITTER, place.getTwitter());
		cv.put(WEB, place.getWeb());
		cv.put(EMAIL, place.getEmail());
		cv.put(DIRECCION, place.getAddress());
		cv.put(CIUDAD, place.getCity());
		cv.put(DESCRIPCION, place.getDescription());
		cv.put(TIPOLOCAL, place.getPlaceType());
		cv.put(NOMBRE, place.getName());
		cv.put(HAYCHECKIN, place.getHasCheckin());
		return cv;
	}
	
	/**
	 * Establece los datos del local según los datos del cursor.
	 * @param c
	 * @return
	 */
	private static Place setPlace(Cursor c) {
		Place place = new Place();
		place.setPlaceId(c.getLong(c.getColumnIndex(ID)));
		place.setName(c.getString(c.getColumnIndex(NOMBRE)));
		place.setPlaceType(c.getString(c.getColumnIndex(TIPOLOCAL)));
		place.setDescription(c.getString(c.getColumnIndex(DESCRIPCION)));
		place.setAddress(c.getString(c.getColumnIndex(DIRECCION)));
		place.setWeb(c.getString(c.getColumnIndex(WEB)));
		place.setFacebook(c.getString(c.getColumnIndex(WEB_FACEBOOK)));
		place.setTwitter(c.getString(c.getColumnIndex(WEB_TWITTER)));
		place.setEmail(c.getString(c.getColumnIndex(EMAIL)));
		place.setPhone(c.getString(c.getColumnIndex(TELEFONO)));
		place.setHasIcon(c.getInt(c.getColumnIndex(TIENEICONO)) == 1);
		place.setIcon(c.getString(c.getColumnIndex(ICONODEFECTO)));
		place.setLatitude(c.getDouble(c.getColumnIndex(LATITUD)));
		place.setLongitude(c.getDouble(c.getColumnIndex(LONGITUD)));
		place.setVisits(c.getInt(c.getColumnIndex(VISITAS)));
		place.setFavorites(c.getInt(c.getColumnIndex(FAVORITOS)));
		place.setHasEvents(c.getInt(c.getColumnIndex(TIENEEVENTOS)) ==1);
		place.setHasPromos(c.getInt(c.getColumnIndex(TIENEPROMOS)) ==1);
		place.setIsForDrinking(c.getInt(c.getColumnIndex(PARABEBER)) ==1);
		place.setIsForEating(c.getInt(c.getColumnIndex(PARACOMER)) ==1);
		place.setIsForPartying(c.getInt(c.getColumnIndex(PARASALIR)) ==1);
		place.setIsFavorite(c.getInt(c.getColumnIndex(ESFAVORITO)) ==1);
		place.setAffinity(c.getFloat(c.getColumnIndex(AFINIDAD)));
		place.setAffinityStr(c.getString(c.getColumnIndex(AFINIDADSTR)));
		place.setFame(c.getFloat(c.getColumnIndex(FAMA)));
		place.setHasCheckin(c.getInt(c.getColumnIndex(HAYCHECKIN)) ==1);
		return place;
	}
	
	/**
	 * Inserta un local en la caché.
	 * @param place Local a insertar.
	 * @return Si la operación ha terminado con éxito.
	 */
	public boolean insert(Place place) {
		try {
			if (!this.open()) return false;
			long code = -1;
			synchronized(this.db) {
				try {
					code = this.db.insert(TABLE_NAME, null, PlacesDao.setContentValues(place));
				} catch (Exception e) {
					code = -1;
				}
			}
			if (code == -1) {
				this.upgrade();
				return this.insert(place);
			}
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			this.close();
		}
	}
	
	/**
	 * Inserta varios locales en la caché.
	 * @param places Lista de los locales a insertar.
	 * @return Número de locales insertados.
	 */
	public int insert (List<Place> places) {
		try {
			if (!this.open()) return 0;
			int contador = 0;
			for (Place place : places) {
				if (this.insert(place)) contador++;
			}
			return contador;
		} finally {
			this.close();
		}
	}
	
	/**
	 * Modifica un local en la caché.
	 * @param place Local a modificar, identificado por su placeId.
	 * @return Si la operación ha concluído con éxito.
	 */
	public boolean update(Place place) {
		try {
			if (!this.open()) return false;
			String args[] = new String[]{place.getPlaceId().toString()};
			long code = -1;
			try {
				synchronized (this.db) {
					code = this.db.update(TABLE_NAME, PlacesDao.setContentValues(place), ID + "=?", args);
				}
			} catch (Exception e) {
				code = -1;
			}
			if (code == -1) {
				this.upgrade();
				return this.update(place);
			}
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			this.close();
		}
	}
	
	/**
	 * Elimina el local especificado por su identificador de la caché.
	 * @param id Identificador del local a eliminar.
	 * @return Si la operación ha concluído con éxito.
	 */
	public boolean delete(Long id) {
		try {
			if (!this.open()) return false;
			String args[] = new String[]{id.toString()};
			long code = -1;
			synchronized(this.db) {
				code = this.db.delete(TABLE_NAME, ID + "=?", args);
			}
			if (code == -1) {
				this.upgrade();
				return this.delete(id);
			}
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			this.close();
		}
	}
	
	/**
	 * Obtiene todos los locales de la caché.
	 * @return La lista de locales.
	 */
	public ArrayList<Place> get() {
		Cursor c = null;
		try {
			if (!this.open()) return null;
			ArrayList<Place> result = new ArrayList<Place>();
			c = this.db.query(TABLE_NAME, FIELDS, null, null, null, null, null, null);
			if (c.moveToFirst()) {
				do {
					result.add(PlacesDao.setPlace(c));
				} while (c.moveToNext());
			}
			return result;
		}
		catch (Exception e) {
			return null;
		} finally {
			if (c!= null && !c.isClosed()) c.close();
			this.close();
		}
	}
	
	private static final String ORDER_BY_AFFINITY = AFINIDAD + " ASC";
	/**
	 * Obtiene todos los locales de la caché ordenados por afinidad.
	 * @return La lista de locales.
	 */
	public ArrayList<Place> getOrderByAffinity() {
		Cursor c = null;
		try {
			if (!this.open()) return null;
			ArrayList<Place> result = new ArrayList<Place>();
			c = this.db.query(TABLE_NAME, FIELDS, null, null, null, null, ORDER_BY_AFFINITY, null);
			if (c.moveToFirst()) {
				do {
					result.add(PlacesDao.setPlace(c));
				} while (c.moveToNext());
			}
			return result;
		}
		catch (Exception e) {
			return null;
		} finally {
			if (c!= null && !c.isClosed()) c.close();
			this.close();
		}
	}
	
	private static final double MULT = 1.1;
	private static final double RADIUS = 2000.0; //meters
	private static final String SELECTION_TO_SELECT_NEARBY = 
			LATITUD + " > ? AND " + LATITUD + " < ? AND " +
			LONGITUD + " < ? AND " + LONGITUD + " > ?";
	/**
	 * Obtiene los locales cercanos a la posición dada. 
	 * @param lat
	 * @param lon
	 * @return
	 */
	public List<Place> get(float lat, float lon) {
		Cursor c = null;
		try {
			if (!this.open()) return null;
			PointF center = new PointF(lat, lon);
			PointF p1 = GeoHelper.calculateDerivedPosition(center, MULT * RADIUS, 0);
			PointF p2 = GeoHelper.calculateDerivedPosition(center, MULT * RADIUS, 90);
			PointF p3 = GeoHelper.calculateDerivedPosition(center, MULT * RADIUS, 180);
			PointF p4 = GeoHelper.calculateDerivedPosition(center, MULT * RADIUS, 270);
			String argsSelect[] = {String.valueOf(p3.x), String.valueOf(p1.x), String.valueOf(p2.y), String.valueOf(p4.y)};
			ArrayList<Place> result = new ArrayList<Place>();
			c = this.db.query(TABLE_NAME, FIELDS, SELECTION_TO_SELECT_NEARBY, argsSelect, null, null, null, null);
			if (c.moveToFirst()) {
				do {
					result.add(PlacesDao.setPlace(c));
				} while (c.moveToNext());
			}
			return GeoHelper.sortPlacesByDistance(result, new GeoPoint(lat, lon), result.size());
		} catch (Exception e) {
			return null;
		} finally {
			if (c!= null && !c.isClosed()) c.close();
			this.close();
		}
	}
	
	/**
	 * Obtiene los locales cercanos a la posición y radio dados. 
	 * @param lat
	 * @param lon
	 * @param radius
	 * @return
	 */
	public List<Place> get(float lat, float lon, float radius) {
		Cursor c = null;
		try {
			if (!this.open()) return null;
			PointF center = new PointF(lat, lon);
			PointF p1 = GeoHelper.calculateDerivedPosition(center, MULT * radius, 0);
			PointF p2 = GeoHelper.calculateDerivedPosition(center, MULT * radius, 90);
			PointF p3 = GeoHelper.calculateDerivedPosition(center, MULT * radius, 180);
			PointF p4 = GeoHelper.calculateDerivedPosition(center, MULT * radius, 270);
			String argsSelect[] = {String.valueOf(p3.x), String.valueOf(p1.x), String.valueOf(p2.y), String.valueOf(p4.y)};
			ArrayList<Place> result = new ArrayList<Place>();
			c = this.db.query(TABLE_NAME, FIELDS, SELECTION_TO_SELECT_NEARBY, argsSelect, null, null, null, null);
			if (c.moveToFirst()) {
				do {
					result.add(PlacesDao.setPlace(c));
				} while (c.moveToNext());
			}
			return GeoHelper.sortPlacesByDistance(result, new GeoPoint(lat, lon), result.size());
		} catch (Exception e) {
			return null;
		} finally {
			if (c!= null && !c.isClosed()) c.close();
			this.close();
		}
	}
	
	private static final String SELECTION_TO_SELECT_BY_CITY = 
			CIUDAD + "=?";
	/**
	 * Obtiene los locales en la ciudad especificada. 
	 * @param city
	 * @return
	 */
	public ArrayList<Place> getByCity(String city) {
		Cursor c = null;
		try {
			if (!this.open()) return null;
			String argsSelect[] = {city};
			ArrayList<Place> result = new ArrayList<Place>();
			c = this.db.query(TABLE_NAME, FIELDS, SELECTION_TO_SELECT_BY_CITY, argsSelect, null, null, null, null);
			if (c.moveToFirst()) {
				do {
					result.add(PlacesDao.setPlace(c));
				} while (c.moveToNext());
			}
			return result;
		}
		catch (Exception e) {
			return null;
		} finally {
			if (c!= null && !c.isClosed()) c.close();
			this.close();
		}
	}
	
	/**
	 * Obtiene los locales en la ciudad especificada ordenados por afinidad. 
	 * @param city
	 * @return
	 */
	public ArrayList<Place> getByCityOrderByAffinity(String city) {
		Cursor c = null;
		try {
			if (!this.open()) return null;
			String argsSelect[] = {city};
			ArrayList<Place> result = new ArrayList<Place>();
			c = this.db.query(TABLE_NAME, FIELDS, SELECTION_TO_SELECT_BY_CITY, argsSelect, null, null, ORDER_BY_AFFINITY, null);
			if (c.moveToFirst()) {
				do {
					result.add(PlacesDao.setPlace(c));
				} while (c.moveToNext());
			}
			return result;
		}
		catch (Exception e) {
			return null;
		} finally {
			if (c!= null && !c.isClosed()) c.close();
			this.close();
		}
	}

	/**
	 * Comprueba si el local especificado por su identificador ya existe en la caché.
	 * @param placeId El identificador del local a comprobar.
	 * @return Si el local ya existe.
	 */
	public Boolean exists(Long placeId) {
		Cursor c = null;
		try {
			if (!this.open()) return null;
			c = this.db.query(TABLE_NAME, new String[]{"1"}, ID + "=?", new String[]{placeId.toString()}, null, null, null);
			return c.getCount() > 0;
		} catch (Exception e) {
			return false;
		} finally {
			if (c!= null && !c.isClosed()) c.close();
			this.close();
		}
	}
	
	private static final String SELECTION_TO_SELECT_WITH_CHECKIN = 
			HAYCHECKIN + "=?";
	/**
	 * Obtiene todos los locales de la caché que poseen visitas de amigos.
	 * @return La lista de locales.
	 */
	public ArrayList<Place> getWithCheckin() {
		Cursor c = null;
		try {
			if (!this.open()) return null;
			String argsSelect[] = {"1"};
			ArrayList<Place> result = new ArrayList<Place>();
			c = this.db.query(TABLE_NAME, FIELDS, SELECTION_TO_SELECT_WITH_CHECKIN, argsSelect, null, null, null, null);
			if (c.moveToFirst()) {
				do {
					result.add(PlacesDao.setPlace(c));
				} while (c.moveToNext());
			}
			return result;
		}
		catch (Exception e) {
			return null;
		} finally {
			if (c!= null && !c.isClosed()) c.close();
			this.close();
		}
	}
	
	/**
	 * Obtiene todos los locales de la caché que poseen visitas de amigos.
	 * @return La lista de locales.
	 */
	public ArrayList<Place> getWithoutCheckin() {
		Cursor c = null;
		try {
			if (!this.open()) return null;
			String argsSelect[] = {"0"};
			ArrayList<Place> result = new ArrayList<Place>();
			c = this.db.query(TABLE_NAME, FIELDS, SELECTION_TO_SELECT_WITH_CHECKIN, argsSelect, null, null, null, null);
			if (c.moveToFirst()) {
				do {
					result.add(PlacesDao.setPlace(c));
				} while (c.moveToNext());
			}
			return result;
		}
		catch (Exception e) {
			return null;
		} finally {
			if (c!= null && !c.isClosed()) c.close();
			this.close();
		}
	}
	
	private static final String SELECTION_TO_SELECT_FAVORITES = 
			ESFAVORITO + "=?";
	/**
	 * Obtiene todos los locales de la caché favoritos del usuario.
	 * @return La lista de locales.
	 */
	public ArrayList<Place> getFavorites() {
		Cursor c = null;
		try {
			if (!this.open()) return null;
			String argsSelect[] = {"1"};
			ArrayList<Place> result = new ArrayList<Place>();
			c = this.db.query(TABLE_NAME, FIELDS, SELECTION_TO_SELECT_FAVORITES, argsSelect, null, null, null, null);
			if (c.moveToFirst()) {
				do {
					result.add(PlacesDao.setPlace(c));
				} while (c.moveToNext());
			}
			return result;
		}
		catch (Exception e) {
			return null;
		} finally {
			if (c!= null && !c.isClosed()) c.close();
			this.close();
		}
	}
	
	private static final String SELECTION_TO_SELECT_FAVORITES_FILTERED = 
			ESFAVORITO + "=? AND ( " +
			PARACOMER + "=? OR " +
			PARABEBER + "=? OR " +
			PARASALIR + "=?)";
	/**
	 * Obtiene todos los locales de la caché favoritos del usuario según el filtrado especificado.
	 * @param forEating Filtrado de locales para comer.
	 * @param forDrinking Filtrado de locales para beber.
	 * @param forPartying Filtrado de locales para salir.
	 * @return
	 */
	public ArrayList<Place> getFavorites(Boolean forEating, Boolean forDrinking, Boolean forPartying) {
		Cursor c = null;
		try {
			if (!this.open()) return null;
			String argsSelect[] = {"1", forEating ? "1" : "0", forDrinking ? "1" : "0", forPartying ? "1" : "0"};
			ArrayList<Place> result = new ArrayList<Place>();
			c = this.db.query(TABLE_NAME, FIELDS, SELECTION_TO_SELECT_FAVORITES_FILTERED, argsSelect, null, null, null, null);
			if (c.moveToFirst()) {
				do {
					result.add(PlacesDao.setPlace(c));
				} while (c.moveToNext());
			}
			return result;
		}
		catch (Exception e) {
			return null;
		} finally {
			if (c!= null && !c.isClosed()) c.close();
			this.close();
		}
	}
	
	private static final String SELECTION_TO_SELECT_AFFINED_FILTERED = 
			AFINIDAD + ">=? OR (" +
			PARACOMER + "=? OR " +
			PARABEBER + "=? OR " +
			PARASALIR + "=?)";
	/**
	 * Obtiene todos los locales de la caché más afines al usuario según el filtrado especificado.
	 * @param forEating Filtrado de locales para comer.
	 * @param forDrinking Filtrado de locales para beber.
	 * @param forPartying Filtrado de locales para salir.
	 * @return
	 */
	public ArrayList<Place> getAffined(Boolean forEating, Boolean forDrinking, Boolean forPartying) {
		Cursor c = null;
		try {
			if (!this.open()) return null;
			String argsSelect[] = {UseFeeling.THRESHOLD_GREEN.toString(), forEating ? "1" : "0", forDrinking ? "1" : "0", forPartying ? "1" : "0"};
			ArrayList<Place> result = new ArrayList<Place>();
			c = this.db.query(TABLE_NAME, FIELDS, SELECTION_TO_SELECT_AFFINED_FILTERED, argsSelect, null, null, null, null);
			if (c.moveToFirst()) {
				do {
					result.add(PlacesDao.setPlace(c));
				} while (c.moveToNext());
			}
			return result;
		}
		catch (Exception e) {
			return null;
		} finally {
			if (c!= null && !c.isClosed()) c.close();
			this.close();
		}
	}
	
	private static final String SELECTION_TO_SELECT_AFFINED = 
			AFINIDAD + ">=?";
	/**
	 * Obtiene todos los locales de la caché más afines al usuario.
	 * @return La lista de locales.
	 */
	public ArrayList<Place> getAffined() {
		Cursor c = null;
		try {
			if (!this.open()) return null;
			String argsSelect[] = {UseFeeling.THRESHOLD_GREEN.toString()};
			ArrayList<Place> result = new ArrayList<Place>();
			c = this.db.query(TABLE_NAME, FIELDS, SELECTION_TO_SELECT_AFFINED, argsSelect, null, null, null, null);
			if (c.moveToFirst()) {
				do {
					result.add(PlacesDao.setPlace(c));
				} while (c.moveToNext());
			}
			return result;
		}
		catch (Exception e) {
			return null;
		} finally {
			if (c!= null && !c.isClosed()) c.close();
			this.close();
		}
	}
	
	private static final String SELECTION_TO_SELECT_WITH_ACTIVITIES = 
			TIENEEVENTOS + "=? OR " +
			TIENEPROMOS + "=?";
	/**
	 * Obtiene todos los locales de la caché que posean alguna actividad (evento o promo).
	 * @return La lista de locales.
	 */
	public ArrayList<Place> getWithActivities() {
		Cursor c = null;
		try {
			if (!this.open()) return null;
			String argsSelect[] = {"1", "1"};
			ArrayList<Place> result = new ArrayList<Place>();
			c = this.db.query(TABLE_NAME, FIELDS, SELECTION_TO_SELECT_WITH_ACTIVITIES, argsSelect, null, null, null, null);
			if (c.moveToFirst()) {
				do {
					result.add(PlacesDao.setPlace(c));
				} while (c.moveToNext());
			}
			return result;
		}
		catch (Exception e) {
			return null;
		} finally {
			if (c!= null && !c.isClosed()) c.close();
			this.close();
		}
	}
	
	private static final String SELECTION_TO_SELECT_WITH_ACTIVITIES_FILTERED = "(" + 
			TIENEEVENTOS + "=? OR " +
			TIENEPROMOS + "=?) AND (" +
			PARACOMER + "=? OR " +
			PARABEBER + "=? OR " +
			PARASALIR + "=?)";
	/**
	 * Obtiene todos los locales de la caché que posean alguna actividad (evento o promo).
	 * @param forEating Filtrado de locales para comer.
	 * @param forDrinking Filtrado de locales para beber.
	 * @param forPartying Filtrado de locales para salir.
	 * @return
	 */
	public ArrayList<Place> getWithActivities(Boolean forEating, Boolean forDrinking, Boolean forPartying) {
		Cursor c = null;
		try {
			if (!this.open()) return null;
			String argsSelect[] = {"1", "1", forEating ? "1" : "0", forDrinking ? "1" : "0", forPartying ? "1" : "0"};
			ArrayList<Place> result = new ArrayList<Place>();
			c = this.db.query(TABLE_NAME, FIELDS, SELECTION_TO_SELECT_WITH_ACTIVITIES_FILTERED, argsSelect, null, null, null, null);
			if (c.moveToFirst()) {
				do {
					result.add(PlacesDao.setPlace(c));
				} while (c.moveToNext());
			}
			return result;
		}
		catch (Exception e) {
			return null;
		} finally {
			if (c!= null && !c.isClosed()) c.close();
			this.close();
		}
	}
	
	private static final String SELECTION_TO_SELECT_CLUSTER =
			HAYCHECKIN + "=? AND " +
			ESFAVORITO + "=? AND " +
			AFINIDAD + "<? AND (" +
			TIENEEVENTOS + "=? OR " +
			TIENEPROMOS + "=?)";
	/**
	 * Obtiene todos los locales de la caché para ser representados en cluster sobre el mapa.
	 * @return La lista de locales.
	 */
	public ArrayList<Place> getCluster() {
		Cursor c = null;
		try {
			if (!this.open()) return null;
			String argsSelect[] = {"0", "0", UseFeeling.THRESHOLD_GREEN.toString(), "0", "0"};
			ArrayList<Place> result = new ArrayList<Place>();
			c = this.db.query(TABLE_NAME, FIELDS, SELECTION_TO_SELECT_CLUSTER, argsSelect, null, null, null, null);
			if (c.moveToFirst()) {
				do {
					result.add(PlacesDao.setPlace(c));
				} while (c.moveToNext());
			}
			return result;
		}
		catch (Exception e) {
			return null;
		} finally {
			if (c!= null && !c.isClosed()) c.close();
			this.close();
		}
	}
		
	private static final String SELECTION_TO_SELECT_CLUSTER_FILTERED =
			HAYCHECKIN + "=? AND " +
			ESFAVORITO + "=? AND " +
			AFINIDAD + "<? AND (" +
			TIENEEVENTOS + "=? OR " +
			TIENEPROMOS + "=?) AND (" +
			PARACOMER + "=? OR " +
			PARABEBER + "=? OR " +
			PARASALIR + "=?)";
	
	/**
	 * Obtiene todos los locales de la caché para ser representados en cluster sobre el mapa según el filtrado especificado.
	 * @param forEating Filtrado de locales para comer.
	 * @param forDrinking Filtrado de locales para beber.
	 * @param forPartying Filtrado de locales para salir.
	 * @return Los locales.
	 */
	public ArrayList<Place> getCluster(Boolean forEating, Boolean forDrinking, Boolean forPartying) {
		Cursor c = null;
		try {
			if (!this.open()) return null;
			String argsSelect[] = {"0", "0", UseFeeling.THRESHOLD_GREEN.toString(), "0", "0", forEating ? "1" : "0", forDrinking ? "1" : "0", forPartying ? "1" : "0"};
			ArrayList<Place> result = new ArrayList<Place>();
			c = this.db.query(TABLE_NAME, FIELDS, SELECTION_TO_SELECT_CLUSTER_FILTERED, argsSelect, null, null, null, null);
			if (c.moveToFirst()) {
				do {
					result.add(PlacesDao.setPlace(c));
				} while (c.moveToNext());
			}
			return result;
		}
		catch (Exception e) {
			return null;
		} finally {
			if (c!= null && !c.isClosed()) c.close();
			this.close();
		}
	}
	
	
	private static final String SELECTION_TO_SELECT_FAMOUS = FAMA + " > 0";
	/**
	 * Obtiene todos los locales de la caché ordenados por su fama.
	 * @return La lista de locales.
	 */
	public ArrayList<Place> getFamous() {
		Cursor c = null;
		try {
			if (!this.open()) return null;
			ArrayList<Place> result = new ArrayList<Place>();
			c = this.db.query(TABLE_NAME, FIELDS, SELECTION_TO_SELECT_FAMOUS, null, null, null, ORDER_BY_AFFINITY + ", " + FAMA + " DESC ", null);
			if (c.moveToFirst()) {
				do {
					result.add(PlacesDao.setPlace(c));
				} while (c.moveToNext());
			}
			return result;
		}
		catch (Exception e) {
			return null;
		} finally {
			if (c!= null && !c.isClosed()) c.close();
			this.close();
		}
	}

	/**
	 * Marca un local como favorito.
	 * @param placeId Identificador del local.
	 */
	public boolean markPlaceAsFav(Long placeId) {
		try {
			String args[] = new String[]{placeId.toString()};
			if (!this.open()) return false;
			ContentValues cv = new ContentValues();
			cv.put(ESFAVORITO, "1");
			long code = -1;
			synchronized(this.db) {
				code = this.db.update(TABLE_NAME, cv, ID + "=?", args);
			}
			if (code == -1) return false;
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			this.close();
		}
	}

	/**
	 * Desmarca un local como favorito.
	 * @param placeId Identificador del local.
	 */
	public boolean unmarkPlaceAsFav(Long placeId) {
		String args[] = new String[]{placeId.toString()};
		if (!this.open()) return false;
		ContentValues cv = new ContentValues();
		cv.put(ESFAVORITO, "0");
		long code = -1;
		synchronized(this.db) {
			code = this.db.update(TABLE_NAME, cv, ID + "=?", args);
		}
		if (code == -1) return false;
		return true;
	}

	private static final String SELECTION_TO_SELECT_FROM_ID = 
			ID + "=?";
	/**
	 * Obtiene la información de un local por su identificador.
	 * @param placeId Identificador del local.
	 * @return Local requerido o null si no existe.
	 */
	public Place get(Long placeId) {
		Cursor c = null;
		String args[] = new String[]{placeId.toString()};
		try {
			if (!this.open()) return null;
			c = this.db.query(TABLE_NAME, FIELDS, SELECTION_TO_SELECT_FROM_ID, args, null, null, null, null);
			Place place = null;
			if (c.moveToFirst()) {
				place = PlacesDao.setPlace(c);
			}
			return place;
		}
		catch (Exception e) {
			return null;
		} finally {
			if (c!= null && !c.isClosed()) c.close();
			this.close();
		}
	}

	/**
	 * Delete all data from tables
	 */
	public boolean delete() {
		try {
			if (!this.open()) return false;
			long code = -1;
			synchronized(this.db) {
				code = this.db.delete(TABLE_NAME, null, null);
			}
			if (code == -1) {
				this.upgrade();
				return this.delete();
			}
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			this.close();
		}
	}
	
	
}
