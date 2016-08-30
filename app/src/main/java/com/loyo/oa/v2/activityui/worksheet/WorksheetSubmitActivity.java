package com.loyo.oa.v2.activityui.worksheet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.loyo.oa.v2.activityui.other.adapter.ImageGridViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.AttachmentBatch;
import com.loyo.oa.v2.beans.AttachmentForNew;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.CusGridView;
import com.loyo.oa.v2.customview.multi_image_selector.MultiImageSelectorActivity;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【提交完成】工单
 * Created by yyy on 16/8/30.
 */
public class WorksheetSubmitActivity extends BaseActivity implements View.OnClickListener{


    /** UI */
    private TextView    tv_title_1;
    private CusGridView gridView_photo;    //图片gridView
    private EditText    view_edit;         //输入content
    private RelativeLayout img_title_right;
    private LinearLayout   img_title_left,  //返回
                           layout_image,    //图片
                           layout_location; //定位

    /** Data */
    private int uploadSize,
            bizType = 17,
            uploadNum = 0,       //上传附件数量
            attachmentCount = 0; //当前附件总数

    private List<String> mSelectPath;
    private AttachmentBatch attachmentBatch;
    private ArrayList<AttachmentBatch> attachment;
    private ArrayList<SelectPicPopupWindow.ImageInfo> pickPhotsResult;
    private ArrayList<SelectPicPopupWindow.ImageInfo> pickPhots = new ArrayList<>();


    /** Other */
    private ImageGridViewAdapter imageGridViewAdapter;
    private OSS oss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worksheet_submit);
        initUI();
    }

    void initUI(){
        tv_title_1 = (TextView) findViewById(R.id.tv_title_1);
        super.setTitle("提交完成");
        img_title_right  = (RelativeLayout) findViewById(R.id.img_title_right);
        img_title_left   = (LinearLayout) findViewById(R.id.img_title_left);
        layout_image     = (LinearLayout) findViewById(R.id.layout_image);
        layout_location  = (LinearLayout) findViewById(R.id.layout_location);
        gridView_photo   = (CusGridView) findViewById(R.id.gridView_photo);
        view_edit = (EditText) findViewById(R.id.view_edit);

        img_title_right.setOnClickListener(this);
        img_title_left.setOnClickListener(this);
        layout_image.setOnClickListener(this);
        layout_location.setOnClickListener(this);

        layout_image.setOnTouchListener(Global.GetTouch());
        layout_location.setOnTouchListener(Global.GetTouch());
    }

    /**
     * 图片列表绑定
     */
    void init_gridView_photo() {
/*        if(pickPhots.size() != 0){
            layout_photo.setVisibility(View.VISIBLE);
        }*/
        imageGridViewAdapter = new ImageGridViewAdapter(this, true, true, 0, pickPhots);
        ImageGridViewAdapter.setAdapter(gridView_photo, imageGridViewAdapter);
    }


    /**
     * 传附件到Oss
     */
    void uploadOssFile(OSS oss, String bucketName, String oKey, String filePath) {

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
     * 上传附件信息
     */
    void postAttaData() {
        showLoading("");
        RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class)
                .setAttachementData(attachment, new Callback<ArrayList<AttachmentForNew>>() {
                    @Override
                    public void success(ArrayList<AttachmentForNew> attachmentForNew, Response response) {
                        HttpErrorCheck.checkResponse("上传附件信息", response);
                        commitDynamic();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                    }
                });
    }

    /**
     * 提交数据
     */
    void commitDynamic(){

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            /*提交*/
            case R.id.img_title_right:
                Toast("提交");
                break;

            /*返回*/
            case R.id.img_title_left:
                onBackPressed();
                break;

            /*图片选择*/
            case R.id.layout_image:
                app.startSelectImage(WorksheetSubmitActivity.this, pickPhots);
                break;

            /*定位选择*/
            case R.id.layout_location:
                Toast("定位选择");
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null == data || resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode){

            /*相册选择 回调*/
            case MainApp.PICTURE:
                if (null != data) {
                    pickPhotsResult = new ArrayList<>();
                    mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    for (String path : mSelectPath) {
                        pickPhotsResult.add(new SelectPicPopupWindow.ImageInfo("file://" + path));
                    }
                    pickPhots.addAll(pickPhotsResult);
                    init_gridView_photo();
                    Utils.autoEjetcEdit(view_edit, 300);
                }
                break;

           /*附件删除回调*/
            case FinalVariables.REQUEST_DEAL_ATTACHMENT:
                pickPhots.remove(data.getExtras().getInt("position"));
                init_gridView_photo();
                break;

            default:
                break;

        }
    }
}
