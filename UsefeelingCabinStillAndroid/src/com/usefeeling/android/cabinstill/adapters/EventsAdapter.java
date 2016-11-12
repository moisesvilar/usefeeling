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
import com.usefeeling.android.cabinstill.api.Event;
import com.usefeeling.android.cabinstill.facades.DataFacade;
import com.usefeeling.android.cabinstill.helpers.DateTimeHelper;
import com.usefeeling.android.cabinstill.helpers.GeoHelper;
import com.usefeeling.android.cabinstill.imageloader.ImageManager;

/**
 * Adaptador para listas de eventos.
 * @author Moisés Vilar.
 *
 */
public class EventsAdapter extends ArrayAdapter<Event> {
	private List<Event> events;
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
	public EventsAdapter(Activity _activity, int layoutResourceId, List<Event> objects) {
		super(_activity, layoutResourceId, objects);
		this.events = objects;
		this.activity = _activity;
		this.layoutId = layoutResourceId;
		this.imageManager = new ImageManager(this.activity);
		this.dataFacade = new DataFacade(this.activity);
		this.inflater = (LayoutInflater)this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	/**
	 * Clase para holders del tipo Event
	 * @author Moisés Vilar.
	 *
	 */
	private static class EventHolder {
		public ImageView qcbIcon;
		public TextView tvEventName;
		public TextView tvPlaceName;
		public TextView tvDistance;
		public TextView tvInitTime;
		public TextView tvEndTime;
		public ImageView ivAffinity;
	}
	
	@Override
	public long getItemId(int position) {
		return this.events.get(position).getEventId();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		EventHolder holder;
		if (convertView == null) {
			convertView = this.inflater.inflate(this.layoutId, null);
			holder = new EventHolder();
			holder.qcbIcon = (ImageView)convertView.findViewById(R.id.qcbIcon);
			holder.tvEventName = (TextView)convertView.findViewById(R.id.tvEventName);
			holder.tvPlaceName = (TextView)convertView.findViewById(R.id.tvPlaceName);
			holder.tvDistance = (TextView)convertView.findViewById(R.id.tvDistance);
			holder.tvInitTime = (TextView)convertView.findViewById(R.id.tvInitTime);
			holder.tvEndTime = (TextView)convertView.findViewById(R.id.tvEndTime);
			holder.ivAffinity = (ImageView)convertView.findViewById(R.id.ivAffinity);
			convertView.setTag(holder);
		} else {
			holder = (EventHolder) convertView.getTag();
		}
		
		final Event event = this.events.get(position);
		holder.tvEventName.setText(event.getName());
		if (holder.tvPlaceName != null) holder.tvPlaceName.setText(event.getPlace().getName());
		if (holder.tvDistance != null) holder.tvDistance.setText(GeoHelper.distanceToString(event.getLatitude(), event.getLongitude(), this.dataFacade.getUserPosition().getLatitude(), this.dataFacade.getUserPosition().getLongitude()));
		if (event.getInitTime() != null && event.getInitTime() > 0) holder.tvInitTime.setText(this.activity.getString(R.string.from) + " " + DateTimeHelper.toString(event.getInitTime()));
		if (event.getEndTime() != null && event.getEndTime() > 0) holder.tvEndTime.setText(this.activity.getString(R.string.to) + " " + DateTimeHelper.toString(event.getEndTime()));
		holder.ivAffinity.setImageDrawable(this.activity.getResources().getDrawable(this.imageManager.getAffinityImageResId(event.getAffinityStr())));
		this.imageManager.loadBitmap(event.getImageUrl(), holder.qcbIcon, drawableId, false);	
		return convertView;
	}
}
