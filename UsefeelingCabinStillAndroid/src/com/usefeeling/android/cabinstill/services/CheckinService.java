package com.usefeeling.android.cabinstill.services;

import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import com.usefeeling.android.cabinstill.facades.ApplicationFacade;
import com.usefeeling.android.cabinstill.facades.DeviceFacade;
import com.usefeeling.android.cabinstill.threads.LocationListenerThread;
import com.usefeeling.android.cabinstill.threads.WifiScannerThread;

/**
 * Servicio que extiende de WakefulIntentService y realiza las tareas de
 * envío de ubicación y registro automático de visita.
 * @author Luisma Morán.
 *
 */
public class CheckinService extends WakefulIntentService {

	private static final String TAG = "UseFeelingCheckinService";
	private static final int WAIT_TO_LOCATION = 15 * 1000; //15 segundos
	private static final int WAIT_TO_WIFI_SCAN = 30 * 1000; //30 segundos
	private static final String NAME = "CheckinService";
	
	/**
	 * Constructor.
	 * @param name Nombre del servicio.
	 */
	public CheckinService() {
		super(NAME);
	}

	@Override
	protected void doWakefulWork(Intent intent) {
		//Comprobamos estado del dispositivo
		if (!this.checkDevice()) return;
		//Enviamos posición
		this.sendPosition();
		//Enviamos wifis
		this.sendWifiPoints();
		//Registramos GCM
		this.registerGcm();
	}

	/**
	 * Registra el dispositivo en el sistema GCM
	 */
	private void registerGcm() {
		ApplicationFacade.registerGcm(this);
	}
	
	/**
	 * Envía la posición del usuario.
	 */
	private void sendPosition() {
		Thread t = new LocationListenerThread(this);
		t.start();
		try {
			t.join(WAIT_TO_LOCATION);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			Log.e(TAG, e.toString());
		}
		t = null;
	}

	/**
	 * Envía las direcciones MAC de los puntos wifi detectados.
	 */
	private void sendWifiPoints() {
		Thread t = new WifiScannerThread(this);
		t.start();
		try {
			t.join(WAIT_TO_WIFI_SCAN);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			Log.e(TAG, e.toString());
		}
		t = null;
	}

	/**
	 * Comprueba el estado del dispositivo:
	 * <li>Si tiene conexión a internet</li>
	 * <li>Si el proveedor de posicionamiento está disponible</li>
	 * <li>Que el dispositivo no se encuentre en modo tethering</li>
	 * @return <b>true</b> si el dispositivo está correctamente configurado para realizar el registro automático de visitas
	 */
	private boolean checkDevice() {
		boolean result = true;
		try {
			result = DeviceFacade.checkInternetConnection(this);
			result = result && DeviceFacade.checkLocationProvider(this, LocationManager.NETWORK_PROVIDER);
			result = result && DeviceFacade.checkTethering(this);
		} catch (Exception e) {
			result = false;
			Log.e(TAG, e.getMessage());
			Log.e(TAG, e.toString());
		}
		return result;
	}
	
}
