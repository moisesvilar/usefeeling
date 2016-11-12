package com.usefeeling.android.cabinstill.api;

/**
 * Clase para almacenar identificadores recogidos desde peticiones HTTP que devuelven objetos JSON.
 * @author Moisés Vilar.
 *
 */
public class FacebookGraphData {

	private String id;
	private String link;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
}
