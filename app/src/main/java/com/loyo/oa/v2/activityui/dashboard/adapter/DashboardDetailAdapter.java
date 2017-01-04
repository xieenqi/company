package com.loyo.oa.v2.activityui.dashboard.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.dashboard.common.DashboardType;
import com.loyo.oa.v2.activityui.dashboard.model.StatisticRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xeq on 16/12/13.
 */

public class DashboardDetailAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<StatisticRecord> records = new ArrayList<>();//数据列表
    private DashboardType type;

    public DashboardDetailAdapter(Context context, DashboardType type) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.type = type;
    }

    //加载更多
    public void addAll(List<StatisticRecord> data) {
        records.addAll(data);
        notifyDataSetChanged();
    }

    //刷新
    public void reload(List<StatisticRecord> data) {
        records.clear();
        records.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        //因为多了一行表头
        return records.size() + 1;
    }

    public boolean isEmpty() {
        return records == null || records.size() <= 0;
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
                setChildNumName(true);
            } else if (1 == position) {
                //总计
                view1.setVisibility(View.GONE);
                view2.setVisibility(View.VISIBLE);
                setChildViewColor(itemColor2);
                ((TextView) ((FrameLayout)item.getChildAt(0)).getChildAt(0)).setText("总计");
                setChildNumName(false);

                bindData(0);
            } else {
                //其他具体的内容
                view1.setVisibility(View.GONE);
                view2.setVisibility(View.VISIBLE);
                setChildViewColor(itemColor3);
                setChildNumName(false);
                bindData(position);
            }
        }

        //设置一行，每一个字段的颜色
        private void setChildViewColor(int color) {
            for (int i = 0; i < item.getChildCount(); i++) {
                ((TextView) ((FrameLayout)item.getChildAt(i)).getChildAt(0)).setTextColor(color);
            }
        }

        //设置一行的字段数目和表头
        private void setChildNumName(boolean isTableNum) {
            String[] heads = type.getTableHead();
            for (int i = 0; i < item.getChildCount(); i++) {
                //如果是表头，就设置一下表头
                if (isTableNum) {
                    for (int j = 0; j < heads.length; j++) {
                        ((TextView) ((FrameLayout)item.getChildAt(j)).getChildAt(0)).setText(heads[j]);
                    }
                }
                //把多余的字段，隐藏了。
                if (i >= heads.length) {
                    item.getChildAt(i).setVisibility(View.GONE);
                }
            }
        }

        //判断不同的数据，绑定数据
        private void bindData(int position) {
            ArrayList<String> displayColumns = null;
            if (0 == position) {
                displayColumns = records.get(0).getDsiplayColumnForType(type);
            }
            else {
                displayColumns = records.get(position-1).getDsiplayColumnForType(type);
            }

            for (int i = 0; i < 4 && i < displayColumns.size(); i++) {
                getTextViewAtIndex(i).setText(displayColumns.get(i));
            }
        }

        private TextView getTextViewAtIndex(int index) {
            TextView result = text1;
            switch (index){
                case 0:
                    result = text1;
                    break;
                case 1:
                    result = text2;
                    break;
                case 2:
                    result = text3;
                    break;
                case 3:
                    result = text4;
                    break;
            }
            return result;
        }

    }
}
