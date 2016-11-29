package com.loyo.oa.v2.activityui.sale.contract;

import com.loyo.oa.v2.activityui.other.model.SaleStage;
import com.loyo.oa.v2.common.BaseView;
import com.loyo.oa.v2.common.base.BasePersenter;

import java.util.ArrayList;

/**
 * Created by xeq on 16/11/28.
 */

public interface SaleOpportunitiesContract {
    interface View extends BaseView{
        void setSaleStgesData(ArrayList<SaleStage> saleStages);
        void closePageView();
    }

    interface Presenter extends BasePersenter{
        void closePage();
    }

    interface Model {
        void getStageData();
    }


}