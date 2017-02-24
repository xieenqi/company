package com.loyo.oa.v2.activityui.customer.adapter;

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

import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.commonview.CommonHtmlUtils;
import com.loyo.oa.v2.activityui.commonview.CommonImageView;
import com.loyo.oa.v2.activityui.commonview.CommonTextVew;
import com.loyo.oa.v2.activityui.commonview.MapSingleView;
import com.loyo.oa.v2.activityui.customer.model.ImgAndText;
import com.loyo.oa.v2.activityui.customer.viewcontrol.CustomerFollowUpListView;
import com.loyo.oa.v2.activityui.followup.adapter.ListOrDetailsCommentAdapter;
import com.loyo.oa.v2.activityui.followup.adapter.ListOrDetailsGridViewAdapter;
import com.loyo.oa.v2.activityui.followup.adapter.ListOrDetailsOptionsAdapter;
import com.loyo.oa.v2.activityui.followup.model.FollowUpListModel;
import com.loyo.oa.v2.activityui.followup.viewcontrol.AudioPlayCallBack;
import com.loyo.oa.v2.activityui.other.PreviewImageListActivity;
import com.loyo.oa.v2.activityui.signin.adapter.ListOrDetailsAudioAdapter;
import com.loyo.oa.v2.activityui.signin.bean.AudioModel;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.CusGridView;
import com.loyo.oa.v2.customview.CustomerListView;
import com.loyo.oa.v2.customview.RoundImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * 【我的跟进列表】adapter
 * Created by yyy on 16/11/12.
 */

public class CustomerFollowUpListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<FollowUpListModel> listModel;
    private CustomerFollowUpListView viewCrol;
    private AudioPlayCallBack audioPlayCallBack;

    private ListOrDetailsGridViewAdapter gridViewAdapter;  /* 九宫格附件 */
    private ListOrDetailsCommentAdapter commentAdapter;    /* 评论区域 */
    private ListOrDetailsAudioAdapter audioAdapter;        /* 录音语音 */
    private ListOrDetailsOptionsAdapter optionAdapter;     /* 文件区域 */
    private int parentIndex;

    public CustomerFollowUpListAdapter(Context mContext, ArrayList<FollowUpListModel> listModel,
                                       CustomerFollowUpListView viewCrol, AudioPlayCallBack audioCallBack, int parentIndex) {
        this.mContext = mContext;
        this.listModel = listModel;
        this.viewCrol = viewCrol;
        this.audioPlayCallBack = audioCallBack;
        this.parentIndex = parentIndex;
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
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder holder = null;
        final FollowUpListModel followUpListModel = listModel.get(position);
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_customer_and_clue_followup, null);
            holder.iv_heading = (RoundImageView) convertView.findViewById(R.id.iv_heading);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_contact = (TextView) convertView.findViewById(R.id.tv_contact);
            holder.tv_contact_tag = (TextView) convertView.findViewById(R.id.tv_contact_tag);
            holder.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
            holder.tv_toast = (TextView) convertView.findViewById(R.id.tv_toast);
            holder.tv_memo = (TextView) convertView.findViewById(R.id.tv_memo);
            holder.tv_last_time = (TextView) convertView.findViewById(R.id.tv_last_time);
            holder.iv_phone_call = (TextView) convertView.findViewById(R.id.iv_phone_call);
            holder.tv_audio_length = (TextView) convertView.findViewById(R.id.tv_audio_length);
            holder.tv_kind = (TextView) convertView.findViewById(R.id.tv_kind);
            holder.iv_comment = (ImageView) convertView.findViewById(R.id.iv_comment);
            holder.layout_gridview = (CusGridView) convertView.findViewById(R.id.layout_gridview);
            holder.lv_comment = (CustomerListView) convertView.findViewById(R.id.lv_comment);
            holder.lv_audio = (CustomerListView) convertView.findViewById(R.id.lv_audio);
            holder.lv_options = (CustomerListView) convertView.findViewById(R.id.lv_options);
            holder.layout_comment = (LinearLayout) convertView.findViewById(R.id.layout_comment);
            holder.layout_phonely = (LinearLayout) convertView.findViewById(R.id.layout_phonely);
            holder.layout_address = (LinearLayout) convertView.findViewById(R.id.layout_address);
            holder.layout_lasttime = (LinearLayout) convertView.findViewById(R.id.layout_lasttime);
            holder.ll_web = (LinearLayout) convertView.findViewById(R.id.ll_web);
            holder.iv_lasttime = (ImageView) convertView.findViewById(R.id.iv_lasttime);
            holder.tv_create_time = (TextView) convertView.findViewById(R.id.tv_create_time);
            holder.ll_contact_tag = (LinearLayout) convertView.findViewById(R.id.ll_contact_tag);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.iv_comment.setOnTouchListener(Global.GetTouch());

        holder.setContent(holder.ll_web, followUpListModel.content);
        ImageLoader.getInstance().displayImage(followUpListModel.avatar, holder.iv_heading);
        holder.tv_name.setText(followUpListModel.creatorName);
        /*联系人*/
        followUpListModel.setContactName(holder.tv_contact);
        holder.ll_contact_tag.setVisibility(View.VISIBLE);
        holder.tv_contact_tag.setText("联系人角色: " + (TextUtils.isEmpty(followUpListModel.contactRole) ? "无" : followUpListModel.contactRole));
        holder.tv_create_time.setText(DateTool.getHourMinute(followUpListModel.createAt));
        holder.tv_kind.setText(TextUtils.isEmpty(followUpListModel.typeName) ? "无" : "# " + followUpListModel.typeName);

        /** 电话录音设置 */
        followUpListModel.setPhoneRecord(holder.layout_phonely,holder.tv_audio_length,holder.iv_phone_call);
