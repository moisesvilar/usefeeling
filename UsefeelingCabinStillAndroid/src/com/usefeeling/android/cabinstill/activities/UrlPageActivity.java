package com.usefeeling.android.cabinstill.activities;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.usefeeling.android.R;
import com.usefeeling.android.cabinstill.facades.ApplicationFacade;
import com.usefeeling.android.cabinstill.values.Extras;

/**
 * Activity para visualizar información desde una URL.<br>
 * En su intent de entrada se debe especificar un extra, de nombre Extras.URL,
 * que contenga en un objecto de la clase String la URL del contenido a mostrar.
 * 
 * @author Moisés Vilar.
 * 
 */
public class UrlPageActivity extends SherlockActivity {

	private ProgressBar progressBar;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.url_page);
		this.getSupportActionBar().setIcon(R.drawable.usefeeling_icon_transparent_background);
		this.getSupportActionBar().setBackgroundDrawable(
		this.getResources().getDrawable(R.drawable.button_bar_gradient));
		this.getSupportActionBar().setDisplayShowTitleEnabled(false);
		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		this.progressBar = (ProgressBar) this.findViewById(R.id.progressbar);
		String url = this.getIntent().getExtras().getString(Extras.URL);
		WebView webView = (WebView) this.findViewById(R.id.webView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}
		});
		webView.loadUrl(url);
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int progress) {
				progressBar.setProgress(0);
				progressBar.setVisibility(View.VISIBLE);
				UrlPageActivity.this.setProgress(progress * 1000);
				progressBar.incrementProgressBy(progress);
				if (progress == 100) {
					progressBar.setVisibility(View.GONE);
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.default_actionbar_menu, menu);
		return true;
	}
	
	@SuppressLint("InlinedApi")
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		int homeId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? android.R.id.home : R.id.abs__home; 
        if (item.getItemId() == homeId) {
        	ApplicationFacade.goHome(this, MapActivity.class);
        }
        return true;
    }

}
