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
        final FollowUpListModel model = listModel.get(position);
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

        ImageLoader.getInstance().displayImage(model.avatar, holder.iv_heading);
        holder.tv_name.setText(model.creatorName);
        /*联系人*/
        model.setContactName(holder.tv_contact);
        holder.ll_contact_tag.setVisibility(View.VISIBLE);
        holder.tv_contact_tag.setText("联系人角色: " + (TextUtils.isEmpty(model.contactRole) ? "无" : model.contactRole));
        holder.tv_create_time.setText(DateTool.getHourMinute(model.createAt));
        holder.tv_kind.setText(TextUtils.isEmpty(model.typeName) ? "无" : "# " + model.typeName);

        /** 电话录音设置 */
        model.setPhoneRecord(holder.layout_phonely,holder.tv_audio_length,holder.iv_phone_call);

        /** 下次跟进时间 */
        model.setFullowUpTime(holder.tv_last_time, holder.iv_lasttime, holder.layout_lasttime);

        /** 设置跟进内容 */
        model.setContent(holder.ll_web,holder.tv_memo);
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

        /** 客户地址 */
        model.setAddress(holder.layout_address, holder.tv_address);

        /** @的相关人员 */
        model.setAtPerson(holder.tv_toast);

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
            holder.lv_comment.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    viewCrol.deleteCommentEmbl(model.comments.get(position).id);
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
                viewCrol.commentEmbl(model.id, parentIndex, position);
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
