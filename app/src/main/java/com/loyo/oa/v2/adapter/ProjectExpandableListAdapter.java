package com.loyo.oa.v2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.BaseBeans;
import com.loyo.oa.v2.beans.PagingGroupData_;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.ViewHolder;

import java.util.ArrayList;
import java.util.Date;

public class ProjectExpandableListAdapter<T extends BaseBeans> extends BasePagingGroupDataAdapter_<T> {

    public ProjectExpandableListAdapter(Context context, ArrayList<PagingGroupData_<T>> data) {
        super();
        mContext = context;
        pagingGroupDatas = data;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_listview_common, null, false);
        }

        Project project = (Project) getChild(groupPosition, childPosition);

        ImageView status = ViewHolder.get(convertView, R.id.img_status);
        status.setVisibility(View.GONE);
        TextView title = ViewHolder.get(convertView, R.id.tv_title);
        TextView content = ViewHolder.get(convertView, R.id.tv_content);
        TextView time = ViewHolder.get(convertView, R.id.tv_time);
        View ack = ViewHolder.get(convertView, R.id.view_ack);

//        status.setVisibility(View.VISIBLE);

//        if (project.getStatus() == 1) {
//            status.setImageResource(R.drawable.task_status_1);
//        } else {
//            status.setImageResource(R.drawable.img_project_complete);
//        }

        try {
            time.setText("提交时间: " + app.df9.format(new Date(project.getCreatedAt())));
        } catch (Exception e) {
            Global.ProcException(e);
        }

        content.setText(project.getContent());
        ack.setVisibility(View.GONE);
        title.setText(project.getTitle());

        return convertView;
    }

}
