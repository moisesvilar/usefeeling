package com.usefeeling.android.cabinstill.facades;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.usefeeling.android.R;
import com.usefeeling.android.cabinstill.api.UseFeeling;

/**
 * Fachada de operaciones para mostrar mensajes por pantalla.
 * @author Moisés Vilar.
 *
 */
public abstract class MessagesFacade {
	
	/**
	 * Clase estática que contiene los posibles valores de configuración de botones
	 * en ventanas de aviso.
	 * @author Moisés Vilar
	 *
	 */
	public static class MessageButtons {
		/**
		 * Un único botón de aceptación.
		 */
		public static final int OK = 0;
		/**
		 * Un único botón de reintento
		 */
		public static final int RETRY = 3;
		/**
		 * Un botón de aceptación y otro de cancelación.
		 */
		public static final int OK_CANCEL = 1;
		/**
		 * Dos botones, uno de reintento y otro de cancelación
		 */
		public static final int RETRY_CANCEL = 4;
		/**
		 * Botones de aceptación y cancelación con el texto Sí/No respecticamente.
		 */
		public static final int YES_NO = 2;
	}

	/**
	 * Muestra una ventana con el título y la descripción especificados y dos botones de confirmación
	 * y cancelación con los comportamientos indicados. Los textos de los botones dependerán del valor
	 * del parámetro buttons que debe corresponderse a uno de los atributos estáticos de la clase
	 * Messages.MessageButtons.
	 * @param context Contexto desde el que se invoca este método.
	 * @param title Título de la ventana.
	 * @param description Mensaje que se mostrará en la ventana.
	 * @param buttons Valor estático de la clase Messages.MessageButtons del que dependerá el texto
	 * de los botones de la ventana.
	 * @param positiveListener DialogInterface.OnClickListener que definirá el comportamiento del
	 * botón de confirmación.
	 * @param negativeListener DialogInterface.OnClickListener que definirá el comportamiento del
	 * botón de cancelación.
	 * @return La ventana de diálogo.
	 */
	public static AlertDialog showDialog(Context context, String title, String description, int buttons, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
		try {
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
			alertDialog.setTitle(title);
			alertDialog.setMessage(description);
			String positiveButton = "";
			String negativeButton = "";
			switch (buttons) {
				case MessageButtons.OK:
				case MessageButtons.OK_CANCEL:
					positiveButton = context.getResources().getString(R.string.ok);
					negativeButton = context.getResources().getString(R.string.cancel);
					break;
				case MessageButtons.YES_NO:
					positiveButton = context.getResources().getString(R.string.yes);
					negativeButton = context.getResources().getString(R.string.no);
					break;
				case MessageButtons.RETRY:
					positiveButton = context.getResources().getString(R.string.retry);
					break;
				case MessageButtons.RETRY_CANCEL:
					positiveButton = context.getResources().getString(R.string.retry);
					negativeButton = context.getResources().getString(R.string.cancel);
					break;
				
			}
			alertDialog.setPositiveButton(positiveButton, positiveListener);
			if (buttons != MessageButtons.OK && buttons != MessageButtons.RETRY) {
				alertDialog.setNegativeButton(negativeButton, negativeListener);
			}
	        return alertDialog.show();
		} catch (Throwable t) {
			return null;
		}
	}

	/**
	 * Muestra una ventana emergente durante el tiempo máximo con el mensaje especificado.
	 * @param context Contexto desde el que se invoca este método.
	 * @param message Mensaje a mostrar.
	 */
	public static void toastLong(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
		
	}

	/**
	 * Construye una ventana de selección única entre varios elementos.
	 * @param context Contexto desde el que se invoca este método.
	 * @param title Título de la ventana.
	 * @param elements Matriz de elementos.
	 * @return La ventana de selección.
	 */
	public static Builder createSingleChoiceDialog(Context context, String title, CharSequence[] elements, OnClickListener listenerItem) {
		final Builder build = new Builder(context);
		build.setTitle(title);
		build.setCancelable(true);
		build.setSingleChoiceItems(elements, -1, listenerItem);
		return build;
	}

