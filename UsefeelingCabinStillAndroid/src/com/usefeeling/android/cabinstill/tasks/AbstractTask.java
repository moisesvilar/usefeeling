package com.usefeeling.android.cabinstill.tasks;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Build;

import com.usefeeling.android.R;
import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.api.ResultMessages;
import com.usefeeling.android.cabinstill.api.UseFeeling;
import com.usefeeling.android.cabinstill.facades.DataFacade;
import com.usefeeling.android.cabinstill.facades.SharedPreferencesFacade;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;

/**
 * Clase abstracta de tareas a ejecutar en segundo plano
 * @author Moisés Vilar
 *
 */
public abstract class AbstractTask implements Runnable {

	protected UseFeeling mUsefeeling;
	protected SharedPreferencesFacade mPrefs;
	protected ProgressDialog mProgress;
	protected Activity mContext;
	protected Context mCtx;
	protected OnTaskCompleted mListener;
	protected Object[] mParams;
	protected Result result = null;
	protected DataFacade mDataFacade;
	
	private static final int corePoolSize = 60;
	private static final int maximumPoolSize = 80;
	private static final int keepAliveTime = 10;

	private static final BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
	private static final Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
	
	/**
	 * Constructor.
	 * @param context Contexto desde el que se instacia el hilo.
	 */
	public AbstractTask(Activity context, OnTaskCompleted listener, Object... params) {
		this.mContext = context;
		this.mListener = listener;
		this.mParams = params;
		this.mPrefs = new SharedPreferencesFacade(mContext);
		this.mUsefeeling = new UseFeeling(this.mPrefs.getUserId(), this.mPrefs.getPassword());
		this.mDataFacade = new DataFacade(this.mContext);
		this.result = new Result(ResultCodes.Ok, ResultMessages.Ok());
	}
	
	/**
	 * Constructor.
	 * @param context Contexto desde el que se instacia el hilo.
	 */
	public AbstractTask(Context context, OnTaskCompleted listener, Object... params) {
		this.mCtx = context;
		this.mListener = listener;
		this.mParams = params;
		this.mPrefs = new SharedPreferencesFacade(mCtx);
		this.mUsefeeling = new UseFeeling(this.mPrefs.getUserId(), this.mPrefs.getPassword());
	}
	
	/**
	 * Ejecuta la tarea. En el caso de que la versión de Android sea superior 
	 * a Honeycomb, la ejecuta en el pool de ejecución.
	 */
	public void execute() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			threadPoolExecutor.execute(this);
		}
		else {
			new Thread(this).start();
		}
	}
	
	/**
	 * Listener de cancelación.
	 */
	protected OnCancelListener cancelListener = new DialogInterface.OnCancelListener() {
		@Override
		public void onCancel(DialogInterface dialog) {
			onPostExecute(null);
		}
	};
	
	/**
	 * Tareas a realizar en el hilo de UI previas a la ejecución.
	 */
	protected void onPreExecute() {
		if (this.mContext != null) {
			this.mContext.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						mProgress = ProgressDialog.show(mContext, "", mContext.getString(R.string.wait_please), true, true, cancelListener);
					} catch (Exception e) {}
				}
			});
		}
	}
	
	/**
	 * Tareas a realizar en el hilo de UI durante la ejecución.
	 * @param msg Mensaje a mostrar en el diálogo de progreso.
	 */
	protected void publishProgress(final String msg) {
		if (this.mContext != null) {
			this.mContext.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						if (mProgress != null) mProgress.setMessage(msg);
					} catch (Exception e) {}
				}				
			});
		}
	}
	
	/**
	 * Tareas a realizar en el hilo de UI posterior a la ejecución.
	 * @param result Resultado de la ejecución.
	 */
	protected void onPostExecute(final Object result) {
		if (this.mContext != null) {
			this.mContext.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						if (mProgress != null) mProgress.dismiss();
					} catch (Exception e) {}
					try {
						if (mListener != null) mListener.onTaskCompleted(result);
					} catch (Exception e) {}
				}
			});
		}
	}
	
	@Override
	public abstract void run();

}
