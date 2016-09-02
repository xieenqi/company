package com.loyo.oa.v2.activityui.worksheet.common;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.worksheet.WorksheetDetailActivity;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetEventsSupporter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.RoundImageView;
import com.loyo.oa.v2.tool.DateTool;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 工单事件流程
 * Created by xeq on 16/8/30.
 */

public class WorksheetEventLayout extends LinearLayout {

    public static int ACTION_PERSON = 1;
    public static int ACTION_REDO = 2;
    public static int ACTION_COMPILE = 3;

    private RoundImageView iv_avatar;
    private ImageView iv_status, iv_action;
    private TextView tv_content, tv_name, tv_time;
    private boolean isAssignment, isresponsor, isCreated;//是分派人 ，是负责人,是创建者
    private int action; //1 事件选取负责人  2事件重做  3事件提交完成

    public WorksheetEventLayout(Context context, Handler handler, WorksheetEventsSupporter data,
                                boolean isAssignment, boolean isCreated, int worksheetStatus) {
        super(context);
        bindView(context, handler, data, isAssignment, isCreated, worksheetStatus);
    }

    public WorksheetEventLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WorksheetEventLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void bindView(Context context, final Handler handler, final WorksheetEventsSupporter data,
                          boolean isAssignment, boolean isCreated, int worksheetStatus) {
        View eventView = LayoutInflater.from(context).inflate(R.layout.item_worksheet_event, null, false);
        iv_avatar = (RoundImageView) eventView.findViewById(R.id.iv_avatar);
        iv_status = (ImageView) eventView.findViewById(R.id.iv_status);
        iv_action = (ImageView) eventView.findViewById(R.id.iv_action);
        tv_content = (TextView) eventView.findViewById(R.id.tv_content);
        tv_name = (TextView) eventView.findViewById(R.id.tv_name);
        tv_time = (TextView) eventView.findViewById(R.id.tv_time);
        tv_content.setText(data.content);
        tv_name.setText(null == data.responsor ? "未设置" : data.responsor.getName());
        tv_time.setText(data.startTime == 0 ? "--" : DateTool.getDiffTime(data.startTime) + " | " +
                (data.endTime == 0 ? "--" : DateTool.getDiffTime(data.endTime) + "截止"));
        if (MainApp.user.id.equals(data.responsorId)) {
            isresponsor = true;
        }
        if (isCreated)//创建者没有操作权限最小权限
            iv_action.setVisibility(INVISIBLE);
        if (isAssignment)
            iv_action.setVisibility(VISIBLE);
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
        //处理工单的状态
        switch (worksheetStatus) {//1.待分派  2.进行中 3.待审核 4.已完成 5.意外终止
            case 1://待分派
                if (isAssignment && !(TextUtils.isEmpty(data.responsorId))) {//有负责人
                    iv_action.setImageResource(R.drawable.icon_worksheet_setting);
                    ((WorksheetDetailActivity) context).setSetting();
                    action = ACTION_PERSON;
                } else {//没有负责人
                    iv_action.setImageResource(R.drawable.icon_worksheet_assignment);
                    action = ACTION_PERSON;
                    ((WorksheetDetailActivity) context).setSetting();
                }
                break;
            case 2://进行中
                if (isAssignment && data.status == 1) {
                    iv_action.setImageResource(R.drawable.icon_worksheet_setting);
                    action = ACTION_PERSON;
                } else if (isAssignment && data.status == 2) {
                    iv_action.setImageResource(R.drawable.icon_worksheet_setting);
                    action = ACTION_PERSON;
                } else if (isAssignment && data.status == 3) {
                    iv_action.setImageResource(R.drawable.icon_worksheet_redo);
                    action = ACTION_REDO;
                } else if (isresponsor && data.status == 1) {
                    iv_action.setImageResource(R.drawable.icon_worksheet_compile);
                    action = ACTION_COMPILE;
                }
                break;
            case 3://待审核
                if (isAssignment && data.status == 3) {
                    iv_action.setImageResource(R.drawable.icon_worksheet_redo);
                    action = ACTION_REDO;
                }
                break;
            case 4://已完成
                break;
            case 5://意外终止
                break;
        }

        if (null != data.responsor) {
            ImageLoader.getInstance().displayImage(data.responsor.getAvatar(), iv_avatar);
        }
        eventView.setOnTouchListener(Global.GetTouch());
        eventView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = new Message();
                msg.obj = data.id;
                msg.arg1 = action;
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
                switch (action) {
                    case 1:
                        msg.what = ExtraAndResult.REQUEST_CODE_STAGE;
                        break;
                    case 2:
                        msg.what = ExtraAndResult.REQUEST_CODE_PRODUCT;
                        break;
                    case 3:
                        msg.what = ExtraAndResult.REQUEST_CODE_TYPE;
                        break;
                }
                handler.sendMessage(msg);
            }
        });
        this.addView(eventView);
    }
}
