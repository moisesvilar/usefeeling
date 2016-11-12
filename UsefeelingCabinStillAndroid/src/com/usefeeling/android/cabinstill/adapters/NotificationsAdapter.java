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
import com.usefeeling.android.cabinstill.api.Notification;
import com.usefeeling.android.cabinstill.helpers.DateTimeHelper;
import com.usefeeling.android.cabinstill.imageloader.ImageManager;

/**
 * Adaptador para ListViews de notificaciones del usuario.
 * @author Moisés Vilar.
 *
 */
public class NotificationsAdapter extends ArrayAdapter<Notification> {
	
	private ArrayList<Notification> notifications;
	private Activity activity;
	private LayoutInflater inflater;
	private int layoutId;
	private ImageManager imageManager;
	
	/**
	 * Constructor.
	 * @param _activity Contexto desde el que se requiere el adaptardor.
	 * @param layoutResourceId Identificador del layout empleado para cada fila del ListView correspondiente.
	 * @param objects Lista de objetos del adaptador.
	 */
	public NotificationsAdapter(Activity _activity, int layoutResourceId, ArrayList<Notification> objects) {
		super(_activity, layoutResourceId, objects);
		this.notifications = objects;
		this.activity = _activity;
		this.layoutId = layoutResourceId;
		this.imageManager = new ImageManager(this.activity);
		this.inflater = (LayoutInflater)this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	/**
	 * Clase para holders del tipo Notification
	 * @author Moisés Vilar.
	 *
	 */
	private static class NotificationHolder {
		public ImageView qcbIcon;
		public TextView tvTitle;
		public TextView tvDate;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		NotificationHolder holder;
		if (convertView == null) {
			convertView = this.inflater.inflate(this.layoutId, null);
			holder = new NotificationHolder();
			holder.qcbIcon = (ImageView)convertView.findViewById(R.id.qcbIcon);
			holder.tvTitle = (TextView)convertView.findViewById(R.id.tvTitle);
			holder.tvDate = (TextView)convertView.findViewById(R.id.tvDate);
			convertView.setTag(holder);
		} else {
			holder = (NotificationHolder) convertView.getTag();
		}
		
		final Notification notification = this.notifications.get(position);
		holder.tvTitle.setText(notification.getTitle(this.activity));
		holder.tvDate.setText(DateTimeHelper.toString(notification.getTimestamp()));
		this.imageManager.loadBitmap(notification.getIcon(), holder.qcbIcon, this.imageManager.getDefaultNotificationPortrait(), true);
		return convertView;
	}
}
