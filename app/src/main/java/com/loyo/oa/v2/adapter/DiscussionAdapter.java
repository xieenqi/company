package com.loyo.oa.v2.adapter;

import android.content.Context;
import android.view.View;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Discussion;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.tool.HaitHelper;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.TimeFormatUtil;
import com.loyo.oa.v2.tool.commonadapter.CommonAdapter;
import com.loyo.oa.v2.tool.commonadapter.ViewHolder;

import java.util.ArrayList;

/**
 * 讨论 界面 的adapter
 */
public class DiscussionAdapter extends CommonAdapter<Discussion> {

    MainApp app;
    private OnSelectUserCallback mOnSelectUserCallback;

    public DiscussionAdapter(final Context context, final ArrayList<Discussion> discussions) {
        super(context, discussions, R.layout.item_listview_discussion);
        app = MainApp.getMainApp();
    }

    @Override
    public void convert(final ViewHolder holder, final Discussion discussion) {
        LogUtil.d(MainApp.gson.toJson(discussion) + " 讨2论的名 " + discussion.getCreator().getRealname());
        holder.setText(R.id.tv_creator, discussion.getCreator().getRealname())
                .setText(R.id.tv_create_time, TimeFormatUtil.toFormat(discussion.getCreatedAt())).setText(R.id.tv_comment, discussion.getContent())
                .setImageUri(R.id.img_disscution_creator, discussion.getCreator().avatar, R.drawable.img_default_user);

        holder.getView(R.id.img_disscution_creator).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                User user = discussion.getCreator();
                HaitHelper.SelectUser selectUser = new HaitHelper.SelectUser(user.getRealname(), user.getId());
                if (null != mOnSelectUserCallback) {
                    mOnSelectUserCallback.onCallback(selectUser);
                }
                return true;
            }
        });
    }

    /**
     * 设置长按图像返回用户监听
     *
     * @param callback
     */
    public void setSelectUserCallback(final OnSelectUserCallback callback) {
        this.mOnSelectUserCallback = callback;
    }

    public interface OnSelectUserCallback {
        void onCallback(HaitHelper.SelectUser user);
    }
}