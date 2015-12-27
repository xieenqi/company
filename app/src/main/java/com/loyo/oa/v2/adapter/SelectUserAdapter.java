package com.loyo.oa.v2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.Department;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.tool.LogUtil;

import java.util.ArrayList;


/**
 * 人员选择适配器
 * Created by yyy on 15/12/25.
 */
public class SelectUserAdapter extends BaseAdapter {

    public ArrayList<User> listDepartment;
    public Context mContext;
    public LayoutInflater mInflater;
    public String deptName;
    public String npcName;


    public SelectUserAdapter(Context mContext, ArrayList<User> listDepartment) {
        this.mContext = mContext;
        this.listDepartment = listDepartment;
        mInflater = LayoutInflater.from(mContext);
    }


    public class ViewHolder {
        TextView userName;
        TextView worker;
        TextView dept;
    }

    @Override
    public int getCount() {
        return listDepartment.size();
    }

    @Override
    public Object getItem(int position) {
        return listDepartment.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_selectcustomer_right_lv, null);
            holder.userName = (TextView) convertView.findViewById(R.id.item_selectdu_right_name);
            holder.dept = (TextView) convertView.findViewById(R.id.item_selectdu_right_dept);
            holder.worker = (TextView) convertView.findViewById(R.id.item_selectdu_right_worker);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        try{
            deptName = listDepartment.get(position).depts.get(0).getShortDept().getName();
        }catch (NullPointerException e){
            e.printStackTrace();
            deptName = "无";
        }

        try{
            npcName = listDepartment.get(position).role.name;
        }catch (NullPointerException e){
            e.printStackTrace();
            npcName = "无";
        }

        holder.userName.setText(listDepartment.get(position).getRealname());
        holder.dept.setText(deptName);
        holder.worker.setText(npcName);

        return convertView;

    }
}
