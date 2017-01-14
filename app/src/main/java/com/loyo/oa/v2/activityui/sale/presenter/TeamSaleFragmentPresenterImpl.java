package com.loyo.oa.v2.activityui.sale.presenter;


import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.activityui.sale.bean.SaleList;
import com.loyo.oa.v2.activityui.sale.bean.SaleRecord;
import com.loyo.oa.v2.activityui.sale.contract.TeamSaleFragmentContract;
import com.loyo.oa.v2.activityui.sale.model.TeamSaleFragmentModelImpl;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.tool.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by xeq on 2016/11/29
 */

public class TeamSaleFragmentPresenterImpl implements TeamSaleFragmentContract.Presenter {
    private TeamSaleFragmentContract.View mView;
    private TeamSaleFragmentContract.Model model;
    private String xPath = "";
    private String sortType = "";
    private String userId = "";
    private String stageId = "";
    private PaginationX<SaleRecord> mPaginationX=new PaginationX<>();

    public TeamSaleFragmentPresenterImpl(TeamSaleFragmentContract.View mView) {
        this.mView = mView;
        model = new TeamSaleFragmentModelImpl(this);
    }

    @Override
    public void getData() {
        getLoadingView().setStatus(LoadingLayout.Loading);
        mPaginationX.setFirstPage();
        getPageData();
    }

    @Override
    public void getScreenData(String stageId, String sortType, String xPath, String userId) {
        getLoadingView().setStatus(LoadingLayout.Loading);
        this.stageId = stageId;
        this.sortType = sortType;
        this.xPath = xPath;
        this.userId = userId;
        mPaginationX.setFirstPage();
        getPageData();
    }

    @Override
    public LoadingLayout getLoadingView() {
        return mView.getLoadingUI();
    }

    @Override
    public void getPageData(Object... pag) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", mPaginationX.getShouldLoadPageIndex());
        map.put("pageSize", mPaginationX.getPageSize());
        map.put("stageId", stageId);
        map.put("sortType", sortType);
        map.put("xpath", xPath);
        map.put("userId", userId);
        model.getData(map,mPaginationX);
    }


    @Override
    public void bindPageData(Object obj) {
        PaginationX<SaleRecord> data = (PaginationX<SaleRecord>) obj;
        mPaginationX.loadRecords(data);
        if (mPaginationX.isEnpty()) {
            getLoadingView().setStatus(LoadingLayout.Empty);
        }else{
            getLoadingView().setStatus(LoadingLayout.Success);
        }
        mView.bindData(mPaginationX.getRecords());
        if(mPaginationX.isNeedToBackTop()){
            mView.backToTop();
        }

    }

    @Override
    public void sendPageData(Object obj) {

    }

    @Override
    public void pullUp() {
        getPageData();
    }

    @Override
    public void pullDown() {
        mPaginationX.setFirstPage();
        getPageData();

    }

    @Override
    public void refreshComplete() {
        mView.refreshComplete();
    }
}