//        if (null != followUpListModel.audioUrl && !TextUtils.isEmpty(followUpListModel.audioUrl)) {
//            holder.layout_phonely.setVisibility(View.VISIBLE);
//            holder.tv_audio_length.setText(com.loyo.oa.common.utils.DateTool.int2time(followUpListModel.audioLength * 1000));
//            int audioLength = followUpListModel.audioLength;
//            if (audioLength > 0 && audioLength <= 60) {
//                holder.iv_phone_call.setText("000");
//            } else if (audioLength > 60 && audioLength <= 300) {
//                holder.iv_phone_call.setText("00000");
//            } else if (audioLength > 300 && audioLength <= 600) {
//                holder.iv_phone_call.setText("0000000");
//            } else if (audioLength > 600 && audioLength <= 1200) {
//                holder.iv_phone_call.setText("000000000");
//            } else if (audioLength > 1200 && audioLength <= 1800) {
//                holder.iv_phone_call.setText("00000000000");
//            } else if (audioLength > 1800 && audioLength <= 3600) {
//                holder.iv_phone_call.setText("00000000000000");
//            } else if (audioLength > 3600) {
//                holder.iv_phone_call.setText("0000000000000000");
//            } else {
//                holder.iv_phone_call.setText("");
//            }
//        } else {
//            holder.layout_phonely.setVisibility(View.GONE);
//        }

        /** 下次跟进时间 */
        followUpListModel.setFullowUpTime(holder.tv_last_time, holder.iv_lasttime, holder.layout_lasttime);

        /** 设置跟进内容 */
        if (null != followUpListModel.content && !TextUtils.isEmpty(followUpListModel.content)) {
            if (followUpListModel.content.contains("<p>")) {
                holder.setContent(holder.ll_web, followUpListModel.content);
                holder.tv_memo.setVisibility(View.GONE);
            } else {
                holder.tv_memo.setVisibility(View.VISIBLE);
                holder.tv_memo.setText(followUpListModel.content);
                holder.ll_web.removeAllViews();
            }
        }

        /** 客户地址 */
        if (null != followUpListModel.location.addr && !TextUtils.isEmpty(followUpListModel.location.addr)) {
            holder.layout_address.setVisibility(View.VISIBLE);
            holder.tv_address.setText(followUpListModel.location.addr);
            holder.tv_address.setOnTouchListener(Global.GetTouch());
        } else {
            holder.layout_address.setVisibility(View.GONE);
        }


        /** @的相关人员 */
        if (null != followUpListModel.atNameAndDepts && !TextUtils.isEmpty(followUpListModel.atNameAndDepts)) {
            holder.tv_toast.setVisibility(View.VISIBLE);
            holder.tv_toast.setText("@" + followUpListModel.atNameAndDepts);
        } else {
            holder.tv_toast.setVisibility(View.GONE);
        }

        /** 录音语音 */
        if (null != followUpListModel.audioInfo) {
            holder.lv_audio.setVisibility(View.VISIBLE);
            audioAdapter = new ListOrDetailsAudioAdapter(mContext, followUpListModel.audioInfo, audioPlayCallBack);
            holder.lv_audio.setAdapter(audioAdapter);
        } else {
            holder.lv_audio.setVisibility(View.GONE);
        }

        /** 文件列表 数据绑定 */
        if (null != followUpListModel.attachments && followUpListModel.attachments.size() > 0) {
            holder.lv_options.setVisibility(View.VISIBLE);
            optionAdapter = new ListOrDetailsOptionsAdapter(mContext, followUpListModel.attachments);
            holder.lv_options.setAdapter(optionAdapter);
        } else {
            holder.lv_options.setVisibility(View.GONE);
        }

        /** 绑定图片与GridView监听 */
        if (null != followUpListModel.imgAttachments && followUpListModel.imgAttachments.size() > 0) {
            holder.layout_gridview.setVisibility(View.VISIBLE);
            gridViewAdapter = new ListOrDetailsGridViewAdapter(mContext, followUpListModel.imgAttachments);
            holder.layout_gridview.setAdapter(gridViewAdapter);

            /*图片预览*/
            holder.layout_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", followUpListModel.imgAttachments);
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
        if (null != followUpListModel.comments && followUpListModel.comments.size() > 0) {
            holder.layout_comment.setVisibility(View.VISIBLE);
            commentAdapter = new ListOrDetailsCommentAdapter(mContext, followUpListModel.comments, audioPlayCallBack);
            holder.lv_comment.setAdapter(commentAdapter);

            /*长按删除*/
            holder.lv_comment.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    viewCrol.deleteCommentEmbl(followUpListModel.comments.get(position).id);
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
                viewCrol.commentEmbl(followUpListModel.id, parentIndex, position);
            }
        });

        /** 查看定位地址 */
        holder.tv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != followUpListModel.location.loc) {
                    Intent mIntent = new Intent(mContext, MapSingleView.class);
                    mIntent.putExtra("la", Double.valueOf(followUpListModel.location.loc[0]));
                    mIntent.putExtra("lo", Double.valueOf(followUpListModel.location.loc[1]));
                    mIntent.putExtra("address", followUpListModel.location.addr);
                    mContext.startActivity(mIntent);
                } else {
                    Toast.makeText(mContext, "GPS坐标不全!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /** 电话录音播放 */
        final TextView iv_phone_call = (TextView) convertView.findViewById(R.id.iv_phone_call);
        iv_phone_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioModel audioModel = new AudioModel();
                audioModel.url = followUpListModel.audioUrl;
                audioModel.length = followUpListModel.audioLength;
                audioPlayCallBack.playVoice(audioModel, iv_phone_call);
            }
        });

        return convertView;
    }

    class ViewHolder {
        RoundImageView iv_heading;
        TextView tv_name;        /*创建人*/
        TextView tv_address;     /*地址*/
        TextView tv_contact;     /*联系人*/
        TextView tv_contact_tag; /*联系人角色*/
        TextView tv_toast;       /*通知人员*/
        TextView tv_memo;        /*内容*/
        TextView tv_kind;        /*跟进类型*/
        TextView tv_last_time;   /*下次跟进时间*/
        TextView tv_audio_length;
        TextView iv_phone_call;
        TextView tv_create_time; /*创建时间*/

        LinearLayout ll_web;
        LinearLayout layout_comment;
        LinearLayout layout_address;
        LinearLayout layout_lasttime;
        LinearLayout layout_phonely;
        LinearLayout ll_contact_tag;
        CustomerListView lv_comment; /*评论区*/
        CustomerListView lv_audio;   /*语音录音区*/
        CustomerListView lv_options; /*文件列表区*/
        GridView layout_gridview;    /*图片9宫格区*/
        ImageView iv_comment;        /*评论按钮*/
        ImageView iv_lasttime;     /*下次跟进图标*/


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
