package com.loyo.oa.v2.activityui.clue.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.library.module.widget.nestlistview.NestListView;
import com.library.module.widget.nestlistview.NestListViewAdapter;
import com.library.module.widget.nestlistview.NestViewHolder;
import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.common.utils.DensityUtil;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.clue.model.ClueFollowGroupModel;
import com.loyo.oa.v2.activityui.clue.viewcontrol.ClueFollowUpListView;
import com.loyo.oa.v2.activityui.commonview.CommonHtmlUtils;
import com.loyo.oa.v2.activityui.commonview.CommonImageView;
import com.loyo.oa.v2.activityui.commonview.CommonTextVew;
import com.loyo.oa.v2.activityui.commonview.MapSingleView;
import com.loyo.oa.v2.activityui.customer.model.ImgAndText;
import com.loyo.oa.v2.activityui.followup.adapter.ListOrDetailsCommentAdapter;
import com.loyo.oa.v2.activityui.followup.adapter.ListOrDetailsGridViewAdapter;
import com.loyo.oa.v2.activityui.followup.adapter.ListOrDetailsOptionsAdapter;
import com.loyo.oa.v2.activityui.followup.model.FollowUpListModel;
import com.loyo.oa.v2.activityui.followup.viewcontrol.AudioPlayCallBack;
import com.loyo.oa.v2.activityui.other.PreviewImageListActivity;
import com.loyo.oa.v2.activityui.signin.adapter.ListOrDetailsAudioAdapter;
import com.loyo.oa.v2.activityui.signin.bean.AudioModel;
import com.loyo.oa.v2.activityui.signin.bean.CommentModel;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.CusGridView;
import com.loyo.oa.v2.customview.CustomerListView;
import com.loyo.oa.v2.customview.RoundImageView;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.ViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import static com.loyo.oa.v2.R.id.iv_calls;
import static com.loyo.oa.v2.R.id.layout_audio;
import static com.loyo.oa.v2.R.id.tv_image_size;
import static com.loyo.oa.v2.R.id.tv_name;

/**
 * 列表 分组 adapter
 * Created by xeq on 17/2/27.
 */

public class FollowUpExpandable extends BaseExpandableListAdapter {
    private ArrayList<ClueFollowGroupModel> listModel;
    private Context mContext;
    private LayoutInflater inflater;
    private AudioPlayCallBack audioCb;
    private ClueFollowUpListView crolView;
    private ListOrDetailsGridViewAdapter gridViewAdapter;  /* 九宫格附件 */
    private ListOrDetailsCommentAdapter commentAdapter;    /* 评论区域 */
    private ListOrDetailsAudioAdapter audioAdapter;        /* 录音语音 */
    private ListOrDetailsOptionsAdapter optionAdapter;     /* 文件区域 */

    public FollowUpExpandable(Context mContext, AudioPlayCallBack audioCb, ClueFollowUpListView crolView) {
        this.mContext = mContext;
        this.audioCb = audioCb;
        this.crolView = crolView;
        inflater = LayoutInflater.from(mContext);
    }

