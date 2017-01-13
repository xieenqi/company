package com.loyo.oa.v2.activityui.dashboard.viewcontrol;

import com.loyo.oa.v2.activityui.dashboard.common.ScreenType;
import com.loyo.oa.v2.activityui.dashboard.model.FollowupStatistic;
import com.loyo.oa.v2.activityui.dashboard.model.HomePaymentModel;
import com.loyo.oa.v2.activityui.dashboard.model.MoneyStatistic;
import com.loyo.oa.v2.activityui.dashboard.model.StockStatistic;
import com.loyo.oa.v2.common.BaseView;

import java.util.ArrayList;

/**
 * Created by yyy on 16/12/13.
 */

public interface HomeDashBoardView extends BaseView{

    void setScreenVal(ScreenType screenType,int type,String value);

    void followUpSuccessEmbl(FollowupStatistic csclueFowUp);

    void followUpErrorEmbl();

    void stockSuccessEmbl(ArrayList<StockStatistic> stockListModel);

    void stockErrorEmbl();

    void stockErrorEmbl(String errorMsg);

    void moneyConSuccessEmbl(MoneyStatistic moneyCountModel);

    void moneyConErrorEmbl();


    void paymentConSuccessEmbl(HomePaymentModel paymentModel);

    void paymentConErrorEmbl();
}
