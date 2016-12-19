package com.loyo.oa.v2.activityui.sale.presenter;


import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.activityui.sale.bean.SaleList;
import com.loyo.oa.v2.activityui.sale.bean.SaleRecord;
import com.loyo.oa.v2.activityui.sale.contract.MySaleFrgmentContract;
import com.loyo.oa.v2.activityui.sale.model.MySaleFrgmentModelImpl;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by xeq on 2016/11/29
 */

public class MySaleFrgmentPresenterImpl implements MySaleFrgmentContract.Presenter {
    private MySaleFrgmentContract.View mView;
    private MySaleFrgmentContract.Model model;
    private int page = 1;
    private String stageId = "";
    private String sortType = "";
    private boolean isPullUp = false;
    private ArrayList<SaleRecord> recordData = new ArrayList<>();

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
    public void getPageData(Object... pag) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", (int) pag[0]);
        map.put("pageSize", 15);
        map.put("stageId", stageId);
        map.put("sortType", sortType);
        model.getData(map,(int) pag[0]);
    }

    @Override
    public void bindPageData(Object obj) {
        SaleList data = (SaleList) obj;
        if (null == data.records || data.records.size() == 0) {
            if (isPullUp) {
                mView.showMsg("没有更多数据了!");
            } else {
                recordData.clear();
                getLoadingView().setStatus(LoadingLayout.Empty);
            }
        } else {
            if (isPullUp) {
                recordData.addAll(data.records);
            } else {
                recordData.clear();
                recordData = data.records;
            }
        }
        mView.bindData(recordData);
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