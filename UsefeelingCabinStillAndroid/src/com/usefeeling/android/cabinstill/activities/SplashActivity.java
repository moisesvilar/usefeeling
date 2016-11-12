package com.usefeeling.android.cabinstill.activities;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.KeyEvent;

import com.actionbarsherlock.app.SherlockActivity;
import com.usefeeling.android.R;
import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.facades.ApplicationFacade;
import com.usefeeling.android.cabinstill.facades.DataFacade;
import com.usefeeling.android.cabinstill.facades.MessagesFacade;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;
import com.usefeeling.android.cabinstill.tasks.GetInitialData;

/**
 * Activity de obtención de datos iniciales.<br>
 * Obtiene los datos de la cuenta del usuario, así como las propuestas sugeridas en ese momento y la posición de los amigos del usuario.<br>
 * @author Moisés Vilar.
 *
 */
public class SplashActivity extends SherlockActivity implements OnTaskCompleted {
	
	private DataFacade dataFacade;
	
	/**
	 * Listener de escucha de reintento de descarga de datos iniciales
	 */
	private OnClickListener retryListener = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			new GetInitialData(SplashActivity.this, SplashActivity.this).execute();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.splash);
		this.dataFacade = new DataFacade(this);
        new GetInitialData(this, this).execute();
	}

	@Override
	public void onTaskCompleted(Object o) {
		if (o == null) {
			MessagesFacade.showDialog(this, "", this.getString(R.string.quit_confirmation), MessagesFacade.MessageButtons.YES_NO, new OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					ApplicationFacade.kill(SplashActivity.this);
				}
			}, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {}
			});
			return;
		}
		Result result = (Result)o;
		if (result.getCode() != ResultCodes.Ok) {
			if (result.getDatabaseCode() == ResultCodes.DatabaseCodes.UserError) {
				this.dataFacade.remove();
				ApplicationFacade.startActivity(this, MainActivity.class);
				this.finish();
			}
			MessagesFacade.showDialog(this, "", result.getMessage(), MessagesFacade.MessageButtons.RETRY, this.retryListener, null);
			return;
		}
		ApplicationFacade.copyDatabaseToSdCard(this);
		ApplicationFacade.startActivity(this, MapActivity.class);
		this.finish();
	}
    
	/**
	 * Manejador del evento onKeyDown, que se lanza cuando se presiona una tecla.<br><br>
	 * En la reescritura de este método, se añade funcionalidad al presionado del botón <i>BACK</i>,
	 * cerrando la ventana transparente de información si es que se encuentra visible.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			MessagesFacade.showDialog(this, "", this.getString(R.string.quit_confirmation), MessagesFacade.MessageButtons.YES_NO, new OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					ApplicationFacade.kill(SplashActivity.this);
				}
			}, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {}
			});
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
