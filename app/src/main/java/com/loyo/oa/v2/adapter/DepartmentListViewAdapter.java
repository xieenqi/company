package com.loyo.oa.v2.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.ui.activity.other.DepartmentActivity;
import com.loyo.oa.v2.ui.activity.other.DepartmentUserActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Department;

import java.util.ArrayList;

/**
 * 部门选择适配器
 * Created by Administrator on 2014/12/11 0011.
 */
public class DepartmentListViewAdapter extends BaseAdapter {

    MainApp app;
    LayoutInflater mInflater;

    public ArrayList<Department> listDepartment;
    public SparseBooleanArray isSelected = new SparseBooleanArray();

    Context context;
    boolean enabled = true;
    int select_type, show_type;

    public DepartmentListViewAdapter(final Context _context, final ArrayList<Department> lstData, final int _select_type, final int _show_type) {
        mInflater = LayoutInflater.from(_context);
        app = MainApp.getMainApp();
        context = _context;
        this.listDepartment = lstData;
        this.select_type = _select_type;
        this.show_type = _show_type;
        init();
    }

    //初始化
    private void init() {
        //这儿定义isSelected这个map是记录每个listitem的状态，初始状态全部为false。
        if (listDepartment != null) {
            for (int i = 0; i < listDepartment.size(); i++) {
                isSelected.put(i, false);
            }
        }
    }

    @Override
    public int getCount() {
        return listDepartment.size();
    }

    @Override
    public Object getItem(final int position) {
        return listDepartment.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_listview_department, null);
            holder.img = (ImageView) convertView.findViewById(R.id.img);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.img_title_right_reviewer = (ImageView) convertView.findViewById(R.id.img_title_right_reviewer);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_title.setText(listDepartment.get(position).getName());

        if (enabled) {
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString(DepartmentUserActivity.STR_SUPER_ID, listDepartment.get(position).getId());
                    bundle.putString(DepartmentUserActivity.STR_SUPER_NAME, listDepartment.get(position).getName());
                    bundle.putInt(DepartmentUserActivity.STR_SELECT_TYPE, select_type);
                    bundle.putInt(DepartmentUserActivity.STR_SHOW_TYPE, show_type);

//                    app.startActivity((Activity)context,DepartmentActivity.class,MainApp.ENTER_TYPE_RIGHT,false,bundle);

                    app.startActivityForResult((Activity) context, DepartmentActivity.class, MainApp.ENTER_TYPE_RIGHT, DepartmentActivity.RESULT_ON_ACTIVITY_RETURN, bundle);
                }
            });
        } else {
            holder.img_title_right_reviewer.setVisibility(View.GONE);
        }

        return convertView;
    }

    class ViewHolder {
        public ImageView img_title_right_reviewer;
        public ImageView img;
        public TextView tv_title;
    }

    public void setEnabled(final boolean _enabled) {
        enabled = _enabled;
    }
}
