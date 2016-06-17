package com.loyo.oa.v2.activity.home.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.home.adapter.ProcessDataAdapter;
import com.loyo.oa.v2.activity.home.cusview.incrementview.Bar;
import com.loyo.oa.v2.activity.home.cusview.incrementview.BarChartView;
import com.loyo.oa.v2.activity.home.cusview.incrementview.BarSet;
import com.loyo.oa.v2.activity.home.cusview.incrementview.DataRetriever;
import com.loyo.oa.v2.activity.home.cusview.incrementview.YController;

/**
 * 【统计】fragment
 */
public class FragmentHomeStatistics extends Fragment {

    private final static String[] tracyColors = {"#f8668a", "#4ec469", "#4ddac2", "#31cbe8", "#88b9f7", "#7fcaff", "#f18f73", "#fdb485", "#fde068", "#12db8a"};
    private LinearLayout ll_process;
    private BarChartView bcv_view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO FragmentHomeStatistics -->onCreate()
        super.onCreate(savedInstanceState);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~fragment2-->onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_statistics, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {//@drawable/shape_progressbar_mini
        ll_process = (LinearLayout) view.findViewById(R.id.ll_process);
        bcv_view = (BarChartView) view.findViewById(R.id.bcv_view);
        for (int i = 0; i < 5; i++) {
            ll_process.addView(new ProcessDataAdapter(getActivity()));
        }
        setIncrementData(1, 3);
    }

    private final static String[] mLabels = {"新增客户数(12)", "新增机会数(23)", "新增合同数(32)"};
    private final static String[] colors = {"#7fcaff", "#feb98d", "#fde068"};
    private final static float[] valus = {56, 93, 61};

    private void setIncrementData(int nSets, int nPoints) {
        BarSet data;//条形
        Bar bar;
        bcv_view.reset();
        for (int i = 0; i < nSets; i++) {//循环几次创建几个条形

            data = new BarSet();
            for (int j = 0; j < nPoints; j++) {
                bar = new Bar(mLabels[j], valus[j]);//条形的名字  和 值
                bar.setColor(Color.parseColor(colors[j]));
                data.addBar(bar);
//                data.setColor(Color.parseColor(colors[j]));//条形的颜色
            }
            bcv_view.addData(data);
        }

        bcv_view.setBarSpacing(200);//间隔宽度
        bcv_view.setBarBackground(true);
        bcv_view.setBarBackgroundColor(Color.parseColor("#f4f8fe"));
        bcv_view.setRoundCorners(1);//设置圆角

        bcv_view.setBorderSpacing(60)//条形到两边的边框的距离
                .setFontSize(30)
                .setLabelColor(Color.parseColor("#999999"))//字体颜色
//                .setGrid(DataRetriever.randPaint())
//                .setHorizontalGrid(DataRetriever.randPaint())
//                .setVerticalGrid(DataRetriever.randPaint())
                .setYLabels(YController.LabelPosition.NONE)//竖的是否显示
//                .setYAxis(true)
                .setXLabels(DataRetriever.getXPosition())
//                .setXAxis(DataRetriever.randBoolean())
//                        .setThresholdLine(2, DataRetriever.randPaint())
                .setMaxAxisValue(100 * nSets, 20)//最大值 单位值
                .animate(DataRetriever.randAnimation(new Runnable() {
                    @Override
                    public void run() {

                    }
                }, 3))
        //.show()
        ;
    }

    public void onInIt() {
        Toast.makeText(getActivity(), "我收到tab1微信的传召", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        System.out
                .println("~~~~~~~~~~~~~~~~~~~~~~fragment2-->onActivityCreated()");
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~fragment2-->onDestroy()");
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~fragment2-->onPause()");
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~fragment2-->onResume()");
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~fragment2-->onStart()");
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~fragment2-->onStop()");
    }
}
