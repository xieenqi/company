package com.loyo.oa.v2.activityui.signin.adapter;

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

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.commonview.MapSingleView;
import com.loyo.oa.v2.activityui.followup.adapter.ListOrDetailsCommentAdapter;
import com.loyo.oa.v2.activityui.followup.adapter.ListOrDetailsGridViewAdapter;
import com.loyo.oa.v2.activityui.followup.adapter.ListOrDetailsOptionsAdapter;
import com.loyo.oa.v2.activityui.followup.viewcontrol.AudioPlayCallBack;
import com.loyo.oa.v2.activityui.other.PreviewImageListActivity;
import com.loyo.oa.v2.activityui.signin.bean.SigninNewListModel;
import com.loyo.oa.v2.activityui.signin.viewcontrol.SigninListView;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customermanagement.activity.CustomerDetailActivity;
import com.loyo.oa.v2.customview.CusGridView;
import com.loyo.oa.v2.customview.CustomerListView;
import com.loyo.oa.v2.customview.RoundImageView;
import com.loyo.oa.v2.customview.SweetAlertDialogView;
import com.loyo.oa.v2.permission.BusinessOperation;
import com.loyo.oa.v2.permission.PermissionManager;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.DecimalFormat;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 【我的拜访】adapter
 * Created by yyy on 16/11/12.
 */

