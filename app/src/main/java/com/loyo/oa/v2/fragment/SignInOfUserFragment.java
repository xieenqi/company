package com.loyo.oa.v2.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.SignInActivity;
import com.loyo.oa.v2.activity.SignInfoActivity;
import com.loyo.oa.v2.adapter.SignInListAdapter;
import com.loyo.oa.v2.beans.LegWork;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.point.ILegwork;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * 客户拜访－【我的拜访】  列表
 */

@SuppressLint("ValidFragment")
public class SignInOfUserFragment extends BaseFragment implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {

    private ViewGroup imgTimeLeft;
    private ViewGroup imgTimeRight;
    private PullToRefreshListView lv;
    private TextView tv_time;
    private Button btn_add;


    private ArrayList<LegWork> legWorks = new ArrayList<>();
    private SignInListAdapter adapter;
    private long endAt;
    private Calendar cal;

    private View mView;

    private PaginationX<LegWork> workPaginationX = new PaginationX<>(20);

    private boolean isTopAdd;
    private User mUser;
    private String currentTime, nextTime;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_signin, container, false);

            tv_time = (TextView) mView.findViewById(R.id.tv_time);

            imgTimeLeft = (ViewGroup) mView.findViewById(R.id.img_time_left);
            imgTimeRight = (ViewGroup) mView.findViewById(R.id.img_time_right);


            imgTimeLeft.setOnTouchListener(Global.GetTouch());
            imgTimeRight.setOnTouchListener(Global.GetTouch());
            imgTimeLeft.setOnClickListener(this);
            imgTimeRight.setOnClickListener(this);

            lv = (PullToRefreshListView) mView.findViewById(R.id.listView_legworks);

            btn_add = (Button) mView.findViewById(R.id.btn_add);
            btn_add.setOnTouchListener(Global.GetTouch());
            btn_add.setOnClickListener(this);

            lv.setMode(PullToRefreshBase.Mode.BOTH);
            lv.setOnRefreshListener(this);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    LegWork legWork = legWorks.get(i - 1);
                    previewLegwork(legWork);
                }
            });

            cal = Calendar.getInstance(Locale.CHINA);
            if (null != getArguments()) {
                if (getArguments().containsKey("user")) {
                    mUser = (User) getArguments().getSerializable("user");
                }
            }

            if (!mUser.isCurrentUser()) {
                btn_add.setVisibility(View.GONE);
            }
            initTimeStr(System.currentTimeMillis());
            endAt = DateTool.getEndAt_ofDay();
            getData();
        }
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    /**
     * 初始化时间显示
     *
     * @param mills
     */
    private void initTimeStr(long mills) {
        String time = app.df12.format(new Date(mills));
        tv_time.setText(time);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_time_left:
                previousDay();
                break;
            case R.id.img_time_right:
                java.util.Calendar c1 = java.util.Calendar.getInstance();
                java.util.Calendar c2 = java.util.Calendar.getInstance();
                currentTime = app.df12.format(System.currentTimeMillis());
                nextTime = app.df12.format(endAt);
                try{
                    c1.setTime(app.df12.parse(nextTime));//获得的时间
                    c2.setTime(app.df12.parse(currentTime));//系统当前时间
                    int resultTime = c1.compareTo(c2);
                    if (resultTime < 0 ) {
                        nextDay();
                    }
                }catch (Exception e){

                }

                break;
            case R.id.btn_add:
                create();
                break;
        }
    }

    /**
     * 新增拜访
     */
    private void create() {
        startActivityForResult(new Intent(mActivity, SignInActivity.class), FinalVariables.REQUEST_CREATE_LEGWORK);
    }

    /**
     * 前一天
     */
    private void previousDay() {
        if (cal.get(Calendar.DAY_OF_MONTH) == cal.getActualMinimum(Calendar.DAY_OF_MONTH)) {
            if (cal.get(Calendar.MONTH) == cal.getActualMinimum(Calendar.MONTH)) {
                cal.set((cal.get(Calendar.YEAR) - 1), cal.getActualMaximum(Calendar.MONTH), cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            } else {
                cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            }
        } else {
            cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);
        }

        refreshData();
    }

    /**
     * 后一天
     */
    private void nextDay() {
        if (cal.get(Calendar.DAY_OF_MONTH) == cal.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            if (cal.get(Calendar.MONTH) == cal.getActualMaximum(Calendar.MONTH)) {
                cal.set((cal.get(Calendar.YEAR) + 1), cal.getActualMinimum(Calendar.MONTH), 1);
            } else {
                cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
                cal.set(Calendar.DAY_OF_MONTH, 1);
            }
        } else {
            int dd = cal.get(Calendar.DAY_OF_MONTH);
            cal.set(Calendar.DAY_OF_MONTH, ++dd);
        }

        refreshData();
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        endAt = cal.getTime().getTime();
        //nextTime=app.df12.format(new Date(endAt));
        LogUtil.d(" 获得的时间 " + endAt);
        initTimeStr(cal.getTime().getTime());
        onPullDownToRefresh(lv);

    }

    /**
     * 绑定数据
     */
    private void bindData() {
        if (null == adapter) {
            adapter = new SignInListAdapter(mActivity, SignInListAdapter.TYPE_LIST_OF_USER, legWorks);
            lv.setAdapter(adapter);
        } else {
            adapter.setLegWorks(legWorks);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取列表
     */
    private void getData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", mUser.id);
        map.put("startAt", (endAt - DateTool.DAY_MILLIS) / 1000);
        map.put("endAt", endAt / 1000);
        map.put("custId", "");
        map.put("pageIndex", workPaginationX.getPageIndex());
        map.put("pageSize", isTopAdd ? legWorks.size() >= 20 ? legWorks.size() : 20 : 20);

        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ILegwork.class).getLegworks(map, new RCallback<PaginationX<LegWork>>() {
            @Override
            public void success(PaginationX<LegWork> paginationX, Response response) {
                lv.onRefreshComplete();
                workPaginationX = paginationX;
                if (isTopAdd) {
                    legWorks.clear();
                }
                legWorks.addAll(paginationX.getRecords());
                bindData();

                try {
                    LogUtil.dll("我的拜访:" + Utils.convertStreamToString(response.getBody().in()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                lv.onRefreshComplete();
                super.failure(error);
            }
        });
    }

    /**
     * 查看签到
     *
     * @param legWork
     */
    private void previewLegwork(LegWork legWork) {

        Intent intent = new Intent(mActivity, SignInfoActivity.class);
        intent.putExtra(LegWork.class.getName(), legWork);
        intent.putExtra("mCustomer", legWork.customer);
        intent.putExtra("Id", legWork.customerId);
        startActivity(intent);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != FinalVariables.REQUEST_CREATE_LEGWORK || resultCode != Activity.RESULT_OK || null == data) {
            return;
        }
        onPullDownToRefresh(lv);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        workPaginationX.setPageIndex(1);
        isTopAdd = true;
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        isTopAdd = false;
        workPaginationX.setPageIndex(workPaginationX.getPageIndex() + 1);
        getData();
    }

}
