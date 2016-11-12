package com.usefeeling.android.cabinstill.api;

import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.InvalidKeySpecException;

import org.apache.http.client.ClientProtocolException;


/**
 * Texto de los mensajes de los resultados de las operaciones en el sistema UseFeeling.
 * @author Moisés Vilar.
 *
 */
public class ResultMessages {

	/**
	 * La operación ha concluído con éxito.
	 * @return Mensaje de error.
	 */
	public static final String Ok() {
		return "La operación ha concluído con éxito.";
	}
	
	/**
	 * Se ha lanzado la excepción ClassNotFoundException.
	 * @param e Excepción capturada.
	 * @return Mensaje de error.
	 */
	public static final String ClassNotFoundException(Exception e) {
		return "Se ha lanzado la excepción ClassNotFoundException. " + e.getMessage();
	}
	
	/**
	 * Se ha lanzado la excepción SQLException.
	 * @param e Excepción capturada.
	 * @return Mensaje de error.
	 */
	public static final String SQLException(Exception e) { 
		return "Se ha lanzado la excepción SQLException. " + e.getMessage();
	}
	
	/**
	 * El nombre de usuario no pertenece a ningún usuario en UseFeeling.
	 * @return Mensaje de error.
	 */
	public static final String UserNameError() { 
		return "El nombre de usuario no pertenece a ningún usuario en UseFeeling.";
	}
	
	/**
	 * La contraseña para este usuario no es correcta.
	 * @return Mensaje de error.
	 */
	public static final String PasswordError() { 
		return "La contraseña para este usuario no es correcta.";
	}
	
	/**
	 * Ninguna fila ha sido afectada por la operación.
	 * @return Mensaje de error.
	 */
	public static final String NoRowAffected() { 
		return "Ninguna fila ha sido afectada por la operación.";
	}
	
	/**
	 * Más de una fila ha sido afectada por la operación.
	 * @return Mensaje de error.
	 */
	public static final String MoreThanOneRowAffected() { 
		return "Más de una fila ha sido afectada por la operación.";
	}
	
	/**
	 * Se ha producido un error no definido.
	 * @return Mensaje de error.
	 */
	public static final String NotDefinedError() { 
		return "Ups... Parece que hay algún problema con la conexión...";
	}
	
	/**
	 * Se ha lanzado la excepción FileNotFoundException.
	 * @param e Excepción capturada.
	 * @return Mensaje de error.
	 */
	public static final String FileNotFoundException(Exception e) { 
		return "Se ha lanzado la excepción FileNotFoundException. " + e.getMessage();
	}
	
	/**
	 * Se ha lanzado la excepción IOException
	 * @param e Excepción capturada.
	 * @return Mensaje de error.
	 */
	public static final String IOException(Exception e) { 
		return "Se ha lanzado la excepción IOException. " + e.getMessage();
	}
	
	/**
	 * Error de autentificación. O el nombre de usuario o la contraseña no son correctos.
	 * @return Mensaje de error.
	 */
	public static final String AuthenticationError() { 
		return "Error de autentificación. O el nombre de usuario o la contraseña no son correctos.";
	}
	
	/**
	 * Se ha lanzado la excepción FileUploadException.
	 * @param e Excepción capturada.
	 * @return Mensaje de error.
	 */
	public static final String FileUploadException(Exception e) { 
		return "Se ha lanzado la excepción FileUploadException. " + e.getMessage();
	}
	
	/**
	 * Error de privilegios. El usuario ha intentado ejecutar una operación para la que no tiene privilegios suficientes.
	 * @return Mensaje de error.
	 */
	public static final String PrivilegesError() { 
		return "Error de privilegios. El usuario ha intentado ejecutar una operación para la que no tiene privilegios suficientes.";
	}
	
	/**
	 * El sello temporal suministrado en la consulta es demasiado antiguo.
	 * @return Mensaje de error.
	 */
	public static final String TimestampError(){
		return "El sello temporal suministrado en la consulta es demasiado antiguo.";
	}
	
