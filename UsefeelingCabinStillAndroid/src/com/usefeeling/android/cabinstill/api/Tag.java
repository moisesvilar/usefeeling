package com.usefeeling.android.cabinstill.api;

import java.io.Serializable;

/**
 * POJO para datos sobre etiquetas.
 * @author Moisés Vilar.
 *
 */
public class Tag implements Serializable {

	private static final long serialVersionUID = 624963520124558071L;
	private Integer tagId;
	private Short superTagId;
	private String superTag;
	private String name;
	private Float affinity;
	
	/**
	 * @return the tagId
	 */
	public Integer getTagId() {
		return tagId;
	}
	/**
	 * @param tagId the tagId to set
	 */
	public void setTagId(Integer tagId) {
		this.tagId = tagId;
	}
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
	 * @return the superTag
	 */
	public String getSuperTag() {
		return superTag;
	}
	/**
	 * @param superTag the superTag to set
	 */
	public void setSuperTag(String superTag) {
		this.superTag = superTag;
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
