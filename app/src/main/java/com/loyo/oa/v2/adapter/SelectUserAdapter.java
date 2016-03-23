package com.loyo.oa.v2.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.User;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;


/**
 * 人员选择适配器
 * Created by yyy on 15/12/25.
 */
public class SelectUserAdapter extends BaseAdapter {

    private ArrayList<User> listUsers;
    private Context mContext;
    private LayoutInflater mInflater;
    private String deptName;
    private String npcName;
    private boolean isAllCheck;
    public Handler handler;

    public SelectUserAdapter(final Context mContext, final ArrayList<User> listUsers, final boolean isAllCheck, final Handler handler) {

        this.isAllCheck = isAllCheck;
        this.mContext = mContext;
        this.listUsers = listUsers;
        mInflater = LayoutInflater.from(mContext);
        this.handler = handler;
    }

    class ViewHolder {

        TextView userName;
        TextView worker;
        TextView dept;
        ImageView heading;
        CheckBox checkBox;

    }

    @Override
    public int getCount() {
        return listUsers.size();
    }

    @Override
    public Object getItem(final int position) {
        return listUsers.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    public ArrayList<User> getData() {
        return listUsers;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_selectcustomer_right_lv, null);
            holder.userName = (TextView) convertView.findViewById(R.id.item_selectdu_right_name);
            holder.dept = (TextView) convertView.findViewById(R.id.item_selectdu_right_dept);
            holder.worker = (TextView) convertView.findViewById(R.id.item_selectdu_right_worker);
            holder.heading = (ImageView) convertView.findViewById(R.id.img_title_left);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.item_selectdu_checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        /*部门名称*/
        try {
            deptName = listUsers.get(position).depts.get(0).getShortDept().getName();
        } catch (NullPointerException e) {
            e.printStackTrace();
            deptName = "无";
        }

        /*用户职称*/
        try {
            npcName = listUsers.get(position).getDepts().get(0).getTitle();
        } catch (NullPointerException e) {
            e.printStackTrace();
            npcName = "无";
        }
        holder.userName.setText(listUsers.get(position).getRealname());
        holder.dept.setText(deptName);
        holder.worker.setText(npcName);

            /*选中赋值*/
        if (listUsers.get(position).isIndex()) {
            holder.checkBox.setChecked(true);
        } else if (!listUsers.get(position).isIndex()) {
            holder.checkBox.setChecked(false);
        } else {
            holder.checkBox.setChecked(false);
        }

        ImageLoader.getInstance().displayImage(listUsers.get(position).getAvatar(), holder.heading);

        return convertView;
    }
}
