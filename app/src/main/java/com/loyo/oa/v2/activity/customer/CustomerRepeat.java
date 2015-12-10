package com.loyo.oa.v2.activity.customer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.adapter.CustomerRepeatAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.City;
import com.loyo.oa.v2.beans.CustomerRepeatList;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;

import org.androidannotations.annotations.Click;

import java.io.IOException;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 客户管理，查重
 * Created by yyy on 15/12/9.
 */
public class CustomerRepeat extends BaseActivity {

    private LinearLayout ll_showonly,img_title_left;
    private ListView lv_list;
    private TextView tv_customer_onlyname, tv_serach;
    private EditText edt_serach;
    private Intent mIntent;
    private CustomerRepeatAdapter adapter;
    private boolean isOk;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x01) {
                tv_customer_onlyname.setText("不存在该用户，点击" + edt_serach.getText().toString() + ",创建该客户");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_repeat);
        initUi();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {

                case R.id.img_title_left:
                    finish();
                    break;

                case R.id.ll_showonly:
                    Intent intent = new Intent();
                    intent.putExtra("name", edt_serach.getText().toString());
                    app.finishActivity((Activity) mContext, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
                    break;

                case R.id.tv_serach:
                    if (edt_serach.getText().toString().isEmpty()) {
                        Toast("搜索框不能为空");
                    }
                    serachRepate(edt_serach.getText().toString());
                    break;

            }
        }
    };

    /**
     * 数据初始化
     * */
    void initUi() {

        mIntent = getIntent();
        ll_showonly = (LinearLayout) findViewById(R.id.ll_showonly);
        lv_list = (ListView) findViewById(R.id.lv_list);
        tv_customer_onlyname = (TextView) findViewById(R.id.tv_customer_onlyname);
        edt_serach = (EditText) findViewById(R.id.edt_serach);
        tv_serach = (TextView) findViewById(R.id.tv_serach);
        img_title_left = (LinearLayout)findViewById(R.id.img_title_left);

        edt_serach.setText(mIntent.getStringExtra("name"));
        serachRepate(mIntent.getStringExtra("name"));

        ll_showonly.setOnClickListener(onClickListener);
        tv_serach.setOnClickListener(onClickListener);
        img_title_left.setOnClickListener(onClickListener);


    }

    /**
     * 查重请求
     */
    void serachRepate(String name) {

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("pageIndex", 1);
        map.put("pageSize", 20);
        map.put("keyWords", name);

        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).getSerachRepeat(map, new RCallback<PaginationX<CustomerRepeatList>>() {
            @Override
            public void success(PaginationX<CustomerRepeatList> customerRepeatList, Response response) {
                LogUtil.dll("success result:" + MainApp.gson.toJson(customerRepeatList));
                setViewdata(customerRepeatList);
                mHandler.sendEmptyMessage(0x01);
            }

            @Override
            public void failure(RetrofitError error) {

                if (error.getKind() == RetrofitError.Kind.NETWORK) {

                    Toast("请检查您的网络连接");

                } else if (error.getKind() == RetrofitError.Kind.HTTP) {
                    if (error.getResponse().getStatus() == 500) {
                        Toast("网络异常500,请稍候再试");
                    }
                }
                finish();
            }
        });
    }

    /**
     * 若没有相同数据，则展示ll_showonly,提示该名字可以创建
     */
    void setViewdata(PaginationX<CustomerRepeatList> customerRepeatList) {

        if (customerRepeatList.getRecords().size() != 0) {
            for (int i = 0; i < customerRepeatList.getRecords().size(); i++) {
                isOk = customerRepeatList.getRecords().get(i).getName().equals(edt_serach.getText().toString()) ? true : false;
            }
        } else {
            isOk = false;
        }

        if (isOk == false) {
            ll_showonly.setVisibility(View.VISIBLE);
            tv_customer_onlyname.setText("不存在该用户，点击" + edt_serach.getText().toString() + ",创建该客户");
        } else if (isOk) {
            ll_showonly.setVisibility(View.GONE);
        }

        adapter = new CustomerRepeatAdapter(customerRepeatList, CustomerRepeat.this);
        lv_list.setAdapter(adapter);

    }
}
