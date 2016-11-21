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

import com.loyo.oa.dropdownmenu.view.DoubleListView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.commonview.CommonHtmlUtils;
import com.loyo.oa.v2.activityui.commonview.CommonImageView;
import com.loyo.oa.v2.activityui.commonview.CommonTextVew;
import com.loyo.oa.v2.activityui.commonview.MapSingleView;
import com.loyo.oa.v2.activityui.customer.CustomerDetailInfoActivity_;
import com.loyo.oa.v2.activityui.customer.CustomerManagerActivity;
import com.loyo.oa.v2.activityui.customer.model.ImgAndText;
import com.loyo.oa.v2.activityui.followup.model.FollowUpListModel;
import com.loyo.oa.v2.activityui.followup.viewcontrol.AudioPlayCallBack;
import com.loyo.oa.v2.activityui.followup.viewcontrol.FollowUpListView;
import com.loyo.oa.v2.activityui.other.PreviewImageListActivity;
import com.loyo.oa.v2.activityui.signinnew.adapter.ListOrDetailsAudioAdapter;
import com.loyo.oa.v2.activityui.signinnew.model.SigninNewListModel;
import com.loyo.oa.v2.activityui.signinnew.viewcontrol.SigninNewListView;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.CusGridView;
import com.loyo.oa.v2.customview.CustomerListView;
import com.loyo.oa.v2.customview.RoundImageView;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * 【我的跟进列表】adapter
 * Created by yyy on 16/11/12.
 */

