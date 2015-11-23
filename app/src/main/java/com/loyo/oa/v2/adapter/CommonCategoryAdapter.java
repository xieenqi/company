package com.loyo.oa.v2.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.tool.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * com.loyo.oa.v2.adapter
 * 描述 :
 * 作者 : ykb
 * 时间 : 15/9/14.
 */
public class CommonCategoryAdapter extends BaseAdapter {


    private LayoutInflater layoutInflater;
    public List<String> lstData=new ArrayList<>();
    private int selectPosition = -1;

    public CommonCategoryAdapter(Context context, List<String> lstData) {
        this.lstData = lstData;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setSelectPosition(int position) {
        selectPosition = position;
    }

    @Override
    public int getCount() {
        return null == lstData ? 0 : lstData.size();
    }

    @Override
    public Object getItem(int position) {

        return null == lstData || lstData.isEmpty() ? null : lstData.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_wf_category_layout, parent, false);
        }
        TextView tv = ViewHolder.get(convertView, R.id.item_wf_category_tv);

        String content = lstData.get(position);
        if (!TextUtils.isEmpty(content)) {
            tv.setText(content);
        }
        if (position == 0) {
            convertView.setBackgroundResource(R.drawable.item_bg_top);
        } else if (position == lstData.size() - 1) {
            convertView.setBackgroundResource(R.drawable.item_bg_buttom);
        } else {
            convertView.setBackgroundColor(convertView.getResources().getColor(R.color.white));
        }
        return convertView;
    }

}
