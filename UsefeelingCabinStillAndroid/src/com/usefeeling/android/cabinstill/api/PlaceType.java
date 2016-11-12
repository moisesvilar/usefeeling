package com.usefeeling.android.cabinstill.api;

import java.io.Serializable;

/**
 * POJO para datos sobre tipos de locales.
 * @author Moisés Vilar.
 *
 */
public class PlaceType implements Serializable {
	private static final long serialVersionUID = -172652912182326029L;

	private Integer typeId;
	private String name;
	/**
	 * @return the typeId
	 */
	public Integer getTypeId() {
		return typeId;
	}
	/**
	 * @param typeId the typeId to set
	 */
	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
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
