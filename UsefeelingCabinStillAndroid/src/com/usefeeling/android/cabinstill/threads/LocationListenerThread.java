package com.usefeeling.android.cabinstill.threads;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

import com.usefeeling.android.cabinstill.api.UseFeeling;
import com.usefeeling.android.cabinstill.facades.DataFacade;

/**
 * Hilo que obtiene y envía la posición del usuario
 * @author Moisés Vilar.
 *
 */
public class LocationListenerThread extends Thread implements LocationListener {
	
	private Context context;
	private UseFeeling usefeeling;
	private LocationManager locationManager;
	private DataFacade dataFacade;
	
	/**
	 * Constructor.
	 * @param context Contexto desde el que se ejecuta el hilo.
	 */
	public LocationListenerThread(Context context) {
		this.context = context;
		this.dataFacade = new DataFacade(this.context);
		this.usefeeling = new UseFeeling(this.dataFacade.getUserId(), this.dataFacade.getPassword());
		this.locationManager = (LocationManager)this.context.getSystemService(Context.LOCATION_SERVICE);
	}
	
	@Override
	public void run() {
		//Si el proveedor de localización por red no está disponible, enviamos la última posición conocida.
		if (this.locationManager != null && !this.locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			Location location = this.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (location == null) {
				location = this.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if (location == null) return;
			}
			this.usefeeling.setGpsPosition(location.getLatitude(), location.getLongitude(), new java.util.Date().getTime(), true);
			return;
		}
		if (this.locationManager == null) return;
		//Registramos este objeto como listener de escucha de nueva posición.
		this.locationManager.removeUpdates(this);
		Looper.prepare();
		this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 400, 1, this);
		Looper.loop();
	}

	@Override
	public void onLocationChanged(Location location) {
		if (Looper.myLooper() != null) Looper.myLooper().quit();
		if (this.locationManager != null) this.locationManager.removeUpdates(this);
		if (location != null) this.usefeeling.setGpsPosition(location.getLatitude(), location.getLongitude(), System.currentTimeMillis(), true);
		if (location != null) dataFacade.setUserPosition(location.getLatitude(), location.getLongitude());
	}

	@Override
	public void onProviderDisabled(String provider) {
		if (Looper.myLooper() != null) Looper.myLooper().quit();
		if (this.locationManager != null) this.locationManager.removeUpdates(this);
	}

	@Override
	public void onProviderEnabled(String provider) {
		if (Looper.myLooper() != null) Looper.myLooper().quit();
		if (this.locationManager != null) this.locationManager.removeUpdates(this);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		if (Looper.myLooper() != null) Looper.myLooper().quit();
		if (this.locationManager != null) this.locationManager.removeUpdates(this);
	}
}
