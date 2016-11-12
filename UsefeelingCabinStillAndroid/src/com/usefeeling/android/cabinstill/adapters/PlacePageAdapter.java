package com.usefeeling.android.cabinstill.adapters;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.usefeeling.android.R;
import com.usefeeling.android.cabinstill.activities.EventPageActivity;
import com.usefeeling.android.cabinstill.activities.PromoPageActivity;
import com.usefeeling.android.cabinstill.api.Event;
import com.usefeeling.android.cabinstill.api.FacebookGraphData;
import com.usefeeling.android.cabinstill.api.Place;
import com.usefeeling.android.cabinstill.api.Promo;
import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.facades.ApplicationFacade;
import com.usefeeling.android.cabinstill.facades.DataFacade;
import com.usefeeling.android.cabinstill.facades.DeviceFacade;
import com.usefeeling.android.cabinstill.facades.MessagesFacade;
import com.usefeeling.android.cabinstill.helpers.GoogleStaticMapsHelper;
import com.usefeeling.android.cabinstill.imageloader.ImageManager;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;
import com.usefeeling.android.cabinstill.tasks.GetEventsByPlace;
import com.usefeeling.android.cabinstill.tasks.GetFacebookIdFromUrl;
import com.usefeeling.android.cabinstill.tasks.GetPromosByPlace;
import com.usefeeling.android.cabinstill.tasks.GetVisitsAndFavorites;
import com.usefeeling.android.cabinstill.values.Extras;

/**
 * Adaptador de columnas en la página de local.
 * @author Moisés Vilar.
 *
 */
public class PlacePageAdapter extends PagerAdapter {

	private String[] titles;
	private Activity mContext;
	private ImageManager mImageManager;
	private Place mPlace;
	private boolean mapDraw = false;
	private DataFacade dataFacade;
	
	/**
	 * View's positions for Place Page.
	 * @author Moises Vilar
	 *
	 */
	public static class ViewPositions {
		public static final int INFO = 0;
		public static final int EVENTS = 1;
		public static final int PROMOS = 2;
	}
	
	/**
	 * Constructor.
	 * @param _context Contexto desde el que se requiere el adaptador.
	 */
	public PlacePageAdapter(Activity _context, Place _place) {
		this.mContext = _context;
		this.titles = this.mContext.getResources().getStringArray(R.array.place_page_titles);
		this.mImageManager = new ImageManager(this.mContext);
		this.dataFacade = new DataFacade(this.mContext);
		this.mPlace = _place;
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		return this.titles[position % this.titles.length];
	}
	
	@Override
	public Object instantiateItem(ViewGroup collection, int position) {
		View v;
		if (position == ViewPositions.INFO) v = this.buildPlacePageInfoPage();
		else if (position == ViewPositions.EVENTS) v = this.buildPlacePageEventsPage();
		else v = this.buildPlacePagePromosPage();
		((ViewPager) collection).addView(v, 0);
        return v;
	}

