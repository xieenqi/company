package com.loyo.oa.v2.activityui.customer.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.clue.bean.ClueListItem;
import com.loyo.oa.v2.activityui.customer.CustomerDetailInfoActivity;
import com.loyo.oa.v2.activityui.customer.CustomerDetailInfoActivity_;
import com.loyo.oa.v2.activityui.customer.CustomerDynamicAddActivity;
import com.loyo.oa.v2.activityui.customer.CustomerManagerActivity;
import com.loyo.oa.v2.activityui.customer.CustomerSearchActivity;
import com.loyo.oa.v2.activityui.customer.adapter.MyCustomerAdapter;
import com.loyo.oa.v2.activityui.customer.presenter.impl.DynamicSelectCustomerAndCuleFragmentPCersener;
import com.loyo.oa.v2.activityui.customer.viewcontrol.DynamicSelectCustomerAndCuleFragmentVControl;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseFragment;

import java.util.ArrayList;

/**
 * 写跟进 选择 【客户】
 * Created by xeq on 16/11/10.
 */

public class DynamicSelectCustomerFragment extends BaseFragment implements DynamicSelectCustomerAndCuleFragmentVControl, PullToRefreshBase.OnRefreshListener2 {
    private LinearLayout ll_search;
    private PullToRefreshListView lv_list;
    private DynamicSelectCustomerAndCuleFragmentPCersener pCersener;
    private MyCustomerAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_cule, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        pCersener = new DynamicSelectCustomerAndCuleFragmentPCersener(this, DynamicSelectCustomerAndCuleFragmentPCersener.SELECT_CUSTOMER);
        ll_search = (LinearLayout) view.findViewById(R.id.ll_search);
        lv_list = (PullToRefreshListView) view.findViewById(R.id.lv_list);
        lv_list.setMode(PullToRefreshBase.Mode.BOTH);
        lv_list.setOnRefreshListener(this);
        adapter = new MyCustomerAdapter(app);
        lv_list.setAdapter(adapter);
        Global.SetTouchView(ll_search);
        pCersener.getPageData();
        ll_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putInt(ExtraAndResult.EXTRA_TYPE, CustomerManagerActivity.CUSTOMER_MY);
                b.putInt("from", BaseActivity.DYNAMIC_MANAGE);
                app.startActivity(mActivity, CustomerSearchActivity.class, MainApp.ENTER_TYPE_RIGHT, false, b);
            }
        });
    }

    @Override
    public void showProgress(String message) {

    }

    @Override
    public void hideProgress() {
        lv_list.onRefreshComplete();
    }

    @Override
    public void showMsg(String message) {
        Toast(message);
    }


    /**
     * 绑定数据
     */
    @Override
    public void bindCustomerData(ArrayList<Customer> mCustomers) {
        adapter.setData(mCustomers);
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent mIntent = new Intent(mActivity, CustomerDynamicAddActivity.class);
                mIntent.putExtra(Customer.class.getName(), adapter.getItemData(position));
                startActivity(mIntent);
            }
        });
    }

    @Override
    public void bindClueData(ArrayList<ClueListItem> mClues) {

    }

    @Override
    public void getDataComplete() {
        lv_list.onRefreshComplete();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        pCersener.pullDownCus();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        pCersener.pullUpCus();
    }
}
