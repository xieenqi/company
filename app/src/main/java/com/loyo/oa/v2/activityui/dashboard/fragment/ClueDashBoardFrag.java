package com.loyo.oa.v2.activityui.dashboard.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.tool.BaseFragment;
import me.itangqi.waveloadingview.WaveLoadingView;

/**
 * 【线索仪表盘】
 * Created by yyy on 16/12/9.
 */

public class ClueDashBoardFrag extends BaseFragment {

    private View mView;
    private WaveLoadingView mWaveLoadingView1,mWaveLoadingView2;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(null == mView){
            mView = inflater.inflate(R.layout.fragment_clue_dashboard, container, false);
        }
        initUi();
        return mView;
    }

    private void initUi(){

        mWaveLoadingView1 = (WaveLoadingView) mView.findViewById(R.id.waveLoadingView1);
        mWaveLoadingView2 = (WaveLoadingView) mView.findViewById(R.id.waveLoadingView2);

        mWaveLoadingView1.setShapeType(WaveLoadingView.ShapeType.CIRCLE);
        //mWaveLoadingView1.setTopTitle("Top Title");
        mWaveLoadingView1.setCenterTitleColor(Color.WHITE);   //中心标题颜色
        mWaveLoadingView1.setTopTitleStrokeColor(Color.BLUE); //顶部标题颜色
        mWaveLoadingView1.setBorderColor(Color.GRAY);         //边框颜色
        mWaveLoadingView1.setBorderWidth(2);                  //边框宽度
        mWaveLoadingView1.setProgressValue(55);               //涨幅范围
        mWaveLoadingView1.setWaveColor(getActivity().getResources().getColor(R.color.dashBoradbg)); //波浪颜色
        mWaveLoadingView1.setBottomTitleSize(18);
        mWaveLoadingView1.setAmplitudeRatio(60);
        mWaveLoadingView1.setTopTitleStrokeWidth(3);
        mWaveLoadingView1.setCenterTitle("55%");


        mWaveLoadingView2.setShapeType(WaveLoadingView.ShapeType.CIRCLE);
        //mWaveLoadingView2.setTopTitle("Top Title");
        mWaveLoadingView2.setCenterTitleColor(Color.WHITE);   //中心标题颜色
        mWaveLoadingView2.setTopTitleStrokeColor(Color.BLUE); //顶部标题颜色
        mWaveLoadingView2.setBorderColor(Color.GRAY);         //边框颜色
        mWaveLoadingView2.setBorderWidth(2);                  //边框宽度
        mWaveLoadingView2.setProgressValue(34);               //涨幅范围
        mWaveLoadingView2.setWaveColor(getActivity().getResources().getColor(R.color.dashBoradbg)); //波浪颜色
        mWaveLoadingView2.setBottomTitleSize(18);
        mWaveLoadingView2.setAmplitudeRatio(60);
        mWaveLoadingView2.setTopTitleStrokeWidth(3);
        mWaveLoadingView2.setCenterTitle("34%");

    }

}
