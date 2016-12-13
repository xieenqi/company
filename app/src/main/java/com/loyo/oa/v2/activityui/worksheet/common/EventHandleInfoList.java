package com.loyo.oa.v2.activityui.worksheet.common;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.commonview.MapSingleView;
import com.loyo.oa.v2.activityui.worksheet.adapter.WorkSheetListNestingAdapter;
import com.loyo.oa.v2.activityui.worksheet.bean.EventHandleInfoItem;
import com.loyo.oa.v2.customview.CustomerListView;
import com.loyo.oa.v2.customview.RoundImageView;
import com.loyo.oa.v2.tool.DateTool;

/**
 * 事件 详情  处理信息列表
 * Created by xeq on 16/8/31.
 */
public class EventHandleInfoList extends LinearLayout {
    private RoundImageView iv_avatar;
    private TextView tv_time, tv_content, tv_address;
    private CustomerListView lv_listview;
    private LinearLayout ll_address;

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


    private void bindView(final Context context, final EventHandleInfoItem data) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event_handler_info_list, null, false);
        iv_avatar = (RoundImageView) view.findViewById(R.id.iv_avatar);
        tv_time = (TextView) view.findViewById(R.id.tv_time);
        tv_content = (TextView) view.findViewById(R.id.tv_content);
        tv_address = (TextView) view.findViewById(R.id.tv_address);
        lv_listview = (CustomerListView) view.findViewById(R.id.lv_listview);
        ll_address = (LinearLayout) view.findViewById(R.id.ll_address);
        tv_content.setText(data.content);
        tv_time.setText(data.creatorName + "  " + com.loyo.oa.common.utils.DateTool.getFriendlyTime(data.createdAt,true) + (data.type == 1 ? " 提交完成" : " 打回重做"));

        if (null != data.attachments && data.attachments.size() > 0) {
            WorkSheetListNestingAdapter mAdapter = new WorkSheetListNestingAdapter(data.attachments, context);
            lv_listview.setAdapter(mAdapter);
            mAdapter.refreshData();
        }
        if (null != data.address && !TextUtils.isEmpty(data.address.addr)) {
            ll_address.setVisibility(VISIBLE);
            tv_address.setText(data.address.addr);
            ll_address.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mIntent = new Intent(context, MapSingleView.class);
                    mIntent.putExtra("la", data.address.getLoc()[1]);
                    mIntent.putExtra("lo", data.address.getLoc()[0]);
                    mIntent.putExtra("address", data.address.addr);
                    context.startActivity(mIntent);
                }
            });
        }
        this.addView(view);
    }
}
