package com.usefeeling.android.cabinstill.tasks;

import android.app.Activity;

import com.usefeeling.android.cabinstill.api.Place;
import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.facades.DataFacade;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;

public class GetPlaceFromCache extends AbstractTask {
	
	private Place mPlace;
	private DataFacade mDataFacade;

	public GetPlaceFromCache(Activity context, OnTaskCompleted listener, Object... params) {
		super(context, listener, params);
		this.mDataFacade = new DataFacade(context);
	}

	@Override
	public void run() {
		try {
			Long placeid = (Long)this.mParams[0];
			this.mPlace = this.mDataFacade.getPlace(placeid);
			result = this.mUsefeeling.getLastResult();
		} catch (Throwable t) {
			result = new Result(ResultCodes.Exception, t.getMessage());
			return;
		} finally {
			this.onPostExecute(new Object[]{result, mPlace});
		}
	}

}
