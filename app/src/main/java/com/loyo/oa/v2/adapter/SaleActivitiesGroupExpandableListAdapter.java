package com.loyo.oa.v2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PagingGroupData;
import com.loyo.oa.v2.beans.SaleActivity;
import com.loyo.oa.v2.tool.ViewHolder;

import java.util.ArrayList;
import java.util.Date;

public class SaleActivitiesGroupExpandableListAdapter extends BaseExpandableListAdapter {
    private MainApp mainApp;
    private Context context;
    private LayoutInflater layoutInflater;

    ArrayList<PagingGroupData<SaleActivity>> lstSaleActivitiesGroupData;

    private Item_info_Group item_info_Group;

    public SaleActivitiesGroupExpandableListAdapter(final Context context, final ArrayList<PagingGroupData<SaleActivity>> lstSaleActivitiesGroupData) {
        this.lstSaleActivitiesGroupData = lstSaleActivitiesGroupData;
        this.context = context;
        mainApp = MainApp.getMainApp();
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return lstSaleActivitiesGroupData.size();
    }

    @Override
    public int getChildrenCount(final int groupPosition) {
        return lstSaleActivitiesGroupData.get(groupPosition).getRecords().size();
    }

    @Override
    public Object getGroup(final int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(final int groupPosition, final int childPosition) {
        return lstSaleActivitiesGroupData.get(groupPosition).getRecords().get(childPosition);
    }

    @Override
    public long getGroupId(final int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(final int groupPosition, final int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, final ViewGroup parent) {

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_sign_show_group, null);
            item_info_Group = new Item_info_Group();
            item_info_Group.textView_item_titel = (TextView) convertView.findViewById(R.id.tv_title);
            convertView.setTag(item_info_Group);
        } else {
            item_info_Group = (Item_info_Group) convertView.getTag();
        }

        PagingGroupData data = lstSaleActivitiesGroupData.get(groupPosition);
        if (data != null && data.getTime() != null) {
            item_info_Group.textView_item_titel.setText(data.getTime());
        }

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild, View convertView, final ViewGroup parent) {

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_saleactivities_group_child, null);
        }

        TextView tv_previous = ViewHolder.get(convertView, R.id.tv_previous);
        TextView tv_creator_name = ViewHolder.get(convertView, R.id.tv_creator_name);
        TextView tv_content = ViewHolder.get(convertView, R.id.tv_content);
        TextView tv_time = ViewHolder.get(convertView, R.id.tv_time);

        SaleActivity saleActivity = lstSaleActivitiesGroupData.get(groupPosition).getRecords().get(childPosition);
        if (saleActivity != null) {
            tv_content.setText(saleActivity.getContent());
            tv_creator_name.setText(saleActivity.getCreator().getName());
            if (saleActivity.getType() != null) {
                tv_previous.setText(saleActivity.getType().getName());
            }
//            tv_time.setText(DateTool.getDiffTime(saleActivity.getCreateAt()));
            tv_time.setText(mainApp.df4.format(new Date(saleActivity.getCreateAt()*1000 )));
        }

        if (childPosition == lstSaleActivitiesGroupData.get(groupPosition).getRecords().size() - 1) {
            convertView.setBackgroundResource(R.drawable.item_bg_buttom);
        } else {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.white));
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(final int groupPosition, final int childPosition) {
        return true;
    }

    private class Item_info_Group {
        TextView textView_item_titel;
    }
}
