package com.usefeeling.android.cabinstill.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class OrbitronTextView extends TextView {
	
	private Typeface tf;
	
	public OrbitronTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public OrbitronTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OrbitronTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            this.tf = Typeface.createFromAsset(getContext().getAssets(), "orbitron.ttf");
        }
    }
    
    @Override
    protected void dispatchDraw(Canvas canvas) {
        setTypeface(tf);
    }
}
