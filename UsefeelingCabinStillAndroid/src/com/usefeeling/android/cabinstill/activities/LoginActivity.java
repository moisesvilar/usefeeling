package com.usefeeling.android.cabinstill.activities;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.usefeeling.android.R;
import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.facades.ApplicationFacade;
import com.usefeeling.android.cabinstill.facades.MessagesFacade;
import com.usefeeling.android.cabinstill.facades.SharedPreferencesFacade;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;
import com.usefeeling.android.cabinstill.tasks.Login;
import com.usefeeling.android.cabinstill.tasks.RememberPassword;
import com.usefeeling.android.cabinstill.values.States;

/**
 * Pantalla de acceso a UseFeeling.<br><br>
 * El usuario puede acceder a su cuenta de UseFeeling con su dirección de correo electrónico y su contraseña.<br>
 * También se permite recordar la contraseña de acceso a través del correo electrónico introducido.<br>
 * @author Moisés Vilar.
 *
 */
public class LoginActivity extends SherlockActivity {

	private EditText etEmail;
	private EditText etPassword;
	private CheckBox cbShowPassword;
	private boolean widgetsAreSet = false;
	
	/**
	 * Listener de escucha para cuando la tarea asíncrona de comprobación de credenciales termine su ejecución.<br>
	 * Si la operación ha terminado con éxito, lanza la Activity de obtención de datos del usuario.<br>
	 * En caso contrario, muestra un error por pantalla en el hilo de la interfaz de usuario.
	 */
	private OnTaskCompleted loginListener = new OnTaskCompleted() {
		@Override
		public void onTaskCompleted(Object o) {
			final Result result = (Result)o;
			//Si ha ocurrido algún error, mostramos mensaje por pantalla.
			if (result.getCode() != ResultCodes.Ok) {
				runOnUiThread(new Runnable(){
					@Override
					public void run() {
						MessagesFacade.toastLong(LoginActivity.this, result.getMessage());
					}
				});
			}
			//Sino, lanzamos activity de obtención de datos del usuario.
			else {
				SharedPreferencesFacade prefs = new SharedPreferencesFacade(LoginActivity.this);
				prefs.setUserId(Long.parseLong(result.getPayload()));
				prefs.setPassword(LoginActivity.this.etPassword.getText().toString().trim());
				prefs.setSessionStarted(true);
				ApplicationFacade.startCheckinService(LoginActivity.this);
				ApplicationFacade.registerGcm(LoginActivity.this);
				runOnUiThread(new Runnable(){
					@Override
					public void run() {
						ApplicationFacade.startActivity(LoginActivity.this, SplashActivity.class);
						LoginActivity.this.finish();
					}
				});
			}
		}		
	};
	
