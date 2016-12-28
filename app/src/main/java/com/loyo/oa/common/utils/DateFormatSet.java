package com.loyo.oa.common.utils;

import java.text.SimpleDateFormat;

/**
 * 用来格式化时间的格式化工具集合
 * [注意] 关系比较复杂,不可以随便修改,如果要修改时间格式,请到DateTool查看具体使用的什么函数以后,再修改
 * Created by jie on 16/12/12.
 */
public class DateFormatSet {
    public static SimpleDateFormat secondCommonSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //精确到s的时间格式
    public static SimpleDateFormat secondSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    //精确到分钟的时间格式
    public static SimpleDateFormat minuteSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    //精确到天的时间格式
    public static SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
    //特殊的时间格式 ,比较特殊,用于显示一个时间段 eg 11.12-12.10
    public static SimpleDateFormat daySdf = new SimpleDateFormat("MM.dd");
    //服务器返回的时间格式
    public static SimpleDateFormat serverSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    //大概是是api的时间格式。原来的名字叫df_api_get
    public static SimpleDateFormat serverApiSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    //特殊格式-->df3
    public static SimpleDateFormat specialMinuteSdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");

    /******截取时间中的一部分的格式*/
    public static SimpleDateFormat dateYearFormat = new SimpleDateFormat("yyyy");
    public static SimpleDateFormat dateMonthFormat = new SimpleDateFormat("MM");
    public static SimpleDateFormat dateDayFormat = new SimpleDateFormat("dd");//格式化day of month
    //只显示小时和分钟
    public static SimpleDateFormat dateHourMinute = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat dateNoYear = new SimpleDateFormat("MM-dd HH:mm");
    public static SimpleDateFormat dateMonthDay = new SimpleDateFormat("MM-dd");
    public static SimpleDateFormat dateYearMonth = new SimpleDateFormat("yyyy-MM");

    /****其他***/
    //用于文件的重命名
    public static SimpleDateFormat fileNameSdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    //用于获取年月日的没有间隔的格式 eg:19941020
    public static SimpleDateFormat dateNumSdf = new SimpleDateFormat("yyyyMMdd");

    //在轨迹缓存数据库中存储的格式
    public static SimpleDateFormat LDBSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+08:00");


}
