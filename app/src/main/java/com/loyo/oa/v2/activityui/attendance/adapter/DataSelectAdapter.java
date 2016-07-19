package com.loyo.oa.v2.activityui.attendance.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attendance.bean.DataSelect;

import java.util.ArrayList;

/**
 * 【考勤管理】时间选择adater v2.2.2
 * Created by yyy on 16/7/8.
 */
public class DataSelectAdapter extends RecyclerView.Adapter<DataSelectAdapter.MViewHolder> {

    private LinearLayout.LayoutParams linearParams;
    private Context mContext;
    private ArrayList<DataSelect> data;
    private int windowW;
    private int selectPosition;
    private int defaultPosition;
    private int type;
    private boolean itemLock;
    private ScaleAnimation animation;

    public DataSelectAdapter(Context mContext, ArrayList<DataSelect> data, int windowW, int type, int defaultPosition) {
        this.mContext = mContext;
        this.data = data;
        this.windowW = windowW;
        this.type = type;
        this.defaultPosition = defaultPosition;
    }

    @Override
    public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = View.inflate(mContext, R.layout.item_data_select, null);
        MViewHolder holder = new MViewHolder(mView);

        animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(120);//设置动画持续时间

        return holder;
    }


    public void selectPosition(int position) {
        selectPosition = position;
        itemLock = true;
    }


    @Override
    public void onBindViewHolder(final MViewHolder holder, int position) {
        DataSelect dataSelect = data.get(position);

        //我的考勤
        if (type == 1) {
            if (dataSelect.top.substring(0, 1).equals("0")) {
                holder.num.setText(dataSelect.top.substring(1, 2));
            } else {
                holder.num.setText(dataSelect.top);
            }
            holder.name.setText(dataSelect.bottom);
        }
        //团队考勤
        else {
            if (dataSelect.top.substring(0, 1).equals("0")) {
                holder.num.setText(dataSelect.top.substring(1, 2));
            } else {
                holder.num.setText(dataSelect.top);
            }

            if (dataSelect.bottom.substring(0, 1).equals("0")) {
                holder.name.setText(dataSelect.bottom.substring(1, 2) + "月");
            } else {
                holder.name.setText(dataSelect.bottom + "月");
            }
        }

        linearParams = (LinearLayout.LayoutParams) holder.div.getLayoutParams(); //取控件textView当前的布局参数
        linearParams.width = windowW / 7;//一页只显示7条数据
        holder.div.setLayoutParams(linearParams);

        if (itemLock && selectPosition == position) {
            if (position == 0) {
                holder.num.setBackground(null);
            }
            holder.num.setTextColor(Color.parseColor("#ffffff"));
            holder.num.setBackgroundResource(R.drawable.shape_count_gd);
            holder.num.setAnimation(animation);
        } else {
            if (position == 0) {
                holder.num.setBackgroundResource(R.drawable.shape_count_top);
            } else {
                holder.num.setBackground(null);
            }
            holder.num.setTextColor(mContext.getResources().getColor(R.color.text33));
        }

        if (!itemLock && position == defaultPosition) {
            holder.num.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.num.setBackgroundResource(R.drawable.shape_count_gd);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MViewHolder extends RecyclerView.ViewHolder {

        public TextView num;
        public TextView name;
        public LinearLayout div;

        public MViewHolder(View view) {
            super(view);
            this.num = (TextView) view.findViewById(R.id.item_select_num);
            this.name = (TextView) view.findViewById(R.id.item_select_name);
            this.div = (LinearLayout) view.findViewById(R.id.item_select_div);
        }
    }
}
