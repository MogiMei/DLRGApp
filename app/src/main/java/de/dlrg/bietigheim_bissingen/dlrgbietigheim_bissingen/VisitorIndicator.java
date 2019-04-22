package de.dlrg.bietigheim_bissingen.dlrgbietigheim_bissingen;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class VisitorIndicator extends View {
    private int barColor;
    private int percentValue;

    private int viewWidth;
    private int viewHeight;

    private Paint paint;
    private Rect rect;

    public VisitorIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.VisitorIndicator, 0, 0);
        try {
            barColor = a.getInt(R.styleable.VisitorIndicator_barColor, 0);
            percentValue = a.getInt(R.styleable.VisitorIndicator_percentValue, 0);
        } finally {
            a.recycle();
        }
        rect = new Rect(0, 0, 140, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        viewHeight = getHeight();
        viewWidth = getWidth();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        paint.setColor(barColor);
        rect.left = viewWidth / 100 * percentValue;
        rect.right = viewWidth / 100 * percentValue + 10;
        rect.bottom = viewHeight;
        canvas.drawRect(rect, paint);
    }

    public void setPercentValue(int value) {
        percentValue = value;
        invalidate();
    }
}
