package com.loyo.oa.v2.customview.classify_seletor;

import android.content.Context;
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
 * 分类选择器的下面的item的adaper
 * Created by jie on 16/12/29.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private String TAG = "ItemAdapter";
    private List<ClassifySeletorItem> items;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private List<ClassifySeletorItem> status = new ArrayList<>();//保存选中的item
    //设置是否是单选模式
    private  boolean isSingleSelete=false;

    //为了一个蛋疼的跳了页面，还要认为刚才的是选中的
    public static ClassifySeletorItem seletedItem;

    public ItemAdapter(Context context) {
        items = new ArrayList<>();
        this.context = context;
    }


    public void setSingleSelete(boolean singleSelete) {
        isSingleSelete = singleSelete;
    }

    /**
     * 重置数据
     */
    public void reset() {
        status.clear();
        notifyDataSetChanged();
    }

    //设置要显示的数据
    public void setData(List<ClassifySeletorItem> data) {
        items.clear();
        items.addAll(data);
        notifyDataSetChanged();
        status.clear();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.customview_cs_item_item, null);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        holder.textView.setText(items.get(position).getName());
        //如果有下一级，就显示箭头，否则就不显示箭头
        ClassifySeletorItem item=items.get(position);
        //根据点击的item，加载下一页的数据
        if (null==item.getFinal()) {
            //还不知道是不是最后一级,调用判断函数，去判断
            item.setFinal(onItemClickListener.isFinal(item));
        }
        //恢复选中状态
        if (status.contains(items.get(position))) {
            holder.imageView.setSelected(true);
        } else {
            if(seletedItem==item){
                holder.imageView.setSelected(true);

            }else{
                holder.imageView.setSelected(false);
            }
        }

        //根据是不是最后一级，来判断是否显示箭头
        if (item.getFinal()) {
            holder.arrow.setVisibility(View.GONE);
            //如果是最后一级，直接处理选中，不考虑加载下一级
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickSaveStatus(holder,position);
                }
            });
        } else {
            holder.arrow.setVisibility(View.VISIBLE);
            //不是最后一级，先设置图片的点击选中状态
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickSaveStatus(holder,position);
                }
            });
            //不是最后一级，再设置点击item，加载下一级
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != onItemClickListener) {
                        //调用item的点击事件，加载数据
                        onItemClickListener.clickItemToLoadData(holder, position, items.get(position));
                    }
                }
            });
        }
    }

    //用来保存元素的状态
    private void clickSaveStatus(ItemViewHolder holder,int position){
        if(!isSingleSelete){
            if (status.contains(items.get(position))) {
                status.remove(items.get(position));
                holder.imageView.setSelected(false);
            } else {
                status.add(items.get(position));
                holder.imageView.setSelected(true);
            }

        }else{
            if(holder.imageView.isSelected()){
                reset();
                holder.imageView.setSelected(false);
            }else{
                reset();
                status.add(items.get(position));
                holder.imageView.setSelected(true);
            }
        }
        //单选
        onItemClickListener.clickItem(items.get(position));
        seletedItem=items.get(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public View itemView;
        public ImageView arrow;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.customview_cs_item_iv);
            arrow = (ImageView) itemView.findViewById(R.id.customview_cs_rv_title_iv);
            textView = (TextView) itemView.findViewById(R.id.customview_cs_item_tv);
        }
    }

    public List<ClassifySeletorItem> getStatus() {
        return status;
    }

    public void setStatus(List<ClassifySeletorItem> status) {
        this.status = status;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        /**
         * 点击事件
         * @param holder
         * @param position
         * @param item
         */
        void clickItemToLoadData(ItemViewHolder holder, int position, ClassifySeletorItem item);
        /**
         * 判断某个节点是不是最后一级了
         * @param item 节点
         * @return false：不是，true：是
         */
        Boolean isFinal(ClassifySeletorItem item);
        //点击改变状态的时候
        void clickItem(ClassifySeletorItem item);
    }
}
