package com.loyo.oa.v2.activityui.home.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.home.adapter.FunnelDataAdapter;
import com.loyo.oa.v2.activityui.home.adapter.ProcessDataAdapter;
import com.loyo.oa.v2.activityui.home.api.StatisticsService;
import com.loyo.oa.v2.activityui.home.bean.HttpAchieves;
import com.loyo.oa.v2.activityui.home.bean.HttpBulking;
import com.loyo.oa.v2.activityui.home.bean.HttpProcess;
import com.loyo.oa.v2.activityui.home.bean.HttpSalechance;
import com.loyo.oa.v2.activityui.home.bean.HttpStatistics;
import com.loyo.oa.v2.activityui.home.cusview.LoopView;
import com.loyo.oa.v2.common.DialogHelp;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;

import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【统计】fragment
 */
public class HomeStatisticsFragment extends BaseFragment {

    private LinearLayout ll_process, ll_funnel, ll_achieves1, ll_achieves2, ll_bulking_yes, ll_achieves_yes;
    ProgressBar pb_progress_vertical1, pb_progress_vertical2;
    TextView tv_number1, tv_name1, tv_number2, tv_name2, tv_achieves_toal1, tv_achieves_toal2, tv_achieves_finsh1, tv_achieves_finsh2;
    LoopView lv_round1, lv_round2;
    RadioButton rb_process_today, rb_process_week, rb_bulking_today, rb_bulking_week, rb_achieves_week, rb_achieves_month, rb_funnel_week, rb_funnel_month;
    private ImageView im_process_no, im_funnel_no, im_bulking_no, im_achieves_no;
    private boolean isRRefresh = false;
    private SwipeRefreshLayout srl_refresh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO HomeStatisticsFragment -->onCreate()
        super.onCreate(savedInstanceState);
        LogUtil.d("~~~~~~~~~~~~~~~~~~~~~~fragment2-->onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_statistics, container, false);
        initView(view);
        LogUtil.d("~~~~~~~~~~~~~~~~~~~~~~fragment2-->onCreateView()");
        return view;
    }

    private void initView(View view) {
        ll_process = (LinearLayout) view.findViewById(R.id.ll_process);
        ll_funnel = (LinearLayout) view.findViewById(R.id.ll_funnel);
        pb_progress_vertical1 = (ProgressBar) view.findViewById(R.id.pb_progress_vertical1);
        pb_progress_vertical1.setProgressDrawable(getResources().getDrawable(R.drawable.shape_progressbar_vertical1));
        pb_progress_vertical2 = (ProgressBar) view.findViewById(R.id.pb_progress_vertical2);
        pb_progress_vertical2.setProgressDrawable(getResources().getDrawable(R.drawable.shape_progressbar_vertical2));
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
        im_process_no = (ImageView) view.findViewById(R.id.im_process_no);
        im_funnel_no = (ImageView) view.findViewById(R.id.im_funnel_no);
        im_bulking_no = (ImageView) view.findViewById(R.id.im_bulking_no);
        ll_bulking_yes = (LinearLayout) view.findViewById(R.id.ll_bulking_yes);
        im_achieves_no = (ImageView) view.findViewById(R.id.im_achieves_no);
        ll_achieves_yes = (LinearLayout) view.findViewById(R.id.ll_achieves_yes);
        srl_refresh = (SwipeRefreshLayout) view.findViewById(R.id.srl_refresh);
        srl_refresh.setColorSchemeColors(Color.parseColor("#2c9dfc"));
        srl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srl_refresh.setRefreshing(true);
                getStatisticAllData();
            }
        });
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
        isRRefresh = true;
        rb_process_today.setChecked(true);
        rb_bulking_today.setChecked(true);
        rb_achieves_week.setChecked(true);
        rb_funnel_week.setChecked(true);
        HashMap<String, Object> map = new HashMap<>();
