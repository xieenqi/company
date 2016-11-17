package com.loyo.oa.v2.activityui.signinnew;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.commonview.CommonHtmlUtils;
import com.loyo.oa.v2.activityui.commonview.CommonImageView;
import com.loyo.oa.v2.activityui.commonview.CommonTextVew;
import com.loyo.oa.v2.activityui.customer.model.ImgAndText;
import com.loyo.oa.v2.activityui.followup.MsgAudiomMenu;
import com.loyo.oa.v2.activityui.followup.adapter.ListOrDetailsCommentAdapter;
import com.loyo.oa.v2.activityui.followup.adapter.ListOrDetailsGridViewAdapter;
import com.loyo.oa.v2.activityui.followup.adapter.ListOrDetailsOptionsAdapter;
import com.loyo.oa.v2.activityui.signinnew.adapter.ListOrDetailsAudioAdapter;
import com.loyo.oa.v2.activityui.signinnew.model.SigninNewListModel;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.ActionSheetDialog;
import com.loyo.oa.v2.customview.CusGridView;
import com.loyo.oa.v2.customview.CustomerListView;
import com.loyo.oa.v2.customview.RoundImageView;
import com.loyo.oa.v2.point.ISigninNeworFollowUp;
import com.loyo.oa.v2.tool.AnimationCommon;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * 【拜访详情】
 * Created by yyy on 16/11/10.
 */

public class SigninNewDetailsActivity extends BaseActivity implements View.OnClickListener,MsgAudiomMenu.MsgAudioMenuCallBack {


    private ScrollView layout_scrollview;

    private TextView tv_title;
    private TextView tv_toast;
    private TextView tv_address;           /* 地址 */
    private TextView tv_position;          /* 定位 */
    private TextView tv_name;              /* 姓名 */
    private TextView tv_kind;              /* 方式 */
    private TextView tv_customer_name;     /* 客户名字 */
    private TextView tv_contact_name;      /* 联系人 */
    private TextView tv_pc;                /* 偏差 */
    private TextView tv_time;              /* 时间 */
    private RoundImageView iv_heading;     /* 头像 */

    private CustomerListView lv_comment;
    private CustomerListView lv_options;
    private CustomerListView lv_audio;
    private CusGridView gv_image;

    private LinearLayout layout_touch;
    private LinearLayout ll_web;
    private LinearLayout layout_enclosure;
    private LinearLayout layout_bottom_menu;
    private LinearLayout layout_comment;
    private LinearLayout layout_back;
    private ImageView iv_comment;

    private float mPosX;
    private float mPosY;
    private float mCurPosX;
    private float mCurPosY;

    private String id;  /* 查询详情id */
    private SigninNewListModel mSigninDelModel;

    private ListOrDetailsCommentAdapter commentAdapter;  /* 评论区Adapter */
    private ListOrDetailsGridViewAdapter imageAdapter;   /* 图片9宫格Adapter */
    private ListOrDetailsOptionsAdapter  optionAdapter;  /* 文件列表Adapter */
    private ListOrDetailsAudioAdapter audioAdapter;        /* 录音语音 */

