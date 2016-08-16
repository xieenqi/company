package com.loyo.oa.v2.activityui.order;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.commonview.bean.OssToken;
import com.loyo.oa.v2.activityui.other.adapter.AttachmentSwipeAdapter;
import com.loyo.oa.v2.activityui.other.bean.User;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.AttachmentBatch;
import com.loyo.oa.v2.beans.AttachmentForNew;
import com.loyo.oa.v2.common.DialogHelp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.multi_image_selector.MultiImageSelectorActivity;
import com.loyo.oa.v2.customview.swipelistview.SwipeListView;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.tool.AliOSSManager;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.ListUtil;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【订单附件】
 * Created by yyy on 16/8/2.
 */
public class OrderAttachmentActivity extends BaseActivity implements View.OnClickListener {

    private ArrayList<User> mUserList;
    private String uuid = StringUtil.getUUID();
    private String ak;
    private String sk;
    private String token;
    private String expiration;
    private int bizType;
    private int attachmentCount = 0; //当前附件总数
    private int uploadSize;
    private int uploadNum = 0; //上传附件数量
    private boolean isOver;    //当前业务已经结束
    private boolean isPic = false;
    private boolean isAdd;     //操作权限

    private LinearLayout img_title_left;
    private TextView tv_title;
    private TextView tv_upload;
    private SwipeListView mListViewAttachment;

