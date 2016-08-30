package com.loyo.oa.v2.activityui.worksheet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.worksheet.bean.WorkSheet;
import com.loyo.oa.v2.activityui.worksheet.common.GroupsData;

/**
 * Created by EthanGong on 16/8/27.
 */
public class WorksheetListAdapter extends BaseGroupsDataAdapter {

    public WorksheetListAdapter(final Context context, final GroupsData data) {
        super();
        mContext = context;
        groupsData = data;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_worksheet, null, false);
        }

        WorkSheet ws = (WorkSheet) getChild(groupPosition, childPosition);
        return convertView;
    }
}
