package com.loyo.oa.v2.activityui.worksheet.common;

import android.widget.TextView;

import com.loyo.oa.v2.R;

/**
 * 工单 模块 公用方法
 * Created by xeq on 16/8/30.
 */
public class WorksheetCommon {

    public static void setStatus(TextView tv, int status) {
        String info = "";
        int bj = R.drawable.common_lable_gray;
        switch (status) {
            case 1:
                info = "待分派";
                bj = R.drawable.common_lable_red;
                break;
            case 2:
                info = "进行中";
                bj = R.drawable.common_lable_purple;
                break;
            case 3:
                info = "待审核";
                bj = R.drawable.common_lable_blue;
                break;
            case 4:
                info = "已完成";
                bj = R.drawable.common_lable_green;
                break;
            case 5:
                info = "意外终止";
                bj = R.drawable.common_lable_gray;
                break;
        }
        tv.setText(info);
        tv.setBackgroundResource(bj);
    }



}
