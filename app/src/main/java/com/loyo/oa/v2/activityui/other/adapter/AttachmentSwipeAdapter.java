package com.loyo.oa.v2.activityui.other.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loyo.oa.common.utils.DateFormatSet;
import com.loyo.oa.photo.PhotoPreview;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.other.PreviewOfficeActivity;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.SweetAlertDialogView;
import com.loyo.oa.v2.customview.swipelistview.SwipeListView;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【附件适配器】
 */
public class AttachmentSwipeAdapter extends BaseAdapter {

    public static final int REQUEST_ATTACHMENT = 4000;
    private Context mContext;
    private ArrayList<Attachment> mAttachments;
    private ArrayList<User> users = new ArrayList<>();
    private MainApp app;
    private AttachmentAction mAction;
    //    private OnRightClickCallback callback;
    private int bizType;
    private boolean isOver;
    private String uuid;
    private SwipeListView listView;

//    public interface OnRightClickCallback {
//        void onRightClick(Bundle b);
//    }

    public AttachmentSwipeAdapter(Context _context, ArrayList<Attachment> _attachments,
                                  ArrayList<User> _users, int bizType, String uuid, boolean isOver) {
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

    public AttachmentSwipeAdapter(final Context _context,
                                  final ArrayList<Attachment> _attachments,
                                  final ArrayList<User> _users,
                                  final SwipeListView listView,
                                  final int _bizType,
                                  final String _uuid,
                                  final boolean _isOver) {
        this(_context, _attachments, _users, _bizType, _uuid, _isOver);
//        callback = _callback;
        this.listView = listView;
    }

    public void setData(final ArrayList<Attachment> attachments) {
        mAttachments = attachments;
    }

    public void setUsers(final ArrayList<User> users) {
        this.users = users;
    }

    @Override
    public int getCount() {
        return mAttachments.size();
    }

    @Override
    public Object getItem(final int position) {
        return mAttachments.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            //修复进入附件时崩溃的bug ykb 07-13
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_swipelistview_attachments, null);
            holder = new ViewHolder();

            holder.front = (ViewGroup) convertView.findViewById(R.id.front);
            holder.img_attachment = (ImageView) convertView.findViewById(R.id.img_attachment);
            holder.tv_creator = (TextView) convertView.findViewById(R.id.tv_creator);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_createtime);
            holder.pb_progress = (ProgressBar) convertView.findViewById(R.id.pb_progress);
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
                ImageLoader.getInstance().displayImage(attachment.getUrl(), holder.img_attachment, MainApp.options_3, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {
                        holder.pb_progress.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {
                        holder.pb_progress.setVisibility(View.INVISIBLE);
                        holder.img_attachment.setImageResource(R.drawable.img_file_null);
                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        holder.pb_progress.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
            } else {
                holder.img_attachment.setImageResource(Global.getAttachmentIcon(attachment.getUrl()));
            }
        } else {
            ImageLoader.getInstance().displayImage(Uri.fromFile(file).toString(), holder.img_attachment, MainApp.options_3);
        }

        holder.tv_title.setText(attachment.getOriginalName());
//        holder.tv_creator.setText(String.format("%s %s 上传", attachment.getCreator().getRealname(), DateTool.getDateFriendly(attachment.getCreatedAt(), app.df_api_get, app.df3)));
        holder.tv_creator.setText(String.format("%s %s 上传", attachment.getCreator().getRealname(), com.loyo.oa.common.utils.DateTool.convertDate(attachment.getCreatedAt(), DateFormatSet.specialMinuteSdf)));

//        holder.tv_time.setText(MainApp.getMainApp().df14.format(new Date(Integer.parseInt(attachment.getCreatedAt()) * 1000L)));
        holder.tv_time.setText(com.loyo.oa.common.utils.DateTool.getDateTimeFriendly(Long.parseLong(attachment.getCreatedAt())));
        holder.img_attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (attachment.getAttachmentType() == Attachment.AttachmentType.IMAGE) {
                    ArrayList<String> selectedPhotos = new ArrayList<>();

                    for (int i = 0; i < mAttachments.size(); i++) {
                        String path = mAttachments.get(i).getUrl();
                        if (path != null) {
                            selectedPhotos.add(path);
                        }
                    }
                    PhotoPreview.builder()
                            .setPhotos(selectedPhotos)
                            .setCurrentItem(position)
                            .setShowDeleteButton(false)
                            .start((Activity) mContext);

                } else if (attachment.getAttachmentType() == Attachment.AttachmentType.OFFICE) {
                    //预览文件
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", attachment.getUrl());
                    app.startActivity((Activity) mContext, PreviewOfficeActivity.class, MainApp.ENTER_TYPE_RIGHT, false, bundle);
                } else {
                    Global.Toast("不支持的附件类型");
                }
            }
        });

        /**
         * 是自己的附件，才能设置权限\删除
         非开启/未完成/待点评/待审批/不通过状态下，不允许删除附件
         */

        if (MainApp.user != null && !MainApp.user.id.equals(attachment.getCreator().getId())) {
            holder.layout_action_delete.setVisibility(View.INVISIBLE);
        } else {
            /*未结束*/
            if (!isOver) {
                if (holder.layout_action_delete.getVisibility() == View.INVISIBLE) {
                    holder.layout_action_delete.setVisibility(View.VISIBLE);
                }
               /*已结束*/
            } else {
                if (holder.layout_action_delete.getVisibility() == View.VISIBLE) {
                    holder.layout_action_delete.setVisibility(View.INVISIBLE);
                }
            }

            /**附件删除*/
            holder.layout_action_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {

                    final SweetAlertDialogView sweetAlertDialog = new SweetAlertDialogView(mContext);
                    sweetAlertDialog.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                        }
                    }, new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
