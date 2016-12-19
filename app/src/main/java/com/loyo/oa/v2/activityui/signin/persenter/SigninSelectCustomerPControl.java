package com.loyo.oa.v2.activityui.signin.persenter;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.activityui.signin.bean.SigninSelectCustomer;
import com.loyo.oa.v2.activityui.signin.viewcontrol.SigninSelectCustomerVControl;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ISigninOrFollowUp;
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
    private ArrayList<SigninSelectCustomer> listData = new ArrayList<>();
    private boolean isPull;
    private int page = 1;
    private double dis = 0;

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
        getPageData(page);
    }

    @Override
    public void getPageData(final Object... pag) {
        if (listData.size() > 0) {
            dis = listData.get(listData.size() - 1).distance;
        }
        if (((int) pag[0]) == 1)
            dis = 0;
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageIndex", pag[0]);
        params.put("pageSize", 20);
        params.put("x", longitude);
        params.put("y", latitude);
        params.put("dis", dis);
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISigninOrFollowUp.class).
                getSiginiNearCustomer(params, new Callback<BaseBeanT<ArrayList<SigninSelectCustomer>>>() {
                    @Override
                    public void success(BaseBeanT<ArrayList<SigninSelectCustomer>> result, Response response) {
                        HttpErrorCheck.checkResponse("拜访签到客户选择", response, vControl.getLoadingLayout());

                        if (null == result.data || result.data.size() == 0) {
                            if (isPull) {
                                vControl.showMsg("没有更多数据了!");
                            } else {
                                listData.clear();
                                vControl.getLoadingLayout().setStatus(LoadingLayout.Empty);
                            }
                            vControl.getDataComplete();
                        } else {
                            if (isPull) {
                                listData.addAll(result.data);
                            } else {
                                listData.clear();
                                listData = result.data;
                            }
                        }
                        vControl.getDataComplete();
                        vControl.bindData(listData);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        vControl.getDataComplete();
                        HttpErrorCheck.checkError(error, vControl.getLoadingLayout(), ((int) pag[0]) == 1 ? true : false);
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
        isPull = false;
        page = 1;
        getPageData(page);
    }

    @Override
    public void pullUp() {
        isPull = true;
        page++;
        getPageData(page);
    }
}
