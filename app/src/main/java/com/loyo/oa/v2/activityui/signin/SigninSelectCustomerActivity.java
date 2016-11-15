package com.loyo.oa.v2.activityui.signin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.signin.adapter.SigninSelectCustomerAdapter;
import com.loyo.oa.v2.activityui.signin.bean.SigninSelectCustomer;
import com.loyo.oa.v2.activityui.signin.persenter.SigninSelectCustomerPControl;
import com.loyo.oa.v2.activityui.signin.viewcontrol.SigninSelectCustomerVControl;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LocationUtilGD;
import com.loyo.oa.v2.tool.UMengTools;

import java.util.ArrayList;

/**
 * 拜访签到【选择客户】页面
 * Created by xeq on 16/11/15.
 */

public class SigninSelectCustomerActivity extends BaseActivity implements View.OnClickListener,
        PullToRefreshBase.OnRefreshListener2, SigninSelectCustomerVControl {

    private PullToRefreshListView lv_list;
    private SigninSelectCustomerPControl pControl;
    private SigninSelectCustomerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_select_customer);
        pControl = new SigninSelectCustomerPControl(this);
        initView();
    }

    private void initView() {
        findViewById(R.id.ll_back).setOnClickListener(this);
        findViewById(R.id.ll_search).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_title)).setText("选择客户");
        lv_list = (PullToRefreshListView) findViewById(R.id.lv_list);
        lv_list.setMode(PullToRefreshBase.Mode.BOTH);
        lv_list.setOnRefreshListener(this);
        adapter = new SigninSelectCustomerAdapter(app);
        lv_list.setAdapter(adapter);
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SigninSelectCustomer item = adapter.getItemData(position);
                Intent intent = new Intent();
                intent.putExtra("id", item.id);
                intent.putExtra("name", item.name);
                intent.putExtra("address", item.loc.addr);
                setResult(RESULT_OK, intent);
                onBackPressed();
            }
        });
        startLocation();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.ll_search:
                Toast("等待开借口呢");
                break;

        }
    }

    private void startLocation() {
        new LocationUtilGD(this, new LocationUtilGD.AfterLocation() {
            @Override
            public void OnLocationGDSucessed(final String address, final double longitude, final double latitude, final String radius) {
                pControl.oneGetNearCustomer(longitude, latitude);
                LocationUtilGD.sotpLocation();
                UMengTools.sendLocationInfo(address, longitude, latitude);
            }

            @Override
            public void OnLocationGDFailed() {
                Toast("获取附近客户信息失败！");
                LocationUtilGD.sotpLocation();
            }
        });
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        pControl.pullDown();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        pControl.pullUp();
    }

    @Override
    public void showProgress(String message) {
        showLoading("");
    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showMsg(String message) {
        Toast(message);
    }

    @Override
    public void getDataComplete() {
        lv_list.onRefreshComplete();
    }

    @Override
    public void bindData(ArrayList<SigninSelectCustomer> data) {
        adapter.setData(data);
    }
}
