package com.loyo.oa.v2.activityui.sale.contract;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.activityui.sale.bean.SaleRecord;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.BaseView;
import com.loyo.oa.v2.common.base.BasePersenter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by xeq on 16/11/29.
 */

public interface TeamSaleFragmentContract {
    interface View extends BaseView {
        void refreshComplete();

        void bindData(ArrayList<SaleRecord> recordData);

        LoadingLayout getLoadingUI();
        void backToTop();
    }

    interface Presenter extends BasePersenter {
        void pullUp();

        void pullDown();

        void refreshComplete();

        void getData();

        void getScreenData(String stageId, String sortType, String xPath, String userId);

        LoadingLayout getLoadingView();
    }

    interface Model {
        void getData(HashMap<String, Object> map, PaginationX<SaleRecord> mPaginationX);
    }


}