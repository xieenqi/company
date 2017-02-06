package com.loyo.oa.v2.activityui.followup.persenter;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.activityui.clue.api.ClueService;
import com.loyo.oa.v2.activityui.clue.model.ClueList;
import com.loyo.oa.v2.activityui.clue.model.ClueListItem;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.followup.viewcontrol.FollowSelectCustomerAndCuleFragmentVControl;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.tool.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 跟进对象 选择客户 和 选择线索 公用的一个 persenter
 * Created by xeq on 16/11/10.
 */

public class FollowSelectCustomerAndCuleFragmentPCersener implements FollowSelectCustomerAndCuleFragmentPersener {
    public static final int SELECT_CUSTOMER = 101;
    public static final int SELECT_CULE = 102;
    private FollowSelectCustomerAndCuleFragmentVControl vControl;
    private int type;
    private boolean isPullCus, isPullClue;
    private PaginationX<Customer> paginationXCustomer=new PaginationX<>();
    private PaginationX<ClueListItem> paginationXClue=new PaginationX<>();

    public FollowSelectCustomerAndCuleFragmentPCersener(FollowSelectCustomerAndCuleFragmentVControl vControl, int type) {
        this.type = type;
        this.vControl = vControl;
    }

    @Override
    public void getPageData(Object... pag) {
        if (type == SELECT_CUSTOMER) {
            getCustomerData();
        } else if (type == SELECT_CULE) {
            getCuleData();
        }
    }

    @Override
    public void bindPageData(Object obj) {

    }

    @Override
    public void sendPageData(Object obj) {

    }

    /**
     * 获取我的客户的数据
     */
    private void getCustomerData() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageIndex", paginationXCustomer.getShouldLoadPageIndex());
        params.put("pageSize", paginationXCustomer.getPageSize());
        LogUtil.d("我的客户查询参数：" + MainApp.gson.toJson(params));

        CustomerService.getInvovedCustomers(params)
                .subscribe(new DefaultLoyoSubscriber<PaginationX<Customer>>(vControl.getLoadingLayout()) {
                    @Override
                    public void onError(Throwable e) {
                        /* 重写父类方法，不调用super */
                        @LoyoErrorChecker.CheckType
                        int type = paginationXCustomer.isEnpty() ?LoyoErrorChecker.LOADING_LAYOUT:LoyoErrorChecker.TOAST;
                        LoyoErrorChecker.checkLoyoError(e, type, vControl.getLoadingLayout());
                        vControl.getDataComplete();
                    }

                    @Override
                    public void onNext(PaginationX<Customer> customerPaginationX) {
                        vControl.getDataComplete();
                        paginationXCustomer.loadRecords(customerPaginationX);
                        if(paginationXCustomer.isEnpty()){
                            vControl.getLoadingLayout().setStatus(LoadingLayout.Empty);
                        }else{
                            vControl.getLoadingLayout().setStatus(LoadingLayout.Success);
                        }
                        vControl.bindCustomerData(paginationXCustomer.getRecords());
                    }
                });
    }

    /**
     * 获取我的 线索 的数据
     */
    private void getCuleData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", paginationXClue.getShouldLoadPageIndex());
        map.put("pageSize", paginationXClue.getPageSize());

        ClueService.getMyClueList(map).subscribe(new DefaultLoyoSubscriber<PaginationX<ClueListItem>>() {
            @Override
            public void onError(Throwable e) {
                /* 重写父类方法，不调用super */
                @LoyoErrorChecker.CheckType
                int type =paginationXClue.isEnpty() ?LoyoErrorChecker.LOADING_LAYOUT:LoyoErrorChecker.TOAST;
                LoyoErrorChecker.checkLoyoError(e, type, vControl.getLoadingLayout());
            }

            @Override
            public void onNext(PaginationX<ClueListItem> clueList) {
                vControl.getDataComplete();
                paginationXClue.loadRecords(clueList);
                if(paginationXClue.isEnpty()){
                    vControl.getLoadingLayout().setStatus(LoadingLayout.Empty);
                }else{
                    vControl.getLoadingLayout().setStatus(LoadingLayout.Success);
                }
                vControl.bindClueData(paginationXClue.getRecords());


            }
        });

    }

    @Override
    public void pullDownCus() {
        paginationXCustomer.setFirstPage();
        getPageData();
    }

    @Override
    public void pullUpCus() {

        getPageData();
    }

    @Override
    public void pullDownCule() {
        paginationXClue.setFirstPage();
        getPageData();
    }

    @Override
    public void pullUpCule() {
        getPageData();
    }
}
