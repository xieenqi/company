package com.loyo.oa.v2.activityui.work.adapter;


/**
 * 【通讯录本部门】适配器
 * Created by yyy on 15/12/30.
 *
 * Update by ethangong 16/08/04
 * 重构
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.other.bean.User;
import com.loyo.oa.v2.db.bean.*;
import com.loyo.oa.v2.tool.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class ContactsInMyDeptAdapter extends BaseAdapter implements SectionIndexer {

    private List<Object> list = null;
    private Context mContext;
    private int defaultAvatar;

    public ContactsInMyDeptAdapter(final Context mContext, final List<Object> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override //SectionIndexer
    public Object[] getSections() {
        return null;
    }

    @Override //BaseAdapter
    public int getCount() {
        return this.list.size();
    }

    @Override //BaseAdapter
    public Object getItem(final int position) {
        return list.get(position);
    }

    @Override //BaseAdapter
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        Object item = list.get(position);
        if (item.getClass() == String.class) {
            return false;
        }
        return super.isEnabled(position);
    }

    @Override //BaseAdapter
    public View getView(final int position, View view, final ViewGroup arg2) {
        Object item = list.get(position);
        if (item.getClass() == String.class) {

            // 分区
            SectionViewHolder holder = null;
            if (view == null) {
                holder = new SectionViewHolder();
                view = LayoutInflater.from(mContext).inflate(R.layout.item_contact_section, null);
                holder.sectionTitle = (TextView) view.findViewById(R.id.section_title);
                view.setTag(holder);
            }
            else {
                holder = (SectionViewHolder)view.getTag();
            }

            holder.sectionTitle.setText((String)item);
        }
        else if (item.getClass() == DBUser.class) {

            // 用户
            UserViewHolder holder = null;
            if (view == null) {
                holder = new UserViewHolder();
                view = LayoutInflater.from(mContext).inflate(R.layout.item_contact_user, null);
                holder.userName = (TextView) view.findViewById(R.id.user_name);
                holder.dept = (TextView) view.findViewById(R.id.user_dept);
                holder.avatarImage = (ImageView) view.findViewById(R.id.avatar_view);
                view.setTag(holder);
            }
            else {
                holder = (UserViewHolder)view.getTag();
            }

            DBUser user = (DBUser)item;

            holder.userName.setText(user.name);
            holder.dept.setText(user.shortDeptNames);
            if(null == user.avatar || user.avatar.isEmpty() || !user.avatar.contains("http")){
                if (user.gender == 2) {
                    defaultAvatar = R.drawable.icon_contact_avatar;
                } else {
                    defaultAvatar = R.drawable.img_default_user;
                }
                holder.avatarImage.setImageResource(defaultAvatar);
            }else{
                ImageLoader.getInstance().displayImage(user.avatar, holder.avatarImage);
            }

        }

        //
        return view;
    }


    // TODO:
    @Override
    public int getSectionForPosition(final int position) {

        Object item = list.get(position);
        String sortStr = "#";
        if(item.getClass()==String.class)
        {
            sortStr = (String)item;
        }
        else {
            sortStr = ((DBUser)item).getSortLetter();
        }
        char firstChar = sortStr.toUpperCase().charAt(0);
        return firstChar;
    }

    // TODO:
    @Override
    public int getPositionForSection(final int section) {
        for (int i = 0; i < getCount(); i++) {
            Object item = list.get(i);
            String sortStr = "#";
            if(item.getClass()==String.class)
            {
                sortStr = (String)item;
            }
            else {
                sortStr = ((DBUser)item).getSortLetter();
            }
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

    public int getPositionForSectionTitle(final String section) {
        for (int i = 0; i < getCount(); i++) {
            Object item = list.get(i);
            if (item.getClass() != String.class) {
                continue;
            }
            if (section.equals(item)) {
                return i;
            }
        }

        return -1;
    }


    /*
    *  Inner Class
    * */

    static final class UserViewHolder {
        TextView dept;
        TextView userName;
        ImageView avatarImage;
    }

    static final class SectionViewHolder {
        TextView sectionTitle;
    }
}
