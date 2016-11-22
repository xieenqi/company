package com.loyo.oa.v2.activityui.other.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.loyo.oa.common.utils.ReusePool;
import com.loyo.oa.photo.PhotoPreview;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.other.PreviewOfficeActivity;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Bulletin;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.RoundImageView;
import com.loyo.oa.v2.tool.LogUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yyy on 16/10/10.
 */

public class NoticeAdapter2 extends RecyclerView.Adapter<NoticeAdapter2.BulletinViewHolder> {

    private ArrayList<Bulletin> mBulletins;
    private Context mContext;
    private Activity mActivity;
    private ReusePool<ViewGroup> pool;


    public class BulletinViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_time;
        private TextView tv_title;
        private TextView tv_content;
        private TextView tv_name;
        private RoundImageView iv_avatar;
        private GridLayout grid;


        public BulletinViewHolder(final View itemView) {
            super(itemView);
            tv_time = (TextView) itemView.findViewById(R.id.tv_notice_time);
            tv_title = (TextView) itemView.findViewById(R.id.tv_notice_title);
            tv_content = (TextView) itemView.findViewById(R.id.tv_notice_content);
            tv_name = (TextView) itemView.findViewById(R.id.tv_notice_publisher);
            iv_avatar = (RoundImageView) itemView.findViewById(R.id.iv_notice_publisher_avatar);
            grid = (GridLayout) itemView.findViewById(R.id.grid);
        }
    }


    public NoticeAdapter2(final ArrayList<Bulletin> bulletins, Context mContext, Activity mActivity) {
        mBulletins = bulletins;
        this.mContext = mContext;
        this.mActivity = mActivity;
        setHasStableIds(true);
        pool = new ReusePool<ViewGroup>();
        pool.setCreator(new ReusePool.ReusableCreator<ViewGroup>(){

            @Override
            public ViewGroup getInstance() {
                return ( ViewGroup) LayoutInflater.from(NoticeAdapter2.this.mContext).inflate(R.layout.item_img_ct_browse, null);
            }
        });
    }

    public void setmDatas(final ArrayList<Bulletin> bulletins) {
        mBulletins = bulletins;
        notifyDataSetChanged();
    }

    @Override
    public BulletinViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_notice_layout2, parent, false);
        return new BulletinViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final BulletinViewHolder holder, final int position) {
        final Bulletin bulletin = mBulletins.get(position);
        holder.tv_time.setText(MainApp.getMainApp().df3.format(new Date(bulletin.createdAt * 1000)));
        holder.tv_title.setText(bulletin.title);
        holder.tv_content.setText(bulletin.content);

        holder.tv_name.setText(bulletin.getUserName() + " " + (
                creatorIsEmpty(bulletin.creator) ? bulletin.creator.depts.get(0).getShortDept().getName() : "")
                + " " + (creatorIsEmpty(bulletin.creator) ? bulletin.creator.depts.get(0).getShortDept().title : ""));

        ImageLoader.getInstance().displayImage(bulletin.creator.avatar, holder.iv_avatar);
        ArrayList<Attachment> attachments = bulletin.attachments;
        if (null != attachments && !attachments.isEmpty()) {
            holder.grid.setVisibility(View.VISIBLE);
            holder.grid.setRowCount(attachments.size()+2/3);
            for (int i = 0, c = 0, r = 0; i < attachments.size(); i++, c++) {
                if (c == 3) {
                    c = 0;
                    r++;
                }
                ViewGroup oImageView = pool.getReusableInstance();
                ImageView imageView = (ImageView) oImageView.findViewById(R.id.imageView);

                final Attachment attachment = attachments.get(i);
                final boolean isImage = (attachment.getAttachmentType() == Attachment.AttachmentType.IMAGE);

                if (isImage) {//预览图片
                    Glide.with(MainApp.getMainApp()).load(attachment.getUrl())
                            .placeholder(R.drawable.default_image)
                            .override(200, 200)
                            .into(imageView);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            ArrayList<String> paths = bulletin.attachmentUrls();
                            int index = paths.indexOf(attachment.getUrl());
                            if (index < 0 ||index >paths.size()) {
                                index = 0;
                            }
                            PhotoPreview.builder()
                                    .setPhotos(paths)
                                    .setCurrentItem(index)
                                    .setShowDeleteButton(false)
                                    .start((Activity) mContext);
                        }
                    });
                } else {
                    Glide.with(MainApp.getMainApp())
                            .load(Global.getAttachmentIcon(attachment.originalName))
                            .into(imageView);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            LogUtil.d(" 预览文件的URL：" + attachment.getUrl());
                            //预览文件
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("data", attachment.getUrl());
                            MainApp.getMainApp().startActivity(mActivity, PreviewOfficeActivity.class, MainApp.ENTER_TYPE_RIGHT, false, bundle);
                        }
                    });
                }

                GridLayout.Spec row = GridLayout.spec(r, 1);
                GridLayout.Spec colspan = GridLayout.spec(c, 1);
                GridLayout.LayoutParams gridLayoutParam = new GridLayout.LayoutParams(row, colspan);
                holder.grid.addView(oImageView, gridLayoutParam);
            }

        } else {
            holder.grid.setVisibility(View.GONE);
        }
    }

    @Override public void onViewRecycled(NoticeAdapter2.BulletinViewHolder holder) {
        super.onViewRecycled(holder);
        int count = holder.grid.getChildCount();
        List<View> children = new ArrayList<>();
        for (int i = 0; i <count; i++) {
            View child =  holder.grid.getChildAt(i);
            // pool.recycleInstance((ViewGroup) child);
            children.add(child);
        }
        for (final View c :children) {
            holder.grid.removeView(c);
            pool.recycleInstance((ViewGroup) c);
            ImageView imageView = (ImageView) c.findViewById(R.id.imageView);
            Glide.clear(imageView);
            imageView.setOnClickListener(null);
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

    @Override
    public long getItemId(int position) {
        final Bulletin bulletin = mBulletins.get(position);
        return bulletin.getId().hashCode();
    }

}