	/**
	 * Construye la ventana de visualización de los términos de servicio.
	 * @param context Contexto desde el que se invoca este método.
	 * @return La ventana de visualización de los términos de servicio.
	 */
	public static AlertDialog showTosDialog(Context context) {
		final ProgressBar progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
		progressBar.setVisibility(View.GONE);
		WebView wvTos = new WebView(context);
		wvTos.loadUrl(UseFeeling.TOS_URL);
		wvTos.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int progress) {
				progressBar.setProgress(0);
				progressBar.setVisibility(View.VISIBLE);
				progressBar.incrementProgressBy(progress);
				if (progress == 100) progressBar.setVisibility(View.GONE);
			}
		});
		LinearLayout dialogLayout = new LinearLayout(context);
		dialogLayout.setOrientation(LinearLayout.VERTICAL);
		dialogLayout.addView(progressBar);
		dialogLayout.addView(wvTos);
		final Builder tosDialog = new AlertDialog.Builder(context);
		ScrollView scrollPanel = new ScrollView(context);
		scrollPanel.addView(dialogLayout);
		tosDialog.setView(scrollPanel);
		tosDialog.setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {}
		});
		return tosDialog.show();
	}
	
	/**
	 * Muestra la ventana de aviso de que el tipo de red actual no es suficiente para una conexión estable.
	 * @param context Contexto desde el que se invoca el método.
	 * @param positiveListener Listener al pulsar el botón positivo.
	 * @param negativeListener Listener al pulsar el botón negativo.
	 * @return La ventana construída.
	 */
	public static AlertDialog showCurrentNetworkisNotAcceptableDialog(Context context, OnClickListener positiveListener, OnClickListener negativeListener) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
		alertDialog.setTitle("");
		String description = context.getString(R.string.network_type_warning);
		alertDialog.setMessage(description);
		alertDialog.setPositiveButton(context.getString(R.string.yes), positiveListener);
		alertDialog.setNegativeButton(context.getString(R.string.no), negativeListener);
		return alertDialog.show();
	}

	/**
	 * Muestra la ventana de aviso de que la localización por redes inalámbricas está desactivada.
	 * @param context Contexto desde el que se invoca el método.
	 * @param positiveListener Listener al pulsar el botón positivo.
	 * @param negativeListener Listener al pulsar el botón negativo.
	 * @return La ventana construída.
	 */
	public static AlertDialog showWirelessLocationIsNotEnableDialog(Context context, OnClickListener positiveListener, OnClickListener negativeListener) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
		alertDialog.setTitle("");
		String description = context.getString(R.string.network_location_disabled);
		alertDialog.setMessage(description);
		alertDialog.setPositiveButton(context.getString(R.string.enable_network_location), positiveListener);
		alertDialog.setNegativeButton(context.getString(R.string.cancel), negativeListener);
		return alertDialog.show();
	}
	
	/**
	 * Muestra la ventana de aviso de que se va a realizar una monitorización de la posición del usuario.
	 * @param context Contexto desde el que se invoca el método.
	 * @param positiveListener Listener al pulsar el botón positivo.
	 * @param negativeListener Listener al pulsar el botón negativo.
	 * @return La ventana construída.
	 */
	public static AlertDialog showLocationMonitoringConfirmationDialog(Context context, OnClickListener positiveListener, OnClickListener negativeListener) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
		alertDialog.setTitle("");
		String description = context.getString(R.string.location_monitoring);
		alertDialog.setMessage(description);
		alertDialog.setPositiveButton(context.getString(R.string.yes), positiveListener);
		alertDialog.setNegativeButton(context.getString(R.string.no), negativeListener);
		return alertDialog.show();
	}
	
	/**
	 * Muestra la ventana de aviso de que la localización es imprescindible para el servicio.
	 * @param context Contexto.
	 * @return El diálogo.
	 */
	public static AlertDialog showLocationIsRequiredDialog(final Activity context) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
		alertDialog.setTitle("");
		String description = context.getString(R.string.location_is_required);
		alertDialog.setMessage(description);
		alertDialog.setPositiveButton(context.getString(R.string.ok), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ApplicationFacade.kill(context);
			}
		});
		return alertDialog.show();
	}
}
