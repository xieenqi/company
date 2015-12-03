package com.loyo.oa.v2.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.tool.BaseActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * com.loyo.oa.v2.activity
 * 描述 :启动页
 * 作者 : ykb
 * 时间 : 15/11/3.
 */
@EActivity(R.layout.activity_launcher)
public class LauncherActivity extends BaseActivity {
    @ViewById ImageView iv_launcher_adv, iv_launcher_fade;
    @ViewById ImageView iv_launcher_bottom;
    @ViewById ViewGroup layout_launcher_fade;

    @AfterViews
    void initVies() {
        setTouchView(NO_SCROLL);
        iv_launcher_adv.postDelayed(advRunner, 1000);
    }

    private Runnable finishRunner = new Runnable() {
        @Override
        public void run() {
            ObjectAnimator animator = ObjectAnimator.ofFloat(findViewById(R.id.layout_launcher_main), "alpha", 1, 0).setDuration(1000);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }
                //动画加载完成
                @Override
                public void onAnimationEnd(Animator animator) {
                    Intent intent = new Intent(LauncherActivity.this, MainActivity_.class);
                    if (TextUtils.isEmpty(MainApp.getToken())) {
                        intent.setClass(LauncherActivity.this, LoginActivity.class);
                    }
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0, 0);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animator.start();
        }
    };

    private Runnable advRunner = new Runnable() {
        @Override
        public void run() {
            iv_launcher_adv.setVisibility(View.VISIBLE);
            ObjectAnimator animator = ObjectAnimator.ofFloat(iv_launcher_adv, "translationY", iv_launcher_adv.getHeight(), 0);
            animator.setDuration(500);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    iv_launcher_adv.postDelayed(rocketRunner, 2500);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animator.start();
        }
    };

    private Runnable rocketRunner = new Runnable() {
        @Override
        public void run() {
            iv_launcher_fade.setY(layout_launcher_fade.getTop());
            ObjectAnimator animator = ObjectAnimator.ofFloat(layout_launcher_fade, "translationY", (float)getResources().getDisplayMetrics().heightPixels-layout_launcher_fade.getHeight(),0f);
            animator.setDuration(300);
            animator.setInterpolator(new AccelerateInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    iv_launcher_fade.setVisibility(View.VISIBLE);
                    layout_launcher_fade.setVisibility(View.GONE);
                    float value = (float) valueAnimator.getAnimatedValue();
                    iv_launcher_fade.setY(value);
                    if (Math.round(value) == 0) {
                        iv_launcher_adv.post(finishRunner);
                    }
                }
            });

            animator.start();
        }
    };
}
