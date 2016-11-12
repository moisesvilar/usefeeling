package com.usefeeling.android.cabinstill.facades;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.osmdroid.util.GeoPoint;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.usefeeling.android.R;
import com.usefeeling.android.cabinstill.api.Account;
import com.usefeeling.android.cabinstill.api.City;
import com.usefeeling.android.cabinstill.api.Event;
import com.usefeeling.android.cabinstill.api.Notification;
import com.usefeeling.android.cabinstill.api.Place;
import com.usefeeling.android.cabinstill.api.Promo;
import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.api.UseFeeling;
import com.usefeeling.android.cabinstill.api.User;
import com.usefeeling.android.cabinstill.dao.ActivitiesDao;
import com.usefeeling.android.cabinstill.dao.PlacesDao;
import com.usefeeling.android.cabinstill.helpers.Position;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;
import com.usefeeling.android.cabinstill.tasks.MarkPlaceAsFav;

/**
 * Fachada de datos cargados en la aplicación.
 * @author Moisés Vilar.
 *
 */
public class DataFacade implements Serializable {
	
	private static final long serialVersionUID = 1984732860399504246L;
	
	private Activity mActivity;
	private SharedPreferencesFacade mPrefs;
	private PlacesDao mPlacesDao;
	private ActivitiesDao mActivitiesDao;
	private UseFeeling mUseFeeling;
	
	private static volatile Account account = null;
	private static volatile Position userPosition;
	private static volatile ArrayList<Notification> notifications = null;
	private static volatile ArrayList<City> cities = null;
	private static volatile ArrayList<Place> places = null;
	private static volatile ArrayList<Place> favoritePlaces = null;
	private static volatile ArrayList<Place> affinedPlaces = null;
	private static volatile ArrayList<Event> events = null;
	private static volatile ArrayList<Promo> promos = null;
	
	private static volatile Object sync = new Object();
	
	/**
	 * Constructor.
	 * @param context Activity.
	 */
	public DataFacade(Activity context){
		this.mActivity = context;
		this.mPrefs = new SharedPreferencesFacade(this.mActivity);
		this.mPlacesDao = new PlacesDao(this.mActivity);
		this.mActivitiesDao = new ActivitiesDao(this.mActivity);
		this.mUseFeeling = new UseFeeling(this.mPrefs.getUserId(), this.mPrefs.getPassword());
	}
	
	/**
	 * Constructor.
	 * @param context Contexto.
	 */
	public DataFacade(Context context) {
		this.mPrefs = new SharedPreferencesFacade(context);
		this.mPlacesDao = new PlacesDao(context);
	}

	/**
	 * @return the userPosition
	 */
	public Position getUserPosition() {
		userPosition = this.mPrefs.getUserPosition();
		if (userPosition == null) return new Position(UseFeeling.DEFAULT_LAT, UseFeeling.DEFAULT_LON);
		return new Position(this.mActivity.getString(R.string.your_position), userPosition.getLatitude(), userPosition.getLongitude());
	}

	/**
	 * @param userPosition the userPosition to set
	 */
	public void setUserPosition(Position userPosition) {
		synchronized (DataFacade.sync) { DataFacade.userPosition = userPosition; }
		this.mPrefs.setUserPosition(userPosition);
	}
	
	/**
	 * Establece la posición del usuario.
	 * @param lat Latitud.
	 * @param lon Longitud
	 */
	public void setUserPosition(double lat, double lon) {
		try {
			if (account == null || account.getName() == null || account.getUserid() == null) synchronized(DataFacade.sync) { account = this.mPrefs.getAccount(); }
			if (userPosition == null) {
				synchronized (sync) { 
					userPosition = new Position(account.getName(), lat, lon, UseFeeling.USERID + "=" + account.getUserid());
				}
			}
			this.setUserPosition(userPosition);
		} catch (Exception e){}
	}
	
	/**
	 * @return the account
	 */
	public Account getAccount() {
		if (account == null) synchronized (sync) { account = this.mPrefs.getAccount(); }
		return account;
	}

	/**
	 * @param account the account to set
	 */
	public void setAccount(Account account) {
		this.mPrefs.setAccount(account);
		synchronized (DataFacade.sync) { DataFacade.account = account; }
	}

