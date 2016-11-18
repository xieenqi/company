package com.loyo.oa.v2.activityui.clue.adapter;

import android.app.Activity;
import android.content.Context;
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

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.clue.bean.ClueFollowUpListModel;
import com.loyo.oa.v2.activityui.clue.viewcontrol.ClueFollowUpListView;
import com.loyo.oa.v2.activityui.commonview.CommonHtmlUtils;
import com.loyo.oa.v2.activityui.commonview.CommonImageView;
import com.loyo.oa.v2.activityui.commonview.CommonTextVew;
import com.loyo.oa.v2.activityui.customer.CustomerDynamicManageActivity;
import com.loyo.oa.v2.activityui.customer.model.ImgAndText;
import com.loyo.oa.v2.activityui.followup.adapter.ListOrDetailsCommentAdapter;
import com.loyo.oa.v2.activityui.followup.adapter.ListOrDetailsGridViewAdapter;
import com.loyo.oa.v2.activityui.followup.adapter.ListOrDetailsOptionsAdapter;
import com.loyo.oa.v2.activityui.followup.model.FollowUpListModel;
import com.loyo.oa.v2.activityui.followup.viewcontrol.AudioPlayCallBack;
import com.loyo.oa.v2.activityui.followup.viewcontrol.FollowUpListView;
import com.loyo.oa.v2.activityui.other.PreviewImageListActivity;
import com.loyo.oa.v2.activityui.signinnew.adapter.ListOrDetailsAudioAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.CusGridView;
import com.loyo.oa.v2.customview.CustomerListView;
import com.loyo.oa.v2.customview.RoundImageView;
import com.loyo.oa.v2.tool.DateTool;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * 【线索下跟进】adapter
 * Created by yyy on 16/11/12.
 */

public class ClueFollowUpListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ClueFollowUpListModel> listModel;
    private ClueFollowUpListView viewCrol;
    private AudioPlayCallBack audioCallBack;

    private ListOrDetailsGridViewAdapter gridViewAdapter;  /* 九宫格附件 */
    private ListOrDetailsCommentAdapter commentAdapter;    /* 评论区域 */
    private ListOrDetailsAudioAdapter audioAdapter;        /* 录音语音 */
    private ListOrDetailsOptionsAdapter optionAdapter;     /* 文件区域 */

    public ClueFollowUpListAdapter(Context mContext, ArrayList<ClueFollowUpListModel> listModel, ClueFollowUpListView viewCrol, AudioPlayCallBack audioCallBack) {
        this.mContext = mContext;
        this.listModel = listModel;
        this.viewCrol = viewCrol;
        this.audioCallBack = audioCallBack;
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
        final ClueFollowUpListModel model = listModel.get(position);
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_clue_followup_list, null);
            holder.iv_heading = (RoundImageView) convertView.findViewById(R.id.iv_heading);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_contact = (TextView) convertView.findViewById(R.id.tv_contact);
            holder.tv_create_time = (TextView) convertView.findViewById(R.id.tv_create_time);
            holder.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
            holder.tv_toast = (TextView) convertView.findViewById(R.id.tv_toast);
            holder.tv_memo = (TextView) convertView.findViewById(R.id.tv_memo);
            holder.tv_customer = (TextView) convertView.findViewById(R.id.tv_customer);
            holder.tv_kind = (TextView) convertView.findViewById(R.id.tv_kind);
            holder.iv_comment = (ImageView) convertView.findViewById(R.id.iv_comment);
            holder.layout_gridview = (CusGridView) convertView.findViewById(R.id.layout_gridview);
            holder.lv_comment = (CustomerListView) convertView.findViewById(R.id.lv_comment);
            holder.lv_audio = (CustomerListView) convertView.findViewById(R.id.lv_audio);
            holder.lv_options = (CustomerListView) convertView.findViewById(R.id.lv_options);
            holder.layout_comment = (LinearLayout) convertView.findViewById(R.id.layout_comment);
            holder.ll_web = (LinearLayout) convertView.findViewById(R.id.ll_web);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.setContent(holder.ll_web, model.content);
        holder.iv_comment.setOnTouchListener(Global.GetTouch());
        ImageLoader.getInstance().displayImage(model.avatar, holder.iv_heading);
        holder.tv_name.setText(model.creatorName);
        holder.tv_address.setText(TextUtils.isEmpty(model.addr) ? "无定位信息" : model.addr);
        holder.tv_contact.setText(TextUtils.isEmpty(model.contactName) ? "无联系人信息" : model.contactName);
        holder.tv_create_time.setText(DateTool.timet(model.createAt + "", "MM-dd hh:mm"));
        holder.tv_kind.setText(TextUtils.isEmpty(model.typeName) ? "无" : "# "+model.typeName);

        /** @的相关人员 */
        if(null != model.atNameAndDepts && !TextUtils.isEmpty(model.atNameAndDepts)){
            holder.tv_toast.setText("@" + model.atNameAndDepts);
        }else{
            holder.tv_toast.setVisibility(View.GONE);
        }

        /** 录音语音 */
        if(null != model.audioInfo){
            holder.lv_audio.setVisibility(View.VISIBLE);
            audioAdapter = new ListOrDetailsAudioAdapter(mContext,model.audioInfo,audioCallBack);
            holder.lv_audio.setAdapter(audioAdapter);
        }else{
            holder.lv_audio.setVisibility(View.GONE);
        }

        /** 文件列表 数据绑定 */
        if(null != model.attachments && model.attachments.size() > 0){
            optionAdapter = new ListOrDetailsOptionsAdapter(mContext,model.attachments);
            holder.lv_options.setAdapter(optionAdapter);
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
        }else{
            holder.layout_gridview.setVisibility(View.GONE);
        }

        /** 绑定评论数据 */
        if (null != model.comments && model.comments.size() > 0) {
            holder.layout_comment.setVisibility(View.VISIBLE);
            commentAdapter = new ListOrDetailsCommentAdapter(mContext, model.comments,audioCallBack);
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
                viewCrol.commentEmbl(position);
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
        TextView tv_kind;        /*跟进类型*/

        LinearLayout ll_web;
        LinearLayout layout_comment;
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
