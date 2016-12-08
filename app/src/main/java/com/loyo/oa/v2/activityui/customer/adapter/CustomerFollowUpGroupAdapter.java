package com.loyo.oa.v2.activityui.customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.model.FollowUpGroupModel;
import com.loyo.oa.v2.activityui.customer.viewcontrol.CustomerFollowUpListView;
import com.loyo.oa.v2.activityui.followup.model.FollowUpListModel;
import com.loyo.oa.v2.activityui.followup.viewcontrol.AudioPlayCallBack;
import com.loyo.oa.v2.activityui.signinnew.model.AudioModel;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.customview.CustomerListView;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;

import java.util.ArrayList;

/**
 * Created by yyy on 16/12/5.
 */

public class CustomerFollowUpGroupAdapter extends BaseAdapter {

    private Context mContext;
    private CustomerFollowUpListAdapter mAdapter;
    private ArrayList<FollowUpGroupModel> listModel;
    private AudioPlayCallBack audioCb;
    private CustomerFollowUpListView crolView;
    private FollowUpGroupModel groupModel;
    private LayoutInflater mInflater;

    public CustomerFollowUpGroupAdapter(Context context, ArrayList<FollowUpGroupModel> listModel, AudioPlayCallBack audioCb, CustomerFollowUpListView crolView) {

        this.listModel = listModel;
        this.mContext = context;
        this.audioCb = audioCb;
        this.crolView = crolView;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return listModel.size();
    }

    @Override
    public Object getItem(int i) {
        return listModel.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        groupModel = listModel.get(position);
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.activity_customer_followup_group, null);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.layout_listview = (CustomerListView) convertView.findViewById(R.id.layout_listview);
            holder.layout_timegroup = (LinearLayout) convertView.findViewById(R.id.layout_timegroup);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (crolView.getBottomMenuLayout().getVisibility() == View.GONE) {
            holder.tv_title.setText(DateTool.getDiffNoMs(Long.parseLong(groupModel.timeStamp)));
            if (null != groupModel.activities && groupModel.activities.size() > 0) {
                mAdapter = new CustomerFollowUpListAdapter(mContext, groupModel.activities, crolView, audioCb);
                holder.layout_listview.setAdapter(mAdapter);
            }
        }

        return convertView;
    }

    class ViewHolder {

        TextView tv_title;
        CustomerListView layout_listview;
        LinearLayout layout_timegroup;

    }
}
