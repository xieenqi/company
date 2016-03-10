package com.loyo.oa.v2.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.PreviewImageActivity;
import com.loyo.oa.v2.activity.PreviewOfficeActivity;
import com.loyo.oa.v2.activity.attachment.AttachmentRightActivity_;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Attachment;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.customview.GeneralPopView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedString;

public class AttachmentSwipeAdapter extends BaseAdapter {

    public static final int REQUEST_ATTACHMENT = 4000;
    private Context mContext;
    private ArrayList<Attachment> mAttachments;
    private ArrayList<User> users = new ArrayList<>();
    private MainApp app;
    private AttachmentAction mAction;
    private OnRightClickCallback callback;
    private int bizType;
    private boolean isOver;
    private String uuid;

    public interface OnRightClickCallback {
        void onRightClick(Bundle b);
    }

    public AttachmentSwipeAdapter(Context _context, ArrayList<Attachment> _attachments, ArrayList<User> _users, int bizType,String uuid,boolean isOver) {
        super();
        mAttachments = _attachments;
        mContext = _context;
        app = (MainApp) _context.getApplicationContext();
        this.bizType = bizType;
        this.uuid = uuid;
        this.isOver = isOver;

        if (_users != null) {
            users = _users;
            users.remove(MainApp.user);
        }
    }

    public AttachmentSwipeAdapter(Context _context, ArrayList<Attachment> _attachments, ArrayList<User> _users, OnRightClickCallback _callback,int _bizType,String _uuid,boolean _isOver) {
        this(_context, _attachments, _users,_bizType,_uuid,_isOver);
        callback = _callback;
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

        /**
         * 是自己的附件，才能设置权限\删除
           非开启/进行中/待点评/待审批/不通过状态下，不允许删除附件
         */

        if (!MainApp.user.id.equals(attachment.getCreator().getId())) {
                holder.layout_action_delete.setVisibility(View.INVISIBLE);
        } else {
           if(!isOver){
               if(holder.layout_action_delete.getVisibility() == View.INVISIBLE){
                   holder.layout_action_delete.setVisibility(View.VISIBLE);
               }
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
                    final GeneralPopView generalPopView = new GeneralPopView(mContext,true);
                    generalPopView.show();
                    generalPopView.setMessage("是否删除附件?");
                    generalPopView.setCanceledOnTouchOutside(true);
                    //确定
                    generalPopView.setSureOnclick(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Utils.dialogShow(mContext, "请稍候");
                            HashMap<String,Object> map = new HashMap<String, Object>();
                            map.put("bizType",bizType);
                            map.put("uuid",uuid);
                            RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class).remove(attachment.getId(), map, new RCallback<Attachment>() {
                                @Override
                                public void success(Attachment att, Response response) {
                                    HttpErrorCheck.checkResponse(response);
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

                            generalPopView.dismiss();
                        }
                    });
                    //取消
                    generalPopView.setCancelOnclick(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            generalPopView.dismiss();
                        }
                    });
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
