package com.loyo.oa.v2.activityui.customer;

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
import com.loyo.oa.v2.activityui.commonview.bean.OssToken;
import com.loyo.oa.v2.activityui.customer.bean.Contact;
import com.loyo.oa.v2.activityui.other.adapter.ImageGridViewAdapter;
import com.loyo.oa.v2.activityui.sale.bean.CommonTag;
import com.loyo.oa.v2.activityui.signin.SigninSelectCustomer;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.AttachmentBatch;
import com.loyo.oa.v2.beans.AttachmentForNew;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.SaleActivity;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.CusGridView;
import com.loyo.oa.v2.customview.DateTimePickDialog;
import com.loyo.oa.v2.customview.multi_image_selector.MultiImageSelectorActivity;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.AliOSSManager;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
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
 * 【新建跟进】客户管理
 *
 * Create by yyy on 16/08/24
 */
public class CustomerDynamicAddActivity extends BaseActivity implements View.OnClickListener {

    private ViewGroup img_title_left, img_title_right, layout_remain_time, layout_sale_action;
    private CusGridView gridView_photo;
    private LinearLayout layout_image;
    private EditText edt;
    private TextView tv_sale_action, tv_remain_time, tv_customer, tv_contact_name;
    private Customer mCustomer;
    private String tagItemIds, contactId, contactName = "无";
    private LinearLayout ll_customer, ll_contact, ll_contactItem,layout_photo;
    private ImageGridViewAdapter imageGridViewAdapter;
    private OSS oss;

    private String content;
    private String uuid = StringUtil.getUUID();
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
        setContentView(R.layout.activity_dynamic_activities_add);

        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            mCustomer = (Customer) bundle.getSerializable(Customer.class.getName());
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
        layout_image = (LinearLayout) findViewById(R.id.layout_image);
        layout_photo = (LinearLayout) findViewById(R.id.layout_photo);
        layout_image.setOnClickListener(this);
        layout_image.setOnTouchListener(Global.GetTouch());
        tv_remain_time = (TextView) findViewById(R.id.tv_remain_time);
        tv_sale_action = (TextView) findViewById(R.id.tv_sale_action);
        gridView_photo = (CusGridView) findViewById(R.id.gridView_photo);

        ViewUtil.OnTouchListener_view_transparency touch = ViewUtil.OnTouchListener_view_transparency.Instance();

        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(this);
        img_title_left.setOnTouchListener(touch);

        layout_sale_action = (ViewGroup) findViewById(R.id.layout_sale_action);
        layout_sale_action.setOnClickListener(this);
        layout_sale_action.setOnTouchListener(touch);

        layout_remain_time = (ViewGroup) findViewById(R.id.layout_remain_time);
        layout_remain_time.setOnClickListener(this);
        layout_remain_time.setOnTouchListener(touch);


        img_title_right = (ViewGroup) findViewById(R.id.img_title_right);
        img_title_right.setOnClickListener(this);
        img_title_right.setOnTouchListener(touch);

        ll_customer = (LinearLayout) findViewById(R.id.ll_customer);
        ll_customer.setOnClickListener(this);
        ll_customer.setOnTouchListener(touch);
        tv_customer = (TextView) findViewById(R.id.tv_customer);

