package com.loyo.oa.v2.activityui.signin.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attendance.adapter.DataSelectAdapter;
import com.loyo.oa.v2.activityui.attendance.model.DataSelect;
import com.loyo.oa.v2.activityui.signin.SignInActivity;
import com.loyo.oa.v2.activityui.signin.SignInfoActivity;
import com.loyo.oa.v2.activityui.signin.adapter.SignInListAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.LegWork;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.RecyclerItemClickListener;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.CustomRecyclerView;
import com.loyo.oa.v2.point.ILegwork;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * 【我的拜访】 列表
 */

@SuppressLint("ValidFragment")
public class SignInOfUserFragment extends BaseFragment implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {

    private ViewGroup layout_nodata;
    private PullToRefreshListView lv;
    private Button btn_add;
    private TextView data_time_tv;
    private int defaultPosition;

    private ArrayList<LegWork> legWorks = new ArrayList<>();
    private SignInListAdapter adapter;
    private boolean isTopAdd;
    private int windowW;
    private String startTime,endTime;
    private View mView;
    private PaginationX<LegWork> workPaginationX = new PaginationX<>(20);

    private LinearLayoutManager layoutManager;
    private DataSelectAdapter dataSelectAdapter;
    private ArrayList<DataSelect> dataSelects;

    private User mUser;
    private CustomRecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_signin, container, false);
            layout_nodata = (ViewGroup) mView.findViewById(R.id.layout_nodata);
            lv = (PullToRefreshListView) mView.findViewById(R.id.listView_legworks);
            recyclerView = (CustomRecyclerView) mView.findViewById(R.id.recy_data_select);

            data_time_tv = (TextView) mView.findViewById(R.id.data_time_tv);
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

            if (null != getArguments()) {
                if (getArguments().containsKey("user")) {
                    mUser = (User) getArguments().getSerializable("user");
                    defaultPosition = getArguments().getInt("position");
                }
            }

            if (!mUser.isCurrentUser()) {
                btn_add.setVisibility(View.GONE);
            }
            dataSelectInit();
            initTimeStr(defaultPosition);
            getData();

            Utils.btnHideForListView(lv.getRefreshableView(), btn_add);
        }
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void dataSelectInit(){

        int year = Integer.parseInt(DateTool.getNowTime("yyyy"));
        dataSelects = DateTool.getYearAllofDay(2015,year);
        Collections.reverse(dataSelects);
        dataSelects.remove(dataSelects.size() - 1);
        windowW = Utils.getWindowHW(getActivity()).getDefaultDisplay().getWidth();
        data_time_tv.setText(dataSelects.get(defaultPosition).yearMonDay);
        layoutManager = new LinearLayoutManager(getActivity(),1,true);//true 反向显示 false 正常显示
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        dataSelectAdapter = new DataSelectAdapter(mActivity,dataSelects,windowW,2,defaultPosition);
        recyclerView.setAdapter(dataSelectAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                dataSelectAdapter.selectPosition(position);
                dataSelectAdapter.notifyDataSetChanged();
                data_time_tv.setText(dataSelects.get(position).yearMonDay);
                initTimeStr(position);
                onPullDownToRefresh(lv);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    /**
     * 初始化时间显示
     * 获取某天开始时间和结束时间
     */
    private void initTimeStr(int position) {

        String startTimestr = DateTool.timet(dataSelects.get(position).mapOftime, "yyyy-MM-dd")+" 00:00:00";
        String endTimestr = DateTool.timet(dataSelects.get(position).mapOftime,"yyyy-MM-dd")+ " 23:59:59";
        startTime = DateTool.getDataOne(startTimestr, DateTool.DATE_FORMATE_ALL);
        endTime = DateTool.getDataOne(endTimestr,DateTool.DATE_FORMATE_ALL);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
        getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
    }

    /**
     * 绑定数据
     */
    private void bindData() {
        if (null == adapter) {
            if (null == legWorks || legWorks.size() == 0) {
                layout_nodata.setVisibility(View.VISIBLE);
                lv.setVisibility(View.GONE);
            } else {
                layout_nodata.setVisibility(View.GONE);
                lv.setVisibility(View.VISIBLE);
                adapter = new SignInListAdapter(mActivity, SignInListAdapter.TYPE_LIST_OF_USER, legWorks);
                lv.setAdapter(adapter);
            }

        } else {
            adapter.setLegWorks(legWorks);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取列表
     */
    private void getData() {
        showLoading("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", mUser.id);
        map.put("startAt", Long.parseLong(startTime));
        map.put("endAt",  Long.parseLong(endTime));
        map.put("custId", "");
        map.put("pageIndex", workPaginationX.getPageIndex());
        map.put("pageSize", isTopAdd ? legWorks.size() >= 20 ? legWorks.size() : 20 : 20);
        LogUtil.d("获取拜访列表map数据:"+ MainApp.gson.toJson(map));
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ILegwork.class).getLegworks(map, new RCallback<PaginationX<LegWork>>() {
            @Override
            public void success(PaginationX<LegWork> paginationX, Response response) {
                HttpErrorCheck.checkResponse("我客户拜访", response);
                if (paginationX== null) {
                    return;
                }

                lv.onRefreshComplete();
                workPaginationX = paginationX;
                if (isTopAdd) {
                    legWorks.clear();
                }


                legWorks.addAll(paginationX.getRecords());
                bindData();
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
                lv.onRefreshComplete();
                layout_nodata.setVisibility(View.VISIBLE);
                lv.setVisibility(View.GONE);
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
        isTopAdd = true;
        workPaginationX.setPageIndex(1);
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        isTopAdd = false;
        workPaginationX.setPageIndex(workPaginationX.getPageIndex() + 1);
        getData();
    }

}
