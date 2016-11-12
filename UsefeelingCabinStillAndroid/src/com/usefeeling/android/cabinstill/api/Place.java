package com.usefeeling.android.cabinstill.api;

import java.io.Serializable;

import android.app.Activity;

import com.usefeeling.android.cabinstill.facades.DataFacade;
import com.usefeeling.android.cabinstill.helpers.GeoHelper;
import com.usefeeling.android.cabinstill.interfaces.Mappable;

/**
 * POJO para datos sobre locales.
 * @author Moisés Vilar.
 *
 */
public class Place implements Serializable, Mappable, Comparable<Place> {

	private static final long serialVersionUID = -3281449021159882499L;
	private Long placeId = 0L;
	private String placeType = "";
	private Integer placeTypeId;
	private String name = "";
	private String description = "";
	private String address = "";
	private String street = "";
	private String number = "";
	private String city = "";
	private Integer cityId = 0;
	private String postalCode = "";
	private String web = "";
	private String email = "";
	private String facebook = "";
	private String twitter = "";
	private String phone = "";
	private Boolean hasIcon = false;
	private Double latitude = 0.0;
	private Double longitude = 0.0;
	private Integer visits = 0;
	private Integer friendVisits = 0;
	private Integer favorites = 0;
	private Float affinity = 0.0f;
	private String affinityStr = "";
	private Float fame;
	private Boolean hasEvents = false;
	private Boolean hasPromos = false;
	private Boolean isFavorite = false;
	private Boolean isForEating = false;
	private Boolean isForDrinking = false;
	private Boolean isForPartying = false;
	private String icon = "";
	private Boolean completed = false;
	private Boolean hasCheckin = false;	
	
	@Override
	public String toString() {
		return this.getTitle();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description.replace("  ", " ");
	}

	/**
	 * @return the hasIcon
	 */
	public Boolean hasIcon() {
		return hasIcon;
	}

	/**
	 * @return the hasEvents
	 */
	public Boolean hasEvents() {
		return hasEvents;
	}

	/**
	 * @return the hasPromos
	 */
	public Boolean hasPromos() {
		return hasPromos;
	}

	/**
	 * @return the isForEating
	 */
	public Boolean isForEating() {
		return isForEating;
	}

	/**
	 * @return the isForDrinking
	 */
	public Boolean isForDrinking() {
		return isForDrinking;
	}

	/**
	 * @return the isForPartying
	 */
	public Boolean isForPartying() {
		return isForPartying;
	}

	/**
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}

	/**
	 * @param icon the icon to set
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}

	/**
	 * @param hasIcon the hasIcon to set
	 */
	public void setHasIcon(Boolean hasIcon) {
		this.hasIcon = hasIcon;
	}

	/**
	 * @return the isFavourite
	 */
	public Boolean isFavorite() {
		return isFavorite;
	}

