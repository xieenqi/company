package com.loyo.oa.v2.ui.activity.customer.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.ui.activity.customer.CustomerDetailInfoActivity_;
import com.loyo.oa.v2.ui.activity.customer.CustomerManagerActivity;
import com.loyo.oa.v2.ui.activity.customer.adapter.NearCustomerAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.DialogHelp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.BaseMainListFragment;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;
import java.util.ArrayList;
import java.util.HashMap;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【附近客户】列表
 * Created by yyy on 16/6/1.
 */
public class NearCustomerFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2{

    private View mView;
    private ViewStub emptyView;
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

    public void initView(View view){
        customer_type = getArguments().getInt("type");
        position = getArguments().getString("position");
        emptyView = (ViewStub) view.findViewById(R.id.vs_nodata);
        listView = (PullToRefreshListView) view.findViewById(R.id.lv_list);
        listView.setEmptyView(emptyView);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);
        showLoading("");
        getData();
        DialogHelp.cancelLoading();
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

        RestAdapterFactory.getInstance().build(url).create(ICustomer.class).query(params, new RCallback<PaginationX<Customer>>() {
                    @Override
                    public void success(PaginationX<Customer> customerPaginationX, Response response) {
                        HttpErrorCheck.checkResponse("附近", response);
                        if (null == customerPaginationX || PaginationX.isEmpty(customerPaginationX)) {
                            if (!isPullUp) {
                                mPagination.setPageIndex(1);
                                mPagination.setPageSize(20);
                                mCustomers.clear();
                                bindData();
                            } else {
                                Toast("没有更多数据了");
                                listView.onRefreshComplete();
                                return;
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
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                        listView.onRefreshComplete();
                    }
                }
        );
    }

    /**
     * 绑定数据
     */
    private void bindData() {

        if (null == adapter) {
            adapter = new NearCustomerAdapter(getActivity(),mCustomers,customer_type);
            listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        /**
         * 列表监听 进入客户详情页面
         * */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (customer_type == Customer.CUSTOMER_TYPE_NEAR_COMPANY) {
                    Toast("你没有查看权限");
                }else{
                    Intent intent = new Intent();
                    intent.putExtra("Id", mCustomers.get(position - 1).getId());
                    intent.putExtra(ExtraAndResult.EXTRA_TYPE, CustomerManagerActivity.CUSTOMER_MY);
                    intent.setClass(mActivity, CustomerDetailInfoActivity_.class);
                    startActivityForResult(intent, BaseMainListFragment.REQUEST_REVIEW);
                }
            }
        });
    }
}
