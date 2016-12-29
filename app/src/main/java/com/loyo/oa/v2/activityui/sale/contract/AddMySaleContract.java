package com.loyo.oa.v2.activityui.sale.contract;

import com.loyo.oa.hud.progress.LoyoProgressHUD;
import com.loyo.oa.v2.activityui.customer.model.ContactLeftExtras;
import com.loyo.oa.v2.activityui.sale.bean.SaleStage;
import com.loyo.oa.v2.common.BaseView;
import com.loyo.oa.v2.common.base.BasePersenter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by xeq on 16/11/30.
 */

public interface AddMySaleContract {
    interface View extends BaseView {

        LoyoProgressHUD getHUD();

        void setDynamicUI(ArrayList<ContactLeftExtras> dynamicData);

        void setHintUI(boolean... booleen);

        void setStageUI(String stage, String stageId);

        void creatSaleAction();

        void editSaleAction();
    }

    interface Presenter extends BasePersenter {

        LoyoProgressHUD getHUD();

        void getDynamic();

        void setDynamic(ArrayList<ContactLeftExtras> dynamic);

        void getStage();

        void setStage(ArrayList<SaleStage> saleStage);

        void creatSale(HashMap<String, Object> map);

        void editSale(HashMap<String, Object> map, String chanceId);

        void creatSaleSuccess();

        void editSaleSuccess();

    }

    interface Model {
        void getDynamicInfo();

        void getSaleStageData();

        void creatSaleSend(HashMap<String, Object> map);

        void editSaleSend(HashMap<String, Object> map, String chanceId);
    }


}