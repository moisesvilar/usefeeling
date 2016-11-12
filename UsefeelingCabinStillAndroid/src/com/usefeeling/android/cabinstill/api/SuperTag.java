package com.usefeeling.android.cabinstill.api;

import java.io.Serializable;

/**
 * POJO para datos sobre superetiquetas.
 * @author Moisés Vilar.
 *
 */
public class SuperTag implements Serializable {
	private static final long serialVersionUID = -525158049744434311L;
	private Short superTagId;
	private String name;
	private Float affinity;
	
	/**
	 * @return the superTagId
	 */
	public Short getSuperTagId() {
		return superTagId;
	}
	/**
	 * @param superTagId the superTagId to set
	 */
	public void setSuperTagId(Short superTagId) {
		this.superTagId = superTagId;
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
	/**
	 * @return the affinity
	 */
	public Float getAffinity() {
		return affinity;
	}
	/**
	 * @param affinity the affinity to set
	 */
	public void setAffinity(Float affinity) {
		this.affinity = affinity;
	}
}
