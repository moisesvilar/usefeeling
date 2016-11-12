package com.usefeeling.android.cabinstill.tasks;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;

import com.google.gson.Gson;
import com.usefeeling.android.cabinstill.api.FacebookGraphData;
import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.api.ResultMessages;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;

public class GetFacebookIdFromUrl extends AbstractTask {

	private static final String PREFIX_URL = "http://graph.facebook.com/";
	private static final Gson gson = new Gson();
	
	/**
	 * Constructor.
	 * @param context Contexto desde el que se ejecuta la tarea.
	 * @param listener Listener de llamada cuando la tarea finalice su ejecución.
	 * @param params Parámetros de entrada para la tarea.
	 */
	public GetFacebookIdFromUrl(Activity context, OnTaskCompleted listener, Object... params) {
		super(context, listener, params);
	}
	
	@Override
	public void run() {
		FacebookGraphData id = new FacebookGraphData();
		try {
			this.onPreExecute();
			String url = (String)this.mParams[0];
			int indexOfQuestionMark = url.lastIndexOf("?");
			if (indexOfQuestionMark != -1) {
				url = url.substring(0, indexOfQuestionMark);
			}
			String lastString = url.substring(url.lastIndexOf("/") + 1);
			HttpClient httpclient = new DefaultHttpClient();
		    HttpResponse response = httpclient.execute(new HttpGet(PREFIX_URL + lastString));
		    StatusLine statusLine = response.getStatusLine();
		    if(statusLine.getStatusCode() == HttpStatus.SC_OK){
		        ByteArrayOutputStream out = new ByteArrayOutputStream();
		        response.getEntity().writeTo(out);
		        out.close();
		        String json = out.toString();
		        if (json.substring(0, 10).contains("error")) {
		        	com.usefeeling.android.cabinstill.api.FacebookError error = (com.usefeeling.android.cabinstill.api.FacebookError)gson.fromJson(json, com.usefeeling.android.cabinstill.api.FacebookError.class);
		        	result = new Result(ResultCodes.NotDefinedError, error.getMessage());
		        }
		        id = (FacebookGraphData)gson.fromJson(json, FacebookGraphData.class);
		        result = new Result(ResultCodes.Ok, ResultMessages.Ok());
		    } else{
		        response.getEntity().getContent().close();
		        result = new Result(ResultCodes.IOException, statusLine.getReasonPhrase());
		    }
		} catch (Throwable t) {
			result = new Result(ResultCodes.Exception, t.getMessage());
			return;
		} finally {
			this.onPostExecute(new Object[]{result, id});
		}
	}

}
