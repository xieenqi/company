package com.loyo.oa.v2.activityui.discuss.persenter;

import android.os.Handler;
import android.os.Message;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.discuss.HttpDiscussItem;
import com.loyo.oa.v2.activityui.discuss.api.DiscussService;
import com.loyo.oa.v2.activityui.discuss.viewcontrol.MyDisscussVControl;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 【我的讨论】的相关操作
 * Created by xeq on 16/10/13.
 */

public class MyDisscussPControl implements MyDiscussPersenter {
    private boolean isTopAdd = false;
    private int pageIndex = 1;
    private ArrayList<HttpDiscussItem> listData = new ArrayList<>();
    private MyDisscussVControl vControl;
    private Handler handler;

    public MyDisscussPControl(MyDisscussVControl vControl, Handler handler) {
        this.vControl = vControl;
        this.handler = handler;
    }

    @Override
    public void getPageData(Object... pag) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", pageIndex + "");
        map.put("pageSize", "10");
//        RestAdapterFactory.getInstance().build(Config_project.API_URL_EXTRA()).create(MyDiscuss.class).
//                getDiscussList(map, new RCallback<PaginationX<HttpDiscussItem>>() {
//                    @Override
//                    public void success(final PaginationX<HttpDiscussItem> discuss, final Response response) {
//                        HttpErrorCheck.checkResponse(" 我的讨论数据： ", response);
//                        if (!PaginationX.isEmpty(discuss)) {
//                            if (isTopAdd) {
//                                listData = null;
//                                listData = discuss.getRecords();
//                            } else {
//                                listData.addAll(discuss.getRecords());
//                            }
//                            bindPageData(listData);
//                            vControl.getLoadingLayout().setStatus(LoadingLayout.Success);
//                        } else {
//                            if (isTopAdd) {
//                                vControl.getLoadingLayout().setStatus(LoadingLayout.Empty);
//                            } else {
//                                vControl.getLoadingLayout().setStatus(LoadingLayout.Success);
//                                Global.Toast(!isTopAdd ? R.string.app_list_noMoreData : R.string.app_no_newest_data);
//                            }
//                        }
//                        vControl.hideProgress();
//                    }
//
//                    @Override
//                    public void failure(final RetrofitError error) {
//                        HttpErrorCheck.checkError(error, vControl.getLoadingLayout(),pageIndex==1?true:false);
//                        super.failure(error);
//                        vControl.hideProgress();
//                    }
//                });

        DiscussService.getDiscussList(map).subscribe(new DefaultLoyoSubscriber<PaginationX<HttpDiscussItem>>() {
            @Override
            public void onError(Throwable e) {
                @LoyoErrorChecker.CheckType
                int type=pageIndex!=1? LoyoErrorChecker.TOAST:LoyoErrorChecker.LOADING_LAYOUT;
                LoyoErrorChecker.checkLoyoError(e,type,vControl.getLoadingLayout());
                vControl.hideProgress();
            }

            @Override
            public void onNext(PaginationX<HttpDiscussItem> discuss) {
                if (!PaginationX.isEmpty(discuss)) {
                    if (isTopAdd) {
                        listData = null;
                        listData = discuss.getRecords();
                    } else {
                        listData.addAll(discuss.getRecords());
                    }
                    bindPageData(listData);
                    vControl.getLoadingLayout().setStatus(LoadingLayout.Success);
                } else {
                    if (isTopAdd) {
                        vControl.getLoadingLayout().setStatus(LoadingLayout.Empty);
                    } else {
                        vControl.getLoadingLayout().setStatus(LoadingLayout.Success);
                        Global.Toast(!isTopAdd ? R.string.app_list_noMoreData : R.string.app_no_newest_data);
                    }
                }
                vControl.hideProgress();
            }
        });
    }

    @Override
    public void bindPageData(Object obj) {
        Message msg = new Message();
        msg.obj = obj;
        handler.sendMessage(msg);
    }

    @Override
    public void sendPageData(Object obj) {

    }

    @Override
    public void skipAtMy() {

    }

    @Override
    public void openItem() {

    }

    @Override
    public void onPullDown() {
        pageIndex = 1;
        isTopAdd = true;
        getPageData();
    }

    @Override
    public void onPullUp() {
        pageIndex++;
        isTopAdd = false;
        getPageData();
    }
}
