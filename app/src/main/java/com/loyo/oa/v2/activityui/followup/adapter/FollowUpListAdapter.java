package com.loyo.oa.v2.activityui.followup.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loyo.oa.common.utils.UmengAnalytics;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.clue.ClueDetailActivity;
import com.loyo.oa.v2.activityui.commonview.CommonHtmlUtils;
import com.loyo.oa.v2.activityui.commonview.CommonImageView;
import com.loyo.oa.v2.activityui.commonview.CommonTextVew;
import com.loyo.oa.v2.activityui.commonview.MapSingleView;
import com.loyo.oa.v2.activityui.customer.CustomerDetailInfoActivity_;
import com.loyo.oa.v2.activityui.customer.model.ImgAndText;
import com.loyo.oa.v2.activityui.followup.model.FollowUpListModel;
import com.loyo.oa.v2.activityui.followup.viewcontrol.AudioPlayCallBack;
import com.loyo.oa.v2.activityui.followup.viewcontrol.FollowUpListView;
import com.loyo.oa.v2.activityui.other.PreviewImageListActivity;
import com.loyo.oa.v2.activityui.signin.adapter.ListOrDetailsAudioAdapter;
import com.loyo.oa.v2.activityui.signin.bean.AudioModel;
import com.loyo.oa.v2.activityui.signin.bean.CommentModel;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.CusGridView;
import com.loyo.oa.v2.customview.CustomerListView;
import com.loyo.oa.v2.customview.RoundImageView;
import com.loyo.oa.v2.customview.SweetAlertDialogView;
import com.loyo.oa.v2.permission.BusinessOperation;
import com.loyo.oa.v2.permission.PermissionManager;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 【我的跟进列表】adapter
 * Created by yyy on 16/11/12.
 */