    public void setData(ArrayList<ClueFollowGroupModel> listModel) {
        if (listModel != null) {
            this.listModel = listModel;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getGroupCount() {
        return listModel == null ? 0 : listModel.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listModel.get(groupPosition).activities == null ? 0 : listModel.get(groupPosition).activities.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listModel.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listModel.get(groupPosition).activities;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_followup_groupview, null);
        }
        TextView tv_title = ViewHolder.get(convertView, R.id.tv_title);
        tv_title.setText(DateTool.getDateFriendly(Long.parseLong(((ClueFollowGroupModel) getGroup(groupPosition)).timeStamp)));
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Holder holder;
        final FollowUpListModel model = listModel.get(groupPosition).activities.get(childPosition);
        if (null == convertView) {
            holder = new Holder();
            convertView = inflater.inflate(R.layout.item_customer_and_clue_followup, null);
            holder.iv_heading = (RoundImageView) convertView.findViewById(R.id.iv_heading);
            holder.tv_name = (TextView) convertView.findViewById(tv_name);
            holder.tv_contact = (TextView) convertView.findViewById(R.id.tv_contact);
            holder.tv_create_time = (TextView) convertView.findViewById(R.id.tv_create_time);
            holder.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
            holder.iv_phone_call = (TextView) convertView.findViewById(R.id.iv_phone_call);
            holder.tv_toast = (TextView) convertView.findViewById(R.id.tv_toast);
            holder.tv_audio_length = (TextView) convertView.findViewById(R.id.tv_audio_length);
            holder.tv_memo = (TextView) convertView.findViewById(R.id.tv_memo);
            holder.tv_last_time = (TextView) convertView.findViewById(R.id.tv_last_time);
            holder.tv_kind = (TextView) convertView.findViewById(R.id.tv_kind);
            holder.iv_comment = (ImageView) convertView.findViewById(R.id.iv_comment);
            holder.layout_gridview = (CusGridView) convertView.findViewById(R.id.layout_gridview);
            holder.lv_comment = (NestListView) convertView.findViewById(R.id.lv_comment);
            holder.lv_audio = (NestListView) convertView.findViewById(R.id.lv_audio);
            holder.lv_options = (NestListView) convertView.findViewById(R.id.lv_options);
            holder.layout_comment = (LinearLayout) convertView.findViewById(R.id.layout_comment);
            holder.layout_address = (LinearLayout) convertView.findViewById(R.id.layout_address);
            holder.layout_lasttime = (LinearLayout) convertView.findViewById(R.id.layout_lasttime);
            holder.layout_phonely = (LinearLayout) convertView.findViewById(R.id.layout_phonely);
            holder.ll_web = (LinearLayout) convertView.findViewById(R.id.ll_web);
            holder.iv_lasttime = (ImageView) convertView.findViewById(R.id.iv_lasttime);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.iv_comment.setOnTouchListener(Global.GetTouch());
        ImageLoader.getInstance().displayImage(model.avatar, holder.iv_heading);
        holder.tv_name.setText(model.creatorName);
        holder.tv_address.setText(TextUtils.isEmpty(model.addr) ? "无定位信息" : model.addr);
        holder.tv_create_time.setText(DateTool.getHourMinute(model.createAt));
        holder.tv_kind.setText(TextUtils.isEmpty(model.typeName) ? "无" : "# " + model.typeName);
        holder.tv_contact.setText("姓名: " + (TextUtils.isEmpty(model.contactName) ? "无联系人信息" : model.contactName));

        /** 电话录音设置 */
        model.setPhoneRecord(holder.layout_phonely, holder.tv_audio_length, holder.iv_phone_call);

        /** 下次跟进时间 */
        model.setFullowUpTime(holder.tv_last_time, holder.iv_lasttime, holder.layout_lasttime);

        /** 设置跟进内容 */
        model.setContent(mContext, holder.ll_web, holder.tv_memo);

        /** 客户地址 */
        model.setAddress(holder.layout_address, holder.tv_address);

        /** @的相关人员 */
        model.setAtPerson(holder.tv_toast);

        /** 录音语音 */
        if (null != model.audioInfo) {
            holder.lv_audio.setVisibility(View.VISIBLE);
//            audioAdapter = new ListOrDetailsAudioAdapter(mContext, model.audioInfo, audioCb);
//            holder.lv_audio.setAdapter(audioAdapter);
            holder.lv_audio.setAdapter(new NestListViewAdapter<AudioModel>(R.layout.item_sigfollw_audio, model.audioInfo) {
                @Override
                public void onBind(int pos, final AudioModel ben, NestViewHolder holder) {
                    final TextView iv_calls = holder.getView(R.id.iv_calls);
                    TextView tv_audio_length = holder.getView(R.id.tv_audio_length);
                    ViewGroup layout_audio = holder.getView(R.id.layout_audio);
                    ben.setRecordUI(layout_audio, iv_calls, tv_audio_length);
                    //        /*点击播放录音*/
                    iv_calls.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            audioCb.playVoice(ben, iv_calls);
                        }
                    });
                    layout_audio.setPadding(DensityUtil.px2dp(mContext, 0), DensityUtil.px2dp(mContext, 5),
                            DensityUtil.px2dp(mContext, 0), DensityUtil.px2dp(mContext, 5));
                }
            });
        } else {
            holder.lv_audio.setVisibility(View.GONE);
        }

        /** 文件列表 数据绑定 */
        if (null != model.attachments && model.attachments.size() > 0) {
            holder.lv_options.setVisibility(View.VISIBLE);
//            optionAdapter = new ListOrDetailsOptionsAdapter(mContext, model.attachments);
//            holder.lv_options.setAdapter(optionAdapter);
            holder.lv_options.setAdapter(new NestListViewAdapter<Attachment>(R.layout.item_dynamic_listorlist, model.attachments) {
                @Override
                public void onBind(int pos, Attachment ben, NestViewHolder holder) {
                    holder.setText(R.id.tv_image_name, ben.getName());
                    holder.setText(R.id.tv_image_size, "大小:" + Utils.FormetFileSize(Long.valueOf(ben.getSize())));
                    holder.setImageResource(R.id.iv_image, Global.getAttachmentIcon(ben.originalName));
                    ben.setPreviewAction(mContext, holder.getView(R.id.iv_image));
                }
            });
        } else {
            holder.lv_options.setVisibility(View.GONE);
        }

        /** 绑定图片与GridView监听 */
        if (null != model.imgAttachments && model.imgAttachments.size() > 0) {
            holder.layout_gridview.setVisibility(View.VISIBLE);
            if (gridViewAdapter == null)
                gridViewAdapter = new ListOrDetailsGridViewAdapter(mContext);
            gridViewAdapter.setData(model.imgAttachments);
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

        /** 绑定评论数据(文字 语音) */
        if (null != model.comments && model.comments.size() > 0) {
            holder.layout_comment.setVisibility(View.VISIBLE);
//            commentAdapter = new ListOrDetailsCommentAdapter(mContext, audioCb);
//            commentAdapter.setData(model.comments);
//            holder.lv_comment.setAdapter(commentAdapter);
//
//            /*长按删除*/
//            holder.lv_comment.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//                @Override
//                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                    crolView.deleteCommentEmbl(model.comments.get(position).id);
//                    return false;
//                }
//            });
            holder.lv_comment.setAdapter(new NestListViewAdapter<CommentModel>(R.layout.item_comment, model.comments) {
                @Override
                public void onBind(final int pos, final CommentModel ben, NestViewHolder holder) {
                    holder.setText(R.id.tv_name, ben.creatorName + ": ");
                    TextView title = holder.getView(R.id.tv_title);
                    title.setText(ben.title);
                    final TextView tv_calls = holder.getView(iv_calls);
                    LinearLayout layout_audio = holder.getView(R.id.layout_audio);
                    TextView tv_audio_length = holder.getView(R.id.tv_audio_length);
                    /** 如果有语音 */
                    ben.setRecordUI(layout_audio, tv_calls, tv_audio_length);
                     /*点击播放录音*/
                    tv_calls.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            audioCb.playVoice(ben.audioInfo, tv_calls);
                        }
                    });
                    title.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            crolView.deleteCommentEmbl(model.comments.get(pos).id);
                            return false;
                        }
                    });
                }
            });

        } else {
            holder.layout_comment.setVisibility(View.GONE);
        }

        /** 评论发送 */
        holder.iv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crolView.commentEmbl(model.id, groupPosition, childPosition);
            }
        });

        /** 查看定位地址 */
        holder.tv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (model.location != null && null != model.location.loc) {
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
                audioCb.playVoice(audioModel, iv_phone_call);
            }
        });

        return convertView;
    }

    class Holder {
        RoundImageView iv_heading;
        TextView tv_name;        /*创建人*/
        TextView tv_address;     /*地址*/
        TextView tv_contact;     /*联系人*/
        TextView tv_create_time; /*创建时间*/
        TextView tv_toast;       /*通知人员*/
        TextView tv_memo;        /*内容*/
        TextView tv_kind;        /*跟进类型*/
        TextView tv_last_time;   /*下次跟进时间*/
        TextView tv_audio_length;
        TextView iv_phone_call;

        LinearLayout ll_web;
        LinearLayout layout_comment;
        LinearLayout layout_address;
        LinearLayout layout_lasttime;
        LinearLayout layout_phonely;
        NestListView lv_comment; /*评论区*/
        NestListView lv_audio;   /*语音录音区*/
        NestListView lv_options; /*文件列表区*/
        GridView layout_gridview;    /*图片9宫格区*/
        ImageView iv_comment;        /*评论按钮*/
        ImageView iv_lasttime;     /*下次跟进图标*/

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
