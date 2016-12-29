package com.loyo.oa.v2.activityui.dashboard.presenter.impl;

import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.dashboard.api.DashBoardService;
import com.loyo.oa.v2.activityui.dashboard.common.LoadStatus;
import com.loyo.oa.v2.activityui.dashboard.model.CsclueFowUp;
import com.loyo.oa.v2.activityui.dashboard.presenter.HomeDashboardPresenter;
import com.loyo.oa.v2.activityui.dashboard.viewcontrol.HomeDashBoardView;
import com.loyo.oa.v2.customview.PaymentPopView;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;

import java.util.HashMap;

import me.itangqi.waveloadingview.WaveLoadingView;

/**
 * Created by yyy on 16/12/13.
 */

public class HomeDashboardPresenterImpl implements HomeDashboardPresenter{

    private Context mContext;
    private HomeDashBoardView crolView;

    public HomeDashboardPresenterImpl(Context mContext,HomeDashBoardView crolView){
        this.mContext = mContext;
        this.crolView = crolView;
    }

    @Override
    public void initUi(WaveLoadingView wv1, WaveLoadingView wv2) {

        wv1.setShapeType(WaveLoadingView.ShapeType.CIRCLE);
        //wv1.setTopTitle("Top Title");
        wv1.setCenterTitleColor(Color.WHITE);   //中心标题颜色
        wv1.setTopTitleStrokeColor(Color.BLUE); //顶部标题颜色
        wv1.setBorderColor(Color.GRAY);         //边框颜色
        wv1.setBorderWidth(2);                  //边框宽度
        wv1.setProgressValue(29);               //涨幅范围
        wv1.setWaveColor(mContext.getResources().getColor(R.color.dashborad)); //波浪颜色
        wv1.setWaveBgColor(mContext.getResources().getColor(R.color.dashbg));  //背景颜色
        wv1.setBottomTitleSize(18);
        wv1.setAmplitudeRatio(55);              //波浪弧度
        wv1.setTopTitleStrokeWidth(3);
        wv1.setCenterTitle("29%");

        wv2.setShapeType(WaveLoadingView.ShapeType.CIRCLE);
        //wv2.setTopTitle("Top Title");
        wv2.setCenterTitleColor(Color.WHITE);   //中心标题颜色
        wv2.setTopTitleStrokeColor(Color.BLUE); //顶部标题颜色
        wv2.setBorderColor(Color.GRAY);         //边框颜色
        wv2.setBorderWidth(2);                  //边框宽度
        wv2.setProgressValue(90);               //涨幅范围
        wv2.setWaveColor(mContext.getResources().getColor(R.color.dashborad)); //波浪颜色
        wv2.setWaveBgColor(mContext.getResources().getColor(R.color.dashbg));  //背景颜色
        wv2.setBottomTitleSize(18);
        wv2.setAmplitudeRatio(55);              //波浪弧度
        wv2.setTopTitleStrokeWidth(3);
        wv2.setCenterTitle("29%");
    }

    @Override
    public void screenControlView(String[] list,String title) {

        final PaymentPopView popViewKind = new PaymentPopView(mContext, list, title);
        popViewKind.show();
        popViewKind.setCanceledOnTouchOutside(true);
        popViewKind.setCallback(new PaymentPopView.VaiueCallback() {
            @Override
            public void setValue(String value, int index) {
                crolView.setScreenVal(value);
            }
        });
    }

    @Override
    public void getFollowUpData(LoadingLayout ll_loading,int type) {
        HashMap<String,Object> map = new HashMap<>();
        map.put("qType",type);
        DashBoardService.getFupCusClue(map).subscribe(new DefaultLoyoSubscriber<CsclueFowUp>(ll_loading) {
            @Override
            public void onNext(CsclueFowUp csclueFowUp) {
                crolView.followUpSuccessEmbl();
            }
        });
    }


    /**
     * 设置Loading状态
     *
     * @param  modelView:  展示数据view
     * @param  load:       loading动画
     * @param  error:      点击重试view
     * @param  loadview:   整个Layout
     *
     * */
    public void setOnSucssView(LinearLayout modelView, ImageView load, LinearLayout error, RelativeLayout loadview, LoadStatus status){
        modelView.setVisibility(status.getModelView());
        load.setVisibility(status.getLoadView());
        error.setVisibility(status.getErrorView());
        loadview.setVisibility(status.getLayoutView());
    }
}
