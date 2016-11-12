package com.usefeeling.android.cabinstill.activities;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.usefeeling.android.R;
import com.usefeeling.android.cabinstill.facades.ApplicationFacade;
import com.usefeeling.android.cabinstill.facades.SharedPreferencesFacade;

/**
 * Activity de pantalla inicial.<br/>
 * Aquí el usuario puede crear una nueva cuenta en UseFeeling o acceder a su cuenta con sus credenciales de acceso.<br><br>
 * 
 * Si crea una nueva cuenta, se le pregunta al usuario si desea enlazar su cuenta de Facebook con UseFeeling. En este caso,
 * ejecuta la fachada de Facebook para obtener los datos personales del usuario en Facebook (concretamente: nombre y apellidos,
 * fecha de nacimiento, dirección de correo electrónico, sexo y foto de perfil) y los establece en el intent de entrada de la
 * <font face="courier new">Activity</font> de crear cuenta.<br><br>
 * 
 * Si accede con sus credenciales de acceso, lanza la <font face="courier new">Activity</font> de acceso.
 * @author Moisés Vilar.
 *
 */
public class StartScreenActivity extends SherlockActivity implements LocationListener {
	
	private SharedPreferencesFacade prefs;
	private LocationManager locationManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_screen);
		this.locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);        
        if (this.locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, this);
        }
		this.prefs = new SharedPreferencesFacade(this);
		this.setUserInterface();
	}
	
	/**
	 * Establece la interfaz de usuario.
	 */
	private void setUserInterface() {
		TextView tvInfo = (TextView)findViewById(R.id.tvWhatsUseFeeling);
		tvInfo.setText(tvInfo.getText().toString().replace("[$CITIES]", this.prefs.getCitiesToString()));
	}
	
	/**
	 * Manejador del evento OnClick del botón bCreateAccount.<br><br>
	 * Muestra el mensaje por pantalla donde se le solicita permiso al usuario para enlazar su cuenta de Facebook
	 * con UseFeeling.
	 * @param v
	 */
	public void bCreateAccount_OnClick(View v) {
		if (v.getId() != R.id.bCreateAccount) return;
		//Lanzamos la pantalla de creación de cuenta
		ApplicationFacade.startActivity(this, CreateAccountActivity.class);
	}
	
	/**
	 * Manejador del evento OnClick del botón bLogin.
	 * @param v
	 */
	public void bLogin_OnClick(View v) {
		if (v.getId() != R.id.bLogin) return;
		ApplicationFacade.startActivity(this, LoginActivity.class);
	}

	/**
	 * Al recibir la posición del usuario, obtenemos la ciudad en la que se encuentra.
	 * Si UseFeeling no está desplegado en dicha ciudad, mostramos el <font face="courier new">TextView</font>
	 * que informa de dicho suceso en la interfaz de usuario.
	 */
	@Override
	public void onLocationChanged(Location location) {
		this.locationManager.removeUpdates(this);
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		String city = "";
		try {
			List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
			if (!addresses.isEmpty()) city = addresses.get(0).getLocality();
		} catch (Exception e) {
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, this);
			return;
		}
		if (city == null) return;
		this.prefs.setCity(city);
		String cities = this.prefs.getCitiesToString();
		if (cities.contains(city)) return;
		final String cityFinal = city;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				TextView tvInfoCity = (TextView)findViewById(R.id.tvInfoCity);
				tvInfoCity.setText(getString(R.string.city_not_in_usefeeling).replace("[$CITY]", cityFinal));
				tvInfoCity.setVisibility(View.VISIBLE);
			}
		});
	}

	@Override
	public void onProviderDisabled(String provider) {}

	@Override
	public void onProviderEnabled(String provider) {}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}
}
