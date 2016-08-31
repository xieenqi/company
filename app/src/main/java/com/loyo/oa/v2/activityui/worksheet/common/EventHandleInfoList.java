package com.loyo.oa.v2.activityui.worksheet.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.customview.RoundImageView;

/**
 * 事件 详情  处理信息列表
 * Created by xeq on 16/8/31.
 */
public class EventHandleInfoList extends LinearLayout {
    private RoundImageView iv_avatar;

    public EventHandleInfoList(Context context) {
        super(context);
        bindView(context);
    }

    public EventHandleInfoList(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EventHandleInfoList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private void bindView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event_handler_info_list, null, false);
        iv_avatar = (RoundImageView) view.findViewById(R.id.iv_avatar);
        this.addView(view);
    }
}
