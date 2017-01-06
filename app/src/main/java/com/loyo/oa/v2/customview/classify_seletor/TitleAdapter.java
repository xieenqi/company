package com.loyo.oa.v2.customview.classify_seletor;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.loyo.oa.v2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 头部导航的适配器
 * Created by jie on 16/12/29.
 */

public class TitleAdapter extends RecyclerView.Adapter<TitleAdapter.TitleViewHolder> {
    private String TAG = "TitleAdapter";
    private List<ClassifySeletorItem> list;
    private Context context;
    private TitleAdapter.OnItemClickListener onItemClickListener;
    private RecyclerView recyclerView;

    public TitleAdapter(Context context, RecyclerView recyclerView) {
        this.context = context;
        this.recyclerView = recyclerView;
        list = new ArrayList<>();
    }

    //添加一个标题进来
    public void push(ClassifySeletorItem item) {

        list.add(item);
        notifyDataSetChanged();
        //自动滚动
        recyclerView.smoothScrollToPosition(getItemCount() - 1);
    }

    //返回上一级/删除一个标题
    public void pop() {
        list.remove(list.size() - 1);
        notifyDataSetChanged();
    }

    /**
     * 删除多个标题
     */
    public void setPage(int position) {
        int size = getItemCount();
        for (int i = position + 1; i < size; i++) {
            ClassifySeletorItem remove = list.remove(getItemCount() - 1);
        }
        notifyDataSetChanged();
    }

    @Override
    public TitleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.customview_cs_title_item, null);
        return new TitleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TitleViewHolder holder, final int position) {
        if (0 == position) {
            //开头的 全部分类
            holder.ivArrow.setVisibility(View.GONE);
            holder.tvText.setText(list.get(0).getName());
            //全部分类的时候，显示灰色
            if (list.size() == 1) {
                holder.tvText.setTextColor(Color.parseColor("#666666"));
            } else {
                holder.tvText.setTextColor(Color.parseColor("#2c9dfc"));
            }
            //第一个标题，左边要加padding
            holder.tvText.setPadding(20, holder.tvText.getPaddingTop(), holder.tvText.getPaddingRight(), holder.tvText.getPaddingBottom());
        } else if (position + 1 == list.size()) {
            //最后一个，去掉箭头
            holder.ivArrow.setVisibility(View.VISIBLE);
            holder.tvText.setText(list.get(position).getName());
            holder.tvText.setTextColor(Color.parseColor("#666666"));
        } else {
            //中间的正常状态
            holder.ivArrow.setVisibility(View.VISIBLE);
            holder.tvText.setText(list.get(position).getName());
            holder.tvText.setTextColor(Color.parseColor("#2c9dfc"));
        }
        //点击事件，最后一个只是显示，不设点击事件
        if (position + 1 == getItemCount()) {
            holder.itemView.setOnClickListener(null);
        } else {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //把点击委托出去,只有当响应了点击，返回true才消除标题
                    if (onItemClickListener.click(holder, position, list.get(position))) {
                        setPage(position);
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class TitleViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivArrow;
        public TextView tvText;
        public View itemView;

        public TitleViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ivArrow = (ImageView) itemView.findViewById(R.id.customview_cs_rv_title_iv);
            tvText = (TextView) itemView.findViewById(R.id.customview_cs_rv_title_tv);
        }
    }

    public TitleAdapter.OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(TitleAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        /**
         * 标题被点击了
         *
         * @return 如果被成功处理了点击时间，就返true，否则返回false。避免动画过程中，点击了标题，标题消失，但是页面没有跟上
         */
        boolean click(TitleAdapter.TitleViewHolder holder, int position, ClassifySeletorItem item);
    }
}
