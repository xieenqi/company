package com.loyo.oa.v2.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.adapter.SignInGridViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Attachment;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.LegWork;
import com.loyo.oa.v2.common.Global;
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

    TextView tv_address;
    TextView tv_customer_name;
    TextView tv_memo;
    ViewGroup img_title_left;
    GridView gridView_photo;
    ViewGroup layout_customer_info;
    private Customer mCustomer;

    SignInGridViewAdapter signInGridViewAdapter;
    ArrayList<Attachment> lstData_Attachment;
    LegWork legWork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_info);

        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                if (bundle.containsKey("mCustomer")) {
                    mCustomer = (Customer) bundle.getSerializable("mCustomer");

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
            public void onClick(View v) {
                app.finishActivity((Activity) v.getContext(), MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, null);
            }
        });

        img_title_left.setOnTouchListener(ViewUtil.OnTouchListener_view_transparency.Instance());

        layout_customer_info = (ViewGroup) findViewById(R.id.layout_customer_info);
        layout_customer_info.setOnTouchListener(Global.GetTouch());
        layout_customer_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                b.putString("Id", legWork.getCustomerId());
                app.startActivityForResult(SignInfoActivity.this, CustomerDetailInfoActivity_.class, 0, REQUEST_PREVIEW_CUSTOMER_INFO, b);
            }
        });

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
                public void success(LegWork _legWork, Response response) {
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
        Utils.getAttachments(legWork.getAttachmentUUId(), new RCallback<ArrayList<Attachment>>() {
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


    void updateUI() {
        if (legWork != null) {
            tv_address.setText(legWork.getAddress());
            tv_memo.setText(legWork.getMemo());

            String name = null == mCustomer ? legWork.getCustomerName() : mCustomer.name;
            tv_customer_name.setText(name);
        }

        if (legWork != null) {
            lstData_Attachment = legWork.getAttachments();
            if (null == lstData_Attachment) {
                getAttachments();
            } else {
                init_gridView_photo();
            }
        }
    }

    void init_gridView_photo() {
        if(null==lstData_Attachment){
            return;
        }
        signInGridViewAdapter = new SignInGridViewAdapter(this, lstData_Attachment, false);
        SignInGridViewAdapter.setAdapter(gridView_photo, signInGridViewAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_PREVIEW_CUSTOMER_INFO || resultCode != Activity.RESULT_OK || null == data) {
            return;
        }
        Customer customer = (Customer) data.getSerializableExtra(Customer.class.getName());
        legWork.setAddress(customer.loc.getAddr());
        legWork.setCustomerName(customer.name);
        updateUI();
    }
}
