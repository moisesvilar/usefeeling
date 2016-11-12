package com.usefeeling.android.cabinstill.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class MolengoTextView extends TextView {

	public MolengoTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MolengoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MolengoTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "molengo.ttf");
            setTypeface(tf);
        }
    }
	
}
