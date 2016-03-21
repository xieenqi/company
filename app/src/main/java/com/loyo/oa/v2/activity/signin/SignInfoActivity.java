package com.loyo.oa.v2.activity.signin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.customer.CustomerDetailInfoActivity_;
import com.loyo.oa.v2.adapter.SignInGridViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Attachment;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.LegWork;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.ViewUtil;

import java.util.ArrayList;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【签到详情】 页面  xnq
 */
public class SignInfoActivity extends BaseActivity {
    private static final int REQUEST_PREVIEW_CUSTOMER_INFO = 320;

    private TextView tv_address;
    private TextView tv_customer_name;
    private TextView tv_memo;
    private ViewGroup img_title_left;
    private GridView gridView_photo;
    private ViewGroup layout_customer_info;
    private SignInGridViewAdapter signInGridViewAdapter;
    private ArrayList<Attachment> lstData_Attachment;
    private Customer mCustomer;
    private boolean isFormCustom;
    private LegWork legWork;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_info);

        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                if (bundle.containsKey("mCustomer")) {
                    mCustomer = (Customer) bundle.getSerializable("mCustomer");
                    isFormCustom = bundle.getBoolean(ExtraAndResult.EXTRA_STATUS);
                }
                if (bundle.containsKey(LegWork.class.getName())) {
                    legWork = (LegWork) bundle.getSerializable(LegWork.class.getName());
                }
            }
        }

        super.setTitle("签到详情");
        initUI();
    }

    void initUI() {
        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                app.finishActivity((Activity) v.getContext(), MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, null);
            }
        });

        img_title_left.setOnTouchListener(ViewUtil.OnTouchListener_view_transparency.Instance());

        layout_customer_info = (ViewGroup) findViewById(R.id.layout_customer_info);
        // layout_customer_info.setOnTouchListener(Global.GetTouch());
        if (!isFormCustom) {
            layout_customer_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    Bundle b = new Bundle();
                    b.putString("Id", legWork.customerId);
                    app.startActivityForResult(SignInfoActivity.this, CustomerDetailInfoActivity_.class, 0, REQUEST_PREVIEW_CUSTOMER_INFO, b);
                }
            });

        }

        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_customer_name = (TextView) findViewById(R.id.tv_customer_name);
        tv_memo = (TextView) findViewById(R.id.tv_memo);
        gridView_photo = (GridView) findViewById(R.id.gridView_photo);
        getLegwork();
    }

    /**
     * 获取签到详情
     */
    private void getLegwork() {
        if (legWork != null) {
            RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).getLegwork(legWork.getId(), new RCallback<LegWork>() {
                @Override
                public void success(final LegWork _legWork, final Response response) {
                    legWork = _legWork;
                    updateUI();
                }
            });
        }
    }

    /**
     * 获取附件
     */
    private void getAttachments() {
        Utils.getAttachments(legWork.attachmentUUId, new RCallback<ArrayList<Attachment>>() {
            @Override
            public void success(final ArrayList<Attachment> attachments, final Response response) {
                lstData_Attachment = attachments;
                init_gridView_photo();
            }

            @Override
            public void failure(final RetrofitError error) {
                Toast("获取附件失败");
                super.failure(error);
            }
        });
    }


    void updateUI() {
        if (legWork != null) {
            tv_address.setText(legWork.address);
            tv_memo.setText(legWork.memo);

            if(null != legWork.customerName){
                tv_customer_name.setText(legWork.customerName);
            }else{
                tv_customer_name.setTextColor(this.getResources().getColor(R.color.diseditable));
                tv_customer_name.setText("未指定拜访客户");
                layout_customer_info.setEnabled(false);
            }
        }

        if (legWork != null) {
            lstData_Attachment = legWork.attachments;
            if (null == lstData_Attachment) {
                getAttachments();
            } else {
                init_gridView_photo();
            }
        }
    }

    void init_gridView_photo() {
        if (null == lstData_Attachment) {
            return;
        }
        signInGridViewAdapter = new SignInGridViewAdapter(this, lstData_Attachment, false, false,0);
        SignInGridViewAdapter.setAdapter(gridView_photo, signInGridViewAdapter);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_PREVIEW_CUSTOMER_INFO || resultCode != Activity.RESULT_OK || null == data) {
            return;
        }
        Customer customer = (Customer) data.getSerializableExtra(Customer.class.getName());
        legWork.address = customer.loc.addr;
        legWork.customerName = customer.name;
        updateUI();
    }
}
