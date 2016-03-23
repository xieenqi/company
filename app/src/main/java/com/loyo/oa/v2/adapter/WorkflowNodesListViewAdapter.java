package com.loyo.oa.v2.adapter;


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
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.customview.pullToRefresh.internal.LoadingLayout;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Administrator on 2014/12/11 0011.
 */
public class WorkflowNodesListViewAdapter extends BaseAdapter {
    private MainApp app;
    private LayoutInflater mInflater;
    private ArrayList<WfNodes> lstData;
    private int wfInstanceStatus;
    private int serverTime;

    public WorkflowNodesListViewAdapter(int _wfInstanceStatus, ArrayList<WfNodes> lstData, LayoutInflater _mInflater,int serverTime) {
        mInflater = _mInflater;
        wfInstanceStatus = _wfInstanceStatus;
        app = MainApp.getMainApp();
        this.lstData = lstData;
        this.serverTime = serverTime;
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
            item_info.tv_startTime = (TextView) convertView.findViewById(R.id.tv_startTime);
            item_info.tv_endTime = (TextView) convertView.findViewById(R.id.tv_endTime);
            item_info.tv_redTime = (TextView) convertView.findViewById(R.id.tv_redTime);

            convertView.setTag(item_info);
        } else {
            item_info = (Item_info) convertView.getTag();
        }

        WfNodes wfNodes = lstData.get(position);
        StringBuffer stringBuffer = new StringBuffer();
        if (wfNodes != null) {
            if (wfNodes.getExecutorUser() != null) {
                item_info.tv_name.setText(wfNodes.getExecutorUser().getRealname());
                try {
                    item_info.tv_creator_title.setText(Utils.getDeptName(stringBuffer,wfNodes.getExecutorUser().getDepts()));
                } catch (Exception ex) {
                    item_info.tv_creator_title.setText("");
                    Global.ProcException(ex);
                }
            }

            /**是否限时，是否超时*/
            if(wfNodes.getRemindAt() != 0){
                if((wfNodes.getRemindAt()*3600)+wfNodes.getHandAt() > serverTime){
                    int totalTime = (wfNodes.getRemindAt()*3600)+wfNodes.getHandAt();
                    LogUtil.dll("审批时间:"+totalTime);
                    LogUtil.dll("服务器时间:"+serverTime);
                    item_info.tv_startTime.setText("(限"+wfNodes.getRemindAt()+"小时,");
                    item_info.tv_redTime.setText("已超时");
                    item_info.tv_endTime.setText(")");
                }else{
                    item_info.tv_startTime.setText("(限"+wfNodes.getRemindAt()+"小时)");
                    item_info.tv_redTime.setVisibility(View.GONE);
                    item_info.tv_endTime.setVisibility(View.GONE);
                }
            }

            /*已通过*/
            if (wfInstanceStatus == 4) {
                item_info.img_left.setImageResource(R.drawable.img_wfinstance_agree);
                item_info.tv_result.setText("同意");
                item_info.tv_result.setTextColor(convertView.getResources().getColor(R.color.wfinstance_agree));
                item_info.tv_comment.setVisibility(View.VISIBLE);
                item_info.tv_comment.setText("处理意见:" + wfNodes.getComment());
                item_info.tv_deal_time.setVisibility(View.VISIBLE);
                item_info.tv_deal_time.setText(DateTool.timet(wfNodes.getUpdateAt()+"",DateTool.DATE_FORMATE_SPLITE_BY_POINT));
            } else {
                if (wfNodes.getActive() == 1) {
                    item_info.img_left.setImageResource(R.drawable.img_wfinstance_wait);
                    item_info.tv_result.setText("待处理");
                    item_info.tv_comment.setVisibility(View.GONE);
                    item_info.tv_result.setTextColor(convertView.getResources().getColor(R.color.wfinstance_notprocess));
                } else if (wfNodes.getActive() == 2) {
                    item_info.img_left.setImageResource(R.drawable.img_wfinstance_wait);
                    item_info.tv_result.setText("处理中");
                    item_info.tv_comment.setVisibility(View.GONE);
                    item_info.tv_result.setTextColor(convertView.getResources().getColor(R.color.wfinstance_notprocess));
                } else if (wfNodes.getActive() == 3) {
                    if (wfNodes.isApproveFlag()) {
                        item_info.img_left.setImageResource(R.drawable.img_wfinstance_agree);
                        item_info.tv_result.setText("同意");
                        item_info.tv_result.setTextColor(convertView.getResources().getColor(R.color.wfinstance_agree));
                        item_info.tv_comment.setVisibility(View.VISIBLE);
                        item_info.tv_comment.setText("处理意见:" + wfNodes.getComment());
                        item_info.tv_deal_time.setVisibility(View.VISIBLE);
                        item_info.tv_deal_time.setText(DateTool.timet(wfNodes.getUpdateAt()+"",DateTool.DATE_FORMATE_SPLITE_BY_POINT));
                    } else {
                        item_info.img_left.setImageResource(R.drawable.img_wfinstance_notagree);
                        item_info.tv_result.setTextColor(convertView.getResources().getColor(R.color.wfinstance_notagree));
                        item_info.tv_result.setText("驳回");
                        item_info.tv_comment.setVisibility(View.VISIBLE);
                        item_info.tv_comment.setText("处理意见:" + wfNodes.getComment());
                        item_info.tv_deal_time.setVisibility(View.VISIBLE);
                        item_info.tv_deal_time.setText(DateTool.timet(wfNodes.getUpdateAt()+"",DateTool.DATE_FORMATE_SPLITE_BY_POINT));
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
        public TextView tv_startTime;
        public TextView tv_redTime;
        public TextView tv_endTime;
    }
}