	/**
	 * Listener de escucha para cuando la tarea asíncrona de recordatorio de contraseña termine su ejecución.<br>
	 * Sea cual sea el resultado de la operación, muestra un mensaje explicativo por pantalla a través del hilo
	 * de la interfaz de usuario.
	 */
	private OnTaskCompleted rememberPasswordListener = new OnTaskCompleted() {
		@Override
		public void onTaskCompleted(Object o) {
			final Result result = (Result)o;
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					MessagesFacade.toastLong(LoginActivity.this, result.getMessage());
				}
			});
		}
	};
	
	/**
	 * Listener de escucha cuando el usuario activa o desactiva el <font face="courier new">CheckBox cbShowPassword</font><br><br>
	 * Establece el tipo de entrada del <font face="courier new">EditText etPassword</font> a <font face="courier new">InputType.TYPE_TEXT_VARIATION_PASSWORD</font>,
	 * si el usuario activa el <font face="courier new">CheckBox</font>, o a <font face="courier new">InputType.TYPE_CLASS_TEXT</font> si el usuario
	 * desactiva el <font face="courier new">CheckBox</font>. 
	 */
	private final OnCheckedChangeListener onShowPasswordChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton view, boolean isChecked) {
			if (isChecked) etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
			else etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.login);
		this.getSupportActionBar().setIcon(R.drawable.usefeeling_icon_transparent_background);
		this.getSupportActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.drawable.button_bar_gradient));
		this.getSupportActionBar().setDisplayShowTitleEnabled(false);
		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		this.setWidgets();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.default_actionbar_menu, menu);
        return true;
    }
	
	@SuppressLint("InlinedApi")
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		int homeId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? android.R.id.home : R.id.abs__home; 
        if (item.getItemId() == homeId) {
        	ApplicationFacade.goHome(this, MapActivity.class);
        }
        return true;
    }
	
	/**
	 * Establece los widgets de la interfaz de usuario.
	 */
	private void setWidgets() {
		if (this.widgetsAreSet) return;
		this.etEmail = (EditText)findViewById(R.id.etEmail);
		this.etPassword = (EditText)findViewById(R.id.etPassword);
		this.cbShowPassword = (CheckBox)findViewById(R.id.cbShowPassword);
		this.cbShowPassword.setOnCheckedChangeListener(this.onShowPasswordChangeListener);
		this.widgetsAreSet = true;
	}

	@Override
	public void onRestoreInstanceState(Bundle inState) {
		this.setWidgets();
		this.etEmail.setText(inState.getString(States.EMAIL));
		this.etPassword.setText(inState.getString(States.PASSWORD));
		this.cbShowPassword.setChecked(inState.getBoolean(States.SHOW_PASSWORD));
		super.onRestoreInstanceState(inState);		
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(States.EMAIL, this.etEmail.getText().toString());
		outState.putString(States.PASSWORD, this.etPassword.getText().toString());
		outState.putBoolean(States.SHOW_PASSWORD, this.cbShowPassword.isChecked());
	}
	
	/**
	 * Manejador del evento OnClick del botón de acceso a la cuenta.
	 * @param v
	 */
	public void bLogin_OnClick(View v) {
		if (v.getId() != R.id.bLogin) return;
		if (!this.checkDataToLogin()) return;
		new Login(this, this.loginListener, this.etEmail.getText().toString().trim(), this.etPassword.getText().toString().trim()).execute();
	}
	
	/**
	 * Comprueba que los datos introducidos para el acceso sean válidos.<br>
	 * Comprueba que el usuario haya introducido texto en el correo electrónico.<br>
	 * Comprueba que la contraseña introducida tenga al menos 6 caracteres.
	 * @return <b>true</b> si los datos superan estas comprobaciones básicas o <b>false</b> en caso contrario.
	 */
	private boolean checkDataToLogin() {
		if (this.etEmail.getText().toString().trim().equals("")) {
			this.etEmail.setError(getString(R.string.error_email));
			return false;
		}
		if (this.etPassword.getText().toString().trim().length() < 6) {
			this.etPassword.setError(getString(R.string.error_password));
			return false;
		}
		return true;
	}
	
	/**
	 * Manejador del evento OnClick del botón de recordatorio de contraseña.
	 * @param v
	 */
	public void bRememberPassword_OnClick(View v) {
		if (v.getId() != R.id.bRememberPassword) return;
		if (!this.checkDataRememberPassword()) return;
		new RememberPassword(this, this.rememberPasswordListener, this.etEmail.getText().toString().trim()).execute();
	}
	
	/**
	 * Comprueba que los datos introducidos para el recordatorio de contraseña sean válidos.<br>
	 * Comprueba que el usuario haya introducido texto en el correo electrónico.<br>
	 * @return <b>true</b> si los datos superan estas comprobaciones básicas o <b>false</b> en caso contrario.
	 */
	private boolean checkDataRememberPassword() {
		if (this.etEmail.getText().toString().trim().equals("")) {
			this.etEmail.setError(getString(R.string.error_email));
			return false;
		}
		return true;
	}
}
