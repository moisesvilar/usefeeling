package com.usefeeling.android.cabinstill.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Build;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.usefeeling.android.R;
import com.usefeeling.android.cabinstill.api.Event;
import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.facades.ApplicationFacade;
import com.usefeeling.android.cabinstill.facades.DataFacade;
import com.usefeeling.android.cabinstill.facades.DeviceFacade;
import com.usefeeling.android.cabinstill.facades.MessagesFacade;
import com.usefeeling.android.cabinstill.helpers.DateTimeHelper;
import com.usefeeling.android.cabinstill.helpers.GoogleStaticMapsHelper;
import com.usefeeling.android.cabinstill.imageloader.ImageManager;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;
import com.usefeeling.android.cabinstill.tasks.GetEvent;
import com.usefeeling.android.cabinstill.values.Extras;

/**
 * Activity de visualización de página de evento.<br>
 * Recibe en su intent de entrada un objeto de la clase Event, con nombre Extras.EVENT, que contiene la información del evento a visualizar.<br>
 * @author Moisés Vilar.
 *
 */
public class EventPageActivity extends SherlockActivity implements OnTaskCompleted {

	private ImageManager mImageManager;
	private Event event;
	private long eventid;
	private boolean mapDraw = false;
	private DataFacade dataFacade;
	
	/**
	 * Listener de escucha de reintento de descarga de datos iniciales
	 */
	private OnClickListener retryListener = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			new GetEvent(EventPageActivity.this, EventPageActivity.this, EventPageActivity.this.eventid).execute();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.event_page2);
		this.getSupportActionBar().setIcon(R.drawable.usefeeling_icon_transparent_background);
		this.getSupportActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.drawable.button_bar_gradient));
		this.getSupportActionBar().setDisplayShowTitleEnabled(false);
		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		this.dataFacade = new DataFacade(this);
		this.mImageManager = new ImageManager(this);
		this.event = (Event)this.getIntent().getExtras().getSerializable(Extras.EVENT);
		if (this.event == null) {
			this.eventid = this.getIntent().getLongExtra(Extras.EVENTID, -1);
			new GetEvent(this, this, this.eventid).execute();
		}
		else {
			this.setUI();
		}
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.event_page_actionbar_menu, menu);
        return true;
    }
	
	@SuppressLint("InlinedApi")
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		int homeId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? android.R.id.home : R.id.abs__home; 
        if (item.getItemId() == homeId) {
        	ApplicationFacade.goHome(this, MapActivity.class);
        }
        else if (item.getItemId() == R.id.itemShare) {
        	DeviceFacade.shareVenue(this, this.event.getPlace());
        }
        return true;
    }
	
	/**
	 * Establece la interfaz de usuario
	 */
	private void setUI() {
		if (this.event == null) return;
		this.mImageManager.loadBitmap(this.event.getImageUrl(), (ImageView)this.findViewById(R.id.qcbIcon), R.drawable.default_portrait, false);
		((ImageView)this.findViewById(R.id.ivAffinity)).setImageResource(this.mImageManager.getAffinityImageResId(this.event.getAffinityStr()));
		((TextView)this.findViewById(R.id.tvEventName)).setText(this.event.getName());
		((TextView)this.findViewById(R.id.tvPlaceNameEventPage)).setText(this.event.getPlace().getName());
		final ImageView ivMap = (ImageView)this.findViewById(R.id.ivEventLocation);
		ViewTreeObserver vto = ivMap.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
		    public boolean onPreDraw() {
		        int finalHeight = ivMap.getMeasuredHeight();
		        int finalWidth = ivMap.getMeasuredWidth();
		        if (!EventPageActivity.this.mapDraw) EventPageActivity.this.mImageManager.loadBitmap(GoogleStaticMapsHelper.buildUrl(EventPageActivity.this.event.getPlace().getLatitude(), EventPageActivity.this.event.getPlace().getLongitude(), true, finalWidth, finalHeight), ivMap, R.drawable.map, true);
		        EventPageActivity.this.mapDraw = true;
		        return true;
		    }
		});
		ivMap.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Double slat = EventPageActivity.this.dataFacade.getUserPosition().getLatitude();
				Double slon = EventPageActivity.this.dataFacade.getUserPosition().getLongitude();
				Double dlat = EventPageActivity.this.event.getLatitude();
				Double dlon = EventPageActivity.this.event.getLongitude();
				DeviceFacade.openGMaps(EventPageActivity.this, slat, slon, dlat, dlon);
			}
		});
		((TextView)this.findViewById(R.id.tvAddress)).setText(this.event.getPlace().getAddress());
		((TextView)this.findViewById(R.id.tvDescription)).setText(this.event.getDescription());
		Linkify.addLinks(((TextView)this.findViewById(R.id.tvDescription)), Linkify.ALL);
		if (this.event.getInitTime() != null && this.event.getInitTime() > 0) ((TextView)this.findViewById(R.id.tvSince)).setText(this.getString(R.string.from) + " " + DateTimeHelper.toString(event.getInitTime()));
		if (this.event.getEndTime() != null && this.event.getEndTime() > 0) ((TextView)this.findViewById(R.id.tvTo)).setText(this.getString(R.string.to) + " " + DateTimeHelper.toString(event.getEndTime()));
	}

	@Override
	public void onTaskCompleted(Object rawResult) {
		if (rawResult == null) {
			this.finish();
			return;
		}
		Result result = (Result)((Object[])rawResult)[0];
		if (result.getCode() != ResultCodes.Ok) {
			MessagesFacade.showDialog(this, "", result.getMessage(), MessagesFacade.MessageButtons.RETRY, this.retryListener, null);
			return;
		}
		this.event = (Event)((Object[])rawResult)[1];
		this.setUI();
	}
}
