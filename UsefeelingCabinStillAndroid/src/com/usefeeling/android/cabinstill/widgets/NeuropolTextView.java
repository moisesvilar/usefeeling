package com.usefeeling.android.cabinstill.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * TextView con la fuente Neuropol.
 * @author Moisés Vilar.
 *
 */
public class NeuropolTextView extends TextView {

	public NeuropolTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public NeuropolTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NeuropolTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "neuropol.ttf");
            setTypeface(tf);
        }
    }
	
}