//        RestAdapterFactory.getInstance().build(Config_project.API_URL_STATISTICS())
//                .create(IStatistics.class).getNoticeList(map, new Callback<HttpStatistics>() {
//            @Override
//            public void success(HttpStatistics httpStatistics, Response response) {
//                HttpErrorCheck.checkResponse("销售统计全部数据：", response);
//                srl_refresh.setRefreshing(false);
//                if (null == httpStatistics) {
//                    return;
//                } else {
//                    if (null != httpStatistics.process)
//                        setprocessData(httpStatistics.process);
//                    if (null != httpStatistics.bulking)
//                        setBulkingData(httpStatistics.bulking);
//                    if (null != httpStatistics.salechance)
//                        setFunnelData(httpStatistics.salechance);
//                    if (null != httpStatistics.achieves)
//                        setAchievesData(httpStatistics.achieves);
//                }
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                HttpErrorCheck.checkError(error);
//                srl_refresh.setRefreshing(false);
//            }
//        });

        StatisticsService.getNoticeList(map).subscribe(new DefaultLoyoSubscriber<HttpStatistics>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                srl_refresh.setRefreshing(false);
            }

            @Override
            public void onNext(HttpStatistics httpStatistics) {
                srl_refresh.setRefreshing(false);
                if (null == httpStatistics) {
                    return;
                } else {
                    if (null != httpStatistics.process)
                        setprocessData(httpStatistics.process);
                    if (null != httpStatistics.bulking)
                        setBulkingData(httpStatistics.bulking);
                    if (null != httpStatistics.salechance)
                        setFunnelData(httpStatistics.salechance);
                    if (null != httpStatistics.achieves)
                        setAchievesData(httpStatistics.achieves);
                }
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
//        RestAdapterFactory.getInstance().build(Config_project.API_URL_STATISTICS())
//                .create(IStatistics.class).getProcessList(map, new Callback<List<HttpProcess>>() {
//            @Override
//            public void success(List<HttpProcess> result, Response response) {
//                HttpErrorCheck.checkResponse("【过程统计】数据：", response);
//                setprocessData(result);
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                HttpErrorCheck.checkError(error);
//            }
//        });

        StatisticsService.getProcessList(map).subscribe(new DefaultLoyoSubscriber<List<HttpProcess>>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                DialogHelp.cancelLoading();
            }

            @Override
            public void onNext(List<HttpProcess> httpProcesses) {
                DialogHelp.cancelLoading();
                setprocessData(httpProcesses);
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
//        RestAdapterFactory.getInstance().build(Config_project.API_URL_STATISTICS())
//                .create(IStatistics.class).getBulkingList(map, new Callback<List<HttpBulking>>() {
//            @Override
//            public void success(List<HttpBulking> result, Response response) {
//                HttpErrorCheck.checkResponse("【增量统计】数据：", response);
//                setBulkingData(result);
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                HttpErrorCheck.checkError(error);
//            }
//        });

        StatisticsService.getBulkingList(map).subscribe(new DefaultLoyoSubscriber<List<HttpBulking>>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                DialogHelp.cancelLoading();
            }

            @Override
            public void onNext(List<HttpBulking> httpBulkings) {
                DialogHelp.cancelLoading();
                setBulkingData(httpBulkings);
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
//        RestAdapterFactory.getInstance().build(Config_project.API_URL_STATISTICS())
//                .create(IStatistics.class).getAchievesList(map, new Callback<List<HttpAchieves>>() {
//            @Override
//            public void success(List<HttpAchieves> result, Response response) {
//                HttpErrorCheck.checkResponse("【业绩目标】数据：", response);
//                setAchievesData(result);
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                HttpErrorCheck.checkError(error);
//            }
//        });

        StatisticsService.getAchievesList(map).subscribe(new DefaultLoyoSubscriber<List<HttpAchieves>>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                DialogHelp.cancelLoading();
            }

            @Override
            public void onNext(List<HttpAchieves> result) {
                DialogHelp.cancelLoading();
                setAchievesData(result);
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
//        RestAdapterFactory.getInstance().build(Config_project.API_URL_STATISTICS())
//                .create(IStatistics.class).getFunnelList(map, new Callback<List<HttpSalechance>>() {
//            @Override
//            public void success(List<HttpSalechance> result, Response response) {
//                HttpErrorCheck.checkResponse("【销售漏斗】数据：", response);
//                setFunnelData(result);
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                HttpErrorCheck.checkError(error);
//            }
//        });

        StatisticsService.getFunnelList(map).subscribe(new DefaultLoyoSubscriber<List<HttpSalechance>>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                DialogHelp.cancelLoading();
            }

            @Override
            public void onNext(List<HttpSalechance> result) {
                DialogHelp.cancelLoading();
                setFunnelData(result);
            }
        });
    }

    /**
     * 设置 过程统计
     */
    private void setprocessData(List<HttpProcess> data) {
        if (ll_process.getChildCount() > 0) {
            ll_process.removeAllViews();
        }
        if (null == data || data.size() == 0) {
            im_process_no.setVisibility(View.VISIBLE);
            return;
        } else {
            im_process_no.setVisibility(View.GONE);
        }
        int max = 0;
        for (HttpProcess ele : data) {
            max += ele.totalNum;
        }

        int j = 0;
        for (int i = 0; i < data.size(); i++) {
            HttpProcess processData = data.get(i);
            if (j < 10) {
                ll_process.addView(new ProcessDataAdapter(app, processData.value, processData.totalNum, max, j));
                j++;
            } else {
                j = 0;
                ll_process.addView(new ProcessDataAdapter(app, processData.value, processData.totalNum, max, j));
                j++;
            }

        }
    }

    /**
     * 设置 增量统计
     */
    private void setBulkingData(List<HttpBulking> data) {
        if (null == data || data.size() == 0) {
            im_bulking_no.setVisibility(View.VISIBLE);
            ll_bulking_yes.setVisibility(View.GONE);
            return;
        } else {
            im_bulking_no.setVisibility(View.GONE);
            ll_bulking_yes.setVisibility(View.VISIBLE);
        }

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
        if (null == data || data.size() == 0) {
            im_achieves_no.setVisibility(View.VISIBLE);
            ll_achieves_yes.setVisibility(View.GONE);
            return;
        } else {
            im_achieves_no.setVisibility(View.GONE);
            ll_achieves_yes.setVisibility(View.VISIBLE);
        }

        for (HttpAchieves ele : data) {
            if (1 == ele.achieveType) {
                ll_achieves1.setVisibility(View.VISIBLE);
                tv_achieves_toal1.setText("目标赢单量  ￥" + Utils.setValueDouble(ele.achieveMoney));
                tv_achieves_finsh1.setText("已完成  ￥" + Utils.setValueDouble(ele.finshMoney));
                lv_round1.setMaxCount(ele.achieveMoney);
                lv_round1.setCount(ele.finshMoney);
                lv_round1.refalsh();
            } else if (2 == ele.achieveType) {
                ll_achieves2.setVisibility(View.VISIBLE);
                tv_achieves_toal2.setText("目标金额  ￥" + Utils.setValueDouble(ele.achieveMoney));
                tv_achieves_finsh2.setText("已完成  ￥" + Utils.setValueDouble(ele.finshMoney));
                lv_round2.setMaxCount(ele.achieveMoney);
                lv_round2.setCount(ele.finshMoney);
                lv_round2.refalsh();
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
        if (null == data || data.size() == 0) {
            im_funnel_no.setVisibility(View.VISIBLE);
            return;
        } else {
            im_funnel_no.setVisibility(View.GONE);
        }
        int bjIndex = 10 - data.size();
        for (int i = 0; i < data.size(); i++) {
            ll_funnel.addView(new FunnelDataAdapter(app, i, data.get(i), bjIndex));
            bjIndex++;
        }
    }

    /**
     * 选择此页面刷新全部数据
     */
    public void onInIt() {
        if (isRRefresh)
            getStatisticAllData();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        LogUtil.d("~~~~~~~~~~~~~~~~~~~~~~fragment2-->onActivityCreated()");
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        LogUtil.d("~~~~~~~~~~~~~~~~~~~~~~fragment2-->onDestroy()");
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        LogUtil.d("~~~~~~~~~~~~~~~~~~~~~~fragment2-->onPause()");
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        LogUtil.d("~~~~~~~~~~~~~~~~~~~~~~fragment2-->onResume()");
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        LogUtil.d("~~~~~~~~~~~~~~~~~~~~~~fragment2-->onStart()");
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        LogUtil.d("~~~~~~~~~~~~~~~~~~~~~~fragment2-->onStop()");
    }
}
