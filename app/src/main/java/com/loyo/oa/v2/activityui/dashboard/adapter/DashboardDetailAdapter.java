package com.loyo.oa.v2.activityui.dashboard.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.percent.PercentRelativeLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;

/**
 * Created by xeq on 16/12/13.
 */

public class DashboardDetailAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private String[] titles;//表头标题


    public DashboardDetailAdapter(Context context,String[] titles) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.titles=titles;
    }

    @Override
    public int getCount() {
        return 202;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            holder = new Holder();
            convertView = inflater.inflate(R.layout.item_dashboard_detail, null);
            holder.text1 = (TextView) convertView.findViewById(R.id.text1);
            holder.text2 = (TextView) convertView.findViewById(R.id.text2);
            holder.text3 = (TextView) convertView.findViewById(R.id.text3);
            holder.text4 = (TextView) convertView.findViewById(R.id.text4);
            holder.view1 = convertView.findViewById(R.id.view1);
            holder.view2 = convertView.findViewById(R.id.view2);
            holder.item = (LinearLayout) convertView.findViewById(R.id.item);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.setContent(position);
        return convertView;
    }


    class Holder {
        TextView text1, text2, text3, text4;
        View view1;//下面无边距的分割线
        View view2;//下面有左边距的分割线
        LinearLayout item;

        public void setContent(int position) {
            int itemColor1 = Color.parseColor("#666666");
            int itemColor2 = Color.parseColor("#ff9900");
            int itemColor3 = Color.parseColor("#333333");
            if (position == 0) {
                //表哥头部标题
                setChildViewColor(itemColor1);
                view1.setVisibility(View.VISIBLE);
                view2.setVisibility(View.GONE);
                setChildNum(true);
            } else if(1==position){
                //总计
                view1.setVisibility(View.GONE);
                view2.setVisibility(View.VISIBLE);
                setChildViewColor(itemColor2);
                ((TextView)item.getChildAt(0)).setText("总计");
                setChildNum(false);
            }else{
                //其他具体的内容
                view1.setVisibility(View.GONE);
                view2.setVisibility(View.VISIBLE);
                setChildViewColor(itemColor3);
                setChildNum(false);
            }

        }

        //设置一行，每一个字段的颜色
        private void setChildViewColor(int color) {
            for (int i = 0; i < item.getChildCount(); i++) {
                ((TextView) item.getChildAt(i)).setTextColor(color);
            }
        }

        //设置一行的字段数目和表头
        private void setChildNum(boolean isTableNum){
            for (int i = 0; i < item.getChildCount(); i++) {
                //如果是表头，就设置一下表头
                if(isTableNum){
                    for (int j = 0; j < titles.length; j++) {
                        ((TextView)item.getChildAt(j)).setText(titles[j]);
                    }
                }
                //把多余的字段，隐藏了。
                if(i>=titles.length){
                    item.getChildAt(i).setVisibility(View.GONE);
                }

            }
        }

    }
}
