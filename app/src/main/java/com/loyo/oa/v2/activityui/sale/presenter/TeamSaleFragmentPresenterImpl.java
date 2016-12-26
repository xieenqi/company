package com.loyo.oa.v2.activityui.sale.presenter;


import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.activityui.sale.bean.SaleList;
import com.loyo.oa.v2.activityui.sale.bean.SaleRecord;
import com.loyo.oa.v2.activityui.sale.contract.TeamSaleFragmentContract;
import com.loyo.oa.v2.activityui.sale.model.TeamSaleFragmentModelImpl;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.tool.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by xeq on 2016/11/29
 */

public class TeamSaleFragmentPresenterImpl implements TeamSaleFragmentContract.Presenter {
    private TeamSaleFragmentContract.View mView;
    private TeamSaleFragmentContract.Model model;
    private ArrayList<SaleRecord> mData = new ArrayList<>();
    private boolean isPullUp = false;
    private int page = 1;
    private String xPath = "";
    private String sortType = "";
    private String userId = "";
    private String stageId = "";

    public TeamSaleFragmentPresenterImpl(TeamSaleFragmentContract.View mView) {
        this.mView = mView;
        model = new TeamSaleFragmentModelImpl(this);
    }

    @Override
    public void getData() {
        getLoadingView().setStatus(LoadingLayout.Loading);
        pullDown();
    }

    @Override
    public void getScreenData(String stageId, String sortType, String xPath, String userId) {
        getLoadingView().setStatus(LoadingLayout.Loading);
        this.stageId = stageId;
        this.sortType = sortType;
        this.xPath = xPath;
        this.userId = userId;
        pullDown();
    }

    @Override
    public LoadingLayout getLoadingView() {
        return mView.getLoadingUI();
    }

    @Override
    public void getPageData(Object... pag) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", (int) pag[0]);
        map.put("pageSize", 15);
        map.put("stageId", stageId);
        map.put("sortType", sortType);
        map.put("xpath", xPath);
        map.put("userId", userId);
        LogUtil.d("团队机会列表 请求数据:" + MainApp.gson.toJson(map));
        model.getData(map, (int) pag[0]);
    }

    @Override
    public void bindPageData(Object obj) {
        SaleList data = (SaleList) obj;
        if (null == data.records || data.records.size() == 0) {
            if (isPullUp) {
                mView.showMsg("没有更多数据了!");
            } else {
                mData.clear();
                getLoadingView().setStatus(LoadingLayout.Empty);
            }
        } else {
            if (isPullUp) {
                mData.addAll(data.records);
            } else {
                mData.clear();
                mData = data.records;
            }
        }
        mView.bindData(mData);
    }

    @Override
    public void sendPageData(Object obj) {

    }

    @Override
    public void pullUp() {
        isPullUp = true;
        page++;
        getPageData(page);
    }

    @Override
    public void pullDown() {
        isPullUp = false;
        page = 1;
        getPageData(page);

    }

    @Override
    public void refreshComplete() {
        mView.refreshComplete();
    }
}