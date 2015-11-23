package com.loyo.oa.v2.adapter;

import android.content.Context;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Discussion;
import com.loyo.oa.v2.tool.CommonAdapter.CommonAdapter;
import com.loyo.oa.v2.tool.CommonAdapter.ViewHolder;

import java.util.ArrayList;
import java.util.Date;

public class DiscussionAdapter extends CommonAdapter<Discussion> {

    MainApp app;

    public DiscussionAdapter(Context context, ArrayList<Discussion> discussions) {
        super(context, discussions, R.layout.item_listview_discussion);
        app = MainApp.getMainApp();
    }

    @Override
    public void convert(ViewHolder holder, Discussion discussion) {
        holder.setText(R.id.tv_creator, discussion.getCreator().getName())
                .setText(R.id.tv_create_time, app.df3.format(new Date(discussion.getCreatedAt()*1000))).setText(R.id.tv_comment, discussion.getContent())
                .setImageUri(R.id.img_disscution_creator, discussion.getCreator().getAvatar(), R.drawable.img_default_user);
    }
}