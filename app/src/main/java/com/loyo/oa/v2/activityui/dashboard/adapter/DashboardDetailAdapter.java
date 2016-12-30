package com.loyo.oa.v2.activityui.dashboard.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.dashboard.common.DashborardType;
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
    private DashborardType type;

    public DashboardDetailAdapter(Context context, DashborardType type) {
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
                ((TextView) item.getChildAt(0)).setText("总计");
                setChildNumName(false);

                bindDate(0);
            } else {
                //其他具体的内容
                view1.setVisibility(View.GONE);
                view2.setVisibility(View.VISIBLE);
                setChildViewColor(itemColor3);
                setChildNumName(false);
                bindDate(position);
            }
        }

        //设置一行，每一个字段的颜色
        private void setChildViewColor(int color) {
            for (int i = 0; i < item.getChildCount(); i++) {
                ((TextView) item.getChildAt(i)).setTextColor(color);
            }
        }

        //设置一行的字段数目和表头
        private void setChildNumName(boolean isTableNum) {
            String[] heads = type.getTableHead();
            for (int i = 0; i < item.getChildCount(); i++) {
                //如果是表头，就设置一下表头
                if (isTableNum) {
                    for (int j = 0; j < heads.length; j++) {
                        ((TextView) item.getChildAt(j)).setText(heads[j]);
                    }
                }
                //把多余的字段，隐藏了。
                if (i >= heads.length) {
                    item.getChildAt(i).setVisibility(View.GONE);
                }
            }
        }

        //判断不同的数据，绑定数据
        private void bindDate(int position) {
            if (DashborardType.CUS_FOLLOWUP == type || DashborardType.SALE_FOLLOWUP == type||DashborardType.CUS_SIGNIN == type) {
                //客户/线索 跟进/客户拜访
                if (0 == position) {
                    text1.setText(records.get(0).userName + "");
                    text2.setText(records.get(0).totalCustomer + "");
                    text3.setText(records.get(0).total + "");
                } else {
                    int tempP = position - 1;//添加了一个表头，消去下标偏移
                    text1.setText(records.get(tempP).userName + "");
                    text2.setText(records.get(tempP).totalCustomer + "");
                    text3.setText(records.get(tempP).total + "");
                }
            }else if (DashborardType.CUS_CELL_RECORD == type||DashborardType.SALE_CELL_RECORD == type) {
                //客户电话录/线索电话录
                if (0 == position) {
                    text1.setText(records.get(0).userName + "");
                    text2.setText(records.get(0).totalCustomer + "");
                    text3.setText(records.get(0).total + "");
                    text4.setText(records.get(0).totalLength + "");
                } else {
                    int tempP = position - 1;//添加了一个表头，消去下标偏移
                    text1.setText(records.get(tempP).userName + "");
                    text2.setText(records.get(tempP).totalCustomer + "");
                    text3.setText(records.get(tempP).total + "");
                    text4.setText(records.get(tempP).totalLength + "");

                }
            }else if (DashborardType.COMMON == type) {
                //增量/存量
                if (0 == position) {
                    text1.setText(records.get(0).name + "");
                    text2.setText(records.get(0).count + "");
                    text3.setText(records.get(0).addCount + "");
                } else {
                    int tempP = position - 1;//添加了一个表头，消去下标偏移
                    text1.setText(records.get(tempP).name + "");
                    text2.setText(records.get(tempP).count + "");
                    text3.setText(records.get(tempP).addCount + "");
                }
            }else if (DashborardType.ORDER_MONEY == type||DashborardType.ORDER_NUMBER == type) {
                // 订单数量和金额
                if (0 == position) {
                    text1.setText(records.get(0).name + "");
                    text2.setText(records.get(0).targetNum + "");
                    text3.setText(records.get(0).orderNum+ "");
                    text4.setText(records.get(0).finish_rate + "");
                } else {
                    int tempP = position - 1;//添加了一个表头，消去下标偏移
                    text1.setText(records.get(tempP).name + "");
                    text2.setText(records.get(tempP).targetNum + "");
                    text3.setText(records.get(tempP).orderNum + "");
                    text4.setText(records.get(tempP).finish_rate + "");

                }
            }

        }

    }
}
