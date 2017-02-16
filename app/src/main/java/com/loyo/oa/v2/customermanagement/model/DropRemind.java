package com.loyo.oa.v2.customermanagement.model;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

/**
 * Created by EthanGong on 2017/2/16.
 */

public class DropRemind {

    public boolean activityRemind; //跟进行为丢公海提醒飘红
    public boolean orderRemind;   //订单丢公海提醒飘红
    public String activityRecycleRemind; // 拜访丢公海提醒文字
    public String orderRecycleRemind; // 订单丢公海提醒文字

    public SpannableStringBuilder getFormattedDropRemind() {
        if (!hasDropRemind()) {
            SpannableStringBuilder builder = new SpannableStringBuilder("");
            return builder;
        }

        StringBuilder sb = new StringBuilder();
        boolean hasActivityRemind = false;
        boolean hasOrderRemind = false;
        if (!TextUtils.isEmpty(activityRecycleRemind)) {
            sb.append(activityRecycleRemind);
            hasActivityRemind = true;
        }
        if (!TextUtils.isEmpty(orderRecycleRemind)) {
            if (hasActivityRemind) {
                sb.append(" 或 ");
            }
            sb.append(orderRecycleRemind);
            hasOrderRemind = true;
        }
        if (hasActivityRemind || hasOrderRemind) {
            sb.append("丢公海");
        }
        String compoundRemind = sb.toString();

        SpannableStringBuilder builder = new SpannableStringBuilder(compoundRemind);
        try {
            if (hasActivityRemind && activityRemind) {
                ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.parseColor("#f5625a"));
                builder.setSpan(redSpan, 0, activityRecycleRemind.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } catch (Exception e) {
        }
        try {
            if (hasOrderRemind && orderRemind) {
                ForegroundColorSpan redSpan2 = new ForegroundColorSpan(Color.parseColor("#f5625a"));
                builder.setSpan(redSpan2, compoundRemind.length() - orderRecycleRemind.length() - 3,
                        compoundRemind.length() - 3,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } catch (Exception e) {
        }
        return builder;
    }

    public boolean hasDropRemind() {
        return !TextUtils.isEmpty(activityRecycleRemind) || !TextUtils.isEmpty(orderRecycleRemind) ;
    }
}
