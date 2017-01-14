package com.loyo.oa.v2.activityui.sale.model;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.activityui.sale.api.SaleService;
import com.loyo.oa.v2.activityui.sale.bean.SaleList;
import com.loyo.oa.v2.activityui.sale.bean.SaleRecord;
import com.loyo.oa.v2.activityui.sale.contract.MySaleFrgmentContract;
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

public class MySaleFrgmentModelImpl implements MySaleFrgmentContract.Model {
    private MySaleFrgmentContract.Presenter presenter;

    public MySaleFrgmentModelImpl(MySaleFrgmentContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void getData(HashMap<String, Object> map, final PaginationX<SaleRecord> mPaginationX) {

        SaleService.getSaleMyList(map).subscribe(new DefaultLoyoSubscriber<PaginationX<SaleRecord>>(presenter.getLoadingView()) {
            @Override
            public void onError(Throwable e) {
                presenter.refreshComplete();
                 /* 重写父类方法，不调用super */
                @LoyoErrorChecker.CheckType
                int type = mPaginationX.isEnpty() ? LoyoErrorChecker.LOADING_LAYOUT:LoyoErrorChecker.TOAST ;
                LoyoErrorChecker.checkLoyoError(e, type, presenter.getLoadingView());
            }

            @Override
            public void onNext(PaginationX<SaleRecord> saleMyLists) {
                presenter.refreshComplete();
                presenter.bindPageData(saleMyLists);
            }
        });
    }


}