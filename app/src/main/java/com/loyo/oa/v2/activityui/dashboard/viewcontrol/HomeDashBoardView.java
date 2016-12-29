package com.loyo.oa.v2.activityui.dashboard.viewcontrol;

import com.loyo.oa.v2.activityui.dashboard.model.CsclueFowUp;
import com.loyo.oa.v2.activityui.dashboard.model.MoneyCountModel;
import com.loyo.oa.v2.activityui.dashboard.model.StockListModel;
import com.loyo.oa.v2.common.BaseView;

/**
 * Created by yyy on 16/12/13.
 */

public interface HomeDashBoardView extends BaseView{

    void setScreenVal(String val);

    void followUpSuccessEmbl(CsclueFowUp csclueFowUp);

    void followUpErrorEmbl();

    void stockSuccessEmbl(StockListModel stockListModel);

    void stockErrorEmbl();

    void moneyConSuccessEmbl(MoneyCountModel moneyCountModel);

    void moneyConErrorEmbl();
}
