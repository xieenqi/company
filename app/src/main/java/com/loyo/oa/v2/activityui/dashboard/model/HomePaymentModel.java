package com.loyo.oa.v2.activityui.dashboard.model;

/**
 * 仪表盘 首页 回款柱状图 数据模型
 * Created by jie on 17/1/10.
 */

public class HomePaymentModel {
    public String lable;
    public Double notBackMoney;//应收款总额
    public Double planMoney;//计划回款
    public Double backMoney;//回款金额
    public Double backMoneyTarget; //回款目标
}
