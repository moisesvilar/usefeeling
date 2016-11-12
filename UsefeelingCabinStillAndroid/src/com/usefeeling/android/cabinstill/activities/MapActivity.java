package com.usefeeling.android.cabinstill.activities;

import java.util.ArrayList;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.usefeeling.android.R;
import com.usefeeling.android.cabinstill.api.Notification;
import com.usefeeling.android.cabinstill.api.Place;
import com.usefeeling.android.cabinstill.api.Proposal;
import com.usefeeling.android.cabinstill.api.Proposal.ProposalTypes;
import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.facades.ApplicationFacade;
import com.usefeeling.android.cabinstill.facades.DataFacade;
import com.usefeeling.android.cabinstill.facades.MessagesFacade;
import com.usefeeling.android.cabinstill.helpers.GeoHelper;
import com.usefeeling.android.cabinstill.helpers.Position;
import com.usefeeling.android.cabinstill.interfaces.Mappable;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;
import com.usefeeling.android.cabinstill.map.UfItemizedOverlay;
import com.usefeeling.android.cabinstill.map.UfOverlayItem;
import com.usefeeling.android.cabinstill.markerloader.MarkerManager;
import com.usefeeling.android.cabinstill.tasks.GetMapData;
import com.usefeeling.android.cabinstill.values.Extras;
import com.usefeeling.android.cabinstill.widgets.TransparentPanel;

/**
 * Mapa.
 * @author Moisés Vilar.
 *
 */
public class MapActivity extends SlidingFragmentActivity {
	
	private MapView mapView;
	private MarkerManager markerManager;
	private DataFacade dataFacade;
	public static UfOverlayItem currentItem;
	public static Mappable currentMappable;
	private ArrayList<UfItemizedOverlay> overlays;
	private Animation animHide;
	private TransparentPanel panel;
	private SlidingMenu slidingMenu;
	private Menu actionBarMenu;
	private static final int SPAN_OFFSET = 0;
	
