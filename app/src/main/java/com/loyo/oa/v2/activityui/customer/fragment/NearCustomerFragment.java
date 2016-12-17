package com.loyo.oa.v2.activityui.customer.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.CustomerDetailInfoActivity_;
import com.loyo.oa.v2.activityui.customer.adapter.NearCustomerAdapter;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.customermanagement.api.ICustomer;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.network.RetrofitAdapterFactory;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.BaseMainListFragment;
import com.loyo.oa.v2.tool.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 【附近客户】列表 我的附近  我团队的附近
 * Created by yyy on 16/6/1.
 */
public class NearCustomerFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2 {

    private View mView;
    private PullToRefreshListView listView;
    private NearCustomerAdapter adapter;
    private PaginationX<Customer> mPagination = new PaginationX<>(20);
    private ArrayList<Customer> mCustomers = new ArrayList<>();

    private String filed = "lastActAt";
    private String order = "desc";
    private String userId = "";
    private String tagItemIds = "";
    private String departmentId = "";
    private String position;

    private int customer_type;
    private int page = 1;
    private boolean isPullUp = false;
    private LoadingLayout ll_loading;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_near_cus, null);
            initView(mView);
        }
        return mView;
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        page = 1;
        isPullUp = false;
        mPagination.setPageIndex(1);
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        page++;
        isPullUp = true;
        mPagination.setPageIndex(mPagination.getPageIndex() + 1);
        getData();
    }

    public void initView(View view) {
        ll_loading = (LoadingLayout) view.findViewById(R.id.ll_loading);
        ll_loading.setStatus(LoadingLayout.Loading);
        ll_loading.setOnReloadListener(new LoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View v) {
                ll_loading.setStatus(LoadingLayout.Loading);
                onPullDownToRefresh(listView);
            }
        });
        customer_type = getArguments().getInt("type");
        position = getArguments().getString("position");
        listView = (PullToRefreshListView) view.findViewById(R.id.lv_list);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);
        onPullDownToRefresh(listView);
    }


    /**
     * 获取客户数据集
     *
     * @return
     */
    public ArrayList<Customer> getmCustomers() {
        return mCustomers;
    }

    /**
     * 获取数据,默认设置倒序
     */
    private void getData() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageIndex", page);
        params.put("pageSize", 15);
        params.put("field", filed);
        params.put("order", order);
        params.put("tagItemIds", tagItemIds);
        params.put("deptId", departmentId);
        params.put("userId", userId);
        LogUtil.d("客户查询传递参数：" + MainApp.gson.toJson(params));

        String url = "";
        switch (customer_type) {
            case Customer.CUSTOMER_TYPE_NEAR_MINE:
                params.put("position", position);
                url = FinalVariables.QUERY_NEAR_CUSTOMERS_SELF;
                break;
            case Customer.CUSTOMER_TYPE_NEAR_TEAM:
                params.put("position", position);
                url = FinalVariables.QUERY_NEAR_CUSTOMERS_TEAM;
                break;
            case Customer.CUSTOMER_TYPE_NEAR_COMPANY:
                params.put("position", position);
                url = FinalVariables.QUERY_CUSTOMERS_COMPANY;
                break;
        }

        if (TextUtils.isEmpty(url)) {
            LogUtil.d("没有url return");
            return;
        }

        RetrofitAdapterFactory.getInstance()
                .build(url)
                .create(ICustomer.class)
                .getCustomers(params)
                .compose(RetrofitAdapterFactory.<PaginationX<Customer>>compatApplySchedulers())
                .subscribe(new DefaultLoyoSubscriber<PaginationX<Customer>>() {
                    public void onError(Throwable e) {
                        /* 重写父类方法，不调用super */
                        @LoyoErrorChecker.CheckType
                        int type = mCustomers.size() > 0 ?
                                LoyoErrorChecker.TOAST : LoyoErrorChecker.LOADING_LAYOUT;
                        LoyoErrorChecker.checkLoyoError(e, type, ll_loading);
                        listView.onRefreshComplete();
                    }

                    public void onNext(PaginationX<Customer> customerPaginationX) {
                        if (null == customerPaginationX || PaginationX.isEmpty(customerPaginationX)) {
                            if (!isPullUp) {
                                mPagination.setPageIndex(1);
                                mPagination.setPageSize(20);
                                mCustomers.clear();
                                bindData();
                            } else {
                                Toast("没有更多数据了");
                                listView.onRefreshComplete();
                            }
                        } else {
                            mPagination = customerPaginationX;
                            if (!isPullUp) {
                                mCustomers.clear();
                            }
                            mCustomers.addAll(customerPaginationX.getRecords());
                            bindData();
                        }
                        listView.onRefreshComplete();
                        MainApp.getMainApp().isCutomerEdit = false;
                        ll_loading.setStatus(LoadingLayout.Success);
                        if (!isPullUp && mCustomers.size() == 0)
                            ll_loading.setStatus(LoadingLayout.Empty);
                    }
                });
    }

    /**
     * 绑定数据
     */
    private void bindData() {

        if (null == adapter) {
            adapter = new NearCustomerAdapter(getActivity(), mCustomers, customer_type);
            listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        if(!isPullUp&&mCustomers.size()==0)
            ll_loading.setStatus(LoadingLayout.Empty);
        else {
            ll_loading.setStatus(LoadingLayout.Success);
        }

        /**
         * 列表监听 进入客户详情页面
         * */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (customer_type == Customer.CUSTOMER_TYPE_NEAR_COMPANY) {
                    Toast("你没有查看权限");
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("Id", mCustomers.get(position - 1).getId());
                    intent.setClass(mActivity, CustomerDetailInfoActivity_.class);
                    startActivityForResult(intent, BaseMainListFragment.REQUEST_REVIEW);
                    mActivity.overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                }
            }
        });
    }
}
