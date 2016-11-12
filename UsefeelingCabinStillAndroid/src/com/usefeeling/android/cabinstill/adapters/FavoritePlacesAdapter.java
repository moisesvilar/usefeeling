package com.usefeeling.android.cabinstill.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.usefeeling.android.R;
import com.usefeeling.android.cabinstill.api.Place;
import com.usefeeling.android.cabinstill.imageloader.ImageManager;

public class FavoritePlacesAdapter extends ArrayAdapter<Place> {
	private ArrayList<Place> places;
	private Activity activity;
	private ImageManager imageManager;
	private int layoutId;
	private LayoutInflater inflater;
		
	/**
	 * Constructor.
	 * @param _activity Contexto desde el que se requiere el adaptardor.
	 * @param layoutResourceId Identificador del layout empleado para cada fila del ListView correspondiente.
	 * @param objects Lista de objetos del adaptador.
	 */
	public FavoritePlacesAdapter(Activity _activity, int layoutResourceId, ArrayList<Place> objects) {
		super(_activity, layoutResourceId, objects);
		this.places = objects;
		this.activity = _activity;
		this.layoutId = layoutResourceId;
		this.imageManager = new ImageManager(this.activity);
		this.inflater = (LayoutInflater)this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	/**
	 * Clase para holders del tipo FavoritePlace
	 * @author Moisés Vilar.
	 *
	 */
	private static class FavoritePlaceHolder {
		public ImageView qcbIcon;
		public ImageView ivAffinity;
		public TextView tvPlaceType;
		public TextView tvPlaceName;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FavoritePlaceHolder holder;
		if (convertView == null) {
			convertView = this.inflater.inflate(this.layoutId, null);
			holder = new FavoritePlaceHolder();
			holder.qcbIcon = (ImageView)convertView.findViewById(R.id.qcbIcon);
			holder.ivAffinity = (ImageView)convertView.findViewById(R.id.ivAffinity);
			holder.tvPlaceName = (TextView)convertView.findViewById(R.id.tvPlaceName);
			holder.tvPlaceType = (TextView)convertView.findViewById(R.id.tvPlaceType);
			convertView.setTag(holder);
		} else {
			holder = (FavoritePlaceHolder) convertView.getTag();
		}
		
		final Place place = this.places.get(position);
		holder.tvPlaceName.setText(place.getName());
		holder.tvPlaceType.setText(place.getPlaceType());
		holder.ivAffinity.setImageResource(this.imageManager.getAffinityImageResId(place.getAffinityStr()));
		this.imageManager.loadBitmap(place.getPortraitImage(), holder.qcbIcon, this.imageManager.getDefaultVenuePortraitByType(place.getIcon()), true);
		return convertView;
	}

	@Override
	public long getItemId(int position) {
		return this.places.get(position).getPlaceId();
	}
	
	
}
