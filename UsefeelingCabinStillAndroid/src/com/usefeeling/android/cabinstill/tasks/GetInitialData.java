package com.usefeeling.android.cabinstill.tasks;

import java.util.ArrayList;

import android.app.Activity;

import com.usefeeling.android.R;
import com.usefeeling.android.cabinstill.api.Account;
import com.usefeeling.android.cabinstill.api.Notification;
import com.usefeeling.android.cabinstill.api.Place;
import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.dao.ActivitiesDao;
import com.usefeeling.android.cabinstill.dao.PlacesDao;
import com.usefeeling.android.cabinstill.facades.ApplicationFacade;
import com.usefeeling.android.cabinstill.facades.DataFacade;
import com.usefeeling.android.cabinstill.facades.SharedPreferencesFacade;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;

/**
 * Runnable de obtención de datos iniciales.
 * @author Moisés Vilar.
 *
 */
public class GetInitialData extends AbstractTask {
	
	private DataFacade dataFacade;
	
	/**
	 * Constructor.
	 * @param context Contexto desde el que se ejecuta la tarea.
	 * @param listener Listener de llamada cuando la tarea finalice su ejecución.
	 * @param params Parámetros de entrada para la tarea.
	 */
	public GetInitialData(Activity context, OnTaskCompleted listener, Object... params) {
		super(context, listener, params);
		this.dataFacade = new DataFacade(context);
	}
	
	@Override
	public void run() {
		try {
			this.onPreExecute();
			//Obtenemos la cuenta del usuario
			this.getAccount();
	    	if (this.result.getCode() != ResultCodes.Ok) return;
			//Obtenemos las notificaciones
			this.getNotifications();
			if (result.getCode() != ResultCodes.Ok) return;
			//Sincronizamos caché
			this.sinchronize();
			if (this.result.getCode() != ResultCodes.Ok) return;
		} finally {
			this.onPostExecute(result);
		}
	}
	
	/**
	 * Obtiene y guarda los datos de la cuenta del usuario.
	 */
	private void getAccount() {
		this.publishProgress(this.mContext.getString(R.string.getting_account_data));
		Account account = this.mUsefeeling.getAccount(ApplicationFacade.getVersionName(this.mContext), ApplicationFacade.getVersionCode(this.mContext));
		this.result = this.mUsefeeling.getLastResult();
		if (this.result.getCode() != ResultCodes.Ok) return;
		this.mPrefs.setAccount(account);
		this.mPrefs.setCurrentUser(account.toUser());
	}
	
	/**
	 * Descarga los datos sincronizados de los locales
	 */
	private void sinchronize() {
		this.publishProgress(this.mContext.getString(R.string.synchronizing));
		SharedPreferencesFacade prefs = new SharedPreferencesFacade(this.mContext);
		Long lastSyncTs = prefs.getLastSyncTs();
		PlacesDao placesDao = new PlacesDao(this.mContext);
		ActivitiesDao actDao = new ActivitiesDao(this.mContext);
		try {
			//Sincronizamos locales
			ArrayList<Place> places = this.mUsefeeling.synchronize(lastSyncTs);
			this.result = this.mUsefeeling.getLastResult();
			if (this.result != null && this.result.getCode() != null && this.result.getCode() == ResultCodes.Ok) {
				for (Place place : places) {
					if (placesDao.exists(place.getPlaceId())) {
						if (!placesDao.update(place)) {
							this.result = new Result(ResultCodes.CacheError, "Error al actualizar el local " + place.getPlaceId());
							return;
						}
					} else {
						if (!placesDao.insert(place)) {
							this.result = new Result(ResultCodes.CacheError, "Error al insertar el local " + place.getPlaceId());
							return;
						}
					}
				}
			}
			//Sincronizamos actividades
			ArrayList<com.usefeeling.android.cabinstill.api.Activity> activities = this.mUsefeeling.getActivities(lastSyncTs);
			this.result = this.mUsefeeling.getLastResult();
			if (this.result != null && this.result.getCode() != null && this.result.getCode() == ResultCodes.Ok) {
				for (com.usefeeling.android.cabinstill.api.Activity activity : activities) {
					if (actDao.exists(activity.getId(), activity.getType())) {
						if (!actDao.update(activity)) {
							this.result = new Result(ResultCodes.CacheError, "Error al actualizar la actividad " + activity.getId());
							return;
						}
					} else {
						if (!actDao.insert(activity)) {
							this.result = new Result(ResultCodes.CacheError, "Error al insertar la actividad " + activity.getId());
							return;
						}
					}
				}
			}
			//Borramos actividades antiguas
			actDao.deleteOld();
		} catch (Throwable t) {
			this.result = new Result(ResultCodes.Exception, t.getMessage());
			return;
		} finally {
			if (this.result.getCode() == ResultCodes.Ok) {
				prefs.setLastSyncTs(System.currentTimeMillis());
			}
		}
	}
	
	/**
	 * Obtiene las notificaciones.
	 */
	private void getNotifications() {
		this.publishProgress(this.mContext.getString(R.string.getting_notifications));
		ArrayList<Notification> notifications = this.mUsefeeling.getNotifications();
		this.result = this.mUsefeeling.getLastResult();
		dataFacade.setNotifications(notifications);
	}
	
	/**
	 * Obtiene las solicitudes de amistad.
	 */
/*	private void getFriendshipRequests() {
		this.publishProgress(this.mContext.getString(R.string.getting_friendship_requests));
		ArrayList<FriendshipRequest> friendshipRequests = this.mUsefeeling.getFriendshipRequests();
		result = this.mUsefeeling.getLastResult();
		dataFacade.setFriendshipRequests(friendshipRequests);
	}*/
	
