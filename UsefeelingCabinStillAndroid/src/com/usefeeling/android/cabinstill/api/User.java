package com.usefeeling.android.cabinstill.api;

import java.io.Serializable;

/**
 * POJO para datos sobre usuarios.
 * @author Moisés Vilar.
 *
 */
public class User implements Serializable, Comparable<User> {

	private static final long serialVersionUID = -5260555726074275284L;
	private Long userid;
	private String name;
	private Boolean isFriend;
	private String phone;
	private String email;
	private Long birthdate;
	private String gender;
	private String facebookId;
	private Float affinity;
	private String affinityStr;

	/**
	 * Default constructor.
	 */
	public User() {}
	
	/**
	 * Constructor.
	 * @param userid The user's identifier.
	 * @param name The user's name.
	 * @param isFriend Whether the user is friend of the current app user.
	 */
	public User(Long userid, String name, Boolean isFriend) {
		this.userid = userid;
		this.name = name;
		this.isFriend = isFriend;
	}
	
	/**
	 * Constructor
	 * @param userid The user's identifier.
	 * @param name The user's name.
	 * @param isFriend Whether the user is friend of the current app user.
	 * @param phone The user's phone.
	 * @param birthdate The user's birth date
	 * @param gender The user's gender.
	 * @param facebookId The user's facebook identifier.
	 * @param affinity The user's affinity with the current app user.
	 * @param affinityStr The user's affinity string with the current app user.
	 */
	public User(Long userid, String name, Boolean isFriend, String phone, Long birthdate, String gender, String facebookId, Float affinity, String affinityStr) {
		this.userid = userid;
		this.name = name;
		this.isFriend = isFriend;
		this.phone = phone;
		this.birthdate = birthdate;
		this.gender = gender;
		this.facebookId = facebookId;
		this.affinity = affinity;
		this.affinityStr = affinityStr;
	}

	@Override
	public int compareTo(User another) {
		if (this.userid != null && another.getUserid() != null) {
			return another.getUserid().compareTo(this.userid);	
		}
		else if (!this.name.equals("") && !another.getName().equals("") ) {
			return another.getName().compareTo(this.name);
		}
		else return 0;
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
	 * @return the isFriend
	 */
	public Boolean isFriend() {
		return isFriend;
	}

	/**
	 * @param isFriend the isFriend to set
	 */
	public void setIsFriend(Boolean isFriend) {
		this.isFriend = isFriend;
	}

	/**
	 * Devuelve la URL de la imagen de perfil del usuario.
	 * @return La URL de la imagen de perfil del usuario.
	 */
	public String getImageUrl() {
		return "userid=" + this.userid;
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
	 * @return the birthdate
	 */
	public Long getBirthdate() {
		return birthdate;
	}

	/**
	 * @param birthdate the birthdate to set
	 */
	public void setBirthdate(Long birthdate) {
		this.birthdate = birthdate;
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
	 * @return the facebookId
	 */
	public String getFacebookId() {
		return facebookId;
	}

	/**
	 * @param facebookId the facebookId to set
	 */
	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
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
	 * @return the isFriend
	 */
	public Boolean getIsFriend() {
		return isFriend;
	}

	/**
	 * @param affinity the affinity to set
	 */
	public void setAffinity(Float affinity) {
		this.affinity = affinity;
	}

	/**
	 * @return the affinity
	 */
	public Float getAffinity() {
		return affinity;
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

}
