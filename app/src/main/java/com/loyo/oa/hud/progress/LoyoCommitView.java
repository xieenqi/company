/**
 * Created by EthanGong on 16/12/29.
 */


package com.loyo.oa.hud.progress;

import android.content.Context;
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

class LoyoCommitView extends ImageView implements CommitIndeterminate {
    private RectF mBound;

    private float mRotateDegrees;
    private int mFrameTime;
    private boolean mNeedToUpdateView;
    private Runnable mUpdateViewRunnable;

    public LoyoCommitView(Context context) {
        super(context);
        init();
    }

    public LoyoCommitView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoyoCommitView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mBound = new RectF();
    }

    @Override
    public void setAnimationSpeed(float scale) {
        mFrameTime = (int) (1000 / 12 / scale);
    }

    @Override
    public void loadSuccessState() {
        successAnimation();
    }

    @Override
    public void loadErrorState() {
        errorAnimation("");
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        waitAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        mNeedToUpdateView = false;
        super.onDetachedFromWindow();
    }

    /*等待动画*/
    void waitAnimation(){
        AnimationDrawable waitAnim = new AnimationDrawable();
        for (int i = 1; i <= 10; i++) {
            int id = getContext().getResources().getIdentifier("loadwait"+i,"drawable", getContext().getPackageName());
            Drawable drawable = getContext().getResources().getDrawable(id);
            waitAnim.addFrame(drawable, 50);
        }
        waitAnim.setOneShot(false);
        setBackgroundDrawable(waitAnim);
        waitAnim.start();
    }

    /*成功动画*/
    void successAnimation(){
        AnimationDrawable sucsAnim = new AnimationDrawable();
        for (int i = 1; i <= 10; i++) {
            int id = getContext().getResources().getIdentifier("loadsucs"+i,"drawable", getContext().getPackageName());
            Drawable drawable = getContext().getResources().getDrawable(id);
            sucsAnim.addFrame(drawable, 50);
        }
        sucsAnim.setOneShot(true);
        setBackgroundDrawable(sucsAnim);
        sucsAnim.start();
    }

    /*失败动画*/
    void errorAnimation(String message){
        AnimationDrawable erroAnim = new AnimationDrawable();
        for (int i = 1; i <= 10; i++) {
            int id = getContext().getResources().getIdentifier("loaderro"+i,"drawable", getContext().getPackageName());
            Drawable drawable = getContext().getResources().getDrawable(id);
            erroAnim.addFrame(drawable, 50);
        }
        erroAnim.setOneShot(true);
        setBackgroundDrawable(erroAnim);
        erroAnim.start();
    }
}
