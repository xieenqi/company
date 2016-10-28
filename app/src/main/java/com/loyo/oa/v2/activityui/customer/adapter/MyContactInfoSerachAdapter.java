package com.loyo.oa.v2.activityui.customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.model.MyContactInfo;
import java.util.List;

/**
 * Created by yyy on 16/10/28.
 */

public class MyContactInfoSerachAdapter extends BaseAdapter {

    private Context mContext;
    private List<MyContactInfo> contactInfoList;

    public MyContactInfoSerachAdapter(Context mContext, List<MyContactInfo> contactInfoList) {
        this.mContext = mContext;
        this.contactInfoList = contactInfoList;
    }

    @Override
    public int getCount() {
        return contactInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return contactInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_mycontactforlist_serach, null);
            holder.name = (TextView) convertView.findViewById(R.id.tv_contact_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(contactInfoList.get(position).getName());

        return convertView;
    }

    class ViewHolder {
        TextView name;
    }
}
