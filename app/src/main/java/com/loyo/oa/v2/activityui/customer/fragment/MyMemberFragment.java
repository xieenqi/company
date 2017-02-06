package com.loyo.oa.v2.activityui.customer.fragment;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.common.utils.UmengAnalytics;
import com.loyo.oa.dropdownmenu.DropDownMenu;
import com.loyo.oa.dropdownmenu.adapter.DefaultMenuAdapter;
import com.loyo.oa.dropdownmenu.callback.OnMenuModelsSelected;
import com.loyo.oa.dropdownmenu.filtermenu.TagMenuModel;
import com.loyo.oa.dropdownmenu.filtermenu.TimeFilterModel;
import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.CustomerAddActivity_;
import com.loyo.oa.v2.activityui.customer.CustomerDetailInfoActivity_;
import com.loyo.oa.v2.activityui.customer.CustomerManagerActivity;
import com.loyo.oa.v2.activityui.customer.MyContactMailList;
import com.loyo.oa.v2.activityui.customer.NearByCustomersActivity_;
import com.loyo.oa.v2.activityui.customer.adapter.MyCustomerAdapter;
import com.loyo.oa.v2.activityui.customer.event.EditCustomerRushEvent;
import com.loyo.oa.v2.activityui.customer.event.MyCustomerListRushEvent;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.customer.model.CustomerTageConfig;
import com.loyo.oa.v2.activityui.customer.model.NearCount;
import com.loyo.oa.v2.activityui.customer.presenter.MyCustomerFragPresenter;
import com.loyo.oa.v2.activityui.customer.presenter.impl.MyCustomerFragPresenterImpl;
import com.loyo.oa.v2.activityui.customer.viewcontrol.MyCustomerFragView;
import com.loyo.oa.v2.activityui.other.model.Tag;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.LocationUtilGD;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.UMengTools;
import com.loyo.oa.v2.tool.Utils;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 【我参与的】列表
 * Created by yyy on 16/6/1.
 */
public class MyMemberFragment extends BaseCustomerFragment {

    private MemberCallback memberCallback;

    public interface MemberCallback {
        void comeBackHeadPage();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            memberCallback = (MemberCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().getClass().getName()
                    + " must implements interface MyListener");
        }
    }

    /**
     * 获取数据,默认设置倒序
     */
    protected void getData() {
        HashMap<String, Object> params = new HashMap<>();
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
        params.put("field","lastActAt");
        params.put("order","asc");

    }

    /**
     * 新建回调 重启Manager
     */
    @Subscribe
    public void onMyCustomerListRushEvent(MyCustomerListRushEvent event) {
        memberCallback.comeBackHeadPage();
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
