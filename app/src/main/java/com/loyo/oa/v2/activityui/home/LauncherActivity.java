package com.loyo.oa.v2.activityui.home;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.login.LoginActivity;
import com.loyo.oa.v2.activityui.login.WelcomeActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.service.OrganizationService;
import com.loyo.oa.v2.tool.LocationUtilGD;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.SharedUtil;
import com.loyo.oa.v2.tool.UMengTools;

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
public class LauncherActivity extends Activity {
    @ViewById
    ImageView iv_launcher_adv, iv_launcher_fade, iv_white;//小微企业工作台、火箭
    @ViewById
    ViewGroup ll_root, layout_launcher_fade;
    private boolean isWelcom = false;

    @AfterViews
    void initVies() {
//        setTouchView(NO_SCROLL);
        iv_launcher_adv.postDelayed(advRunner, 1000);//小微企业工作台
        new LocationUtilGD(LauncherActivity.this, new LocationUtilGD.AfterLocation() {
            @Override
            public void OnLocationGDSucessed(final String address, final double longitude, final double latitude, final String radius) {
                UMengTools.sendLocationInfo(address, longitude, latitude);
            }

            @Override
            public void OnLocationGDFailed() {

            }
        });
        SharedUtil.put(MainApp.getMainApp(), ExtraAndResult.APP_START, "open");
        Common.getToken();//检查刷新token
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
                public void onAnimationStart(final Animator animator) {

                }

                @Override
                public void onAnimationEnd(final Animator animator) {
                    layout_launcher_fade.postDelayed(rocketRunner, 200);
                }

                @Override
                public void onAnimationCancel(final Animator animator) {

                }

                @Override
                public void onAnimationRepeat(final Animator animator) {

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
                    (float) getResources().getDisplayMetrics().heightPixels - layout_launcher_fade.getHeight(), -760.0f);
            animator.setDuration(500);
            animator.setInterpolator(new AccelerateInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                    iv_launcher_fade.setVisibility(View.VISIBLE);//火箭头
                    layout_launcher_fade.setVisibility(View.GONE);
                    float value = (float) valueAnimator.getAnimatedValue();
                    if (value < 0) {
                        LogUtil.d("动画进度在v " + value);
                        iv_white.setVisibility(View.VISIBLE);
                    }
                    if (Math.round(value) == -760) {
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
        } else {
            //新版主页
            intent.setClass(LauncherActivity.this,
                    TextUtils.isEmpty(MainApp.getToken()) ? LoginActivity.class : MainHomeActivity.class);//MainHomeActivity  NewMainActivity
        }
        startActivity(intent);

        // Add by ethangong 16/08/08
        /* 登录用户未成功更新组织架构时，再次拉取组织架构 */
        if (!TextUtils.isEmpty(MainApp.getToken()) /* TODO: 还需要验证token有效性 */
                && !OrganizationManager.isOrganizationCached()) {
            OrganizationService.startActionFetchAll(MainApp.getMainApp());
        }
        else if (OrganizationManager.isOrganizationCached()){
            // 读取缓存
            OrganizationManager.shareManager().loadOrganizitionDataToMemoryCache();
        }

        finish();
//        overridePendingTransition(R.anim.exit_buttomtotop, R.anim.exit_toptobuttom);
    }
}
