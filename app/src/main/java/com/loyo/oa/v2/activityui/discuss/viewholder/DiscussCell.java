package com.loyo.oa.v2.activityui.discuss.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.discuss.HttpDiscussItem;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.tool.DateTool;

import java.util.Date;

/**
 * Created by EthanGong on 2016/11/22.
 */

public class DiscussCell extends RecyclerView.ViewHolder {

    public ImageView iv_icon;
    public ImageView v_msgPoint;
    public TextView tv_title;
    public TextView tv_time;
    public TextView tv_content;
    public TextView tv_dateTime;

    public DiscussCell(View itemView) {
        super(itemView);
        iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
        v_msgPoint = (ImageView) itemView.findViewById(R.id.v_msgPoint);
        tv_title = (TextView) itemView.findViewById(R.id.tv_title);
        tv_time = (TextView) itemView.findViewById(R.id.tv_time);
        tv_content = (TextView) itemView.findViewById(R.id.tv_content);
        tv_dateTime = (TextView) itemView.findViewById(R.id.tv_dateTime);
    }

    public void openItem(final HttpDiscussItem itemData) {

        tv_title.setText(itemData.title);
        tv_time.setText(itemData.newUpdatedAt != 0 ? com.loyo.oa.common.utils.DateTool.getFriendlyTime(itemData.newUpdatedAt,true) : itemData.updatedAt.substring(11, 19));
        tv_content.setText(itemData.creator.name + ":" + itemData.content);

        switch (itemData.bizType) {
            case 1:
                iv_icon.setImageResource(R.drawable.ic_disuss_report);
                tv_dateTime.setVisibility(View.VISIBLE);
                tv_dateTime.setText(MainApp.getMainApp().df11.format(new Date(System.currentTimeMillis())));
                break;
            case 2:
                iv_icon.setImageResource(R.drawable.ic_discuss_task);
                tv_dateTime.setVisibility(View.GONE);
                break;
            case 5:
                iv_icon.setImageResource(R.drawable.ic_discuss_project);
                tv_dateTime.setVisibility(View.GONE);
                break;
            default:

                break;

        }
        v_msgPoint.setVisibility(itemData.viewed ? View.INVISIBLE : View.VISIBLE);
    }
}
