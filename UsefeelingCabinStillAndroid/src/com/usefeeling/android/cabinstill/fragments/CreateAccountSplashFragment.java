package com.usefeeling.android.cabinstill.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.usefeeling.android.R;

/**
 * Fragment de selección de modo de creacción de cuenta.
 * @author Moisés Vilar.
 *
 */
public class CreateAccountSplashFragment extends SherlockFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.create_account_splash_fragment, container, false);
	    return view;
	}
}
