package com.loyo.oa.v2.activityui.worksheet.common;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetEventsSupporter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.RoundImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 工单事件流程
 * Created by xeq on 16/8/30.
 */

public class WorksheetEventLayout extends LinearLayout {

    private RoundImageView iv_avatar;
    private ImageView iv_status, iv_action;
    private TextView tv_content, tv_name, tv_time;
    private boolean isAssignment, isresponsor;//是分派人 ，是负责人

    public WorksheetEventLayout(Context context, Handler handler, WorksheetEventsSupporter data, String dispatcherId) {
        super(context);
        bindView(context, handler, data, dispatcherId);
    }

    public WorksheetEventLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WorksheetEventLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void bindView(Context context, final Handler handler, final WorksheetEventsSupporter data, String dispatcherId) {
        View eventView = LayoutInflater.from(context).inflate(R.layout.item_worksheet_event, null, false);
        iv_avatar = (RoundImageView) eventView.findViewById(R.id.iv_avatar);
        iv_status = (ImageView) eventView.findViewById(R.id.iv_status);
        iv_action = (ImageView) eventView.findViewById(R.id.iv_action);
        tv_content = (TextView) eventView.findViewById(R.id.tv_content);
        tv_name = (TextView) eventView.findViewById(R.id.tv_name);
        tv_time = (TextView) eventView.findViewById(R.id.tv_time);
        tv_content.setText(data.content);
        tv_name.setText(data.responsor.getName());
        if (MainApp.user.id.equals(dispatcherId))
            isAssignment = true;
        if (MainApp.user.id.equals(data.responsorId))
            isresponsor = true;
        //处理事件状态
        switch (data.status) {
            case 1://待处理
                iv_status.setImageResource(R.drawable.icon_worcksheet_status2);
                break;
            case 2://未触发
                iv_status.setImageResource(R.drawable.icon_worcksheet_status1);
                break;
            case 3://已完成
                iv_status.setImageResource(R.drawable.icon_worcksheet_status3);
                break;
        }


        ImageLoader.getInstance().displayImage(data.responsor.getAvatar(), iv_avatar);
        eventView.setOnTouchListener(Global.GetTouch());
        eventView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = new Message();
                msg.what = ExtraAndResult.REQUEST_CODE_CUSTOMER;
                handler.sendMessage(msg);
            }
        });
        iv_action.setOnTouchListener(Global.GetTouch());
        iv_action.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = new Message();
                msg.obj = data.id;
                msg.what = ExtraAndResult.REQUEST_CODE_STAGE;
                handler.sendMessage(msg);
            }
        });
        this.addView(eventView);
    }
}
