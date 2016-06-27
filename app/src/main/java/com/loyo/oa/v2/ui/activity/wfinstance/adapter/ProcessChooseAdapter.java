package com.loyo.oa.v2.ui.activity.wfinstance.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.WfTemplate;

import java.util.ArrayList;

/**
 * 【流程选择】 页面的适配器
 * Created by yyy on 16/5/18.
 */
public class ProcessChooseAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<WfTemplate> data;

    public ProcessChooseAdapter(final Context context, final ArrayList<WfTemplate> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return null == data ? 0 : data.size();
    }

    @Override
    public Object getItem(final int position) {
        return position;
    }

    public ArrayList<WfTemplate> getData() {
        return data;
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        HoloderView holoder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_two_stair_menu_item, null);
            holoder = new HoloderView(convertView);
            convertView.setTag(holoder);
        } else {
            holoder = (HoloderView) convertView.getTag();
        }
        holoder.setContent(position);

        return convertView;
    }

    class HoloderView {

        TextView text;

        HoloderView(final View view) {
            text = (TextView) view.findViewById(R.id.text);
        }

        public void setContent(final int position) {
            text.setText(data.get(position).getTitle());
        }
    }
}
