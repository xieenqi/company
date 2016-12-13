package com.loyo.oa.common.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 用来格式化时间的格式化工具集合
 * Created by jie on 16/12/12.
 */
public class DateFormatSet {
    //精确到s的时间格式
    public static SimpleDateFormat secondSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    //精确到分钟的时间格式
    public static SimpleDateFormat minuteSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    //精确到天的时间格式
    public static SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
    //特殊的时间格式->df7
    public static SimpleDateFormat daySdf = new SimpleDateFormat("MM.dd");
    //服务器返回的时间格式
    public static SimpleDateFormat serverSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    //大概是是api的时间格式。原来的名字叫df_api_get
    public static SimpleDateFormat serverApiSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    //特殊格式-->df3
    public static SimpleDateFormat specialMinuteSdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");

    //截取时间中的一部分的格式
    public static SimpleDateFormat dateYearFormat = new SimpleDateFormat("yyyy");
    public static SimpleDateFormat dateMonthFormat = new SimpleDateFormat("MM");
    public static SimpleDateFormat dateDayFormat = new SimpleDateFormat("dd");//格式化day of month
    //只显示小时和分钟 ->df6
    public static SimpleDateFormat dateHourMinute = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat dateNoYear = new SimpleDateFormat("MM-dd HH:mm");



}