public class FollowUpListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<FollowUpListModel> listModel;
    private FollowUpListView viewCrol;
    private AudioPlayCallBack audioPlayCallBack;

    private ListOrDetailsGridViewAdapter gridViewAdapter;  /* 九宫格附件 */
    private ListOrDetailsCommentAdapter commentAdapter;    /* 评论区域 */
    private ListOrDetailsAudioAdapter audioAdapter;        /* 录音语音 */
    private ListOrDetailsOptionsAdapter optionAdapter;     /* 文件区域 */

    public FollowUpListAdapter(Context mContext, ArrayList<FollowUpListModel> listModel, FollowUpListView viewCrol, AudioPlayCallBack audioCallBack) {
        this.mContext = mContext;
        this.listModel = listModel;
        this.viewCrol = viewCrol;
        this.audioPlayCallBack = audioCallBack;
    }

    @Override
    public int getCount() {
        return listModel.size();
    }

    @Override
    public Object getItem(int position) {
        return listModel.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final FollowUpListModel model = listModel.get(position);
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_followup_list, null);
            holder.iv_heading = (RoundImageView) convertView.findViewById(R.id.iv_heading);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_contact = (TextView) convertView.findViewById(R.id.tv_contact);
            holder.tv_kind = (TextView) convertView.findViewById(R.id.tv_kind);
            holder.tv_create_time = (TextView) convertView.findViewById(R.id.tv_create_time);
            holder.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
            holder.iv_phone_call = (TextView) convertView.findViewById(R.id.iv_phone_call);
            holder.tv_toast = (TextView) convertView.findViewById(R.id.tv_toast);
            holder.tv_memo = (TextView) convertView.findViewById(R.id.tv_memo);
            holder.tv_customer = (TextView) convertView.findViewById(R.id.tv_customer);
            holder.tv_audio_length = (TextView) convertView.findViewById(R.id.tv_audio_length);
            holder.iv_comment = (ImageView) convertView.findViewById(R.id.iv_comment);
            holder.tv_last_time = (TextView) convertView.findViewById(R.id.tv_last_time);
            holder.tv_clue = (TextView) convertView.findViewById(R.id.tv_clue);
            holder.layout_gridview = (CusGridView) convertView.findViewById(R.id.layout_gridview);
            holder.lv_comment = (CustomerListView) convertView.findViewById(R.id.lv_comment);
            holder.lv_audio = (CustomerListView) convertView.findViewById(R.id.lv_audio);
            holder.lv_options = (CustomerListView) convertView.findViewById(R.id.lv_options);
            holder.layout_comment = (LinearLayout) convertView.findViewById(R.id.layout_comment);
            holder.layout_address = (LinearLayout) convertView.findViewById(R.id.layout_address);
            holder.layout_lasttime = (LinearLayout) convertView.findViewById(R.id.layout_lasttime);
            holder.ll_web = (LinearLayout) convertView.findViewById(R.id.ll_web);
            holder.layout_phonely = (LinearLayout) convertView.findViewById(R.id.layout_phonely);
            holder.layout_customer = (LinearLayout) convertView.findViewById(R.id.layout_customer);
            holder.layout_clue = (LinearLayout) convertView.findViewById(R.id.layout_clue);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.iv_comment.setOnTouchListener(Global.GetTouch());
        if (model.creator != null && model.creator.avatar != null) {
            ImageLoader.getInstance().displayImage(model.creator.avatar, holder.iv_heading);
            holder.tv_name.setText(model.creator.name);
        }
        holder.tv_customer.setText(model.customerName);
        holder.tv_kind.setText(TextUtils.isEmpty(model.typeName) ? "无" : "# " + model.typeName);
        holder.tv_create_time.setText(com.loyo.oa.common.utils.DateTool.getDateTimeFriendly(model.createAt));

        String contact = TextUtils.isEmpty(model.contactName) ? "无联系人信息" : model.contactName;
        if (null != model.contactPhone && !TextUtils.isEmpty(model.contactPhone) && !contact.equals("无联系人信息")) {
            holder.tv_contact.setText(contact + "(" + model.contactPhone + ")");
        } else {
            holder.tv_contact.setText(contact);
        }


        /** 电话录音设置 */
        if (null != model.audioUrl && !TextUtils.isEmpty(model.audioUrl)) {
            holder.layout_phonely.setVisibility(View.VISIBLE);
            holder.tv_audio_length.setText(com.loyo.oa.common.utils.DateTool.int2time(model.audioLength * 1000));
            int audioLength = model.audioLength;
            if (audioLength > 0 && audioLength <= 60) {
                holder.iv_phone_call.setText("000");
            } else if (audioLength > 60 && audioLength <= 300) {
                holder.iv_phone_call.setText("00000");
            } else if (audioLength > 300 && audioLength <= 600) {
                holder.iv_phone_call.setText("0000000");
            } else if (audioLength > 600 && audioLength <= 1200) {
                holder.iv_phone_call.setText("00000000");
            } else if (audioLength > 1200 && audioLength <= 1800) {
                holder.iv_phone_call.setText("000000000");
            } else if (audioLength > 1800 && audioLength <= 3600) {
                holder.iv_phone_call.setText("0000000000");
            } else if (audioLength > 3600) {
                holder.iv_phone_call.setText("00000000000");
            } else {
                holder.iv_phone_call.setText("");
            }

        } else {
            holder.layout_phonely.setVisibility(View.GONE);
        }

        /** 下次跟进时间 */
        if (model.remindAt != 0) {
            holder.layout_lasttime.setVisibility(View.VISIBLE);
//            holder.tv_last_time.setText(DateTool.timet(followUpListModel.remindAt + "", "yyyy-MM-dd HH:mm"));
            holder.tv_last_time.setText(com.loyo.oa.common.utils.DateTool.getDateTimeFriendly(model.remindAt));
        } else {
            holder.layout_lasttime.setVisibility(View.GONE);
        }

        /** 设置跟进内容 */
        if (null != model.content && !TextUtils.isEmpty(model.content)) {
            if (model.content.contains("<p>")) {
                holder.setContent(holder.ll_web, model.content);
                holder.tv_memo.setVisibility(View.GONE);
            } else {
                holder.tv_memo.setVisibility(View.VISIBLE);
                holder.tv_memo.setText(model.content);
                holder.ll_web.removeAllViews();
            }
        }

        /** 线索 */
        if (null != model.salesleadCompanyName && !TextUtils.isEmpty(model.salesleadCompanyName)) {
            holder.layout_clue.setVisibility(View.VISIBLE);
            holder.tv_clue.setText(model.salesleadCompanyName);
            holder.tv_clue.setOnTouchListener(Global.GetTouch());
        } else {
            holder.layout_clue.setVisibility(View.GONE);
        }

        /** 客户姓名 */
        if (null != model.customerName && !TextUtils.isEmpty(model.customerName)) {
            holder.layout_customer.setVisibility(View.VISIBLE);
            holder.tv_customer.setText(model.customerName);
            holder.tv_customer.setOnTouchListener(Global.GetTouch());
        } else {
            holder.layout_customer.setVisibility(View.GONE);
        }

        /** 客户地址 */
        if (null != model.location && null != model.location.addr && !TextUtils.isEmpty(model.location.addr)) {
            holder.layout_address.setVisibility(View.VISIBLE);
            holder.tv_address.setText(model.location.addr);
            holder.tv_address.setOnTouchListener(Global.GetTouch());
        } else {
            holder.layout_address.setVisibility(View.GONE);
        }


        /** @的相关人员 */
        if (null != model.atNameAndDepts) {
            holder.tv_toast.setVisibility(View.VISIBLE);
            holder.tv_toast.setText("@" + model.atNameAndDepts);
        } else {
            holder.tv_toast.setVisibility(View.GONE);
        }

        /** 录音语音 */
        if (null != model.audioInfo) {
            holder.lv_audio.setVisibility(View.VISIBLE);
            audioAdapter = new ListOrDetailsAudioAdapter(mContext, model.audioInfo, audioPlayCallBack);
            holder.lv_audio.setAdapter(audioAdapter);
        } else {
            holder.lv_audio.setVisibility(View.GONE);
        }

        /** 文件列表 数据绑定 */
        if (null != model.attachments && model.attachments.size() > 0) {
            holder.lv_options.setVisibility(View.VISIBLE);
            optionAdapter = new ListOrDetailsOptionsAdapter(mContext, model.attachments);
            holder.lv_options.setAdapter(optionAdapter);
        } else {
            holder.lv_options.setVisibility(View.GONE);
        }

        /** 绑定图片与GridView监听 */
        if (null != model.imgAttachments && model.imgAttachments.size() > 0) {
            holder.layout_gridview.setVisibility(View.VISIBLE);
            gridViewAdapter = new ListOrDetailsGridViewAdapter(mContext, model.imgAttachments);
            holder.layout_gridview.setAdapter(gridViewAdapter);

            /*图片预览*/
            holder.layout_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", model.imgAttachments);
                    bundle.putInt("position", position);
                    bundle.putBoolean("isEdit", false);
                    MainApp.getMainApp().startActivityForResult((Activity) mContext, PreviewImageListActivity.class,
                            MainApp.ENTER_TYPE_BUTTOM, FinalVariables.REQUEST_DEAL_ATTACHMENT, bundle);
                }
            });
        } else {
            holder.layout_gridview.setVisibility(View.GONE);
        }

        /** 绑定评论数据 */
        if (null != model.comments && model.comments.size() > 0) {
            holder.layout_comment.setVisibility(View.VISIBLE);
            commentAdapter = new ListOrDetailsCommentAdapter(mContext, model.comments, audioPlayCallBack);
            holder.lv_comment.setAdapter(commentAdapter);

            /*长按删除*/
            final ViewHolder finalHolder = holder;
            holder.lv_comment.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    viewCrol.deleteCommentEmbl(finalHolder.lv_comment,position,model.comments.get(position).id);
                    return false;
                }
            });

        } else {
            holder.layout_comment.setVisibility(View.GONE);
        }

        /** 评论发送 */
        holder.iv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewCrol.commentEmbl(position);
            }
        });

        /** 查看定位地址 */
        holder.tv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != model.location.loc) {
                    Intent mIntent = new Intent(mContext, MapSingleView.class);
                    mIntent.putExtra("la", Double.valueOf(model.location.loc[0]));
                    mIntent.putExtra("lo", Double.valueOf(model.location.loc[1]));
                    mIntent.putExtra("address", model.location.addr);
                    mContext.startActivity(mIntent);
                } else {
                    Toast.makeText(mContext, "GPS坐标不全!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /** 查看客户详情 */
        holder.tv_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean customerAuth = PermissionManager.getInstance().hasPermission(BusinessOperation.CUSTOMER_MANAGEMENT);
                if (!customerAuth) {

                    SweetAlertDialogView sweetAlertDialogView = new SweetAlertDialogView(mContext);
                    sweetAlertDialogView.alertMessageClick(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                        }
                    }, "提示", "你无此功能权限");
                    return;
                }

                Intent intent = new Intent();
                intent.putExtra("Id", model.customerId);
                intent.setClass(mContext, CustomerDetailInfoActivity_.class);
                mContext.startActivity(intent);
            }
        });

        /** 进入线索详情 */
        holder.tv_clue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent();
                mIntent.putExtra(ExtraAndResult.IS_TEAM, false);
                mIntent.putExtra(ExtraAndResult.EXTRA_ID, /* 线索id */ model.sealsleadId);
                mIntent.setClass(mContext, ClueDetailActivity.class);
                mContext.startActivity(mIntent);
            }
        });

        /** 电话录音播放 */
        final TextView iv_phone_call = (TextView) convertView.findViewById(R.id.iv_phone_call);
        iv_phone_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioModel audioModel = new AudioModel();
                audioModel.url = model.audioUrl;
                audioModel.length = model.audioLength;
                audioPlayCallBack.playVoice(audioModel, iv_phone_call);
            }
        });

        return convertView;
    }

    class ViewHolder {
        RoundImageView iv_heading;
        TextView tv_name;        /*创建人*/
        TextView tv_address;     /*地址*/
        TextView tv_customer;    /*客户名字*/
        TextView tv_contact;     /*联系人*/
        TextView tv_create_time; /*创建时间*/
        TextView tv_toast;       /*通知人员*/
        TextView tv_memo;        /*内容*/
        TextView tv_kind;        /*内容*/
        TextView tv_last_time;   /*下次跟进时间*/
        TextView tv_clue;        /*线索*/
        TextView tv_audio_length;/*通话录音时间*/
        TextView iv_phone_call;

        LinearLayout ll_web;
        LinearLayout layout_comment;
        LinearLayout layout_address;
        LinearLayout layout_customer;
        LinearLayout layout_lasttime;
        LinearLayout layout_clue;
        LinearLayout layout_phonely;
        CustomerListView lv_comment; /*评论区*/
        CustomerListView lv_audio;   /*语音录音区*/
        CustomerListView lv_options; /*文件列表区*/
        GridView layout_gridview;    /*图片9宫格区*/
        ImageView iv_comment;        /*评论按钮*/


        /**
         * 设置图文混编
         */
        public void setContent(LinearLayout layout, String content) {
            layout.removeAllViews();
            for (final ImgAndText ele : CommonHtmlUtils.Instance().checkContentList(content)) {
                if (ele.type.startsWith("img")) {
                    CommonImageView img = new CommonImageView(mContext, ele.data);
                    layout.addView(img);
                } else {
                    CommonTextVew tex = new CommonTextVew(mContext, ele.data);
                    layout.addView(tex);
                }
            }
            layout.setVisibility(View.VISIBLE);
        }
    }
}