public class FollowUpListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<FollowUpListModel> listModel;
    private FollowUpListView viewCrol;
    private AudioPlayCallBack audioCallBack;

    private ListOrDetailsGridViewAdapter gridViewAdapter;  /* 九宫格附件 */
    private ListOrDetailsCommentAdapter commentAdapter;    /* 评论区域 */
    private ListOrDetailsAudioAdapter audioAdapter;        /* 录音语音 */
    private ListOrDetailsOptionsAdapter optionAdapter;     /* 文件区域 */

    public FollowUpListAdapter(Context mContext, ArrayList<FollowUpListModel> listModel, FollowUpListView viewCrol,AudioPlayCallBack audioCallBack) {
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
        final FollowUpListModel followUpListModel = listModel.get(position);
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_followup_list, null);
            holder.iv_heading = (RoundImageView) convertView.findViewById(R.id.iv_heading);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_contact = (TextView) convertView.findViewById(R.id.tv_contact);
            holder.tv_kind = (TextView) convertView.findViewById(R.id.tv_kind);
            holder.tv_create_time = (TextView) convertView.findViewById(R.id.tv_create_time);
            holder.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
            holder.tv_toast = (TextView) convertView.findViewById(R.id.tv_toast);
            holder.tv_memo = (TextView) convertView.findViewById(R.id.tv_memo);
            holder.tv_customer = (TextView) convertView.findViewById(R.id.tv_customer);
            holder.iv_comment = (ImageView) convertView.findViewById(R.id.iv_comment);
            holder.layout_gridview = (CusGridView) convertView.findViewById(R.id.layout_gridview);
            holder.lv_comment = (CustomerListView) convertView.findViewById(R.id.lv_comment);
            holder.lv_audio = (CustomerListView) convertView.findViewById(R.id.lv_audio);
            holder.lv_options = (CustomerListView) convertView.findViewById(R.id.lv_options);
            holder.layout_comment = (LinearLayout) convertView.findViewById(R.id.layout_comment);
            holder.layout_address = (LinearLayout) convertView.findViewById(R.id.layout_address);
            holder.ll_web = (LinearLayout) convertView.findViewById(R.id.ll_web);
            holder.layout_customer = (LinearLayout) convertView.findViewById(R.id.layout_customer);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.iv_comment.setOnTouchListener(Global.GetTouch());
        ImageLoader.getInstance().displayImage(followUpListModel.creator.avatar, holder.iv_heading);
        holder.tv_name.setText(followUpListModel.creator.name);
        holder.tv_contact.setText(TextUtils.isEmpty(followUpListModel.contactName) ? "无联系人信息" : followUpListModel.contactName);
        holder.tv_customer.setText(followUpListModel.customerName);
        holder.tv_create_time.setText(DateTool.timet(followUpListModel.createAt + "", "yyyy-MM-dd hh:mm"));
        holder.tv_kind.setText(TextUtils.isEmpty(followUpListModel.typeName) ? "无" : "# "+followUpListModel.typeName);

        /** 设置跟进内容 */
        if(null != followUpListModel.content && !TextUtils.isEmpty(followUpListModel.content)){
            if(followUpListModel.content.contains("<p>")){
                holder.setContent(holder.ll_web, followUpListModel.content);
            }else{
                holder.tv_memo.setVisibility(View.VISIBLE);
                holder.tv_memo.setText(followUpListModel.content);
            }
        }

        /** 客户姓名 */
        if(null != followUpListModel.customerName && !TextUtils.isEmpty(followUpListModel.customerName)){
            holder.layout_customer.setVisibility(View.VISIBLE);
            holder.tv_customer.setText(followUpListModel.customerName);
            holder.tv_customer.setOnTouchListener(Global.GetTouch());
        }else{
            holder.layout_customer.setVisibility(View.GONE);
        }

        /** 客户地址 */
        if(null != followUpListModel.location.addr && !TextUtils.isEmpty(followUpListModel.location.addr)){
            holder.layout_address.setVisibility(View.VISIBLE);
            holder.tv_address.setText(followUpListModel.location.addr);
            holder.tv_address.setOnTouchListener(Global.GetTouch());
        }else{
            holder.layout_address.setVisibility(View.GONE);
        }


        /** @的相关人员 */
        if(null != followUpListModel.atNameAndDepts){
            holder.tv_toast.setText("@" + followUpListModel.atNameAndDepts);
        }else{
            holder.tv_toast.setVisibility(View.GONE);
        }

        /** 录音语音 */
        if(null != followUpListModel.audioInfo){
            holder.lv_audio.setVisibility(View.VISIBLE);
            audioAdapter = new ListOrDetailsAudioAdapter(mContext,followUpListModel.audioInfo,audioCallBack);
            holder.lv_audio.setAdapter(audioAdapter);
        }else{
            holder.lv_audio.setVisibility(View.GONE);
        }

        /** 文件列表 数据绑定 */
        if(null != followUpListModel.attachments && followUpListModel.attachments.size() > 0){
            optionAdapter = new ListOrDetailsOptionsAdapter(mContext,followUpListModel.attachments);
            holder.lv_options.setAdapter(optionAdapter);
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
        }else{
            holder.layout_gridview.setVisibility(View.GONE);
        }

        /** 绑定评论数据 */
        if (null != followUpListModel.comments && followUpListModel.comments.size() > 0) {
            holder.layout_comment.setVisibility(View.VISIBLE);
            commentAdapter = new ListOrDetailsCommentAdapter(mContext, followUpListModel.comments,audioCallBack);
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
                viewCrol.commentEmbl(position);
            }
        });

        /** 查看定位地址 */
        holder.tv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null != followUpListModel.location.loc){
                    Intent mIntent = new Intent(mContext, MapSingleView.class);
                    mIntent.putExtra("la", Double.valueOf(followUpListModel.location.loc[0]));
                    mIntent.putExtra("lo", Double.valueOf(followUpListModel.location.loc[1]));
                    mIntent.putExtra("address",followUpListModel.location.addr);
                    mContext.startActivity(mIntent);
                }else{
                    Toast.makeText(mContext,"GPS坐标不全!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        /** 查看客户详情 */
        holder.tv_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("Id", followUpListModel.customerId);
                intent.putExtra(ExtraAndResult.EXTRA_TYPE, CustomerManagerActivity.CUSTOMER_MY);
                intent.setClass(mContext, CustomerDetailInfoActivity_.class);
                mContext.startActivity(intent);
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

        LinearLayout ll_web;
        LinearLayout layout_comment;
        LinearLayout layout_address;
        LinearLayout layout_customer;
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