	/**
	 * Se ha lanzado una excepción.
	 * @param e Excepción capturada.
	 * @return Mensaje de error.
	 */
	public static final String Exception(Exception e){
		return "Se ha lanzado una excepción. " + e.getMessage();
	}
	
	/**
	 * Se ha lanzado la excepción ArithmeticException.
	 * @param e Excepción capturada.
	 * @return Mensaje de error.
	 */
	public static final String ArithmeticException(Exception e){
		return "Se ha lanzado la excepción ArithmeticException. " + e.getMessage();
	}
	
	/**
	 * Se ha lanzado la excepción NumberFormatException.
	 * @param e Excepción capturada.
	 * @return Mensaje de error.
	 */
	public static final String NumberFormatException(Exception e){
		return "Se ha lanzado la excepción NumberFormatException. " + e.getMessage();
	}
	
	/**
	 * No se ha encontrado una clave Diffie-Hellman para este dispositivo.
	 * @return Mensaje de error.
	 */
	public static final String NoKeyStored(){
		return "No se ha encontrado una clave Diffie-Hellman para este dispositivo.";
	}
	
	/**
	 * Se ha lanzado la excepción InvalidKeyException
	 * @param e Excepción capturada.
	 * @return Mensaje de error.
	 */
	public static final String InvalidKeyException(Exception e){
		return "Se ha lanzado la excepción InvalidKeyException. " + e.getMessage();
	}
	
	/**
	 * Se ha lanzado la excepción NoSuchAlgorithmException
	 * @param e Excepción capturada.
	 * @return Mensaje de error.
	 */
	public static final String NoSuchAlgorithmException(Exception e){
		return "Se ha lanzado la excepción NoSuchAlgorithmException. " + e.getMessage();
	}
	
	/**
	 * Se ha lanzado la excepción NoSuchPaddingException
	 * @param e Excepción capturada.
	 * @return Mensaje de error.
	 */
	public static final String NoSuchPaddingException(Exception e){
		return "Se ha lanzado la excepción NoSuchPaddingException. " + e.getMessage();
	}
	
	/**
	 * Se ha lanzado la excepción UnsupportedEncodingException
	 * @param e Excepción capturada.
	 * @return Mensaje de error.
	 */
	public static final String UnsupportedEncodingException(Exception e){
		return "Se ha lanzado la excepción UnsupportedEncodingException. " + e.getMessage();
	}
	
	/**
	 * Se ha lanzado la excepción IllegalBlockSizeException
	 * @param e Excepción capturada.
	 * @return Mensaje de error.
	 */
	public static final String IllegalBlockSizeException(Exception e){
		return "Se ha lanzado la excepción IllegalBlockSizeException. " + e.getMessage();
	}
	
	/**
	 * Se ha lanzado la excepción BadPaddingException
	 * @param e Excepción capturada.
	 * @return Mensaje de error.
	 */
	public static final String BadPaddingException(Exception e){
		return "Se ha lanzado la excepción BadPaddingException. " + e.getMessage();
	}
	
	/**
	 * El usuario no tiene definida una foto de perfil.
	 * @return Mensaje de error.
	 */
	public static final String UserHasNoPicture(){
		return "El usuario no tiene definida una foto de perfil.";
	}
	
	/**
	 * La posición del usuario no se encuentra disponible en este momento.
	 * @return Mensaje de error.
	 */
	public static final String PositionNotAvailable(){
		return "La posición del usuario no se encuentra disponible en este momento.";
	}
	
	/**
	 * El usuario no ha definido una dirección de correo electrónico.
	 * @return Mensaje de error.
	 */
	public static final String NoEmailAddressDefined(){
		return "El usuario no ha definido una dirección de correo electrónico.";
	}
	
	/**
	 * Se ha lanzado la excepción JsonSyntaxException.
	 * @param e Excepción capturada.
	 * @return Mensaje de error.
	 */
	public static final String JsonSyntaxException(Exception e){
		return "Se ha lanzado la excepción JsonSyntaxException. " + e.getMessage();
	}

