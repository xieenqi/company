package com.loyo.oa.v2.activityui.followup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.clue.ClueDetailActivity;
import com.loyo.oa.v2.activityui.commonview.AudioPlayer;
import com.loyo.oa.v2.activityui.commonview.CommonHtmlUtils;
import com.loyo.oa.v2.activityui.commonview.CommonImageView;
import com.loyo.oa.v2.activityui.commonview.CommonTextVew;
import com.loyo.oa.v2.activityui.commonview.MapSingleView;
import com.loyo.oa.v2.activityui.commonview.MsgAudiomMenu;
import com.loyo.oa.v2.activityui.customer.CustomerDetailInfoActivity_;
import com.loyo.oa.v2.activityui.customer.CustomerManagerActivity;
import com.loyo.oa.v2.activityui.customer.model.ImgAndText;
import com.loyo.oa.v2.activityui.followup.adapter.ListOrDetailsCommentAdapter;
import com.loyo.oa.v2.activityui.followup.adapter.ListOrDetailsGridViewAdapter;
import com.loyo.oa.v2.activityui.followup.adapter.ListOrDetailsOptionsAdapter;
import com.loyo.oa.v2.activityui.followup.model.FollowUpListModel;
import com.loyo.oa.v2.activityui.followup.viewcontrol.AudioPlayCallBack;
import com.loyo.oa.v2.activityui.other.PreviewImageListActivity;
import com.loyo.oa.v2.activityui.signinnew.adapter.ListOrDetailsAudioAdapter;
import com.loyo.oa.v2.activityui.signinnew.model.AudioModel;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.Record;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.ActionSheetDialog;
import com.loyo.oa.v2.customview.CusGridView;
import com.loyo.oa.v2.customview.CustomerListView;
import com.loyo.oa.v2.customview.RoundImageView;
import com.loyo.oa.v2.point.ISigninNeworFollowUp;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * 【跟进详情】
 * Created by yyy on 16/11/10.
 */

public class FollowUpDetailsActivity extends BaseActivity implements View.OnClickListener, MsgAudiomMenu.MsgAudioMenuCallBack, AudioPlayCallBack {


    private ScrollView layout_scrollview;

    private TextView tv_title;
    private TextView voiceView;
    private TextView tv_name;              /* 姓名 */
    private TextView tv_kind;              /* 方式 */
    private TextView tv_toast;             /* 通知人员 */
    private TextView tv_contact;           /* 联系人 */
    private TextView tv_time;              /* 时间 */
    private TextView tv_customername;      /* 客户姓名 */
    private TextView tv_address;           /* 位置定位 */
    private TextView tv_clue;              /* 线索 */
    private TextView iv_phone_call;
    private TextView tv_audio_length;
    private TextView tv_memo;
    private RoundImageView iv_heading;     /* 头像 */

    private CustomerListView lv_comment;
    private CustomerListView lv_options;
    private CustomerListView lv_audio;
    private CusGridView gv_image;

    private LinearLayout layout_touch;
    private LinearLayout ll_web;
    private LinearLayout layout_enclosure;
    private LinearLayout layout_comment;
    private LinearLayout layout_back;
    private LinearLayout layout_bottom_menu;
    private LinearLayout layout_address;
    private LinearLayout layout_customer;
    private LinearLayout layout_clue;
    private LinearLayout layout_phonely;
    private ImageView iv_comment;

    private float mPosX;
    private float mPosY;
    private float mCurPosX;
    private float mCurPosY;
    private String id;

    private ListOrDetailsCommentAdapter commentAdapter;  /* 评论区Adapter */
    private ListOrDetailsGridViewAdapter imageAdapter;   /* 图片9宫格Adapter */
    private ListOrDetailsOptionsAdapter optionAdapter;   /* 附件列表Adapter */
    private ListOrDetailsAudioAdapter audioAdapter;        /* 录音语音 */

    private FollowUpListModel mFollowUpDelModel;
    private MsgAudiomMenu msgAudiomMenu;
    private String uuid = StringUtil.getUUID();

