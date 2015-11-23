package com.loyo.oa.v2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.BaseBeans;
import com.loyo.oa.v2.beans.PagingGroupData;
import com.loyo.oa.v2.beans.WfInstance;

import java.util.ArrayList;
import java.util.Date;

public class WfInstanceExpandableListAdapter<T extends BaseBeans> extends BasePagingGroupDataAdapter<T> {

    public WfInstanceExpandableListAdapter(Context context, ArrayList<PagingGroupData<T>> data) {
        super();
        mContext = context;
        pagingGroupDatas = data;
    }

    Item_info_Child item_info_Child;

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_listview_wfinstance_manage, null);
            item_info_Child = new Item_info_Child();

            item_info_Child.imgType = (ImageView) convertView.findViewById(R.id.img_wfinstance_status);
            item_info_Child.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            item_info_Child.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
            item_info_Child.tvCreateTime = (TextView) convertView.findViewById(R.id.tv_create_time);
            item_info_Child.view_ack = convertView.findViewById(R.id.view_ack);

            convertView.setTag(item_info_Child);
        } else {
            item_info_Child = (Item_info_Child) convertView.getTag();
        }

        WfInstance wfInstance = (WfInstance) getChild(groupPosition, childPosition);

        if (wfInstance != null) {
            if (wfInstance.getTitle() != null) {
                item_info_Child.tvTitle.setText(wfInstance.getTitle());
            }

            item_info_Child.tvCreateTime.setText(app.df3.format(new Date(wfInstance.getCreatedAt()*1000)));

            if (wfInstance.getCreator() != null) {
                item_info_Child.tvContent.setText(String.format("申请人 %s", wfInstance.getCreator().getRealname()));
            }

            item_info_Child.view_ack.setVisibility(wfInstance.isAck() ? View.GONE : View.VISIBLE);

            switch (wfInstance.getStatus()) {
                case WfInstance.STATUS_NEW:
                    item_info_Child.imgType.setImageResource(R.drawable.img_wfinstance_list_status1);
                    break;
                case WfInstance.STATUS_PROCESSING:
                    item_info_Child.imgType.setImageResource(R.drawable.img_wfinstance_list_status2);
                    break;
                case WfInstance.STATUS_ABORT:
                    item_info_Child.imgType.setImageResource(R.drawable.img_wfinstance_list_status3);
                    break;
                case WfInstance.STATUS_APPROVED:
                    item_info_Child.imgType.setImageResource(R.drawable.img_wfinstance_list_status4);
                    break;
                case WfInstance.STATUS_FINISHED:
                    item_info_Child.imgType.setImageResource(R.drawable.img_wfinstance_list_status5);
                    break;
            }
        }

        return convertView;
    }

    class Item_info_Child {
        View view_ack;
        ImageView imgType;
        TextView tvTitle;
        TextView tvContent;
        TextView tvCreateTime;
    }
}