	@Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        ((ViewPager) collection).removeView((View) view);
    }
	
	@Override
	public int getCount() {
		return this.titles.length;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((View) object);
	}

	@Override
	public void finishUpdate(ViewGroup container) {
	}
	
	/**
	 * Construye la página de información del local.
	 */
	private View buildPlacePageInfoPage() {
		//Parent
		final ScrollView v = (ScrollView)LayoutInflater.from(this.mContext).inflate(R.layout.place_page_info2, null);
		//Portada
		ImageView placeImage = (ImageView)v.findViewById(R.id.placeImage);
		this.mImageManager.loadBitmap(this.mPlace.getPortraitImage(), placeImage, R.drawable.default_portrait, true);
		//Afinidad
		ImageView affinity = (ImageView)v.findViewById(R.id.ivAffinity);
		affinity.setImageDrawable(this.mContext.getResources().getDrawable(this.mImageManager.getAffinityImageResId(this.mPlace.getAffinityStr())));
		//Tipo de local
		TextView tvPlaceType = (TextView)v.findViewById(R.id.placeType);
		tvPlaceType.setText(this.mPlace.getPlaceType());
		//Nombre de local
		TextView tvPlaceName = (TextView)v.findViewById(R.id.placeName);
		tvPlaceName.setText(this.mPlace.getName());
		//Mapa
		final ImageView ivMap = (ImageView)v.findViewById(R.id.ivMap);
		ViewTreeObserver vto = ivMap.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
		    public boolean onPreDraw() {
		        int finalHeight = ivMap.getMeasuredHeight();
		        int finalWidth = ivMap.getMeasuredWidth();
		        if (!PlacePageAdapter.this.mapDraw) PlacePageAdapter.this.mImageManager.loadBitmap(GoogleStaticMapsHelper.buildUrl(PlacePageAdapter.this.mPlace.getLatitude(), PlacePageAdapter.this.mPlace.getLongitude(), true, finalWidth, finalHeight), ivMap, R.drawable.map, true);
		        PlacePageAdapter.this.mapDraw = true;
		        return true;
		    }
		});
		ivMap.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Double slat = PlacePageAdapter.this.dataFacade.getUserPosition().getLatitude();
				Double slon = PlacePageAdapter.this.dataFacade.getUserPosition().getLongitude();
				Double dlat = PlacePageAdapter.this.mPlace.getLatitude();
				Double dlon = PlacePageAdapter.this.mPlace.getLongitude();
				DeviceFacade.openGMaps(PlacePageAdapter.this.mContext, slat, slon, dlat, dlon);
			}
		});
		//Dirección
		TextView tvAddress = (TextView)v.findViewById(R.id.placeAddress);
		tvAddress.setText(this.mPlace.getAddress());
		tvAddress.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String uri = String.format(Locale.ENGLISH, "geo:%f,%f", mPlace.getLatitude(), mPlace.getLongitude());
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
				PlacePageAdapter.this.mContext.startActivity(intent);
			}
		});
		Linkify.addLinks(tvAddress, Linkify.ALL);
		//Descripcion
		TextView tvDescription = (TextView)v.findViewById(R.id.placeDescription);
		tvDescription.setText(this.mPlace.getDescription());
		Linkify.addLinks(tvDescription, Linkify.ALL);
		//Teléfono
		TextView tvPhone = (TextView)v.findViewById(R.id.placePhone);
		tvPhone.setText(this.mPlace.getPhone().trim());
		Linkify.addLinks(tvPhone, Linkify.ALL);
		if (this.mPlace.getPhone().trim().equals("")) {
			TextView tvTitle = (TextView)v.findViewById(R.id.titlePhone);
			tvTitle.setVisibility(View.GONE);
			tvPhone.setVisibility(View.GONE);
		}
		else {
			tvPhone.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(Intent.ACTION_DIAL);
					intent.setData(Uri.parse("tel:" + mPlace.getPhone()));
					PlacePageAdapter.this.mContext.startActivity(intent); 
				}
			});
		}
		//Email
		TextView tvEmail = (TextView)v.findViewById(R.id.placeEmail);
		tvEmail.setText(this.mPlace.getEmail().trim());
		if (this.mPlace.getEmail().trim().equals("")) {
			TextView tvTitle = (TextView)v.findViewById(R.id.titleEmail);
			tvTitle.setVisibility(View.GONE);
			tvEmail.setVisibility(View.GONE);
		}
		//Web
		TextView tvWeb = (TextView)v.findViewById(R.id.placeWeb);
		tvWeb.setText(this.mPlace.getWeb().trim());
		if (this.mPlace.getWeb().trim().equals("")) {
			TextView tvTitle = (TextView)v.findViewById(R.id.titleWeb);
			tvTitle.setVisibility(View.GONE);
			tvWeb.setVisibility(View.GONE);
		}
		tvWeb.setMovementMethod(LinkMovementMethod.getInstance());
		//Visitas y Favoritos
		final TextView tvFavorites = (TextView)v.findViewById(R.id.placeFavorites);
		new GetVisitsAndFavorites(this.mContext, new OnTaskCompleted() {
			@Override
			public void onTaskCompleted(Object rawResult) {
				Result result = (Result)rawResult;
				if (result.getCode() != ResultCodes.Ok) return;
				try {
					Integer favoritos = Integer.parseInt(result.getPayload().split(";")[1]);
					tvFavorites.setText(favoritos.toString());
				} catch (Exception e) {
					return;
				}
			}
		}, this.mPlace.getPlaceId()).execute();
		//Facebook
		ImageView ivFacebook = (ImageView)v.findViewById(R.id.placeFacebook);
		if (this.mPlace.getFacebook().trim().equals("") && this.mPlace.getTwitter().trim().equals("")) {
			TextView tvTitle = (TextView)v.findViewById(R.id.titleFollowUs);
			tvTitle.setVisibility(View.GONE);
		}
		if (this.mPlace.getFacebook().trim().equals("")) {
			ivFacebook.setVisibility(View.GONE);
		}
		else {
			ivFacebook.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					new GetFacebookIdFromUrl(PlacePageAdapter.this.mContext,
							new OnTaskCompleted() {
								@Override
								public void onTaskCompleted(Object rawResult) {
									Result result = (Result) ((Object[])rawResult)[0];
									if (result.getCode() != ResultCodes.Ok) {
										MessagesFacade.toastLong(PlacePageAdapter.this.mContext, result.getMessage());
										return;
									}
									FacebookGraphData data = (FacebookGraphData)((Object[])rawResult)[1];
									PlacePageAdapter.this.mContext.startActivity(ApplicationFacade.getOpenFacebookIntent(PlacePageAdapter.this.mContext, data, true));
								}
					}, PlacePageAdapter.this.mPlace.getFacebook()).execute();
				}
			});
		}
		//Twitter
		ImageView ivTwitter = (ImageView)v.findViewById(R.id.placeTwitter);
		if (this.mPlace.getTwitter().trim().equals("")) {
			ivTwitter.setVisibility(View.GONE);
		}
		else {
			ivTwitter.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					PlacePageAdapter.this.mContext.startActivity(ApplicationFacade.getOpenTwitterIntent(PlacePageAdapter.this.mContext, PlacePageAdapter.this.mPlace.getTwitter()));
				}
			});
		}
		return v;
	}
	
	/**
	 * Construye la página de información de eventos del local.
	 */
	private View buildPlacePageEventsPage() {
		//Parent
		final LinearLayout v = (LinearLayout)LayoutInflater.from(this.mContext).inflate(R.layout.place_page_events, null);
		//ListView
		final ListView listView = (ListView)v.findViewById(R.id.listView);
		new GetEventsByPlace(PlacePageAdapter.this.mContext, new OnTaskCompleted() {
			@Override
			public void onTaskCompleted(Object rawResult) {
				TextView tvWorking = (TextView)v.findViewById(R.id.tvWorkingEvents);
				tvWorking.setVisibility(View.GONE);
				Result result = (Result)((Object[])rawResult)[0];
				if (result.getCode() != ResultCodes.Ok) {
					MessagesFacade.toastLong(PlacePageAdapter.this.mContext, result.getMessage());
					return;
				}
				@SuppressWarnings("unchecked")
				final ArrayList<Event> events = (ArrayList<Event>)((Object[])rawResult)[1];
				TextView tvListViewEmpty = (TextView)v.findViewById(R.id.tvListViewEmpty);
				if (events == null || events.isEmpty()) tvListViewEmpty.setVisibility(View.VISIBLE);
				listView.setAdapter(new EventsAdapter(PlacePageAdapter.this.mContext, R.layout.event_listitem, events));
				listView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
						Event event = events.get(position);
						Intent i = new Intent(PlacePageAdapter.this.mContext, EventPageActivity.class);
						i.putExtra(Extras.EVENT, event);
						PlacePageAdapter.this.mContext.startActivity(i);
					}
				});
			}
		}, PlacePageAdapter.this.mPlace.getPlaceId()).execute();
		return v;
	}
	
	/**
	 * Construye la página de información de eventos del local.
	 */
	private View buildPlacePagePromosPage() {
		//Parent
		final LinearLayout v = (LinearLayout)LayoutInflater.from(this.mContext).inflate(R.layout.place_page_promos, null);
		//ListView
		final ListView listView = (ListView)v.findViewById(R.id.listView);
		new GetPromosByPlace(PlacePageAdapter.this.mContext, new OnTaskCompleted() {
			@Override
			public void onTaskCompleted(Object rawResult) {
				TextView tvWorking = (TextView)v.findViewById(R.id.tvWorkingPromos);
				tvWorking.setVisibility(View.GONE);
				Result result = (Result)((Object[])rawResult)[0];
				if (result.getCode() != ResultCodes.Ok) {
					MessagesFacade.toastLong(PlacePageAdapter.this.mContext, result.getMessage());
					return;
				}
				@SuppressWarnings("unchecked")
				final ArrayList<Promo> promos = (ArrayList<Promo>)((Object[])rawResult)[1];
				TextView tvListViewEmpty = (TextView)v.findViewById(R.id.tvListViewEmpty);
				if (promos == null || promos.isEmpty()) tvListViewEmpty.setVisibility(View.VISIBLE);
				listView.setAdapter(new PromosAdapter(PlacePageAdapter.this.mContext, R.layout.promo_listitem, promos));
				listView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
						Promo promo = promos.get(position);
						Intent i = new Intent(PlacePageAdapter.this.mContext, PromoPageActivity.class);
						i.putExtra(Extras.PROMO, promo);
						PlacePageAdapter.this.mContext.startActivity(i);
					}
				});
			}
		}, PlacePageAdapter.this.mPlace.getPlaceId()).execute();
		return v;
	}
}
