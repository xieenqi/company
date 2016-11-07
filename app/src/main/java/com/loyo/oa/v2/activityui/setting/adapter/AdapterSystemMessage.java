package com.loyo.oa.v2.activityui.setting.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.setting.bean.SystemMessageItem;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.multi_image_selector.bean.Image;
import com.loyo.oa.v2.tool.DateTool;

import java.util.List;

/**
 * Created by xeq on 16/11/7.
 */

public class AdapterSystemMessage extends BaseAdapter {

    private LayoutInflater inflater;
    private List<SystemMessageItem> data;

    public AdapterSystemMessage(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<SystemMessageItem> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        final SystemMessageItem item = data.get(position);
        if (null == convertView) {
            holder = new Holder();
            convertView = inflater.inflate(R.layout.item_system_message, null);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.setContent(item);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.Toast("跳转到:"+item.bizzType.getItemClass());
            }
        });
        return convertView;
    }

    class Holder {
        TextView tv_title, tv_time;
        ImageView iv_icon;

        public void setContent(SystemMessageItem item) {
            tv_title.setText(item.title);
            tv_time.setText(DateTool.getDiffTime(item.createdAt));
            if (item.bizzType != null) {
                iv_icon.setImageResource(item.bizzType.getIcon());
            }
        }
    }
}
