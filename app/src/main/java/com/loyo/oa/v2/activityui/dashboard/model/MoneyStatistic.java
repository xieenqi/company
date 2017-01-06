package com.loyo.oa.v2.activityui.dashboard.model;

import java.text.DecimalFormat;

/**
 * Created by yyy on 16/12/29.
 */

public class MoneyStatistic {

    private static DecimalFormat numberFormatter = new DecimalFormat("0.00");
    private final static long TEN_MILLION     = 10000000l;  // 亿
    private final static long HUNDRED_MILLION = 100000000l; // 千万
    private final static long TEN_THOUSAND    = 10000l;     // 万

    public long totalAmount;
    public long totalNumber;
    public long targetAmount;
    public long targetNumber;

    private String getFormatString(long number) {

        if (number > HUNDRED_MILLION) {
            double num = number*1.0 /HUNDRED_MILLION;
            return String.format("%.2f", num) + "亿";
        }
        else if (number > TEN_MILLION) {
            double num = number*1.0 /TEN_THOUSAND;
            return String.format("%.2f", num) + "万";
        }
        else {
            return String.valueOf(number);
        }
    }

    public String getTotalAmount() {
        return "¥ " + getFormatString(totalAmount);
    }

    public String getTotalNumber() {
        return getFormatString(totalNumber);
    }

    public String getTargetAmount() {
        return "¥ " + getFormatString(targetAmount);
    }

    public String getTargetNumber() {
        return getFormatString(targetNumber);
    }

    public int getNumberPercent(){
        int mvNumValues = 0; /* 数量涨幅值 */
        if(targetAmount != 0){
            double percent = ((double)totalAmount/targetAmount);
            if (percent > 1) {
                mvNumValues = 100;
            }else{
                mvNumValues =  (int) Math.floor(percent*100); //取整
            }
        }
        return mvNumValues;
    }

    public int getMoneyPercent(){
        long targetMoney = targetNumber;  /*  目标金额  */
        long totalMoney  = totalNumber;   /*  实际金额  */

        int mvMonValues = 0; /* 金额涨幅值 */
        if(targetMoney != 0){
            double percent = ((double)totalMoney/targetMoney);
            if (percent >1) {
                mvMonValues = 100;
            }else{
                mvMonValues =  (int) Math.floor(percent*100); //取整
            }
        }
        return mvMonValues;
    }

    public String getNumberDisplayTitle() {
        String mvNumShow = "0%"; /* 数量涨幅百分比 */
        if(targetAmount != 0){
            double percent = ((double)totalAmount/targetAmount);
            mvNumShow = numberFormatter.format(percent*100)+"%";
        }
        return mvNumShow;
    }

    public String getMoneyDisplayTitle() {
        long targetMoney = targetNumber;  /*  目标金额  */
        long totalMoney  = totalNumber;   /*  实际金额  */

        String mvMonShow = "0%"; /* 金额涨幅百分比 */
        if(targetMoney != 0){
            double percent = ((double)totalMoney/targetMoney);
            mvMonShow = numberFormatter.format(percent * 100)+"%";
        }
        return mvMonShow;
    }

    public void test() {
        totalAmount = 12345678;
        totalNumber = 123456789;
        targetAmount = 12345678;
        targetNumber = 123456789;
    }
}
