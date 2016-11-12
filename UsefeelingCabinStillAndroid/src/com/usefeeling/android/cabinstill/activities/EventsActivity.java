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
import com.usefeeling.android.cabinstill.adapters.EventsAdapter;
import com.usefeeling.android.cabinstill.api.Event;
import com.usefeeling.android.cabinstill.facades.ApplicationFacade;
import com.usefeeling.android.cabinstill.facades.DataFacade;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;

public class EventsActivity extends SherlockActivity implements OnTaskCompleted {

	private DataFacade mDataFacade;
	private List<Event> mEvents;
	private EventsAdapter mAdapter;
	private ListView listView;
	private Menu mActionBarMenu;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.events);
		this.getSupportActionBar().setIcon(R.drawable.usefeeling_icon_transparent_background);
		this.getSupportActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.drawable.button_bar_gradient));
		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		this.getSupportActionBar().setDisplayShowTitleEnabled(false);
		this.mDataFacade = new DataFacade(this);
		this.mDataFacade.getEvents(this, false);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.events_actionbar_menu, menu);
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
        	this.mEvents.clear();
        	((TextView)this.findViewById(R.id.tvWorking)).setVisibility(View.VISIBLE);
        	this.mDataFacade.getEvents(this, true);
        }
        return true;
    }

	@SuppressWarnings("unchecked")
	@Override
	public void onTaskCompleted(Object result) {
		if (this.mEvents == null) this.mEvents = (List<Event>) result;
		else this.mEvents.addAll((List<Event>)result);
		if (this.mAdapter == null) {
			listView = (ListView)this.findViewById(R.id.listview);
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					ApplicationFacade.showEventActivity(EventsActivity.this, (Event)parent.getItemAtPosition(position));
				}
			});
			this.mAdapter = new EventsAdapter(this, R.layout.event_listitem, this.mEvents);
			listView.setAdapter(this.mAdapter);
		}
		//Ocultamos TextView de Cargando
		((TextView)this.findViewById(R.id.tvWorking)).setVisibility(View.GONE);
		if (this.mEvents.isEmpty()) ((TextView)this.findViewById(R.id.tvNoElements)).setVisibility(View.VISIBLE);
		if (this.mActionBarMenu != null) {
			MenuItem item = mActionBarMenu.findItem(R.id.itemUpdate);
	        item.setEnabled(true);
		}
	}

}
