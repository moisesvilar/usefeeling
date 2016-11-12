package com.usefeeling.android.cabinstill.adapters;

import java.util.List;

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
import com.usefeeling.android.cabinstill.facades.DataFacade;
import com.usefeeling.android.cabinstill.helpers.GeoHelper;
import com.usefeeling.android.cabinstill.imageloader.ImageManager;

/**
 * Adaptador para lista de locales.
 * @author Moisés Vilar.
 *
 */
public class PlacesAdapter extends ArrayAdapter<Place> {

	private List<Place> places;
	private Activity activity;
	private ImageManager imageManager;
	private int layoutId;
	private LayoutInflater inflater;
	private DataFacade dataFacade;
		
	/**
	 * Constructor.
	 * @param _activity Contexto desde el que se requiere el adaptardor.
	 * @param layoutResourceId Identificador del layout empleado para cada fila del ListView correspondiente.
	 * @param objects Lista de objetos del adaptador.
	 */
	public PlacesAdapter(Activity _activity, int layoutResourceId, List<Place> objects) {
		super(_activity, layoutResourceId, objects);
		this.places = objects;
		this.activity = _activity;
		this.layoutId = layoutResourceId;
		this.imageManager = new ImageManager(this.activity);
		this.inflater = (LayoutInflater)this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.dataFacade = new DataFacade(this.activity);
	}
	
	/**
	 * Clase para holders del tipo FavoritePlace
	 * @author Moisés Vilar.
	 *
	 */
	private static class PlaceHolder {
		public ImageView qcbIcon;
		public TextView tvPlaceType;
		public TextView tvPlaceName;
		public TextView tvDistance;
		public TextView tvAddress;
		public ImageView ivAffinity;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PlaceHolder holder;
		if (convertView == null) {
			convertView = this.inflater.inflate(this.layoutId, null);
			holder = new PlaceHolder();
			holder.qcbIcon = (ImageView)convertView.findViewById(R.id.qcbIcon);
			holder.tvPlaceName = (TextView)convertView.findViewById(R.id.tvPlaceName);
			holder.tvPlaceType = (TextView)convertView.findViewById(R.id.tvPlaceType);
			holder.tvAddress = (TextView)convertView.findViewById(R.id.tvAddress);
			holder.tvDistance = (TextView)convertView.findViewById(R.id.tvDistance);
			holder.ivAffinity = (ImageView)convertView.findViewById(R.id.ivAffinity);
			convertView.setTag(holder);
		} else {
			holder = (PlaceHolder) convertView.getTag();
		}
		
		final Place place = this.places.get(position);
		holder.tvPlaceName.setText(place.getName());
		holder.tvPlaceType.setText(place.getPlaceType());
		holder.tvAddress.setText(place.getAddress());
		holder.tvDistance.setText(GeoHelper.distanceToString(place.getLatitude(), place.getLongitude(), this.dataFacade.getUserPosition().getLatitude(), this.dataFacade.getUserPosition().getLongitude()));
		holder.ivAffinity.setImageResource(this.imageManager.getAffinityImageResId(place.getAffinityStr()));
		this.imageManager.loadBitmap(place.getPortraitImage(), holder.qcbIcon, this.imageManager.getDefaultVenuePortraitByType(place.getIcon()), true);
		return convertView;
	}

	@Override
	public long getItemId(int position) {
		return this.places.get(position).getPlaceId();
	}
	
}
