package com.loyo.oa.common.click;

import android.view.View;

import com.loyo.oa.v2.tool.LogUtil;

import java.util.Calendar;

import hk.ids.gws.android.sclick.SClick;

/**
 * 一定时间内避免响应多次点击操作
 * Created by xeq on 17/1/13.
 */

public abstract class NoDoubleClickListener implements View.OnClickListener {

    public int MIN_CLICK_DELAY_TIME = 2000;
    private long lastClickTime = 0;

    public NoDoubleClickListener() {
    }

    public NoDoubleClickListener(int MIN_CLICK_DELAY_TIME) {
        this.MIN_CLICK_DELAY_TIME = MIN_CLICK_DELAY_TIME;
    }

    @Override
    public void onClick(View v) {
        //两种延时做发都可以
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClick(v);
            LogUtil.d("点击了次数--------------------------");
        }
//        if (SClick.check(SClick.BUTTON_CLICK, MIN_CLICK_DELAY_TIME)) {
//            onNoDoubleClick(v);
//            LogUtil.d("1111111111111111111点击了次数--------------------------");
//        }
    }

    public abstract void onNoDoubleClick(View v);
}