	/**
	 * @param isFavourite the isFavourite to set
	 */
	public void setIsFavorite(Boolean isFavourite) {
		this.isFavorite = isFavourite;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	/**
	 * @param affinity the affinity to set
	 */
	public void setAffinity(Float affinity) {
		this.affinity = affinity;
	}

	/**
	 * @return the placeId
	 */
	public Long getPlaceId() {
		return placeId;
	}

	/**
	 * @param placeId the placeId to set
	 */
	public void setPlaceId(Long placeId) {
		this.placeId = placeId;
	}

	/**
	 * @return the placeType
	 */
	public String getPlaceType() {
		return placeType;
	}

	/**
	 * @param placeType the placeType to set
	 */
	public void setPlaceType(String placeType) {
		this.placeType = placeType;
	}

	/**
	 * @return the placeTypeId
	 */
	public Integer getPlaceTypeId() {
		return placeTypeId;
	}

	/**
	 * @param placeTypeId the placeTypeId to set
	 */
	public void setPlaceTypeId(Integer placeTypeId) {
		this.placeTypeId = placeTypeId;
	}

	/**
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(String number) {
		this.number = number;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the cityId
	 */
	public Integer getCityId() {
		return cityId;
	}

	/**
	 * @param cityId the cityId to set
	 */
	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	/**
	 * @return the postalCode
	 */
	public String getPostalCode() {
		return postalCode;
	}

	/**
	 * @param postalCode the postalCode to set
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	/**
	 * @return the web
	 */
	public String getWeb() {
		return web;
	}

	/**
	 * @param web the web to set
	 */
	public void setWeb(String web) {
		this.web = web;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the visits
	 */
	public Integer getVisits() {
		return visits;
	}

	/**
	 * @param visits the visits to set
	 */
	public void setVisits(Integer visits) {
		this.visits = visits;
	}

	/**
	 * @return the favourites
	 */
	public Integer getFavorites() {
		return favorites;
	}

	/**
	 * @param favourites the favourites to set
	 */
	public void setFavorites(Integer favourites) {
		this.favorites = favourites;
	}

	/**
	 * @param hasEvents the hasEvents to set
	 */
	public void setHasEvents(Boolean hasEvents) {
		this.hasEvents = hasEvents;
	}

	/**
	 * @param hasPromos the hasPromos to set
	 */
	public void setHasPromos(Boolean hasPromos) {
		this.hasPromos = hasPromos;
	}

	@Override
	public Double getLatitude() {
		return this.latitude;
	}

	@Override
	public Double getLongitude() {
		return this.longitude;
	}

	@Override
	public Float getAffinity() {
		return this.affinity;
	}

	/**
	 * Establece el nombre del local.
	 * @param name Nombre del local
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Establece la descripción del local.
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;		
	}

	/**
	 * Establece la calle del local.
	 * @param street La calle del local.
	 */
	public void setStreet(String street) {
		this.street = street;		
	}
	
	/**
	 * Obtiene la calle del local.
	 * @return La calle del local.
	 */
	public String getStreet() {
		return this.street;
	}

	/**
	 * @param isForEating the isForEating to set
	 */
	public void setIsForEating(Boolean isForEating) {
		this.isForEating = isForEating;
	}

	/**
	 * @param isForDrinking the isForDrinking to set
	 */
	public void setIsForDrinking(Boolean isForDrinking) {
		this.isForDrinking = isForDrinking;
	}

	/**
	 * @param isForPartying the isForPartying to set
	 */
	public void setIsForPartying(Boolean isForPartying) {
		this.isForPartying = isForPartying;
	}

	/**
	 * @return the friendVisits
	 */
	public Integer getFriendVisits() {
		return friendVisits;
	}

	/**
	 * @param friendVisits the friendVisits to set
	 */
	public void setFriendVisits(Integer friendVisits) {
		this.friendVisits = friendVisits;
	}

	@Override
	public String getTitle() {
		return this.name;
	}

	@Override
	public String getSubtitle() {
		return this.placeType;
	}

	@Override
	public String getMapDescription(Activity context) {
		//String result = this.street + " " + this.number + " " + this.city;
		String result = this.address;
		String distanceStr = "";
		DataFacade dataFacade = new DataFacade(context);
		if (dataFacade.getUserPosition() != null) {
			distanceStr = GeoHelper.distanceToString(dataFacade.getUserPosition().getLatitude(), dataFacade.getUserPosition().getLongitude(), this.latitude, this.longitude);
		}
		return (result + "\n" + distanceStr).trim();
	}

	@Override
	public String getImageUrl() {
		String result = "";
		if (this.hasIcon) {
			result = "placeid=" + this.placeId;
			if (this.affinity < UseFeeling.THRESHOLD_YELLOW) result += "&red";
			else if (this.affinity < UseFeeling.THRESHOLD_GREEN) result += "&yellow";
			else result += "&green";
		}
		else {
			String prefix = "";
			if (this.affinity < UseFeeling.THRESHOLD_YELLOW) prefix = "red_";
			else if (this.affinity < UseFeeling.THRESHOLD_GREEN) prefix = "yellow_";
			else prefix = "green_";
			result = "defaulticon=" + prefix + this.icon;
		}
		if (this.hasEvents) result += "&hasevents";
		if (this.hasPromos) result += "&haspromos";
		return result;
	}
	
	@Override 
	public String getMapImageUrl() {
		String prefix = "";
		if (this.affinityStr.equalsIgnoreCase(UseFeeling.GREEN.toString())) prefix = "green_";
		else if (this.affinityStr.equalsIgnoreCase(UseFeeling.YELLOW.toString())) prefix = "yellow_";
		else prefix = "red_";
		String result = "defaulticon=" + prefix + this.icon;
		if (this.hasEvents) result += "&hasevents";
		if (this.hasPromos) result += "&haspromos";
		return result;
	}
	
	/**
	 * Obtiene la URL de la imagen.
	 * @param hasCheckin Si el local tiene checkin.
	 * @return La URL de la imagen de icono.
	 */
	public String getMapImageUrl(boolean hasCheckin) {
		String result = this.getMapImageUrl();
		if (hasCheckin) result += "&checkins";
		return result;
	}

	/**
	 * @return the completed
	 */
	public Boolean isCompleted() {
		return completed;
	}

	/**
	 * @param completed the completed to set
	 */
	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}

	/**
	 * @return the facebook
	 */
	public String getFacebook() {
		return facebook;
	}

	/**
	 * @param facebook the facebook to set
	 */
	public void setFacebook(String facebook) {
		this.facebook = facebook;
	}

	public String getAddress() {
		if (!address.trim().equals("")) return address;
		return 
			this.street.trim() + (this.street.trim().equals("") ? "" : " ") + 
			this.number.trim() + (this.number.trim().equals("") ? "" : " ") + 
			this.postalCode.trim() + (this.postalCode.trim().equals("") ? "" : " ") + 
			this.city.trim();
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Boolean getHasCheckin() {
		return hasCheckin;
	}

	public void setHasCheckin(Boolean hasCheckin) {
		this.hasCheckin = hasCheckin;
	}

	@Override
	public int compareTo(Place other) {
		if (other == null) return -1;
		return other.getPlaceId().compareTo(this.placeId);
	}

	/**
	 * Obtiene el nombre de imagen de portada del local.
	 * @return La imagen del nombre de portada del local.
	 */
	public String getPortraitImage() {
		String result = "";
		if (this.hasIcon) {
			result = "placeid=" + this.placeId;
		}
		else {
			result = "portrait=";
			if (this.icon.contains("bar.png")) result += "cerveceria.png";
			else if (this.icon.contains("bar_coktail.png")) result += "copas.png";
			else if (this.icon.contains("billiard_2.png")) result += "billares.png";
			else if (this.icon.contains("bowling.png")) result += "bolera.png";
			else if (this.icon.contains("coffee.png")) result += "cafeteria.png";
			else if (this.icon.contains("dancinghall.png")) result += "discoteca.png";
			else if (this.icon.contains("fast_food.png")) result += "hamburgueseria.png";
			else if (this.icon.contains("gourmet_0star.png")) result += "restaurante.png";
			else if (this.icon.contains("restaurante_buffet.png")) result += "restaurante.png";
			else if (this.icon.contains("restaurante_chinese.png")) result += "restaurante.png";
			else if (this.icon.contains("restaurante_italian.png")) result += "restaurante.png";
			else if (this.icon.contains("restaurante_mexican.png")) result += "restaurante.png";
			else if (this.icon.contains("hotel_0star.png")) result += "hotel.png";
			else if (this.icon.contains("icecream.png")) result += "heladeria.png";
			else if (this.icon.contains("music_live.png")) result += "discoteca.png";
			else if (this.icon.contains("pizzaria.png")) result += "pizzeria.png";
			else if (this.icon.contains("restaurant.png")) result += "restaurante.png";
			else if (this.icon.contains("sandwich_2.png")) result += "hamburgueseria.png";
			else if (this.icon.contains("theater.png")) result += "teatro.png";
			else if (this.icon.contains("winebar.png")) result += "vinoteca.png";
			else result += "discoteca.png";
		}
		return result;
	}

	/**
	 * Devuelve la dirección de twitter del local.
	 * @return El twitter del local.
	 */
	public String getTwitter() {
		return this.twitter;
	}
	
	/**
	 * Establece la dirección de twitter del local.
	 * @param twitter El twitter del local.
	 */
	public void setTwitter(String twitter) {
		this.twitter = twitter;
	}

	/**
	 * Obtiene la URL de facebook formateada para que la entienda la app oficial
	 * @return La URL de facebook
	 */
	public String getFacebookAppUrl() {
		String result = "fb://page/";
		result += this.facebook.substring(this.facebook.lastIndexOf("/")+1);
		return result;
	}

	/**
	 * @return the affinityStr
	 */
	public String getAffinityStr() {
		if (affinityStr == null || affinityStr.trim().equals("")) {
			if (this.affinity < 0.3) this.affinityStr = UseFeeling.RED.toString();
			else if (this.affinity < 0.6) this.affinityStr = UseFeeling.YELLOW.toString();
			else this.affinityStr = UseFeeling.GREEN.toString();
		}
		return affinityStr;
	}

	/**
	 * @param affinityStr the affinityStr to set
	 */
	public void setAffinityStr(String affinityStr) {
		this.affinityStr = affinityStr;
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

	@Override
	public Long getId() {
		return this.placeId;
	}

}
