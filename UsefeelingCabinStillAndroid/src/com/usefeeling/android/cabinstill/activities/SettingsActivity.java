package com.usefeeling.android.cabinstill.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.usefeeling.android.R;
import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.facades.ApplicationFacade;
import com.usefeeling.android.cabinstill.facades.DataFacade;
import com.usefeeling.android.cabinstill.facades.MessagesFacade;
import com.usefeeling.android.cabinstill.facades.SharedPreferencesFacade;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;
import com.usefeeling.android.cabinstill.tasks.RemoveAccount;
import com.usefeeling.android.cabinstill.tasks.SetFavPlacesNotifications;
import com.usefeeling.android.cabinstill.tasks.SetSuggestedProposalsNotifications;

/**
 * Activity de preferencias de la aplicación.
 * @author Moisés Vilar.
 *
 */
public class SettingsActivity extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener {

	private final static String CHANGE_PASSWORD_KEY = "change_password";
	private final static String EDIT_PROFILE_KEY = "edit_profile";
	private final static String DELETE_ACCOUNT_KEY = "delete_account";
	private final static String FAV_PLACES_NOTIFICATIONS_KEY = "fav_places_notifications";
	private final static String SUGGESTED_PROPOSALS_NOTIFICATIONS_KEY = "suggested_proposals_notifications";
	private final static String TUTORIAL_KEY = "tutorial";
	private final static String CONTACT_KEY = "contact";
	private final static String TOS_KEY = "tos";
	private final static String ABOUT_KEY = "about";
	private final static String CLOSE_SESSION_KEY = "close_session";
	
	private SharedPreferencesFacade prefs;
	private ProgressDialog progress;
	private DataFacade dataFacade;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			this.addPreferencesFromResource(R.xml.preferences);
			this.getListView().setBackgroundColor(this.getResources().getColor(R.color.white));
			this.getSupportActionBar().setIcon(R.drawable.usefeeling_icon_transparent_background);
			this.getSupportActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.drawable.button_bar_gradient));
			this.getSupportActionBar().setDisplayShowTitleEnabled(false);
			this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			this.prefs = new SharedPreferencesFacade(this);
			this.dataFacade = new DataFacade(this);
			CheckBoxPreference cbpFavPlacesNotifications = (CheckBoxPreference) this.findPreference(FAV_PLACES_NOTIFICATIONS_KEY);
			cbpFavPlacesNotifications.setChecked(dataFacade.getAccount().getFavouritePlacesNotifications());
			CheckBoxPreference cbpSuggestedProposalsNotifications = (CheckBoxPreference) this.findPreference(SUGGESTED_PROPOSALS_NOTIFICATIONS_KEY);
			cbpSuggestedProposalsNotifications.setChecked(dataFacade.getAccount().getSuggestedProposalsNotifications());
		} catch (Throwable t) {}
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

	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() {
		this.getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		this.finish();
		super.onPause();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		this.getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	/**
	 * Muestra la ventana de progreso
	 */
	protected void showProgress() {
		this.progress = ProgressDialog.show(this, "", getResources().getString(R.string.please_wait));
	}
	
	/**
	 * Elimina la ventana de progreso.
	 */
	protected void dismissProgress() {
		this.progress.dismiss();
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, final Preference pref) {
		if (pref == null) return false;
		if (pref.getKey().equals(CHANGE_PASSWORD_KEY)) {
			this.startActivity(new Intent(this, ChangePasswordActivity.class));
			return true;
		}
		if (pref.getKey().equals(EDIT_PROFILE_KEY)) {
			this.startActivity(new Intent(this, EditProfileActivity.class));
			return true;
		}
		if (pref.getKey().equals(CLOSE_SESSION_KEY)) {
			String description = this.getResources().getString(R.string.close_session_confirmation);
			MessagesFacade.showDialog(this, "", description, MessagesFacade.MessageButtons.YES_NO, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					SettingsActivity.this.showProgress();
					ApplicationFacade.unregisterGcm(SettingsActivity.this);
					ApplicationFacade.stopCheckinService(SettingsActivity.this);
					try { Thread.sleep(3000); } catch (InterruptedException e) {}
					SettingsActivity.this.prefs.clear();
					ApplicationFacade.kill(SettingsActivity.this);
				}
			}, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {}
			});
			return true;
		}
		if (pref.getKey().equals(DELETE_ACCOUNT_KEY)) {
			String description = this.getResources().getString(R.string.delete_account_confirmation);
			MessagesFacade.showDialog(this, "", description, MessagesFacade.MessageButtons.YES_NO,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new RemoveAccount(SettingsActivity.this, new OnTaskCompleted(){
							@Override
							public void onTaskCompleted(Object rawResult) {
								Result result = (Result)rawResult;
								if (result.getCode() != ResultCodes.Ok) {
									MessagesFacade.toastLong(SettingsActivity.this, result.getMessage());
									return;
								}
								SettingsActivity.this.prefs.clear();
								ApplicationFacade.kill(SettingsActivity.this);
							}
							
						}).execute();
					}
				},
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {}
			});
			return true;
		}
		if (pref.getKey().equals(FAV_PLACES_NOTIFICATIONS_KEY)) {
			SettingsActivity.this.showProgress();
			final boolean isChecked = ((CheckBoxPreference)pref).isChecked();
			new SetFavPlacesNotifications(this, new OnTaskCompleted(){
				@Override
				public void onTaskCompleted(Object rawResult) {
					SettingsActivity.this.dismissProgress();
					Result result = (Result)rawResult;
					if (result.getCode() != ResultCodes.Ok) {
						MessagesFacade.toastLong(SettingsActivity.this, result.getMessage());
						((CheckBoxPreference)pref).setChecked(!((CheckBoxPreference)pref).isChecked());
					}
					dataFacade.getAccount().setFavouritePlacesNotifications(isChecked);
				}
			}, isChecked).execute();
			return true;
		}
		if (pref.getKey().equals(SUGGESTED_PROPOSALS_NOTIFICATIONS_KEY)) {
			SettingsActivity.this.showProgress();
			final boolean isChecked = ((CheckBoxPreference)pref).isChecked();
			new SetSuggestedProposalsNotifications(this, new OnTaskCompleted(){
				@Override
				public void onTaskCompleted(Object rawResult) {
					SettingsActivity.this.dismissProgress();
					Result result = (Result)rawResult;
					if (result.getCode() != ResultCodes.Ok) {
						MessagesFacade.toastLong(SettingsActivity.this, result.getMessage());
						((CheckBoxPreference)pref).setChecked(!((CheckBoxPreference)pref).isChecked());
					}
					dataFacade.getAccount().setSuggestedProposalsNotifications(isChecked);
				}
			}, isChecked).execute();
			return true;
		}
		if (pref.getKey().equals(TUTORIAL_KEY)) {
			this.prefs.setFirstRunning(true);
			return true;
		}
		if (pref.getKey().equals(TOS_KEY)) {
			MessagesFacade.showTosDialog(this);
			return true;
		}
		if (pref.getKey().equals(CONTACT_KEY)) {
			ApplicationFacade.sendInfoEmail(this);
			return true;
		}
		if (pref.getKey().equals(ABOUT_KEY)) {
			this.startActivity(new Intent(this, AboutActivity.class));
			return true;
		}
		return false;
	}

	
}
