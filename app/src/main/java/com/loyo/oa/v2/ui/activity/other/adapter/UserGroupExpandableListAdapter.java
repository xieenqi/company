package com.loyo.oa.v2.ui.activity.other.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.ui.activity.other.DepartmentUserActivity;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.beans.UserGroupData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 【人员选择】适配器
 * */

public class UserGroupExpandableListAdapter extends BaseExpandableListAdapter {

    private DepartmentUserActivity departmentUserActivity;
    LayoutInflater layoutInflater;

    public ArrayList<UserGroupData> lstUserGroupData;

    ArrayList<Map<Integer, Boolean>> isSelected = new ArrayList<>();

    private Item_info item_info;
    private Item_info_Group item_info_Group;
    public int isSelected_radio_group = -1;
    public int isSelected_radio_child = -1;

    public UserGroupExpandableListAdapter(DepartmentUserActivity departmentUserActivity, ArrayList<UserGroupData> lstUserGroupData) {
        this.lstUserGroupData = lstUserGroupData;
        this.departmentUserActivity = departmentUserActivity;
        layoutInflater = (LayoutInflater) departmentUserActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //这儿定义isSelected这个map是记录每个listitem的状态，初始状态全部为false。
        for (int i = 0; i < lstUserGroupData.size(); i++) {
            Map<Integer, Boolean> map = new HashMap<>();
            for (int j = 0; j < lstUserGroupData.get(i).getLstUser().size(); j++) {
                map.put(j, false);
            }
            isSelected.add(map);
        }
    }

    @Override
    public int getGroupCount() {
        return lstUserGroupData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return lstUserGroupData.get(groupPosition).getLstUser().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return lstUserGroupData.get(groupPosition).getGroupName();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return lstUserGroupData.get(groupPosition).getLstUser().get(childPosition);
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
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_usergroup_group, null);
            item_info_Group = new Item_info_Group();
            item_info_Group.textView_item_titel = (TextView) convertView.findViewById(R.id.textView_item_titel);

            convertView.setTag(item_info_Group);
        } else {
            item_info_Group = (Item_info_Group) convertView.getTag();
        }

        item_info_Group.textView_item_titel.setText(lstUserGroupData.get(groupPosition).getGroupName());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_usergroup_child, null);
            item_info = new Item_info();
            item_info.img = (ImageView) convertView.findViewById(R.id.img);
            item_info.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            item_info.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            item_info.cBox = (CheckBox) convertView.findViewById(R.id.cb);
            convertView.setTag(item_info);
        } else {
            item_info = (Item_info) convertView.getTag();
        }

        User user = lstUserGroupData.get(groupPosition).getLstUser().get(childPosition);
        item_info.tv_title.setText(user.getRealname());
        item_info.tv_content.setText(user.departmentsName);

        switch (departmentUserActivity.select_type) {
            case DepartmentUserActivity.TYPE_SELECT_SINGLE:
                item_info.cBox.setChecked(isSelected.get(groupPosition).get(childPosition));
                break;
            case DepartmentUserActivity.TYPE_SELECT_MULTUI:
                item_info.cBox.setChecked(
                        groupPosition == isSelected_radio_group && childPosition == isSelected_radio_child
                );
                break;
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class Item_info_Group {
        TextView textView_item_titel;
    }

    public class Item_info {
        ImageView img;
        TextView tv_title;
        TextView tv_content;
        public CheckBox cBox;
    }
}