//                            Utils.dialogShow(mContext, "请稍候");
                            HashMap<String, Object> map = new HashMap<String, Object>();
                            map.put("bizType", bizType);
                            map.put("uuid", uuid);
                            RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class).remove(attachment.getId(), map, new RCallback<Attachment>() {
                                @Override
                                public void success(final Attachment att, final Response response) {
                                    HttpErrorCheck.checkResponse(response);
                                    if (mAction != null) {
                                        mAction.afterDelete(attachment);
                                    }
//                                    Utils.dialogDismiss();
                                }

                                @Override
                                public void failure(final RetrofitError error) {
                                    super.failure(error);
                                    HttpErrorCheck.checkError(error);
//                                    Utils.dialogDismiss();
                                }
                            });

                            sweetAlertDialog.dismiss();
                            listView.closeOpenedItems();
                        }
                    }, "提示", "附件删除后不能恢复，你确定要删除吗？");

/*                    final GeneralPopView generalPopView = new GeneralPopView(mContext, true);
                    generalPopView.show();
                    generalPopView.setMessage("是否删除附件?");
                    generalPopView.setCanceledOnTouchOutside(true);
                    //确定
                    generalPopView.setSureOnclick(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            Utils.dialogShow(mContext, "请稍候");
                            HashMap<String, Object> map = new HashMap<String, Object>();
                            map.put("bizType", bizType);
                            map.put("uuid", uuid);
                            RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class).remove(attachment.getId(), map, new RCallback<Attachment>() {
                                @Override
                                public void success(final Attachment att, final Response response) {
                                    HttpErrorCheck.checkResponse(response);
                                    if (mAction != null) {
                                        mAction.afterDelete(attachment);
                                    }
                                    Utils.dialogDismiss();
                                }

                                @Override
                                public void failure(final RetrofitError error) {
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
                        public void onClick(final View view) {
                            generalPopView.dismiss();
                        }
                    });*/
                }
            });
        }

        return convertView;
    }

    public void setAttachmentAction(final AttachmentAction action) {
        mAction = action;
    }

    public interface AttachmentAction {
        void afterDelete(Attachment attachment);
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
        }, 200);
    }

    class ViewHolder {
        public ImageView img_attachment;
        public TextView tv_creator;
        public TextView tv_title;
        public TextView tv_time;
        public ViewGroup front;
        public ProgressBar pb_progress;
        public ViewGroup layout_action_delete;
    }
}
