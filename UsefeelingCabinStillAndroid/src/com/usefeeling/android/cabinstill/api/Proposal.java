package com.usefeeling.android.cabinstill.api;

import java.io.Serializable;

import com.usefeeling.android.cabinstill.helpers.DateTimeHelper;
import com.usefeeling.android.cabinstill.interfaces.Mappable;

/**
 * Leisure proposals.
 * @author Moisés Vilar.
 *
 */
public class Proposal implements Serializable, Mappable {

	private static final long serialVersionUID = 2673034984971935924L;
	private Long id;
	private String name;
	private String placeName;
	private String address;
	private String mapIcon;
	private String description;
	private Double latitude;
	private Double longitude;
	private Long timestamp;
	private Float affinity;
	private String affinityStr;
	private short type;
	
	/**
	 * Proposal types
	 * @author Moisés Vilar
	 *
	 */
	public static class ProposalTypes {
		public static final short VENUE = 0;
		public static final short EVENT = 1;
		public static final short PROMO = 2;
	}
	
	/**
	 * Default constructor.
	 */
	public Proposal() {}
	
	/**
	 * Constructor from place.
	 * @param place The place.
	 */
	public Proposal(Place place) {
		this.id = place.getPlaceId();
		this.name = place.getName();
		this.placeName = place.getName();
		this.setAddress(place.getAddress());
		this.mapIcon = place.getMapImageUrl();
		this.description = place.getDescription();
		this.latitude = place.getLatitude();
		this.longitude = place.getLongitude();
		this.timestamp = null;
		this.setAffinity(place.getAffinity());
		this.affinityStr = place.getAffinityStr();
		this.type = ProposalTypes.VENUE;
	}
	
	/**
	 * Constructor from event.
	 * @param event The event.
	 */
	public Proposal(Event event) {
		this.id = event.getEventId();
		this.name = event.getName();
		this.placeName = event.getPlace().getName();
		this.setAddress(event.getPlace().getAddress());
		this.mapIcon = event.getMapImageUrl();
		this.description = event.getDescription();
		this.latitude = event.getLatitude();
		this.longitude = event.getLongitude();
		this.timestamp = event.getInitTime();
		this.setAffinity(event.getAffinity());
		this.affinityStr = event.getAffinityStr();
		this.type = ProposalTypes.EVENT;
	}
	
	/**
	 * Constructor from promotion.
	 * @param promo The promotion.
	 */
	public Proposal(Promo promo) {
		this.id = promo.getPromoId();
		this.name = promo.getName();
		this.placeName = promo.getPlace().getName();
		this.setAddress(promo.getPlace().getAddress());
		this.mapIcon = promo.getMapImageUrl();
		this.description = promo.getDescription();
		this.latitude = promo.getLatitude();
		this.longitude = promo.getLongitude();
		this.timestamp = promo.getInitTime();
		this.setAffinity(promo.getAffinity());
		this.affinityStr = promo.getAffinityStr();
		this.type = ProposalTypes.PROMO;
	}
	
	/**
	 * Proposal from activity (event or promotion) and place.
	 * @param activity The activity.
	 * @param place The place.
	 */
	public Proposal(Activity activity, Place place) {
		this.id = activity.getId();
		this.name = activity.getName();
		this.placeName = place.getName();
		this.setAddress(place.getAddress());
		this.mapIcon = place.getMapImageUrl();
		this.description = activity.getDescription();
		this.latitude = place.getLatitude();
		this.longitude = place.getLongitude();
		this.timestamp = activity.getStart();
		this.setAffinity(activity.getAffinity());
		this.affinityStr = activity.getAffinityStr();
		this.type = activity.getType() == 0 ? ProposalTypes.EVENT : ProposalTypes.PROMO;
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
	 * @return the placeName
	 */
	public String getPlaceName() {
		return placeName;
	}

	/**
	 * @param placeName the placeName to set
	 */
	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	/**
	 * @return the mapIcon
	 */
	public String getMapIcon() {
		return mapIcon;
	}

	/**
	 * @param mapIcon the mapIcon to set
	 */
	public void setMapIcon(String mapIcon) {
		this.mapIcon = mapIcon;
	}

	/**
	 * @return the latitude
	 */
	public Double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public Double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
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
	 * @return the affinity
	 */
	public String getAffinityStr() {
		return affinityStr;
	}

	/**
	 * @param affinity the affinity to set
	 */
	public void setAffinityStr(String affinity) {
		this.affinityStr = affinity;
	}

	/**
	 * @return the type
	 */
	public short getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(short type) {
		this.type = type;
	}

	@Override
	public String getTitle() {
		return this.name;
	}

	@Override
	public String getSubtitle() {
		switch(this.type) {
		case ProposalTypes.VENUE:
			return this.address;
		case ProposalTypes.EVENT:
		case ProposalTypes.PROMO:
			return this.placeName + "\n" + DateTimeHelper.toLongString(this.timestamp) + "\n" + this.address;
		default:
			return this.name;
		}
	}

	@Override
	public String getMapDescription(android.app.Activity context) {
		return this.description;
	}

	@Override
	public String getImageUrl() {
		return this.mapIcon;
	}

	@Override
	public String getMapImageUrl() {
		return this.mapIcon;
	}

	@Override
	public Float getAffinity() {
		return this.affinity;
	}

	/**
	 * @param affinity the affinity to set
	 */
	public void setAffinity(Float affinity) {
		this.affinity = affinity;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
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

	@Override
	public Long getId() {
		return this.id;
	}
	
}