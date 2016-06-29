package com.loyo.oa.v2.ui.activity.customer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.ui.activity.customer.adapter.DemandsRadioListViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Demand;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseMainListFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.ViewUtil;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【购买意向】
 */

public class DemandsManageActivity extends BaseActivity implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {

    ViewGroup img_title_left, layout_add;
    private PullToRefreshListView listView_demands;
    private DemandsRadioListViewAdapter demandsRadioListViewAdapter;
    private ArrayList<Demand> lstData_Demand = new ArrayList<Demand>();
    private String customerId, customerName;
    private boolean isMyUser;
    private boolean isChanged;
    private boolean isTopAdd = true;
    private PaginationX<Demand> paginationX = new PaginationX<>(20);
    public static final int VIEW_DEMANDS = 200;
    public static final int CREATE_DEMANDS = 300;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTouchView(NO_SCROLL);
        setContentView(R.layout.activity_demands_manage);

        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                isMyUser = bundle.getBoolean("isMyUser");
                customerId = bundle.getString(ExtraAndResult.EXTRA_ID);
                customerName = bundle.getString(ExtraAndResult.EXTRA_NAME);
            }
        }

        super.setTitle("购买意向");
        initUI();
        getData();
    }

    void initUI() {

        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        layout_add = (ViewGroup) findViewById(R.id.layout_add);

        if (!isMyUser) {
            layout_add.setVisibility(View.GONE);
        }

        img_title_left.setOnClickListener(this);
        img_title_left.setOnTouchListener(new ViewUtil.OnTouchListener_view_transparency());
        layout_add.setOnClickListener(this);
        layout_add.setOnTouchListener(new ViewUtil.OnTouchListener_view_transparency());

        listView_demands = (PullToRefreshListView) findViewById(R.id.listView_demands);
    }

    /**
     * 获取购买意向
     */
    private void getData() {
        showLoading("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", paginationX.getPageIndex());
        map.put("pageSize", isTopAdd ? lstData_Demand.size() >= 20 ? lstData_Demand.size() : 20 : 20);
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).
                getDemands(customerId, map, new RCallback<PaginationX<Demand>>() {
                    @Override
                    public void success(final PaginationX<Demand> demandPaginationX, final Response response) {
                        HttpErrorCheck.checkResponse(" 购买意向详情：", response);
                        listView_demands.onRefreshComplete();
                        if (!PaginationX.isEmpty(demandPaginationX)) {
                            paginationX = demandPaginationX;
                            if (isTopAdd) {
                                lstData_Demand.clear();
                            }
                            lstData_Demand.addAll(paginationX.getRecords());
                            bindData();
                        }
                    }

                    @Override
                    public void failure(final RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                        listView_demands.onRefreshComplete();
                        super.failure(error);
                    }
                });
    }

    /**
     * 绑定数据
     */
    private void bindData() {
        if (lstData_Demand != null) {
            demandsRadioListViewAdapter = new DemandsRadioListViewAdapter(this, lstData_Demand, isMyUser, customerId, customerName);
            listView_demands.setAdapter(demandsRadioListViewAdapter);
            listView_demands.setMode(PullToRefreshBase.Mode.BOTH);
            listView_demands.setOnRefreshListener(this);
        }
    }

    @Override
    public void onClick(final View v) {
        Bundle bundle;
        switch (v.getId()) {
            case R.id.img_title_left:
                onBackPressed();
                break;
            case R.id.layout_add:
                if (customerId == null) {
                    break;
                }
                bundle = new Bundle();
                bundle.putString(ExtraAndResult.EXTRA_NAME, customerName);
                bundle.putString(ExtraAndResult.EXTRA_ID, customerId);
                app.startActivityForResult(this, DemandsAddActivity.class, MainApp.ENTER_TYPE_RIGHT, CREATE_DEMANDS, bundle);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        app.finishActivity((Activity) mContext, MainApp.ENTER_TYPE_LEFT, isChanged ? RESULT_OK : RESULT_CANCELED, new Intent());
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        switch (requestCode) {
            case CREATE_DEMANDS:
            case VIEW_DEMANDS:
            case BaseMainListFragment.REQUEST_REVIEW:
                isChanged = true;
                onPullDownToRefresh(listView_demands);
                break;
            default:

                break;
        }
    }

    @Override
    public void onPullDownToRefresh(final PullToRefreshBase refreshView) {
        isTopAdd = true;
        paginationX.setPageIndex(1);
        getData();
    }

    @Override
    public void onPullUpToRefresh(final PullToRefreshBase refreshView) {
        isTopAdd = false;
        paginationX.setPageIndex(paginationX.getPageIndex() + 1);
        getData();
    }

}
