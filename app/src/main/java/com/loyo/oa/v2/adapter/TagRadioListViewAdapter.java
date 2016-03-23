package com.loyo.oa.v2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.Tag;
import com.loyo.oa.v2.beans.TagItem;

import java.util.ArrayList;

public class TagRadioListViewAdapter extends BaseAdapter {

    Context context;
    LayoutInflater mInflater;
    ArrayList<Tag> lstData;
    Item_info item_info;

    public TagRadioListViewAdapter(final Context context, final ArrayList<Tag> lstData) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.lstData = lstData;
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
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            item_info = new Item_info();
            convertView = mInflater.inflate(R.layout.item_tag_listview, null);
            item_info.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            item_info.gridView = (GridView) convertView.findViewById(R.id.gridView);

            convertView.setTag(item_info);
        } else {
            item_info = (Item_info) convertView.getTag();
        }

        Tag tag = lstData.get(position);
        if (tag != null) {
            item_info.tv_title.setText(tag.getName());
            ArrayList<TagItem> lstDate_TagItem = tag.getItems();
            if (tag.getItems() == null) {
                lstDate_TagItem = new ArrayList<>();

            }
//            for (int i = 0; i < lstDate_TagItem.size(); i++) {
//                if (lstDate_TagItem.get(i).getIsDefault()) {
//                    lstData.get(position).setSelectedId(lstDate_TagItem.get(i).getId());
//                    break;
//                }
//            }

            TagItemRadioGridViewAdapter tagItemRadioGridViewAdapter = new TagItemRadioGridViewAdapter(context, lstDate_TagItem);
            item_info.gridView.setAdapter(tagItemRadioGridViewAdapter);
            item_info.gridView.setOnItemClickListener(new GridViewOnItemClickListener(position, tagItemRadioGridViewAdapter));
        }


        return convertView;
    }

    class GridViewOnItemClickListener implements AdapterView.OnItemClickListener {
        int position_group;
        TagItemRadioGridViewAdapter tagItemRadioGridViewAdapter;

        GridViewOnItemClickListener(final int position_group, final TagItemRadioGridViewAdapter tagItemRadioGridViewAdapter) {
            this.position_group = position_group;
            this.tagItemRadioGridViewAdapter = tagItemRadioGridViewAdapter;
        }

        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {

            TagItemRadioGridViewAdapter.Item_info item_info = (TagItemRadioGridViewAdapter.Item_info) view.getTag();
            item_info.img.setImageResource(R.drawable.img_check1);

            if (tagItemRadioGridViewAdapter.img_last != null) {
                tagItemRadioGridViewAdapter.img_last.setImageResource(R.drawable.radio_cancel1);
            }

            if (tagItemRadioGridViewAdapter.img_last == item_info.img) {
                tagItemRadioGridViewAdapter.img_last = null;

                tagItemRadioGridViewAdapter.isSelected = 0;
//                lstData.get(position_group).setSelectedId(-1);
//                lstData.get(position_group).setSelectedTagItemPosition(0);

            } else {
                tagItemRadioGridViewAdapter.img_last = item_info.img;

                tagItemRadioGridViewAdapter.isSelected = position;
//                long selectedId = tagItemRadioGridViewAdapter.lstData.get(position).getId();
//                lstData.get(position_group).setSelectedId(selectedId);
//                lstData.get(position_group).setSelectedTagItemPosition(position);
            }
        }
    }

    public final class Item_info {
        public TextView tv_title;
        public GridView gridView;
    }
}
