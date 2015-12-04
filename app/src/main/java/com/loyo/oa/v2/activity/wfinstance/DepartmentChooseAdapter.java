package com.loyo.oa.v2.activity.wfinstance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.UserInfo;

import java.util.List;

/**
 * 部门选择 页面的适配器
 * Created by xnq on 15/12/3.
 */
public class DepartmentChooseAdapter extends BaseAdapter {
    private Context context;
    private List<UserInfo> data;

    DepartmentChooseAdapter(Context context,List<UserInfo> data) {
        this.context = context;
        this.data = data;
        System.err.print("牡丹园法国人：" + data.size());
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

//    public void setData(List<UserInfo> data) {
//        this.data = data;
//        notifyDataSetChanged();
//    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HoloderView holoder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_two_stair_menu_item,null);
            holoder=new HoloderView(convertView);
            convertView.setTag(holoder);
        } else {
            holoder=(HoloderView)convertView.getTag();
        }
        holoder.setContent(position);

        return convertView;
    }

    class HoloderView {

        TextView text;
        HoloderView(View view){
            text= (TextView)view.findViewById(R.id.text);
        }
        public void setContent(int position ){
            text.setText(data.get(position).getShortDept().getName());
            //System.err.print("牡丹园法国人：" + data.get(position).getShortDept().getName());
        }
    }
}
