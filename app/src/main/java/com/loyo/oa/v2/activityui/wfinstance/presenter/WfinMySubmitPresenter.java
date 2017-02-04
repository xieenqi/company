package com.loyo.oa.v2.activityui.wfinstance.presenter;


import android.widget.Button;
import android.widget.ExpandableListView;

import com.loyo.oa.v2.activityui.wfinstance.bean.WflnstanceListItem;
import com.loyo.oa.v2.beans.PaginationX;

/**
 * Created by yyy on 16/10/17.
 */

public interface WfinMySubmitPresenter {

//    /*获取审批类型数据*/
//    void getWfBizForms();

    /*获取审批列表数据*/
    void getApproveWfInstancesList(PaginationX<WflnstanceListItem> paginationX);

    /*初始化顶部菜单*/
//    void initDropMenu(String[] FILTER_STATUS);
    void loadFilterOptions();

    /*ListView监听与初始化*/
    void initListView(ExpandableListView mListView, Button btn_add);
}
