package com.loyo.oa.v2.activityui.attendance.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attendance.bean.DataSelect;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;
import java.util.ArrayList;

/**
 * 【考勤管理】时间选择adater v2.2.2
 * Created by yyy on 16/7/8.
 */
public class DataSelectAdapter extends RecyclerView.Adapter<DataSelectAdapter.MViewHolder>{

    private LinearLayout.LayoutParams linearParams;
    private Context mContext;
    private ArrayList<DataSelect> data;
    private int windowW;
    private int selectPosition;
    private int type;
    private boolean itemLock;

   public DataSelectAdapter(Context mContext,ArrayList<DataSelect> data,int windowW,int type){
        this.mContext = mContext;
        this.data = data;
        this.windowW = windowW;
        this.type = type;
   }

    @Override
    public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = View.inflate(mContext,R.layout.item_data_select,null);
        MViewHolder holder = new MViewHolder(mView);
        return holder;
    }


    public void selectPosition(int position){
        selectPosition = position;
        itemLock = true;
    }


    @Override
    public void onBindViewHolder(final MViewHolder holder, int position) {
        DataSelect dataSelect = data.get(position);

        //我的考勤
        if(type == 1){
            if (dataSelect.top.substring(0, 1).equals("0")) {
                holder.num.setText(dataSelect.top.substring(1, 2));
            } else {
                holder.num.setText(dataSelect.top);
            }
            holder.name.setText(dataSelect.bottom);
        }
        //团队考勤
        else{
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
            if(position == 0){
                holder.num.setBackground(null);
            }
            holder.num.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.num.setBackground(mContext.getResources().getDrawable(R.drawable.shape_count_gd));
        } else {
            if(position == 0){
                holder.num.setBackground(mContext.getResources().getDrawable(R.drawable.shape_count_top));
            }else{
                holder.num.setBackground(null);
            }
            holder.num.setTextColor(mContext.getResources().getColor(R.color.text33));
        }

        if (!itemLock && position == 0) {
            holder.num.setBackground(mContext.getResources().getDrawable(R.drawable.shape_count_top));
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
