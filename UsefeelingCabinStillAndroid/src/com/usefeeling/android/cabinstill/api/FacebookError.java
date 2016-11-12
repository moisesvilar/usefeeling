package com.usefeeling.android.cabinstill.api;

/**
 * Clase de error para peticiones HTTP a la API Graph de Facebook que devuelven errores.
 * @author Moisés Vilar
 *
 */
public class FacebookError {

	private String message;
	private String type;
	private int code;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	
	
	
}
