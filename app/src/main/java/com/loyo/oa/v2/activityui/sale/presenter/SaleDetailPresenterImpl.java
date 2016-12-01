package com.loyo.oa.v2.activityui.sale.presenter;

import com.loyo.oa.v2.activityui.sale.bean.SaleDetails;
import com.loyo.oa.v2.activityui.sale.contract.SaleDetailContract;
import com.loyo.oa.v2.activityui.sale.model.SaleDetailModelImpl;

import java.util.HashMap;

/**
 * Created by xeq on 2016/11/30
 */

public class SaleDetailPresenterImpl implements SaleDetailContract.Presenter {

    private SaleDetailContract.View mView;
    private SaleDetailContract.Model model;

    public SaleDetailPresenterImpl(SaleDetailContract.View mView) {
        this.mView = mView;
        model = new SaleDetailModelImpl(this);
    }

    @Override
    public void getPageData(Object... pag) {
        mView.showProgress("");
        model.getDataSend((String) pag[0]);
    }

    @Override
    public void bindPageData(Object obj) {
        mView.bindDataUI((SaleDetails) obj);
    }

    @Override
    public void sendPageData(Object obj) {

    }

    @Override
    public void closePge() {
        mView.closePageUI();
    }

    @Override
    public void editSaleStage(HashMap<String, Object> map, String selectId) {
        mView.showProgress("");
    }

    @Override
    public void editSaleStageSuccess() {
        mView.editSaleStageSuccessUI();
    }
}