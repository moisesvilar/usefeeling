package com.usefeeling.android.cabinstill.dao;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.usefeeling.android.cabinstill.api.Activity;
import com.usefeeling.android.cabinstill.api.Place;
import com.usefeeling.android.cabinstill.api.Proposal;
import com.usefeeling.android.cabinstill.helpers.DateTimeHelper;

public class ActivitiesDao extends AbstractDao {

	private static final String TABLE_NAME = "ACTIVIDADES";
	private static final String ID = "IDACTIVIDAD";
	private static final String IDLOCAL = "IDLOCAL";
	private static final String NOMBRE = "NOMBRE";
	private static final String DESCRIPCION = "DESCRIPCION";
	private static final String INICIO = "INICIO";
	private static final String FIN = "FIN";
	private static final String IMAGENURL = "IMAGENURL";
	private static final String AFINIDAD = "AFINIDAD";
	private static final String AFINIDADSTR = "AFINIDADSTR";
	private static final String FAMA = "FAMA";
	private static final String TIPO = "TIPO";
	private static final String FIELDS[] = {ID, IDLOCAL, NOMBRE, DESCRIPCION, INICIO, FIN, IMAGENURL, AFINIDAD, AFINIDADSTR, FAMA, TIPO};
	private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	private static final String ORDER_BY_AFFINITY = AFINIDAD + " DESC";
	
	/**
	 * Constructor.
	 * @param context
	 */
	public ActivitiesDao(Context context) {
		super(context);
	}
	
	/**
	 * Establece los valores de la actividad en el ContentValues.
	 * @param activity
	 * @return
	 */
	private static ContentValues setContentValues(Activity activity) {
		ContentValues cv = new ContentValues();
		cv.put(ID, activity.getId());
		cv.put(IDLOCAL, activity.getPlaceid());
		cv.put(DESCRIPCION, activity.getDescription());
		cv.put(NOMBRE, activity.getName());
		cv.put(INICIO, (DateTimeHelper.toStringFromPattern(activity.getStart(), DATETIME_PATTERN)));
		cv.put(FIN, (DateTimeHelper.toStringFromPattern(activity.getEnd(), DATETIME_PATTERN)));
		cv.put(IMAGENURL, activity.getImageUrl());
		cv.put(AFINIDAD, activity.getAffinity());
		cv.put(AFINIDADSTR, activity.getAffinityStr());
		cv.put(FAMA, activity.getFame());
		cv.put(TIPO, activity.getType());		
		return cv;
	}
	
	/**
	 * Establece los datos de la actividad según los datos del cursor.
	 * @param c
	 * @return
	 */
	private static Activity setActivity(Cursor c) {
		Activity activity = new Activity();
		activity.setId(c.getLong(c.getColumnIndex(ID)));
		activity.setPlaceid(c.getLong(c.getColumnIndex(IDLOCAL)));
		activity.setName(c.getString(c.getColumnIndex(NOMBRE)));
		activity.setDescription(c.getString(c.getColumnIndex(DESCRIPCION)));
		activity.setStart(DateTimeHelper.getTimeFromString(c.getString(c.getColumnIndex(INICIO)), DATETIME_PATTERN));
		activity.setEnd(DateTimeHelper.getTimeFromString(c.getString(c.getColumnIndex(FIN)), DATETIME_PATTERN));
		activity.setImageUrl(c.getString(c.getColumnIndex(IMAGENURL)));
		activity.setAffinity(c.getFloat(c.getColumnIndex(AFINIDAD)));
		activity.setAffinityStr(c.getString(c.getColumnIndex(AFINIDADSTR)));
		activity.setFame(c.getFloat(c.getColumnIndex(FAMA)));
		activity.setType(c.getShort(c.getColumnIndex(TIPO)));
		return activity;
	}

	/**
	 * Comprueba si una actividad ya existe en la caché.
	 * @param id Identificador de la actividad.
	 * @param type Tipo de actividad.
	 * @return Si la actividad ya existe.
	 */
	public Boolean exists(Long id, Short type) {
		Cursor c = null;
		try {
			if (!this.open()) return null;
			c = this.db.query(TABLE_NAME, new String[]{"1"}, ID + "=? AND " + TIPO + "=?", new String[]{id.toString(), type.toString()}, null, null, null);
			return c.getCount() > 0;
		} catch (Exception e) {
			return false;
		} finally {
			if (c!= null && !c.isClosed()) c.close();
			this.close();
		}
	}

