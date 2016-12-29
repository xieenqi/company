package com.loyo.oa.v2.activityui.dashboard.presenter;

import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.activityui.dashboard.common.LoadStatus;

import me.itangqi.waveloadingview.WaveLoadingView;

/**
 * Created by yyy on 16/12/13.
 */

public interface HomeDashboardPresenter {

    void initUi(WaveLoadingView wv1,WaveLoadingView wv2);

    void screenControlView(String[] list,String title);

    void getFollowUpData(int type);

    void getStockData(int type);

    void getMoneyCountData(int type);

    void setOnSucssView(AnimationDrawable anim,LinearLayout modelView, ImageView load, LinearLayout error, RelativeLayout loadview, LoadStatus status);
}
