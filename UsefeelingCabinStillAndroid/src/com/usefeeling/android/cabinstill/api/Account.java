package com.usefeeling.android.cabinstill.api;

import java.io.Serializable;

/**
 * POJO para datos de cuentas de usuario.
 * @author Moisés Vilar.
 *
 */
public class Account implements Serializable {
	private static final long serialVersionUID = -6329981322621752032L;
	private Long userid;
	private String email;
	private String name;
	private Long birthDate;
	private String gender;
	private Boolean favouritePlacesNotifications;
	private Boolean suggestedProposalsNotifications;
	private Boolean nearProposalsNotifications;
	private String phone;
	private String facebookid;
	
	/**
	 * Constructor.
	 */
	public Account() {
		this.birthDate = 0L;
		this.email = "";
		this.facebookid = "";
		this.favouritePlacesNotifications = false;
		this.gender = "";
		this.name = "";
		this.nearProposalsNotifications = false;
		this.phone = "";
		this.suggestedProposalsNotifications = false;
		this.userid = 0L;
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
	 * @return the birthDate
	 */
	public Long getBirthDate() {
		return birthDate;
	}
	/**
	 * @param birthDate the birthDate to set
	 */
	public void setBirthDate(Long birthDate) {
		this.birthDate = birthDate;
	}
	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}
	/**
	 * @param gender the gender to set
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}
	/**
	 * @return the favouritePlacesNotifications
	 */
	public Boolean getFavouritePlacesNotifications() {
		return favouritePlacesNotifications;
	}
	/**
	 * @param favouritePlacesNotifications the favouritePlacesNotifications to set
	 */
	public void setFavouritePlacesNotifications(Boolean favouritePlacesNotifications) {
		this.favouritePlacesNotifications = favouritePlacesNotifications;
	}
	/**
	 * @return the suggestedProposalsNotifications
	 */
	public Boolean getSuggestedProposalsNotifications() {
		return suggestedProposalsNotifications;
	}
	/**
	 * @param suggestedProposalsNotifications the suggestedProposalsNotifications to set
	 */
	public void setSuggestedProposalsNotifications(
			Boolean suggestedProposalsNotifications) {
		this.suggestedProposalsNotifications = suggestedProposalsNotifications;
	}
	/**
	 * @return the nearProposalsNotifications
	 */
	public Boolean getNearProposalsNotifications() {
		return nearProposalsNotifications;
	}
	/**
	 * @param nearProposalsNotifications the nearProposalsNotifications to set
	 */
	public void setNearProposalsNotifications(Boolean nearProposalsNotifications) {
		this.nearProposalsNotifications = nearProposalsNotifications;
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
	 * @return the facebookid
	 */
	public String getFacebookid() {
		return facebookid;
	}
	/**
	 * @param facebookid the facebookid to set
	 */
	public void setFacebookid(String facebookid) {
		this.facebookid = facebookid;
	}
	
	/**
	 * Obtiene la URL a la imagen de perfil del usuario.
	 * @return La URL a la foto de perfil del usuario.
	 */
	public String getImageUrl() {
		return "userid=" + this.userid;
	}

	/**
	 * Devuelve los datos del usuario a partir de los datos de su cuenta.
	 * @return Los datos del usuario.
	 */
	public User toUser() {
		User user = new User();
		user.setAffinity(1.0f);
		user.setAffinityStr(UseFeeling.GREEN.toString());
		user.setBirthdate(this.birthDate);
		user.setEmail(this.email);
		user.setFacebookId(this.facebookid);
		user.setGender(this.gender);
		user.setIsFriend(true);
		user.setName(this.name);
		user.setPhone(this.phone);
		user.setUserid(this.userid);
		return user;
	}
}
