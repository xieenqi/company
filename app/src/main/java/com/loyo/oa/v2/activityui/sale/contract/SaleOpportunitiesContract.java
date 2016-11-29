package com.loyo.oa.v2.activityui.sale.contract;

import com.loyo.oa.v2.activityui.other.model.SaleStage;

import java.util.ArrayList;

/**
 * Created by xeq on 16/11/28.
 */

public interface SaleOpportunitiesContract {
    public interface View {
        void setSaleStgesData(ArrayList<SaleStage> saleStages);
    }

    public interface Presenter {
        void initStageData();
    }

    public interface Model {
        void getStageData();
    }


}