	/*
	@Override
	public void run() {
		try {
			//Realizamos tareas previas a la ejecución
			this.onPreExecute();
			//Obtenemos parámetros
    		Double lat = (Double) this.mParams[0];
    		Double lon = (Double) this.mParams[1];
    		//Obtenemos los datos de la cuenta
    		this.publishProgress(this.mContext.getString(R.string.getting_account_data));
    		Account account = this.mUsefeeling.getAccount(ApplicationFacade.getVersionName(this.mContext), ApplicationFacade.getVersionCode(this.mContext));
    		result = this.mUsefeeling.getLastResult();
    		if (result.getCode() != ResultCodes.Ok) return;
    		
    		//Obtenemos la última posición del usuario
    		this.publishProgress(this.mContext.getString(R.string.getting_user_last_position));
    		Position userPosition = this.mUsefeeling.getLastPosition(this.mPrefs.getUserId());
    		result = this.mUsefeeling.getLastResult();
    		if (result.getCode() != ResultCodes.Ok) return;
    		
    		//Obtenemos las últimas posiciones de los amigos.
    		this.publishProgress(this.mContext.getString(R.string.getting_friends_last_positions));
    		ArrayList<Position> friends = this.mUsefeeling.getLastFriendsPositions();
    		result = this.mUsefeeling.getLastResult();
    		if (result.getCode() != ResultCodes.Ok) return;
    		
    		//Construimos la lista de visitas
    		this.publishProgress(this.mContext.getString(R.string.building_checkins_list));
    		ArrayList<Checkin> checkins = UseFeelingHelper.buildCheckinList(friends);
    		
    		//Filtramos la lista de posiciones
    		this.publishProgress(this.mContext.getString(R.string.filtering_positions_list));
    		UseFeelingHelper.filterPositions(checkins, friends);
    	
    		//Obtenemos las propuestas de la ciudad
    		this.publishProgress(this.mContext.getString(R.string.getting_city_proposals));
    		ArrayList<Proposal> proposals = this.mUsefeeling.getCityProposals(this.mPrefs.getCity());
     		result = this.mUsefeeling.getLastResult();
    		if (result.getCode() != ResultCodes.Ok) return;
    		
    		//Introducimos las propuestas en la base de datos local
    		this.publishProgress(this.mContext.getString(R.string.saving_city_proposals));
    		ProposalsDao dao = new ProposalsDao(this.mContext);
    		if (dao.delete()) {
    			dao.insert(proposals);
    		}
    		
    		//Filtramos las visitas
    		this.publishProgress(this.mContext.getString(R.string.filtering_checkins));
    		UseFeelingHelper.filterCheckins(checkins, proposals);
    		
    		//Obtenemos las notificaciones
    		this.publishProgress(this.mContext.getString(R.string.getting_notifications));
    		ArrayList<Notification> notifications = this.mUsefeeling.getNotifications();
    		result = this.mUsefeeling.getLastResult();
    		if (result.getCode() != ResultCodes.Ok) return;
    		
    		//Obtenemos las solicitudes de amistad
    		this.publishProgress(this.mContext.getString(R.string.getting_friendship_requests));
    		ArrayList<FriendshipRequest> friendshipRequests = this.mUsefeeling.getFriendshipRequests();
    		result = this.mUsefeeling.getLastResult();
    		if (result.getCode() != ResultCodes.Ok) return;
    		
    		//Construimos la lista de locales
    		this.publishProgress(this.mContext.getString(R.string.building_places_list));
    		ArrayList<Place> places = UseFeelingHelper.buildPlacesList(proposals);
    		
    		//Obtenemos los locales próximos al usuario
    		this.publishProgress(this.mContext.getString(R.string.getting_near_places));
    		ArrayList<Place> nearPlaces = this.mUsefeeling.getNearPlaces(lat, lon);
    		result = this.mUsefeeling.getLastResult();
    		if (result.getCode() != ResultCodes.Ok) return;
    		
    		//Guardamos los datos en la fachada de datos
    		this.publishProgress(this.mContext.getString(R.string.saving_account_data));
    		DataFacade.getInstance().setAccount(account);
    		
    		this.publishProgress(this.mContext.getString(R.string.saving_user_last_position));
    		DataFacade.getInstance().setUserPosition(userPosition, this.mContext);
    		
    		this.publishProgress(this.mContext.getString(R.string.saving_friends_last_positions));
    		DataFacade.getInstance().setFriendPositions(friends);
    		
    		this.publishProgress(this.mContext.getString(R.string.saving_city_proposals));
    		DataFacade.getInstance().setProposals(this.mContext, proposals);
    		
    		this.publishProgress(this.mContext.getString(R.string.saving_notifications));
    		DataFacade.getInstance().setNotifications(notifications);
    		
    		this.publishProgress(this.mContext.getString(R.string.saving_friendship_requests));
    		DataFacade.getInstance().setFriendshipRequests(friendshipRequests);
    		
    		this.publishProgress(this.mContext.getString(R.string.saving_checkins));
    		DataFacade.getInstance().setCheckins(checkins);
    		
    		this.publishProgress(this.mContext.getString(R.string.saving_places));
    		DataFacade.getInstance().setPlaces(places);
    		
    		this.publishProgress(this.mContext.getString(R.string.saving_near_places));
    		DataFacade.getInstance().setNearPlaces(nearPlaces);
		} catch (Throwable t) {
			result = new Result(ResultCodes.Exception, t.getMessage());
			return;
		} finally {
			this.onPostExecute(result);
		}
	}
	*/
}
