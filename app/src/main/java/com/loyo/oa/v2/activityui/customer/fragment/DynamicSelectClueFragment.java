package com.loyo.oa.v2.activityui.customer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.presenter.impl.DynamicSelectCustomerAndCuleFragmentPCersener;
import com.loyo.oa.v2.activityui.customer.viewcontrol.DynamicSelectCustomerAndCuleFragmentVControl;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;
import com.loyo.oa.v2.tool.BaseFragment;

import java.util.ArrayList;

/**
 * 写跟进 选择 【线索】
 * Created by xeq on 16/11/10.
 */

public class DynamicSelectClueFragment extends BaseFragment implements DynamicSelectCustomerAndCuleFragmentVControl {

    private LinearLayout ll_search;
    private PullToRefreshListView lv_list;
    private DynamicSelectCustomerAndCuleFragmentPCersener pCersener;

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
        pCersener = new DynamicSelectCustomerAndCuleFragmentPCersener(this, DynamicSelectCustomerAndCuleFragmentPCersener.SELECT_CULE);
        ll_search = (LinearLayout) view.findViewById(R.id.ll_search);
        lv_list = (PullToRefreshListView) view.findViewById(R.id.lv_list);
        Global.SetTouchView(ll_search);
    }

    @Override
    public void showProgress(String message) {


    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showMsg(String message) {
        Toast(message);
    }

    @Override
    public void bindData(ArrayList<Customer> mCustomers) {

    }

    @Override
    public void getDataComplete() {

    }
}