	/**
	 * @return the proposals
	 */
	public ArrayList<Place> getPlaces() {
		if (DataFacade.places == null) synchronized (DataFacade.sync) { DataFacade.places = this.mPlacesDao.get(); }
		return DataFacade.places;
	}
	
	/**
	 * Obtiene el local especificado.
	 * @param placeid El identificador del local.
	 * @return El local.
	 */
	public Place getPlace(Long placeid) {
		if (DataFacade.places != null) {
			for (Place place : DataFacade.places) {
				if (place.getPlaceId().equals(placeid)) return place;
			}
		}
		return this.mPlacesDao.get(placeid);				
	}
	
	/**
	 * Obtiene los locales en un hilo aparte.
	 * @param listener Listener a invocar cuando se termine la tarea.
	 */
	public void getPlaces(final OnTaskCompleted listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (places == null) synchronized (DataFacade.sync) { places = mPlacesDao.get(); }
				if (mActivity != null) if (mActivity !=null) mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						listener.onTaskCompleted(places);
					}
				});
			}
		}).start();
	}
	
	/**
	 * Obtiene los locales ordenados por afinidad en un hilo aparte.
	 * @param listener Listener a invocar cuando se termine la tarea.
	 */
	public void getPlacesOrderByAffinity(final OnTaskCompleted listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final ArrayList<Place> result = mPlacesDao.getOrderByAffinity();
				if (mActivity != null) if (mActivity !=null) mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						listener.onTaskCompleted(result);
					}
				});
			}
		}).start();
	}

	/**
	 * @param proposals the proposals to set
	 */
	public void setPlaces(ArrayList<Place> places) {
		synchronized (DataFacade.sync) { DataFacade.places = places; }
		this.mPlacesDao.insert(places);
	}

	/**
	 * @return the notifications
	 */
	public ArrayList<Notification> getNotifications() {
		if (DataFacade.notifications == null) synchronized (DataFacade.sync) { DataFacade.notifications = this.mPrefs.getNotifications(); }
		return notifications;
	}

	/**
	 * @param notifications the notifications to set
	 */
	public void setNotifications(ArrayList<Notification> notifications) {
		this.mPrefs.setNotifications(notifications);
		synchronized (DataFacade.sync) { DataFacade.notifications = notifications; }
	}
	
	/**
	 * Obtiene el número de notificaciones no visualizadas por el usuario.
	 * @return El número de notificaciones no visualizadas por el usuario.
	 */
	public int getNumUnviewedNotifications() {
		int result = 0;
		if (DataFacade.notifications == null) synchronized (DataFacade.sync) { DataFacade.notifications = this.mPrefs.getNotifications(); }
		if (DataFacade.notifications == null) return 0;
		for (Notification notification : DataFacade.notifications) {
			if (notification.isPendiente()) result++;
		}
		return result;
	}

	/**
	 * Obtiene los locales como una matriz de cadenas.
	 * @return Los locales.
	 */
	public String[] placesToArrayString() {
		if (DataFacade.places == null) synchronized (DataFacade.sync) { DataFacade.places = this.mPlacesDao.get(); }
		ArrayList<String> result = new ArrayList<String>();
		for (Place place : DataFacade.places) {
			result.add(place.getName());
		}
		return result.toArray(new String[result.size()]);
	}

	/**
	 * Obtiene las ciudades descargadas como un array de cadenas.
	 * @return Las ciudades descargadas.
	 */
	public CharSequence[] getCities() {
		if (DataFacade.cities  == null) synchronized (DataFacade.sync) { DataFacade.cities = this.mPrefs.getCities(); }
		CharSequence[] result = new CharSequence[DataFacade.cities.size()];
		for (int i=0; i< DataFacade.cities.size(); i++) {
			City city = DataFacade.cities.get(i);
			result[i] = city.getName();
		}
		return result;
	}
	
	/**
	 * Obtiene las opciones de filtrado de ciudad como un array de cadenas.
	 * @return Las ciudades descargadas.
	 */
	public CharSequence[] getCityOptions() {
		if (DataFacade.cities  == null) synchronized (DataFacade.sync) { DataFacade.cities = this.mPrefs.getCities(); }
		CharSequence[] result = new CharSequence[DataFacade.cities.size() + 1];
		result[0] = "-";
		for (int i=0; i< DataFacade.cities.size(); i++) {
			City city = DataFacade.cities.get(i);
			result[i+1] = city.getName();
		}
		return result;
	}
	
	/**
	 * Obtiene los locales próximos al usuario.
	 * @return Los locales próximos al usuario.
	 */
	public List<Place> getNearPlaces(float lat, float lon) {
		return this.mPlacesDao.get(lat, lon);
	}
	
	/**
	 * Obtiene los locales próximos al usario en un hilo aparte.
	 * @param listener Listener a invocar cuando se termine la tarea.
	 */
	public void getNearPlaces(final float lat, final float lon, final OnTaskCompleted listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final List<Place> nearplaces = mPlacesDao.get(lat, lon);
				if (mActivity !=null) mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						listener.onTaskCompleted(nearplaces);
					}
				});
			}
		}).start();
	}
	
	/**
	 * Obtiene los locales próximos al usario según el radio especificado en un hilo aparte.
	 * @param lat
	 * @param lon
	 * @param radius
	 * @param listener Listener a invocar cuando se termine la tarea.
	 */
	public void getNearPlaces(final float lat, final float lon, final float radius, final OnTaskCompleted listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final List<Place> nearplaces = mPlacesDao.get(lat, lon, radius);
				if (mActivity !=null) mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						listener.onTaskCompleted(nearplaces);
					}
				});
			}
		}).start();
	}
	
	/**
	 * Obtiene los locales ubicados en la ciudad especificada en un hilo aparte.
	 * @param city
	 * @param listener Listener a invocar cuando se termine la tarea.
	 */
	public void getPlacesByCity(final String city, final OnTaskCompleted listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final ArrayList<Place> nearplaces = mPlacesDao.getByCity(city);
				if (mActivity !=null) mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						listener.onTaskCompleted(nearplaces);
					}
				});
			}
		}).start();
	}
	
	/**
	 * Obtiene los locales ubicados en la ciudad especificada y ordenados por afinidad en un hilo aparte.
	 * @param city
	 * @param listener Listener a invocar cuando se termine la tarea.
	 */
	public void getPlacesByCityOrderByAffinity(final String city, final OnTaskCompleted listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final ArrayList<Place> nearplaces = mPlacesDao.getByCityOrderByAffinity(city);
				if (mActivity !=null) mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						listener.onTaskCompleted(nearplaces);
					}
				});
			}
		}).start();
	}
	
	/**
	 * Obtiene los locales próximos al usuario.
	 * @return Los locales próximos al usuario.
	 */
	public List<Place> getNearPlaces(double lat, double lon) {
		return this.mPlacesDao.get((float)lat, (float)lon);
	}
	
	/**
	 * Obtiene los locales próximos al usario en un hilo aparte.
	 * @param listener Listener a invocar cuando se termine la tarea.
	 */
	public void getNearPlaces(final double lat, final double lon, final OnTaskCompleted listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final List<Place> nearplaces = mPlacesDao.get((float)lat, (float)lon);
				if (mActivity !=null) mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						listener.onTaskCompleted(nearplaces);
					}
				});
			}
		}).start();
	}

	/**
	 * Marca el local como favorito.
	 * @param placeId Identificador del local.
	 */
	public void markPlaceAsFav(Long placeId) {
		if (DataFacade.places != null) {
			for (Place place : DataFacade.places) {
				if (place.getPlaceId().equals(placeId)) {
					synchronized (DataFacade.sync) { place.setIsFavorite(true);} 
					break;
				}
			}
		}
		this.mPlacesDao.markPlaceAsFav(placeId);
	}
	
	/**
	 * Marca el local como favorito.
	 * @param placeId Identificador del local.
	 */
	public void unmarkPlaceAsFav(Long placeId) {
		if (DataFacade.places != null) {
			for (Place place : DataFacade.places) {
				if (place.getPlaceId().equals(placeId)) {
					synchronized (DataFacade.sync) { place.setIsFavorite(false); }
					break;
				}
			}
		}
		this.mPlacesDao.unmarkPlaceAsFav(placeId);
	}

	/**
	 * Obtiene el identificador del usuario.
	 * @return El identificador del usuario.
	 */
	public Long getUserId() {
		return this.mPrefs.getUserId();
	}

	/**
	 * Obtiene la posición del local especificado por su índice en la lista.
	 * @param index Índice del local en la lista.
	 * @return La posición del local especificado por su índice.
	 */
	public GeoPoint getPlacePosition(int index) {
		if (DataFacade.places == null) synchronized (DataFacade.sync) { DataFacade.places = this.mPlacesDao.get(); }
		return new GeoPoint(DataFacade.places.get(index).getLatitude(), DataFacade.places.get(index).getLongitude());
	}
	
	/**
	 * Obtiene los locales favoritos del usuario.
	 * @return Los locales.
	 */
	public ArrayList<Place> getFavoritePlaces() {
		if (DataFacade.favoritePlaces == null) synchronized (DataFacade.sync) { DataFacade.favoritePlaces = this.mPlacesDao.getFavorites(); }
		return DataFacade.favoritePlaces;
	}
	
	/**
	 * Obtiene los locales favoritos del usuario en un hilo aparte.
	 * @param listener Listener a invocar cuando se termine la tarea.
	 */
	public void getFavoritePlaces(final OnTaskCompleted listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (favoritePlaces == null) synchronized (DataFacade.sync) { favoritePlaces = mPlacesDao.getFavorites(); }
				if (mActivity !=null) mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						listener.onTaskCompleted(favoritePlaces);
					}
				});
			}
		}).start();
	}
	
	/**
	 * Obtiene los locales más afines con el usuario.
	 * @return Los locales.
	 */
	public ArrayList<Place> getAffinedPlaces() {
		if (DataFacade.affinedPlaces == null) synchronized (DataFacade.sync) { DataFacade.affinedPlaces = this.mPlacesDao.getAffined(); }
		return DataFacade.affinedPlaces;
	}
	
	/**
	 * Obtiene los locales más afines al usuario en un hilo aparte.
	 * @param listener Listener a invocar cuando se termine la tarea.
	 */
	public void getAffinedPlaces(final OnTaskCompleted listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (affinedPlaces == null) synchronized (DataFacade.sync) { affinedPlaces = mPlacesDao.getAffined(); }
				if (mActivity !=null) mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						listener.onTaskCompleted(affinedPlaces);
					}
				});
			}
		}).start();
	}
	
	/**
	 * Marca el local como favorito.
	 * @param item Item del menú de la action bar.
	 */
	public void markAsFavorite(final Activity context, final Place place, final MenuItem item) {
		new MarkPlaceAsFav(context, new OnTaskCompleted() {
			@Override
			public void onTaskCompleted(Object rawResult) {
				Result result = (Result)rawResult;
				if (result.getCode() != ResultCodes.Ok) {
					MessagesFacade.toastLong(context, result.getMessage());
					return;
				}
				place.setIsFavorite(!place.isFavorite());
				if (!place.isFavorite()) {
					DataFacade.this.unmarkPlaceAsFav(place.getPlaceId());
					place.setFavorites(place.getFavorites()-1);
				}
				else { 
					DataFacade.this.markPlaceAsFav(place.getPlaceId());
					place.setFavorites(place.getFavorites()+1);
				}
				MessagesFacade.toastLong(context, place.isFavorite() ? context.getResources().getString(R.string.place_marked_as_fav) : context.getResources().getString(R.string.place_unmarked_as_fav));
				item.setIcon(place.isFavorite() ? R.drawable.star_filled : R.drawable.star_unfilled);
				((TextView)context.findViewById(R.id.placeFavorites)).setText(place.getFavorites().toString());
			}
		}, place.getPlaceId(), !place.isFavorite()).execute();
	}

	/**
	 * Obtiene los eventos cercanos al usuario en un hilo aparte.
	 * @param listener
	 */
	public void getEvents(final OnTaskCompleted listener, final boolean update) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (update) { 
					synchronized (DataFacade.sync) { 
						events = mUseFeeling.getEvents(getUserPosition().getLatitude(), getUserPosition().getLongitude());
						if (events == null) events = new ArrayList<Event>();
					}
				} else if (events == null) {
					synchronized (DataFacade.sync) { 
						events = mUseFeeling.getEvents(getUserPosition().getLatitude(), getUserPosition().getLongitude());
						if (events == null) events = new ArrayList<Event>();
					}
				}
				if (mActivity !=null) mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						listener.onTaskCompleted(events);
					}
				});
			}
		}).start();
	}

	/**
	 * Obtiene las promociones cercanas al usuario en un hilo aparte.
	 * @param listener
	 */
	public void getPromos(final OnTaskCompleted listener, final boolean update) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (update) { 
					synchronized (DataFacade.sync) { 
						promos = mUseFeeling.getPromos(getUserPosition().getLatitude(), getUserPosition().getLongitude());
						if (promos == null) promos = new ArrayList<Promo>();
					}
				} else if (promos == null) {
					synchronized (DataFacade.sync) { 
						promos = mUseFeeling.getPromos(getUserPosition().getLatitude(), getUserPosition().getLongitude());
						if (promos == null) promos = new ArrayList<Promo>();
					}
				}
				if (mActivity !=null) mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						listener.onTaskCompleted(promos);
					}
				});
			}
		}).start();
	}

	/**
	 * Obtiene los datos del usuario actual de la aplicación.
	 * @return Los datos del usuario actual de la aplicación.
	 */
	public User getCurrentUser() {
		return this.mPrefs.getCurrentUser();
	}
	
	/**
	 * Establece los datos del usuario actural de la aplicación.
	 * @param user Los datos del usuario actual de la aplicación.
	 */
	public void setCurrentUser(User user) {
		this.mPrefs.setCurrentUser(user);
	}

	/**
	 * Retrieves the leisure proposals right now.
	 * @param limit The max limit of proposals.
	 * @return The proposals
	 */
	public ArrayList<?> getNow(int limit) {
		return this.mActivitiesDao.getNow(limit);
	}

	/**
	 * Retrieves the limit of proposals to show over map.
	 * @return
	 */
	public int getProposalsLimit() {
		// TODO Set the limit over the internet
		return 10;
	}

	/**
	 * Retrieves the leisure proposals for today.
	 * @param limit The max limit of proposals.
	 * @return The proposals
	 */
	public ArrayList<?> getToday(int limit) {
		return this.mActivitiesDao.getToday(limit);
	}

	/**
	 * Retrieves the leisure proposals for later.
	 * @param limit The max limit of proposals.
	 * @return The proposals
	 */
	public ArrayList<?> getLater(int limit) {
		return this.mActivitiesDao.getLater(limit);
	}

	/**
	 * Retrieves the more famous venues.
	 * @return The venues.
	 */
	public ArrayList<?> getFamous() {
		return this.mPlacesDao.getFamous();
	}

	/**
	 * Retrieves every venues.
	 * @return The venues.
	 */
	public ArrayList<?> getEverything() {
		return this.mPlacesDao.getOrderByAffinity();
	}

	/**
	 * Set first running flag value.
	 * @param b The value.
	 */
	public void setFirstRunning(boolean b) {
		this.mPrefs.setFirstRunning(b);
	}

	/**
	 * Retrieves the first running flag.
	 * @return The frist running flag.
	 */
	public boolean isFirstRunning() {
		return true; //TODO remove
		//return this.mPrefs.isFirstRunning();
	}

	/**
	 * Removes all user data
	 */
	public void remove() {
		this.mPrefs.clear();
		this.mActivitiesDao.delete();
		this.mPlacesDao.delete();
	}

	/**
	 * Retrieves if session is started.
	 * @return If session is started.
	 */
	public boolean isSessionStarted() {
		return this.mPrefs.isSessionStarted();
	}

	/**
	 * Checks if the user has confirmed the location monitoring.
	 * @return The value.
	 */
	public boolean isLocationMonitoringConfirmed() {
		return this.mPrefs.isLocationMonitoringConfirmed();
	}

	/**
	 * Sets the location monitoring confirmation to true.
	 */
	public void setLocationMonitoringConfirmation() {
		this.mPrefs.setLocationMonitoringConfirmation();
		
	}

	/**
	 * Retrieves the password.
	 * @return The password.
	 */
	public String getPassword() {
		return this.mPrefs.getPassword();
	}
}
 