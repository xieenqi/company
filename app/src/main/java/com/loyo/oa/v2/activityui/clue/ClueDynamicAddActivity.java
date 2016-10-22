package com.loyo.oa.v2.activityui.clue;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.loyo.oa.v2.activityui.customer.CommonTagSelectActivity;
import com.loyo.oa.v2.activityui.customer.CommonTagSelectActivity_;
import com.loyo.oa.v2.activityui.other.adapter.ImageGridViewAdapter;
import com.loyo.oa.v2.activityui.sale.bean.CommonTag;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.AttachmentBatch;
import com.loyo.oa.v2.beans.AttachmentForNew;
import com.loyo.oa.v2.beans.SaleActivity;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.CusGridView;
import com.loyo.oa.v2.customview.multi_image_selector.MultiImageSelectorActivity;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.point.IClue;
import com.loyo.oa.v2.tool.AliOSSManager;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.ViewUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【新建跟进动态】销售线索
 */
public class ClueDynamicAddActivity extends BaseActivity implements View.OnClickListener {

    private CusGridView gridView_photo;
    private ViewGroup img_title_left, img_title_right, layout_sale_action;
    private EditText edt;
    private TextView tv_sale_action, tv_contact_name;
    private LinearLayout ll_contact, ll_contactItem;
    private LinearLayout layout_image;
    private ImageGridViewAdapter imageGridViewAdapter;
    private OSS oss;

    private String clueId;
    private String tagItemIds, contactId, contactName = "无";
    private String content;
    private String   uuid = StringUtil.getUUID();
    private String ak;
    private String sk;
    private String token;
    private String expiration;
    private int attachmentCount = 0; //当前附件总数
    private int uploadSize;
    private int uploadNum = 0;      //上传附件数量
    private int bizType = 17;

