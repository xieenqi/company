package com.loyo.oa.v2.activity.home.cusview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.Utils;

/**
 * 圆形统计控件
 */
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
    private Paint loopPaint;

    private long count;
    private long maxCount;
    private int textSize = 47;
    private String rightRoundDeflautCoror = "#f4f8fe";//右半圆颜色
    private String lefttRoundDeflautCoror = "#4ab0fd";//左半圆颜色
    private String deflautTextCoror = "#999999";//字的颜色

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

    public void setCount(long c) {
        count = c;
    }

    public long getCount() {
        return count;
    }

    public void setMaxCount(long c) {
        maxCount = c;
    }

    public long getMaxCount() {
        return maxCount;
    }

    public void setTextSize(int c) {
        textSize = c;
    }

    public String getRightRoundDeflautCoror() {
        return rightRoundDeflautCoror;
    }

    public void setRightRoundDeflautCoror(String rightRoundDeflautCoror) {
        this.rightRoundDeflautCoror = rightRoundDeflautCoror;
    }

    public String getLefttRoundDeflautCoror() {
        return lefttRoundDeflautCoror;
    }

    public void setLefttRoundDeflautCoror(String lefttRoundDeflautCoror) {
        this.lefttRoundDeflautCoror = lefttRoundDeflautCoror;
    }

    public String getDeflautTextCoror() {
        return deflautTextCoror;
    }

    public void setDeflautTextCoror(String deflautTextCoror) {
        this.deflautTextCoror = deflautTextCoror;
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
//右半圆
        loopPaint.setColor(Color.parseColor(rightRoundDeflautCoror));
        loopPaint.setAntiAlias(true);
        loopPaint.setStrokeWidth(15);
        canvas.drawCircle(center_x, center_y, outerRadius, loopPaint);
//左半圆
        loopPaint.setColor(Color.parseColor(lefttRoundDeflautCoror));
        loopPaint.setAntiAlias(true);
        loopPaint.setStrokeWidth(25);
//		count = 75;
//		maxCount = 100;
        angle = (new Double(((float) count / (float) maxCount) * 360)).intValue();
        canvas.drawArc(rect, startAngle, angle, true, loopPaint);
//中间圆
        loopPaint.setColor(Color.WHITE);
        loopPaint.setAntiAlias(true);
        loopPaint.setStrokeWidth(15);
        canvas.drawCircle(center_x, center_y, innerRadius, loopPaint);

        loopPaint.setColor(Color.parseColor(deflautTextCoror));
        loopPaint.setAntiAlias(true);
        loopPaint.setTextAlign(Paint.Align.CENTER);
        loopPaint.setStrokeWidth(15);
        loopPaint.setTextSize(textSize);
        FontMetrics fontMetrics = loopPaint.getFontMetrics();
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        float textBaseY = height - (height - fontHeight) / 2 - fontMetrics.bottom;
        canvas.drawText(setPercentageData(maxCount, count), center_y, textBaseY, loopPaint);

    }

    /**
     * 设置百分数的值 限制小数两位
     */
    private String setPercentageData(long max, long count) {
        if (0 == max) {
            return "0%";
        } else {
            LogUtil.d("百分------------------------------------------------------------------比值：" + ((double) count / (double) max) * 100 + "%");
            return Utils.setValueDouble(((double) count / (double) max) * 100) + "%";
        }
    }

    public void refalsh() {
        invalidate();
    }
}
