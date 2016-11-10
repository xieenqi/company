package com.loyo.oa.v2.activityui.customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.model.MyContactInfo;
import com.loyo.oa.v2.tool.LogUtil;

import java.util.List;

/**
 * 【我的通讯录信息】adapter
 * Created by yyy on 16/10/27.
 */

public class MyContactInfoListAdapter extends BaseAdapter {

    private Context mContext;
    private List<MyContactInfo> contactInfoList;

    public MyContactInfoListAdapter(Context mContext, List<MyContactInfo> contactInfoList) {
        this.mContext = mContext;
        this.contactInfoList = contactInfoList;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的char ascii值
     */
    public int getSectionForPosition(int position) {
        return contactInfoList.get(position).getSortLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = contactInfoList.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_mycontactforlist, null);
            holder.name = (TextView) convertView.findViewById(R.id.tv_contact_name);
            holder.title = (TextView) convertView.findViewById(R.id.tv_contact_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(contactInfoList.get(position).getName());
        LogUtil.dee("首字母:"+contactInfoList.get(position).getSortLetters());

        //根据position获取分类的首字母的char ascii值
        int section = getSectionForPosition(position);

        LogUtil.dee("section:"+section);

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if(position == getPositionForSection(section)){
            holder.title.setVisibility(View.VISIBLE);
            holder.title.setText(contactInfoList.get(position).getSortLetters());
        }else{
            holder.title.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder {
        TextView name;
        TextView title;
    }

}
