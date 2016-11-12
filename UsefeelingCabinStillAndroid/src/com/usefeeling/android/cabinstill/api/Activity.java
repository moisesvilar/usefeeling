package com.usefeeling.android.cabinstill.api;

import java.io.Serializable;

/**
 * POJO para datos sobre actividades.
 * @author Moisés Vilar.
 *
 */
public class Activity implements Serializable {

	private static final long serialVersionUID = 2268337437325980564L;

	private Long id;
	private Long placeid;
	private String name;
	private String description;
	private Long start;
	private Long end;
	private String imageUrl;
	private Float affinity;
	private String affinityStr;
	private Float fame;
	private short type;
	
	/**
	 * Constructor por defecto.
	 */
	public Activity() {
		this.id = 0L;
		this.placeid = 0L;
		this.name = "";
		this.description = "";
		this.start = 0L;
		this.end = 0L;
		this.imageUrl = "";
		this.affinity = 0.0f;
		this.affinityStr = "";
		this.fame = 0.0f;
		this.type = 0;
	}
	
	/**
	 * Constructor.
	 * @param id
	 * @param placeid
	 * @param name
	 * @param description
	 * @param start
	 * @param end
	 * @param imageUrl
	 * @param affinity
	 * @param affinityStr
	 * @param type
	 */
	public Activity(Long id, Long placeid, String name, String description, Long start, Long end, String imageUrl, Float affinity, String affinityStr, Float fame, short type) {
		super();
		this.id = id;
		this.placeid = placeid;
		this.name = name;
		this.description = description;
		this.start = start;
		this.end = end;
		this.imageUrl = imageUrl;
		this.affinity = affinity;
		this.affinityStr = affinityStr;
		this.fame = fame;
		this.type = type;
	}
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the placeid
	 */
	public Long getPlaceid() {
		return placeid;
	}
	/**
	 * @param placeid the placeid to set
	 */
	public void setPlaceid(Long placeid) {
		this.placeid = placeid;
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the start
	 */
	public Long getStart() {
		return start;
	}
	/**
	 * @param start the start to set
	 */
	public void setStart(Long start) {
		this.start = start;
	}
	/**
	 * @return the end
	 */
	public Long getEnd() {
		return end;
	}
	/**
	 * @param end the end to set
	 */
	public void setEnd(Long end) {
		this.end = end;
	}
	/**
	 * @return the imageUrl
	 */
	public String getImageUrl() {
		return imageUrl;
	}
	/**
	 * @param imageUrl the imageUrl to set
	 */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
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
	/**
	 * @return the affinityStr
	 */
	public String getAffinityStr() {
		return affinityStr;
	}
	/**
	 * @param affinityStr the affinityStr to set
	 */
	public void setAffinityStr(String affinityStr) {
		this.affinityStr = affinityStr;
	}
	/**
	 * @return the type
	 */
	public Short getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(short type) {
		this.type = type;
	}

	/**
	 * @return the fame
	 */
	public Float getFame() {
		return fame;
	}

	/**
	 * @param fame the fame to set
	 */
	public void setFame(Float fame) {
		this.fame = fame;
	}
}

