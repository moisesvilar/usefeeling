package com.usefeeling.android.cabinstill.receivers;

import com.usefeeling.android.cabinstill.facades.ApplicationFacade;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnReboot extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ApplicationFacade.startCheckinService(context);
	}

}
