package com.loyo.oa.v2.activity.signin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.adapter.SignInGridViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Attachment;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.LegWork;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseSearchActivity;
import com.loyo.oa.v2.tool.CommonSubscriber;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LocationUtilGD;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.customview.CountTextWatcher;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【 拜访签到 】 页面
 */
public class SignInActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_customer_name;
    private TextView tv_address;
    private TextView wordcount;
    private EditText edt_memo;
    private ViewGroup img_title_left, img_title_right;
    private GridView gridView_photo;
    private ArrayList<Attachment> lstData_Attachment = new ArrayList<>();
    private String uuid = StringUtil.getUUID();
    private String mAddress;
    private String customerId = "";
    private String customerName;
    private SignInGridViewAdapter signInGridViewAdapter;
    private ImageView img_refresh_address;
    private double mLat, mLng;
    boolean mLocationFlag = false;  //是否定位完成的标记
    private Customer mCustomer;
    private Animation animation;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        super.setTitle("拜访签到");
        Intent intent = getIntent();
        if (null != intent && intent.hasExtra("data")) {
            mCustomer = (Customer) intent.getSerializableExtra("data");
            customerId = mCustomer.getId();
            customerName = mCustomer.name;
        }
        animation = AnimationUtils.loadAnimation(this, R.anim.rotateanimation);
        initUI();
    }

    void initUI() {
        tv_customer_name = (TextView) findViewById(R.id.tv_customer_name);

        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(this);
        img_title_left.setOnTouchListener(Global.GetTouch());

        img_title_right = (ViewGroup) findViewById(R.id.img_title_right);
        img_title_right.setOnClickListener(this);
        img_title_right.setOnTouchListener(Global.GetTouch());

        img_refresh_address = (ImageView) findViewById(R.id.img_refresh_address);
        img_refresh_address.setOnTouchListener(Global.GetTouch());
        img_refresh_address.setOnClickListener(this);

        edt_memo = (EditText) findViewById(R.id.edt_memo);
        wordcount = (TextView) findViewById(R.id.wordcount);
        edt_memo.addTextChangedListener(new CountTextWatcher(wordcount));

        ViewGroup layout_customer_name = (ViewGroup) findViewById(R.id.layout_customer_name);
        if (null == mCustomer) {
            layout_customer_name.setOnTouchListener(Global.GetTouch());
            layout_customer_name.setOnClickListener(this);
        } else {

            findViewById(R.id.divider_customer_name).setVisibility(View.GONE);
            layout_customer_name.setVisibility(View.GONE);
            tv_customer_name.setText(customerName);
        }

        tv_address = (TextView) findViewById(R.id.tv_address);

        gridView_photo = (GridView) findViewById(R.id.gridView_photo);
        init_gridView_photo();

        startLocation();
    }

    void startLocation() {
        img_refresh_address.startAnimation(animation);
        mLocationFlag = false;
        tv_address.setText("获取当前位置中...");

        new LocationUtilGD(this, new LocationUtilGD.AfterLocation() {
            @Override
            public void OnLocationGDSucessed(final String address, final double longitude, final double latitude, final String radius) {
                img_refresh_address.clearAnimation();
                animation.reset();
                mLat = latitude;
                mLng = longitude;
                mAddress = address;
                tv_address.setText(address);
                LocationUtilGD.sotpLocation();
            }

            @Override
            public void OnLocationGDFailed() {
                img_refresh_address.clearAnimation();
                animation.reset();
                boolean gpsOpen = Utils.isGPSOPen(mContext);
                if (!gpsOpen) {
                    Global.ToastLong("建议开启GPS,重新定位");
                }
                Toast("定位失败,请在网络和GPS信号良好时重试");
                LocationUtilGD.sotpLocation();
            }
        });
    }

    /**
     * 图片适配器绑定
     */
    void init_gridView_photo() {
        signInGridViewAdapter = new SignInGridViewAdapter(this, lstData_Attachment, true, true, 0);
        SignInGridViewAdapter.setAdapter(gridView_photo, signInGridViewAdapter);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {

            case R.id.img_title_left:
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, 0, null);
                break;

            case R.id.img_title_right:
                addSignIn();
                break;

            /*选择客户*/
            case R.id.layout_customer_name:

                Bundle b = new Bundle();
      /*          b.putInt("queryType", 1);
                b.putInt("from",SIGNIN_ADD);*/
                app.startActivityForResult(this, SigninSelectCustomer.class, MainApp.ENTER_TYPE_RIGHT, BaseSearchActivity.REQUEST_SEARCH, b);

                break;

            /*地址更新*/
            case R.id.img_refresh_address:
                startLocation();
                break;
            default:

                break;
        }
    }

    /**
     * 新增签到
     */
    private void addSignIn() {
//        if (TextUtils.isEmpty(customerId)) {
//            Toast("请选择客户");
//            return;
//        }

        if (TextUtils.isEmpty(mAddress)) {
            Global.ToastLong("无效地址!请刷新地址后重试");
            return;
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("gpsInfo", mLng + "," + mLat);
        map.put("address", mAddress.trim());
        map.put("attachmentUUId", uuid);
        map.put("customerId", customerId);

        if (!StringUtil.isEmpty(edt_memo.getText().toString())) {
            map.put("memo", edt_memo.getText().toString());
        }
        LogUtil.d(" 新增拜访传递数据：" + MainApp.gson.toJson(map));
        showLoading("");
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).addSignIn(map, new RCallback<LegWork>() {
            @Override
            public void success(final LegWork legWork, final Response response) {
                HttpErrorCheck.checkResponse(" 新增拜访传result：", response);
                if (legWork != null) {
                    Toast(getString(R.string.sign) + getString(R.string.app_succeed));
                    if (!TextUtils.isEmpty(legWork.getId())) {
                        Intent intent = getIntent();
                        intent.putExtra("data", legWork);
                        app.finishActivity(SignInActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
                    } else {
                        Toast(getString(R.string.sign) + "异常!");
                    }
                } else {
                    Toast("提交失败" + response.getStatus());
//                    legWork.creator = MainApp.user.toShortUser();
                }
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
    }

    /**
     * 获取附件
     */
    private void getAttachments() {
        Utils.getAttachments(uuid, new RCallback<ArrayList<Attachment>>() {
            @Override
            public void success(final ArrayList<Attachment> attachments, final Response response) {
                HttpErrorCheck.checkResponse(response);
                lstData_Attachment = attachments;
                init_gridView_photo();
            }

            @Override
            public void failure(final RetrofitError error) {
                HttpErrorCheck.checkError(error);
                Toast("获取附件失败");
                LogUtil.dll("failure code:" + error.getResponse().getStatus());
            }
        });
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (data == null || resultCode != RESULT_OK) {
            return;
        }

        /**
         * 添加附件流程：拍照回调－流生成file－上传服务器－从服务器下载－展示在View中
         * 费解
         * */
        switch (requestCode) {
            case SelectPicPopupWindow.GET_IMG:
                try {
                    ArrayList<SelectPicPopupWindow.ImageInfo> pickPhots = (ArrayList<SelectPicPopupWindow.ImageInfo>) data.getSerializableExtra("data");
                    for (SelectPicPopupWindow.ImageInfo item : pickPhots) {
                        Uri uri = Uri.parse(item.path);
                        File newFile = null;
                        newFile = Global.scal(this, uri);

                        if (newFile != null && newFile.length() > 0) {
                            if (newFile.exists()) {
                                /**上传附件*/
                                Utils.uploadAttachment(uuid, 0, newFile).subscribe(new CommonSubscriber(this) {
                                    @Override
                                    public void onNext(final Serializable serializable) {
                                        getAttachments();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        super.onError(e);
                                    }
                                });
                            }
                        }
                    }
                } catch (IOException e) {
                    LogUtil.dll("IO异常");
                    e.printStackTrace();
                }

                break;
            case FinalVariables.REQUEST_DEAL_ATTACHMENT:
                try {
                    final Attachment delAttachment = (Attachment) data.getSerializableExtra("delAtm");
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("bizType", 0);
                    map.put("uuid", uuid);
                    RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class).remove(String.valueOf(delAttachment.getId()), map, new RCallback<Attachment>() {
                        @Override
                        public void success(final Attachment attachment, final Response response) {
                            Toast("删除附件成功!");
                            lstData_Attachment.remove(delAttachment);
                            init_gridView_photo();
                        }

                        @Override
                        public void failure(final RetrofitError error) {
                            Toast("删除附件失败!");
                            super.failure(error);
                        }
                    });
                } catch (Exception e) {
                    Global.ProcException(e);
                }
                break;
            /*选择客户回调*/
            case BaseSearchActivity.REQUEST_SEARCH:
                Customer customer = (Customer) data.getSerializableExtra("data");
                if (null != customer) {
                    customerId = customer.getId();
                    customerName = customer.name;
                }
                tv_customer_name.setText(TextUtils.isEmpty(customerName) ? "无" : customerName);
                break;
            default:

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, 0, null);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
