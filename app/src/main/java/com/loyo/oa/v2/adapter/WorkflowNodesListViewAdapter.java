package com.loyo.oa.v2.adapter;


import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.WfNodes;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.LogUtil;
import java.util.ArrayList;

/**
 * Created by Administrator on 2014/12/11 0011.
 */
public class WorkflowNodesListViewAdapter extends BaseAdapter {
    MainApp app;
    LayoutInflater mInflater;
    ArrayList<WfNodes> lstData;
    int wfInstanceStatus;

    public WorkflowNodesListViewAdapter(int _wfInstanceStatus, ArrayList<WfNodes> lstData, LayoutInflater _mInflater) {
        mInflater = _mInflater;
        wfInstanceStatus = _wfInstanceStatus;
        app = MainApp.getMainApp();
        this.lstData = lstData;
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Item_info item_info;
        //convertView为null的时候初始化convertView。
        if (convertView == null) {
            item_info = new Item_info();
            convertView = mInflater.inflate(R.layout.item_workflownodes, null);
            item_info.img_left = (ImageView) convertView.findViewById(R.id.img_left);
            item_info.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            item_info.tv_deal_time = (TextView) convertView.findViewById(R.id.tv_deal_time);
            item_info.tv_comment = (TextView) convertView.findViewById(R.id.tv_comment);
            item_info.tv_creator_title = (TextView) convertView.findViewById(R.id.tv_creator_title);
            item_info.tv_result = (TextView) convertView.findViewById(R.id.tv_result);
            convertView.setTag(item_info);
        } else {
            item_info = (Item_info) convertView.getTag();
        }

        WfNodes wfNodes = lstData.get(position);
        if (wfNodes != null) {
            if (wfNodes.getExecutorUser() != null) {
                item_info.tv_name.setText(wfNodes.getExecutorUser().getRealname());
                try {
                    item_info.tv_creator_title.setText(wfNodes.getExecutorUser().shortPosition.getName());
                } catch (Exception ex) {
                    item_info.tv_creator_title.setText("");
                    Global.ProcException(ex);
                }
            }

            item_info.tv_result.setTextSize(TypedValue.COMPLEX_UNIT_SP, app.pxTosp(app.diptoPx(20)));

            /*已通过*/
           if(wfInstanceStatus == 4){
               item_info.img_left.setImageResource(R.drawable.img_wfinstance_agree);
               item_info.tv_result.setText("同意");
           }
          else{
               if(wfNodes.getActive() == 1){
                   item_info.img_left.setImageResource(R.drawable.img_wfinstance_wait);
                   item_info.tv_deal_time.setText("");
                   item_info.tv_result.setText("待处理");
                   item_info.tv_comment.setVisibility(View.GONE);
                   item_info.tv_result.setTextColor(convertView.getResources().getColor(R.color.wfinstance_notprocess));
               }

               else if(wfNodes.getActive() == 2){
                   item_info.img_left.setImageResource(R.drawable.img_wfinstance_wait);
                   item_info.tv_deal_time.setText("");
                   item_info.tv_result.setText("处理中");
                   item_info.tv_comment.setVisibility(View.GONE);
                   item_info.tv_result.setTextColor(convertView.getResources().getColor(R.color.wfinstance_notprocess));
               }

               else if(wfNodes.getActive() == 3){
                   if (wfNodes.isApproveFlag() == true) {
                       item_info.img_left.setImageResource(R.drawable.img_wfinstance_agree);
                       item_info.tv_result.setText("同意");
                       item_info.tv_comment.setVisibility(View.VISIBLE);
                       item_info.tv_comment.setText("处理意见:"+wfNodes.getComment());
                   } else {
                       item_info.img_left.setImageResource(R.drawable.img_wfinstance_notagree);
                       item_info.tv_result.setTextColor(convertView.getResources().getColor(R.color.wfinstance_notagree));
                       item_info.tv_result.setText("驳回");
                       item_info.tv_comment.setVisibility(View.VISIBLE);
                       item_info.tv_comment.setText("处理意见:"+wfNodes.getComment());
                   }
               }
           }
        }
        return convertView;
    }

    public class Item_info {
        public ImageView img_left;
        public TextView tv_deal_time;
        public TextView tv_creator_title;
        public TextView tv_name;
        public TextView tv_comment;
        public TextView tv_result;
    }
}
