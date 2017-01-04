package com.loyo.oa.v2.activityui.dashboard.model;

import java.text.DecimalFormat;

/**
 * Created by yyy on 16/12/29.
 */

public class MoneyStatistic {

    private static DecimalFormat numberFormatter = new DecimalFormat("0.00");

    public long totalAmount;
    public long totalNumber;
    public long targetAmount;
    public long targetNumber;

    public String getTotalAmount() {
        return "¥ " + String.valueOf(totalAmount);
    }

    public String getTotalNumber() {
        return String.valueOf(totalNumber);
    }

    public String getTargetAmount() {
        return "¥ " + String.valueOf(targetAmount);
    }

    public String getTargetNumber() {
        return String.valueOf(targetNumber);
    }

    public int getNumberPercent(){
        int mvNumValues = 100; /* 数量涨幅值 */
        if(targetAmount != 0 && targetAmount > totalAmount){
            mvNumValues =  (int) Math.floor((double)totalAmount/targetAmount); //取整
        }
        return mvNumValues;
    }

    public String getNumberDisplayTitle() {
        String mvNumShow = "100%"; /* 数量涨幅百分比 */
        if(targetAmount != 0 && targetAmount > totalAmount){
            mvNumShow = numberFormatter.format(((double)totalAmount/targetAmount * 100))+"%";
        }
        return mvNumShow;
    }

    public int getMoneyPercent(){
        long targetMoney = targetNumber;  /*  目标金额  */
        long totalMoney  = totalNumber;   /*  实际金额  */

        int mvMonValues = 100; /* 金额涨幅值 */
        if(targetMoney != 0 && targetMoney > totalMoney){
            mvMonValues =  (int) Math.floor((double)totalMoney/targetMoney); //取整
        }
        return mvMonValues;
    }

    public String getMoneyDisplayTitle() {
        long targetMoney = targetNumber;  /*  目标金额  */
        long totalMoney  = totalNumber;   /*  实际金额  */

        String mvMonShow = "100%"; /* 金额涨幅百分比 */
        if(targetMoney != 0 && targetMoney > totalMoney){
            mvMonShow = numberFormatter.format(((double)totalMoney/targetMoney * 100))+"%";
        }
        return mvMonShow;
    }
}
