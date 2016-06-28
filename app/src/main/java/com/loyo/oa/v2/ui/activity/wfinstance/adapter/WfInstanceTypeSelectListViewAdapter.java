package com.loyo.oa.v2.ui.activity.wfinstance.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.ui.activity.wfinstance.bean.BizForm;

import java.util.ArrayList;
/** xnq
 * 【审批 类型 选择】adapter
 */
public class WfInstanceTypeSelectListViewAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    public ArrayList<BizForm> lstData;
    private Item_info item_info;
    private boolean showArrow = true;

    public WfInstanceTypeSelectListViewAdapter(Context context, ArrayList<BizForm> lstData) {
        this.lstData = lstData;
        layoutInflater = LayoutInflater.from(context);

    }

    public WfInstanceTypeSelectListViewAdapter(Context context, ArrayList<BizForm> lstData, boolean isShowArrow) {
        this(context, lstData);
        showArrow = isShowArrow;
    }

    @Override
    public int getCount() {
        return lstData.size();
    }

    @Override
    public Object getItem(int position) {

        return lstData.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    /**外部获取list数据
     * xnq
     * @return
     */
    public ArrayList<BizForm> getData(){
        return lstData;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_listview_bizform, null);
            item_info = new Item_info();
            item_info.tv = (TextView) convertView.findViewById(R.id.tv);
            item_info.img_right = (ImageView) convertView.findViewById(R.id.img_right);
            convertView.setTag(item_info);
        } else {
            item_info = (Item_info) convertView.getTag();
        }
        item_info.img_right.setVisibility(showArrow ? View.VISIBLE : View.GONE);

        BizForm bizForm = lstData.get(position);
        if (bizForm != null) {
            item_info.tv.setText(bizForm.getName());
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

    class Item_info {
        TextView tv;
        ImageView img_right;
    }
}
