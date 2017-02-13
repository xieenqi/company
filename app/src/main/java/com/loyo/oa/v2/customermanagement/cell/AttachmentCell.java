package com.loyo.oa.v2.customermanagement.cell;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loyo.oa.common.utils.DateFormatSet;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Global;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by EthanGong on 2017/2/13.
 */

public class AttachmentCell extends RecyclerView.ViewHolder {


    public interface OnAttachmentCellListener {
        void onAttachmentSelected(int index);
    }

    @BindView(R.id.container) ViewGroup container;
    @BindView(R.id.img_attachment) ImageView img_attachment;
    @BindView(R.id.tv_creator) TextView tv_creator;
    @BindView(R.id.tv_title) TextView tv_title;
    @BindView(R.id.tv_createtime) TextView tv_time;
    @BindView(R.id.pb_progress) ProgressBar pb_progress;

    @OnClick(R.id.container) void onSelect() {
        if (listenerRef != null && listenerRef.get() != null) {
            listenerRef.get().onAttachmentSelected(index);
        }
    }

    private Attachment model;
    private int index;
    public WeakReference<OnAttachmentCellListener> listenerRef;


    public static AttachmentCell instance(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_attachment, parent, false);
        AttachmentCell cell = new AttachmentCell(itemView);
        return cell;
    }

    public AttachmentCell(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        container.setOnTouchListener(Global.GetTouch());
    }

    public void loadAttachment(Attachment attachment, int index) {
        model = attachment;
        this.index = index;

        boolean isPic = (attachment.getAttachmentType() == Attachment.AttachmentType.IMAGE);
        final File file = attachment.getFile();

        if (file == null) {
            if (isPic) {
                ImageLoader.getInstance().displayImage(attachment.getUrl(), img_attachment,
                        MainApp.options_3, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {
                        pb_progress.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {
                        pb_progress.setVisibility(View.INVISIBLE);
                        img_attachment.setImageResource(R.drawable.img_file_null);
                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        pb_progress.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
            } else {
                img_attachment.setImageResource(Global.getAttachmentIcon(attachment.getUrl()));
            }
        } else {
            ImageLoader.getInstance().displayImage(Uri.fromFile(file).toString(), img_attachment, MainApp.options_3);
        }

        tv_title.setText(attachment.getOriginalName());
        tv_creator.setText(String.format("%s %s 上传", attachment.getCreator().getRealname(), com.loyo.oa.common.utils.DateTool.convertDate(attachment.getCreatedAt(), DateFormatSet.specialMinuteSdf)));
        tv_time.setText(com.loyo.oa.common.utils.DateTool.getDateTimeFriendly(Long.parseLong(attachment.getCreatedAt())));

    }
}
