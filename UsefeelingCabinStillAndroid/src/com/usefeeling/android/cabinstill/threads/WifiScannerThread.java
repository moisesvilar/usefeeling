package com.usefeeling.android.cabinstill.threads;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.usefeeling.android.cabinstill.api.UseFeeling;
import com.usefeeling.android.cabinstill.facades.SharedPreferencesFacade;
import com.usefeeling.android.cabinstill.helpers.Synchronizer;

/**
 * Hilo que escanea y envía los puntos wifi que detecta el dispositivo.
 * @author Moisés Vilar.
 *
 */
public class WifiScannerThread extends Thread {

	private static final String TAG = "WifiScannerThread";
	private static final int WAIT_TIME = 5 * 1000;
	private Context context;
	private SharedPreferencesFacade prefs;
	private UseFeeling usefeeling;
	private WifiManager wifiManager;
	private boolean turnOff = false;
	private final Synchronizer synchronizer = new Synchronizer();
	
	/**
	 * Constructor.
	 * @param context Contexto desde el que se ejecuta el hilo.
	 */
	public WifiScannerThread(Context context) {
		this.context = context;
		this.prefs = new SharedPreferencesFacade(this.context);
		this.usefeeling = new UseFeeling(this.prefs.getUserId(), this.prefs.getPassword());
		this.wifiManager = (WifiManager)this.context.getSystemService(Context.WIFI_SERVICE);
	}
	
	@Override
	public void run() {
		//Si la wifi no está activada...
		if (this.wifiManager != null && !this.wifiManager.isWifiEnabled()) {
			//Marcamos bandera para desactivarla después
			this.turnOff = true;
			//Activamos wifi
			this.wifiManager.setWifiEnabled(true);
			//Esperamos unos segundos a que la wifi se active
			try {
				Thread.sleep(WAIT_TIME);
			} catch (Exception e) {}
		}
		//Registramos el receiver
		this.registerReceiver();
		//Comenzamos el escaneo
		if (this.wifiManager != null) this.wifiManager.startScan();
		synchronizer.espera();
	}

	/**
	 * Registra el receptor de resultados del escaneo wifi
	 */
	private void registerReceiver() {
		this.context.registerReceiver(new BroadcastReceiver(){
			
			@Override
			public void onReceive(Context context, Intent intent) {
				try {
					//Desregistramos el receptor
					context.unregisterReceiver(this);
					//Obtenemos los resultados del escaneo
					List<ScanResult> results = wifiManager.getScanResults();
					//Ordenamos los resultados de mayor a menor potencia recibida
					Collections.sort(results, new ScanResultComparator());
					//Nos quedamos con los 5 puntos wifi más potentes detectados
					results = results.subList(0, Math.min(5, results.size()));
					//Guardamos las direcciones MAC de los puntos wifi detectados con una potencia superior a -85 dBm
					ArrayList<String> macs = new ArrayList<String>();
					for (ScanResult result : results) if (result.level > -85) macs.add(result.BSSID);
					final ArrayList<String> finalMacs = macs;
					final Context finalContext = context;
					//Enviamos las direcciones MAC
					if (usefeeling != null && !macs.isEmpty()) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								SharedPreferencesFacade prefs = new SharedPreferencesFacade(finalContext);
								UseFeeling usefeeling = new UseFeeling(prefs.getUserId(), prefs.getPassword());
								usefeeling.sendWifiPoints(finalMacs, System.currentTimeMillis(), true);
							}
						}).start();
					}
					//Desactivamos la wifi si es necesario
					if (turnOff) {
						if (wifiManager != null) wifiManager.setWifiEnabled(false);
						turnOff = false;
					}
				} catch (Exception e) {
					Log.e(TAG, e.getMessage());
					Log.e(TAG, e.toString());
				} finally {
					synchronizer.notifica();
				}
			}
			
		}, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	}
	
	/**
	 * Clase utilizada para la ordenación de la lista de ScanResult
	 * Compara ScanResult para ordenarlos por su potencia de señal (de mayor a menor)
	 * @author Luisma Morán
	 *
	 */
	private static class ScanResultComparator implements Comparator<ScanResult> {
	    @Override
	    public int compare(ScanResult sr1, ScanResult sr2) {
	        return (sr1.level > sr2.level ? -1 : (sr1.level==sr2.level ? 0 : 1));
	    }
	}
	
}