public class SigninListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<SigninNewListModel> listModel;
    private SigninListView viewCrol;
    private AudioPlayCallBack audioPlayCallBack;

    private ListOrDetailsGridViewAdapter gridViewAdapter;  /* 九宫格图片 */
    private ListOrDetailsCommentAdapter commentAdapter;    /* 评论区域 */
    private ListOrDetailsAudioAdapter audioAdapter;        /* 录音语音 */
    private ListOrDetailsOptionsAdapter optionAdapter;     /* 文件区域 */
    private DecimalFormat df = new DecimalFormat("0.0");


    public SigninListAdapter(Context mContext, ArrayList<SigninNewListModel> listModel, SigninListView viewCrol, AudioPlayCallBack audioPlayCallBack) {
        this.mContext = mContext;
        this.listModel = listModel;
        this.viewCrol = viewCrol;
        this.audioPlayCallBack = audioPlayCallBack;
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
        final SigninNewListModel model = listModel.get(position);
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_sgninnew_selflist, null);
            holder.iv_heading = (RoundImageView) convertView.findViewById(R.id.iv_heading);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
            holder.tv_contact = (TextView) convertView.findViewById(R.id.tv_contact);
            holder.tv_position = (TextView) convertView.findViewById(R.id.tv_position);
            holder.tv_offset = (TextView) convertView.findViewById(R.id.tv_offset);
            holder.tv_create_time = (TextView) convertView.findViewById(R.id.tv_create_time);
            holder.tv_toast = (TextView) convertView.findViewById(R.id.tv_toast);
            holder.tv_memo = (TextView) convertView.findViewById(R.id.tv_memo);
            holder.tv_customer = (TextView) convertView.findViewById(R.id.tv_customer);
            holder.iv_comment = (ImageView) convertView.findViewById(R.id.iv_comment);
            holder.layout_gridview = (CusGridView) convertView.findViewById(R.id.layout_gridview);
            holder.lv_comment = (CustomerListView) convertView.findViewById(R.id.lv_comment);
            holder.lv_options = (CustomerListView) convertView.findViewById(R.id.lv_options);
            holder.lv_audio = (CustomerListView) convertView.findViewById(R.id.lv_audio);
            holder.layout_comment = (LinearLayout) convertView.findViewById(R.id.layout_comment);
            holder.layout_address = (LinearLayout) convertView.findViewById(R.id.layout_address);
            holder.layout_customer = (LinearLayout) convertView.findViewById(R.id.layout_customer);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.iv_comment.setOnTouchListener(Global.GetTouch());

        ImageLoader.getInstance().displayImage(model.creator.avatar, holder.iv_heading);
        holder.tv_name.setText(model.creator.name);
        holder.tv_position.setText(model.address);
        holder.tv_create_time.setText(com.loyo.oa.common.utils.DateTool.getDateTimeFriendly(model.createdAt));

        String contact = TextUtils.isEmpty(model.contactName) ? "无联系人信息" : model.contactName;
        if(null != model.contactTpl && !TextUtils.isEmpty(model.contactTpl) && !contact.equals("无联系人信息")){
            holder.tv_contact.setText(contact+"("+model.contactTpl+")");
        }else{
            holder.tv_contact.setText(contact);
        }

        /** 偏差距离,当未知显示红色 */
        if(model.distance.equals("未知")){
            holder.tv_offset.setTextColor(mContext.getResources().getColor(R.color.red));
        }else{
            holder.tv_offset.setTextColor(mContext.getResources().getColor(R.color.text99));
        }
        holder.tv_offset.setText(model.distance);


        /** 客户姓名 */
        if(null != model.customerName && !TextUtils.isEmpty(model.customerName)){
            holder.layout_customer.setVisibility(View.VISIBLE);
            holder.tv_customer.setText(model.customerName);
            holder.tv_customer.setOnTouchListener(Global.GetTouch());
        }else{
            holder.layout_customer.setVisibility(View.GONE);
        }

        /** 客户地址 */
        if(null != model.position && !TextUtils.isEmpty(model.position)){
            holder.layout_address.setVisibility(View.VISIBLE);
            holder.tv_address.setText(model.position);
            holder.layout_address.setOnTouchListener(Global.GetTouch());
        }else{
            holder.layout_address.setVisibility(View.GONE);
        }

        /** 备注内容 */
        if(null != model.memo && !TextUtils.isEmpty(model.memo)){
            holder.tv_memo.setVisibility(View.VISIBLE);
            holder.tv_memo.setText(model.memo);
        }else{
            holder.tv_memo.setVisibility(View.GONE);
        }

        /** @的相关人员 */
        if(!TextUtils.isEmpty(model.atNameAndDepts)){
            holder.tv_toast.setVisibility(View.VISIBLE);
            holder.tv_toast.setText("@" + model.atNameAndDepts);
        }else{
            holder.tv_toast.setVisibility(View.GONE);
        }

        /** 录音语音 */
        if(null != model.audioInfo){
            holder.lv_audio.setVisibility(View.VISIBLE);
            audioAdapter = new ListOrDetailsAudioAdapter(mContext,model.audioInfo,audioPlayCallBack);
            holder.lv_audio.setAdapter(audioAdapter);
        }else{
            holder.lv_audio.setVisibility(View.GONE);
        }

        /** 文件列表 数据绑定 */
        if(null != model.attachments && model.attachments.size() > 0){
            holder.lv_options.setVisibility(View.VISIBLE);
            optionAdapter = new ListOrDetailsOptionsAdapter(mContext,model.attachments);
            holder.lv_options.setAdapter(optionAdapter);
        }else{
            holder.lv_options.setVisibility(View.GONE);
        }

        /** 绑定图片与GridView监听 */
        if (null != model.imageAttachments && model.imageAttachments.size() > 0) {
            holder.layout_gridview.setVisibility(View.VISIBLE);
            gridViewAdapter = new ListOrDetailsGridViewAdapter(mContext, model.imageAttachments);
            holder.layout_gridview.setAdapter(gridViewAdapter);

            /*图片预览*/
            holder.layout_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", model.imageAttachments);
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
            commentAdapter = new ListOrDetailsCommentAdapter(mContext, model.comments,audioPlayCallBack);
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

        /** 打开评论 */
        holder.iv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewCrol.commentEmbl(position);
            }
        });

        /** 查看位置地图 */
        holder.layout_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null != model.gpsInfo && !TextUtils.isEmpty(model.gpsInfo)){
                    Intent mIntent = new Intent(mContext, MapSingleView.class);
                    String[] gps = model.gpsInfo.split(",");
                    mIntent.putExtra("la",Double.valueOf(gps[1]));
                    mIntent.putExtra("lo",Double.valueOf(gps[0]));
                    mIntent.putExtra("address",model.position);
                    mIntent.putExtra("title","签到地址");
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
                intent.putExtra(CustomerDetailActivity.KEY_ID, model.customerId);
                intent.putExtra(ExtraAndResult.EXTRA_OBJ,true);
                intent.setClass(mContext, CustomerDetailActivity.class);
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
        TextView tv_position;    /*客户定位*/
        TextView tv_offset;      /*偏差*/
        TextView tv_create_time; /*创建时间*/
        TextView tv_toast;       /*通知人员*/
        TextView tv_memo;        /*内容*/

        LinearLayout layout_comment;
        LinearLayout layout_address;
        LinearLayout layout_customer;
        CustomerListView lv_comment; /*评论区*/
        CustomerListView lv_audio;   /*语音录音区*/
        CustomerListView lv_options; /*文件区*/
        GridView layout_gridview;    /*图片9宫格区*/
        ImageView iv_comment;        /*评论按钮*/
    }
}
