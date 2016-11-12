package com.usefeeling.android.cabinstill.activities;

import java.util.List;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.usefeeling.android.R;
import com.usefeeling.android.cabinstill.adapters.PromosAdapter;
import com.usefeeling.android.cabinstill.api.Event;
import com.usefeeling.android.cabinstill.api.Promo;
import com.usefeeling.android.cabinstill.facades.ApplicationFacade;
import com.usefeeling.android.cabinstill.facades.DataFacade;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;

public class PromosActivity extends SherlockActivity implements OnTaskCompleted {

	private DataFacade mDataFacade;
	private List<Promo> mPromos;
	private PromosAdapter mAdapter;
	private ListView listView;
	private Menu mActionBarMenu;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.promos);
		this.getSupportActionBar().setIcon(R.drawable.usefeeling_icon_transparent_background);
		this.getSupportActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.drawable.button_bar_gradient));
		this.getSupportActionBar().setDisplayShowTitleEnabled(false);
		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		this.mDataFacade = new DataFacade(this);
		this.mDataFacade.getPromos(this, false);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.promos_actionbar_menu, menu);
        MenuItem item = menu.findItem(R.id.itemUpdate);
        item.setEnabled(false);
        this.mActionBarMenu = menu;
        return true;
    }
	
	@SuppressLint("InlinedApi")
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		int homeId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? android.R.id.home : R.id.abs__home; 
        if (item.getItemId() == homeId) {
        	ApplicationFacade.goHome(this, MapActivity.class);
        }
        else if (item.getItemId() == R.id.itemUpdate) {
        	item.setEnabled(false);
        	this.mAdapter.clear();
        	this.mAdapter.notifyDataSetChanged();
        	this.mPromos.clear();
        	((TextView)this.findViewById(R.id.tvWorking)).setVisibility(View.VISIBLE);
        	this.mDataFacade.getPromos(this, true);
        }
        return true;
    }

	@SuppressWarnings("unchecked")
	@Override
	public void onTaskCompleted(Object result) {
		if (this.mPromos == null) this.mPromos = (List<Promo>) result;
		else this.mPromos.addAll((List<Promo>)result);
		if (this.mAdapter == null) {
			listView = (ListView)this.findViewById(R.id.listview);
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					ApplicationFacade.showEventActivity(PromosActivity.this, (Event)parent.getItemAtPosition(position));
				}
			});
			this.mAdapter = new PromosAdapter(this, R.layout.promo_listitem, this.mPromos);
			listView.setAdapter(this.mAdapter);
		}
		//Ocultamos TextView de Cargando
		((TextView)this.findViewById(R.id.tvWorking)).setVisibility(View.GONE);
		if (this.mPromos.isEmpty()) ((TextView)this.findViewById(R.id.tvNoElements)).setVisibility(View.VISIBLE);
		if (this.mActionBarMenu != null) {
			MenuItem item = mActionBarMenu.findItem(R.id.itemUpdate);
	        item.setEnabled(true);
		}
	}

}
