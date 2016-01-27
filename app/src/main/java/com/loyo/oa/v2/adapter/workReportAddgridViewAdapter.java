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
    private int[] redGroup = {0,3,6,9,12,15};
    private int[] greenGroup = {1,4,7,10,13,16};
    private int[] blueGroup = {2,5,8,11,14,17};

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
        return null == dynList ? 0 : dynList.size();
    }

    @Override
    public Object getItem(int position) {
        return null == dynList || dynList.isEmpty() ? null : dynList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
    LogUtil.dll("GridView Position:"+position);
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

        for(int i = 0;i<redGroup.length;i++){
            if(position == redGroup[i]){
                holder.num.setTextColor(mContext.getResources().getColor(R.color.red));
            }
        }

        for(int i = 0;i<greenGroup.length;i++){
            if(position == greenGroup[i]){
                holder.num.setTextColor(mContext.getResources().getColor(R.color.title_bg1));
            }
        }

        for(int i = 0;i<blueGroup.length;i++){
            if(position == blueGroup[i]){
                holder.num.setTextColor(mContext.getResources().getColor(R.color.isfinish));
            }
        }

        return convertView;
    }
}
