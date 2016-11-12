package com.usefeeling.android.cabinstill.api;

import java.io.Serializable;

/**
 * Pojo para resultados de operaciones en el sistema UseFeeling.
 * @author Moisés Vilar.
 *
 */
public class Result implements Serializable {
	
	private static final long serialVersionUID = 3926107471514555741L;
	private Integer code;
	private String message;
	private String payload;
	
	/**
	 * Constructor por defecto.
	 */
	public Result(){
		this.code = null;
		this.message = "";
		this.payload = "";
	}
	
	/**
	 * Constructor específico.
	 * @param c Código del resultado.
	 * @param m Mensaje.
	 * @param p Carga útil.
	 */
	public Result(int c, String m, String p){
		this.code = c;
		this.message = m;
		this.payload = p;
	}

	/**
	 * Constructor específico.
	 * @param code Código de resultado.
	 * @param message Mensaje.
	 */
	public Result(int code, String message) {
		this.code = code;
		this.message = message;
	}

	/**
	 * @return the code
	 */
	public Integer getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(Integer code) {
		this.code = code;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		if (this.code != ResultCodes.DatabaseRutineError) return message;
		int errorCode = Integer.parseInt(this.message.substring(this.message.lastIndexOf(':') + 1).trim());
		switch (errorCode) {
		case -1:
			return "El usuario no existe";
		case -2:
			return "Buen intento... pero no eres administrador!!!";
		case -3:
			return "La nueva contraseña no puede ser igual a la antigua";
		case -4:
			return "La contraseña debe tener al menos 6 caraceteres";
		case -5:
			return "La contraseña no es correcta";
		case -6:
			return "El local especificado no existe";
		case -7:
			return "La solicitud de amistad no existe";
		case -8:
			return "El remitente de la solicitud de amistad no existe";
		case -9:
			return "El receptor de la solicitud de amistad no existe";
		case -10:
			return "Los usuarios ya son amigos";
		case -15:
			return "Ya existe un usuario con esa dirección de correo electrónico";
		case -17:
			return "Ya existe una solicitud de amistad entre estos dos usuarios";
		case -32:
			return "La dirección de correo electrónico debe ser superior a 6 caracteres";
		case -34:
			return "Debes ser mayor de edad para crear una cuenta en UseFeeling";
		default:
			return this.message;
		}
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the payload
	 */
	public String getPayload() {
		return payload;
	}

	/**
	 * @param payload the payload to set
	 */
	public void setPayload(String payload) {
		this.payload = payload;
	}

	/**
	 * Retrieves the database error code.
	 * @return
	 */
	public int getDatabaseCode() {
		if (this.code != ResultCodes.DatabaseRutineError) return -1;
		int errorCode = Integer.parseInt(this.message.substring(this.message.lastIndexOf(':') + 1).trim());
		return errorCode;
	}

	
}
