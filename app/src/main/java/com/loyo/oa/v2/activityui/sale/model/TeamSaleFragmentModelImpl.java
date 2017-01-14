package com.loyo.oa.v2.activityui.sale.model;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.activityui.sale.api.SaleService;
import com.loyo.oa.v2.activityui.sale.bean.SaleList;
import com.loyo.oa.v2.activityui.sale.bean.SaleRecord;
import com.loyo.oa.v2.activityui.sale.contract.TeamSaleFragmentContract;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by xeq on 2016/11/29
 */

public class TeamSaleFragmentModelImpl implements TeamSaleFragmentContract.Model {

    private TeamSaleFragmentContract.Presenter mPersenter;

    public TeamSaleFragmentModelImpl(TeamSaleFragmentContract.Presenter mPersenter) {
        this.mPersenter = mPersenter;
    }

    @Override
    public void getData(HashMap<String, Object> map, final PaginationX<SaleRecord> mPaginationX) {
        SaleService.getSaleTeamList(map).subscribe(new DefaultLoyoSubscriber<PaginationX<SaleRecord>>(mPersenter.getLoadingView()) {
            @Override
            public void onError(Throwable e) {
                mPersenter.refreshComplete();
                 /* 重写父类方法，不调用super */
                @LoyoErrorChecker.CheckType
                int type = mPaginationX.isEnpty() ? LoyoErrorChecker.LOADING_LAYOUT:LoyoErrorChecker.TOAST ;
                LoyoErrorChecker.checkLoyoError(e, type, mPersenter.getLoadingView());
            }

            @Override
            public void onNext(PaginationX<SaleRecord> saleList) {
                mPersenter.getLoadingView().setStatus(LoadingLayout.Success);
                mPersenter.bindPageData(saleList);
                mPersenter.refreshComplete();
            }
        });
    }
}