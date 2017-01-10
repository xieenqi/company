package com.loyo.oa.v2.activityui.dashboard.model;

import com.loyo.oa.common.utils.BigNumberFormatTool;
import com.loyo.oa.v2.activityui.dashboard.common.DashboardType;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * 仪表盘 列表的模型
 * Created by jie on 16/12/28.
 */

public class StatisticRecord {


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
    public Double orderNum;
    public Double targetNum;
    public String finish_rate;


    public ArrayList<String> getDisplayColumnForType(final DashboardType type) {
        if (DashboardType.CUS_FOLLOWUP == type || DashboardType.SALE_FOLLOWUP == type || DashboardType.CUS_SIGNIN == type) {
            //客户/线索 跟进/客户拜访
            return new ArrayList<String>() {{
                add(userName);
                add(String.valueOf(total));
                add(String.valueOf(totalCustomer));
            }};


        } else if (DashboardType.CUS_CELL_RECORD == type || DashboardType.SALE_CELL_RECORD == type) {
            //客户电话录/线索电话录
            return new ArrayList<String>() {{
                add(userName);
                add(String.valueOf(total));
                add(String.valueOf(totalCustomer));
                add(String.valueOf(totalLength));
            }};

        } else if (DashboardType.COMMON == type) {
            //增量/存量
            return new ArrayList<String>() {{
                add(name);
                add(String.valueOf(addCount));
                add(String.valueOf(count));
            }};

        } else if (DashboardType.ORDER_MONEY == type || DashboardType.ORDER_NUMBER == type) {
            // 订单数量和金额
            return new ArrayList<String>() {{
                add(name);
                add(format(targetNum,type));
                add(format(orderNum,type));
                add(finish_rate);
            }};
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 数据超过千万时，4舍5入的方式处理略写成万为单位的数值（保留2位小数）：
     * 数据超过亿时，4舍5入的方式处理略写成亿为单位的数值（保留2位小数）
     */
    private String format(Double number,DashboardType type) {
        String result=BigNumberFormatTool.format(number);
        if(null!=result&&DashboardType.ORDER_MONEY==type){
            result= "¥"+result;
        }
        return result;
    }

}
