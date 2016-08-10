package com.loyo.oa.v2.activityui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.order.OrderAddActivity;
import com.loyo.oa.v2.activityui.order.OrderDetailActivity;
import com.loyo.oa.v2.activityui.order.adapter.MyOrderAdapter;
import com.loyo.oa.v2.activityui.order.bean.OrderListItem;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.Permission;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.ViewUtil;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 客户详情 【订单】列表
 * Created by xeq on 16/8/8.
 */
public class CustomerOrderList extends BaseActivity implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {

    private ViewGroup img_title_left, layout_add;
    private TextView tv_add;
    private Permission permission;
    private PullToRefreshListView listView_demands;
    private MyOrderAdapter listAdapter;
    private ArrayList<OrderListItem> listData = new ArrayList<>();
    private String customerId, customerName;
    private boolean isMyUser;
    private int page = 1;
    private boolean isPullDown = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        super.setTitle("订单");
        initUI();

    }

    private void initUI() {
        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        layout_add = (ViewGroup) findViewById(R.id.layout_add);
        tv_add = (TextView) findViewById(R.id.tv_add);
        tv_add.setText("新增订单");
        if (!isMyUser) {
            layout_add.setVisibility(View.GONE);
        }
        img_title_left.setOnClickListener(this);
        img_title_left.setOnTouchListener(new ViewUtil.OnTouchListener_view_transparency());
        layout_add.setOnClickListener(this);
        layout_add.setOnTouchListener(new ViewUtil.OnTouchListener_view_transparency());
        listView_demands = (PullToRefreshListView) findViewById(R.id.listView_demands);
        listAdapter = new MyOrderAdapter(this);
        listView_demands.setAdapter(listAdapter);
        listView_demands.setMode(PullToRefreshBase.Mode.BOTH);
        listView_demands.setOnRefreshListener(this);
        listView_demands.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent mIntent = new Intent();
//              mIntent.putExtra(ExtraAndResult.IS_TEAM, false);
                mIntent.putExtra(ExtraAndResult.EXTRA_ID, listAdapter.getItemData(position - 1).id);
                mIntent.setClass(CustomerOrderList.this, OrderDetailActivity.class);
                startActivityForResult(mIntent, CustomerOrderList.this.RESULT_FIRST_USER);
                overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
            }
        });
        //超级管理员\权限判断
        if (null != MainApp.user && !MainApp.user.isSuperUser()) {
            try {
                permission = (Permission) MainApp.rootMap.get("0215");//具体权限你没有做
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        Bundle bundle;
        switch (v.getId()) {
            case R.id.img_title_left:
                onBackPressed();
                break;
            case R.id.layout_add:

                if (null != permission && !permission.isEnable()) {
                    showGeneralDialog(true, false, "此功能权限已关闭，请联系管理员开启后再试！")
                            .setNoCancelOnclick(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    generalPopView.dismiss();
                                }
                            });
                } else {
                    if (customerId == null) {
                        break;
                    }
                    bundle = new Bundle();
                    bundle.putString(ExtraAndResult.EXTRA_NAME, customerName);
                    bundle.putString(ExtraAndResult.EXTRA_ID, customerId);
                    app.startActivityForResult(this, OrderAddActivity.class, MainApp.ENTER_TYPE_RIGHT, 200, bundle);
                }
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    /**
     * 获取 订单 列表
     */
    private void getData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", page);
        map.put("pageSize", 15);
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).
                getCutomerOrder(customerId, map, new RCallback<PaginationX<OrderListItem>>() {
                    @Override
                    public void success(PaginationX<OrderListItem> resultData, Response response) {
                        HttpErrorCheck.checkResponse(" 客户 订单 列表：", response);
                        listView_demands.onRefreshComplete();
                        if (null == resultData || null == resultData.records) {
                            Toast("没有数据");
                            return;
                        }
                        if (!isPullDown) {
                            listData.addAll(resultData.records);
                        } else {
                            listData = resultData.records;
                        }
                        listAdapter.setData(listData);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                        listView_demands.onRefreshComplete();
                        super.failure(error);
                    }
                });
    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        isPullDown = true;
        page = 1;
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        isPullDown = false;
        page++;
        getData();
    }
}
