package com.loyo.oa.v2.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.login.LoginActivity;
import com.loyo.oa.v2.activity.login.WelcomeActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.SharedUtil;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import java.util.Timer;
import java.util.TimerTask;

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
    @ViewById ViewGroup ll_root, layout_launcher_fade;
    private boolean isWelcom = false;

    @AfterViews
    void initVies() {
        setTouchView(NO_SCROLL);
        iv_launcher_adv.postDelayed(advRunner, 1000);
    }

    private Runnable finishRunner = new Runnable() {
        @Override
        public void run() {
            ObjectAnimator animator = ObjectAnimator.ofFloat(findViewById(R.id.ll_root), "alpha", 0, 1).setDuration(500);//结束动画
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                //动画加载完成
                @Override
                public void onAnimationEnd(Animator animator) {
                    isWelcom = SharedUtil.getBoolean(LauncherActivity.this, ExtraAndResult.WELCOM_KEY);
                    LogUtil.d("wlecom: " + isWelcom);
                    Intent intent = new Intent();
                    if (!isWelcom) {
                        intent.setClass(LauncherActivity.this, WelcomeActivity.class);
                        SharedUtil.putBoolean(LauncherActivity.this, ExtraAndResult.WELCOM_KEY, true);
                    } else {
                        intent.setClass(LauncherActivity.this,
                                TextUtils.isEmpty(MainApp.getToken()) ? LoginActivity.class : MainActivity_.class);
                    }
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.enter_in, R.anim.enter_out);
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
        public void run() {//小微企业工作台
            iv_launcher_adv.setVisibility(View.VISIBLE);
            ObjectAnimator animator = ObjectAnimator.ofFloat(iv_launcher_adv, "translationY", iv_launcher_adv.getHeight(), 0);
            animator.setDuration(500);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    iv_launcher_fade.setVisibility(View.VISIBLE);
                    Animation launcherAnimation = AnimationUtils.loadAnimation(
                            LauncherActivity.this, R.anim.launcher_anim);
                    iv_launcher_fade.startAnimation(launcherAnimation);
                    //iv_launcher_adv.postDelayed(rocketRunner, 1200);
                    launcherAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            layout_launcher_fade.setVisibility(View.INVISIBLE);
                                        }
                                    });
                                }
                            }, 650);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                            iv_launcher_fade.setImageResource(R.drawable.white);
                            ll_root.post(finishRunner);

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
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
//            Animation launcherAnimation = AnimationUtils.loadAnimation(
//                    LauncherActivity.this, R.anim.launcher_anim);
//            layout_launcher_fade.startAnimation(launcherAnimation);


//            iv_launcher_fade.setY(layout_launcher_fade.getTop() + 500);
//            ObjectAnimator animator = ObjectAnimator.ofFloat(layout_launcher_fade, "translationY",
//                    (float) getResources().getDisplayMetrics().heightPixels - layout_launcher_fade.getHeight(), -300f);
//            animator.setDuration(300);
//            animator.setInterpolator(new AccelerateInterpolator());
//            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                @Override
//                public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                    iv_launcher_fade.setVisibility(View.VISIBLE);//火箭头
//                    layout_launcher_fade.setVisibility(View.GONE);
//                    float value = (float) valueAnimator.getAnimatedValue();
//                    iv_launcher_fade.setY(value);
//                    if (Math.round(value) == 0) {
//                        //iv_launcher_adv.post(finishRunner);
//                    }
//                }
//            });
//
//            animator.start();
        }
    };
}
