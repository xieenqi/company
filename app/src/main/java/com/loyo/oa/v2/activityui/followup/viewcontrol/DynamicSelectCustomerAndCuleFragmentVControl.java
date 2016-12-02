package com.loyo.oa.v2.activityui.followup.viewcontrol;

import com.loyo.oa.v2.activityui.clue.bean.ClueListItem;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.common.BaseView;

import java.util.ArrayList;

/**
 * 跟进对象 选择客户 和 选择线索 公用的一个 viewControl
 * Created by xeq on 16/11/10.
 */

public interface DynamicSelectCustomerAndCuleFragmentVControl extends BaseView {
    void bindCustomerData(ArrayList<Customer> mCustomers);

    void bindClueData(ArrayList<ClueListItem> mClues);

    void getDataComplete();
}


