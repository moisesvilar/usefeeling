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
import com.usefeeling.android.cabinstill.tasks.ChangePassword;
import com.usefeeling.android.cabinstill.values.States;

/**
 * Activity de cambio de contraseña.
 * @author Moisés Vilar.
 *
 */
public class ChangePasswordActivity extends SherlockActivity implements OnTaskCompleted {

	private EditText etOldPassword;
	private EditText etNewPassword;
	private CheckBox cbShowPassword;
	private String newPassword;
	private String oldPassword;
	
	/**
	 * Listener de escucha cuando el usuario activa o desactiva el <font face="courier new">CheckBox cbShowPassword</font><br><br>
	 * Establece el tipo de entrada del <font face="courier new">EditText etPassword</font> a <font face="courier new">InputType.TYPE_TEXT_VARIATION_PASSWORD</font>,
	 * si el usuario activa el <font face="courier new">CheckBox</font>, o a <font face="courier new">InputType.TYPE_CLASS_TEXT</font> si el usuario
	 * desactiva el <font face="courier new">CheckBox</font>. 
	 */
	private final OnCheckedChangeListener onShowPasswordChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton view, boolean isChecked) {
			if (isChecked) {
				etOldPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
				etNewPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
			}
			else {
				etOldPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				etNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.change_password);
		this.getSupportActionBar().setIcon(R.drawable.usefeeling_icon_transparent_background);
		this.getSupportActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.drawable.button_bar_gradient));
		this.getSupportActionBar().setDisplayShowTitleEnabled(false);
		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		this.setUi();
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
	 * Establece la interfaz de usuario.
	 */
	private void setUi() {
		this.etOldPassword = (EditText)this.findViewById(R.id.etOldPassword);
		this.etNewPassword = (EditText)this.findViewById(R.id.etNewPassword);
		this.cbShowPassword = (CheckBox)this.findViewById(R.id.cbShowPassword);
		this.cbShowPassword.setOnCheckedChangeListener(this.onShowPasswordChangeListener);
		if (this.newPassword != null) this.etNewPassword.setText(newPassword);
		if (this.oldPassword != null) this.etOldPassword.setText(oldPassword);
	}

	@Override
	public void onRestoreInstanceState(Bundle inState) {
		super.onRestoreInstanceState(inState);
		this.etOldPassword = (EditText)this.findViewById(R.id.etOldPassword);
		this.etNewPassword = (EditText)this.findViewById(R.id.etNewPassword);
		this.cbShowPassword = (CheckBox)this.findViewById(R.id.cbShowPassword);
		this.etOldPassword.setText(inState.getString(States.OLD_PASSWORD));
		this.etNewPassword.setText(inState.getString(States.NEW_PASSWORD));
		this.cbShowPassword.setChecked(inState.getBoolean(States.SHOW_PASSWORD));
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString(States.OLD_PASSWORD, this.etOldPassword.getText().toString().trim());
		outState.putString(States.NEW_PASSWORD, this.etNewPassword.getText().toString().trim());
		outState.putBoolean(States.SHOW_PASSWORD, this.cbShowPassword.isChecked());
		super.onSaveInstanceState(outState);
	}
	
	/**
	 * Manejador del evento OnClick del botón de cambio de contraseña.
	 * @param v
	 */
	public void bChangePassword_OnClick(View v) {
		if (v.getId() != R.id.bChangePassword) return;
		newPassword = this.etNewPassword.getText().toString().trim();
		oldPassword = this.etOldPassword.getText().toString().trim();
		boolean isRight = true;
		if (newPassword.equals("")) {
			this.etNewPassword.setError(this.getResources().getString(R.string.new_password_error));
			isRight = false;
		}
		else if (newPassword.length()<6) {
			this.etNewPassword.setError(this.getResources().getString(R.string.new_password_length_error));
			isRight = false;
		}
		else if (newPassword.equals(oldPassword)) {
			this.etNewPassword.setError(this.getResources().getString(R.string.passwords_are_equals_error));
			isRight = false;
		}
		
		if (oldPassword.equals("")) {
			this.etOldPassword.setError(this.getResources().getString(R.string.old_password_error));
			isRight = false;
		}
		else if (oldPassword.length()<6) {
			this.etOldPassword.setError(this.getResources().getString(R.string.old_password_length_error));
			isRight = false;
		}
		if (isRight) {
			new ChangePassword(this, this, oldPassword, newPassword).execute();
		}
	}

	@Override
	public void onTaskCompleted(Object rawResult) {
		this.setContentView(R.layout.change_password);
		this.setUi();
		Result result = (Result)rawResult;
		if (result.getCode() != ResultCodes.Ok) {
			MessagesFacade.toastLong(this, result.getMessage());
			return;
		}
		SharedPreferencesFacade prefs = new SharedPreferencesFacade(this);
		prefs.setPassword(newPassword);
		MessagesFacade.toastLong(this, this.getString(R.string.password_changed_successfully));
		this.finish();
	}	
}
