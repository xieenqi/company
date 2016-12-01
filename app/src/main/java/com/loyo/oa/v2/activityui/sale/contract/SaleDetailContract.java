package com.loyo.oa.v2.activityui.sale.contract;

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
    }

    interface Presenter extends BasePersenter {

        void closePge();

        void editSaleStage(HashMap<String, Object> map, String selectId);

        void editSaleStageSuccess();
    }

    interface Model {
        void getDataSend(String selectId);

        void editSaleStageSend(HashMap<String, Object> map, String selectId);
    }


}