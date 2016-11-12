package com.usefeeling.android.cabinstill.helpers;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.osmdroid.util.GeoPoint;

import android.graphics.PointF;
import android.location.Location;

import com.usefeeling.android.cabinstill.api.Place;

/**
 * Clase ayudante para operaciones con puntos geográficos.
 * @author Moisés Vilar.
 *
 */
public class GeoHelper {
	
	/**
	 * Devuelve una cadena de texto que describe la distancia, en metros o kilómetros, que separa dos puntos geográficos.
	 * @param lat1 Latitud del primer punto.
	 * @param lon1 Longitud del primer punto.
	 * @param lat2 Latitud del segundo punto.
	 * @param lon2 Longitud del segundo punto.
	 * @return La distancia entre los dos puntos geográficos como cadena de texto.
	 */
	public static String distanceToString(double lat1, double lon1, double lat2, double lon2) {
		int distance = GeoHelper.distance(lat1, lon1, lat2, lon2);
		if (distance < 1000) return distance + " m.";
		else return Math.round((float)distance/1000.0) + " km";
	}
	
	/**
	* Calculates the end-point from a given source at a given range (meters)
	* and bearing (degrees). This methods uses simple geometry equations to
	* calculate the end-point.
	* @param point Point of origin
	* @param range Range in meters
	* @param bearing Bearing in degrees
	* @return End-point from the source given the desired range and bearing.
	*/
	public static PointF calculateDerivedPosition(PointF point, double range, double bearing) {
	    double EarthRadius = 6371000; //meters (aprox)
	    double latA = Math.toRadians(point.x);
	    double lonA = Math.toRadians(point.y);
	    double angularDistance = range / EarthRadius;
	    double trueCourse = Math.toRadians(bearing);
	    double lat = Math.asin(Math.sin(latA) * Math.cos(angularDistance) + Math.cos(latA) * Math.sin(angularDistance) * Math.cos(trueCourse));
	    double dlon = Math.atan2(Math.sin(trueCourse) * Math.sin(angularDistance) * Math.cos(latA), Math.cos(angularDistance) - Math.sin(latA) * Math.sin(lat));
	    double lon = ((lonA + dlon + Math.PI) % (Math.PI * 2)) - Math.PI;
	    lat = Math.toDegrees(lat);
	    lon = Math.toDegrees(lon);
	    PointF newPoint = new PointF((float) lat, (float) lon);
	    return newPoint;
    }
	
	/**
	 * Calcula la distancia entre dos puntos.
	 @param lat1 Latitud del primer punto.
	 * @param lon1 Longitud del primer punto.
	 * @param lat2 Latitud del segundo punto.
	 * @param lon2 Longitud del segundo punto.
	 * @return La distancia entre los dos puntos geográficos.
	 */
	public static int distance(double lat1, double lon1, double lat2, double lon2) {
		GeoPoint point1 = new GeoPoint(lat1, lon1);
		GeoPoint point2 = new GeoPoint(lat2, lon2);
		return point1.distanceTo(point2);
	}
	
	/**
	 * Esta interfaz define el contrato que debe cumplir la función o código para obtener la distancia
	 * de cada punto a ordenar
	 * 
	 * @author D. García
	 */
	public interface DistanceCalculator<T> {

		/**
		 * Calcula y devuelve la distancia desde un punto de referencia implicito en este calculator al
		 * punto pasado, representado por un objeto
		 * 
		 * @param point
		 *            El objeto desde el cual se debe obtener la distancia
		 * @return El valor de distancia para comparar con los otros puntos en el ordenamiento
		 */
		public Double calculateDistanceTo(T point);

	}
	
	/**
	 * Esta clase es una implementación de comparator que delega en un bloque de código para calcular la
	 * distancia a cada
	 * 
	 * @author D. García
	 */
	public static class DistanceComparator<T> implements Comparator<T> {

		private DistanceCalculator<T> distanceCalculator;

		private boolean useCacheForDistance = true;

		private Map<T, Double> localCache;

		public int compare(final T compared, final T reference) {
			final Double comparedDistance = getDistanceFrom(compared);
			final Double referenceDistance = getDistanceFrom(reference);
			return (int) (comparedDistance - referenceDistance);
		}

		/**
		 * Devuelve la distancia del punto pasado obtenida del cache o directamente desde el objeto
		 * 
		 * @param point
		 *            El objeto a evaluar por distancia
		 * @return La distancia obtenida
		 */
		private Double getDistanceFrom(final T point) {
			Double distance = null;
			if (useCacheForDistance) {
				distance = getCachedDistanceFor(point);
			}
			if (distance == null) {
				distance = this.distanceCalculator.calculateDistanceTo(point);
				if (distance == null) {
					throw new RuntimeException("El calculador de distancia devolvio null para: " + point);
				}
			}
			return distance;
		}

		/**
		 * Devuelve la distancia conservada en la caché local
		 * 
		 * @param point
		 *            El punto desde el que se obtendrá la distancia
		 * @return La distancia cacheada o null si no hay ninguna
		 */
		private Double getCachedDistanceFor(final T point) {
			return getLocalCache().get(point);
		};

		public Map<T, Double> getLocalCache() {
			if (localCache == null) {
				localCache = new HashMap<T, Double>();
			}
			return localCache;
		}

		public static <T> DistanceComparator<T> create(final DistanceCalculator<T> distanceCalculator,
				final boolean doCacheDistances) {
			final DistanceComparator<T> comparator = new DistanceComparator<T>();
			comparator.distanceCalculator = distanceCalculator;
			comparator.useCacheForDistance = doCacheDistances;
			return comparator;
		}
	}
	
