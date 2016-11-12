package com.usefeeling.android.cabinstill.api;

/**
 * Valores de código de resultados de operación en el sistema UseFeeling.
 * @author Moisés Vilar.
 *
 */
public final class ResultCodes {
	
	public final class DatabaseCodes {
		public static final int UserError = -1;
		public static final int PasswordEquals = -3;
		public static final int LengthPassword = -4;
		public static final int WrongPassword = -5;
		public static final int UserExists = -15;
		public static final int EmailError = -32;
		public static final int AgeError = -34;
	}
	
	/**
	 * La operación ha concluído con éxito.
	 */
	public static final int Ok = 0;
	/**
	 * Se ha lanzado la excepción ClassNotFoundException
	 */
	public static final int ClassNotFoundException = -1;
	/**
	 * Se ha lanzado la excepción SQLException
	 */
	public static final int SQLException = -2;
	/**
	 * El nombre de usuario no pertenece a ningún usuario en UseFeeling.
	 */
	public static final int UserNameError = -3;
	/**
	 * La contraseña para este usuario no es correcta.
	 */
	public static final int PasswordError = -4;
	/**
	 * Ninguna fila ha sido afectada por la operación.
	 */
	public static final int NoRowAffected = -5;
	/**
	 * Más de una fila ha sido afectada por la operación.
	 */
	public static final int MoreThanOneRowAffected = -6;
	/**
	 * Se ha producido un error no definido.
	 */
	public static final int NotDefinedError = -7;
	/**
	 * Se ha lanzado la excepción FileNotFoundException
	 */
	public static final int FileNotFoundException = -8;
	/**
	 * Se ha lanzado la excepción IOException
	 */
	public static final int IOException = -9;
	/**
	 * Error de autentificación. O el nombre de usuario o la contraseña no son correctos.
	 */
	public static final int AuthenticationError = -10;
	/**
	 * Se ha lanzado la excepción FileUploadException
	 */
	public static final int FileUploadException = -11;
	/**
	 * Error de privilegios. El usuario ha intentado ejecutar una operación para la que no tiene privilegios suficientes.
	 */
	public static final int PrivilegesError = -12;
	/**
	 * El sello temporal suministrado en la consulta es demasiado antiguo.
	 */
	public static final int TimestampError = -13;
	/**
	 * Se ha lanzado la excepción NumberFormatException
	 */
	public static final int NumberFormatException = -14;
	/**
	 * Se ha lanzado la excepción ArithmeticException
	 */
	public static final int ArithmeticException = -15;
	/**
	 * Se ha lanzado una excepción
	 */
	public static final int Exception = -16;
	/**
	 * No se ha encontrado una clave Diffie-Hellman para este dispositivo.
	 */
	public static final int NoKeyStored = -17;
	/**
	 * Se ha lanzado la excepción InvalidKeyException
	 */
	public static final int InvalidKeyException = -18;
	/**
	 * Se ha lanzado la excepción NoSuchAlgorithmException
	 */
	public static final int NoSuchAlgorithmException = -19;
	/**
	 * Se ha lanzado la excepción NoSuchPaddingException
	 */
	public static final int NoSuchPaddingException = -20;
	/**
	 * Se ha lanzado la excepción UnsupportedEncodingException
	 */
	public static final int UnsupportedEncodingException = -21;
	/**
	 * Se ha lanzado la excepción IllegalBlockSizeException
	 */
	public static final int IllegalBlockSizeException = -22;
	/**
	 * Se ha lanzado la excepción BadPaddingException
	 */
	public static final int BadPaddingException = -23;
	/**
	 * El usuario no tiene definida una foto de perfil.
	 */
	public static final int UserHasNoPicture = -24;
	/**
	 * La posición del usuario no se encuentra disponible en este momento.
	 */
	public static final int PositionNotAvailable = -25;
	/**
	 * El usuario no ha definido una dirección de correo electrónico.
	 */
	public static final int NoEmailAddressDefined = -26;
	/**
	 * Se ha lanzado la excepción JsonSyntaxException.
	 */
	public static final int JsonSyntaxException = -27;
	/**
	 * Se ha lanzado la excepción IllegalStateException.
	 */
	public static final int IllegalStateException = -28;
	/**
	 * Se ha lanzado la excepción URISyntaxException.
	 */
	public static final int URISyntaxException = -29;
	/**
	 * Se ha lanzado la excepción ArrayIndexOutOfBoundsException.
	 */
	public static final int ArrayIndexOutOfBoundsException = -30;
	/**
	 * No se han definido todos los parámetros necesarios para esta operación.
	 */
	public static final int ParametersError = -31;
	/**
	 * Se ha lanzado la excepción InvalidAlgorithmParameterException.
	 */
	public static final int InvalidAlgorithmParameterException = -32;
	/**
	 * Se ha lanzado la excepción InvalidKeySpecException.
	 */
	public static final int InvalidKeySpecException = -33;
	/**
	 * Se ha lanzado la excepción ClientProtocolException.
	 */
	public static final int ClientProtocolException = -34;
	/**
	 * El propietario ya existe en UseFeeling.
	 */
	public static final int OwnerExists = -35;
	/**
	 * Se ha detectado un error al procesar la rutina en la base de datos.
	 */
	public static final int DatabaseRutineError = -36;
	/**
	 * Se ha lanzado la excepción AddressExcepcion.
	 */
	public static final int AddressException = -37;
	/**
	 * Se ha lanzado la excepción MessagingException.
	 */
	public static final int MessagingException = -38;
	/**
	 * Se ha producido un error al procesar la llamada en el servidor XMPP.
	 */
	public static final int XmppError = -39;
	/**
	 * Se ha lanzado la excepción DocumentException.
	 */
	public static final int DocumentException = -40;
	/**
	 * Se ha lanzado el error OutOfMemoryError. 
	 */
	public static final int OutOfMemoryError = -41;
	/**
	 * Se ha producido un error al acceder a la caché.
	 */
	public static final int CacheError = -42;
}


