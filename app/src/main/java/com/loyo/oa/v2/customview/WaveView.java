package com.loyo.oa.v2.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;

/**
 * com.loyo.oa.v2.customview
 * 描述 :简单的水波视图
 * 作者 : ykb
 * 时间 : 15/8/12.
 */
public class WaveView extends Button {

    /**
     * 半径的增量
     */
    private static final long RADIUS_INCREMENT_BLOCK = 50;

    /**
     * 波纹模式 -扩散
     */
    public static final int WAVE_MODE_SPREAD = 1;
    /**
     * 波纹模式 -收缩
     */
    public static final int WAVE_MODE_SHRINK = WAVE_MODE_SPREAD + 1;
    /**
     * 防止多次加载
     */
    private boolean loadOnce;
    /**
     * 画笔
     */
    private Paint mPaint;
    /**
     * 占据宽度
     */
    private int width;
    /**
     * 占据高度
     */
    private int height;
    /**
     * 波纹颜色
     */
    private int waveColor;
    /**
     * 延迟重绘的时间 ms
     */
    private long drawDelayMills;
    /**
     * 视图背景色
     */
    private int backGroundColor;
    /**
     * 波纹透明度
     */
    private int waveAlpha;
    /**
     * 波纹渲染半径
     */
    private int radius;
    /**
     * 是否绘制
     */
    private boolean needDraw;
    /**
     * 是否回调最后的波纹颜色
     */
    private boolean changeColor;
    /**
     * 波纹模式 {@link #WAVE_MODE_SHRINK}收缩,{@link #WAVE_MODE_SPREAD}扩散
     */
    private int mode;
    /**
     * 波纹绘制完成后的回调
     */
    private OnWaveCompleteListener callback;
    /**
     * 圆心的横坐标
     */
    private int centerX;
    /**
     * 圆心纵坐标
     */
    private int centerY;
    /**
     * 是否响应touch事件
     */
    private boolean responseTouch;


    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, AttributeSet set) {
        this(context, set, 0);

    }

    public WaveView(Context context, AttributeSet set, int defaultStyle) {
        super(context, set, defaultStyle);
        final TypedArray typedArray=context.obtainStyledAttributes(set,R.styleable.WaveView);
        backGroundColor=typedArray.getColor(R.styleable.WaveView_wave_backGroundColor,getResources().getColor(R.color.title_bg1));
        waveColor=typedArray.getColor(R.styleable.WaveView_wave_color,Color.LTGRAY);
        waveAlpha=typedArray.getInt(R.styleable.WaveView_wave_alpha, 255);
        mode=typedArray.getInt(R.styleable.WaveView_wave_mode,WAVE_MODE_SPREAD);
        responseTouch =typedArray.getBoolean(R.styleable.WaveView_wave_response_touch,false);
        drawDelayMills=typedArray.getInt(R.styleable.WaveView_wave_duration,300);
        typedArray.recycle();
    }

    /**
     * 设置视图背景色
     * @param backGroundColor
     */
    public synchronized void setBackGroundColor(int backGroundColor) {
        this.backGroundColor=backGroundColor;
        postInvalidate();
    }

    /**
     * 设置是否响应touch事件
     */
    public void setResponseTouch(boolean responseTouch) {
        this.responseTouch = responseTouch;
    }

    /**
     * 设置是否需要返回颜色
     *
     * @param changeColor
     */
    public void setChangeColor(boolean changeColor) {
        this.changeColor = changeColor;
    }

    /**
     * 设置波纹展现模式
     *
     * @param mode {@link #WAVE_MODE_SHRINK}收缩,{@link #WAVE_MODE_SPREAD}扩散
     */
    public void setMode(int mode) {
        this.mode = mode;
    }

    /**
     * 开始绘制
     */
    public void startDraw() {
        if (centerX != width / 2) {
            centerX = width / 2;
        }
        if (centerY != height / 2) {
            centerY = height / 2;
        }
        startInvalidate();
    }

    /**
     * 开始绘制
     */
    private void startInvalidate() {
        needDraw = true;
        resetPaint();
        switch (mode) {
            case WAVE_MODE_SHRINK:
                radius = width;
                break;
            case WAVE_MODE_SPREAD:
                radius = 0;
                break;
        }
        invalidate();
    }

    /**
     * 設置是否可以画
     *
     * @param needDraw
     */
    public void setNeedDraw(boolean needDraw) {
        this.needDraw = needDraw;
    }

    /**
     * 设置波纹透明度
     *
     * @param waveAlpha
     */
    public void setWaveAlpha(int waveAlpha) {
        this.waveAlpha = waveAlpha;
    }

    /**
     * 设置波纹颜色
     *
     * @param waveColor
     */
    public void setWaveColor(int waveColor) {
        this.waveColor = waveColor;
    }

    /**
     * 设置波浪完成后的回调接口
     *
     * @param callback
     */
    public void setCallback(OnWaveCompleteListener callback) {
        this.callback = callback;
    }

    /**
     * 初始化
     */
    private void onLayoutInit() {
        width = getWidth();
        height = getHeight();
        centerX = width / 2;
        centerY = height / 2;
        mPaint = new Paint();
        resetPaint();
        loadOnce = !loadOnce;
    }

    /**
     * 画笔重置
     */
    private void resetPaint() {
        mPaint.reset();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed && !loadOnce) {
            onLayoutInit();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        MainApp.getMainApp().logUtil.e("onTouch,action : " + event.getAction());
        requestFocus();
        if (!responseTouch) {
            return super.onTouchEvent(event);
        }

        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {
            centerX = (int) event.getX();
            centerY = (int) event.getY();
            startInvalidate();
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(backGroundColor);
        boolean end = false;
        switch (mode) {
            case WAVE_MODE_SHRINK:
                if (radius <= 0) {
                    end = true;
                }
                break;
            case WAVE_MODE_SPREAD:
                if (radius >= width / 2) {
                    end = true;
                }
                break;
        }

        if (!needDraw || end) {
            if (null != callback && needDraw && changeColor) {
                callback.onWaveComplete(waveColor);
            }
            super.draw(canvas);
            return;
        }
        mPaint.setColor(waveColor);
        mPaint.setAlpha(waveAlpha);
        canvas.drawCircle(centerX, centerY, radius, mPaint);
        radius += (mode == WAVE_MODE_SPREAD ? RADIUS_INCREMENT_BLOCK : -RADIUS_INCREMENT_BLOCK);

        super.draw(canvas);

        postInvalidateDelayed(drawDelayMills);
    }

    public interface OnWaveCompleteListener {
        void onWaveComplete(int color);
    }
}
