package com.loyo.oa.v2.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.WorkReportDyn;
import com.loyo.oa.v2.tool.LogUtil;

import java.util.ArrayList;

/**
 * Created by yyy on 16/1/21.
 */
public class workReportAddgridViewAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<WorkReportDyn> dynList;

    public workReportAddgridViewAdapter(Context context,ArrayList<WorkReportDyn> dyn){
        this.mContext = context;
        this.dynList = dyn;
    }

    public class ViewHolder{
        TextView name;
        TextView num;
    }


    @Override
    public int getCount() {
        return dynList.size();
    }

    @Override
    public Object getItem(int position) {
        return dynList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
    ViewHolder holder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_workreportgridview,null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.tv_workdysn_item_name);
            holder.num = (TextView) convertView.findViewById(R.id.tv_workdysn_item_num);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(dynList.get(position).getName());
        holder.num.setText(dynList.get(position).getNum());

        LogUtil.dll((position+1)+"");

        return convertView;
    }
}
