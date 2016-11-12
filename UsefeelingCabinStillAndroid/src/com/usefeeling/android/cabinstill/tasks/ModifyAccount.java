package com.usefeeling.android.cabinstill.tasks;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.Activity;
import android.graphics.Bitmap;

import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.api.ResultMessages;
import com.usefeeling.android.cabinstill.imageloader.ImageManager;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;

/**
 * Modifica los datos de la cuenta de UseFeeling del usuario.<br><br>
 * Al ejecutarla, invocando su método <font face="courier new">execute</font>, se le deben pasar los siguientes parámetros, por orden:
 * <li>Nombre y apellidos del usuario, de tipo <font face="courier new">String</font>.</li>
 * <li>Fecha de nacimiento del usuario, de tipo <font face="courier new">String</font>, siguiendo el formato dd-MM-yyyy.</li>
 * <li>Sexo del usuario, de tipo <font face="courier new">String</font>.</li>
 * <li>Correo electrónico del usuario, de tipo <font face="courier new">String</font>.</li>.
 * <li>Foto de perfil, de tipo <font face="courier new">Bitmap</font>.</li>
 * <li>Número de móvil del usuario, de tipo <font face="courier new">String</font>. Si no se especifica, dejar como una cadena vacía.</li>
 * @author Moisés Vilar.
 *
 */
public class ModifyAccount extends AbstractTask {
	private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
	private String name;
	private Long birthdate;
	private String gender;
	private String email;
	private String phone;
	
	/**
	 * Constructor.
	 * @param activity Activity que ha ejecutado la tarea. 
	 * @param listener Objeto a la escucha de resultados.
	 */
	public ModifyAccount(Activity activity, OnTaskCompleted listener, Object... params) {
		super(activity, listener, params);
	}
		
	@Override
	public void run() {
		this.onPreExecute();
		try {
			//Obtenemos datos del usuario
			name = (String)this.mParams[0];
			String birthDateStr = (String)this.mParams[1];
			gender = (String)this.mParams[2];
			email = (String)this.mParams[3];
			Bitmap picture = null;
			if (this.mParams[4] != null) picture = (Bitmap)this.mParams[4];
			phone = (String)this.mParams[5];
			try {
				birthdate = this.sdf.parse(birthDateStr).getTime();
			} catch (ParseException e) {
				result = new Result(ResultCodes.Exception, ResultMessages.Exception(e));
				return;
			}
			//Creamos usuario
			result = this.mUsefeeling.updateUser(email, name, birthdate, gender, phone);
			if (result.getCode() != ResultCodes.Ok) return;
			//Subimos foto de perfil
			if (picture != null) {
				byte[] pictureBytes = ImageManager.compress(picture);
				if (pictureBytes == null) {
					result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
					return;
				}
				result = this.mUsefeeling.setProfilePicture(pictureBytes);
			}
		} catch (Throwable t) {
			result = new Result(ResultCodes.Exception, t.getMessage());
			return;
		} finally {
			this.onPostExecute(result);
		}
	}
}
