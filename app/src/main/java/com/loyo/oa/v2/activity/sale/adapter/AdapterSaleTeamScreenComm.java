package com.loyo.oa.v2.activity.sale.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import java.util.ArrayList;

/**
 * 销售机会 筛选公共adapter
 * Created by yyy on 16/5/19.
 */
public class AdapterSaleTeamScreenComm extends BaseAdapter {

    private Context mContext;
    private ArrayList arrayList = new ArrayList();

    public AdapterSaleTeamScreenComm(final Context context){
        this.mContext = context;
        for(int i = 0;i<10;i++){
            arrayList.add("测试Item"+i);
        }
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(null == convertView){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_saleteam_common,null);
            holder.name = (TextView)convertView.findViewById(R.id.item_saleteam_common_name);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(arrayList.get(position).toString());
        return convertView;
    }

    class ViewHolder{
        TextView name;
    }
}