	/**
	 * Ordena la lista de locales de entrada en función de la distancia que los separa con una posición de referencia.
	 * @param places Lista de locales.
	 * @param reference Posición de referencia.
	 * @param limit Cantidad límite de locales devuelta.
	 * @return La lista de locales ordenada.
	 */
	public static List<Place> sortPlacesByDistance(List<Place> places, GeoPoint reference, int limit) {
		final double referenceLatitude = reference.getLatitudeE6() / 1e6;
        final double referenceLongitude = reference.getLongitudeE6() / 1e6;
        DistanceCalculator<Place> distanceCalculator = new DistanceCalculator<Place>() {
            public Double calculateDistanceTo(Place point) {
                   double pointLatitude = point.getLatitude();
                   double pointLongitude = point.getLongitude();
                   float[] results = new float[1];
                   Location.distanceBetween(referenceLatitude, referenceLongitude, pointLatitude, pointLongitude, results);
                   return (double) results[0];

            }
        };
        Comparator<? super Place> distanceComparator = DistanceComparator.create(distanceCalculator, true);
        List<Place> filtered = GeoHelper.filterFirstMin(limit, places, distanceComparator);
        return filtered;
	}
	
	/**
	 * Tipos de filtrado.
	 * @author D. García
	 *
	 */
	private static enum FilterType {
		/**
		 * Se queda con los máximos de la comparación
		 */
		MAX {
			@Override
			public <T> boolean isWorstOrEqualsThan(T referent, T compared, Comparator<? super T> orden) {
				boolean isSmaller = orden.compare(referent, compared) >= 0;
				return isSmaller;
			};
		},
		/**
		 * Se queda con los mínimos de la comparación
		 */
		MIN {
			@Override
			public <T> boolean isWorstOrEqualsThan(T referent, T compared, Comparator<? super T> orden) {
				boolean isBigger = orden.compare(referent, compared) <= 0;
				return isBigger;
			};
		};

		/**
		 * Indica si el elemento comparado es peor que el referente respecto del orden indicado,
		 * dado este tipo de filtro.<br>
		 * El elemento es peor, si este filtro es mínimo y el comparado es mayor. O si se busca el
		 * máximo y la comparación da que es menor
		 * 
		 * @param <T> Tipo de los elementos
		 * @param referent El objeto usado como referente para la comparación
		 * @param compared El elemento comparado
		 * @param orden El orden que indica la relación entre ellos
		 * @return true si el comparado debe posicionarse después del referente
		 */
		public abstract <T> boolean isWorstOrEqualsThan(T referent, T compared, Comparator<? super T> orden);
	}

	/**
	 * A partir de las lista pasada esta función se queda con los primeros n elementos que son
	 * mínimos con respecto al comparador indicado
	 * 
	 * @param cantidadFiltrada Cantidad de elementos a preservar en la lista devuelta
	 * @param elementosAFiltrar Los objetos que se deben filtrar
	 * @param orden Relación de orden entre los elementos que permite encontrar el mínimo
	 * @return La lista ordenada de menor a mayor de los primeros elementos
	 */
	private static <T> List<T> filterFirstMin(int cantidadFiltrada, Iterable<? extends T> elementosAFiltrar, Comparator<? super T> orden) {
		return filterFirstElements(cantidadFiltrada, elementosAFiltrar, orden, FilterType.MIN);
	}

	/**
	 * A partir de la lista pasada devuelve una nueva lista con los mejores N elementos.<br>
	 * Cuáles son mejores se indica con el tipo de filtro, y la relación de comparación entre ellos
	 * con el comparator
	 * 
	 * @param <T> Tipo de los elementos
	 * @param cantidadFiltrada Cantidad máxima de elementos devueltos
	 * @param elementosAFiltrar Los elementos a ordenar y filtrar
	 * @param orden La relación de orden entre los elementos
	 * @param filterType El tipo de extremo buscado, los máximos o mínimos de la relación
	 * @return La lista ordenada de los primeros máximos o mínimos acotada a la cantidad indicada
	 */
	private static <T> List<T> filterFirstElements(int cantidadFiltrada, Iterable<? extends T> elementosAFiltrar,
			Comparator<? super T> orden, FilterType filterType) {
		LinkedList<T> filtered = new LinkedList<T>();

		for (T elemento : elementosAFiltrar) {
			if (filtered.isEmpty()) {
				// Es el primer elemento
				filtered.add(elemento);
				continue;
			}
			int elementIndex = filtered.size();
			// Buscamos en reversa el lugar que le corresponde
			for (; elementIndex > 0; elementIndex--) {
				int previousPosition = elementIndex - 1;
				T possibleBetter = filtered.get(previousPosition);
				if (filterType.isWorstOrEqualsThan(possibleBetter, elemento, orden)) {
					// Llegamos al lugar que le corresponde
					break;
				}
			}

			if (elementIndex < filtered.size()) {
				// Es mejor que los valores actuales
				filtered.add(elementIndex, elemento);
				if (filtered.size() > cantidadFiltrada) {
					// Si nos excedimos, sacamos al peor
					filtered.removeLast();
				}
			} else {
				// Hay que agregarlo al final
				if (filtered.size() < cantidadFiltrada) {
					// Todavía queda lugar para agregarlo
					filtered.add(elemento);
				}
				continue;
			}
		}

		return filtered;
	}
}
