package com.loyo.oa.v2.activityui.sale.model;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.activityui.sale.api.SaleService;
import com.loyo.oa.v2.activityui.sale.bean.SaleList;
import com.loyo.oa.v2.activityui.sale.contract.MySaleFrgmentContract;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
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
    public void getData(HashMap<String, Object> map, final int page) {
//        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISale.class).getSaleMyList(map, new RCallback<SaleList>() {
//            @Override
//            public void success(SaleList saleMyLists, Response response) {
//                HttpErrorCheck.checkResponse("销售机会 客户列表:", response, presenter.getLoadingView());
//                presenter.bindPageData(saleMyLists);
//                presenter.refreshComplete();
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                HttpErrorCheck.checkError(error, presenter.getLoadingView());
//                presenter.refreshComplete();
//            }
//        });

        SaleService.getSaleMyList(map).subscribe(new DefaultLoyoSubscriber<SaleList>(presenter.getLoadingView()) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                presenter.refreshComplete();
            }

            @Override
            public void onNext(SaleList saleMyLists) {
                presenter.getLoadingView().setStatus(LoadingLayout.Success);
                presenter.bindPageData(saleMyLists);
                presenter.refreshComplete();
            }
        });
    }
}