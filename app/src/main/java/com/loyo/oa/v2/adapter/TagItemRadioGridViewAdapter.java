package com.loyo.oa.v2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.TagItem;

import java.util.ArrayList;

/**
 * Created by Administrator on 2014/11/1 0001.
 */
public class TagItemRadioGridViewAdapter extends BaseAdapter {
    //    private MainApp mainApplication;
    private LayoutInflater layoutInflater;
    public ArrayList<TagItem> lstData;
    private Item_info item_info;
    //    private Context context;
    public long isSelected = -1;
    public ImageView img_last;

    public TagItemRadioGridViewAdapter(final Context context,
                                       final ArrayList<TagItem> lstData) {
//        mainApplication = MainApp.getMainApp();
//        this.context = context;
        this.lstData = lstData;

        layoutInflater = LayoutInflater.from(context);
//        for (int i = 0; i > lstData.size(); i++) {
//            if (lstData.get(i).getIsDefault()) {
//                isSelected = i;
//                break;
//            }
//        }
    }

    @Override
    public int getCount() {
        return lstData.size();
    }

    @Override
    public Object getItem(final int position) {
        return lstData.get(position);
    }

    @Override
    public long getItemId(final int position) {

        return lstData.size();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_gridview_item_tag, null);
            item_info = new Item_info();
            item_info.img = (ImageView) convertView
                    .findViewById(R.id.img);
            item_info.tv = (TextView) convertView
                    .findViewById(R.id.tv);
            convertView.setTag(item_info);
        } else {
            item_info = (Item_info) convertView.getTag();
        }
        TagItem tagItem = lstData.get(position);
        if (tagItem != null) {
            item_info.tv.setText(tagItem.getName());
        }
        if (isSelected == position) {
            item_info.img.setImageResource(R.drawable.img_check1);
            if (img_last == null) {
                img_last = item_info.img;
            }
        } else {
            item_info.img.setImageResource(R.drawable.radio_cancel1);
        }

        return convertView;
    }


    public class Item_info {
        ImageView img;
        TextView tv;
    }

}
