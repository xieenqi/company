package com.loyo.oa.v2.activityui.worksheet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.loyo.oa.v2.activityui.commonview.MapModifyView;
import com.loyo.oa.v2.activityui.commonview.MapSingleView;
import com.loyo.oa.v2.activityui.commonview.bean.OssToken;
import com.loyo.oa.v2.activityui.commonview.bean.PositionResultItem;
import com.loyo.oa.v2.activityui.customer.bean.HttpLoc;
import com.loyo.oa.v2.activityui.other.adapter.ImageGridViewAdapter;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetDetail;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetInfo;
import com.loyo.oa.v2.activityui.worksheet.event.WorksheetEventChangeEvent;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.AttachmentBatch;
import com.loyo.oa.v2.beans.AttachmentForNew;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.CusGridView;
import com.loyo.oa.v2.customview.multi_image_selector.MultiImageSelectorActivity;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.point.IWorksheet;
import com.loyo.oa.v2.tool.AliOSSManager;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【提交完成/打回重做】工单
 * Created by yyy on 16/8/30.
 */
public class WorksheetSubmitActivity extends BaseActivity implements View.OnClickListener {

    /**
     * UI
     */
    private TextView tv_title_1;
    private TextView tv_address;        //定位内容
    private CusGridView gridView_photo;    //图片gridView
    private EditText view_edit;         //输入content
    private RelativeLayout img_title_right;
    private LinearLayout img_title_left,  //返回
            layout_image,    //图片
            layout_location, //定位
            layout_address,  //定位layout
            layout_delete_location,  //定位删除
            layout_address_info;     //定位信息

    /**
     * Data
     */
    private int uploadSize;
    private int uploadNum = 0;           //上传附件数量
    private int fromPage;                //判断来自的页面(打回重做0x10 提交完成0x02)
    private int type;                    //type 1为提交完成，2为打回重做
    private int bizType = 29;            //附件type
    private String uuid = StringUtil.getUUID();
    private String ak, sk, token, expiration;
    private String id;                  //事件Id
    private double laPosition;          //当前位置的经纬度
    private double loPosition;
    private HttpLoc httpLoc = new HttpLoc();
    private List<String> mSelectPath;
    private AttachmentBatch attachmentBatch;
    private PositionResultItem positionResultItem;
    private ArrayList<AttachmentBatch> attachment;
    private ArrayList<SelectPicPopupWindow.ImageInfo> pickPhotsResult;
    private ArrayList<SelectPicPopupWindow.ImageInfo> pickPhots = new ArrayList<>();


