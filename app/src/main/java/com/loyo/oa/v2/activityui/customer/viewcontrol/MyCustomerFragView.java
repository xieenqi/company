package com.loyo.oa.v2.activityui.customer.viewcontrol;

import android.widget.PopupWindow;

/**
 * Created by loyo_dev1 on 16/10/28.
 */

public interface MyCustomerFragView {

    /*通讯录导入客户*/
    void intentAutoInsert(PopupWindow popupWindow);

    /*手动添加客户*/
    void intentHandInsert(PopupWindow popupWindow);

}
