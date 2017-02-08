package com.loyo.oa.v2.activityui.customer.fragment;

import android.os.Bundle;
import android.view.View;

import com.loyo.oa.v2.activityui.customer.CustomerManagerActivity;
import com.loyo.oa.v2.activityui.customer.NearByCustomersActivity_;
import com.loyo.oa.v2.activityui.customer.event.MyCustomerListRushEvent;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.Utils;

import org.greenrobot.eventbus.Subscribe;

/**
 * 【我负责】列表
 * Created by yyy on 16/6/1.
 */
public class MyResponFragment  extends BaseCustomerFragment {

    @Override
    public void initView(View view) {
        super.initView(view);
        //只有我负责才现实添加按钮
        btn_add.setVisibility(View.VISIBLE);
        Utils.btnHideForListView(listView.getRefreshableView(), btn_add);
    }

    protected void getData() {
        params.put("pageIndex", mPagination.getShouldLoadPageIndex());
        params.put("pageSize", mPagination.getPageSize());
        CustomerService.getMyCustomers(params)
                .subscribe(new DefaultLoyoSubscriber<PaginationX<Customer>>() {
                    public void onError(Throwable e) {
                        fail(e);
                    }

                    public void onNext(PaginationX<Customer> customerPaginationX) {
                       success(customerPaginationX);
                        getNearCustomersInfo();
                    }
                });
    }

    @Override
    protected void onNearCustomerBtn() {
        Bundle bundle = new Bundle();
        bundle.putString("position", position);
        bundle.putSerializable("nearCount", nearCount);
        bundle.putInt("type", CustomerManagerActivity.NEARCUS_SELF);//团队2 个人1
        app.startActivity(mActivity, NearByCustomersActivity_.class, MainApp.ENTER_TYPE_RIGHT, false, bundle);
    }

    @Override
    protected void initFilterParams() {
        params.put("field","lastActAt");
        params.put("order","asc");
    }

    /**
     * 刷新列表回调
     */
    @Subscribe
    public void onMyCustomerListRushEvent(MyCustomerListRushEvent event) {
//        mPagination.setFirstPage();
//        getData();

        if(MyCustomerListRushEvent.EVENT_CODE_ADD==event.eventCode){
            mPagination.getRecords().add(0,event.data);
            adapter.notifyDataSetChanged();
        }
    }

}
