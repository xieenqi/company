package com.loyo.oa.v2.activityui.worksheet.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.worksheet.adapter.WorkSheetListNestingAdapter;
import com.loyo.oa.v2.activityui.worksheet.bean.EventHandleInfoItem;
import com.loyo.oa.v2.customview.CustomerListView;
import com.loyo.oa.v2.customview.RoundImageView;

/**
 * 事件 详情  处理信息列表
 * Created by xeq on 16/8/31.
 */
public class EventHandleInfoList extends LinearLayout {
    private RoundImageView iv_avatar;
    private TextView tv_time, tv_content;
    private CustomerListView lv_listview;

    public EventHandleInfoList(Context context, EventHandleInfoItem data) {
        super(context);
        bindView(context, data);
    }

    public EventHandleInfoList(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EventHandleInfoList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private void bindView(Context context, EventHandleInfoItem data) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event_handler_info_list, null, false);
        iv_avatar = (RoundImageView) view.findViewById(R.id.iv_avatar);
        tv_time = (TextView) view.findViewById(R.id.tv_time);
        tv_content = (TextView) view.findViewById(R.id.tv_content);
        lv_listview = (CustomerListView) view.findViewById(R.id.lv_listview);
        tv_content.setText(data.content);
        tv_time.setText(data.creatorName + "  " + data.createdAt + (data.type == 1 ? " 提交完成" : " 打回重做"));
//        ImageLoader.getInstance().displayImage(data.);
        if (null != data.attachments && data.attachments.size() > 0) {
            WorkSheetListNestingAdapter mAdapter = new WorkSheetListNestingAdapter(data.attachments, context);
            lv_listview.setAdapter(mAdapter);
        }
        this.addView(view);
    }
}
