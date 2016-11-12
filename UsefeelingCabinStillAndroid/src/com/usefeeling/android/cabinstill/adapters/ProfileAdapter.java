package com.usefeeling.android.cabinstill.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.usefeeling.android.R;
import com.usefeeling.android.cabinstill.activities.PlacePageActivity;
import com.usefeeling.android.cabinstill.api.Place;
import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.api.UseFeeling;
import com.usefeeling.android.cabinstill.api.User;
import com.usefeeling.android.cabinstill.facades.MessagesFacade;
import com.usefeeling.android.cabinstill.helpers.DateTimeHelper;
import com.usefeeling.android.cabinstill.imageloader.ImageManager;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;
import com.usefeeling.android.cabinstill.tasks.GetAffinedPlaces;
import com.usefeeling.android.cabinstill.tasks.GetFavoritePlaces;
import com.usefeeling.android.cabinstill.values.Extras;

/**
 * Adaptador de columnas en la ventana de perfil de usuario.
 * @author Moisés Vilar.
 *
 */
public class ProfileAdapter extends PagerAdapter {

	private String[] titles;
	private Activity mContext;
	private ImageManager mImageManager;
	private User mUser;
	
	/**
	 * View's positions in Profile Page.
	 * @author Moises Vilar
	 *
	 */
	public static class ViewPositions {
		public static final int INFO = 0;
		public static final int AFFINED = 1;
		public static final int FAVORITES = 2;
	}
	
	/**
	 * Constructor.
	 * @param _context Contexto desde el que se requiere el adaptador.
	 */
	public ProfileAdapter(Activity _context, User _user) {
		this.mContext = _context;
		this.titles = this.mContext.getResources().getStringArray(R.array.profile_titles);
		this.mImageManager = new ImageManager(this.mContext);
		this.mUser = _user;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return this.titles[position % this.titles.length];
	}

	@Override
	public Object instantiateItem(ViewGroup collection, int position) {
		View v;
		if (position == ViewPositions.INFO) v = this.buildProfileInfoPage();
		else if (position == ViewPositions.AFFINED) v = this.buildProfileMostAffinedVenues();
		else v = this.buildProfileFavoritesPage();
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
		return view == (View) object;
	}

	@Override
	public void finishUpdate(ViewGroup container) {
	}
	
