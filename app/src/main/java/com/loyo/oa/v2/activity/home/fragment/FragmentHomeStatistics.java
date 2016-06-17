package com.loyo.oa.v2.activity.home.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.home.adapter.FunnelDataAdapter;
import com.loyo.oa.v2.activity.home.adapter.ProcessDataAdapter;
import com.loyo.oa.v2.activity.home.bean.HttpProcess;
import com.loyo.oa.v2.activity.home.bean.HttpSalechance;
import com.loyo.oa.v2.activity.home.bean.HttpStatistics;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IStatistics;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【统计】fragment
 */
public class FragmentHomeStatistics extends Fragment {

    private final static String[] tracyColors = {"#f8668a", "#4ec469", "#4ddac2", "#31cbe8", "#88b9f7", "#7fcaff", "#f18f73", "#fdb485", "#fde068", "#12db8a"};
    private LinearLayout ll_process, ll_funnel;
    ProgressBar pb_progress_vertical;

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

    private void initView(View view) {
        ll_process = (LinearLayout) view.findViewById(R.id.ll_process);
        ll_funnel = (LinearLayout) view.findViewById(R.id.ll_funnel);
        pb_progress_vertical = (ProgressBar) view.findViewById(R.id.pb_progress_vertical);

        pb_progress_vertical.setProgress(46);
        pb_progress_vertical.setProgressDrawable(getResources().getDrawable(R.drawable.shape_progressbar_vertical1));

        getStatisticAllData();
    }

    /**
     * 获取销售统计全部数据
     */
    private void getStatisticAllData() {
        HashMap<String, Object> map = new HashMap<>();
//        map.put("pageIndex", pagination.getPageIndex());
//        map.put("pageSize", isTopAdd ? lstData.size() >= 20 ? lstData.size() : 20 : 20);
//        map.put("joinType", mJoinType);
//        map.put("status", mStatus);
        RestAdapterFactory.getInstance().build(Config_project.API_URL_STATISTICS())
                .create(IStatistics.class).getNoticeList(map, new Callback<HttpStatistics>() {
            @Override
            public void success(HttpStatistics httpStatistics, Response response) {
                HttpErrorCheck.checkResponse("销售统计全部数据：", response);
                setprocessData(httpStatistics.process);
                setFunnelData(httpStatistics.salechance);
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
            }
        });
    }

    /**
     * 设置 过程统计
     */
    private void setprocessData(List<HttpProcess> data) {
        int max = 0;
        for (HttpProcess ele : data) {
            if (max < ele.totalNum)
                max = ele.totalNum;
        }
        for (int i = 0; i < data.size(); i++) {
            HttpProcess processData = data.get(i);
            ll_process.addView(new ProcessDataAdapter(getActivity(), processData.value, processData.totalNum, max));
        }
    }

    /**
     * 设置 销售漏斗
     */
    private void setFunnelData(List<HttpSalechance> data) {
        for (int i = 0; i < data.size(); i++) {
            ll_funnel.addView(new FunnelDataAdapter(getActivity(), i,data.get(i)));
        }
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
