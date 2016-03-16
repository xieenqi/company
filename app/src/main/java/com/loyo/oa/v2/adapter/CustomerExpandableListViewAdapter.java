package com.loyo.oa.v2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBeans;
import com.loyo.oa.v2.beans.Contact;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.Item_info_Group;
import com.loyo.oa.v2.beans.PagingGroupData;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;

public class CustomerExpandableListViewAdapter<T extends BaseBeans> extends BasePagingGroupDataAdapter<T> {

    public CustomerExpandableListViewAdapter(final Context context, final ArrayList<PagingGroupData<T>> data) {
        mContext = context;
        pagingGroupDatas = data;
        app = (MainApp) mContext.getApplicationContext();
    }

    @Override
    public int getGroupCount() {
        return pagingGroupDatas.size();
    }

    @Override
    public int getChildrenCount(final int groupPosition) {
        return pagingGroupDatas.get(groupPosition).getRecords().size();
    }

    @Override
    public Object getGroup(final int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(final int groupPosition, final int childPosition) {
        return pagingGroupDatas.get(groupPosition).getRecords().get(childPosition);
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

    Item_info_Group item_info_Group;
    Item_info_Child item_info;

    class Item_info_Child {
        TextView tv_name;
        TextView tv_customer_status;
        TextView tv_customer_contract;
        TextView tv_customer_visitnum;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, final ViewGroup parent) {
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
    public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_customer_manage, null);
            item_info = new Item_info_Child();

            item_info.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            item_info.tv_customer_status = (TextView) convertView.findViewById(R.id.tv_customer_status);
            item_info.tv_customer_contract = (TextView) convertView.findViewById(R.id.tv_customer_contract);
            item_info.tv_customer_visitnum = (TextView) convertView.findViewById(R.id.tv_customer_visitnum);

            convertView.setTag(item_info);
        } else {
            item_info = (Item_info_Child) convertView.getTag();
        }

        Customer customer = (Customer) getChild(groupPosition, childPosition);

        if (customer != null) {
            if (customer.name != null) {
                item_info.tv_name.setText(customer.name);
            }

            Contact contact=Utils.findDeault(customer);
            if(null!=contact) {
                item_info.tv_customer_contract.setText(contact.getName());
            }
            if(null!=customer.counter) {
                item_info.tv_customer_visitnum.setText(String.format("签到%d次", customer.counter.getVisit()));
            }

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < customer.tags.size(); i++) {
                if (i > 0) {
                    sb.append(" / ");
                }

                sb.append(customer.tags.get(i).getItemName());
            }

            item_info.tv_customer_status.setText(sb.toString());
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(final int groupPosition, final int childPosition) {
        return true;
    }
}
