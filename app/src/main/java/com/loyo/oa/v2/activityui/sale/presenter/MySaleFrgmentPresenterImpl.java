package com.loyo.oa.v2.activityui.sale.presenter;


import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.activityui.sale.api.SaleService;
import com.loyo.oa.v2.activityui.sale.bean.SaleRecord;
import com.loyo.oa.v2.activityui.sale.contract.MySaleFrgmentContract;
import com.loyo.oa.v2.activityui.sale.model.MySaleFrgmentModelImpl;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;

import java.util.HashMap;

/**
 * Created by xeq on 2016/11/29
 */

public class MySaleFrgmentPresenterImpl implements MySaleFrgmentContract.Presenter {
    private MySaleFrgmentContract.View mView;
    private MySaleFrgmentContract.Model model;
    private String stageId = "";
    private String sortType = "";
    private PaginationX<SaleRecord> mPaginationX = new PaginationX<>();

    public MySaleFrgmentPresenterImpl(MySaleFrgmentContract.View mView) {
        this.mView = mView;
        model = new MySaleFrgmentModelImpl(this);
    }

    @Override
    public void getData() {
        getLoadingView().setStatus(LoadingLayout.Loading);
        pullDown();
    }

    @Override
    public void getScreenData(String stageId, String sortType) {
        getLoadingView().setStatus(LoadingLayout.Loading);
        this.stageId = stageId;
        this.sortType = sortType;
        pullDown();
    }

    @Override
    public LoadingLayout getLoadingView() {
        return mView.getLoadingUI();
    }


    @Override
    public void getPageData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", mPaginationX.getShouldLoadPageIndex());
        map.put("pageSize", mPaginationX.getPageSize());
        map.put("stageId", stageId);
        map.put("sortType", sortType);
        model.getData(map,mPaginationX);
    }

    @Override
    public void getPageData(Object... pag) {
        //这个方法是继承下来的，没有卵用，重构以后删除
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