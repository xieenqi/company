package com.loyo.oa.v2.activityui.dashboard.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.dashboard.DashboardDetailActivity;
import com.loyo.oa.v2.activityui.dashboard.common.DashboardType;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseFragment;

import me.itangqi.waveloadingview.WaveLoadingView;

/**
 * 【客户仪表盘】
 * Created by yyy on 16/12/9.
 */

public class CustomerDashBoardFrag extends BaseFragment implements View.OnClickListener {

    private View mView;
    private WaveLoadingView mWaveLoadingView1, mWaveLoadingView2;
    private LinearLayout ll_dashboard_cus_followup;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_customer_dashboard, container, false);
        }
        initUi();
        return mView;
    }


    private void initUi() {

        mWaveLoadingView1 = (WaveLoadingView) mView.findViewById(R.id.waveLoadingView1);
        mWaveLoadingView2 = (WaveLoadingView) mView.findViewById(R.id.waveLoadingView2);

        mWaveLoadingView1.setShapeType(WaveLoadingView.ShapeType.CIRCLE);
        //mWaveLoadingView1.setTopTitle("Top Title");
        mWaveLoadingView1.setCenterTitleColor(Color.WHITE);   //中心标题颜色
        mWaveLoadingView1.setTopTitleStrokeColor(Color.BLUE); //顶部标题颜色
        mWaveLoadingView1.setBorderColor(Color.GRAY);         //边框颜色
        mWaveLoadingView1.setBorderWidth(2);                  //边框宽度
        mWaveLoadingView1.setProgressValue(29);               //涨幅范围
        mWaveLoadingView1.setWaveColor(getActivity().getResources().getColor(R.color.dashBoradbg)); //波浪颜色
        mWaveLoadingView1.setBottomTitleSize(18);
        mWaveLoadingView1.setAmplitudeRatio(60);
        mWaveLoadingView1.setTopTitleStrokeWidth(3);
        mWaveLoadingView1.setCenterTitle("29%");


        mWaveLoadingView2.setShapeType(WaveLoadingView.ShapeType.CIRCLE);
        //mWaveLoadingView2.setTopTitle("Top Title");
        mWaveLoadingView2.setCenterTitleColor(Color.WHITE);   //中心标题颜色
        mWaveLoadingView2.setTopTitleStrokeColor(Color.BLUE); //顶部标题颜色
        mWaveLoadingView2.setBorderColor(Color.GRAY);         //边框颜色
        mWaveLoadingView2.setBorderWidth(2);                  //边框宽度
        mWaveLoadingView2.setProgressValue(90);               //涨幅范围
        mWaveLoadingView2.setWaveColor(getActivity().getResources().getColor(R.color.dashBoradbg)); //波浪颜色
        mWaveLoadingView2.setBottomTitleSize(18);
        mWaveLoadingView2.setAmplitudeRatio(60);
        mWaveLoadingView2.setTopTitleStrokeWidth(3);
        mWaveLoadingView2.setCenterTitle("29%");
        ll_dashboard_cus_followup = (LinearLayout) mView.findViewById(R.id.ll_dashboard_cus_followup);
        ll_dashboard_cus_followup.setOnClickListener(this);
        Global.SetTouchView(ll_dashboard_cus_followup);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_dashboard_cus_followup:
                Bundle bundle = new Bundle();
                bundle.putSerializable("type", DashboardType.CUS_FOLLOWUP);
                app.startActivity(mActivity, DashboardDetailActivity.class, MainApp.ENTER_TYPE_RIGHT, false, bundle);
                break;
        }
    }
}
