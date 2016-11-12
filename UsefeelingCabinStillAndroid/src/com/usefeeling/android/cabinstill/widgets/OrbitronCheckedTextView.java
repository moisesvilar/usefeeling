package com.usefeeling.android.cabinstill.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.CheckedTextView;

public class OrbitronCheckedTextView extends CheckedTextView {
	private Typeface tf;
	
	public OrbitronCheckedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public OrbitronCheckedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OrbitronCheckedTextView(Context context) {
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
