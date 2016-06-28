package com.loyo.oa.v2.ui.activity.wfinstance.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.ui.activity.wfinstance.bean.BizFormFields;
import com.loyo.oa.v2.common.Global;

import java.util.ArrayList;
import java.util.HashMap;

public class WfInstanceValuesListViewAdapter extends BaseAdapter {


    public ArrayList<HashMap<String,Object>> mDatas;
    public ArrayList<BizFormFieldsListViewAdapter>  childs=new ArrayList<BizFormFieldsListViewAdapter>();
    LayoutInflater mLayoutInflater;
    ArrayList<BizFormFields> mFields;
    IDeleteItem mDeleteItem;

    public WfInstanceValuesListViewAdapter(Context context, ArrayList<HashMap<String,Object>> datas
            , ArrayList<BizFormFields> _mFields, IDeleteItem deleteItem) {
        mDatas=datas;
        if(null==mDatas)
            mDatas=new ArrayList<>();
        mLayoutInflater = LayoutInflater.from(context);
        if (_mFields != null) {
            mFields = _mFields;
        }
        mDeleteItem = deleteItem;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public HashMap<String,Object> getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Item_info item_info;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_listview_wfinstancevalues, null);
            item_info = new Item_info();
            item_info.layout_wfInstanceValuesDatas = (ViewGroup) convertView.findViewById(R.id.layout_wfInstanceValuesDatas);
            item_info.layout_delete = (ViewGroup) convertView.findViewById(R.id.layout_delete);
            item_info.tv_title = (TextView) convertView.findViewById(R.id.tv_title);


            convertView.setTag(item_info);
        } else {
            item_info = (Item_info) convertView.getTag();
        }

        if(0==position)
            convertView.findViewById(R.id.layout_delete_).setVisibility(View.GONE);
//        item_info.layout_wfInstanceValuesDatas.removeAllViews();

        if (mDeleteItem != null) {
            item_info.layout_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDeleteItem.deleteItem(position);
                }
            });
        } else {
            item_info.layout_delete.setVisibility(View.GONE);
        }

        item_info.tv_title.setText("新增内容" + (position));

        HashMap<String, Object> tempData = getItem(position);

        if(0>=item_info.layout_wfInstanceValuesDatas.getChildCount()) {
            View view_value = mLayoutInflater.inflate(R.layout.item_wfinstance_child_layout, item_info.layout_wfInstanceValuesDatas, true);
            BizFormFieldsListViewAdapter adapter = new BizFormFieldsListViewAdapter(convertView.getContext(), mFields);
            adapter.map_Values = tempData;
            item_info.itemLvAdapter=adapter;
            ListView lv = (ListView) item_info.layout_wfInstanceValuesDatas.findViewById(R.id.listView_add);
            lv.setAdapter(adapter);
            Global.setListViewHeightBasedOnChildren(lv);
        }
        else {
            item_info.itemLvAdapter.map_Values = tempData;
            item_info.itemLvAdapter.notifyDataSetChanged();
        }
        return convertView;
    }

    private class Item_info {
        BizFormFieldsListViewAdapter itemLvAdapter;
        ViewGroup layout_wfInstanceValuesDatas;
        ViewGroup layout_delete;
        TextView tv_title;
    }

    public interface IDeleteItem {
        void deleteItem(int position);
    }

}
