package com.usefeeling.android.cabinstill.helpers;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Clase ayudante para operaciones con fechas y horas.
 * @author Moisés Vilar
 *
 */
public abstract class DateTimeHelper {

	private static GregorianCalendar calendar = new GregorianCalendar();
	
	/**
	 * El año actual.
	 * @return
	 */
	public static int getYear() {
		return calendar.get(Calendar.YEAR);
	}
	
	/**
	 * El mes actual.
	 * @return
	 */
	public static int getMonth() {
		return calendar.get(Calendar.MONTH);
	}
	
	/**
	 * El día del mes actual.
	 * @return
	 */
	public static int getDay() {
		return calendar.get(Calendar.DATE);
	}
	
	/**
	 * Obtiene el número de milisegundos transcurridos desde 01/01/1970 hasta la fecha especificada.
	 * @param year Año.
	 * @param month Mes.
	 * @param day Día.
	 * @return
	 */
	public static long getTime(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day);
		return calendar.getTime().getTime();
	}

	/**
	 * Obtiene una fecha en formato Calendar a partir de un objeto Date.
	 * @param parse
	 * @return
	 */
	public static Calendar toCalendar(Date parse) {
		Calendar result = Calendar.getInstance();
		result.setTime(parse);
		return result;
	}
	
	/**
	 * Transforma el instante especificado en una cadena de texto que informa del tiempo
	 * transcurrido hasta el momento actual. 
	 * @param time Instante.
	 * @return Tiempo transcurrido hasta el momento actual.
	 */
	public static String desde(long time) {
		int total = (int) ( (System.currentTimeMillis() / 1000) - (time / 1000) );
		//Menos de 10 minutos: "Ahora mismo"
		if (total < 10 * 60) {
			return "Ahora mismo";
		}
		//Menos que una hora: en minutos
		else if (total < 3600) {
			return "hace " + (total / 60) + ((total / 60) == 1 ? " minuto" : " minutos");
		}
		//Menos que un día: en horas
		else if (total < 84600) {
			return "hace " + (total / 3600) + ((total / 3600) == 1 ? " hora" : " horas");
		}
		//Menos de una semana: en días
		else if (total < 592200) {
			return "hace " + (total / 84600) + ((total / 84600) == 1 ? " día" : " días");
		}
		//Más de una semana: la fecha completa
		else {
			return "el " + DateTimeHelper.toString(time);
		}
	}
	
	/**
	 * Transforma el instante especificado en una cadena de texto.
	 * @param time Tiempo expresado en número de milisegundos transcurridos desde 1/1/1970 a las 00:00
	 * @return La fecha y hora en una cadena de texto con formato DD/MM/AAAA HH:MM
	 */
	public static String toString(long time) {
		calendar.setTime(new java.util.Date(time));
		int iDay = calendar.get(Calendar.DATE);
		int iMonth = (calendar.get(Calendar.MONTH) + 1);
		int iYear = calendar.get(Calendar.YEAR);
		int iHour = calendar.get(Calendar.HOUR_OF_DAY);
		int iMinute = calendar.get(Calendar.MINUTE);
		String sDay = iDay < 10 ? "0" + iDay : String.valueOf(iDay);
		String sMonth = iMonth < 10 ? "0" + iMonth : String.valueOf(iMonth);
		String sYear = String.valueOf(iYear);
		String sHour = iHour < 10 ? "0" + iHour : String.valueOf(iHour);
		String sMinute = iMinute < 10 ? "0" + iMinute : String.valueOf(iMinute);
		return sDay + "/" + sMonth + "/" + sYear + " " + sHour + ":" + sMinute;
	}
	
	/**
	 * Comprueba entre dos sellos temporales, uno de un registro de visita y otro de una posición GPS suelta,
	 * si el registro de visita se ha realizado 10 minutos antes, como máximo, que el sello temporal de la posición GPS.
	 * @param tscheckin Instante en el que se ha realizado el registro de visita.
	 * @param tsgps Sello temporal de la posición GPS.
	 * @return <b>true</b> si el registro de visita se ha realizado 5 minutos antes, como mucho, que la posición GPS.
	 */
	public static boolean isCheckin(final Long tscheckin, final Long tsgps) {
		boolean result = false;
		if (tscheckin == null) return false;
		else if (tsgps == null) return true;
		Calendar calendarCheckin = Calendar.getInstance();
		calendarCheckin.setTime(new Date(tscheckin));
		Calendar calendarGps = Calendar.getInstance();
		calendarGps.setTime(new Date(tsgps));
		calendarCheckin.add(Calendar.MINUTE, 10);
		if (calendarCheckin.compareTo(calendarGps) >= 0) result = true;
		return result;
	}

	/**
	 * Compara la fecha actual con una fecha pasada la cantidad de tiempo especificada.
	 * @param time Fecha a comparar.
	 * @param amount Cantidad de tiempo.
	 * @param type Tipo de cantidad de tiempo: Calendar.HOUR, Calendar.MINUTE, Calendar.SECOND.
	 * @return -1 si el instante pasado como parámetro más la cantidad de tiempo especificada es anterior al momento actual.<br>
	 * 0 si el instante pasado como parámetro más la cantidad de tiempo especificada es igual al momento actual.<br>
	 * 1 si el instante pasado como parámetro más la cantidad de tiempo especificada es posterior al momento actual.
	 */
	public static int compare(long time, int amount, int type) {
		java.util.Date dateNow = new java.util.Date();
		calendar.setTime(new java.util.Date(time));
		calendar.add(type, amount);
		Calendar now = Calendar.getInstance();
		now.setTime(dateNow);
		return calendar.compareTo(now);
	}

	/**
	 * Builds a birthday string from a birthdate timestamp.
	 * @param birthdate The birhdate timestamp.
	 * @return The birthday string.
	 */
	public static String toBirthdayString(Long birthdate) {
		calendar.setTime(new java.util.Date(birthdate));
		DateTime date = new DateTime(calendar);
		DateTimeFormatter fmt = DateTimeFormat.forPattern("d MMMM");
		fmt = fmt.withLocale(Locale.getDefault());
		return date.toString(fmt);
	}
	
	/**
	 * Builds a string from current date using the specified pattern
	 * @param pattern The pattern.
	 * @return The date string.
	 */
	public static String nowToStringFromPattern(String pattern) {
		DateTime date = new DateTime(calendar);
		DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
		return date.toString(fmt);
	}

	/**
	 * Builds a string from specified date using a long format like d MMMM, HH:MM
	 * @param timestamp The date.
	 * @return The date string.
	 */
	public static String toLongString(Long timestamp) {
		calendar.setTime(new java.util.Date(timestamp));
		DateTime date = new DateTime(calendar);
		DateTimeFormatter fmt = DateTimeFormat.forPattern("d MMMM, HH:MM");
		fmt = fmt.withLocale(Locale.getDefault());
		return date.toString(fmt);
	}

	/**
	 * Builds a string from current date with specified offset using the specified pattern.
	 * @param pattern The pattern.
	 * @param offset The offset.
	 * @param calendarType The calendar type.
	 * @return The date string.
	 */
	public static String offsetToStringFromPattern(String pattern, int offset, int calendarType) {
		calendar.setTime(new java.util.Date());
		calendar.add(calendarType, offset);
		DateTime date = new DateTime(calendar);
		DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
		return date.toString(fmt);
	}

	/**
	 * Builds a string from the specified date using the specified pattern
	 * @param time The date.
	 * @param pattern The pattern-
	 * @return The date string.
	 */
	public static String toStringFromPattern(long time, String pattern) {
		calendar.setTime(new java.util.Date(time));
		DateTime date = new DateTime(calendar);
		DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
		return date.toString(fmt);
	}
	
	/**
	 * Retrieves  the milliseconds of the datetime string following the specified pattern from the Java epoch of 1970-01-01T00:00:00Z. 
	 * @param date The datetime string.
	 * @param pattern The pattern.
	 * @return The milliseconds.
	 */
	public static long getTimeFromString(String date, String pattern) {
		DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern(pattern);
		DateTime time = dateStringFormat.parseDateTime(date);
		return time.getMillis();
	}
	
}
