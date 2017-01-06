package com.loyo.oa.v2.activityui.sale.contract;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.hud.progress.LoyoProgressHUD;
import com.loyo.oa.v2.activityui.sale.bean.SaleDetails;
import com.loyo.oa.v2.common.BaseView;
import com.loyo.oa.v2.common.base.BasePersenter;

import java.util.HashMap;

/**
 * Created by xeq on 16/11/30.
 */

public interface SaleDetailContract {

    interface View extends BaseView {
        void closePageUI();

        void bindDataUI(SaleDetails saleDetails);

        void editSaleStageSuccessUI();

        LoadingLayout getLoadingUI();
        LoyoProgressHUD getHud();
    }

    interface Presenter extends BasePersenter {

        void closePge();

        void editSaleStage(HashMap<String, Object> map, String selectId);

        void editSaleStageSuccess();

        LoadingLayout getLoadingView();
        LoyoProgressHUD getHud();
    }

    interface Model {
        void getDataSend(String selectId);

        void editSaleStageSend(HashMap<String, Object> map, String selectId);
    }


}