package com.usefeeling.android.cabinstill.api;

import java.io.Serializable;

/**
 * POJO para datos de solicitud de amistad.
 * @author Moisés Vilar.
 *
 */
public class FriendshipRequest implements Serializable  {

	private static final long serialVersionUID = -8239636809082761036L;
	private Long userid;
	private String name;
	private Long timestamp;
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
	 * Obtiene la URL de la foto de perfil del usuario.
	 * @return La URL.
	 */
	public String getImageUrl() {
		return "userid=" + this.userid;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}	
}
