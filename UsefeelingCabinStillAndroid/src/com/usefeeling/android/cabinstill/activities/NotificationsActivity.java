package com.usefeeling.android.cabinstill.activities;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.gson.Gson;
import com.usefeeling.android.R;
import com.usefeeling.android.cabinstill.adapters.NotificationsAdapter;
import com.usefeeling.android.cabinstill.api.Notification;
import com.usefeeling.android.cabinstill.api.Place;
import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.facades.ApplicationFacade;
import com.usefeeling.android.cabinstill.facades.MessagesFacade;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;
import com.usefeeling.android.cabinstill.tasks.GetMoreNotifications;
import com.usefeeling.android.cabinstill.values.Extras;

/**
 * Activity que muestra las notificaciones del usuario.<br>
 * Recibe en su intent de entrada un extra, de nombre Extras.NOTIFICATIONS, que contiene un ArrayList de objetos Notification, con las notificaciones a
 * visualizar.
 * @author Moisés Vilar.
 *
 */
public class NotificationsActivity extends SherlockActivity implements OnTaskCompleted {

	private ArrayList<Notification> notifications;
	private NotificationsAdapter adapter;
	private ListView listView;
	private boolean loadingMore = false;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Establecemos layout de la activity
		this.setContentView(R.layout.notifications);
		this.getSupportActionBar().setIcon(R.drawable.usefeeling_icon_transparent_background);
		this.getSupportActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.drawable.button_bar_gradient));
		this.getSupportActionBar().setDisplayShowTitleEnabled(false);
		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		//Obtenemos el listado de notificaciones de los extras del intent de entrada
		this.notifications = (ArrayList<Notification>)this.getIntent().getExtras().getSerializable(Extras.NOTIFICATIONS);
		TextView tvListViewEmpty = (TextView)this.findViewById(R.id.tvListViewEmpty);
		if (this.notifications == null || this.notifications.isEmpty()) tvListViewEmpty.setVisibility(View.VISIBLE);
		//Establecemos el listview
		this.listView = (ListView)this.findViewById(R.id.listView);
		//Construimos el adaptador
		this.adapter = new NotificationsAdapter(this, R.layout.notification_list_item, this.notifications);
		//Establecemos el adaptador en el listview
		this.listView.setAdapter(this.adapter);
		//Establecemos el manejador del evento OnScroll del listview
		try {
		this.listView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				//Si no hay ninguna notificación, salimos
				if (totalItemCount == 0) return;
				//Obtenemos la posición del último elemento en pantalla
				int lastInScreen = firstVisibleItem + visibleItemCount;
				//Si el último elemento en pantalla es el último elemento del listview y no se están cargando más notificaciones...
				if ((lastInScreen == totalItemCount) && !(loadingMore)) {
					//Cargamos más notificaciones a partir de la fecha de la última notificación almacenada
					loadingMore = true;
					long lastTimeStamp = notifications.get(notifications.size() - 1).getTimestamp(); 
					new GetMoreNotifications(NotificationsActivity.this, NotificationsActivity.this, lastTimeStamp).execute();
				}
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {}
			
		});
		} catch (Exception e) {
			Log.e("", e.getMessage());
		}
		//Establecemos el manejador del evento OnItemClick del listview
		this.listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				//Obtenemos la notificación pulsada
				Notification notification = NotificationsActivity.this.notifications.get(position);
				//Cambiamos color de fondo de la notificación
				view.setBackgroundColor(Color.WHITE);
				final Gson gson = new Gson();
				//En función del tipo de notificación...
				switch (notification.getNotificationType()) {
					//Si es una sugerencia de local, abrimos página de local.
					case Notification.NotificationTypes.PLACE_NOTIFICATION: {
						Place place = (Place)gson.fromJson(notification.getPayload(), Place.class);
						Intent i = new Intent(NotificationsActivity.this, PlacePageActivity.class);
						i.putExtra(Extras.PLACE, place);
						NotificationsActivity.this.startActivity(i);
						break;
					}
					//Si es una sugerencia de evento, abrimos página de evento.
					case Notification.NotificationTypes.EVENT_NOTIFICATION: {
						Intent i = new Intent(NotificationsActivity.this, EventPageActivity.class);
						Long eventid = notification.getActivityId();
						if (eventid == null) eventid = Long.parseLong(notification.getPayload());
						i.putExtra(Extras.EVENTID, eventid);
						NotificationsActivity.this.startActivity(i);
						break;
					}
					//Si es una sugerencia de promoción, abrimos página de promoción
					case Notification.NotificationTypes.PROMO_NOTIFICATION: {
						Intent i = new Intent(NotificationsActivity.this, PromoPageActivity.class);
						Long promoid = notification.getActivityId();
						if (promoid == null) promoid = Long.parseLong(notification.getPayload());
						i.putExtra(Extras.PROMOID, promoid);
						NotificationsActivity.this.startActivity(i);
						break;
					}
					//Si es una notificación conducida por URL, abrimos página de visualización de contenido de URL
					case Notification.NotificationTypes.URL_NOTIFICATION: {
						String url = notification.getPayload();
						Intent i = new Intent(NotificationsActivity.this, UrlPageActivity.class);
						i.putExtra(Extras.URL, url);
						NotificationsActivity.this.startActivity(i);
					}
					case Notification.NotificationTypes.SUGGESTION: {
						//Do nothing
					}
				}
			}
		});
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

	@Override
	public void onTaskCompleted(Object rawResult) {
		//Obtenemos resultado de la tarea
		Result result = (Result)((Object[])rawResult)[0];
		//Si el resultado no es correcto, sacamos mensaje por pantalla y salimos
		if (result.getCode() != ResultCodes.Ok) {
			MessagesFacade.toastLong(this, result.getMessage());
			return;
		}
		//Obtenemos la lista con las nuevas notificaciones descargadas
		@SuppressWarnings("unchecked")
		ArrayList<Notification> newNotifications = (ArrayList<Notification>)((Object[])rawResult)[1];
		if (newNotifications == null || newNotifications.isEmpty()) return;
		//Añadimos notificaciones a la lista de notificaciones actuales
		this.notifications.addAll(newNotifications);
		//Actualizamos adaptador del listview con las nuevas notificaciones
		for (Notification notification : newNotifications) {
			this.adapter.add(notification);
		}
		this.adapter.notifyDataSetChanged();
		this.loadingMore = false;
	}	
}
