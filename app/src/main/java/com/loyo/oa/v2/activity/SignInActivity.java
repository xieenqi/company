package com.loyo.oa.v2.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.adapter.SignInGridViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Attachment;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.LegWork;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseSearchActivity;
import com.loyo.oa.v2.tool.CommonSubscriber;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LocationUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class SignInActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_customer_name;
    private TextView tv_address;
    EditText edt_memo;

    ViewGroup img_title_left, img_title_right;

    private GridView gridView_photo;

    ArrayList<Attachment> lstData_Attachment = new ArrayList<>();

    private String uuid = StringUtil.getUUID();
    private String cc_user_id = null;
    private String cc_department_id = null;
    String mAddress;
    private String customerId;
    private String customerName;
    private SignInGridViewAdapter signInGridViewAdapter;

    ImageView img_refresh_address;

    double mLat, mLng;

    boolean mLocationFlag = false;  //是否定位完成的标记
    private Customer mCustomer;
    private Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        super.setTitle("拜访签到");
        Intent intent = getIntent();
        if (null != intent && intent.hasExtra("data")) {
            mCustomer = (Customer) intent.getSerializableExtra("data");
            customerId = mCustomer.getId();
            customerName = mCustomer.getName();
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

        ViewGroup layout_customer_name = (ViewGroup) findViewById(R.id.layout_customer_name);
        if (null == mCustomer) {
            layout_customer_name.setOnTouchListener(Global.GetTouch());
            layout_customer_name.setOnClickListener(this);
        } else {
            uuid = mCustomer.getUuid();
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
        tv_address.setText("获取当前位置");

        new LocationUtil(this, new LocationUtil.AfterLocation() {
            @Override
            public void OnLocationSucessed(String address, double longitude, double latitude, float radius) {
                img_refresh_address.clearAnimation();
                animation.reset();
                mLat = latitude;
                mLng = longitude;
                mAddress = address;

                boolean gpsOpen = Utils.isGPSOPen(mContext);
                if (radius > 200 || !gpsOpen) {
                    address = address.concat(" (gps未开启)");
                    if (!gpsOpen) {
                        Global.ToastLong("建议开启GPS,重新定位");
                    }
                }
                tv_address.setText(address);
            }

            @Override
            public void OnLocationFailed() {
                img_refresh_address.clearAnimation();
                animation.reset();
                Toast.makeText(SignInActivity.this, "定位失败,请在网络和GPS信号良好时重试", Toast.LENGTH_LONG).show();
            }
        });
    }

    void init_gridView_photo() {
        signInGridViewAdapter = new SignInGridViewAdapter(this, lstData_Attachment, true);
        SignInGridViewAdapter.setAdapter(gridView_photo, signInGridViewAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, 0, null);
                break;
            case R.id.img_title_right:
                addSignIn();
                break;
            case R.id.layout_customer_name:
                Bundle b=new Bundle();
                b.putInt("queryType",1);
                b.putBoolean("isSelect",true);
                app.startActivityForResult(this, CustomerSearchActivity.class, MainApp.ENTER_TYPE_RIGHT, BaseSearchActivity.REQUEST_SEARCH, b);
                break;
            case R.id.img_refresh_address:
                startLocation();
                break;
        }
    }

    /**
     * 新增签到
     */
    private void addSignIn() {
        if (TextUtils.isEmpty(customerId)) {
            Toast("请选择客户");
            return;
        }

        if (TextUtils.isEmpty(mAddress)) {
            Global.ToastLong("无效地址!请刷新地址后重试");
            return;
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("gpsInfo", mLng + "," + mLat);
        map.put("address", mAddress.trim());
        map.put("attachmentUUId", uuid);
        map.put("customerid", customerId);
        if (!StringUtil.isEmpty(edt_memo.getText().toString())) {
            map.put("memo", edt_memo.getText().toString());
        }

        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).addSignIn(map, new RCallback<LegWork>() {
            @Override
            public void success(LegWork legWork, Response response) {

                if (legWork.getCreator() == null) {
                    legWork.setCreator(MainApp.user.toShortUser());
                }
                Toast(getString(R.string.sign) + getString(R.string.app_succeed));
                if (!TextUtils.isEmpty(legWork.getId())) {
                    Intent intent = getIntent();
                    intent.putExtra("data", legWork);
                    app.finishActivity(SignInActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
                } else {
                    Toast(getString(R.string.sign) + "异常!");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                super.failure(error);
                Toast("签到失败");
                Toast(error.getMessage());
            }
        });
    }

    /**
     * 获取附件
     */
    private void getAttachments() {
        Utils.getAttachments(uuid, new RCallback<ArrayList<Attachment>>() {
            @Override
            public void success(ArrayList<Attachment> attachments, Response response) {
                lstData_Attachment = attachments;
                init_gridView_photo();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast("获取附件失败");
                super.failure(error);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null || resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case SelectPicPopupWindow.GET_IMG:

                try {
                    ArrayList<SelectPicPopupWindow.ImageInfo> pickPhots = (ArrayList<SelectPicPopupWindow.ImageInfo>) data.getSerializableExtra("data");
                    for (SelectPicPopupWindow.ImageInfo item : pickPhots) {
                        Uri uri = Uri.parse(item.path);
                        File newFile = Global.scal(this, uri);

                        if (newFile != null && newFile.length() > 0) {
                            if (newFile.exists()) {
                                Utils.uploadAttachment(uuid, newFile).subscribe(new CommonSubscriber(this) {
                                    @Override
                                    public void onNext(Serializable serializable) {
                                        getAttachments();
                                    }
                                });
                            }
                        }
                    }
                } catch (Exception ex) {
                    Global.ProcException(ex);
                }

                break;
            case FinalVariables.REQUEST_DEAL_ATTACHMENT:
                try {
                    final Attachment delAttachment = (Attachment) data.getSerializableExtra("delAtm");
                    RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class).remove(String.valueOf(delAttachment.getId()), new RCallback<Attachment>() {
                        @Override
                        public void success(Attachment attachment, Response response) {
                            Toast("删除附件成功!");
                            lstData_Attachment.remove(delAttachment);
                            init_gridView_photo();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Toast("删除附件失败!");
                            super.failure(error);
                        }
                    });
                } catch (Exception e) {
                    Global.ProcException(e);
                }
                break;
            case BaseSearchActivity.REQUEST_SEARCH:
                Customer customer = (Customer) data.getSerializableExtra("data");
                if (null != customer) {
                    customerId = customer.getId();
                    customerName = customer.getName();
                }
                tv_customer_name.setText(TextUtils.isEmpty(customerName) ? "无" : customerName);
                break;
        }
    }
}
