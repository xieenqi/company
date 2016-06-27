package com.loyo.oa.v2.ui.activity.other.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.UserInfo;

import java.util.List;

/**
 * 【部门选择】 页面的适配器
 * Created by xnq on 15/12/3.
 */
public class DepartmentChooseAdapter extends BaseAdapter {
    private Context context;
    private List<UserInfo> data;

    public DepartmentChooseAdapter(final Context context, final List<UserInfo> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(final int position) {
        return position;
    }

    public List<UserInfo> getData( ) {
        return  data;
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView,final ViewGroup parent) {
        HoloderView holoder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_two_stair_menu_item,null);
            holoder=new HoloderView(convertView);
            convertView.setTag(holoder);
        } else {
            holoder=(HoloderView)convertView.getTag();
        }
        holoder.setContent(position);

        return convertView;
    }

    class HoloderView {

        TextView text;
        HoloderView(final View view){
            text= (TextView)view.findViewById(R.id.text);
        }
        public void setContent(final int position ){
            text.setText(data.get(position).getShortDept().getName());
        }
    }
}
