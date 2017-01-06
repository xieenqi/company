package com.loyo.oa.v2.activityui.dashboard.presenter.impl;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.dashboard.api.DashBoardService;
import com.loyo.oa.v2.activityui.dashboard.common.LoadStatus;
import com.loyo.oa.v2.activityui.dashboard.common.ScreenType;
import com.loyo.oa.v2.activityui.dashboard.model.FollowupStatistic;
import com.loyo.oa.v2.activityui.dashboard.model.MoneyStatistic;
import com.loyo.oa.v2.activityui.dashboard.model.StockStatistic;
import com.loyo.oa.v2.activityui.dashboard.presenter.HomeDashboardPresenter;
import com.loyo.oa.v2.activityui.dashboard.viewcontrol.HomeDashBoardView;
import com.loyo.oa.v2.customview.PaymentPopView;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.LogUtil;

import java.util.ArrayList;
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
    public void initWave(WaveLoadingView wv1, WaveLoadingView wv2,int mvNumValues,int mvMonValues,String mvNumShow,String mvMonShow) {

        LogUtil.dee("mvMonValues:"+mvMonValues);
        LogUtil.dee("mvNumValues:"+mvNumValues);

        wv1.setShapeType(WaveLoadingView.ShapeType.CIRCLE);
        //wv1.setTopTitle("Top Title");
        wv1.setCenterTitleColor(Color.WHITE);   //中心标题颜色
        wv1.setTopTitleStrokeColor(Color.BLUE); //顶部标题颜色
        wv1.setBorderColor(Color.GRAY);         //边框颜色
        wv1.setBorderWidth(2);                  //边框宽度
        wv1.setProgressValue(mvMonValues);      //涨幅范围
        wv1.setWaveColor(mContext.getResources().getColor(R.color.dashborad)); //波浪颜色
        wv1.setWaveBgColor(mContext.getResources().getColor(R.color.dashbg));  //背景颜色
        wv1.setBottomTitleSize(18);
        wv1.setAmplitudeRatio(55);              //波浪弧度
        wv1.setTopTitleStrokeWidth(3);
        wv1.setCenterTitle(mvMonShow);

        wv2.setShapeType(WaveLoadingView.ShapeType.CIRCLE);
        //wv2.setTopTitle("Top Title");
        wv2.setCenterTitleColor(Color.WHITE);   //中心标题颜色
        wv2.setTopTitleStrokeColor(Color.BLUE); //顶部标题颜色
        wv2.setBorderColor(Color.GRAY);         //边框颜色
        wv2.setBorderWidth(2);                  //边框宽度
        wv2.setProgressValue(mvNumValues);        //涨幅范围
        wv2.setWaveColor(mContext.getResources().getColor(R.color.dashborad)); //波浪颜色
        wv2.setWaveBgColor(mContext.getResources().getColor(R.color.dashbg));  //背景颜色
        wv2.setBottomTitleSize(18);
        wv2.setAmplitudeRatio(55);              //波浪弧度
        wv2.setTopTitleStrokeWidth(3);
        wv2.setCenterTitle(mvNumShow);
    }

    @Override
    public void screenControlViews(final ScreenType screenType) {
        final PaymentPopView popViewKind = new PaymentPopView(mContext, screenType.screenTitle(), "选择时间");
        popViewKind.show();
        popViewKind.setCanceledOnTouchOutside(true);
        popViewKind.setCallback(new PaymentPopView.VaiueCallback() {
            @Override
            public void setValue(String value, int index) {
                switch (value){
                    case "今天":
                        crolView.setScreenVal(screenType,1,value);
                        break;
                    case "昨天":
                        crolView.setScreenVal(screenType,2,value);
                        break;
                    case "本周":
                        crolView.setScreenVal(screenType,3,value);
                        break;
                    case "上周":
                        crolView.setScreenVal(screenType,4,value);
                        break;
                    case "本月":
                        crolView.setScreenVal(screenType,5,value);
                        break;
                    case "上月":
                        crolView.setScreenVal(screenType,6,value);
                        break;
                    case "本季度":
                        crolView.setScreenVal(screenType,7,value);
                        break;
                    case "上季度":
                        crolView.setScreenVal(screenType,8,value);
                        break;
                    case "本年":
                        crolView.setScreenVal(screenType,9,value);
                        break;
                    case "去年":
                        crolView.setScreenVal(screenType,10,value);
                        break;
                }
            }
        });
    }

    /*获取跟进*/
    @Override
    public void getFollowUpData(int type) {
        HashMap<String,Object> map = new HashMap<>();
        map.put("qType",type);
        DashBoardService.getFupCusClue(map).subscribe(new DefaultLoyoSubscriber<FollowupStatistic>() {
            @Override
            public void onNext(FollowupStatistic csclueFowUp) {
                crolView.followUpSuccessEmbl(csclueFowUp);
            }

            @Override
            public void onError(Throwable e) {
                crolView.followUpErrorEmbl();
            }
        });
    }

    /*获取存量增量*/
    @Override
    public void getStockData(int type) {
        HashMap<String,Object> map = new HashMap<>();
        map.put("qType",type);
        DashBoardService.getStock(map)
                .subscribe(new DefaultLoyoSubscriber<ArrayList<StockStatistic>>() {
            @Override
            public void onNext(ArrayList<StockStatistic> stockListModel) {
                crolView.stockSuccessEmbl(stockListModel);
            }

            @Override
            public void onError(Throwable e) {
                crolView.stockErrorEmbl();
            }
        });
    }

    /*获取数量金额*/
    @Override
    public void getMoneyCountData(int type) {
        HashMap<String,Object> map = new HashMap<>();
        map.put("qType",type);
        DashBoardService.getMoneyCount(map).subscribe(new DefaultLoyoSubscriber<MoneyStatistic>() {
            @Override
            public void onNext(MoneyStatistic moneyCountModel) {
                crolView.moneyConSuccessEmbl(moneyCountModel);
            }

            @Override
            public void onError(Throwable e) {
                crolView.moneyConErrorEmbl();
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
    @Override
    public void setOnSucssView(AnimationDrawable anim,LinearLayout modelView, ImageView load, LinearLayout error, RelativeLayout loadview, LoadStatus status){
        modelView.setVisibility(status.getModelView() == View.VISIBLE?View.VISIBLE:View.GONE);
        load.setVisibility(status.getLoadView() == View.VISIBLE?View.VISIBLE:View.GONE);
        error.setVisibility(status.getErrorView() == View.VISIBLE?View.VISIBLE:View.GONE);
        loadview.setVisibility(status.getLayoutView() == View.VISIBLE?View.VISIBLE:View.GONE);
        status.animEmbl(anim);
    }

    @Override
    public String getNumFormat(long num) {
        return null;
    }
}
