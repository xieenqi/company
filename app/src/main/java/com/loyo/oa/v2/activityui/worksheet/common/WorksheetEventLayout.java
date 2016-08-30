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

    public WorksheetEventLayout(Context context, Handler handler, WorksheetEventsSupporter data) {
        super(context);
        bindView(context, handler, data);
    }

    public WorksheetEventLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WorksheetEventLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void bindView(Context context, final Handler handler, WorksheetEventsSupporter data) {
        View eventView = LayoutInflater.from(context).inflate(R.layout.item_worksheet_event, null, false);
        iv_avatar = (RoundImageView) eventView.findViewById(R.id.iv_avatar);
        iv_status = (ImageView) eventView.findViewById(R.id.iv_status);
        iv_action = (ImageView) eventView.findViewById(R.id.iv_action);
        tv_content = (TextView) eventView.findViewById(R.id.tv_content);
        tv_name = (TextView) eventView.findViewById(R.id.tv_name);
        tv_time = (TextView) eventView.findViewById(R.id.tv_time);
        tv_content.setText(data.content);
        tv_name.setText(data.responsor.getName());
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
                msg.what = ExtraAndResult.REQUEST_CODE_STAGE;
                handler.sendMessage(msg);
            }
        });
        this.addView(eventView);
    }
}
