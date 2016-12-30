/**
 * Created by EthanGong on 16/12/29.
 */


package com.loyo.oa.hud.progress;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.loyo.oa.v2.R;

class LoyoSpinView extends View implements Indeterminate {
    private RectF mBound;

    private float mRotateDegrees;
    private int mFrameTime;
    private boolean mNeedToUpdateView;
    private Runnable mUpdateViewRunnable;

    private Drawable lightningDrawable;
    private Drawable spinDrawable;

    public LoyoSpinView(Context context) {
        super(context);
        init();
    }

    public LoyoSpinView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoyoSpinView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mBound = new RectF();

        lightningDrawable = getContext().getResources().getDrawable(R.drawable.loading_fixed);
        spinDrawable = getContext().getResources().getDrawable(R.drawable.loading_rotate);

        mFrameTime = 1000 / 12;
        mUpdateViewRunnable = new Runnable() {
            @Override
            public void run() {
                mRotateDegrees += 30;
                mRotateDegrees = mRotateDegrees < 360 ? mRotateDegrees : mRotateDegrees - 360;
                invalidate();
                if (mNeedToUpdateView) {
                    postDelayed(this, mFrameTime);
                }
            }
        };
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mBound.set(0, 0, w, h);
        lightningDrawable.setBounds(w/4, (h-w*64/100)/2, w*3/4, (h+w*64/100)/2);
        spinDrawable.setBounds(0, 0, w, h);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int dimension = Helper.dpToPixel(40, getContext());
        setMeasuredDimension(dimension, dimension);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        lightningDrawable.draw(canvas);
        canvas.rotate(mRotateDegrees, getWidth() / 2, getHeight() / 2);
        spinDrawable.draw(canvas);
    }

    @Override
    public void setAnimationSpeed(float scale) {
        mFrameTime = (int) (1000 / 12 / scale);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mNeedToUpdateView = true;
        post(mUpdateViewRunnable);
    }

    @Override
    protected void onDetachedFromWindow() {
        mNeedToUpdateView = false;
        super.onDetachedFromWindow();
    }
}
