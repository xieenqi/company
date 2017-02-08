package com.loyo.oa.v2.activityui.customer.fragment;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.common.utils.UmengAnalytics;
import com.loyo.oa.dropdownmenu.adapter.DefaultMenuAdapter;
import com.loyo.oa.dropdownmenu.callback.OnMenuModelsSelected;
import com.loyo.oa.dropdownmenu.filtermenu.TagMenuModel;
import com.loyo.oa.dropdownmenu.filtermenu.TimeFilterModel;
import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.v2.activityui.customer.adapter.CommCustomerAdapter;
import com.loyo.oa.v2.activityui.customer.event.MyCustomerRushEvent;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * 【公海客户】列表
 * Created by yyy on 16/6/1.
 */
public class CommCustomerFragment extends BaseCustomerFragment {

    @Override
    protected void initFilterParams() {
        params.put("field","recycledAt");
        params.put("order","desc");

    }
    @Override
    protected void initDate() {
        adapter = new CommCustomerAdapter(app, mPagination.getRecords());
        listView.setAdapter(adapter);
        mPagination.setFirstPage();
        getData();
    }

    protected void loadFilterOptions() {
        List<FilterModel> options = new ArrayList<>();
        options.add(TimeFilterModel.getFilterModel3());
        options.add(TagMenuModel.getTagFilterModel(mTags));
        DefaultMenuAdapter adapter = new DefaultMenuAdapter(getContext(), options);
        filterMenu.setMenuAdapter(adapter);
        adapter.setCallback(new OnMenuModelsSelected() {
            @Override
            public void onMenuModelsSelected(int menuIndex, List<MenuModel> selectedModels, Object userInfo) {
                filterMenu.close();

                if (menuIndex == 0) { // TimeFilterModel
                    MenuModel model = selectedModels.get(0);
                    String key = model.getKey();
                    String value = model.getValue();
                    filterMenu.headerTabBar.setTitleAtPosition(value, menuIndex);
                    String[] keys = key.split(" ");
                    params.put("field",keys[0]);
                    params.put("order",keys[1]);
                    UmengAnalytics.umengSend(mActivity, UmengAnalytics.timePublic);
                } else if (menuIndex == 1) { // TagFilter
                    params.put("tagsParams",userInfo.toString());
                    UmengAnalytics.umengSend(mActivity, UmengAnalytics.tagPublic);
                }
                ll_loading.setStatus(LoadingLayout.Loading);
                mPagination.setFirstPage();
                getData();
            }
        });
    }

    /**
     * 获取数据
     */
    protected void getData() {
        params.put("pageIndex", mPagination.getShouldLoadPageIndex());
        params.put("pageSize", mPagination.getPageSize());

        CustomerService.getDumpedCustomers(params)
                .subscribe(new DefaultLoyoSubscriber<PaginationX<Customer>>() {
                    public void onError(Throwable e) {
                        /* 重写父类方法，不调用super */
                      fail(e);
                    }

                    public void onNext(PaginationX<Customer> customerPaginationX) {
                      success(customerPaginationX);
                    }
                });
    }

    /**
     * 刷新列表回调
     */
    @Subscribe
    public void onMyCustomerListRushEvent(MyCustomerRushEvent event) {
        mPagination.setFirstPage();
        getData();
    }

}
