package com.loyo.oa.v2.activityui.customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.model.FollowUpGroupModel;
import com.loyo.oa.v2.activityui.customer.model.SigninNewGroupModel;
import com.loyo.oa.v2.activityui.customer.viewcontrol.CustomerFollowUpListView;
import com.loyo.oa.v2.activityui.customer.viewcontrol.CustomerSigninNewListView;
import com.loyo.oa.v2.activityui.followup.viewcontrol.AudioPlayCallBack;
import com.loyo.oa.v2.activityui.signinnew.viewcontrol.SigninNewListView;
import com.loyo.oa.v2.customview.CustomerListView;
import com.loyo.oa.v2.tool.DateTool;

import java.util.ArrayList;

/**
 * 客户详情——拜访签到——适配器
 * Created by yyy on 16/12/5.
 */

public class CustomerSigninNewGroupAdapter extends BaseAdapter {

    private Context mContext;
    private CustomerSigninNewListAdapter mAdapter;
    private ArrayList<SigninNewGroupModel> listModel;
    private AudioPlayCallBack audioCb;
    private CustomerSigninNewListView crolView;
    private SigninNewGroupModel groupModel;


    public CustomerSigninNewGroupAdapter(Context context, ArrayList<SigninNewGroupModel> listModel, AudioPlayCallBack audioCb, CustomerSigninNewListView crolView) {

        this.listModel = listModel;
        this.mContext = context;
        this.audioCb = audioCb;
        this.crolView = crolView;

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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_customer_signinnew_group, null);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.layout_listview = (CustomerListView) convertView.findViewById(R.id.layout_listview);
            holder.layout_timegroup = (LinearLayout) convertView.findViewById(R.id.layout_timegroup);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (crolView.getBottomMenuLayout().getVisibility() == View.GONE) {
            holder.tv_title.setText(com.loyo.oa.common.utils.DateTool.getDateFriendly(Long.parseLong(groupModel.timeStamp)));
            if (null != groupModel.activities && groupModel.activities.size() > 0) {
                mAdapter = new CustomerSigninNewListAdapter(mContext, groupModel.activities, crolView, audioCb);
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
