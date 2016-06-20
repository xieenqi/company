package com.loyo.oa.v2.activity.home.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.home.adapter.FunnelDataAdapter;
import com.loyo.oa.v2.activity.home.adapter.ProcessDataAdapter;
import com.loyo.oa.v2.activity.home.bean.HttpAchieves;
import com.loyo.oa.v2.activity.home.bean.HttpBulking;
import com.loyo.oa.v2.activity.home.bean.HttpProcess;
import com.loyo.oa.v2.activity.home.bean.HttpSalechance;
import com.loyo.oa.v2.activity.home.bean.HttpStatistics;
import com.loyo.oa.v2.activity.home.cusview.LoopView;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IStatistics;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【统计】fragment
 */
public class FragmentHomeStatistics extends BaseFragment {

    private LinearLayout ll_process, ll_funnel, ll_achieves1, ll_achieves2;
    ProgressBar pb_progress_vertical1, pb_progress_vertical2;
    TextView tv_number1, tv_name1, tv_number2, tv_name2, tv_achieves_toal1, tv_achieves_toal2, tv_achieves_finsh1, tv_achieves_finsh2;
    LoopView lv_round1, lv_round2;
    RadioButton rb_process_today, rb_process_week, rb_bulking_today, rb_bulking_week, rb_achieves_week, rb_achieves_month, rb_funnel_week, rb_funnel_month;

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
        pb_progress_vertical1 = (ProgressBar) view.findViewById(R.id.pb_progress_vertical1);
        pb_progress_vertical1.setProgressDrawable(getResources().getDrawable(R.drawable.shape_progressbar_vertical1));
        pb_progress_vertical2 = (ProgressBar) view.findViewById(R.id.pb_progress_vertical2);
        pb_progress_vertical2.setProgressDrawable(getResources().getDrawable(R.drawable.shape_progressbar_vertical1));
        tv_number1 = (TextView) view.findViewById(R.id.tv_number1);
        tv_number2 = (TextView) view.findViewById(R.id.tv_number2);
        tv_name1 = (TextView) view.findViewById(R.id.tv_name1);
        tv_name2 = (TextView) view.findViewById(R.id.tv_name2);
        ll_achieves1 = (LinearLayout) view.findViewById(R.id.ll_achieves1);
        ll_achieves2 = (LinearLayout) view.findViewById(R.id.ll_achieves2);
        lv_round1 = (LoopView) view.findViewById(R.id.lv_round1);
        lv_round2 = (LoopView) view.findViewById(R.id.lv_round2);
        tv_achieves_toal1 = (TextView) view.findViewById(R.id.tv_achieves_toal1);
        tv_achieves_toal2 = (TextView) view.findViewById(R.id.tv_achieves_toal2);
        tv_achieves_finsh1 = (TextView) view.findViewById(R.id.tv_achieves_finsh1);
        tv_achieves_finsh2 = (TextView) view.findViewById(R.id.tv_achieves_finsh2);
        rb_process_today = (RadioButton) view.findViewById(R.id.rb_process_today);
        rb_process_week = (RadioButton) view.findViewById(R.id.rb_process_week);
        rb_bulking_today = (RadioButton) view.findViewById(R.id.rb_bulking_today);
        rb_bulking_week = (RadioButton) view.findViewById(R.id.rb_bulking_week);
        rb_achieves_week = (RadioButton) view.findViewById(R.id.rb_achieves_week);
        rb_achieves_month = (RadioButton) view.findViewById(R.id.rb_achieves_month);
        rb_funnel_week = (RadioButton) view.findViewById(R.id.rb_funnel_week);
        rb_funnel_month = (RadioButton) view.findViewById(R.id.rb_funnel_month);
        rb_process_today.setOnClickListener(click);
        rb_process_week.setOnClickListener(click);
        rb_bulking_today.setOnClickListener(click);
        rb_bulking_week.setOnClickListener(click);
        rb_achieves_week.setOnClickListener(click);
        rb_achieves_month.setOnClickListener(click);
        rb_funnel_week.setOnClickListener(click);
        rb_funnel_month.setOnClickListener(click);
        getStatisticAllData();
    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rb_process_today:
                    getProcessInfo(1);
                    break;
                case R.id.rb_process_week:
                    getProcessInfo(2);
                    break;
                case R.id.rb_bulking_today:
                    getBulkingInfo(1);
                    break;
                case R.id.rb_bulking_week:
                    getBulkingInfo(2);
                    break;
                case R.id.rb_achieves_week:
                    getAchievesInfo(1);
                    break;
                case R.id.rb_achieves_month:
                    getAchievesInfo(2);
                    break;
                case R.id.rb_funnel_week:
                    getFunnelInfo(1);
                    break;
                case R.id.rb_funnel_month:
                    getFunnelInfo(2);
                    break;
            }
        }
    };

    /**
     * 获取销售统计全部数据
     */
    private void getStatisticAllData() {
        rb_process_today.setChecked(true);
        rb_bulking_today.setChecked(true);
        rb_achieves_week.setChecked(true);
        rb_funnel_week.setChecked(true);
        HashMap<String, Object> map = new HashMap<>();
        RestAdapterFactory.getInstance().build(Config_project.API_URL_STATISTICS())
                .create(IStatistics.class).getNoticeList(map, new Callback<HttpStatistics>() {
            @Override
            public void success(HttpStatistics httpStatistics, Response response) {
                HttpErrorCheck.checkResponse("销售统计全部数据：", response);
                setprocessData(httpStatistics.process);
                setBulkingData(httpStatistics.bulking);
                setFunnelData(httpStatistics.salechance);
                setAchievesData(httpStatistics.achieves);
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
            }
        });
    }

    /**
     * 获取 【过程统计】 今日 本周 的数据
     */
    private void getProcessInfo(int type) {
        showLoading("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("qtype", type);
        RestAdapterFactory.getInstance().build(Config_project.API_URL_STATISTICS())
                .create(IStatistics.class).getProcessList(map, new Callback<List<HttpProcess>>() {
            @Override
            public void success(List<HttpProcess> result, Response response) {
                HttpErrorCheck.checkResponse("【过程统计】数据：", response);
                setprocessData(result);
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
            }
        });
    }

    /**
     * 获取 【增量统计】 今日 本周 的数据
     */
    private void getBulkingInfo(int type) {
        showLoading("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("qtype", type);
        RestAdapterFactory.getInstance().build(Config_project.API_URL_STATISTICS())
                .create(IStatistics.class).getBulkingList(map, new Callback<List<HttpBulking>>() {
            @Override
            public void success(List<HttpBulking> result, Response response) {
                HttpErrorCheck.checkResponse("【增量统计】数据：", response);
                setBulkingData(result);
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
            }
        });
    }

    /**
     * 获取 【业绩目标】本周 本月 的数据
     */
    private void getAchievesInfo(int type) {
        showLoading("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("qtype", type);
        RestAdapterFactory.getInstance().build(Config_project.API_URL_STATISTICS())
                .create(IStatistics.class).getAchievesList(map, new Callback<List<HttpAchieves>>() {
            @Override
            public void success(List<HttpAchieves> result, Response response) {
                HttpErrorCheck.checkResponse("【业绩目标】数据：", response);
                setAchievesData(result);
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
            }
        });
    }

    /**
     * 获取 【销售漏斗】本周 本月 的数据
     */
    private void getFunnelInfo(int type) {
        showLoading("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("qtype", type);
        RestAdapterFactory.getInstance().build(Config_project.API_URL_STATISTICS())
                .create(IStatistics.class).getFunnelList(map, new Callback<List<HttpSalechance>>() {
            @Override
            public void success(List<HttpSalechance> result, Response response) {
                HttpErrorCheck.checkResponse("【销售漏斗】数据：", response);
                setFunnelData(result);
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
            max += ele.totalNum;
        }
        if (ll_process.getChildCount() > 0) {
            ll_process.removeAllViews();
        }
        int j = 10;
        for (int i = 0; i < data.size(); i++) {
            HttpProcess processData = data.get(i);
            if (!(i >= 10)) {
                ll_process.addView(new ProcessDataAdapter(getActivity(), processData.value, processData.totalNum, max, i));
            } else {
                ll_process.addView(new ProcessDataAdapter(getActivity(), processData.value, processData.totalNum, max, i - j));
                j--;
            }
        }
    }

    /**
     * 设置 增量统计
     */
    private void setBulkingData(List<HttpBulking> data) {
        int max = 0;
        for (HttpBulking ele : data) {
            max += ele.totalNum;
        }
        for (HttpBulking ele : data) {
            if (ele.typeId == 1) {
                pb_progress_vertical1.setProgress(ele.totalNum);
                pb_progress_vertical1.setMax(max);
                tv_name1.setText(ele.name);
                tv_number1.setText(ele.totalNum + "");
            } else {
                pb_progress_vertical2.setProgress(ele.totalNum);
                pb_progress_vertical2.setMax(max);
                tv_name2.setText(ele.name);
                tv_number2.setText(ele.totalNum + "");
            }
        }
    }

    /**
     * 设置 业绩目标
     */
    private void setAchievesData(List<HttpAchieves> data) {
        for (HttpAchieves ele : data) {
            if (1 == ele.achieveType) {
                ll_achieves1.setVisibility(View.VISIBLE);
                tv_achieves_toal1.setText("目标赢单量  " + ele.achieveMoney);
                tv_achieves_finsh1.setText("已完成  " + ele.finshMoney);
                lv_round1.setMaxCount(ele.achieveMoney);
                lv_round1.setCount(ele.finshMoney);
            } else if (2 == ele.achieveType) {
                ll_achieves2.setVisibility(View.VISIBLE);
                tv_achieves_toal2.setText("目标金额  " + ele.achieveMoney);
                tv_achieves_finsh2.setText("已完成  " + ele.finshMoney);
                lv_round2.setMaxCount(100);
                lv_round2.setCount(59);
            }
        }

    }

    /**
     * 设置 销售漏斗
     */
    private void setFunnelData(List<HttpSalechance> data) {
        if (ll_funnel.getChildCount() > 0) {
            ll_funnel.removeAllViews();
        }
        for (int i = 0; i < data.size(); i++) {
            ll_funnel.addView(new FunnelDataAdapter(getActivity(), i, data.get(i)));
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
