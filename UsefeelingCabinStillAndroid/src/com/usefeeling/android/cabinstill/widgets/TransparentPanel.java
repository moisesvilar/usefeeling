package com.usefeeling.android.cabinstill.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Widget para un panel transparente.
 * @author Moisés Vilar.
 *
 */
public class TransparentPanel extends LinearLayout {

	private Paint innerPaint;
	
	/**
	 * Constructor.
	 * @param context Contexto desde el que se requiere el widget.
	 * @see LinearLayout#LinearLayout(Context context)
	 */
	public TransparentPanel(Context context) {
		super(context);
		init();
	}

	/**
	 * Constructor.
	 * @param context Contexto desde el que se requiere el widget.
	 * @param attrs Conjunto de atributos del widget.
	 * @see LinearLayout#LinearLayout(Context context, AttributeSet attrs)
	 */
	public TransparentPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	/**
	 * Inicialización del widget
	 */
	private void init() {
		this.setOrientation(LinearLayout.VERTICAL);
		innerPaint = new Paint();
		innerPaint.setARGB(225, 153, 204, 0);
		innerPaint.setAntiAlias(true);
	}

	
	public void setInnerPaint(Paint innerPaint) {
		this.innerPaint = innerPaint;
	}


    @Override
    protected void dispatchDraw(Canvas canvas) {
    	
    	RectF drawRect = new RectF();
    	drawRect.set(0,0, getMeasuredWidth(), getMeasuredHeight());
    	
    	canvas.drawRoundRect(drawRect, 5, 5, innerPaint);
		
		super.dispatchDraw(canvas);
    }

}
