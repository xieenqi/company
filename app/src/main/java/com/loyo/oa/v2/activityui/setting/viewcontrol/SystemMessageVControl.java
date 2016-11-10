package com.loyo.oa.v2.activityui.setting.viewcontrol;

import com.loyo.oa.v2.activityui.setting.bean.SystemMessageItem;
import com.loyo.oa.v2.common.BaseView;

import java.util.List;

/**
 * Created by xeq on 16/10/14.
 */

public interface SystemMessageVControl extends BaseView {

    void setEmptyView();

    void getDataComplete();

    void bindingView(List<SystemMessageItem> data);
}
