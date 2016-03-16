package com.loyo.oa.v2.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.tool.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户拜访列表 的adapter  xnq
 * com.loyo.oa.v2.adapter
 */
public class CommonCategoryAdapter extends BaseAdapter {


    private LayoutInflater layoutInflater;
    public List<String> lstData = new ArrayList<>();
    private int selectPosition = -1;

    public CommonCategoryAdapter(final Context context, final List<String> lstData) {
        this.lstData = lstData;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setSelectPosition(final int position) {
        selectPosition = position;
    }

    @Override
    public int getCount() {
        return null == lstData ? 0 : lstData.size();
    }

    @Override
    public Object getItem(final int position) {

        return null == lstData || lstData.isEmpty() ? null : lstData.get(position);
    }

    @Override
    public long getItemId(final int position) {

        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_wf_category_layout, parent, false);
        }
        TextView tv = ViewHolder.get(convertView, R.id.item_wf_category_tv);
        ImageView iv = ViewHolder.get(convertView, R.id.iv_head);
        String content = lstData.get(position);
        if (!TextUtils.isEmpty(content)) {
            tv.setText(content);
        }
        if (position == 0) {
            iv.setBackgroundResource(R.drawable.icon_my);
        } else if (position == 1) {
            if (lstData.get(1).contains("团队")) {
                iv.setBackgroundResource(R.drawable.icon_team);
            } else {
                iv.setBackgroundResource(R.drawable.icon_public);
            }

        } else if (position == 2) {
            iv.setBackgroundResource(R.drawable.icon_public);
        }
        return convertView;
    }

}
