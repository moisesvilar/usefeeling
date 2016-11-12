package com.usefeeling.android.cabinstill.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

import com.actionbarsherlock.app.SherlockActivity;
import com.usefeeling.android.R;
import com.usefeeling.android.cabinstill.facades.ApplicationFacade;
import com.usefeeling.android.cabinstill.facades.DataFacade;
import com.usefeeling.android.cabinstill.facades.DeviceFacade;
import com.usefeeling.android.cabinstill.facades.MessagesFacade;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;
import com.usefeeling.android.cabinstill.tasks.DownloadCities;

/**
 * Activity principal de la aplicación.
 * Comprueba si hay una sesión iniciada en el teléfono con una cuenta de UseFeeling.
 * Si es así, descarga los datos sobre amigos y propuestas y muestra la pantalla de mapa principal.
 * Si no es así, descarga los datos sobre ciudades y muestra la pantalla de inicio.
 * @author Moisés Vilar.
 *
 */
public class MainActivity extends SherlockActivity implements OnTaskCompleted, LocationListener {

	private DataFacade dataFacade;
	private LocationManager locManager;
	private Handler handler;
	private String provider;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		this.dataFacade = new DataFacade(this);
		//Comprobamos que la red actual es suficiente
		if (!DeviceFacade.isCurrentNetworkTypeAcceptable(this)) {
			MessagesFacade.showCurrentNetworkisNotAcceptableDialog(this,
			new OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {}
			},
			new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					ApplicationFacade.kill(MainActivity.this);
				}
			});
		}
		//Comprobamos que la localización por redes está activada
		if (!DeviceFacade.isWirelessLocationEnable(this)) {
			MessagesFacade.showWirelessLocationIsNotEnableDialog(
				this,
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        MainActivity.this.startActivityForResult(callGPSSettingIntent, 0);
					}
				},
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						MessagesFacade.showLocationIsRequiredDialog(MainActivity.this);
					}
				}
			);
		}
		//Comprobamos que el usuario haya dado permiso para monitorizar su posición
		if (!this.dataFacade.isLocationMonitoringConfirmed()) {
			MessagesFacade.showLocationMonitoringConfirmationDialog(
					this, 
					new OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							MainActivity.this.dataFacade.setLocationMonitoringConfirmation();
						}
					}, new OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							MessagesFacade.showLocationIsRequiredDialog(MainActivity.this);
						}
					}
			);
		}
		//Si el posicionamiento por redes está activado, iniciamos la aplicación
		if (DeviceFacade.isWirelessLocationEnable(this)) {
			this.updateLocation();
		}
		//ApplicationFacade.copyDatabaseToSdCard(this);
	}

	/**
	 * Launches the location updating process.
	 */
	private void updateLocation() {
		this.locManager = (LocationManager)	this.getSystemService(Context.LOCATION_SERVICE);
		if (this.locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			this.provider = LocationManager.GPS_PROVIDER;
			this.locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		} else {
			this.provider = LocationManager.NETWORK_PROVIDER;
			this.locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		}
		this.launchHandler();
	}
	
	/**
	 * Inits and launch the handler for controling the location timeout.
	 */
	private void launchHandler() {
		this.handler = new Handler();
        this.handler.postDelayed(this.locationCounter,1000L );
	}
	
	private final Runnable locationCounter = new Runnable() {
    	private int counter = 0;
		@Override
		public void run() {
			this.counter++;
			if (this.counter > 5) stopLocation();
			else handler.postDelayed(this, 1000L);
		}
    };
	
    @Override
    public void onLocationChanged(Location location) {
    	this.handler.removeCallbacks(this.locationCounter);
    	this.locManager.removeUpdates(this);
    	this.dataFacade.setUserPosition(location.getLatitude(), location.getLongitude());
    	this.init(false);
    }
        
    @Override
    public void onProviderDisabled(String provider) {}
    
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
	
	/**
	 * Inicializa la aplicación
	 */
	private void init(boolean defaultLocation) {
		//Si hay una sesión iniciada, lanzamos servicios
		if (this.dataFacade.isSessionStarted()) {
			ApplicationFacade.registerGcm(this);
			ApplicationFacade.startCheckinService(this);
		}
		//Descargamos ciudades
		new DownloadCities(this, this, defaultLocation).execute();
	}
	
	/**
	 * Stops the location and download default location from server
	 */
	private void stopLocation() {
		this.handler.removeCallbacks(this.locationCounter);
		this.locManager.removeUpdates(this);
		if (this.provider.equals(LocationManager.GPS_PROVIDER)) {
			this.provider = LocationManager.NETWORK_PROVIDER;
			this.locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
			this.launchHandler();
			return;
		}
		Location location = this.locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) == null ? this.locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) : this.locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (location == null) {
			if (this.dataFacade.getUserPosition() == null) this.init(true);
			else this.init(false);
		}
		else {
			this.dataFacade.setUserPosition(location.getLatitude(), location.getLongitude());
			this.init(false);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 0) {
			if (!DeviceFacade.isWirelessLocationEnable(this)) {
				MessagesFacade.showWirelessLocationIsNotEnableDialog(
					this,
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	                        MainActivity.this.startActivity(callGPSSettingIntent);
						}
					},
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							ApplicationFacade.kill(MainActivity.this);
						}
					}
				);
			}
			else {
				this.updateLocation();
			}
		}
	}

	@Override
	public void onTaskCompleted(Object result) {
		//Si hay una sesión iniciada, obtenemos datos iniciales de la cuenta.
		if (this.dataFacade.isSessionStarted()) {
			ApplicationFacade.startActivity(this, SplashActivity.class);
		}
		//Si no hay una sesión iniciada, mostramos la ventana de inicio.
		else {
			ApplicationFacade.startActivity(this, StartScreenActivity.class);
		}
		this.finish();
	}
}
