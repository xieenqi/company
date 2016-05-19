package com.loyo.oa.v2.activity.sale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import java.util.ArrayList;

/**
 * 我的团队 销售机会 adapter
 * Created by yyy on 16/5/17.
 */
public class AdapterSaleTeam extends BaseAdapter {

    public Context mContext;
    public ArrayList arrayList;

    public AdapterSaleTeam(Context context){
        mContext = context;
        arrayList = new ArrayList();
        for(int i = 0;i<20;i++){
            arrayList.add(i);
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
        viewHolder holder = null;
        if(convertView == null){
            holder = new viewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_saleteamlist,null);
            holder.creator = (TextView) convertView.findViewById(R.id.sale_teamlist_creator);
            holder.state = (TextView) convertView.findViewById(R.id.sale_teamlist_state);
            holder.guess = (TextView) convertView.findViewById(R.id.sale_teamlist_guess);
            holder.money = (TextView) convertView.findViewById(R.id.sale_teamlist_money);
            holder.title = (TextView) convertView.findViewById(R.id.sale_teamlist_title);
            convertView.setTag(holder);
        }else{
            holder = (viewHolder)convertView.getTag();
        }



        holder.title.setText("成都东软收购机会"+arrayList.get(position));

        return convertView;
    }

    class viewHolder{
        TextView title;
        TextView state;
        TextView guess;
        TextView money;
        TextView creator;
    }
}
