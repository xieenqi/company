package com.loyo.oa.v2.activityui.worksheet.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.loyo.oa.v2.activityui.commonview.bean.OssToken;
import com.loyo.oa.v2.activityui.customer.CommonTagSelectActivity;
import com.loyo.oa.v2.activityui.other.adapter.ImageGridViewAdapter;
import com.loyo.oa.v2.activityui.sale.bean.CommonTag;
import com.loyo.oa.v2.activityui.worksheet.WorksheetAddActivity;
import com.loyo.oa.v2.activityui.worksheet.bean.Worksheet;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetOrderListWrapper;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetWrapper;
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
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
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
import retrofit.http.PUT;

public class WorksheetAddStep2Fragment extends BaseFragment implements View.OnClickListener  {
    private View mView;
    private ViewGroup img_title_left, img_title_right;
    TextView tv_title_1;
    EditText edt_title, edt;

    private LinearLayout layout_image;
    private ImageGridViewAdapter imageGridViewAdapter;
    private CusGridView gridView_photo;

    private OSS oss;
    private String   uuid = StringUtil.getUUID();
    private String ak;
    private String sk;
    private String token;
    private String expiration;
    private int attachmentCount = 0; //当前附件总数
    private int uploadSize;
    private int uploadNum = 0;      //上传附件数量
    private int bizType = 29;

    private AttachmentBatch attachmentBatch;
    private ArrayList<AttachmentBatch> attachment;

    private ArrayList<SelectPicPopupWindow.ImageInfo> pickPhots = new ArrayList<>();

    WorksheetAddActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (WorksheetAddActivity)getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.activity_worksheet_add_step2, null);
            initUI(mView);
        }
        return mView;
    }

    void initUI(View mView) {

        gridView_photo = (CusGridView) mView.findViewById(R.id.gridView_photo);

        edt_title = (EditText) mView.findViewById(R.id.edt_title);
        edt = (EditText) mView.findViewById(R.id.edt);

        img_title_left = (ViewGroup) mView.findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(this);

        img_title_right = (ViewGroup) mView.findViewById(R.id.img_title_right);
        img_title_right.setOnClickListener(this);

        tv_title_1 = (TextView) mView.findViewById(R.id.tv_title_1);
        tv_title_1.setText("填写工单内容");

        init_gridView_photo();

        if (mActivity != null) {
            edt_title.setText(mActivity.selectedOrder.title +"-"+ mActivity.selectedType.name);
        }
    }

    /**
     * 图片列表绑定
     * */
    void init_gridView_photo() {
        imageGridViewAdapter = new ImageGridViewAdapter(getActivity(), true, true, 0, pickPhots);
        ImageGridViewAdapter.setAdapter(gridView_photo, imageGridViewAdapter);
    }

    public void loadPhotoData(ArrayList<SelectPicPopupWindow.ImageInfo> data) {
        pickPhots = data;
        init_gridView_photo();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                ((WorksheetAddActivity)getActivity()).previousStep();
                break;
            case R.id.img_title_right:

                if (pickPhots.size() <= 0) {
                    //
                    commitWorksheet();
                }
                else {
                    setAttachmentData();
                }

                break;
        }
    }

    private void commitWorksheet() {

        /**
         * 新建工单
         *
         * @param body     {
         *                 "title"      :"工单001",
         *                 "orderId"    :"57c3ef26ebe07f2d0b000001",
         *                 "orderName"  :"新建的工单",
         *                 "templateId" :"57c3ef26ebe07f2d0b000001",
         *                 "content"    : "工单事件很多哟!",
         *                 "uuid"       :"57c3ef26ebe07f2d0b000001"
         *                 }
         * @param callback
         */

        String title = edt_title.getText().toString().trim();
        if (title == null && title.length() <= 0) {
            Toast("标题不能为空");
            return;
        }

        String content = edt.getText().toString().trim();
        String orderId = mActivity.selectedOrder.id;
        String orderName = mActivity.selectedOrder.title;
        String templateId = mActivity.selectedType.id;

        final HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("title", title);
        map.put("orderId", orderId);
        map.put("orderName", orderName);
        map.put("templateId", templateId);
        if (content != null) {
            map.put("content", content);
        }
        if (attachment!= null && attachment.size() > 0) {
            map.put("uuid", uuid);
        }

        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                create(IWorksheet.class).addWorksheet(map, new Callback<WorksheetWrapper>() {
            @Override
            public void success(WorksheetWrapper wrapper, Response response) {
                if (wrapper.errcode == 0) {
                    Intent intent = new Intent();
                    intent.putExtra(ExtraAndResult.EXTRA_BOOLEAN, true);

                    Worksheet ws = wrapper.data;
                    if (ws == null) {
                        ws = new Worksheet();
                    }
                    AppBus.getInstance().post(ws);

                    app.finishActivity(getActivity(), MainApp.ENTER_TYPE_LEFT, 0, intent);
                }
                else {
                    Toast("" + wrapper.errmsg);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
            }
        });
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
                File newFile = Global.scal(getActivity(), uri);
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

                        AliOSSManager.getInstance().init(getActivity(), ak, sk, token, expiration);
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

        IAttachment service = RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class);

        service.setAttachementData(attachment, new Callback<ArrayList<AttachmentForNew>>() {
                    @Override
                    public void success(ArrayList<AttachmentForNew> attachmentForNew, Response response) {
                        HttpErrorCheck.checkResponse("上传附件信息", response);

                        // TODO:
                        commitWorksheet();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                    }
                });
        showLoading("");
    }

}
