package com.loyo.oa.v2.tool;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.SparseArray;
import android.widget.TextView;

import com.loyo.oa.v2.activityui.work.WorkReportAddActivity;
import com.loyo.oa.v2.application.MainApp;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class WeeksDialog {

    /**
     * 显示操作结果的result
     */
    private TextView resultTview;
    private int singleIndex = 0;

    private SparseArray<HashMap<String, Long>> sourseList = new SparseArray();
    private HashMap<String, Long> returnHashMap = new HashMap<String, Long>();
    private Context mContext;
    private String sourseArray[];
    private String sourseToWeek[];
    private String dateR;
    private Date curDate = new Date();
    private Handler mHandler;

    public TextView getResultTview() {
        return resultTview;
    }

    public void setResultTview(TextView resultTview) {
        this.resultTview = resultTview;
    }

    public HashMap<String, Long> getResultList() {
        if (returnHashMap == null || returnHashMap.size() == 0) {
            returnHashMap = sourseList.get(0);
        }

        return returnHashMap;
    }

    /**
     * 填充最近(num-1)个星期的范围
     * */
    private void setDataSource(int num) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(curDate);

        long monday, sunday;

        for (int i = 0; i < num; i++) {
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            monday = cal.getTime().getTime();
            cal.add(Calendar.DATE, 6);
            sunday = cal.getTime().getTime();

            HashMap<String, Long> item = new HashMap<String, Long>();
            item.put("begin", monday);
            item.put("end", sunday);

            cal.setTimeInMillis(monday);
            cal.add(Calendar.DATE, -2);

            sourseList.put(i, item);
        }
    }

    public WeeksDialog(TextView view,Handler handler) {
        this.mContext = view.getContext();
        this.resultTview = view;
        this.mHandler = handler;
        setDataSource(4);

        if (sourseList != null) {

            /*不含本周数据*/
            ArrayList<String> exList = new ArrayList<String>();
            for (int i = 0; i < sourseList.size(); i++) {
                HashMap<String, Long> item = sourseList.get(i);
                long begin = item.get("begin");
                long end = item.get("end");

                /*周报补签，取消本周显示*/
                if (i == 0) {
                    //exList.add(DateTool.getMMDD(begin).concat(" - ").concat(DateTool.getMMDD(end)).concat(" (本周)"));
                } else {
                    exList.add(DateTool.getMMDD(begin).concat(" - ").concat(DateTool.getMMDD(end)));
                }
            }
            sourseArray = exList.toArray(new String[exList.size()]);

            /*包含本周数据*/
            ArrayList<String> exListToWeek = new ArrayList<String>();
            for (int i = 0; i < sourseList.size(); i++) {
                HashMap<String, Long> item = sourseList.get(i);
                long begin = item.get("begin");
                long end = item.get("end");

                /*周报补签，取消本周显示*/
                if (i == 0) {
                    exListToWeek.add(DateTool.getMMDD(begin).concat(" - ").concat(DateTool.getMMDD(end)).concat(" (本周)"));
                } else {
                    exListToWeek.add(DateTool.getMMDD(begin).concat(" - ").concat(DateTool.getMMDD(end)));
                }
            }
            sourseToWeek = exListToWeek.toArray(new String[exList.size()]);
        }
    }

    public Dialog showChoiceDialog(String titleid) {

        return new AlertDialog.Builder(mContext)
                .setTitle(titleid)
                .setSingleChoiceItems(sourseArray, singleIndex, singleListener)
                .setPositiveButton("确定", btnListener).create();
    }

    private DialogInterface.OnClickListener btnListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int which) {// 按下项所在的索引
            String date=sourseArray[singleIndex].replace(" (本周)", "").split("-")[1];
            dateR=sourseArray[singleIndex].replace(" (本周)", "");
            if (DateTool.getDateToTimestamp(date,MainApp.getMainApp().df7)<DateTool.getBeginAt_ofWeek())
            {
                dateR+="(补签)";
            }
            resultTview.setText(dateR);
            mHandler.sendEmptyMessage(WorkReportAddActivity.WEEK_RESULT);
        }
    };

    private DialogInterface.OnClickListener singleListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            singleIndex = which;
            returnHashMap = sourseList.get(which);
        }
    };

    public String GetDefautlText() {
        return sourseToWeek[0].replace(" (本周)", "");
    }

    /**
     * 获取当前星期开始 结束时间
     * */
    public Long[] getNowBeginandEndAt(){
        Calendar calendar = Calendar.getInstance();
        Long[] begAndendAt = new Long[2];
        String biber = sourseToWeek[0].replace("(本周)", "").trim();
        String[] gdragon = biber.split("-");
        String[] begAt = gdragon[0].trim().split("\\.");
        String[] endAt = gdragon[1].trim().split("\\.");

        begAndendAt[0] = DateTool.getSomeWeekBeginAt(calendar.get(calendar.YEAR),(Integer.valueOf(begAt[0])-1),Integer.valueOf(begAt[1]));
        begAndendAt[1] = DateTool.getSomeWeekEndAt(calendar.get(calendar.YEAR),(Integer.valueOf(endAt[0])-1),Integer.valueOf(endAt[1]));
        return begAndendAt;
    }

    /**
     * 获取选中星期的开始 结束时间
     * */
    public Long[] GetBeginandEndAt(){
        Calendar calendar = Calendar.getInstance();
        Long[] begAndendAt = new Long[2];
        String biber = dateR.replace("(补签)","").trim();
        String[] gdragon = biber.split("-");
        String[] begAt = gdragon[0].trim().split("\\.");
        String[] endAt = gdragon[1].trim().split("\\.");

        begAndendAt[0] = DateTool.getSomeWeekBeginAt(calendar.get(calendar.YEAR),(Integer.valueOf(begAt[0])-1),Integer.valueOf(begAt[1]));
        begAndendAt[1] = DateTool.getSomeWeekEndAt(calendar.get(calendar.YEAR),(Integer.valueOf(endAt[0])-1),Integer.valueOf(endAt[1]));
        return begAndendAt;
    }
}