	/**
	 * Se ha lanzado la excepción IllegalStateException.
	 * @param e Excepción capturada.
	 * @return Mensaje de error.
	 */
	public static String IllegalStateException(IllegalStateException e) {
		return "Se ha lanzado la excepción IllegalStateException. " + e.getMessage();
	}

	/**
	 * Se ha lanzado la excepción URISyntaxException.
	 * @param e Excepción capturada.
	 * @return Mensaje de error.
	 */
	public static String URISyntaxException(URISyntaxException e) {
		return "Se ha lanzado la excepción URISyntaxException. " + e.getMessage();
	}

	/**
	 * Se ha lanzado la excepción ArrayIndexOutOfBoundsException.
	 * @param e Excepción capturada.
	 * @return Mensaje de error.
	 */
	public static String ArrayIndexOutOfBoundsException(ArrayIndexOutOfBoundsException e) {
		return "Se ha lanzado la excepción ArrayIndexOutOfBoundsException. " + e.getMessage();
	}

	/**
	 * No se han definido todos los parámetros necesarios para esta operación.
	 * @return Mensaje de error.
	 */
	public static String ParametersError() {
		return "No se han definido todos los parámetros necesarios para esta operación.";
	}

	/**
	 * Se ha lanzado la excepción InvalidAlgorithmParameterException.
	 * @param e Excepción capturada.
	 * @return Mensaje de error.
	 */
	public static String InvalidAlgorithmParameterException(InvalidAlgorithmParameterException e) {
		return "Se ha lanzado la excepción InvalidAlgorithmParameterException. " + e.getMessage();
	}

	/**
	 * Se ha lanzado la excepción InvalidKeySpecException.
	 * @param e Excepción capturada.
	 * @return Mensaje de error.
	 */
	public static String InvalidKeySpecException(InvalidKeySpecException e) {
		return "Se ha lanzado la excepción InvalidKeySpecException. " + e.getMessage();
	}

	/**
	 * Se ha lanzado la excepción ClientProtocolException.
	 * @param e Excepción capturada.
	 * @return Mensaje de error.
	 */
	public static String ClientProtocolException(ClientProtocolException e) {
		return "Se ha lanzado la excepción ClientProtocolException. " + e.getMessage();
	}
	
	/**
	 * El propietario ya existe en la base de datos.
	 * @return Mensaje de error.
	 */
	public static String OwnerExists() {
		return "Un propietario con la misma dirección de correo electrónico ya existe en UseFeeling.";
	}
	
	/**
	 * Se ha detectado un error al procesar la rutina en la base de datos.
	 * @param errorcode Código del error detectado en la rutina de la base de datos.
	 * @return Mensaje de error.
	 */
	public static String DatabaseRutineError(short errorcode) {
		return "Se ha detectado un error al procesar la rutina en la base de datos: " + errorcode;
	}

	/**
	 * Se ha detectado un error al procesar la rutina en la base de datos.
	 * @param errorcode Código del error detectado en la rutina de la base de datos.
	 * @return Mensaje de error.
	 */
	public static String DatabaseRutineError(String errorcode) {
		return "Se ha detectado un error al procesar la rutina en la base de datos: " + errorcode;
	}
	
	/**
	 * Se ha producido un error al procesar la llamada al servidor XMPP.
	 * @param text Mensaje de error del servidor XMPP.
	 * @return Mensaje de error.
	 */
	public static String XmppError(String text) {
		return "Se ha producido un error al procesar la llamada al servidor XMPP. " + text;
	}
	
	/**
	 * Se ha lanzado el error OutOfMemoryError.
	 * @param e Error OutOfMemoryError.
	 * @return Mensaje de error.
	 */
	public static String OutOfMemoryError(OutOfMemoryError e) {
		return "Se ha lanzado el error OutOfMemoryError: " + e.getMessage();
	}
}


