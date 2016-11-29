package com.loyo.oa.v2.activityui.sale.presenter;

import android.view.View;

import com.loyo.oa.v2.activityui.sale.bean.SaleList;
import com.loyo.oa.v2.activityui.sale.bean.SaleRecord;
import com.loyo.oa.v2.activityui.sale.contract.MySaleFrgmentContract;
import com.loyo.oa.v2.activityui.sale.model.MySaleFrgmentModelImpl;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ISale;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

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
        pullDown();
    }

    @Override
    public void getScreenData(String stageId, String sortType) {
        this.stageId = stageId;
        this.sortType = sortType;
        pullDown();
    }

    @Override
    public void getPageData(Object... pag) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", (int) pag[0]);
        map.put("pageSize", 15);
        map.put("stageId", stageId);
        map.put("sortType", sortType);
        model.getData(map);

//        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISale.class).getSaleMyList(map, new RCallback<SaleList>() {
//            @Override
//            public void success(SaleList saleMyLists, Response response) {
//                HttpErrorCheck.checkResponse("销售机会 客户列表:", response);
////                if (null == saleMyLists.records || saleMyLists.records.size() == 0) {
////                    if (isPull) {
////                        Toast("没有更多数据了!");
////                    } else {
////                        recordData.clear();
////                        re_nodata.setVisibility(View.VISIBLE);
////                    }
////                    listView.onRefreshComplete();
////                } else {
////                    if (isPull) {
////                        recordData.addAll(saleMyLists.records);
////                    } else {
////                        recordData.clear();
////                        recordData = saleMyLists.records;
////                    }
////                }
////                bindData();
////                listView.onRefreshComplete();
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                HttpErrorCheck.checkError(error);
//            }
//        });
    }

    @Override
    public void bindPageData(Object obj) {
        SaleList data = (SaleList) obj;
        if (null == data.records || data.records.size() == 0) {
            if (isPullUp) {
                mView.showMsg("没有更多数据了!");
            } else {
                recordData.clear();
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