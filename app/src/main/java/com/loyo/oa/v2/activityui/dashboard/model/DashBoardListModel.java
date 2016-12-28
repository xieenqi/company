package com.loyo.oa.v2.activityui.dashboard.model;

import com.loyo.oa.v2.beans.BaseBeanT;

/**
 * 仪表盘 列表的模型
 * Created by jie on 16/12/28.
 */

public class DashBoardListModel extends BaseBeanT<DashBoardListModel.Data>{
    class Data{
        public Integer pageIndex;
        public Integer pageSize;
        public Integer totalRecords;
        public Record[] records;

    }
    class Record{
        public Integer total;
        public Integer totalCustomer;
        public String userName;

    }
}
