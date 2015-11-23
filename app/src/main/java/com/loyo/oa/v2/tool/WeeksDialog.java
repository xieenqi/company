package com.loyo.oa.v2.tool;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.SparseArray;
import android.widget.TextView;

import com.loyo.oa.v2.application.MainApp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class WeeksDialog {
    /**
     * 显示操作结果的result
     */
    TextView resultTview;

    int singleIndex = 0;

    SparseArray<HashMap<String, Long>> sourseList = new SparseArray();

    HashMap<String, Long> returnHashMap = new HashMap<String, Long>();

    Context mContext;
    String sourseArray[];

    Date curDate = new Date();

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

    private void setDataSource() {
        //填充最近五个星期的范围
        Calendar cal = Calendar.getInstance();
        cal.setTime(curDate);

        long monday, sunday;

        for (int i = 0; i < 5; i++) {
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

    public WeeksDialog(TextView view) {
        this.mContext = view.getContext();
        this.resultTview = view;
        setDataSource();

        if (sourseList != null) {
            ArrayList<String> exList = new ArrayList<String>();
            for (int i = 0; i < sourseList.size(); i++) {
                HashMap<String, Long> item = sourseList.get(i);
                long begin = item.get("begin");
                long end = item.get("end");

                if (i == 0) {
                    exList.add(DateTool.getMMDD(begin).concat(" - ").concat(DateTool.getMMDD(end)).concat(" (本周)"));
                } else {
                    exList.add(DateTool.getMMDD(begin).concat(" - ").concat(DateTool.getMMDD(end)));
                }
            }
            sourseArray = exList.toArray(new String[exList.size()]);
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
            String dateR=sourseArray[singleIndex].replace(" (本周)", "");
            if (DateTool.getDateToTimestamp(date,MainApp.getMainApp().df7)<DateTool.getBeginAt_ofWeek())
            {
                dateR+="(补签)";
            }
            resultTview.setText(dateR);
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
        return sourseArray[singleIndex].replace(" (本周)", "");
    }
}