    private MsgAudiomMenu msgAudiomMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signinnew_details);
        initUI();
    }

    /**
     * 适配器绑定
     * */
    private void bindAdapter(){

        /*录音语音*/
        if(null != mSigninDelModel.audioInfo){
            lv_audio.setVisibility(View.VISIBLE);
            audioAdapter = new ListOrDetailsAudioAdapter(mContext,mSigninDelModel.audioInfo);
            lv_audio.setAdapter(audioAdapter);
        }

        /*评论数据绑定*/
        if(null == commentAdapter){
            commentAdapter = new ListOrDetailsCommentAdapter(mContext,mSigninDelModel.comments);
            lv_comment.setAdapter(commentAdapter);
        }else{
            commentAdapter.notifyDataSetChanged();
        }

        /*文件数据绑定*/
        if (null != mSigninDelModel.attachments && mSigninDelModel.attachments.size() > 0) {
            if (null == optionAdapter) {
                optionAdapter = new ListOrDetailsOptionsAdapter(mContext, mSigninDelModel.attachments);
                lv_options.setAdapter(optionAdapter);
            } else {
                optionAdapter.notifyDataSetChanged();
            }
        }

        /*gridView数据绑定*/
        if (null != mSigninDelModel.imageAttachments && mSigninDelModel.imageAttachments.size() > 0) {
            if(null == imageAdapter){
                if (null != mSigninDelModel.imageAttachments && mSigninDelModel.imageAttachments.size() > 0)
                    gv_image.setVisibility(View.VISIBLE);
                    imageAdapter = new ListOrDetailsGridViewAdapter(mContext,mSigninDelModel.imageAttachments);
                    gv_image.setAdapter(imageAdapter);
            }else{
                imageAdapter.notifyDataSetChanged();
            }
        }
    }

    private void initUI() {
        id = getIntent().getStringExtra("id");

        layout_touch = (LinearLayout) findViewById(R.id.layout_touch);
        layout_enclosure = (LinearLayout) findViewById(R.id.layout_enclosure);
        layout_back = (LinearLayout) findViewById(R.id.layout_back);

        ll_web = (LinearLayout) findViewById(R.id.ll_web);
        layout_bottom_menu = (LinearLayout) findViewById(R.id.layout_bottom_menu);
        layout_comment = (LinearLayout) findViewById(R.id.layout_comment);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_position = (TextView) findViewById(R.id.tv_position);
        tv_kind = (TextView) findViewById(R.id.tv_kind);
        tv_contact_name = (TextView) findViewById(R.id.tv_contact_name);
        tv_pc = (TextView) findViewById(R.id.tv_pc);
        tv_toast = (TextView) findViewById(R.id.tv_toast);
        tv_customer_name = (TextView) findViewById(R.id.tv_customer_name);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_time = (TextView) findViewById(R.id.tv_time);
        iv_comment = (ImageView) findViewById(R.id.iv_comment);
        iv_heading = (RoundImageView) findViewById(R.id.iv_heading);
        layout_scrollview = (ScrollView) findViewById(R.id.layout_scrollview);
        lv_comment = (CustomerListView) findViewById(R.id.lv_comment);
        lv_options = (CustomerListView) findViewById(R.id.lv_options);
        lv_audio = (CustomerListView) findViewById(R.id.lv_audio);
        gv_image = (CusGridView) findViewById(R.id.layout_gridview);

        iv_comment.setOnClickListener(this);
        layout_back.setOnClickListener(this);

        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText("拜访详情");

        msgAudiomMenu = new MsgAudiomMenu(mContext, this);
        layout_bottom_menu.addView(msgAudiomMenu);

        requestDetails();
    }

    /**
     * 评论删除
     * */
    private void deleteComment(String id){
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISigninNeworFollowUp.class).deleteComment(id, new RCallback<Object>() {
            @Override
            public void success(Object object, Response response) {
                HttpErrorCheck.checkResponse("评论", response);
                requestDetails();
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
                super.failure(error);
            }
        });
    }

    /**
     * 评论操作
     * */
    private void requestComment(String content){
        HashMap<String, Object> map = new HashMap<>();
        map.put("bizzId", id);
        map.put("title", content);
        map.put("commentType",1); //1文本 2语音
        map.put("bizzType", 1);   //1拜访 2跟进
        //map.put("audioInfo", "");//语音信息
        LogUtil.dee("评论参数:"+MainApp.gson.toJson(map));
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISigninNeworFollowUp.class).requestComment(map, new RCallback<Object>() {
            @Override
            public void success(Object object, Response response) {
                HttpErrorCheck.checkResponse("评论", response);
                msgAudiomMenu.closeMenu();
                requestDetails();
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
                super.failure(error);
            }
        });
    }

    private void bindData(){
        setContent(ll_web," ");
        bindAdapter();

        if(TextUtils.isEmpty(mSigninDelModel.creator.avatar)){
            ImageLoader.getInstance().displayImage(mSigninDelModel.creator.avatar,iv_heading);
        }

        tv_name.setText(mSigninDelModel.creator.name);
        tv_address.setText(TextUtils.isEmpty(mSigninDelModel.address) ? "无地址数据" : mSigninDelModel.address);
        tv_toast.setText(mSigninDelModel.atNameAndDepts);
        tv_customer_name.setText(mSigninDelModel.customerName);
        tv_position.setText(mSigninDelModel.position);
        tv_pc.setText(mSigninDelModel.offsetDistance+"");
        tv_time.setText(DateTool.timet(mSigninDelModel.createdAt+"","yyyy-MM-dd hh:mm:ss"));

        /** 绑定评论数据 */
        if (null != mSigninDelModel.comments && mSigninDelModel.comments.size() > 0) {
            layout_comment.setVisibility(View.VISIBLE);
            commentAdapter = new ListOrDetailsCommentAdapter(mContext, mSigninDelModel.comments);
            lv_comment.setAdapter(commentAdapter);

            /*长按删除*/
            lv_comment.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    ActionSheetDialog dialog = new ActionSheetDialog(mContext).builder();
                    dialog.addSheetItem("删除评论", ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
                        @Override
                        public void onClick(int which) {
                            deleteComment(mSigninDelModel.comments.get(position).id);
                        }
                    });
                    dialog.show();

                    return false;
                }
            });

        } else {
            layout_comment.setVisibility(View.GONE);
        }

    }

    /**
     * 获取详情数据
     * */
    private void requestDetails(){
        showLoading("");
        HashMap<String,Object> map = new HashMap<>();
        map.put("split",true);
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISigninNeworFollowUp.class).getSigninDetails(id,map, new RCallback<BaseBeanT<SigninNewListModel>>() {
            @Override
            public void success(BaseBeanT<SigninNewListModel> signinNewListModel, Response response) {
                HttpErrorCheck.checkResponse("拜访详情", response);
                mSigninDelModel = signinNewListModel.data;
                bindData();
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
                super.failure(error);
            }
        });
    }

    /**
     * 设置图文混编
     */
    public void setContent(LinearLayout layout, String content) {
        layout.removeAllViews();
        for (final ImgAndText ele : CommonHtmlUtils.Instance().checkContentList(content)) {
            if (ele.type.startsWith("img")) {
                CommonImageView img = new CommonImageView(SigninNewDetailsActivity.this, ele.data);
                layout.addView(img);
            } else {
                CommonTextVew tex = new CommonTextVew(SigninNewDetailsActivity.this, ele.data);
                layout.addView(tex);
            }
        }
        layout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            /*返回*/
            case R.id.layout_back:
                finish();
                break;

        }
    }

    /**
     * 回调发送评论
     * */
    @Override
    public void sendMsg(EditText editText) {
        if (TextUtils.isEmpty(editText.getText().toString())) {
            Toast("请输入评论内容!");
            return;
        }
        requestComment(editText.getText().toString());
    }
}
