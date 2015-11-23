package com.loyo.oa.v2.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.BaseBeans;
import com.loyo.oa.v2.beans.PagingGroupData;
import com.loyo.oa.v2.beans.Task;

import java.util.ArrayList;
import java.util.Date;

public class TasksExpandableListAdapter<T extends BaseBeans> extends BasePagingGroupDataAdapter<T> {

    public TasksExpandableListAdapter(Context context, ArrayList<PagingGroupData<T>> data) {
        super();
        mContext = context;
        pagingGroupDatas = data;
    }

    Item_info item_info;

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_listview_tasks_manage, null);
            item_info = new Item_info();

            item_info.tv_creator_name = (TextView) convertView.findViewById(R.id.tv_creator_name);
            item_info.img_task_status = (ImageView) convertView.findViewById(R.id.img_task_status);
            item_info.tv_responsiblePerson_name = (TextView) convertView.findViewById(R.id.tv_responsiblePerson_name);
            item_info.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            item_info.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            item_info.layout_title = (ViewGroup) convertView.findViewById(R.id.layout_title);
            item_info.view_ack = convertView.findViewById(R.id.view_ack);

            convertView.setTag(item_info);
        } else {
            item_info = (Item_info) convertView.getTag();
        }

        Task task = (Task) getChild(groupPosition, childPosition);
        if (task != null) {
            if (task.getCreator() != null) {
                item_info.tv_creator_name.setText(task.getCreator().getRealname());
            }

            if (task.getResponsiblePerson() != null) {
                item_info.tv_responsiblePerson_name.setText(task.getResponsiblePerson().getRealname());
            }

            if (task.getStatus() == 1) {
                item_info.img_task_status.setImageResource(R.drawable.task_status_1);
            } else if (task.getStatus() == 2) {
                item_info.img_task_status.setImageResource(R.drawable.task_status_2);
            } else if (task.getStatus() == 3) {
                item_info.img_task_status.setImageResource(R.drawable.task_status_3);
            }

            Log.e(getClass().getSimpleName(),"oncreate View , date : "+task.getCreatedAt());
            try {
//                item_info.tv_time.setText(DateTool.formateServerDate(task.getCreatedAt(), app.df3));
                item_info.tv_time.setText(app.df3.format(new Date(task.getCreatedAt())));
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(),"oncreate View , exception : "+e.toString());
//                Global.ProcException(e);
            }

            item_info.view_ack.setVisibility(task.isAck() ? View.GONE : View.VISIBLE);
            item_info.tv_title.setText(task.getTitle());
        }

        return convertView;
    }

    class Item_info {
        ImageView img_task_status;
        View view_ack;
        TextView tv_creator_name;
        TextView tv_responsiblePerson_name;
        TextView tv_time;
        TextView tv_title;
        ViewGroup layout_title;
    }
}
