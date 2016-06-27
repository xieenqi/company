package com.loyo.oa.v2.ui.activity.other.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.User;

import java.util.ArrayList;

public class UserListViewAdapter extends BaseAdapter {

    ArrayList<User> listUser;
    public SparseBooleanArray isSelected = new SparseBooleanArray();

    Context context;
    Item_info item_info;
    boolean enabled = true;
    int select_type;

    public UserListViewAdapter(Context _context, ArrayList<User> lstData, int _select_type) {
        context = _context;
        this.listUser = lstData;
        select_type = _select_type;
        init();
    }

    //初始化
    void init() {
        //这儿定义isSelected这个map是记录每个listitem的状态，初始状态全部为false。
        if (listUser != null) {
            for (int i = 0; i < listUser.size(); i++) {
                isSelected.put(i, false);
            }
        }
    }

    @Override
    public int getCount() {
        return listUser.size();
    }

    @Override
    public Object getItem(int position) {
        return listUser.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            //修复筛选部门人员时崩溃的bug ykb 07-13
            convertView = LayoutInflater.from(context).inflate(R.layout.item_usergroup_child, null);
            item_info = new Item_info();
            item_info.img = (ImageView) convertView.findViewById(R.id.img);
            item_info.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            item_info.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            item_info.cBox = (CheckBox) convertView.findViewById(R.id.cb);
            convertView.setTag(item_info);
        } else {
            item_info = (Item_info) convertView.getTag();
        }

        User user = listUser.get(position);
        item_info.tv_title.setText(user.getRealname());

        if (user.departmentsName != null) {
            item_info.tv_content.setText(user.departmentsName);
        }

        if (!enabled) {
            item_info.cBox.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    public void setEnabled(boolean _enabled) {
        enabled = _enabled;
    }

    public class Item_info {
        ImageView img;
        TextView tv_title;
        TextView tv_content;
        public CheckBox cBox;
    }
}
