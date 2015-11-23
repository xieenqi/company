package com.loyo.oa.v2.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBeans;
import com.loyo.oa.v2.beans.PagingGroupData;

import java.util.ArrayList;

public class WorkReportsExpandableListAdapter<T extends BaseBeans> extends BasePagingGroupDataAdapter<T> {

    public WorkReportsExpandableListAdapter(Context context, ArrayList<PagingGroupData<T>> data) {
        mContext = context;
        pagingGroupDatas = data;
        app = (MainApp) mContext.getApplicationContext();
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return null;
    }


    //    @Override
//    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
//        if (convertView == null) {
//            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_listview_workreport_manage, null);
//            item_info_Child = new Item_info_Child();
//
//            item_info_Child.imgStatus = (ImageView) convertView.findViewById(R.id.img_workreport_status);
//            item_info_Child.view_ack = convertView.findViewById(R.id.view_ack);
//            item_info_Child.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
//            item_info_Child.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
//            item_info_Child.tvCreateTime = (TextView) convertView.findViewById(R.id.tv_create_time);
//
//            convertView.setTag(item_info_Child);
//        } else {
//            item_info_Child = (Item_info_Child) convertView.getTag();
//        }
//
//        WorkReport workReport = (WorkReport) getChild(groupPosition, childPosition);
//
//        if (workReport != null) {
//            if (workReport.getTitle() != null) {
//                item_info_Child.tvTitle.setText(workReport.getTitle());
//            }
//
//            if (workReport.getCreatedAt() != null) {
//                try {
//                    item_info_Child.tvCreateTime.setText(DateTool.getDate(workReport.getCreatedAt(), app.df_api_get, app.df6));
//                } catch (Exception e) {
//                    Global.ProcException(e);
//                }
//            }
//
//            if (workReport.getBeginAt() != null && workReport.getEndAt() != null) {
//                try {
//                    String begin = DateTool.getDate(workReport.getBeginAt(), app.df_api_get, app.df4);
//                    String end = DateTool.getDate(workReport.getEndAt(), app.df_api_get, app.df4);
//                    switch (workReport.getType()){
//                        case WorkReport.DAY:
//                            item_info_Child.tvTime.setText(begin);
//                            break;
//                        case WorkReport.WEEK:
//                        case WorkReport.MONTH:
//                            item_info_Child.tvTime.setText(String.format("%s - %s", begin, end));
//                            break;
//                    }
//                } catch (Exception e) {
//                    Global.ProcException(e);
//                }
//            }
//
//            item_info_Child.view_ack.setVisibility(workReport.isAck() ? View.GONE : View.VISIBLE);
//            item_info_Child.imgStatus.setImageResource(
//                    workReport.isViewed() ? R.drawable.img_workreport_list_status2 :
//                            R.drawable.img_workreport_list_status1
//            );
//        }
//
//        return convertView;
//    }
//
//    Item_info_Child item_info_Child;
//
//    class Item_info_Child {
//        View view_ack;
//        ImageView imgStatus;
//        TextView tvTitle;
//        TextView tvTime;
//        TextView tvCreateTime;
//    }
}
