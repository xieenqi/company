package com.loyo.oa.v2.activityui.setting.persenter;


import android.view.View;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.activityui.home.api.HomeService;
import com.loyo.oa.v2.activityui.setting.bean.SystemMessageItem;
import com.loyo.oa.v2.activityui.setting.viewcontrol.SystemMessageVControl;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【系统消息】persenter
 * Created by xeq on 16/11/2.
 */

public class SystemMessagePControl implements SystemMesssagePersenter {
    private SystemMessageVControl vControl;
    private int page = 1;
    private boolean isPull = false;
    private List<SystemMessageItem> data = new ArrayList<>();

    public SystemMessagePControl(SystemMessageVControl vControl) {
        this.vControl = vControl;
//        getPageData(page);
    }

    @Override
    public void getPageData(final Object... pag) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", pag[0] + "");
        map.put("pageSize", 20 + "");
//        RestAdapterFactory.getInstance().build(Config_project.API_URL_STATISTICS()).create(IMain.class).
//                getSystemMessage(map, new Callback<PaginationX<SystemMessageItem>>() {
//                    @Override
//                    public void success(PaginationX<SystemMessageItem> result, Response response) {
//                        HttpErrorCheck.checkResponse("系统消息:", response, vControl.getLoadingLayout());
//                        if (null == result.records || result.records.size() == 0) {
//                            if (isPull) {
//                                vControl.showMsg("没有更多数据了!");
//                            } else {
//                                data.clear();
//                                vControl.setEmptyView();
//                            }
//                            vControl.getDataComplete();
//                        } else {
//                            if (isPull) {
//                                data.addAll(result.records);
//                            } else {
//                                data.clear();
//                                data = result.records;
//                            }
//                        }
//                        vControl.getDataComplete();
//                        vControl.bindingView(data);
//                    }
//
//                    @Override
//                    public void failure(RetrofitError error) {
//                        HttpErrorCheck.checkError(error, vControl.getLoadingLayout(),(int)pag[0]==1?true:false);
//                        vControl.getDataComplete();
//                    }
//                });

        HomeService.getSystemMessage(map).subscribe(new DefaultLoyoSubscriber<PaginationX<SystemMessageItem>>(vControl.getLoadingLayout()) {
            @Override
            public void onError(Throwable e) {
                @LoyoErrorChecker.CheckType
                int type=(int)pag[0]!=1? LoyoErrorChecker.LOADING_LAYOUT:LoyoErrorChecker.TOAST;
                LoyoErrorChecker.checkLoyoError(e,type,vControl.getLoadingLayout());
                vControl.getDataComplete();
            }

            @Override
            public void onNext(PaginationX<SystemMessageItem> result) {
                vControl.getLoadingLayout().setStatus(LoadingLayout.Success);
                if (null == result.records || result.records.size() == 0) {
                    if (isPull) {
                        vControl.showMsg("没有更多数据了!");
                    } else {
                        data.clear();
                        vControl.setEmptyView();
                    }
                    vControl.getDataComplete();
                } else {
                    if (isPull) {
                        data.addAll(result.records);
                    } else {
                        data.clear();
                        data = result.records;
                    }
                }
                vControl.getDataComplete();
                vControl.bindingView(data);
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
