package com.usefeeling.android.cabinstill.imageloader;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.usefeeling.android.cabinstill.api.UseFeeling;
import com.usefeeling.android.cabinstill.facades.SharedPreferencesFacade;

public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
	private final WeakReference<View> viewReference;
    private Activity mContext;
    private UseFeeling mUseFeeling;
    private SharedPreferencesFacade mPrefs;
    private int mWidth = -1, mHeight = -1;
    public String url;

    /**
     * Constructor with width and height
     * @param context
     * @param imageView
     * @param w
     * @param h
     */
    public BitmapWorkerTask(Activity context, View imageView, int w, int h) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        viewReference = new WeakReference<View>(imageView);
        this.mContext = context;
        this.mWidth = w;
        this.mHeight = h;
        this.mPrefs = new SharedPreferencesFacade(this.mContext);
        this.mUseFeeling = new UseFeeling(this.mPrefs.getUserId(), this.mPrefs.getPassword());
    }
    
    /**
     * Constructor without width and height
     * @param context
     * @param imageView
     */
    public BitmapWorkerTask(Activity context, View imageView) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        viewReference = new WeakReference<View>(imageView);
        this.mContext = context;
        this.mPrefs = new SharedPreferencesFacade(this.mContext);
        this.mUseFeeling = new UseFeeling(this.mPrefs.getUserId(), this.mPrefs.getPassword());
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(String... params) {
        this.url = params[0];
        byte[] data = null;
        try {
        	if (this.url.startsWith("eventid")) {
		        Long eventid = Long.parseLong(this.url.substring(this.url.lastIndexOf("=") + 1));
		        data = this.mUseFeeling.getEventIcon(eventid);
        	}
        	else if (this.url.startsWith("promoid")) {
        		Long promoid = Long.parseLong(this.url.substring(this.url.lastIndexOf("=") + 1));
		        data = this.mUseFeeling.getPromoIcon(promoid);
        	}
        	else if (this.url.startsWith("userid")) {
        		Long userid = Long.parseLong(this.url.substring(this.url.lastIndexOf("=") + 1));
        		data = this.mUseFeeling.getProfilePicture2(userid);
        	}
        	else if (this.url.startsWith("portrait")) {
        		String placeType = this.url.substring(this.url.lastIndexOf("=") + 1);
        		data = ImageManager.getPortraitImageByType(this.mContext, placeType);
        	}
        	else if (this.url.startsWith("defaulticon")) {
        		String iconName = this.url.substring(this.url.lastIndexOf("=") + 1);
        		data = ImageManager.getMapIconByName(this.mContext, iconName);
        	}
        	else {
        		data = ImageManager.getPictureFromUrl(this.url);
        	}
        	if (data == null) return null;
        	Bitmap bitmap = ImageManager.decodeSampledBitmap(this.mContext.getResources(), data, this.mWidth, this.mHeight);
        	if (bitmap == null) return null;
        	bitmap = ImageManager.transformBitmapByUrl(this.mContext, bitmap, this.url);
	        ImageManager.addBitmapToMemoryCache(this.url, bitmap);
	        return bitmap;
        } catch (NumberFormatException e) {
        	return null;
        }
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
    	if (isCancelled()) {
            bitmap = null;
        }
        if (viewReference != null && bitmap != null) {
            final View reference = viewReference.get();
            final BitmapWorkerTask bitmapWorkerTask = ImageManager.getBitmapWorkerTask(reference);
            if (this == bitmapWorkerTask && reference != null) {
                if (reference instanceof ImageView) {
                	((ImageView)reference).setImageBitmap(bitmap);
                }
                else if (reference instanceof TextView) {
                	((TextView)reference).setCompoundDrawablesWithIntrinsicBounds(null, new BitmapDrawable(this.mContext.getResources(), bitmap), null, null);
                }
                ((View)reference).setBackgroundColor(Color.TRANSPARENT);
            }

        }
    }

}
