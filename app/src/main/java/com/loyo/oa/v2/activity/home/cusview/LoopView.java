package com.loyo.oa.v2.activity.home.cusview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class LoopView extends View {
    private int width;
    private int height;
    private float center_x;
    private float center_y;
    private float left;
    private float right;
    private float top;
    private float bottom;

    private int angle = 90;
    private int startAngle = 270;


    private float innerRadius;
    private float outerRadius;
    private float barWidth = 15;

    private RectF rect;

    // private Paint circleRing;
    // private Paint circleColor;
    // private Paint textPaint;

    private int mAscent;
    private Paint loopPaint;
    private int count;
    private int maxCount;
    private int textSize = 47;

    public LoopView(Context context) {
        super(context);
        loopPaint = new Paint();
    }

    public LoopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loopPaint = new Paint();
    }

    public LoopView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loopPaint = new Paint();
    }

    public void setCount(int c) {
        count = c;
    }

    public int getCount() {
        return count;
    }

    public void setMaxCount(int c) {
        maxCount = c;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setTextSize(int c) {
        textSize = c;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = resolveSize(80, widthMeasureSpec);
        height = resolveSize(80, heightMeasureSpec);
        int size = (width > height) ? height : width;
        center_x = width / 2;
        center_y = height / 2;

        outerRadius = size / 2;
        barWidth = outerRadius / 5;

        innerRadius = outerRadius - barWidth;

        left = center_x - outerRadius;
        right = center_x + outerRadius;
        top = center_y - outerRadius;
        bottom = center_y + outerRadius;
        rect = new RectF();
        rect.set(left, top, right, bottom);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        loopPaint.setColor(Color.GRAY);
        loopPaint.setAntiAlias(true);
        loopPaint.setStrokeWidth(15);
        canvas.drawCircle(center_x, center_y, outerRadius, loopPaint);

        loopPaint.setColor(Color.rgb(0, 189, 0));
        loopPaint.setAntiAlias(true);
        loopPaint.setStrokeWidth(25);
//		count = 75;
//		maxCount = 100;
        angle = (new Double(((float) count / (float) maxCount) * 360)).intValue();
        canvas.drawArc(rect, startAngle, angle, true, loopPaint);

        loopPaint.setColor(Color.WHITE);
        loopPaint.setAntiAlias(true);
        loopPaint.setStrokeWidth(15);
        canvas.drawCircle(center_x, center_y, innerRadius, loopPaint);

        loopPaint.setColor(Color.rgb(0, 189, 0));
        loopPaint.setAntiAlias(true);
        loopPaint.setTextAlign(Paint.Align.CENTER);
        loopPaint.setStrokeWidth(15);
        loopPaint.setTextSize(textSize);
        FontMetrics fontMetrics = loopPaint.getFontMetrics();
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        float textBaseY = height - (height - fontHeight) / 2 - fontMetrics.bottom;
        canvas.drawText(String.valueOf(count) + "%", center_y, textBaseY, loopPaint);

    }

    public void refalsh() {
        invalidate();
    }
}
