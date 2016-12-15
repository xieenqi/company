package com.loyo.oa.v2.activityui.wfinstance.viewcontrol;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.activityui.wfinstance.bean.WflnstanceItemData;
import com.loyo.oa.v2.common.BaseView;

import java.util.ArrayList;

/**
 * Created by loyo_dev1 on 16/10/17.
 */

public interface WfinMyApproveView extends BaseView {

    /*刷新下拉数据*/
    void setPullDownToRefresh();

    /*停止刷新*/
    void setListRefreshComplete();

    /*绑定数据*/
    void bindListData(ArrayList<WflnstanceItemData> datas);

    /*跳转操作*/
    void openItemEmbl(int groupPosition, int childPosition);

    LoadingLayout getLoading();
}
