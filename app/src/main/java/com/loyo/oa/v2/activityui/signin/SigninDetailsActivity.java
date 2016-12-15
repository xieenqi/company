package com.loyo.oa.v2.activityui.signin;

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
import com.loyo.oa.v2.activityui.commonview.CommonHtmlUtils;
import com.loyo.oa.v2.activityui.commonview.CommonImageView;
import com.loyo.oa.v2.activityui.commonview.CommonTextVew;
import com.loyo.oa.v2.activityui.commonview.MapSingleView;
import com.loyo.oa.v2.activityui.customer.CustomerDetailInfoActivity_;
import com.loyo.oa.v2.activityui.customer.model.ImgAndText;
import com.loyo.oa.v2.activityui.commonview.AudioPlayer;
import com.loyo.oa.v2.activityui.commonview.MsgAudiomMenu;
import com.loyo.oa.v2.activityui.followup.adapter.ListOrDetailsCommentAdapter;
import com.loyo.oa.v2.activityui.followup.adapter.ListOrDetailsGridViewAdapter;
import com.loyo.oa.v2.activityui.followup.adapter.ListOrDetailsOptionsAdapter;
import com.loyo.oa.v2.activityui.followup.viewcontrol.AudioPlayCallBack;
import com.loyo.oa.v2.activityui.other.PreviewImageListActivity;
import com.loyo.oa.v2.activityui.signin.adapter.ListOrDetailsAudioAdapter;
import com.loyo.oa.v2.activityui.signin.bean.AudioModel;
import com.loyo.oa.v2.activityui.signin.bean.SigninNewListModel;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.Record;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.ActionSheetDialog;
import com.loyo.oa.v2.customview.CusGridView;
import com.loyo.oa.v2.customview.CustomerListView;
import com.loyo.oa.v2.customview.RoundImageView;
import com.loyo.oa.v2.customview.SweetAlertDialogView;
import com.loyo.oa.v2.permission.BusinessOperation;
import com.loyo.oa.v2.permission.PermissionManager;
import com.loyo.oa.v2.point.ISigninOrFollowUp;
import com.loyo.oa.v2.tool.BaseLoadingActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * 【拜访详情】
 * Created by yyy on 16/11/10.
 */

public class SigninDetailsActivity extends BaseLoadingActivity implements View.OnClickListener, MsgAudiomMenu.MsgAudioMenuCallBack, AudioPlayCallBack {


    private ScrollView layout_scrollview;

    private TextView tv_title;
    private TextView tv_toast;
    private TextView voiceView;
    private TextView tv_address;           /* 地址 */
    private TextView tv_position;          /* 定位 */
    private TextView tv_name;              /* 姓名 */
    private TextView tv_kind;              /* 方式 */
    private TextView tv_customer_name;     /* 客户名字 */
    private TextView tv_contact_name;      /* 联系人 */
    private TextView tv_pc;                /* 偏差 */
    private TextView tv_time;              /* 时间 */
    private TextView tv_memo;              /* 时间 */
    private RoundImageView iv_heading;     /* 头像 */

    private CustomerListView lv_comment;
    private CustomerListView lv_options;
    private CustomerListView lv_audio;
    private CusGridView gv_image;

    private LinearLayout layout_touch;
    private LinearLayout layout_enclosure;
    private LinearLayout layout_bottom_menu;
    private LinearLayout layout_comment;
    private LinearLayout layout_back;
    private LinearLayout layout_position;
    private LinearLayout layout_contact;
    private ImageView iv_comment;

    private float mPosX;
    private float mPosY;
    private float mCurPosX;
    private float mCurPosY;

    private String id;  /* 查询详情id */
    private SigninNewListModel mSigninDelModel;