	/**
	 * Construye la página de información del usuario.
	 * @return El View que contiene la página de información del usuario.
	 */
	private View buildProfileInfoPage() {
		//Parent
		ScrollView v = (ScrollView)LayoutInflater.from(this.mContext).inflate(R.layout.profile_info2, null);
		//Foto de perfil
		ImageView qcbPicture = (ImageView)v.findViewById(R.id.userImage);
		this.mImageManager.loadBitmap(this.mUser.getImageUrl(), qcbPicture, R.drawable.ic_contact_picture, false);
		//Nombre
		TextView tvName = (TextView)v.findViewById(R.id.userName);
		tvName.setText(this.mUser.getName());
		//Afinidad
		TextView tvAffinity = (TextView)v.findViewById(R.id.userAffinity);
		tvAffinity.setText(this.mUser.getAffinityStr());
		if (this.mUser.getAffinityStr().equals(UseFeeling.GREEN.toString())) tvAffinity.setBackgroundResource(R.color.usefeeling_green);
		else if (this.mUser.getAffinityStr().equals(UseFeeling.YELLOW.toString())) tvAffinity.setBackgroundResource(R.color.usefeeling_yellow);
		else tvAffinity.setBackgroundResource(R.color.usefeeling_red);
		//Teléfono
		TextView tvPhone = (TextView)v.findViewById(R.id.userPhone);
		tvPhone.setText(this.mUser.getPhone());
		Linkify.addLinks(tvPhone, Linkify.ALL);
		if (this.mUser.getPhone().trim().equals("")) {
			TextView tvTitle = (TextView)v.findViewById(R.id.titlePhone);
			tvTitle.setVisibility(View.GONE);
			tvPhone.setVisibility(View.GONE);
		}
		else {
			tvPhone.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(Intent.ACTION_DIAL);
					intent.setData(Uri.parse("tel:" + mUser.getPhone()));
					ProfileAdapter.this.mContext.startActivity(intent); 
				}
			});
		}
		//Email
		TextView tvEmail = (TextView)v.findViewById(R.id.userEmail);
		tvEmail.setText(this.mUser.getEmail());
		if (this.mUser.getEmail().trim().equals("")) {
			TextView tvTitle = (TextView)v.findViewById(R.id.titleEmail);
			tvTitle.setVisibility(View.GONE);
			tvEmail.setVisibility(View.GONE);
		}
		//Sexo
		TextView tvGender = (TextView)v.findViewById(R.id.userGender);
		tvGender.setText(this.mUser.getGender());
		//Cumpleaños
		TextView tvBirthday = (TextView)v.findViewById(R.id.userBirthday);
		tvBirthday.setText(DateTimeHelper.toBirthdayString(this.mUser.getBirthdate()));
		return v;
	}

	/**
	 * Construye la página de los locales más afines del usuario.
	 * @return El View que contiene la página de los locales más afines del usuario.
	 */
	private View buildProfileMostAffinedVenues() {
		//Parent
		final LinearLayout v = (LinearLayout)LayoutInflater.from(this.mContext).inflate(R.layout.profile_most_affined_venues, null);
		//ListView
		final ListView listView = (ListView)v.findViewById(R.id.listView);
		//Obtenemos los locales más afines del usuario
		new GetAffinedPlaces(this.mContext, new OnTaskCompleted() {
			@Override
			public void onTaskCompleted(Object rawResult) {
				TextView tvWorking = (TextView)v.findViewById(R.id.tvInitialWorking);
				tvWorking.setVisibility(View.GONE);
				Result result = (Result)((Object[])rawResult)[0];
				if (result.getCode() != ResultCodes.Ok) {
					MessagesFacade.toastLong(ProfileAdapter.this.mContext, result.getMessage());
					return;
				}
				@SuppressWarnings("unchecked")
				final ArrayList<Place> places = (ArrayList<Place>)((Object[])rawResult)[1];
				TextView tvListViewEmpty = (TextView)v.findViewById(R.id.tvListViewEmpty);
				if (places == null || places.isEmpty()) tvListViewEmpty.setVisibility(View.VISIBLE);
				listView.setAdapter(new AffinedPlacesAdapter(ProfileAdapter.this.mContext, R.layout.place_profile_affined_list_item, places));
				listView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
						Intent i = new Intent(ProfileAdapter.this.mContext, PlacePageActivity.class);
						i.putExtra(Extras.PLACE, places.get(position));
						ProfileAdapter.this.mContext.startActivity(i);
					}
				});
			}

		}, this.mUser.getUserid()).execute();
		return v;
	}
	
	/**
	 * Construye la página para visualizar los locales favoritos del usuario.
	 * @return El View que contiene la página de los locales favoritos del usuario.
	 */
	private View buildProfileFavoritesPage() {
		//Parent
		final LinearLayout v = (LinearLayout)LayoutInflater.from(this.mContext).inflate(R.layout.profile_favorite_places, null);
		//ListView
		final ListView listView = (ListView)v.findViewById(R.id.listView);
		new GetFavoritePlaces(this.mContext, new OnTaskCompleted() {
			@Override
			public void onTaskCompleted(Object rawResult) {
				TextView tvWorking = (TextView)v.findViewById(R.id.tvWorkingFavorites);
				tvWorking.setVisibility(View.GONE);
				Result result = (Result)((Object[])rawResult)[0];
				if (result.getCode() != ResultCodes.Ok) {
					MessagesFacade.toastLong(ProfileAdapter.this.mContext, result.getMessage());
					return;
				}
				@SuppressWarnings("unchecked")
				final ArrayList<Place> places = (ArrayList<Place>)((Object[])rawResult)[1];
				TextView tvListViewEmpty = (TextView)v.findViewById(R.id.tvListViewEmpty);
				if (places == null || places.isEmpty()) tvListViewEmpty.setVisibility(View.VISIBLE);
				listView.setAdapter(new FavoritePlacesAdapter(ProfileAdapter.this.mContext, R.layout.place_profile_favorite_list_item, places));
				listView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
						Intent i = new Intent(ProfileAdapter.this.mContext, PlacePageActivity.class);
						i.putExtra(Extras.PLACE, places.get(position));
						ProfileAdapter.this.mContext.startActivity(i);
					}
				});
			}

		}, this.mUser.getUserid()).execute();
		return v;
	}
}
