package com.loyo.oa.v2.activityui.dashboard.model;

import com.loyo.oa.v2.activityui.dashboard.common.DashboardType;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.Utils;

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
    public String orderNum;
    public String targetNum;
    public String finish_rate;


    public ArrayList<String> getDsiplayColumnForType(final DashboardType type) {
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
                add(DashboardType.ORDER_MONEY == type ? "￥" + fomart(targetNum) : targetNum);
                add(DashboardType.ORDER_MONEY == type ? "￥" + fomart(orderNum) : orderNum);
                add(finish_rate);
            }};
        } else {
            return new ArrayList<>();
        }
    }

    private String fomart(String numbre) {
        double ll = Double.valueOf(numbre);
        Double ll2;
        if (ll > 1000 * 10000) {
            ll2 = ll / 10000;
            return Utils.setValueDouble(ll2) + "万";
        }
        return numbre;
    }

}
