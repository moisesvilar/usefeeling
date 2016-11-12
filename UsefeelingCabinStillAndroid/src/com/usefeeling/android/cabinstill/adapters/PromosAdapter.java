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
import com.usefeeling.android.cabinstill.api.Promo;
import com.usefeeling.android.cabinstill.facades.DataFacade;
import com.usefeeling.android.cabinstill.helpers.DateTimeHelper;
import com.usefeeling.android.cabinstill.helpers.GeoHelper;
import com.usefeeling.android.cabinstill.imageloader.ImageManager;

/**
 * Adaptador para listas de promos.
 * @author Moisés Vilar.
 *
 */
public class PromosAdapter extends ArrayAdapter<Promo> {
	private List<Promo> promos;
	private Activity activity;
	private LayoutInflater inflater;
	private int layoutId;
	private ImageManager imageManager;
	private DataFacade dataFacade;
	private static final int drawableId = R.drawable.default_portrait;
	
	/**
	 * Constructor.
	 * @param _activity Contexto desde el que se requiere el adaptardor.
	 * @param layoutResourceId Identificador del layout empleado para cada fila del ListView correspondiente.
	 * @param objects Lista de objetos del adaptador.
	 */
	public PromosAdapter(Activity _activity, int layoutResourceId, List<Promo> objects) {
		super(_activity, layoutResourceId, objects);
		this.promos = objects;
		this.activity = _activity;
		this.layoutId = layoutResourceId;
		this.imageManager = new ImageManager(this.activity);
		this.dataFacade = new DataFacade(this.activity);
		this.inflater = (LayoutInflater)this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	/**
	 * Clase para holders del tipo Promo
	 * @author Moisés Vilar.
	 *
	 */
	private static class PromoHolder {
		public ImageView qcbIcon;
		public TextView tvPromoName;
		public TextView tvPlaceName;
		public TextView tvDistance;
		public TextView tvInitTime;
		public TextView tvEndTime;
		public ImageView ivAffinity;
	}
	
	@Override
	public long getItemId(int position) {
		return this.promos.get(position).getPromoId();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PromoHolder holder;
		if (convertView == null) {
			convertView = this.inflater.inflate(this.layoutId, null);
			holder = new PromoHolder();
			holder.qcbIcon = (ImageView)convertView.findViewById(R.id.qcbIcon);
			holder.tvPromoName = (TextView)convertView.findViewById(R.id.tvPromoName);
			holder.tvPlaceName = (TextView)convertView.findViewById(R.id.tvPlaceName);
			holder.tvDistance = (TextView)convertView.findViewById(R.id.tvDistance);
			holder.tvInitTime = (TextView)convertView.findViewById(R.id.tvInitTime);
			holder.tvEndTime = (TextView)convertView.findViewById(R.id.tvEndTime);
			holder.ivAffinity = (ImageView)convertView.findViewById(R.id.ivAffinity);
			convertView.setTag(holder);
		} else {
			holder = (PromoHolder) convertView.getTag();
		}
		
		final Promo promo = this.promos.get(position);
		holder.tvPromoName.setText(promo.getName());
		if (holder.tvPlaceName != null) holder.tvPlaceName.setText(promo.getPlace().getName());
		if (holder.tvDistance != null) holder.tvDistance.setText(GeoHelper.distanceToString(promo.getLatitude(), promo.getLongitude(), this.dataFacade.getUserPosition().getLatitude(), this.dataFacade.getUserPosition().getLongitude()));
		if (promo.getInitTime() != null && promo.getInitTime() > 0) holder.tvInitTime.setText(this.activity.getString(R.string.from) + " " + DateTimeHelper.toString(promo.getInitTime()));
		if (promo.getEndTime() != null && promo.getEndTime() > 0) holder.tvEndTime.setText(this.activity.getString(R.string.to) + " " + DateTimeHelper.toString(promo.getEndTime()));
		holder.ivAffinity.setImageResource(this.imageManager.getAffinityImageResId(promo.getAffinityStr()));
		this.imageManager.loadBitmap(promo.getImageUrl(), holder.qcbIcon, drawableId, false);		
		return convertView;
	}
}
