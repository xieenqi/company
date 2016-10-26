package com.loyo.oa.v2.customview.multi_level_interaction_menu;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.model.City;
import com.loyo.oa.v2.beans.Province;

import java.util.ArrayList;

/**
 * com.loyo.oa.v2.customview.multi_level_interaction_menu
 * 描述 :
 * 作者 : ykb
 * 时间 : 15/10/19.
 */
public class MultiMenuExpandableListAdapter extends BaseExpandableListAdapter {
    private LayoutInflater mLayoutInflater;
    private ArrayList<Province> provinces;
    private int[] selectPostion = new int[]{-1, -1};

    public MultiMenuExpandableListAdapter(Context context,ArrayList<Province> provinces) {
        mLayoutInflater = LayoutInflater.from(context);
        this.provinces = provinces;
    }

    @Override
    public int getGroupCount() {
        return null==provinces?0:provinces.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        return null==provinces||null==provinces.get(groupPosition).getChildren()?0:provinces.get(groupPosition).getChildren().size();
    }

    @Override
    public Province getGroup(int groupPosition) {
      return null==provinces?null:provinces.get(groupPosition);
    }

    @Override
    public City getChild(int groupPosition, int childPosition) {
        return null==provinces||null==provinces.get(groupPosition).getChildren()||provinces.get(groupPosition).getChildren().isEmpty()?null:provinces.get(groupPosition).getChildren().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    class Holder {
        TextView textView;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (null == convertView) {
            holder = new Holder();
            convertView = mLayoutInflater.inflate(R.layout.list_one_stair_menu_item, null, false);
            holder.textView = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        Province province=(Province)getGroup(groupPosition);
        holder.textView.setText(province.getName());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (null == convertView) {
            holder = new Holder();
            convertView = mLayoutInflater.inflate(R.layout.list_two_stair_menu_item, null, false);
            holder.textView = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        City city=(City)getChild(groupPosition, childPosition);
        holder.textView.setText(city.getName());

        if (getSelectPostion()[0] == groupPosition && getSelectPostion()[1] == childPosition) {
            convertView.setBackgroundColor(Color.argb(100, 127, 204, 232));
        } else {
            convertView.setBackgroundColor(Color.argb(0, 0, 0, 0));
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public int[] getSelectPostion() {
        return selectPostion;
    }

    public void setSelectPostion(int groupPosition, int childPosition) {
        this.selectPostion[0] = groupPosition;
        this.selectPostion[1] = childPosition;
        notifyDataSetChanged();
    }
}
