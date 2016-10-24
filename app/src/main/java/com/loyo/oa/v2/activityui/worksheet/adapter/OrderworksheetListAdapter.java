package com.loyo.oa.v2.activityui.worksheet.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.worksheet.OrderWorksheetListActivity;
import com.loyo.oa.v2.activityui.worksheet.bean.OrderWorksheetListModel;

import java.util.ArrayList;

/**
 * Created by yyy on 16/10/22.
 */

public class OrderworksheetListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<OrderWorksheetListModel> mData;
    public OrderWorksheetListActivity crolView;

    public OrderworksheetListAdapter(Context mContext, ArrayList<OrderWorksheetListModel> mData, OrderWorksheetListActivity crolView){
        this.mContext = mContext;
        this.mData    = mData;
        this.crolView = crolView;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        OrderWorksheetListModel mOrderWorksheet = mData.get(position);

        if(null == convertView){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_worksheet_list,null);
            holder = new ViewHolder();
            holder.tv_worksheet_title = (TextView) convertView.findViewById(R.id.tv_worksheet_title);
            holder.tv_worksheet_kind  = (TextView) convertView.findViewById(R.id.tv_worksheet_kind);
            holder.tv_worksheet_memo  = (TextView) convertView.findViewById(R.id.tv_worksheet_memo);
            holder.tv_delete  = (TextView) convertView.findViewById(R.id.tv_delete);
            holder.tv_edit    = (TextView) convertView.findViewById(R.id.tv_edit);
            holder.tv_source  = (TextView) convertView.findViewById(R.id.tv_source);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        holder.tv_worksheet_title.setText(mOrderWorksheet.title);
        holder.tv_worksheet_kind.setText(mOrderWorksheet.templateName);
        holder.tv_worksheet_memo.setText(mOrderWorksheet.content);
        if(mOrderWorksheet.size != 0){
            holder.tv_source.setText("附件("+mOrderWorksheet.size+")");
        }else{
            holder.tv_source.setText("附件");
        }

        /*删除*/
        holder.tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crolView.deleteWorkSheet(position);
            }
        });

        /*编辑*/
        holder.tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crolView.editWorkSheet(position);
            }
        });

        return convertView;
    }

    class ViewHolder{
        TextView tv_worksheet_title; //工单标题
        TextView tv_worksheet_kind;  //工单类型
        TextView tv_worksheet_memo;  //备注
        TextView tv_source;          //附件
        TextView tv_delete;          //删除
        TextView tv_edit;            //编辑
    }
}
