package com.loyo.oa.common.utils;

import com.loyo.oa.v2.activityui.dashboard.common.DashboardType;

import java.text.DecimalFormat;

/**
 * 对于大数据的处理，大于千万显示：XX万，大于亿，显示XX亿。
 * Created by jie on 17/1/10.
 */

public class BigNumberFormatTool {
    private final static long TEN_MILLION     = 10000000L;  // 千万
    private final static long HUNDRED_MILLION = 100000000L; // 亿
    private final static long TEN_THOUSAND    = 10000L;     // 万
    /**
     * 数据超过千万时，4舍5入的方式处理略写成万为单位的数值（保留2位小数）：
     * 数据超过亿时，4舍5入的方式处理略写成亿为单位的数值（保留2位小数）
     */
    public static String format(Double number) {
        if(null==number){
            return "--";
        }
        String result;
        DecimalFormat df=new DecimalFormat("#.##");
        if(number>HUNDRED_MILLION){
            result=df.format(number/HUNDRED_MILLION)+"亿";
        }else if(number>TEN_MILLION){
            result=df.format(number/TEN_THOUSAND)+"万";
        }else{
            result=df.format(number);
        }
        return result;
    }
}
