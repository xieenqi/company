package com.loyo.oa.v2.activityui.worksheet.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.other.PreviewImagefromHttp;
import com.loyo.oa.v2.activityui.other.PreviewOfficeActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.XCRoundRectImageView;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 【工单公用列表item】中的图片适配器
 * Created by YYY on 16/8/26.
 */
public class WorkSheetListNestingAdapter extends BaseAdapter {

    private ArrayList<Attachment> attachments;
    private Context mContext;
    private LayoutInflater inflater;

    public WorkSheetListNestingAdapter(ArrayList<Attachment> attachments, Context mContext) {
        this.mContext = mContext;
        this.attachments = attachments;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return attachments == null ? 0 : attachments.size();
    }

    @Override
    public Object getItem(int position) {
        return attachments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final Attachment attachment = attachments.get(position);
        final boolean isImage = (attachment.getAttachmentType() == Attachment.AttachmentType.IMAGE);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_worksheet_listorlist, null);
            holder.pb_progress = (ProgressBar) convertView.findViewById(R.id.pb_progress);
            holder.iv_image = (XCRoundRectImageView) convertView.findViewById(R.id.iv_image);
            holder.tv_image_name = (TextView) convertView.findViewById(R.id.tv_image_name);
            holder.tv_image_size = (TextView) convertView.findViewById(R.id.tv_image_size);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.iv_image.setImageResource(R.drawable.img_file_null);
        holder.tv_image_name.setText(attachment.getOriginalName());
        holder.tv_image_size.setText("大小：" + Utils.FormetFileSize(Long.valueOf(attachment.getSize())));
        holder.setContentView(attachment, isImage);
        return convertView;
    }

    public void refreshData() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }, 50);
    }

    class ViewHolder {

        XCRoundRectImageView iv_image;
        TextView tv_image_name;
        TextView tv_image_size;
        ProgressBar pb_progress;

        private void setContentView(final Attachment attachment, boolean isImage) {

            final XCRoundRectImageView theImage = iv_image;
            if (isImage) {
            /*ImageView进度条设置*/
                ImageLoader.getInstance().displayImage(attachment.getUrl(), theImage, MainApp.options_3, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {
                        pb_progress.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {
                        pb_progress.setVisibility(View.INVISIBLE);
                        theImage.setImageResource(R.drawable.img_file_null);
                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        pb_progress.setVisibility(View.INVISIBLE);
                        Log.v("debug", view.toString() + "  " + attachment.getUrl());
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
                theImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle mBundle = new Bundle();
                        mBundle.putString(ExtraAndResult.EXTRA_OBJ, attachment.getUrl());
                        MainApp.getMainApp().startActivityForResult((Activity) mContext, PreviewImagefromHttp.class,
                                MainApp.ENTER_TYPE_RIGHT, FinalVariables.REQUEST_DEAL_ATTACHMENT, mBundle);
                    }
                });
            } else {//文件类型附件
                theImage.setImageResource(Global.getAttachmentIcon(attachment.originalName));

                Log.v("debug", theImage.toString() + "  " + attachment.originalName);
                theImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        LogUtil.d(" 预览文件的URL：" + attachment.getUrl());
                        //预览文件
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("data", attachment.getUrl());
                        MainApp.getMainApp().startActivity((Activity) mContext, PreviewOfficeActivity.class, MainApp.ENTER_TYPE_RIGHT, false, bundle);
                    }
                });
            }
        }
    }

}
