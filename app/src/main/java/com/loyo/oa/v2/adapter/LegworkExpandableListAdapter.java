package com.loyo.oa.v2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.BaseBeans;
import com.loyo.oa.v2.beans.Item_info_Group;
import com.loyo.oa.v2.beans.LegWork;
import com.loyo.oa.v2.beans.PagingGroupData;
import com.loyo.oa.v2.common.Global;

import java.util.ArrayList;
import java.util.Date;

public class LegworkExpandableListAdapter<T extends BaseBeans> extends BasePagingGroupDataAdapter<T> {

    public LegworkExpandableListAdapter(Context context, ArrayList<PagingGroupData<T>> data) {
        super();
        mContext = context;
        pagingGroupDatas = data;
    }

    Item_info_Child item_info_Child;

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_sign_show_group, null);
            item_info_Group = new Item_info_Group();
            item_info_Group.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            convertView.setTag(item_info_Group);
        } else {
            item_info_Group = (Item_info_Group) convertView.getTag();
        }

        PagingGroupData data = pagingGroupDatas.get(groupPosition);
        if (data != null && data.getTime() != null) {
            item_info_Group.tv_title.setText(data.getTime());
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_sign_show_alluser_group_child, null);
            item_info_Child = new Item_info_Child();

            item_info_Child.img = (ImageView) convertView.findViewById(R.id.img);
            item_info_Child.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
            item_info_Child.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            item_info_Child.view_line1 = convertView.findViewById(R.id.view_line1);
            item_info_Child.tv_user_name = (TextView) convertView.findViewById(R.id.tv_user_name);
            item_info_Child.tv_customer_name = (TextView) convertView.findViewById(R.id.tv_customer_name);

            convertView.setTag(item_info_Child);
        } else {
            item_info_Child = (Item_info_Child) convertView.getTag();
        }

        LegWork legWork = (LegWork) getChild(groupPosition, childPosition);

        if (legWork != null) {
            if (legWork.address != null) {
                item_info_Child.tv_address.setText(legWork.address);
            }

            if (legWork.getCreatedAt() != 0) {
                try {
                    item_info_Child.tv_time.setText(app.df6.format(new Date(legWork.getCreatedAt()*1000)));
                } catch (Exception e) {
                    Global.ProcException(e);
                }

                if (legWork.creator != null && legWork.creator.getName() != null) {
                    item_info_Child.tv_user_name.setText(legWork.creator.getName());
                }
            }

            if (legWork.customerName != null) {
                item_info_Child.tv_customer_name.setText(legWork.customerName);
            }
        }

        return convertView;
    }

    class Item_info_Child {
        ImageView img;
        TextView tv_address;
        View view_line1;
        TextView tv_time;
        TextView tv_user_name;
        TextView tv_customer_name;
    }

}
