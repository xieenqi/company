package com.loyo.oa.v2.activityui.sale.presenter;

import com.loyo.oa.v2.activityui.other.model.SaleStage;
import com.loyo.oa.v2.activityui.sale.contract.SaleOpportunitiesContract;
import com.loyo.oa.v2.activityui.sale.model.SaleOpportunitiesModelImpl;

import java.util.ArrayList;

/**
 * Created by xeq on 2016/11/28
 */

public class SaleOpportunitiesPresenterImpl implements SaleOpportunitiesContract.Presenter {

    private SaleOpportunitiesContract.View mView;
    private SaleOpportunitiesContract.Model mModle;

    public SaleOpportunitiesPresenterImpl(SaleOpportunitiesContract.View mView) {
        this.mView = mView;
        mModle = new SaleOpportunitiesModelImpl(this);
    }

    @Override
    public void getPageData(Object... pag) {
        mModle.getStageData();
    }

    @Override
    public void bindPageData(Object obj) {

    }

    @Override
    public void sendPageData(Object obj) {
        mView.setSaleStgesData((ArrayList<SaleStage>) obj);
    }

    @Override
    public void closePage() {
        mView.closePageView();
    }
}