package com.usefeeling.android.cabinstill.activities;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.usefeeling.android.R;
import com.usefeeling.android.cabinstill.adapters.PlacesAdapter;
import com.usefeeling.android.cabinstill.api.Place;
import com.usefeeling.android.cabinstill.facades.ApplicationFacade;
import com.usefeeling.android.cabinstill.facades.DataFacade;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;

/**
 * Activity de la ventana de exploración de locales.
 * @author Moisés Vilar.
 *
 */
public class PlacesActivity extends SherlockActivity implements OnTaskCompleted {
	
	private DataFacade mDataFacade;
	private List<Place> mPlaces;
	private PlacesAdapter mAdapter;
	private Spinner spByDistance;
	private Spinner spByCity;
	private ListView listView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.places);
		this.getSupportActionBar().setIcon(R.drawable.usefeeling_icon_transparent_background);
		this.getSupportActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.drawable.button_bar_gradient));
		this.getSupportActionBar().setDisplayShowTitleEnabled(false);
		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		this.mDataFacade = new DataFacade(this);
		this.mDataFacade.getPlacesOrderByAffinity(this);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.places_actionbar_menu, menu);
        return true;
    }
	
	@SuppressLint("InlinedApi")
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		int homeId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? android.R.id.home : R.id.abs__home; 
        if (item.getItemId() == homeId) {
        	ApplicationFacade.goHome(this, MapActivity.class);
        }
        else if (item.getItemId() == R.id.itemSearch) {
        	final Dialog window = new Dialog(this);
    		window.setContentView(R.layout.venues_search);
    		window.setTitle(this.getString(R.string.venues_search_text));
    		AutoCompleteTextView actvSearch = (AutoCompleteTextView)window.findViewById(R.id.actvSearch);
    		actvSearch.setAdapter(new ArrayAdapter<Place>(this, R.layout.autocompletetextview_list_item, this.mPlaces));
    		actvSearch.setOnItemClickListener(new OnItemClickListener() {
    			@Override
    			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    				window.dismiss();
    				ApplicationFacade.showPlaceActivity(PlacesActivity.this, (Place)parent.getItemAtPosition(position));
    			}
    		});
    		window.show();
        }
        return true;
    }

	@SuppressWarnings("unchecked")
	@Override
	public void onTaskCompleted(Object result) {
		this.mPlaces = (List<Place>) result;
		this.mAdapter = new PlacesAdapter(this, R.layout.place_list_item, this.mPlaces);
		listView = (ListView)this.findViewById(R.id.listview);
		listView.setAdapter(this.mAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ApplicationFacade.showPlaceActivity(PlacesActivity.this, (Place)parent.getItemAtPosition(position));
			}
		});
		//Ocultamos TextView de Cargando
		((TextView)this.findViewById(R.id.tvWorking)).setVisibility(View.GONE);
		//Visualizamos y establecemos manejadores para el Spinner de filtrado por distancia
		this.spByDistance = (Spinner)this.findViewById(R.id.spByDistance);
		this.spByDistance.setVisibility(View.VISIBLE);
		this.spByDistance.setAdapter(ArrayAdapter.createFromResource(this, R.array.distance_options, R.layout.spinner_item));
		((ArrayAdapter<String>)this.spByDistance.getAdapter()).setDropDownViewResource(R.layout.spinner_item);
		this.spByDistance.post(new Runnable() {
			@Override
			public void run() {
				spByDistance.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
						PlacesActivity.this.mAdapter.clear();
						PlacesActivity.this.mAdapter.notifyDataSetChanged();
						((TextView)PlacesActivity.this.findViewById(R.id.tvWorking)).setVisibility(View.VISIBLE);
						double lat = PlacesActivity.this.mDataFacade.getUserPosition().getLatitude();
						double lon = PlacesActivity.this.mDataFacade.getUserPosition().getLongitude();
						String selection = (String)parent.getItemAtPosition(position);
						if (selection.startsWith("-")) {
							PlacesActivity.this.mDataFacade.getPlacesOrderByAffinity(onPlacesChangedListener);
						} else {
							float radius = Float.parseFloat(selection.substring(0, selection.indexOf(" ")).trim()) * 1000;
							PlacesActivity.this.mDataFacade.getNearPlaces((float)lat, (float)lon, radius, onPlacesChangedListener);
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {}
				});
			}
		});
		//Visualizamos, rellenamos y establecemos manejadores para el Spinner de filtrado por ciudad
		this.spByCity = (Spinner)this.findViewById(R.id.spByCity);
		this.spByCity.setVisibility(View.VISIBLE);
		
		this.spByCity.setAdapter(new ArrayAdapter<CharSequence>(this, R.layout.spinner_item, this.mDataFacade.getCityOptions()));
		((ArrayAdapter<CharSequence>)this.spByCity.getAdapter()).setDropDownViewResource(R.layout.spinner_item);
		this.spByCity.post(new Runnable() {
			@Override
			public void run() {
				spByCity.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
						PlacesActivity.this.mAdapter.clear();
						PlacesActivity.this.mAdapter.notifyDataSetChanged();
						((TextView)PlacesActivity.this.findViewById(R.id.tvWorking)).setVisibility(View.VISIBLE);
						String selection = (String)parent.getItemAtPosition(position);
						if (selection.equals("-")) {
							PlacesActivity.this.mDataFacade.getPlacesOrderByAffinity(onPlacesChangedListener);
						} else {
							PlacesActivity.this.mDataFacade.getPlacesByCityOrderByAffinity(selection, onPlacesChangedListener);
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {}
				});
			}
		});
	}
	
	/**
	 * Listener de escucha cuando cambian los elementos del ListView.
	 */
	private OnTaskCompleted onPlacesChangedListener = new OnTaskCompleted() {
		@SuppressWarnings("unchecked")
		@Override
		public void onTaskCompleted(Object result) {
			PlacesActivity.this.mAdapter.notifyDataSetInvalidated();
			PlacesActivity.this.mAdapter.clear();
			PlacesActivity.this.mAdapter.addAll((List<Place>)result);
			PlacesActivity.this.mAdapter.notifyDataSetChanged();
			((TextView)PlacesActivity.this.findViewById(R.id.tvWorking)).setVisibility(View.GONE);
		}
	};
}