    /**
     * Other
     */
    private ImageGridViewAdapter imageGridViewAdapter;
    private OSS oss;
    private Bundle mBundle;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worksheet_submit);
        initUI();
    }

    void initUI() {
        mIntent = getIntent();
        fromPage = mIntent.getIntExtra(ExtraAndResult.EXTRA_DATA, 0x01);
        id = mIntent.getStringExtra(ExtraAndResult.CC_USER_ID);
        if (TextUtils.isEmpty(id)) {
            Toast("参数不全");
            onBackPressed();
        }
        tv_title_1 = (TextView) findViewById(R.id.tv_title_1);
        tv_address = (TextView) findViewById(R.id.tv_address);

        img_title_right = (RelativeLayout) findViewById(R.id.img_title_right);
        img_title_left = (LinearLayout) findViewById(R.id.img_title_left);
        layout_image = (LinearLayout) findViewById(R.id.layout_image);
        layout_location = (LinearLayout) findViewById(R.id.layout_location);
        layout_address = (LinearLayout) findViewById(R.id.layout_address);
        layout_address_info = (LinearLayout) findViewById(R.id.layout_address_info);
        layout_delete_location = (LinearLayout) findViewById(R.id.layout_delete_location);
        gridView_photo = (CusGridView) findViewById(R.id.gridView_photo);
        view_edit = (EditText) findViewById(R.id.view_edit);

        if (fromPage == 0x01) {
            super.setTitle("打回重做");
            view_edit.setHint("请输入重做原因，必填");
            type = 2;
        } else {
            super.setTitle("提交完成");
            view_edit.setHint("请输入完成内容，非必填");
            type = 1;
        }

        img_title_right.setOnClickListener(this);
        img_title_left.setOnClickListener(this);
        layout_image.setOnClickListener(this);
        layout_location.setOnClickListener(this);
        layout_delete_location.setOnClickListener(this);
        layout_address_info.setOnClickListener(this);

        img_title_left.setOnTouchListener(Global.GetTouch());
        layout_image.setOnTouchListener(Global.GetTouch());
        layout_location.setOnTouchListener(Global.GetTouch());
        layout_delete_location.setOnTouchListener(Global.GetTouch());
    }

    /**
     * 图片列表绑定
     */
    void init_gridView_photo() {
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

    /**
     * 提交事件处理数据
     */
    void commitDynamic() {

        HashMap<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("content", view_edit.getText().toString());
        map.put("uuid", uuid);
        map.put("address", httpLoc);
        LogUtil.dee("提交事件信息：" + MainApp.gson.toJson(map));
        RestAdapterFactory.getInstance().build(Config_project.API_URL_STATISTICS()).create(IWorksheet.class).setEventSubmit(id, map, new RCallback<Object>() {
            @Override
            public void success(final Object o, final Response response) {
                HttpErrorCheck.checkResponse("提交事情处理信息", response);
                if (type == 1) {
                    AppBus.getInstance().post(new WorksheetDetail());

                    WorksheetEventChangeEvent event = new WorksheetEventChangeEvent();
                    event.bundle = new Bundle();
                    event.bundle.putString(ExtraAndResult.EXTRA_ID, id);
                    AppBus.getInstance().post(event);
                } else {
                    AppBus.getInstance().post(new WorksheetInfo());
                }
                app.finishActivity(WorksheetSubmitActivity.this, MainApp.ENTER_TYPE_LEFT, 0, new Intent());
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            /*提交*/
            case R.id.img_title_right:
                if (fromPage == 0x01) {
                    if (TextUtils.isEmpty(view_edit.getText().toString())) {
                        Toast("请输入重做原因！");
                        return;
                    }
                }

                if (pickPhots.size() != 0 /*当前提交有附件*/) {
                    setAttachmentData();
                } else {
                    commitDynamic();
                }
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
                mBundle = new Bundle();
                mBundle.putInt("page", MapModifyView.CUSTOMER_PAGE);
                app.startActivityForResult(this, MapModifyView.class, MainApp.ENTER_TYPE_RIGHT, MapModifyView.SERACH_MAP, mBundle);
                break;

            /*删除地址*/
            case R.id.layout_delete_location:
                httpLoc = new HttpLoc();
                layout_address.setVisibility(View.GONE);
                break;

            /*位置信息*/
            case R.id.layout_address_info:
                mIntent = new Intent(WorksheetSubmitActivity.this, MapSingleView.class);
                mIntent.putExtra("la", laPosition);
                mIntent.putExtra("lo", loPosition);
                mIntent.putExtra("address", httpLoc.addr);
                startActivity(mIntent);
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

        switch (requestCode) {

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

           /*附件删除 回调*/
            case FinalVariables.REQUEST_DEAL_ATTACHMENT:
                pickPhots.remove(data.getExtras().getInt("position"));
                init_gridView_photo();
                break;

            /*地址定位 回调*/
            case MapModifyView.SERACH_MAP:
                positionResultItem = (PositionResultItem) data.getSerializableExtra("data");
                if (null != positionResultItem) {
                    httpLoc.loc.add(positionResultItem.loPosition);
                    httpLoc.loc.add(positionResultItem.laPosition);
                    httpLoc.addr = positionResultItem.address;
                    laPosition = positionResultItem.laPosition;
                    loPosition = positionResultItem.loPosition;
                    layout_address.setVisibility(View.VISIBLE);
                    tv_address.setText(positionResultItem.address);
                }
                break;

            default:
                break;

        }
    }
}
