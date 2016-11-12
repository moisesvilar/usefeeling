package com.usefeeling.android.cabinstill.activities;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.usefeeling.android.R;
import com.usefeeling.android.cabinstill.facades.ApplicationFacade;
import com.usefeeling.android.cabinstill.facades.MessagesFacade;
import com.usefeeling.android.cabinstill.facades.SharedPreferencesFacade;

/**
 * Actividad que muestra información acerca de UseFeeling.
 * @author Moisés Vilar.
 *
 */
public class AboutActivity extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.about);
		this.getSupportActionBar().setIcon(R.drawable.usefeeling_icon_transparent_background);
		this.getSupportActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.drawable.button_bar_gradient));
		this.getSupportActionBar().setDisplayShowTitleEnabled(false);
		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		TextView tvVersion = (TextView)this.findViewById(R.id.tvVersion);
		tvVersion.setText(ApplicationFacade.getVersionName(this));
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
	 * Manejador del evento OnClick del textView tvDescripcion. Muestra el identificador del usuario.
	 * @param v
	 */
	public void tvDescription_OnClick(View v) {
		if (v.getId() != R.id.tvDescription) return;
		SharedPreferencesFacade prefs = new SharedPreferencesFacade(this);
		MessagesFacade.toastLong(this, prefs.getUserId().toString());
	}

}
