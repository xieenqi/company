package com.loyo.oa.v2.activityui.discuss.persenter;

import android.os.Handler;
import android.os.Message;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.discuss.HttpDiscussItem;
import com.loyo.oa.v2.activityui.discuss.viewcontrol.MyDisscussVControl;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.MyDiscuss;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

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
        RestAdapterFactory.getInstance().build(Config_project.API_URL_EXTRA()).create(MyDiscuss.class).
                getDisscussList(map, new RCallback<PaginationX<HttpDiscussItem>>() {
                    @Override
                    public void success(final PaginationX<HttpDiscussItem> discuss, final Response response) {
                        HttpErrorCheck.checkResponse(" 我的讨论数据： ", response);
                        if (!PaginationX.isEmpty(discuss)) {
                            if (isTopAdd) {
                                listData = null;
                                listData = discuss.getRecords();
                                if (listData != null && !(listData.size() > 0)) {
                                    vControl.getLoadingLayout().setStatus(LoadingLayout.Empty);
                                } else {
                                    vControl.getLoadingLayout().setStatus(LoadingLayout.Success);
                                }
                            } else {
                                listData.addAll(discuss.getRecords());
                            }
                            bindPageData(listData);
                        } else {
                            Global.Toast(!isTopAdd ? R.string.app_list_noMoreData : R.string.app_no_newest_data);
                        }
                        vControl.hideProgress();
                    }

                    @Override
                    public void failure(final RetrofitError error) {
                        HttpErrorCheck.checkError(error, vControl.getLoadingLayout());
                        super.failure(error);
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