	/**
	 * Actualiza los datos de una actividad en la caché.
	 * @param activity Datos de la actividad.
	 * @return El resultado de la operación.
	 */
	public boolean update(Activity activity) {
		try {
			if (!this.open()) return false;
			String args[] = new String[]{activity.getId().toString(), activity.getType().toString()};
			long code = -1;
			try {
				synchronized (this.db) {
					code = this.db.update(TABLE_NAME, ActivitiesDao.setContentValues(activity), ID + "=? AND " + TIPO + "=?", args);
				}
			} catch (Exception e) {
				code = -1;
			}
			if (code == -1) {
				this.upgrade();
				return this.update(activity);
			}
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			this.close();
		}
	}

	/**
	 * Inserta una nueva actividad en la caché.
	 * @param activity Datos de la actividad.
	 * @return El resultado de la operación.
	 */
	public boolean insert(Activity activity) {
		try {
			if (!this.open()) return false;
			long code = -1;
			synchronized(this.db) {
				try {
					code = this.db.insert(TABLE_NAME, null, ActivitiesDao.setContentValues(activity));
				} catch (Exception e) {
					code = -1;
				}
			}
			if (code == -1) {
				this.upgrade();
				return this.insert(activity);
			}
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			this.close();
		}
	}

	/**
	 * Elimina las actividades no vigentes.
	 * @return The result's operation.
	 */
	public boolean deleteOld() {
		try {
			if (!this.open()) return false;
			String args[] = new String[]{DateTimeHelper.nowToStringFromPattern(DATETIME_PATTERN)};
			long code = -1;
			synchronized(this.db) {
				code = this.db.delete(TABLE_NAME, FIN + "<DATETIME(?)", args);
			}
			if (code == -1) {
				this.upgrade();
				return this.deleteOld();
			}
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			this.close();
		}
	}
	
	private static final String SELECTION_NOW = INICIO + " < ?";
	/**
	 * Obtiene las actividades vigentes ahora mismo.
	 * @param limit El límite de actividades a seleccionar.
	 * @return
	 */
	public ArrayList<Proposal> getNow(Integer limit) {
		Cursor c = null;
		PlacesDao placesDao = new PlacesDao(this.context);
		try {
			if (!this.open()) return null;
			String argsSelect[] = {DateTimeHelper.nowToStringFromPattern(DATETIME_PATTERN)};
			ArrayList<Proposal> result = new ArrayList<Proposal>();
			c = this.db.query(TABLE_NAME, FIELDS, SELECTION_NOW, argsSelect, null, null, ORDER_BY_AFFINITY, limit.toString());
			if (c.moveToFirst()) {
				do {
					Activity activity = ActivitiesDao.setActivity(c);
					Place place = placesDao.get(activity.getPlaceid());
					result.add(new Proposal(activity, place));
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
	
	private static final String SELECTION_TODAY = INICIO + " BETWEEN ? AND ?";
	/**
	 * Obtiene las actividades vigentes durante el día de hoy.
	 * @param limit El límite de actividades a seleccionar.
	 * @return
	 */
	public ArrayList<Proposal> getToday(Integer limit) {
		Cursor c = null;
		PlacesDao placesDao = new PlacesDao(this.context);
		try {
			if (!this.open()) return null;
			String argsSelect[] = {DateTimeHelper.nowToStringFromPattern(DATETIME_PATTERN), DateTimeHelper.offsetToStringFromPattern(DATETIME_PATTERN, 24, Calendar.HOUR)};
			ArrayList<Proposal> result = new ArrayList<Proposal>();
			c = this.db.query(TABLE_NAME, FIELDS, SELECTION_TODAY, argsSelect, null, null, ORDER_BY_AFFINITY, limit.toString());
			if (c.moveToFirst()) {
				do {
					Activity activity = ActivitiesDao.setActivity(c);
					Place place = placesDao.get(activity.getPlaceid());
					result.add(new Proposal(activity, place));
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
	
	private static final String SELECTION_LATER = INICIO + " BETWEEN ? AND ?";
	/**
	 * Obtiene las actividades vigentes durante los próximos tres días.
	 * @param limit El límite de actividades a seleccionar.
	 * @return
	 */
	public ArrayList<Proposal> getLater(Integer limit) {
		Cursor c = null;
		PlacesDao placesDao = new PlacesDao(this.context);
		try {
			if (!this.open()) return null;
			String argsSelect[] = {DateTimeHelper.nowToStringFromPattern(DATETIME_PATTERN), DateTimeHelper.offsetToStringFromPattern(DATETIME_PATTERN, 3, Calendar.DAY_OF_YEAR)};
			ArrayList<Proposal> result = new ArrayList<Proposal>();
			c = this.db.query(TABLE_NAME, FIELDS, SELECTION_LATER, argsSelect, null, null, ORDER_BY_AFFINITY, limit.toString());
			if (c.moveToFirst()) {
				do {
					Activity activity = ActivitiesDao.setActivity(c);
					Place place = placesDao.get(activity.getPlaceid());
					result.add(new Proposal(activity, place));
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
