package com.usefeeling.android.cabinstill.api;

import java.io.Serializable;

import android.content.Context;

import com.google.gson.Gson;
import com.usefeeling.android.R;

/**
 * POJO para datos de notificación.
 * @author Moisés Vilar.
 *
 */
public class Notification implements Serializable  {
	private static final long serialVersionUID = 110689434925874134L;
	
	/**
	 * Tipos de notificaciones.
	 * @author Moisés Vilar.
	 *
	 */
	public static class NotificationTypes {
		public static final short CONFIRM_FRIENDSHIP_REQUEST = 1;
		public static final short SEND_FRIENDSHIP_REQUEST = 2;
		public static final short PLACE_NOTIFICATION = 3;
		public static final short EVENT_NOTIFICATION = 4;
		public static final short PROMO_NOTIFICATION = 5;
		public static final short URL_NOTIFICATION = 6;
		public static final short CHECK_CHECKIN_SERVICE = 7;
		public static final short SUGGESTION = 8;
	}
		
	private Long id;
	private Long userid;
	private Short type;
	private String title;
	private String payload;
	private Boolean pendiente;
	private Long timestamp;
	private Long activityId;
	
	/**
	 * @return the notificationid
	 */
	public Long getNotificationid() {
		return id;
	}
	/**
	 * @param notificationid the notificationid to set
	 */
	public void setNotificationid(Long notificationid) {
		this.id = notificationid;
	}
	/**
	 * @return the userid
	 */
	public Long getUserid() {
		return userid;
	}
	/**
	 * @param userid the userid to set
	 */
	public void setUserid(Long userid) {
		this.userid = userid;
	}
	/**
	 * @return the notificationType
	 */
	public Short getNotificationType() {
		return type;
	}
	/**
	 * @param notificationType the notificationType to set
	 */
	public void setNotificationType(Short notificationType) {
		this.type = notificationType;
	}
	/**
	 * @return the payload
	 */
	public String getPayload() {
		return payload;
	}
	/**
	 * @param payload the payload to set
	 */
	public void setPayload(String payload) {
		this.payload = payload;
	}
	/**
	 * @return the timestamp
	 */
	public Long getTimestamp() {
		return timestamp;
	}
	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	/**
	 * @return the pendiente
	 */
	public Boolean isPendiente() {
		return pendiente;
	}
	/**
	 * @param pendiente the pendiente to set
	 */
	public void setPendiente(Boolean pendiente) {
		this.pendiente = pendiente;
	}
	/**
	 * @return the title
	 */
	public String getTitle(Context context) {
		if (this.type.equals(NotificationTypes.SEND_FRIENDSHIP_REQUEST)) return this.title + " " + context.getString(R.string.send_friendship_request_sufix);
		else if (this.type.equals(NotificationTypes.CONFIRM_FRIENDSHIP_REQUEST)) return this.title + " " + context.getString(R.string.confirm_friendship_request_sufix);
		else return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Obtiene la URL del icono.
	 * @return La URL del icono, formateada en función del tipo de notificación:<br>
	 * <li>Recepción de solicitud de amistad: <font face="courier new">userid=?</font></li>
	 * <li>Confirmación de solicitud de amistad: <font face="courier new">userid=?</font></li>
	 */
	public String getIcon() {
		Gson gson = new Gson();
		switch (this.type) {
		case NotificationTypes.SEND_FRIENDSHIP_REQUEST:
		case NotificationTypes.CONFIRM_FRIENDSHIP_REQUEST:
			FriendshipRequest request = (FriendshipRequest)gson.fromJson(this.payload, FriendshipRequest.class);
			return "userid=" + request.getUserid();
		case NotificationTypes.PLACE_NOTIFICATION:
			Place place = (Place)gson.fromJson(this.payload, Place.class);
			return place.getPortraitImage();
		case NotificationTypes.EVENT_NOTIFICATION:
			return "eventid=" + this.payload;
		case NotificationTypes.PROMO_NOTIFICATION:
			return "promoid=" + this.payload;
		case NotificationTypes.URL_NOTIFICATION:
			return null;
		}
		return "";
	}
	/**
	 * @return the activityId
	 */
	public Long getActivityId() {
		return activityId;
	}
	/**
	 * @param activityId the activityId to set
	 */
	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}
	
}
