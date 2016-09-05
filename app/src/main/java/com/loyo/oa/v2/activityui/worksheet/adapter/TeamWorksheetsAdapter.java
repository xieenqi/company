package com.loyo.oa.v2.activityui.worksheet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.worksheet.bean.Worksheet;
import com.loyo.oa.v2.common.GroupsData;
import com.loyo.oa.v2.common.adapter.BaseGroupsDataAdapter;

/**
 * Created by EthanGong on 16/8/27.
 */
public class TeamWorksheetsAdapter extends BaseGroupsDataAdapter {

    private boolean fromSelfCreated;

    public TeamWorksheetsAdapter(final Context context, final GroupsData data) {
        super();
        mContext = context;
        groupsData = data;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild, View convertView, final ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_team_worksheet, null, false);
            holder = new ViewHolder();
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.tv_progress = (TextView) convertView.findViewById(R.id.tv_progress);
            holder.tv_creator_name = (TextView) convertView.findViewById(R.id.tv_creator_name);
            holder.tv_dispatcher_name = (TextView) convertView.findViewById(R.id.tv_dispatcher_name);
            holder.tv_order = (TextView) convertView.findViewById(R.id.tv_order);
            holder.tv_creator_show = (TextView) convertView.findViewById(R.id.tv_creator_show);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Worksheet ws = (Worksheet) getChild(groupPosition, childPosition);
        holder.loadData(ws);

        return convertView;
    }


    private class ViewHolder {

        TextView tv_content;      // 工单名称
        TextView tv_progress;     // 进度
        TextView tv_creator_name; // 分派者姓名
        TextView tv_dispatcher_name; // 分派者姓名
        TextView tv_order;        // 订单
        TextView tv_creator_show; // 分派人\创建人

        public void loadData(Worksheet ws) {
            tv_content.setText(ws.title);
            tv_creator_name.setText(ws.creatorName);
            tv_dispatcher_name.setText(ws.dispatcherName);
            tv_order.setText(ws.orderName);
            tv_progress.setText("完成度( "+ws.finishCount + "/" + ws.totalCount + " )");

        }
    }
}
