package com.loyo.oa.v2.activityui.sale.contract;

import com.loyo.oa.v2.activityui.sale.bean.SaleRecord;
import com.loyo.oa.v2.common.BaseView;
import com.loyo.oa.v2.common.base.BasePersenter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by xeq on 16/11/29.
 */

public interface MySaleFrgmentContract {
    interface View extends BaseView {
        void refreshComplete();

        void bindData(ArrayList<SaleRecord> recordData);
    }

    interface Presenter extends BasePersenter {
        void pullUp();

        void pullDown();

        void refreshComplete();

        void getData();

        void getScreenData(String stageId, String sortType);
    }

    interface Model {
        void getData(HashMap<String, Object> map);
    }


}