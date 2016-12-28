package com.loyo.oa.v2.activityui.sale.model;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.activityui.sale.api.SaleService;
import com.loyo.oa.v2.activityui.sale.bean.SaleList;
import com.loyo.oa.v2.activityui.sale.contract.TeamSaleFragmentContract;
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

public class TeamSaleFragmentModelImpl implements TeamSaleFragmentContract.Model {

    private TeamSaleFragmentContract.Presenter mPersenter;

    public TeamSaleFragmentModelImpl(TeamSaleFragmentContract.Presenter mPersenter) {
        this.mPersenter = mPersenter;
    }

    @Override
    public void getData(HashMap<String, Object> map, final int page) {
//        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISale.class).getSaleTeamList(map, new RCallback<SaleList>() {
//            @Override
//            public void success(SaleList saleTeamList, Response response) {
//                HttpErrorCheck.checkResponse("团队线索列表", response, mPersenter.getLoadingView());
//                mPersenter.bindPageData(saleTeamList);
//                mPersenter.refreshComplete();
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                HttpErrorCheck.checkError(error, mPersenter.getLoadingView());
//                mPersenter.refreshComplete();
//            }
//        });

        SaleService.getSaleTeamList(map).subscribe(new DefaultLoyoSubscriber<SaleList>(mPersenter.getLoadingView()) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mPersenter.refreshComplete();
            }

            @Override
            public void onNext(SaleList saleList) {
                mPersenter.getLoadingView().setStatus(LoadingLayout.Success);
                mPersenter.bindPageData(saleList);
                mPersenter.refreshComplete();
            }
        });
    }
}