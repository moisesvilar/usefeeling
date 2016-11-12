package com.usefeeling.android.cabinstill.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.usefeeling.android.R;
import com.usefeeling.android.cabinstill.adapters.ProfileAdapter;
import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.api.ResultMessages;
import com.usefeeling.android.cabinstill.api.User;
import com.usefeeling.android.cabinstill.facades.ApplicationFacade;
import com.usefeeling.android.cabinstill.facades.MessagesFacade;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;
import com.usefeeling.android.cabinstill.tasks.GetUser;
import com.usefeeling.android.cabinstill.values.Extras;
import com.viewpagerindicator.TitlePageIndicator;

/**
 * Activity de perfil de usuario.<br>
 * En su Intent de entrada se le debe pasar como extra de nombre Extras.USER un objecto de la clase User con los
 * datos del usuario a visualizar.<br>
 * En el caso de que ese extra sea null, obligatoriamente se le debe de pasar un extra de nombre Extras.USERID que
 * contendrá, en un objecto de tipo Long, el identificador del usuario del que se descargarán y visualizarán sus datos. 
 * @author Moisés Vilar
 *
 */
public class ProfileActivity extends SherlockActivity implements OnTaskCompleted {

	private ViewPager pager;
	private ProfileAdapter adapter;
	private User mUser;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.profile);
		this.getSupportActionBar().setIcon(R.drawable.usefeeling_icon_transparent_background);
		this.getSupportActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.drawable.button_bar_gradient));
		this.getSupportActionBar().setDisplayShowTitleEnabled(false);
		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mUser = (User)this.getIntent().getSerializableExtra(Extras.USER);
		if (mUser == null) {
			Long userid = this.getIntent().getLongExtra(Extras.USERID, -1);
			new GetUser(this, this, userid).execute();
		}
		else {
			this.adapter = new ProfileAdapter(this, mUser);
			this.pager = (ViewPager)this.findViewById(R.id.pager);
			this.pager.setAdapter(this.adapter);
			TitlePageIndicator titleIndicator = (TitlePageIndicator)this.findViewById(R.id.indicator);
			titleIndicator.setViewPager(this.pager);
			titleIndicator.setTextColor(Color.BLACK);
			titleIndicator.setBackgroundColor(this.getResources().getColor(R.color.usefeeling_green));
		}
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.profile_page_actionbar_menu, menu);
        return true;
    }
	
	@SuppressLint("InlinedApi")
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		int homeId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? android.R.id.home : R.id.abs__home; 
        if (item.getItemId() == homeId) {
        	ApplicationFacade.goHome(this, MapActivity.class);
        }
        else if (item.getItemId() == R.id.itemEdit) {
        	this.startActivity(new Intent(this, EditProfileActivity.class));
        }
        return true;
    }
	
	@Override
	public void onTaskCompleted(Object rawResult) {
		Result result = new Result(ResultCodes.NotDefinedError, ResultMessages.NotDefinedError());
		if (rawResult != null) result = (Result)((Object[])rawResult)[0];
		if (result.getCode() != ResultCodes.Ok) {
			MessagesFacade.toastLong(this, result.getMessage());
			return;
		}
		this.mUser = (User)((Object[])rawResult)[1];
		this.adapter = new ProfileAdapter(this, this.mUser);
		this.pager = (ViewPager)this.findViewById(R.id.pager);
		this.pager.setAdapter(this.adapter);
		TitlePageIndicator titleIndicator = (TitlePageIndicator)this.findViewById(R.id.indicator);
		titleIndicator.setViewPager(this.pager);
		titleIndicator.setTextColor(Color.BLACK);
		titleIndicator.setBackgroundColor(this.getResources().getColor(R.color.usefeeling_green));
	}
}
