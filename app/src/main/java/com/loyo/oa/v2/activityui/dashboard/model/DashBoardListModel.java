package com.loyo.oa.v2.activityui.dashboard.model;

import com.loyo.oa.v2.beans.BaseBeanT;

import java.util.List;

/**
 * 仪表盘 列表的模型
 * Created by jie on 16/12/28.
 */

public class DashBoardListModel extends BaseBeanT<DashBoardListModel.Data>{
    public class Data{
        public Integer pageIndex;
        public Integer pageSize;
        public Integer totalRecords;
        //客户／线索 跟进
        public List<Record> records;


    }
    public class Record {
        //公共字段
        public Integer total;
        public Integer totalCustomer;
        public String userName;
        //电话录音
        public String totalLength;



        public String id;
        public String name;
        //增量／存量
        public Integer count;
        public Integer addCount;

        //订单数量和金额
        public String orderNum;
        public String targetNum;
        public String finish_rate;


    }
}
