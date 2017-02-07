package com.loyo.oa.v2.activityui.customer.fragment;

import android.app.Activity;
import android.os.Bundle;

import com.loyo.oa.v2.activityui.customer.CustomerManagerActivity;
import com.loyo.oa.v2.activityui.customer.NearByCustomersActivity_;
import com.loyo.oa.v2.activityui.customer.event.EditCustomerRushEvent;
import com.loyo.oa.v2.activityui.customer.event.MyCustomerListRushEvent;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;

import org.greenrobot.eventbus.Subscribe;

/**
 * 【我参与的】列表
 * Created by yyy on 16/6/1.
 */
public class MyMemberFragment extends BaseCustomerFragment {
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    /**
     * 获取数据,默认设置倒序
     */
    protected void getData() {
        params.put("pageIndex", mPagination.getShouldLoadPageIndex());
        params.put("pageSize", mPagination.getPageSize());
        CustomerService.getMemberCustomers(params)
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
        params.put("field", "lastActAt");
        params.put("order", "asc");

    }

    /**
     * 新建回调，直接把新添加的那一条，放在列表最前面
     */
    @Subscribe
    public void onMyCustomerListRushEvent(MyCustomerListRushEvent event) {
        if (MyCustomerListRushEvent.EVENT_CODE_ADD == event.eventCode) {
            mPagination.getRecords().add(0, event.data);
            adapter.notifyDataSetChanged();
        }
//        memberCallback.comeBackHeadPage();
    }

    /**
     * 编辑回调 刷新列表
     */
    @Subscribe
    public void onEditCustomerRushEvent(EditCustomerRushEvent event) {
//TODO 暂时不刷新数据，后面考虑，直接通过Event直接把编辑以后的数据push过来。
//        getData();
    }


}
