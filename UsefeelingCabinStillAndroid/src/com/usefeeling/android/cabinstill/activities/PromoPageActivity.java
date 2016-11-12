package com.usefeeling.android.cabinstill.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Build;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.usefeeling.android.R;
import com.usefeeling.android.cabinstill.api.Promo;
import com.usefeeling.android.cabinstill.api.Result;
import com.usefeeling.android.cabinstill.api.ResultCodes;
import com.usefeeling.android.cabinstill.facades.ApplicationFacade;
import com.usefeeling.android.cabinstill.facades.DataFacade;
import com.usefeeling.android.cabinstill.facades.DeviceFacade;
import com.usefeeling.android.cabinstill.facades.MessagesFacade;
import com.usefeeling.android.cabinstill.helpers.DateTimeHelper;
import com.usefeeling.android.cabinstill.helpers.GoogleStaticMapsHelper;
import com.usefeeling.android.cabinstill.imageloader.ImageManager;
import com.usefeeling.android.cabinstill.interfaces.OnTaskCompleted;
import com.usefeeling.android.cabinstill.tasks.GetPromo;
import com.usefeeling.android.cabinstill.values.Extras;

/**
 * Activity de visualización de página de promoción.<br>
 * Recibe en su intent de entrada un objeto de la clase Promo, con nombre Extras.PROMO, que contiene la información de la promoción a visualizar.<br>
 * @author Moisés Vilar.
 *
 */
public class PromoPageActivity extends SherlockActivity implements OnTaskCompleted {
	private ImageManager mImageManager;
	private Promo promo;
	private Long promoid;
	private boolean mapDraw = false;
	private DataFacade dataFacade; 
	
	/**
	 * Listener de escucha de reintento de descarga de datos iniciales
	 */
	private OnClickListener retryListener = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			new GetPromo(PromoPageActivity.this, PromoPageActivity.this, PromoPageActivity.this.promoid).execute();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.promo_page2);
		this.getSupportActionBar().setIcon(R.drawable.usefeeling_icon_transparent_background);
		this.getSupportActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.drawable.button_bar_gradient));
		this.getSupportActionBar().setDisplayShowTitleEnabled(false);
		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		this.dataFacade = new DataFacade(this);
		this.mImageManager = new ImageManager(this);
		this.promo = (Promo)this.getIntent().getExtras().getSerializable(Extras.PROMO);
		if (this.promo == null) {
			promoid = this.getIntent().getLongExtra(Extras.PROMOID, -1);
			new GetPromo(this, this, promoid).execute();
		}
		else {
			this.setUI();
		}
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.promo_page_actionbar_menu, menu);
        return true;
    }
	
	@SuppressLint("InlinedApi")
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		int homeId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? android.R.id.home : R.id.abs__home; 
        if (item.getItemId() == homeId) {
        	ApplicationFacade.goHome(this, MapActivity.class);
        }
        else if (item.getItemId() == R.id.itemShare) {
        	DeviceFacade.shareVenue(this, this.promo.getPlace());
        }
        return true;
    }
	
	/**
	 * Establece la interfaz de usuario
	 */
	private void setUI() {
		if (this.promo == null) return;
		((ImageView)this.findViewById(R.id.ivAffinity)).setImageResource(this.mImageManager.getAffinityImageResId(this.promo.getAffinityStr()));
		this.mImageManager.loadBitmap(this.promo.getImageUrl(), (ImageView)this.findViewById(R.id.qcbIcon), R.drawable.default_portrait, false);
		((TextView)this.findViewById(R.id.tvPromoName)).setText(this.promo.getName());
		((TextView)this.findViewById(R.id.tvPlaceNamePromoPage)).setText(this.promo.getPlace().getName());
		final ImageView ivMap = (ImageView)this.findViewById(R.id.ivPromoLocation);
		ViewTreeObserver vto = ivMap.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
		    public boolean onPreDraw() {
		        int finalHeight = ivMap.getMeasuredHeight();
		        int finalWidth = ivMap.getMeasuredWidth();
		        if (!PromoPageActivity.this.mapDraw) PromoPageActivity.this.mImageManager.loadBitmap(GoogleStaticMapsHelper.buildUrl(PromoPageActivity.this.promo.getPlace().getLatitude(), PromoPageActivity.this.promo.getPlace().getLongitude(), true, finalWidth, finalHeight), ivMap, R.drawable.map, true);
		        PromoPageActivity.this.mapDraw = true;
		        return true;
		    }
		});
		ivMap.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Double slat = PromoPageActivity.this.dataFacade.getUserPosition().getLatitude();
				Double slon = PromoPageActivity.this.dataFacade.getUserPosition().getLongitude();
				Double dlat = PromoPageActivity.this.promo.getLatitude();
				Double dlon = PromoPageActivity.this.promo.getLongitude();
				DeviceFacade.openGMaps(PromoPageActivity.this, slat, slon, dlat, dlon);
			}
		});
		((TextView)this.findViewById(R.id.tvAddress)).setText(this.promo.getPlace().getAddress());
		((TextView)this.findViewById(R.id.tvDescription)).setText(this.promo.getDescription());
		Linkify.addLinks(((TextView)this.findViewById(R.id.tvDescription)), Linkify.ALL);
		if (this.promo.getInitTime() != null && this.promo.getInitTime() > 0) ((TextView)this.findViewById(R.id.tvSince)).setText(this.getString(R.string.from) + " " + DateTimeHelper.toString(this.promo.getInitTime()));
		if (this.promo.getEndTime() != null && this.promo.getEndTime() > 0) ((TextView)this.findViewById(R.id.tvTo)).setText(this.getString(R.string.to) + " " + DateTimeHelper.toString(this.promo.getEndTime()));
	}

	@Override
	public void onTaskCompleted(Object rawResult) {
		if (rawResult == null) {
			this.finish();
			return;
		}
		Result result = (Result)((Object[])rawResult)[0];
		if (result.getCode() != ResultCodes.Ok) {
			MessagesFacade.showDialog(this, "", result.getMessage(), MessagesFacade.MessageButtons.RETRY, this.retryListener, null);
			return;
		}
		this.promo = (Promo)((Object[])rawResult)[1];
		this.setUI();
	}
}
