package com.loyo.oa.v2.tool.customview.DropListMenu;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;

import java.util.ArrayList;

public class DropListAdapter extends BaseAdapter {

    ArrayList<DropItem> mItems;
    Context mContext;
    int mSelectIndex = -1, selectItem;

    public DropListAdapter(Context context, ArrayList<DropItem> items) {
        if (items != null) {
            mItems = items;
        } else {
            mItems = new ArrayList<>();
        }

        mContext = context;
    }

    public DropListAdapter(Context context, ArrayList<DropItem> items, int selectIndex) {
        this(context, items);
        mSelectIndex = selectIndex;
    }

    public DropListAdapter(Context context, ArrayList<DropItem> items, DropItem selectItem) {
        this(context, items);

        if (selectItem != null && items != null) {
            for (int i = 0; i < items.size(); i++) {
                if (selectItem.equals(items.get(i))) {
                    mSelectIndex = i;
                }
            }
        }
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null == mItems || mItems.isEmpty() ? null : mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setSelectItem(int items) {
        selectItem = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DropItem item = (DropItem) getItem(position);
        convertView = LayoutInflater.from(mContext).inflate(R.layout.menu_list_item, parent, false);
        TextView textView = (TextView) convertView.findViewById(R.id.tv_menu_item);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setText(null == item ? "" : item.getName());

        if (mSelectIndex == position) {
            ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_menu_select);
            imageView.setVisibility(View.VISIBLE);
        }
/*        if (selectItem == position) {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.gray));
        } else {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }*/
        return convertView;
    }

    public void setSelectIndex(int selectIndex) {
        mSelectIndex = selectIndex;
    }
}
