package com.loyo.oa.v2.beans;

import com.loyo.oa.common.utils.DateFormatSet;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.tool.DateTool;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


public class PagingGroupData<T extends BaseBeans> implements Serializable {
    private String time;
    private ArrayList<T> records;

    public PagingGroupData() {
        records = new ArrayList<T>();
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ArrayList<T> getRecords() {
        return records;
    }

    public void setRecords(ArrayList<T> records) {
        this.records = records;
    }

    public static <T extends BaseBeans> ArrayList<PagingGroupData<T>> convertGroupData(ArrayList<T> records) {

        if (records == null || records.size() == 0) {
            return new ArrayList<>();
        }

        final MainApp app = MainApp.getMainApp();

        //1.先排序
        Comparator<T> comparator = new Comparator<T>() {
            @Override
            public int compare(T lhs, T rhs) {

//                long l = DateTool.getDateToTimestamp(lhs.getOrderStr(), app.df_api_get);
//                long r = DateTool.getDateToTimestamp(rhs.getOrderStr(), app.df_api_get);
                long l= com.loyo.oa.common.utils.DateTool.getSecondStamp(lhs.getOrderStr());
                long r= com.loyo.oa.common.utils.DateTool.getSecondStamp(rhs.getOrderStr());

                return (r - l) > 0 ? 1 : 0;
            }
        };
        Collections.sort(records, comparator);
        ArrayList<PagingGroupData<T>> groupData = new ArrayList<>();

        Date todayDate = new Date();
        Date yesterDate = new Date();

        Calendar today = Calendar.getInstance();    //今天
        today.setTime(todayDate);
        todayDate = today.getTime();

        Calendar yesterday = Calendar.getInstance();
        yesterday.setTime(yesterDate);
        yesterday.add(yesterday.DATE, -1);
        yesterDate = yesterday.getTime();

        String strToday = app.df7.format(todayDate);
        String strYesterday = app.df7.format(yesterDate);

        for (T item : records) {

            String gTime = null;
            if (item.getOrderStr() == null) {
                Calendar c = Calendar.getInstance();
                gTime = app.df7.format(c.getTime());
            } else {
//                gTime = DateTool.getDate(item.getOrderStr(), app.df_api_get, app.df7);
                gTime = com.loyo.oa.common.utils.DateTool.convertDate(item.getOrderStr(), DateFormatSet.daySdf);

                if (gTime.equals(strToday)) {
                    gTime = "今天";
                } else if (gTime.equals(strYesterday)) {
                    gTime = "昨天";
                }
            }

            boolean isExist = false;

            for (PagingGroupData group : groupData) {
                if (group.getTime().equals(gTime)) {
                    isExist = true;

                    group.getRecords().add(item);
                }
            }

            if (!isExist) {
                PagingGroupData data = new PagingGroupData();
                data.setTime(gTime);
                data.getRecords().add(item);
                groupData.add(data);
            }
        }

        return groupData;
    }
}
