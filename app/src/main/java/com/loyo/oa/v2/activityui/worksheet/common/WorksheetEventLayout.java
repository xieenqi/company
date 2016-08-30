package com.loyo.oa.v2.activityui.worksheet.common;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.loyo.oa.v2.R;

/**
 * 工单事件流程
 * Created by xeq on 16/8/30.
 */
public class WorksheetEventLayout extends LinearLayout {
    public WorksheetEventLayout(Context context, Handler handler) {
        super(context);
        bindView(context);
    }

    public WorksheetEventLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WorksheetEventLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void bindView(Context context) {
        View eventView = LayoutInflater.from(context).inflate(R.layout.item_worksheet_event, null, false);

        this.addView(eventView);
    }
}
