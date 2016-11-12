package com.usefeeling.android.cabinstill.activities;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.usefeeling.android.R;
import com.usefeeling.android.cabinstill.adapters.PlacePageAdapter;
import com.usefeeling.android.cabinstill.api.Place;
import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.facades.ApplicationFacade;
import com.usefeeling.android.cabinstill.facades.DataFacade;
import com.usefeeling.android.cabinstill.facades.DeviceFacade;
import com.usefeeling.android.cabinstill.facades.MessagesFacade;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;
import com.usefeeling.android.cabinstill.tasks.GetPlaceFromCache;
import com.usefeeling.android.cabinstill.values.Extras;
import com.viewpagerindicator.TitlePageIndicator;

/**
 * Activity para la pantalla de página de local.<br>
 * En su intent de entrada se le debe pasar un extra, de nombre Extras.PLACE, con un objecto de la clase Place que contenga la información del local
 * a visualizar.
 * @author Moisés Vilar.
 *
 */
public class PlacePageActivity extends SherlockActivity implements OnTaskCompleted {
	private ViewPager pager;
	private PlacePageAdapter adapter;
	private Place mPlace;
	private DataFacade dataFacade;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.place_page);
		this.getSupportActionBar().setIcon(R.drawable.usefeeling_icon_transparent_background);
		this.getSupportActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.drawable.button_bar_gradient));
		this.getSupportActionBar().setDisplayShowTitleEnabled(false);
		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		this.dataFacade = new DataFacade(this);
		//Obtenemos el título de notificación
		if (this.getIntent().getExtras().getString(Extras.NOTIFICATION_TITLE) != null) {
			String notificationTitle = this.getIntent().getExtras().getString(Extras.NOTIFICATION_TITLE);
			((TextView)this.findViewById(R.id.tvNotificationTitle)).setText(notificationTitle);
			((TextView)this.findViewById(R.id.tvNotificationTitle)).setVisibility(View.VISIBLE);
		}
		//Obtenemos el objeto Place de entrada
		this.mPlace = (Place)this.getIntent().getSerializableExtra(Extras.PLACE);
		if (this.mPlace == null) {
			Long placeid = this.getIntent().getLongExtra(Extras.PLACEID, -1);
			new GetPlaceFromCache(this, this, placeid).execute();
		} else this.setUI();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.place_page_actionbar_menu, menu);
        MenuItem item = menu == null ? null : menu.findItem(R.id.itemMarkAsFavorite);
        if (this.mPlace != null && item != null && this.mPlace.isFavorite()) item.setIcon(R.drawable.star_filled);
        return true;
    }
	
	@SuppressLint("InlinedApi")
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		int homeId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? android.R.id.home : R.id.abs__home; 
        if (item.getItemId() == homeId) {
        	ApplicationFacade.goHome(this, MapActivity.class);
        }
        else if (item.getItemId() == R.id.itemMarkAsFavorite) {
            dataFacade.markAsFavorite(this, mPlace, item);
        } else if (item.getItemId() == R.id.itemShare) {
        	DeviceFacade.shareVenue(this, this.mPlace);
        }
        return true;
    }
	
	/**
	 * Establece la interfaz de usuario
	 */
	private void setUI() {
		//Establecemos el adaptador para el ViewPager que mostrará los datos del local
		this.adapter = new PlacePageAdapter(this, mPlace);
		this.pager = (ViewPager)this.findViewById(R.id.pager);
		this.pager.setAdapter(this.adapter);
		//Establecemos el formato del TitlePageIndicator
		TitlePageIndicator titleIndicator = (TitlePageIndicator)this.findViewById(R.id.indicator);
		titleIndicator.setViewPager(this.pager);
		titleIndicator.setTextColor(Color.BLACK);
		titleIndicator.setBackgroundColor(this.getResources().getColor(R.color.usefeeling_green));
	}

	@Override
	public void onTaskCompleted(Object rawResult) {
		//Obtenemos resultado de la tarea
		Result result = (Result)((Object[])rawResult)[0];
		//Si el resultado no es correcto, sacamos mensaje por pantalla y salimos
		if (result.getCode() != ResultCodes.Ok) {
			MessagesFacade.toastLong(PlacePageActivity.this, result.getMessage());
			return;
		}
		this.mPlace = (Place)((Object[])rawResult)[1];
		this.setUI();
	}

}
