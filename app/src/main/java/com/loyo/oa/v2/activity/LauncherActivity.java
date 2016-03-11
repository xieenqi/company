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
import com.loyo.oa.v2.activity.login.LoginActivity;
import com.loyo.oa.v2.activity.login.WelcomeActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LocationUtilGD;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.SharedUtil;
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
    @ViewById ImageView iv_launcher_adv, iv_launcher_fade, iv_white;//小微企业工作台、火箭
    @ViewById ViewGroup ll_root, layout_launcher_fade;
    private boolean isWelcom = false;

    @AfterViews
    void initVies() {
        setTouchView(NO_SCROLL);
        iv_launcher_adv.postDelayed(advRunner, 1000);//小微企业工作台
        new LocationUtilGD(this, new LocationUtilGD.AfterLocation() {
            @Override
            public void OnLocationGDSucessed(String address, double longitude, double latitude, String radius) {

            }

            @Override
            public void OnLocationGDFailed() {

            }
        });
    }

    /**
     * 小微企业工作台
     */
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
                    layout_launcher_fade.postDelayed(rocketRunner, 200);
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
            //iv_launcher_fade.setY(layout_launcher_fade.getTop() + 200);
            ObjectAnimator animator = ObjectAnimator.ofFloat(iv_launcher_fade, "translationY",
                    (float) getResources().getDisplayMetrics().heightPixels - layout_launcher_fade.getHeight(), -750.0f);
            animator.setDuration(500);
            animator.setInterpolator(new AccelerateInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    iv_launcher_fade.setVisibility(View.VISIBLE);//火箭头
                    layout_launcher_fade.setVisibility(View.GONE);
                    float value = (float) valueAnimator.getAnimatedValue();
                    if (value < 0) {
                        LogUtil.d("动画进度在v " + value);
                        iv_white.setVisibility(View.VISIBLE);
                    }
                    if (Math.round(value) == -750) {
                        intentActivity();
                    }
                }

            });
            animator.start();
        }
    };

    public void intentActivity() {
        isWelcom = SharedUtil.getBoolean(LauncherActivity.this, ExtraAndResult.WELCOM_KEY);
        Intent intent = new Intent();
        if (!isWelcom) {
            intent.setClass(LauncherActivity.this, WelcomeActivity.class);
            SharedUtil.putBoolean(getApplicationContext(), ExtraAndResult.WELCOM_KEY, true);
        } else {
            intent.setClass(LauncherActivity.this,
                    TextUtils.isEmpty(MainApp.getToken()) ? LoginActivity.class : MainActivity_.class);
        }
        startActivity(intent);
        finish();
    }
}
