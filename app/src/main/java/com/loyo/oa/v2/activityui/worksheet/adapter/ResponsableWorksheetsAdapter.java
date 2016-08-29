package com.loyo.oa.v2.activityui.worksheet.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.worksheet.bean.Worksheet;
import com.loyo.oa.v2.activityui.worksheet.common.GroupsData;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.tool.ViewHolder;

/**
 * Created by EthanGong on 16/8/27.
 */
public class ResponsableWorksheetsAdapter extends BaseGroupsDataAdapter {
    public ResponsableWorksheetsAdapter(final Context context, final GroupsData data) {
        super();
        mContext = context;
        groupsData = data;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_worksheet_event_wrapper, null, false);
        }

        Worksheet ws = (Worksheet) getChild(groupPosition, childPosition);
        return convertView;
    }
}