    private OSS oss;
    private Intent mIntent;
    private ArrayList<AttachmentBatch> attachment;
    private AttachmentBatch attachmentBatch;
    private ArrayList<Attachment> mListAttachment;
    private AttachmentSwipeAdapter adapter;
    private List<String> mSelectPath;
    private ArrayList<SelectPicPopupWindow.ImageInfo> pickPhots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_attachment);
        initUI();
    }

    public void initUI() {
        mIntent = getIntent();
        if (null != mIntent) {
            mUserList = (ArrayList<User>) mIntent.getSerializableExtra("users");
            bizType = mIntent.getIntExtra("bizType", 0);
            isOver = mIntent.getBooleanExtra("isOver", false);
            isAdd  = mIntent.getBooleanExtra(ExtraAndResult.EXTRA_ADD,true);
            if (null != mIntent.getStringExtra("uuid") && mIntent.getStringExtra("uuid").length() > 5) {
                uuid = mIntent.getStringExtra("uuid");
                isPic = true;
            }
        }

        img_title_left = (LinearLayout) findViewById(R.id.img_title_left);
        tv_title = (TextView) findViewById(R.id.tv_title_1);
        tv_upload = (TextView) findViewById(R.id.tv_upload);
        mListViewAttachment = (SwipeListView) findViewById(R.id.listView_attachment);
        tv_title.setText("附件");

        img_title_left.setOnTouchListener(Global.GetTouch());
        tv_upload.setOnClickListener(this);
        img_title_left.setOnClickListener(this);

        if(!isAdd){
            tv_upload.setVisibility(View.GONE);
        }

        if (isPic) {
            getAttachments();
        }
    }

    /**
     * 传附件到Oss
     */
    public void uploadOssFile(OSS oss, String bucketName, String oKey, String filePath) {

        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(bucketName, oKey, filePath);

        //异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                LogUtil.dee("currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });

        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                uploadSize++;
                LogUtil.dee("UploadSuccess");
                LogUtil.dee("ETag" + result.getETag());
                LogUtil.dee("RequestId" + result.getRequestId());
                if (uploadSize == uploadNum) {
                    postAttaData();
                    cancelLoading();
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {

                // 本地异常如网络异常等
                if (clientExcepion != null) {
                    clientExcepion.printStackTrace();
                }

                // 服务异常
                if (serviceException != null) {
                    LogUtil.dee("ErrorCode" + serviceException.getErrorCode());
                    LogUtil.dee("RequestId" + serviceException.getRequestId());
                    LogUtil.dee("HostId" + serviceException.getHostId());
                    LogUtil.dee("RawMessage" + serviceException.getRawMessage());
                }
            }
        });
    }


    /**
     * 获取上传Token
     */
    public void getServerToken(final String oKey, final String filePath) {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class)
                .getServerToken(new Callback<OssToken>() {
                    @Override
                    public void success(OssToken ossToken, Response response) {

                        HttpErrorCheck.checkResponse("获取OssToken", response);
                        ak = ossToken.Credentials.AccessKeyId;
                        sk = ossToken.Credentials.AccessKeySecret;
                        token = ossToken.Credentials.SecurityToken;
                        expiration = ossToken.Credentials.Expiration;

                        AliOSSManager.getInstance().init(mContext, ak, sk, token, expiration);
                        oss = AliOSSManager.getInstance().getOss();
                        uploadOssFile(oss, Config_project.OSS_UPLOAD_BUCKETNAME(), oKey, filePath);

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                    }
                });
    }

    /**
     * 上传附件信息
     */
    public void postAttaData() {
        showLoading("");
        RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class)
                .setAttachementData(attachment, new Callback<ArrayList<AttachmentForNew>>() {
                    @Override
                    public void success(ArrayList<AttachmentForNew> attachmentForNew, Response response) {
                        HttpErrorCheck.checkResponse("上传附件信息", response);
                        getAttachments();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                    }
                });
    }


    /**
     * 获取附件列表信息
     */
    void getAttachments() {

        showLoading("");
        RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class).getAttachments(uuid, new RCallback<ArrayList<Attachment>>() {
            @Override
            public void success(final ArrayList<Attachment> attachments, final Response response) {
                HttpErrorCheck.checkResponse("获取附件", response);
                mListAttachment = attachments;
                attachmentCount = attachments.size();
                bindAttachment();
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
                finish();
            }
        });
    }


    /**
     * 绑定附件
     */
    void bindAttachment() {
        if (ListUtil.IsEmpty(mListAttachment)) {
            return;
        }

        //Attachment.Sort(mListAttachment);
        if (null == adapter) {
            adapter = new AttachmentSwipeAdapter(mContext, mListAttachment, mUserList, bizType, uuid, isOver);
            adapter.setAttachmentAction(new AttachmentSwipeAdapter.AttachmentAction() {
                @Override
                public void afterDelete(final Attachment attachment) {
                    //附件删除 重新绑定
                    mListAttachment.remove(attachment);
                    attachmentCount = mListAttachment.size();
                    adapter.notifyDataSetChanged();
                }
            });
            mListViewAttachment.setSwipeCloseAllItemsWhenMoveList(true);
            mListViewAttachment.setAdapter(adapter);
        } else {
            adapter.setData(mListAttachment);
            adapter.notifyDataSetChanged();
        }
        if (uploadNum == uploadSize) {
            DialogHelp.cancelLoading();
        }
    }

    /**
     * 组装附件数据
     */
    public void setAttachmentData() {
        try {
            uploadSize = 0;
            uploadNum = pickPhots.size();
            attachment = new ArrayList<>();
            showLoading("");
            for (SelectPicPopupWindow.ImageInfo item : pickPhots) {
                Uri uri = Uri.parse(item.path);
                File newFile = Global.scal(this, uri);
                if (newFile != null && newFile.length() > 0) {
                    if (newFile.exists()) {
                        attachmentBatch = new AttachmentBatch();
                        attachmentBatch.UUId = uuid;
                        attachmentBatch.bizType = bizType;
                        attachmentBatch.mime = Utils.getMimeType(newFile.getPath());
                        attachmentBatch.name = uuid + "/" + newFile.getName();
                        attachmentBatch.size = Integer.parseInt(newFile.length() + "");
                        attachment.add(attachmentBatch);
                        getServerToken(uuid + "/" + newFile.getName(), newFile.getPath());

                    }
                }
            }
        } catch (Exception ex) {
            Global.ProcException(ex);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            //后退
            case R.id.img_title_left:
                onBackPressed();
                break;

            //上传
            case R.id.tv_upload:

                Intent intent = new Intent(this, MultiImageSelectorActivity.class);
                // 是否显示拍摄图片
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
                // 最大可选择图片数量
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
                // 选择模式
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_CROP_CIRCLE, false);
                startActivityForResult(intent, 2);

                break;

        }
    }

    @Override
    public void onBackPressed() {
        mIntent = new Intent();
        mIntent.putExtra("uuid", uuid);
        mIntent.putExtra("size", attachmentCount);
        app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, mIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK && null == data) {
            return;
        }

        switch (requestCode) {

            //相册选择回调
            case SelectPicPopupWindow.PICTURE:
                pickPhots = new ArrayList<>();
                mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                for (String path : mSelectPath) {
                    pickPhots.add(new SelectPicPopupWindow.ImageInfo("file://" + path));
                }
                setAttachmentData();
                break;
        }
    }
}