        ll_contactItem = (LinearLayout) findViewById(R.id.ll_contactItem);
        ll_contact = (LinearLayout) findViewById(R.id.ll_contact);
        ll_contact.setOnClickListener(this);
        ll_contact.setOnTouchListener(touch);
        tv_contact_name = (TextView) findViewById(R.id.tv_contact_name);
        ll_customer.setVisibility(null == mCustomer ? View.VISIBLE : View.GONE);
        ll_contactItem.setVisibility(null == mCustomer ? View.GONE : View.VISIBLE);
        if(null!=mCustomer){
            getDefaultContact( mCustomer.contacts);
        }
        init_gridView_photo();
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
     * 提交新建跟进
     * */
    public void commitDynamic(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("customerId", mCustomer.getId());
        map.put("content", content);
        map.put("typeId", tagItemIds);
        if(pickPhots.size() != 0){
            map.put("uuid", uuid);
        }
        if (!tv_remain_time.getText().toString().isEmpty() || !tv_remain_time.getText().toString().equals("不提醒")) {
            map.put("remindAt", DateTool.getDateToTimestamp(tv_remain_time.getText().toString().trim(), app.df2) / 1000);
        }
        if (!TextUtils.isEmpty(contactId)) {
            map.put("contactId", contactId);
            map.put("contactName", contactName);
        }
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).addSaleactivity(map, new RCallback<SaleActivity>() {
            @Override
            public void success(final SaleActivity saleActivity, final Response response) {
                HttpErrorCheck.checkResponse("新建跟进动态", response);
                app.finishActivity(CustomerDynamicAddActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, new Intent());
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
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
     * 获取客户的默认联系人
     * @param data
     */
    private void getDefaultContact(ArrayList<Contact> data){
    for(Contact ele:data){
        if(!ele.isDefault()){
            continue;
        }else {
            contactId=ele.getId();
            contactName=ele.getName();
            tv_contact_name.setText(contactName);
        }
    }
}
    /**
     * 选择下次跟进时间
     */
    private void selectRemainTime() {
        DateTimePickDialog dateTimePickDialog = new DateTimePickDialog(this, null);
        dateTimePickDialog.dateTimePicKDialog(new DateTimePickDialog.OnDateTimeChangedListener() {
            @Override
            public void onDateTimeChanged(final int year, final int month, final int day, final int hour, final int min) {
                String str = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", day) + String.format(" %02d", hour) + String.format(":%02d", min);
                tv_remain_time.setText(str);
            }

            @Override
            public void onCancel() {
                tv_remain_time.setText("不提醒");
            }
        }, false, "不提醒");
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {

            /*返回*/
            case R.id.img_title_left:
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, 0, null);
                break;

            /*下次跟进提醒*/
            case R.id.layout_remain_time:
                selectRemainTime();
                break;

            /*跟进方式*/
            case R.id.layout_sale_action:
                Bundle loseBundle = new Bundle();
                loseBundle.putString("title", "跟进方式");
                loseBundle.putInt("mode", CommonTagSelectActivity.SELECT_MODE_SINGLE);
                loseBundle.putInt("type", CommonTagSelectActivity.SELECT_TYPE_SALE_ACTIVE_ACTION);
                app.startActivityForResult(this, CommonTagSelectActivity_.class, 0, CommonTagSelectActivity.REQUEST_TAGS, loseBundle);
                break;

            /*提交*/
            case R.id.img_title_right:

                content = edt.getText().toString().trim();
                if (StringUtil.isEmpty(content)) {
                    Toast(getString(R.string.app_content) + getString(R.string.app_no_null));
                    return;
                } else if (TextUtils.isEmpty(tagItemIds)) {
                    Toast("请选择跟进方式");
                    return;
                } else if (null == mCustomer || TextUtils.isEmpty(mCustomer.getId())) {
                    Toast("请选择跟进客户");
                    return;
                }

                if(pickPhots.size() != 0 /*当前提交有附件*/){
                    setAttachmentData();
                }else{
                    commitDynamic();
                }

                break;

            /*选择客户*/
            case R.id.ll_customer:
                Bundle b = new Bundle();
                app.startActivityForResult(CustomerDynamicAddActivity.this, SigninSelectCustomer.class,
                        MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_CUSTOMER, b);
                break;

            /*选择联系人*/
            case R.id.ll_contact:
                Bundle bContact = new Bundle();
                bContact.putSerializable(ExtraAndResult.EXTRA_DATA, mCustomer.contacts);
                bContact.putString(ExtraAndResult.EXTRA_NAME, tv_contact_name.getText().toString());
                app.startActivityForResult(CustomerDynamicAddActivity.this, FollowContactSelectActivity.class,
                        MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_STAGE, bContact);
                break;

            /*选择图片*/
            case R.id.layout_image:
                app.startSelectImage(CustomerDynamicAddActivity.this,pickPhots);
                break;

        }
    }

    boolean isSave = true;
    SaleActivity mSaleActivity;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mCustomer) {
            DBManager.Instance().deleteSaleActivity(mCustomer.getId());
            if (isSave) {
                mSaleActivity = new SaleActivity();
                mSaleActivity.setContent(edt.getText().toString());
                mSaleActivity.setType(null);
                mSaleActivity.setCreator(null);
                mSaleActivity.setAttachments(null);
                DBManager.Instance().putSaleActivity(MainApp.gson.toJson(mSaleActivity), mCustomer.getId());
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

            /*选择客户返回 回调*/
            case ExtraAndResult.REQUEST_CODE_CUSTOMER:
                Customer customer = (Customer) data.getSerializableExtra("data");
                String customerName = "无";
                if (null != customer) {
                    mCustomer = customer;
                    customerName=customer.name;
                    getDefaultContact( mCustomer.contacts);
                }
                tv_customer.setText(customerName);
                ll_contactItem.setVisibility(null == mCustomer ? View.GONE : View.VISIBLE);
                break;

           /* 选择客户联系人 回调*/
            case ExtraAndResult.REQUEST_CODE_STAGE:
                Contact contact = (Contact) data.getSerializableExtra(ExtraAndResult.EXTRA_DATA);
                if (null != contact) {
                    contactId = contact.getId();
                    contactName = contact.getName();
                }
                tv_contact_name.setText(contactName);
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
