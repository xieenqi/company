package com.loyo.oa.v2.activityui.dashboard.presenter;

import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.loyo.oa.v2.activityui.dashboard.common.LoadStatus;
import com.loyo.oa.v2.activityui.dashboard.common.ScreenType;
import me.itangqi.waveloadingview.WaveLoadingView;

/**
 * Created by yyy on 16/12/13.
 */

public interface HomeDashboardPresenter {

    void initWave(WaveLoadingView wv1, WaveLoadingView wv2,int countPert,int moneyPert,String countPertStr,String moneyPertStr);

    void screenControlViews(ScreenType screenType);

    void getFollowUpData(int type);

    void getStockData(int type);

    void getMoneyCountData(int type);

    //回款统计
    void getPayment(int type);

    void setOnSucssView(AnimationDrawable anim,LinearLayout modelView, ImageView load, LinearLayout error, RelativeLayout loadview, LoadStatus status);

    String getNumFormat(long num);

}
