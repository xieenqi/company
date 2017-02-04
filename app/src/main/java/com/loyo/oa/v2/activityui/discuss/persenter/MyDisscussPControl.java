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

    private PaginationX<HttpDiscussItem> paginationX=new PaginationX<>(20);
    private MyDisscussVControl vControl;
    private Handler handler;


    public MyDisscussPControl(MyDisscussVControl vControl, Handler handler) {
        this.vControl = vControl;
        this.handler = handler;
    }

    @Override
    public void getPageData(final Object... pag) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", paginationX.getShouldLoadPageIndex());
        map.put("pageSize", paginationX.getPageSize());
        DiscussService.getDiscussList(map).subscribe(new DefaultLoyoSubscriber<PaginationX<HttpDiscussItem>>() {
            @Override
            public void onError(Throwable e) {
                @LoyoErrorChecker.CheckType
                int type=paginationX.isEnpty()? LoyoErrorChecker.LOADING_LAYOUT:LoyoErrorChecker.TOAST;
                LoyoErrorChecker.checkLoyoError(e,type,vControl.getLoadingLayout());
                vControl.hideProgress();
            }

            @Override
            public void onNext(PaginationX<HttpDiscussItem> discuss) {
                paginationX.loadRecords(discuss);
                if(paginationX.isEnpty()){
                    vControl.getLoadingLayout().setStatus(LoadingLayout.Empty);
                }else{
                    bindPageData(paginationX.getRecords());
                    vControl.getLoadingLayout().setStatus(LoadingLayout.Success);
                }
                vControl.hideProgress();

//                if (!PaginationX.isEmpty(discuss)) {
//                    if (isTopAdd) {
//                        listData = null;
//                        listData = discuss.getRecords();
//                    } else {
//                        listData.addAll(discuss.getRecords());
//                    }
//                    bindPageData(listData);
//                    vControl.getLoadingLayout().setStatus(LoadingLayout.Success);
//                } else {
//                    if (isTopAdd) {
//                        vControl.getLoadingLayout().setStatus(LoadingLayout.Empty);
//                    } else {
//                        vControl.getLoadingLayout().setStatus(LoadingLayout.Success);
//                        Global.Toast(!isTopAdd ? R.string.app_list_noMoreData : R.string.app_no_newest_data);
//                    }
//                }
//                vControl.hideProgress();
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
        paginationX.setFirstPage();
        getPageData();
    }

    @Override
    public void onPullUp() {
        getPageData();
    }
}