	/**
	 * Posiciones de capas sobre mapa.
	 * @author Moisés Vilar.
	 *
	 */
	private class OverlaysPositions {
		public static final int EVERYTHING = 4;
		public static final int FAMOUS = 3;
		public static final int LATER = 2;
		public static final int TODAY = 1;
		public static final int NOW = 0;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.map3);
		this.setBehindContentView(R.layout.transparent_layout);
		this.getSupportActionBar().setIcon(R.drawable.usefeeling_icon_transparent_background);
		this.getSupportActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.drawable.button_bar_gradient));
		this.getSupportActionBar().setDisplayShowTitleEnabled(false);
		this.markerManager = new MarkerManager(this);
		this.dataFacade = new DataFacade(this);
		this.setWidgets();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.map_actionbar_menu, menu);
        this.actionBarMenu = menu;
        return true;
    }
	
	@SuppressLint("InlinedApi")
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		int homeId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? android.R.id.home : R.id.abs__home; 
        if (item.getItemId() == homeId) this.slidingMenu.toggle();
        else if (item.getItemId() == R.id.itemNotifications) this.showNotificationsWindow();
        else if (item.getItemId() == R.id.itemLayers) this.showFilterWindow();
        else if (item.getItemId() == R.id.itemCenter) this.centerMapInUserPosition();
        else return super.onOptionsItemSelected(item);
        return true;
    }
	
	/**
	 * Establece la interfaz de usuario.
	 */
	private void setWidgets() {
		this.setTopLevelLayout();
		this.setMapView();
		this.setTransparentPanel();
		this.setSlidingMenu();
	}
	
	/**
	 * Set the top level layout.
	 */
	private void setTopLevelLayout() {
		final View topLevelLayout = this.findViewById(R.id.top_layout);
		if (!this.dataFacade.isFirstRunning()) topLevelLayout.setVisibility(View.INVISIBLE);
		topLevelLayout.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				topLevelLayout.setVisibility(View.INVISIBLE);
				MapActivity.this.dataFacade.setFirstRunning(true);
				return false;
			}
		});
	}
	
	/**
	 * Establece el menú lateral
	 */
	private void setSlidingMenu() {
		this.slidingMenu = new SlidingMenu(this);
        slidingMenu.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
        slidingMenu.setShadowDrawable(R.drawable.slidingmenu_shadow);
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingMenu.setFadeDegree(0.35f);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        slidingMenu.setMenu(R.layout.slidingmenu);
        this.setSlidingActionBarEnabled(false);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	/**
	 * Establece el panel transparente de información
	 */
	private void setTransparentPanel() {
		this.panel = (TransparentPanel)findViewById(R.id.transparentPanel);
		if (this.panel != null) {
			this.animHide = AnimationUtils.loadAnimation( this, R.anim.popup_hide);
			this.panel.setVisibility(View.GONE);
		}
	}
	
	/**
	 * Muestra la ventana de filtros de mapa.
	 */
	private void showFilterWindow() {
		final Dialog window = new Dialog(this);
		window.setContentView(R.layout.map_filter);
		((TextView)window.findViewById(R.id.ctvNow)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MapActivity.this.toggleOverlay(MapActivity.OverlaysPositions.NOW);
				window.dismiss();
			}
		});
		((TextView)window.findViewById(R.id.ctvToday)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MapActivity.this.toggleOverlay(MapActivity.OverlaysPositions.TODAY);
				window.dismiss();
			}
		});
		((TextView)window.findViewById(R.id.ctvLater)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MapActivity.this.toggleOverlay(MapActivity.OverlaysPositions.LATER);
				window.dismiss();
			}
		});
		((TextView)window.findViewById(R.id.ctvFamous)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MapActivity.this.toggleOverlay(MapActivity.OverlaysPositions.FAMOUS);
				window.dismiss();
			}
		});
		((TextView)window.findViewById(R.id.ctvEverything)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MapActivity.this.toggleOverlay(MapActivity.OverlaysPositions.EVERYTHING);
				window.dismiss();
			}
		});
		window.show();
	}
	
	/**
	 * Activa una capa en el mapa.
	 * @param position Posición de la capa.
	 */
	private void toggleOverlay(int position) {
		this.mapView.getOverlays().clear();
		this.mapView.getOverlays().add(this.overlays.get(position).getOverlay());
		this.mapView.postInvalidate();
		int drawableId = R.drawable.ic_actionbarmenuitem_now;
		int stringId = R.string.what_you_wanna_do_now; 
		switch (position) {
		case (MapActivity.OverlaysPositions.NOW):
			drawableId = R.drawable.ic_actionbarmenuitem_now;
			stringId = R.string.what_you_wanna_do_now;
			break;
		case (MapActivity.OverlaysPositions.TODAY):
			drawableId = R.drawable.ic_actionbarmenuitem_today;
			stringId = R.string.what_you_wanna_do_today;
			break;
		case (MapActivity.OverlaysPositions.LATER):
			drawableId = R.drawable.ic_actionbarmenuitem_later;
			stringId = R.string.what_you_wanna_do_later;
			break;
		case (MapActivity.OverlaysPositions.FAMOUS):
			drawableId = R.drawable.ic_actionbarmenuitem_famous;
			stringId = R.string.only_the_most_famous;
			break;
		case (MapActivity.OverlaysPositions.EVERYTHING):
			drawableId = R.drawable.ic_actionbarmenuitem_everything;
			stringId = R.string.show_me_everything;
			break;
		}
		this.spanZoom(this.overlays.get(position));
		this.actionBarMenu.findItem(R.id.itemLayers).setIcon(drawableId);
		MessagesFacade.toastLong(this, this.getString(stringId));
	}
	
	/**
	 * Establece el mapa.
	 */
	private void setMapView() {
		this.mapView = (MapView)findViewById(R.id.mapview);
		this.mapView.setClickable(true);
		this.mapView.getController().setZoom(15);
		this.mapView.setBuiltInZoomControls(false);
		this.mapView.setMultiTouchControls(true);
		this.mapView.getOverlays().clear();
		this.mapView.getController().setCenter(new GeoPoint(dataFacade.getUserPosition().getLatitude(), dataFacade.getUserPosition().getLongitude()));
		this.mapView.postInvalidate();
		this.startProgress();
		new GetMapData(this, new OnTaskCompleted() {
			@SuppressWarnings("unchecked")
			@Override
			public void onTaskCompleted(Object rawResult) {
				MapActivity.this.stopProgress();
				Result result = (Result)((Object[])rawResult)[0];
				if (result.getCode() != ResultCodes.Ok) {
					MessagesFacade.toastLong(MapActivity.this, result.getMessage());
					return;
				}
				MapActivity.this.overlays = (ArrayList<UfItemizedOverlay>) ((Object[])rawResult)[1];
				MapActivity.this.setActiveOverlay();
			}
		}, this.mapView, this.markerManager).execute();
	}
	
	/**
	 * Activa la barra de progreso
	 */
	private void startProgress() {
		((RelativeLayout)findViewById(R.id.tpProgress)).setVisibility(View.VISIBLE);
		((ProgressBar) this.findViewById(R.id.progress)).setVisibility(View.VISIBLE);
	}
	
	/**
	 * Desactiva la barra de progreso.
	 */
	private void stopProgress() {
		((RelativeLayout)findViewById(R.id.tpProgress)).setVisibility(View.GONE);
		((ProgressBar) this.findViewById(R.id.progress)).setVisibility(View.GONE);
	}
	
	/**
	 * Set the active overlay in map depending on the number of proposals and set the right icon in actionbar menu item.
	 */
	private void setActiveOverlay() {
		for (int i=0; i<this.overlays.size(); i++) {
			UfItemizedOverlay overlay = this.overlays.get(i);
			if (overlay.getOverlay().size() > 0) {
				this.toggleOverlay(i);
				this.spanZoom(overlay);
				return;
			}
		}
	}
	
	/**
	 * Span zoom to show the nearest proposal into the map.
	 * @param overlay Overlay with the proposals
	 */
	private void spanZoom(UfItemizedOverlay overlay) {
		double distance = -1.0;
		int index = -1;
		for (int i=0; i<overlay.getOverlay().size(); i++) {
			Mappable item = overlay.getOverlay().getItem(i).getMappable();
			int auxDistance = GeoHelper.distance(item.getLatitude(), item.getLongitude(), this.dataFacade.getUserPosition().getLatitude(), this.dataFacade.getUserPosition().getLongitude());
			if (i == 0) {
				distance = auxDistance;
			}
			else if (i == overlay.getOverlay().size() - 1) {
				continue;
			}
			else if (auxDistance < distance) {
				distance = auxDistance;
				index = i;
			}
		}
		if (index == -1) return;
		double dLatitudeSpan = Math.abs(this.dataFacade.getUserPosition().getLatitude() - overlay.getOverlay().getItem(index).getMappable().getLatitude());
		double dLongitudeSpan = Math.abs(this.dataFacade.getUserPosition().getLongitude() - overlay.getOverlay().getItem(index).getMappable().getLongitude());
		int iLatitudeSpan = 2*(SPAN_OFFSET + (int)(dLatitudeSpan * 1E6));
		int iLongitudeSpan = 2*(SPAN_OFFSET + (int)(dLongitudeSpan * 1E6));
		this.mapView.getController().zoomToSpan(iLatitudeSpan, iLongitudeSpan);
		this.mapView.getController().setCenter(new GeoPoint(dataFacade.getUserPosition().getLatitude(), dataFacade.getUserPosition().getLongitude()));
	}
	
	/**
	 * Manejador del evento onKeyDown, que se lanza cuando se presiona una tecla.<br><br>
	 * En la reescritura de este método, se añade funcionalidad al presionado del botón <i>BACK</i>,
	 * cerrando la ventana transparente de información si es que se encuentra visible.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//Botón BACK
		if(keyCode == KeyEvent.KEYCODE_BACK){
			//Si se está mostrando el panel transparente, lo ocultamos
			if (this.panel != null && this.panel.getVisibility() != View.GONE) {
				this.panel.startAnimation(animHide);
				this.panel.setVisibility(View.GONE);
			}
			//Si se está mostrando el menú lateral, lo ocultamos
			else if (this.slidingMenu.isMenuShowing()) {
				this.slidingMenu.toggle();
			}
			//Si no se está mostrando nada, mostramos mensaje de confirmación de salir de la aplicación
			else {
				MessagesFacade.showDialog(this, "", this.getString(R.string.quit_confirmation), MessagesFacade.MessageButtons.YES_NO, new OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						ApplicationFacade.kill(MapActivity.this);
					}
				}, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {}
				});
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
    public void onBackPressed() {
        if ( slidingMenu.isMenuShowing()) slidingMenu.toggle();
        else super.onBackPressed();
    }
	
	/**
	 * Centra el mapa en la posición del usuario
	 * @param v
	 */
	public void centerMapInUserPosition() {
		Position userPosition = this.dataFacade.getUserPosition();
		this.mapView.getController().animateTo(userPosition.getLatitude(), userPosition.getLongitude());
		MessagesFacade.toastLong(this, this.getString(R.string.your_position));
	}
	
	/**
	 * Muestra la ventana de notificaciones
	 */
	public void showNotificationsWindow() {
		ArrayList<Notification> notifications = this.dataFacade.getNotifications();
		Intent i = new Intent(this, NotificationsActivity.class);
		i.putExtra(Extras.NOTIFICATIONS, notifications);
		this.startActivity(i);
	}
	
	/**
	 * Manejador del evento OnClick sobre el TransparentPanel.
	 * @param v
	 */
	public void transparentPanel_OnClick(View v) {
		if (v.getId() != R.id.transparentPanel) return;
		if (currentItem == null && currentMappable == null) return;
		//Obtenemos el mappable del elemento actual seleccionado en el mapa
		final Mappable mappable = currentItem == null ? currentMappable : currentItem.getMappable();
		//Si el elemento seleccionado es un evento...
		if (mappable instanceof Proposal && ((Proposal) mappable).getType() == ProposalTypes.EVENT) {
			this.showEventScreen(mappable);
		}
		//Si el elemento seleccionado es una promoción...
		else if (mappable instanceof Proposal && ((Proposal) mappable).getType() == ProposalTypes.PROMO) {
			this.showPromoScreen(mappable);
		}
		//Si el elemento seleccionado es un local...
		else if (mappable instanceof Place) {
			this.showPlaceScreen(mappable);
		}
		//Si el elemento seleccionado es la posición del usuario...
		else if (mappable instanceof Position) {
			this.showUserProfile();
		}
	}

	/**
	 * Shows the user's profile page.
	 */
	private void showUserProfile() {
		ApplicationFacade.showUserProfileActivity(this, this.dataFacade.getCurrentUser());
	}
	
	/**
	 * Muestra la página de local.
	 * @param mappable Información del local.
	 */
	private void showPlaceScreen(final Mappable mappable) {
		//Lanzamos activity de página de local.
		Intent i = new Intent(MapActivity.this, PlacePageActivity.class);
		i.putExtra(Extras.PLACE, ((Place)mappable));
		MapActivity.this.startActivity(i);	
	}

	/**
	 * Muestra la ventana de promociones.
	 * @param mappable Información de la promoción.
	 */
	private void showPromoScreen(Mappable mappable) {
		//Lanzamos activity de página de promoción.
		Intent i = new Intent(MapActivity.this, PromoPageActivity.class);
		i.putExtra(Extras.PROMOID, mappable.getId());
		MapActivity.this.startActivity(i);		
	}

	/**
	 * Muestra la pantalla de evento.
	 * @param mappable Información del evento.
	 */
	private void showEventScreen(Mappable mappable) {
		//Lanzamos activity de página de evento.
		Intent i = new Intent(MapActivity.this, EventPageActivity.class);
		i.putExtra(Extras.EVENTID, mappable.getId());
		MapActivity.this.startActivity(i);		
	}
}