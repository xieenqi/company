package com.loyo.oa.v2.activityui.sale.presenter;

import com.loyo.oa.v2.activityui.sale.contract.SaleOpportunitiesContract;
import com.loyo.oa.v2.activityui.sale.model.SaleOpportunitiesModelImpl;

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
    public void initStageData() {
        mModle.getStageData();
    }
}