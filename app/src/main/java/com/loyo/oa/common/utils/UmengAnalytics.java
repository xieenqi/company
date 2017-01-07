package com.loyo.oa.common.utils;

import android.content.Context;

import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.SharedUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * 友盟分析 统计点击事件
 * Created by xeq on 17/1/7.
 */

public class UmengAnalytics {

    public static String businessQuery = "BusinessQuery";//工商查询

    public static void umengSend(Context mContext, String eventId) {
        if (!Config_project.isRelease) {
            return;
        }
        String time = DateTool.getDateTimeReal(System.currentTimeMillis() / 1000);
        LogUtil.d("转换时间：" + time);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UID", SharedUtil.get(MainApp.getMainApp(), ExtraAndResult.UID));//用户id
        map.put("CID", SharedUtil.get(MainApp.getMainApp(), ExtraAndResult.CID));//公司id
        map.put("TIME", time);//点击时间
        MobclickAgent.onEvent(mContext, eventId, map);
    }
}
