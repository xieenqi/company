package com.loyo.oa.v2.activityui.worksheet.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
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
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by EthanGong on 16/9/1.
 */
public class WorksheetEventCell extends LinearLayout {

    View content;
    private RoundImageView iv_avatar;
    private ImageView iv_status, iv_action;
    private TextView tv_content, tv_name, tv_time, tv_time2;

    Handler handler;

    private WorksheetEventsSupporter data;

    public WorksheetEventCell(Context context, Handler handler) {
        super(context);
        content = LayoutInflater.from(context).inflate(R.layout.item_worksheet_event, null, false);
        iv_avatar = (RoundImageView) content.findViewById(R.id.iv_avatar);
        iv_status = (ImageView) content.findViewById(R.id.iv_status);
        iv_action = (ImageView) content.findViewById(R.id.iv_action);
        tv_content = (TextView) content.findViewById(R.id.tv_content);
        tv_name = (TextView) content.findViewById(R.id.tv_name);
        tv_time = (TextView) content.findViewById(R.id.tv_time);
        tv_time2 = (TextView) content.findViewById(R.id.tv_time2);

        this.handler = handler;

        this.addView(content);
    }

    public void loadData(final WorksheetEventsSupporter data,
                         final WSRole role,
                         final ArrayList<WorksheetEventAction> actions,
                         final WorksheetStatus worksheetStatus) {
        this.data = data;


        /* 多个操作，
        *
        * */
        WorksheetEventAction action = null;
        if (actions.size() == 0) {
            action = WorksheetEventAction.None;
        } else {
            action = actions.get(0);
            for (int i = 0; i < actions.size(); i++) {
                if (actions.get(i) == WorksheetEventAction.Finish) {
                    action = actions.get(i);
                    break;
                }
            }
        }

        /* 事件内容 */
        tv_content.setText(data.content);
        /* 负责人姓名 */
        tv_name.setText(null == data.responsor ? "未设置" : data.responsor.getName());

        if (null != data.responsor) {
            ImageLoader.getInstance().loadImage(data.responsor.getAvatar(), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    if (null != bitmap) {
                        if (data.status == WorksheetEventStatus.UNACTIVATED) {
                            iv_avatar.setImageBitmap(Utils.toGrayscale(bitmap));
                        } else {
                            iv_avatar.setImageBitmap(bitmap);
                        }
                    }
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });


        }

        tv_time.setText(data.startTime == 0 ? "--" : com.loyo.oa.common.utils.DateTool.getFriendlyTime(data.startTime,true));
        LogUtil.dee("endTime:" + data.endTime);
        LogUtil.dee("status:" + data.status);
        if (data.endTime != 0 && data.status != WorksheetEventStatus.UNACTIVATED) {
            //事件 已处理 待处理 标红
            if (data.isOvertime) {
                tv_time2.setText(com.loyo.oa.common.utils.DateTool.getFriendlyTime(data.endTime,true) + "截止" + "(超时)");
                tv_time2.setTextColor(getResources().getColor(R.color.red1));
            } else {
                tv_time2.setText(com.loyo.oa.common.utils.DateTool.getFriendlyTime(data.endTime,true) + "截止");
                tv_time2.setTextColor(getResources().getColor(R.color.text99));
            }
        } else if (data.endTime == 0 || data.status == WorksheetEventStatus.UNACTIVATED) {
            tv_time2.setText("--");
            tv_time2.setTextColor(getResources().getColor(R.color.text99));
        }

        /* 状态按钮 */
        iv_status.setImageResource(data.status.getStatusIcon(iv_avatar));

        /* 操作按钮 */
        iv_action.setVisibility(action.visible() ? View.VISIBLE : View.INVISIBLE);
        iv_action.setImageResource(action.getIcon());

        /* 事件 */
        iv_action.setOnTouchListener(Global.GetTouch());

        final WorksheetEventAction theAction = action;
        iv_action.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = new Message();
                msg.obj = data.id;
                switch (theAction) {
                    case Transfer:
                        msg.what = ExtraAndResult.WORKSHEET_EVENT_TRANSFER;
                        break;
                    case Dispatch:
                        msg.what = ExtraAndResult.WORKSHEET_EVENT_DISPATCH;
                        break;
                    case Redo:
                        msg.what = ExtraAndResult.WORKSHEET_EVENT_REDO;
                        break;
                    case Finish:
                        msg.what = ExtraAndResult.WORKSHEET_EVENT_FINISH;
                        break;
                }
                handler.sendMessage(msg);
            }
        });

        this.setOnTouchListener(Global.GetTouch());
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = new Message();
                msg.obj = data;
                //msg.arg1 = action;
                msg.what = ExtraAndResult.WORKSHEET_EVENT_DETAIL;
                handler.sendMessage(msg);
            }
        });
    }
}
