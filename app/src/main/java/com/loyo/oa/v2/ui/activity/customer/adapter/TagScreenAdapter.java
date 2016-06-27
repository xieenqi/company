package com.loyo.oa.v2.ui.activity.customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.Tag;
import java.util.ArrayList;

/**
 * 【客户列表】 筛选标签Adapter
 * Created by yyy on 16/5/18.
 */
public class TagScreenAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Tag> data;
    private Tag tag;
    private int selectPosition;
    private int page;

    public TagScreenAdapter(Context context, final ArrayList<Tag> data, final int page){
        this.mContext = context;
        this.data = data;
        this.page = page;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void selectPosition(final int position){
        selectPosition = position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        tag = data.get(position);
        ViewHolder holder = null;
        if(null == convertView){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.menu_list_item,null);
            holder.name = (TextView) convertView.findViewById(R.id.tv_menu_item);
            holder.index = (ImageView) convertView.findViewById(R.id.iv_menu_select);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        if(page == 1){
            if(selectPosition == position){
                convertView.setBackgroundColor(mContext.getResources().getColor(R.color.ececec));
            }else{
                convertView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            }
        }else{
            holder.index.setVisibility(tag.isIndex() ? View.VISIBLE:View.GONE);
        }

        holder.name.setText(tag.getName());
        return convertView;
    }

    class ViewHolder{
        TextView name;
        ImageView index;
    }
}
