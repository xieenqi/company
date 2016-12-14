package com.loyo.oa.v2.activityui.other.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.activityui.signin.adapter.SignInGridViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Bulletin;
import com.loyo.oa.v2.customview.CusGridView;
import com.loyo.oa.v2.customview.RoundImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by yyy on 16/10/10.
 */

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.BulletinViewHolder> {

    private ArrayList<Bulletin> mBulletins;
    private Context mContext;
    private Activity mActivity;


    public class BulletinViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_time;
        private TextView tv_title;
        private TextView tv_content;
        private TextView tv_name;
        private RoundImageView iv_avatar;
        private CusGridView gridView;

        public BulletinViewHolder(final View itemView) {
            super(itemView);
            tv_time = (TextView) itemView.findViewById(R.id.tv_notice_time);
            tv_title = (TextView) itemView.findViewById(R.id.tv_notice_title);
            tv_content = (TextView) itemView.findViewById(R.id.tv_notice_content);
            tv_name = (TextView) itemView.findViewById(R.id.tv_notice_publisher);
            iv_avatar = (RoundImageView) itemView.findViewById(R.id.iv_notice_publisher_avatar);
            gridView = (CusGridView) itemView.findViewById(R.id.gv_notice_attachemnts);
        }
    }


    public NoticeAdapter(final ArrayList<Bulletin> bulletins,Context mContext,Activity mActivity) {
        mBulletins = bulletins;
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    public void setmDatas(final ArrayList<Bulletin> bulletins) {
        mBulletins = bulletins;
        notifyDataSetChanged();
    }

    @Override
    public BulletinViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_notice_layout, parent, false);
        return new BulletinViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final BulletinViewHolder holder, final int position) {
        final Bulletin bulletin = mBulletins.get(position);
//        holder.tv_time.setText(MainApp.getMainApp().df3.format(new Date(bulletin.createdAt * 1000)));
        holder.tv_time.setText(DateTool.getDateTimeFriendly(bulletin.createdAt));
        holder.tv_title.setText(bulletin.title);
        holder.tv_content.setText(bulletin.content);

        holder.tv_name.setText(bulletin.getUserName() + " " + (
                creatorIsEmpty(bulletin.creator) ? bulletin.creator.depts.get(0).getShortDept().getName() : "")
                + " " + (creatorIsEmpty(bulletin.creator) ? bulletin.creator.depts.get(0).getShortDept().title : ""));

        ImageLoader.getInstance().displayImage(bulletin.creator.avatar, holder.iv_avatar);
        ArrayList<Attachment> attachments = bulletin.attachments;
        if (null != attachments && !attachments.isEmpty()) {
            holder.gridView.setVisibility(View.VISIBLE);
            SignInGridViewAdapter adapter = new SignInGridViewAdapter(mActivity, attachments, false, true, true, 0);
            SignInGridViewAdapter.setAdapter(holder.gridView, adapter);
        } else {
            holder.gridView.setVisibility(View.GONE);
        }
    }

    /*
    判断创建人部门是数据是否有空
     */
    private boolean creatorIsEmpty(User creator) {
        if (null == creator.depts) {
            return false;
        } else if (0 == creator.depts.size()) {
            return false;
        } else if (TextUtils.isEmpty(creator.depts.get(0).getShortDept().title) ||
                TextUtils.isEmpty(creator.depts.get(0).getShortDept().getName())) {
            return false;
        }
        return true;
    }

    @Override
    public int getItemCount() {
        return mBulletins.size();
    }
}