    private List<String> mSelectPath;
    private AttachmentBatch attachmentBatch;
    private ArrayList<AttachmentBatch> attachment;
    private ArrayList<SelectPicPopupWindow.ImageInfo> pickPhotsResult;
    private ArrayList<SelectPicPopupWindow.ImageInfo> pickPhots = new ArrayList<>();


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clue_followup_create);

        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            clueId = bundle.getString(ExtraAndResult.EXTRA_ID);
            contactName = bundle.getString(ExtraAndResult.EXTRA_NAME);
        }

        initUI();
        getTempSaleActivity();
    }

    void getTempSaleActivity() {
        if (mSaleActivity == null) {
            return;
        }

        edt.setText(mSaleActivity.getContent());
    }

    void initUI() {
        super.setTitle("写跟进");

        edt = (EditText) findViewById(R.id.edt);
        tv_sale_action = (TextView) findViewById(R.id.tv_sale_action);

        ViewUtil.OnTouchListener_view_transparency touch = ViewUtil.OnTouchListener_view_transparency.Instance();
        gridView_photo = (CusGridView) findViewById(R.id.gridView_photo);
        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        layout_image = (LinearLayout) findViewById(R.id.layout_image);
        layout_image.setOnClickListener(this);
        layout_image.setOnTouchListener(Global.GetTouch());
        img_title_left.setOnClickListener(this);
        img_title_left.setOnTouchListener(touch);

        layout_sale_action = (ViewGroup) findViewById(R.id.layout_sale_action);
        layout_sale_action.setOnClickListener(this);
        layout_sale_action.setOnTouchListener(touch);


        img_title_right = (ViewGroup) findViewById(R.id.img_title_right);
        img_title_right.setOnClickListener(this);
        img_title_right.setOnTouchListener(touch);


        ll_contactItem = (LinearLayout) findViewById(R.id.ll_contactItem);
        ll_contact = (LinearLayout) findViewById(R.id.ll_contact);
        ll_contact.setOnClickListener(this);
        ll_contact.setOnTouchListener(touch);
        tv_contact_name = (TextView) findViewById(R.id.tv_contact_name);
        ll_contactItem.setVisibility(null == clueId ? View.GONE : View.VISIBLE);
        tv_contact_name.setText(contactName);
        init_gridView_photo();
    }

    /**
     * 传附件到Oss
     */

    public void uploadFileToOSS(String oKey, String filePath) {

        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(Config_project.OSS_UPLOAD_BUCKETNAME(), oKey, filePath);

        //异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                LogUtil.dee("currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });

        OSSAsyncTask task = AliOSSManager.getInstance().getOss()
                .asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
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
                else {
                    Toast(""+uploadSize);
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
                        // getServerToken(uuid + "/" + newFile.getName(), newFile.getPath());
                        uploadFileToOSS(uuid + "/" + newFile.getName(), newFile.getPath());
                    }
                }
            }
        } catch (Exception ex) {
            Global.ProcException(ex);
        }
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
                        commitDynamic();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                    }
                });
    }

    /**
     * 图片列表绑定
     * */
    void init_gridView_photo() {
/*        if(pickPhots.size() != 0){
            layout_photo.setVisibility(View.VISIBLE);
        }*/
        imageGridViewAdapter = new ImageGridViewAdapter(this,true,true,0,pickPhots);
        ImageGridViewAdapter.setAdapter(gridView_photo, imageGridViewAdapter);
    }

    /**
     * 提交数据
     * */
    public void commitDynamic(){

        if (StringUtil.isEmpty(content)) {
            Toast(getString(R.string.app_content) + getString(R.string.app_no_null));
            return;
        } else if (TextUtils.isEmpty(tagItemIds)) {
            Toast("请选择跟进方式");
            return;
        } else if (TextUtils.isEmpty(clueId)) {
            Toast("请选择跟进线索");
            return;
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("sealsleadId", clueId);
        map.put("content", content);
        map.put("typeId", tagItemIds);
        if (contactName != null) {
            map.put("contactName", contactName);
        }
        if(pickPhots.size() != 0){
            map.put("uuid", uuid);
        }

        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(IClue.class).addSaleactivity(map, new RCallback<SaleActivity>() {
            @Override
            public void success(final SaleActivity saleActivity, final Response response) {
                HttpErrorCheck.checkResponse("新建跟进动态", response);
                app.finishActivity(ClueDynamicAddActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, new Intent());
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });

    }


    @Override
    public void onClick(final View v) {
        switch (v.getId()) {

            /*返回*/
            case R.id.img_title_left:
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, 0, null);
                break;

            /*提醒时间*/
            case R.id.layout_remain_time:
                //selectRemainTime();
                break;

            /*跟进方式*/
            case R.id.layout_sale_action:
                Bundle loseBundle = new Bundle();
                loseBundle.putString("title", "跟进方式");
                loseBundle.putInt("mode", CommonTagSelectActivity.SELECT_MODE_SINGLE);
                loseBundle.putInt("type", CommonTagSelectActivity.SELECT_TYPE_SALE_ACTIVE_ACTION);
                app.startActivityForResult(this, CommonTagSelectActivity_.class, 0, CommonTagSelectActivity.REQUEST_TAGS, loseBundle);
                break;

            /*提交数据*/
            case R.id.img_title_right:
                content = edt.getText().toString().trim();
                if (StringUtil.isEmpty(content)) {
                    Toast(getString(R.string.app_content) + getString(R.string.app_no_null));
                    return;
                } else if (TextUtils.isEmpty(tagItemIds)) {
                    Toast("请选择跟进方式");
                    return;
                }

                if(pickPhots.size() != 0 /*当前提交有附件*/){
                    setAttachmentData();
                }else{
                    commitDynamic();
                }
                break;

            /*选择图片*/
            case R.id.layout_image:
                app.startSelectImage(ClueDynamicAddActivity.this,pickPhots);
                break;
        }
    }

    boolean isSave = true;
    SaleActivity mSaleActivity;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != clueId) {
            DBManager.Instance().deleteSaleActivity("clue"+clueId);
            if (isSave) {
                mSaleActivity = new SaleActivity();
                mSaleActivity.setContent(edt.getText().toString());
                mSaleActivity.setType(null);
                mSaleActivity.setCreator(null);
                mSaleActivity.setAttachments(null);
                DBManager.Instance().putSaleActivity(MainApp.gson.toJson(mSaleActivity), "clue"+clueId);
            }
        }
    }

    /**
     * 获取跟进方式
     *
     * @param tags
     * @return
     */
    private String getSaleTypes(final ArrayList<CommonTag> tags) {
        if (null == tags || tags.isEmpty()) {
            return "";
        }
        StringBuilder reasons = new StringBuilder();
        int index = 0;
        for (CommonTag reson : tags) {
            reasons.append(reson.getName());
            if (index < tags.size() - 1) {
                reasons.append(",");
            }
            index++;
        }
        return reasons.toString();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null == data || resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {

            /*跟进方式 回调*/
            case CommonTagSelectActivity.REQUEST_TAGS:
                ArrayList<CommonTag> tags = (ArrayList<CommonTag>) data.getSerializableExtra("data");
                tv_sale_action.setText(getSaleTypes(tags));
                tagItemIds = tags.get(0).getId();
                break;

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
                    Utils.autoEjetcEdit(edt, 300);
                }
                break;

           /*附件删除回调*/
            case FinalVariables.REQUEST_DEAL_ATTACHMENT:
                pickPhots.remove(data.getExtras().getInt("position"));
                init_gridView_photo();
                break;
        }
    }
}