    private ListOrDetailsCommentAdapter commentAdapter;  /* 评论区Adapter */
    private ListOrDetailsGridViewAdapter imageAdapter;   /* 图片9宫格Adapter */
    private ListOrDetailsOptionsAdapter optionAdapter;  /* 文件列表Adapter */
    private ListOrDetailsAudioAdapter audioAdapter;        /* 录音语音 */

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
        initUI();
    }

    @Override
    public void setLayoutView() {
        setContentView(R.layout.activity_signinnew_details);
    }

    @Override
    public void getPageData() {
        requestDetails();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        audioPlayer.killPlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != voiceView)
            audioPlayer.audioPause(voiceView);
    }


    /**
     * 适配器绑定
     */
    private void bindAdapter() {

        /*录音语音*/
        if (mSigninDelModel != null && null != mSigninDelModel.audioInfo) {
            lv_audio.setVisibility(View.VISIBLE);
            audioAdapter = new ListOrDetailsAudioAdapter(mContext, mSigninDelModel.audioInfo, this);
            lv_audio.setAdapter(audioAdapter);
        }

        /*评论数据绑定*/
        if (null == commentAdapter && mSigninDelModel != null && mSigninDelModel.comments != null) {
            commentAdapter = new ListOrDetailsCommentAdapter(mContext, mSigninDelModel.comments, this);
            lv_comment.setAdapter(commentAdapter);
        } else if (commentAdapter != null) {
            commentAdapter.notifyDataSetChanged();
        }

        /*文件数据绑定*/
        if (mSigninDelModel != null && null != mSigninDelModel.attachments && mSigninDelModel.attachments.size() > 0) {
            if (null == optionAdapter) {
                optionAdapter = new ListOrDetailsOptionsAdapter(mContext, mSigninDelModel.attachments);
                lv_options.setAdapter(optionAdapter);
            } else {
                optionAdapter.notifyDataSetChanged();
            }
        }

        /*gridView数据绑定*/
        if (mSigninDelModel != null && null != mSigninDelModel.imageAttachments && mSigninDelModel.imageAttachments.size() > 0) {
            if (null == imageAdapter) {
                if (null != mSigninDelModel.imageAttachments && mSigninDelModel.imageAttachments.size() > 0)
                    gv_image.setVisibility(View.VISIBLE);
                imageAdapter = new ListOrDetailsGridViewAdapter(mContext, mSigninDelModel.imageAttachments);
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
        layout_touch = (LinearLayout) findViewById(R.id.layout_touch);
        layout_enclosure = (LinearLayout) findViewById(R.id.layout_enclosure);
        layout_back = (LinearLayout) findViewById(R.id.layout_back);
        layout_bottom_voice = (LinearLayout) findViewById(R.id.layout_bottom_voice);
        layout_position = (LinearLayout) findViewById(R.id.layout_position);
        layout_contact = (LinearLayout) findViewById(R.id.layout_contact);

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
        tv_memo = (TextView) findViewById(R.id.tv_memo);
        iv_comment = (ImageView) findViewById(R.id.iv_comment);
        iv_heading = (RoundImageView) findViewById(R.id.iv_heading);
        layout_scrollview = (ScrollView) findViewById(R.id.layout_scrollview);
        lv_comment = (CustomerListView) findViewById(R.id.lv_comment);
        lv_options = (CustomerListView) findViewById(R.id.lv_options);
        lv_audio = (CustomerListView) findViewById(R.id.lv_audio);
        gv_image = (CusGridView) findViewById(R.id.layout_gridview);

        iv_comment.setOnClickListener(this);
        layout_back.setOnClickListener(this);

        tv_position.setOnTouchListener(Global.GetTouch());
        tv_customer_name.setOnTouchListener(Global.GetTouch());

        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText("拜访详情");

        msgAudiomMenu = new MsgAudiomMenu(mContext, this, uuid);
        layout_bottom_menu.addView(msgAudiomMenu);
        getPageData();
    }

    /**
     * 评论删除
     */
    private void deleteComment(String id) {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISigninOrFollowUp.class).deleteComment(id, new RCallback<Object>() {
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
     */
    private void requestComment(String content) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("bizzId", id);
        map.put("title", content);
        map.put("commentType", 1); //1文本 2语音
        map.put("bizzType", 1);   //1拜访 2跟进
        //map.put("audioInfo", "");//语音信息
        LogUtil.dee("评论参数:" + MainApp.gson.toJson(map));
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISigninOrFollowUp.class).requestComment(map, new RCallback<Object>() {
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
     * 评论操作 语音
     */
    private void requestComment(Record record) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("bizzId", id);
        map.put("commentType", 2); //1文本 2语音
        map.put("bizzType", 1);   //1拜访 2跟进
        map.put("audioInfo", record);//语音信息
        LogUtil.dee("评论参数:" + MainApp.gson.toJson(map));
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISigninOrFollowUp.class).requestComment(map, new RCallback<Object>() {
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

    private void bindData() {
        bindAdapter();

        if (TextUtils.isEmpty(mSigninDelModel.creator.avatar)) {
            ImageLoader.getInstance().displayImage(mSigninDelModel.creator.avatar, iv_heading);
        }

        tv_name.setText(mSigninDelModel.creator.name);
        tv_address.setText(TextUtils.isEmpty(mSigninDelModel.address) ? "无地址数据" : mSigninDelModel.address);
        tv_customer_name.setText(mSigninDelModel.customerName);
        tv_position.setText(mSigninDelModel.position);
        tv_time.setText(com.loyo.oa.common.utils.DateTool.getDateTimeFriendly(mSigninDelModel.createdAt));

        /** 备注内容 */
        if (null != mSigninDelModel.memo && !TextUtils.isEmpty(mSigninDelModel.memo)) {
            tv_memo.setVisibility(View.VISIBLE);
            tv_memo.setText(mSigninDelModel.memo);
        } else {
            tv_memo.setVisibility(View.GONE);
        }

        /** 设置@ */
        if (null != mSigninDelModel.atNameAndDepts && !TextUtils.isEmpty(mSigninDelModel.atNameAndDepts)) {
            tv_toast.setVisibility(View.VISIBLE);
            tv_toast.setText("@" + mSigninDelModel.atNameAndDepts);
        }

        /** 设置联系人 */
        if (null != mSigninDelModel.contactName && !TextUtils.isEmpty(mSigninDelModel.contactName)) {
            tv_contact_name.setText(mSigninDelModel.contactName);
        } else {
            layout_contact.setVisibility(View.GONE);
        }

        /** 偏差距离 */
        if (mSigninDelModel.distance.equals("未知")) {
            tv_pc.setTextColor(getResources().getColor(R.color.red));
        } else {
            tv_pc.setTextColor(getResources().getColor(R.color.text99));
        }
        tv_pc.setText(mSigninDelModel.distance + "");

        /** 设置拜访地址信息 */
        if (null != mSigninDelModel.address && !TextUtils.isEmpty(mSigninDelModel.address)) {
            layout_position.setVisibility(View.VISIBLE);
        }

        /** 绑定评论数据 */
        if (null != mSigninDelModel.comments && mSigninDelModel.comments.size() > 0) {
            layout_comment.setVisibility(View.VISIBLE);
            commentAdapter = new ListOrDetailsCommentAdapter(mContext, mSigninDelModel.comments, this);
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


        /*图片预览*/
        gv_image.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", mSigninDelModel.imageAttachments);
                bundle.putInt("position", position);
                bundle.putBoolean("isEdit", false);
                MainApp.getMainApp().startActivityForResult((Activity) mContext, PreviewImageListActivity.class,
                        MainApp.ENTER_TYPE_BUTTOM, FinalVariables.REQUEST_DEAL_ATTACHMENT, bundle);
            }
        });


        /** 查看位置地图 */
        tv_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mSigninDelModel.gpsInfo && !TextUtils.isEmpty(mSigninDelModel.gpsInfo)) {
                    Intent mIntent = new Intent(mContext, MapSingleView.class);
                    String[] gps = mSigninDelModel.gpsInfo.split(",");
                    mIntent.putExtra("la", Double.valueOf(gps[1]));
                    mIntent.putExtra("lo", Double.valueOf(gps[0]));
                    mIntent.putExtra("address", mSigninDelModel.position);
                    mIntent.putExtra("title", "签到地址");
                    mContext.startActivity(mIntent);
                } else {
                    Toast.makeText(mContext, "GPS坐标不全!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /** 查看客户详情 */
        tv_customer_name.setOnClickListener(new View.OnClickListener() {
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
                intent.putExtra("Id", mSigninDelModel.customerId);
                intent.setClass(mContext, CustomerDetailInfoActivity_.class);
                mContext.startActivity(intent);
            }
        });

    }

    /**
     * 获取详情数据
     */
    private void requestDetails() {
//        showLoading("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("split", true);
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISigninOrFollowUp.class).getSigninDetails(id, map, new RCallback<BaseBeanT<SigninNewListModel>>() {
            @Override
            public void success(BaseBeanT<SigninNewListModel> signinNewListModel, Response response) {
                HttpErrorCheck.checkResponse("拜访详情", response, ll_loading);
                mSigninDelModel = signinNewListModel.data;
                if (mSigninDelModel == null) {
                    Toast("没有获取到数据");
                    onBackPressed();
                } else {
                    bindData();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error, ll_loading);
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
                CommonImageView img = new CommonImageView(SigninDetailsActivity.this, ele.data);
                layout.addView(img);
            } else {
                CommonTextVew tex = new CommonTextVew(SigninDetailsActivity.this, ele.data);
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

        if (audioPlayer.isPlaying()) {
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
        } else {
            audioPlayer.audioStart(textView);
            audioPlayer.threadPool(audioModel, textView);
            lastUrl = audioModel.url;
            lastView = textView;
        }
        playVoiceSize++;
    }
}
