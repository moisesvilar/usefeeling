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
 * Crea una nueva cuenta en UseFeeling con los datos proporcionados.<br><br>
 * Al ejecutarla, invocando su método <font face="courier new">execute</font>, se le deben pasar los siguientes parámetros, por orden:
 * <li>Nombre y apellidos del usuario, de tipo <font face="courier new">String</font>.</li>
 * <li>Fecha de nacimiento del usuario, de tipo <font face="courier new">String</font>, siguiendo el formato dd-MM-yyyy.</li>
 * <li>Sexo del usuario, de tipo <font face="courier new">String</font>.</li>
 * <li>Correo electrónico del usuario, de tipo <font face="courier new">String</font>.</li>.
 * <li>Contraseña de acceso, de tipo <font face="courier new">String</font>.</li>.
 * <li>Foto de perfil, de tipo <font face="courier new">Bitmap</font>.</li>
 * <li>Identificador de facebook del usuario, de tipo <font face="courier new">String</font>. Si no se especifica, dejar como una cadena vacía.</li>
 * <li>Número de móvil del usuario, de tipo <font face="courier new">String</font>. Si no se especifica, dejar como una cadena vacía.</li>
 * @author Moisés Vilar.
 *
 */
public class CreateAccount extends AbstractTask{

	private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
	
	/**
	 * Constructor.
	 * @param activity Activity que ha ejecutado la tarea. 
	 * @param listener Objeto a la escucha de resultados.
	 */
	public CreateAccount(Activity activity, OnTaskCompleted listener, Object... params) {
		super(activity, listener, params);
	}

	@Override
	public void run() {
		this.onPreExecute();
		try {
			//Obtenemos datos del usuario
			String name = (String)this.mParams[0];
			String birthDateStr = (String)this.mParams[1];
			String gender = (String)this.mParams[2];
			String email = (String)this.mParams[3];
			String password = (String)this.mParams[4];
			Bitmap picture = null;
			if (this.mParams[5] != null) picture = (Bitmap)this.mParams[5];
			String facebookid = (String)this.mParams[6];
			String phone = (String)this.mParams[7];
			Long birthdate;
			try {
				birthdate = this.sdf.parse(birthDateStr).getTime();
			} catch (ParseException e) {
				this.result = new Result(ResultCodes.Exception, ResultMessages.Exception(e));
				return;
			}
			byte[] pictureBytes = null;
			if (picture != null) {
				pictureBytes = ImageManager.compress(picture);
				if (pictureBytes == null) {
					this.result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
					return;
				}
			}
			//Creamos usuario
			this.result = this.mUsefeeling.createUser(email, password, name, birthdate, gender, phone, facebookid, pictureBytes);
			if (result.getCode() != ResultCodes.Ok) return;
			this.mUsefeeling.setUserId(Long.parseLong(result.getPayload()));
			this.mUsefeeling.setPassword(password);
		} catch (Throwable t) {
			result = new Result(ResultCodes.Exception, t.getMessage());
			return;
		} finally {
			this.onPostExecute(this.result);
		}
	}
}