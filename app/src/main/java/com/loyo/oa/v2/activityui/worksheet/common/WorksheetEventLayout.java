package com.loyo.oa.v2.activityui.worksheet.common;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.customview.RoundImageView;

/**
 * 工单事件流程
 * Created by xeq on 16/8/30.
 */

public class WorkSheetEventLayout extends LinearLayout {

    private RoundImageView iv_avatar;
    private ImageView iv_status, iv_action;
    private TextView tv_content, tv_name, tv_time;

    public WorkSheetEventLayout(Context context, Handler handler) {
        super(context);
        bindView(context);
    }

    public WorkSheetEventLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WorkSheetEventLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void bindView(Context context) {
        View eventView = LayoutInflater.from(context).inflate(R.layout.item_worksheet_event, null, false);
        iv_avatar = (RoundImageView) eventView.findViewById(R.id.iv_avatar);
        iv_status = (RoundImageView) eventView.findViewById(R.id.iv_status);
        iv_action = (RoundImageView) eventView.findViewById(R.id.iv_action);
        tv_content = (TextView) eventView.findViewById(R.id.tv_content);
        tv_name = (TextView) eventView.findViewById(R.id.tv_name);
        tv_time = (TextView) eventView.findViewById(R.id.tv_time);
        this.addView(eventView);
    }
}
