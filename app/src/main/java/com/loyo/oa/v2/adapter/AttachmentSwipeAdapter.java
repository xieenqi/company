package com.loyo.oa.v2.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.attendance.AttachmentRightActivity_;
import com.loyo.oa.v2.activity.PreviewImageActivity;
import com.loyo.oa.v2.activity.PreviewOfficeActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Attachment;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.point.ITask;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class AttachmentSwipeAdapter extends BaseAdapter {

    public static final int REQUEST_ATTACHMENT = 4000;
    private Context mContext;
    private ArrayList<Attachment> mAttachments;
    private ArrayList<User> users = new ArrayList<>();
    private MainApp app;
    private AttachmentAction mAction;
    private OnRightClickCallback callback;
    private int goneBtn; //隐藏对应的按钮 1:权限 2:删除
    private boolean hasRights = true;

    public interface OnRightClickCallback {
        void onRightClick(Bundle b);
    }

    public AttachmentSwipeAdapter(Context _context, ArrayList<Attachment> _attachments, ArrayList<User> _users, int _goneBtn) {
        super();
        mAttachments = _attachments;
        mContext = _context;
        app = (MainApp) _context.getApplicationContext();
        this.goneBtn = _goneBtn;

        if (_users != null) {
            users = _users;
            users.remove(MainApp.user);
        }
    }

    public AttachmentSwipeAdapter(Context _context, ArrayList<Attachment> _attachments, ArrayList<User> _users, OnRightClickCallback _callback, boolean hasRights, int _goneBtn) {
        this(_context, _attachments, _users, _goneBtn);
        this.hasRights = hasRights;
        callback = _callback;
    }

    public void setHasRights(boolean hasRights) {
        this.hasRights = hasRights;
    }

    public void setData(ArrayList<Attachment> attachments) {
        mAttachments = attachments;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    @Override
    public int getCount() {
        return mAttachments.size();
    }

    @Override
    public Object getItem(int position) {
        return mAttachments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            //修复进入附件时崩溃的bug ykb 07-13
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_swipelistview_attachments, null);
            holder = new ViewHolder();

            holder.front = (ViewGroup) convertView.findViewById(R.id.front);
            holder.img_attachment = (ImageView) convertView.findViewById(R.id.img_attachment);
            holder.tv_creator = (TextView) convertView.findViewById(R.id.tv_creator);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_createtime);
            holder.layout_action_update = (ViewGroup) convertView.findViewById(R.id.layout_action_update);
            holder.layout_action_delete = (ViewGroup) convertView.findViewById(R.id.layout_action_delete);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Attachment attachment = mAttachments.get(position);
        if (attachment == null) {
            return convertView;
        }

        boolean isPic = (attachment.getAttachmentType() == Attachment.AttachmentType.IMAGE);
        final File file = attachment.getFile();

        if (file == null) {
            if (isPic) {
                ImageLoader.getInstance().displayImage(attachment.getUrl(), holder.img_attachment, MainApp.options_3);
            } else {
                holder.img_attachment.setImageResource(Global.getAttachmentIcon(attachment.getUrl()));
            }
        } else {
            ImageLoader.getInstance().displayImage(Uri.fromFile(file).toString(), holder.img_attachment, MainApp.options_3);
        }

        holder.tv_title.setText(attachment.getOriginalName());
        holder.tv_creator.setText(String.format("%s %s 上传", attachment.getCreator().getRealname(), DateTool.getDate(attachment.getCreatedAt(), app.df_api_get, app.df3)));
        holder.tv_time.setText(MainApp.getMainApp().df14.format(new Date(Integer.parseInt(attachment.getCreatedAt()) * 1000L)));
        holder.img_attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (attachment.getAttachmentType() == Attachment.AttachmentType.IMAGE) {

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", mAttachments);
                    bundle.putSerializable("position", position);
                    bundle.putBoolean("isEdit", false);
                    MainApp.getMainApp().startActivity((Activity) mContext, PreviewImageActivity.class, MainApp.ENTER_TYPE_BUTTOM, false, bundle);

                } else if (attachment.getAttachmentType() == Attachment.AttachmentType.OFFICE) {

                    //预览文件
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", attachment.getUrl());
                    app.startActivity((Activity) mContext, PreviewOfficeActivity.class, MainApp.ENTER_TYPE_RIGHT, false, bundle);

                }
            }
        });


        /*只有附件的上传人是自已，才可以设置权限*/
        if (!hasRights || !MainApp.user.equals(attachment.getCreator())) {
            holder.layout_action_update.setVisibility(View.INVISIBLE);
            holder.layout_action_delete.setVisibility(View.INVISIBLE);
        } else {
            holder.layout_action_update.setVisibility(View.VISIBLE);
            holder.layout_action_delete.setVisibility(View.VISIBLE);

            /*客户管理里面，没有权限功能，需禁用*/
            if (goneBtn == 1) {
                holder.layout_action_update.setVisibility(View.INVISIBLE);
            }

            /**权限设置*/
            holder.layout_action_update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", attachment);
                    bundle.putSerializable("users", users);
                    if (null != callback) {
                        callback.onRightClick(bundle);
                    } else {
                        app.startActivityForResult((Activity) mContext, AttachmentRightActivity_.class, MainApp.ENTER_TYPE_RIGHT, REQUEST_ATTACHMENT, bundle);
                    }
                }
            });

            /**附件删除*/
            holder.layout_action_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //删除
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("确认");
                    builder.setPositiveButton(mContext.getString(R.string.dialog_submit), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Utils.dialogShow(mContext);
                            RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class).remove(attachment.getId(), new RCallback<Attachment>() {
                                @Override
                                public void success(Attachment att, Response response) {

                                    if (mAction != null) {
                                        mAction.afterDelete(attachment);
                                    }
                                    Utils.dialogDismiss();

                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    super.failure(error);
                                    HttpErrorCheck.checkError(error);
                                    Utils.dialogDismiss();
                                }
                            });

                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton(mContext.getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setMessage("是否删除附件?");
                    builder.show();
                }
            });
        }

        return convertView;
    }

    public void setAttachmentAction(AttachmentAction action) {
        mAction = action;
    }

    public interface AttachmentAction {
        void afterDelete(Attachment attachment);
    }

    class ViewHolder {
        public ImageView img_attachment;
        public TextView tv_creator;
        public TextView tv_title;
        public TextView tv_time;
        public ViewGroup front;
        public ViewGroup layout_action_update;
        public ViewGroup layout_action_delete;
    }
}
