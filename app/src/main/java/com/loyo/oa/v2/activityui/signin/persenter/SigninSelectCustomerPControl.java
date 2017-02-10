package com.loyo.oa.v2.activityui.signin.persenter;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.activityui.followup.api.FollowUpService;
import com.loyo.oa.v2.activityui.signin.bean.SigninSelectCustomer;
import com.loyo.oa.v2.activityui.signin.viewcontrol.SigninSelectCustomerVControl;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by xeq on 16/11/15.
 */

public class SigninSelectCustomerPControl implements SigninSelectCustomerPersenter {
    private SigninSelectCustomerVControl vControl;
    private double longitude, latitude;
    //因为这个页面，数据接口是接口，所以，还是要手动管理分页
    private PaginationX<SigninSelectCustomer> paginationX=new PaginationX<>();
    public SigninSelectCustomerPControl(SigninSelectCustomerVControl vControl) {
        this.vControl = vControl;
    }

    /**
     * 定位成功第一次获取客户数据
     */
    @Override
    public void oneGetNearCustomer(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
//        vControl.showProgress("");
        getPageData();
    }

    @Override
    public void getPageData(final Object... pag) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageIndex", paginationX.getPageIndex()+1);
        params.put("pageSize", paginationX.pageSize);
        params.put("x", longitude);
        params.put("y", latitude);

        FollowUpService.getSiginiNearCustomer(params).subscribe(new DefaultLoyoSubscriber<BaseBeanT<ArrayList<SigninSelectCustomer>>>() {
            @Override
            public void onError(Throwable e) {
                vControl.getDataComplete();
                @LoyoErrorChecker.CheckType
                int type = paginationX.isEnpty() ? LoyoErrorChecker.LOADING_LAYOUT : LoyoErrorChecker.TOAST;
                LoyoErrorChecker.checkLoyoError(e, type, vControl.getLoadingLayout());
            }

            @Override
            public void onNext(BaseBeanT<ArrayList<SigninSelectCustomer>> result) {
                vControl.getDataComplete();
                if(null==result.data){
                    if(paginationX.isEnpty()){
                        vControl.getLoadingLayout().setStatus(LoadingLayout.Empty);
                    }else{
                        vControl.showMsg("没有更多数据了!");
                    }
                    return;
                }
                paginationX.getRecords().addAll(result.data);
                if(paginationX.isEnpty()){
                    vControl.getLoadingLayout().setStatus(LoadingLayout.Empty);
                }else{
                    paginationX.pageIndex++;
                    vControl.getLoadingLayout().setStatus(LoadingLayout.Success);
                    vControl.bindData(paginationX.getRecords());

                }
//                vControl.getLoadingLayout().setStatus(LoadingLayout.Success);
//                if (null == result.data || result.data.size() == 0) {
//                    if (isPull) {
//                        vControl.showMsg("没有更多数据了!");
//                    } else {
//                        listData.clear();
//                        vControl.getLoadingLayout().setStatus(LoadingLayout.Empty);
//                    }
//                    vControl.getDataComplete();
//                } else {
//                    if (isPull) {
//                        listData.addAll(result.data);
//                    } else {
//                        listData.clear();
//                        listData = result.data;
//                    }
//                }
//                vControl.getDataComplete();
//                vControl.bindData(listData);
            }
        });
    }

    @Override
    public void bindPageData(Object obj) {

    }

    @Override
    public void sendPageData(Object obj) {

    }


    @Override
    public void pullDown() {
        paginationX.setFirstPage();
        getPageData();
    }

    @Override
    public void pullUp() {
        getPageData();
    }
}