    /*录音播放相关*/
    private LinearLayout layout_bottom_voice;
    private int playVoiceSize = 0;
    private AudioPlayer audioPlayer;
    private TextView lastView;
    private String lastUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followup_details);
        initUI();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        audioPlayer.killPlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(null != voiceView)
            audioPlayer.audioPause(voiceView);
    }

    /**
     * 适配器绑定
     */
    private void bindAdapter() {

        /*录音语音*/
        if (null != mFollowUpDelModel && null != mFollowUpDelModel.audioInfo) {
            lv_audio.setVisibility(View.VISIBLE);
            audioAdapter = new ListOrDetailsAudioAdapter(mContext, mFollowUpDelModel.audioInfo, this);
            lv_audio.setAdapter(audioAdapter);
        }

        /*评论数据绑定*/
        if (null != mFollowUpDelModel && null != mFollowUpDelModel.comments && mFollowUpDelModel.comments.size() > 0) {
            if (null == commentAdapter) {
                commentAdapter = new ListOrDetailsCommentAdapter(mContext, mFollowUpDelModel.comments, this);
                lv_comment.setAdapter(commentAdapter);
            } else if (commentAdapter != null) {
                commentAdapter.notifyDataSetChanged();
            }
        } else {

        }

        /*文件数据绑定*/
        if (null != mFollowUpDelModel && null != mFollowUpDelModel.attachments && mFollowUpDelModel.attachments.size() > 0) {
            if (null == optionAdapter) {
                optionAdapter = new ListOrDetailsOptionsAdapter(mContext, mFollowUpDelModel.attachments);
                lv_options.setAdapter(optionAdapter);
            } else {
                optionAdapter.notifyDataSetChanged();
            }
        }

        /*gridView数据绑定*/
        if (null != mFollowUpDelModel && null != mFollowUpDelModel.imgAttachments && mFollowUpDelModel.imgAttachments.size() > 0) {
            if (null == imageAdapter) {
                if (null != mFollowUpDelModel.imgAttachments && mFollowUpDelModel.imgAttachments.size() > 0)
                    gv_image.setVisibility(View.VISIBLE);
                imageAdapter = new ListOrDetailsGridViewAdapter(mContext, mFollowUpDelModel.imgAttachments);
                gv_image.setAdapter(imageAdapter);
            } else {
                imageAdapter.notifyDataSetChanged();
            }
        }
    }

    private void initUI() {
        id = getIntent().getStringExtra("id");
        audioPlayer = new AudioPlayer(mContext);
        audioPlayer.initPlayer();
        layout_comment = (LinearLayout) findViewById(R.id.layout_comment);
        layout_touch = (LinearLayout) findViewById(R.id.layout_touch);
        layout_enclosure = (LinearLayout) findViewById(R.id.layout_enclosure);
        layout_bottom_menu = (LinearLayout) findViewById(R.id.layout_bottom_menu);
        layout_bottom_voice = (LinearLayout) findViewById(R.id.layout_bottom_voice);
        layout_customer = (LinearLayout) findViewById(R.id.layout_customer);
        layout_clue = (LinearLayout) findViewById(R.id.layout_clue);
        layout_phonely = (LinearLayout) findViewById(R.id.layout_phonely);
        layout_address = (LinearLayout) findViewById(R.id.layout_address);
        layout_back = (LinearLayout) findViewById(R.id.layout_back);
        ll_web = (LinearLayout) findViewById(R.id.ll_web);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_contact = (TextView) findViewById(R.id.tv_contact);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_kind = (TextView) findViewById(R.id.tv_kind);
        tv_clue = (TextView) findViewById(R.id.tv_clue);
        tv_memo = (TextView) findViewById(R.id.tv_memo);
        iv_phone_call = (TextView) findViewById(R.id.iv_phone_call);
        tv_audio_length = (TextView) findViewById(R.id.tv_audio_length);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_customername = (TextView) findViewById(R.id.tv_customername);
        tv_toast = (TextView) findViewById(R.id.tv_toast);
        iv_comment = (ImageView) findViewById(R.id.iv_comment);
        iv_heading = (RoundImageView) findViewById(R.id.iv_heading);
        layout_scrollview = (ScrollView) findViewById(R.id.layout_scrollview);
        lv_comment = (CustomerListView) findViewById(R.id.lv_comment);
        lv_options = (CustomerListView) findViewById(R.id.lv_options);
        lv_audio = (CustomerListView) findViewById(R.id.lv_audio);
        gv_image = (CusGridView) findViewById(R.id.layout_gridview);
        layout_back.setOnClickListener(this);

        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText("跟进详情");

        msgAudiomMenu = new MsgAudiomMenu(mContext, this, uuid);
        layout_bottom_menu.addView(msgAudiomMenu);
        requestDetails();
    }

    private void bindData() {
        bindAdapter();

        if (TextUtils.isEmpty(mFollowUpDelModel.creator.avatar)) {
            ImageLoader.getInstance().displayImage(mFollowUpDelModel.creator.avatar, iv_heading);
        }

        tv_name.setText(mFollowUpDelModel.creator.name);
        tv_contact.setText(mFollowUpDelModel.contactName);
        tv_customername.setText(mFollowUpDelModel.customerName);
        tv_time.setText(DateTool.getDiffTime(mFollowUpDelModel.createAt));

        /** 设置跟进内容 */
        if(null != mFollowUpDelModel.content && !TextUtils.isEmpty(mFollowUpDelModel.content)){
            if(mFollowUpDelModel.content.contains("<p>")){
                setContent(ll_web, mFollowUpDelModel.content);
            }else{
                tv_memo.setVisibility(View.VISIBLE);
                tv_memo.setText(mFollowUpDelModel.content);
            }
        }

        /** @通知人员 */
        if(null != mFollowUpDelModel.atNameAndDepts && !TextUtils.isEmpty(mFollowUpDelModel.atNameAndDepts)){
            tv_toast.setVisibility(View.VISIBLE);
            tv_toast.setText("@"+mFollowUpDelModel.atNameAndDepts);
        }

        /** 线索 */
        if(null != mFollowUpDelModel.salesleadCompanyName && !TextUtils.isEmpty(mFollowUpDelModel.salesleadCompanyName)){
            layout_clue.setVisibility(View.VISIBLE);
            tv_clue.setText(mFollowUpDelModel.salesleadCompanyName);
            tv_clue.setOnTouchListener(Global.GetTouch());
        }

        /** 客户姓名 */
        if(null != mFollowUpDelModel.customerName && !TextUtils.isEmpty(mFollowUpDelModel.customerName)){
            layout_customer.setVisibility(View.VISIBLE);
            tv_customername.setText(mFollowUpDelModel.customerName);
            tv_customername.setOnTouchListener(Global.GetTouch());
        }

        /** 客户地址 */
        if(null != mFollowUpDelModel.location.addr && !TextUtils.isEmpty(mFollowUpDelModel.location.addr)){
            layout_address.setVisibility(View.VISIBLE);
            tv_address.setText(mFollowUpDelModel.location.addr);
            tv_address.setOnTouchListener(Global.GetTouch());
        }else{
            layout_address.setVisibility(View.GONE);
        }


        /** 绑定评论数据 */
        if (null != mFollowUpDelModel.comments && mFollowUpDelModel.comments.size() > 0) {
            layout_comment.setVisibility(View.VISIBLE);
            commentAdapter = new ListOrDetailsCommentAdapter(mContext, mFollowUpDelModel.comments, this);
            lv_comment.setAdapter(commentAdapter);

            /*长按删除*/
            lv_comment.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    ActionSheetDialog dialog = new ActionSheetDialog(mContext).builder();
                    dialog.addSheetItem("删除评论", ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
                        @Override
                        public void onClick(int which) {
                            deleteComment(mFollowUpDelModel.comments.get(position).id);
                        }
                    });
                    dialog.show();
                    return false;
                }
            });

        } else {
            layout_comment.setVisibility(View.GONE);
        }


        /** 图片预览 */
        gv_image.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", mFollowUpDelModel.imgAttachments);
                bundle.putInt("position", position);
                bundle.putBoolean("isEdit", false);
                MainApp.getMainApp().startActivityForResult((Activity) mContext, PreviewImageListActivity.class,
                        MainApp.ENTER_TYPE_BUTTOM, FinalVariables.REQUEST_DEAL_ATTACHMENT, bundle);
            }
        });

        /** 查看定位地址 */
        tv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null != mFollowUpDelModel.location.loc){
                    Intent mIntent = new Intent(mContext, MapSingleView.class);
                    mIntent.putExtra("la", Double.valueOf(mFollowUpDelModel.location.loc[1]));
                    mIntent.putExtra("lo", Double.valueOf(mFollowUpDelModel.location.loc[0]));
                    mIntent.putExtra("address",mFollowUpDelModel.location.addr);
                    mContext.startActivity(mIntent);
                }else{
                    Toast.makeText(mContext,"GPS坐标不全!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        /** 查看客户详情 */
        tv_customername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("Id", mFollowUpDelModel.customerId);
                intent.putExtra(ExtraAndResult.EXTRA_TYPE, CustomerManagerActivity.CUSTOMER_MMP);
                intent.setClass(mContext, CustomerDetailInfoActivity_.class);
                mContext.startActivity(intent);
            }
        });

        /** 进入线索详情 */
        tv_clue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent();
                mIntent.putExtra(ExtraAndResult.IS_TEAM, false);
                mIntent.putExtra(ExtraAndResult.EXTRA_ID, /* 线索id */ mFollowUpDelModel.sealsleadId);
                mIntent.setClass(mContext, ClueDetailActivity.class);
                mContext.startActivity(mIntent);
            }
        });

        /** 电话录音设置 */
        if(null != mFollowUpDelModel.audioUrl && !TextUtils.isEmpty(mFollowUpDelModel.audioUrl)){
            layout_phonely.setVisibility(View.VISIBLE);
            tv_audio_length.setText(DateTool.stringForTime(mFollowUpDelModel.audioLength * 1000));
            int audioLength = mFollowUpDelModel.audioLength;
            if (audioLength > 0 && audioLength <= 60) {
                iv_phone_call.setText("000");
            } else if (audioLength > 60 && audioLength <= 300) {
                iv_phone_call.setText("00000");
            } else if (audioLength > 300 && audioLength <= 600) {
                iv_phone_call.setText("0000000");
            } else if (audioLength > 600 && audioLength <= 1200) {
                iv_phone_call.setText("000000000");
            } else if (audioLength > 1200 && audioLength <= 1800) {
                iv_phone_call.setText("00000000000");
            } else if (audioLength > 1800 && audioLength <= 3600) {
                iv_phone_call.setText("00000000000000");
            } else if (audioLength > 3600) {
                iv_phone_call.setText("0000000000000000");
            } else {
                iv_phone_call.setText("");
            }
        }

        /** 播放通话录音 */
        iv_phone_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioModel audioModel = new AudioModel();
                audioModel.url = mFollowUpDelModel.audioUrl;
                audioModel.length = 10;
                playVoice(audioModel,iv_phone_call);
            }
        });
    }

    /**
     * 评论删除
     */
    private void deleteComment(String id) {
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
     * 获取详情数据
     */
    private void requestDetails() {
        showLoading("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("split", true);
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISigninNeworFollowUp.class).getFollowUpDetails(map, new RCallback<BaseBeanT<FollowUpListModel>>() {
            @Override
            public void success(BaseBeanT<FollowUpListModel> followuplistmodel, Response response) {
                HttpErrorCheck.checkResponse("跟进详情", response);
                if (followuplistmodel.errcode != 0) {
                    Toast("获取拜访详情出错!");
                    finish();
                } else {
                    mFollowUpDelModel = followuplistmodel.data;
                    bindData();
                }
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
     */
    private void requestComment(String content) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("bizzId", id);
        map.put("title", content);
        map.put("commentType", 1); //1文本 2语音
        map.put("bizzType", 2);   //1拜访 2跟进
//        map.put("audioInfo", "");//语音信息
        LogUtil.dee("评论参数:" + MainApp.gson.toJson(map));
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

    /**
     * 评论操作
     */
    private void requestComment(Record record) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("bizzId", id);
        map.put("commentType", 2); //1文本 2语音
        map.put("bizzType", 2);   //1拜访 2跟进
        map.put("audioInfo", record);//语音信息
        LogUtil.dee("评论参数:" + MainApp.gson.toJson(map));
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

    /**
     * 设置图文混编
     */
    public void setContent(LinearLayout layout, String content) {
        layout.removeAllViews();
        for (final ImgAndText ele : CommonHtmlUtils.Instance().checkContentList(content)) {
            if (ele.type.startsWith("img")) {
                CommonImageView img = new CommonImageView(FollowUpDetailsActivity.this, ele.data);
                layout.addView(img);
            } else {
                CommonTextVew tex = new CommonTextVew(FollowUpDetailsActivity.this, ele.data);
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
     */
    @Override
    public void sendMsg(EditText editText) {
        if (TextUtils.isEmpty(editText.getText().toString())) {
            Toast("请输入评论内容!");
            return;
        }
        requestComment(editText.getText().toString());
    }

    public void sebdRecordInfo(Record record) {
        requestComment(record);
    }

    /**
     * 列表播放语音回调
     */
    @Override
    public void playVoice(AudioModel audioModel, TextView textView) {
        if (TextUtils.isEmpty(audioModel.url)) {
            Toast("无录音资源!");
            return;
        }
        voiceView = textView;
        layout_bottom_voice.setVisibility(View.VISIBLE);
        layout_bottom_voice.removeAllViews();
        layout_bottom_voice.addView(audioPlayer);
        /*关闭上一条TextView动画*/
        if (playVoiceSize > 0) {
            if (null != lastView)
                MainApp.getMainApp().stopAnim(lastView);
        }

        if(audioPlayer.isPlaying()){
            /*点击同一条则暂停播放*/
            if (lastView == textView) {
                LogUtil.dee("同一条");
                MainApp.getMainApp().stopAnim(textView);
                audioPlayer.audioPause(textView);
                lastView = null;
            } else {
                audioPlayer.audioStart(textView);
                audioPlayer.threadPool(audioModel, textView);
                lastUrl = audioModel.url;
                lastView = textView;
            }
        }else{
            audioPlayer.audioStart(textView);
            audioPlayer.threadPool(audioModel, textView);
            lastUrl = audioModel.url;
            lastView = textView;
        }
        playVoiceSize++;
    }

}
