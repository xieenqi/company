package com.loyo.oa.v2.activityui.setting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.home.fragment.MenuFragment;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.activityui.sale.model.SaleStageConfig;
import com.loyo.oa.v2.activityui.setting.persenter.SettingPControl;
import com.loyo.oa.v2.activityui.setting.viewcontrol.SettingVControl;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IUser;
import com.loyo.oa.v2.service.InitDataService_;
import com.loyo.oa.v2.service.OrganizationService;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;


import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【设置】页面
 * Created by xeq on 16/11/2.
 */

public class SettingActivity extends BaseActivity implements View.OnClickListener, SettingVControl {

    private LinearLayout img_title_left, ll_cellphone, ll_setpassword, ll_clean, btn_exit, ll_update;
    private TextView tv_title_1, iv_cell_number, tv_cache_size;
    private ImageView iv_cell_status;
    private SettingPControl pControl;
    /* Broadcasr */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if ("com.loyo.oa.v2.ORGANIZATION_UPDATED".equals(intent.getAction())) {
                //TODO 此处主要接受组织架构的跟新 以后其它的更新在规整
                Toast("更新成功!");
                cancelLoading();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        registerBroadcastReceiver();
    }

    private void initView() {
        img_title_left = (LinearLayout) findViewById(R.id.img_title_left);
        ll_cellphone = (LinearLayout) findViewById(R.id.ll_cellphone);
        ll_setpassword = (LinearLayout) findViewById(R.id.ll_setpassword);
        ll_clean = (LinearLayout) findViewById(R.id.ll_clean);
        ll_update = (LinearLayout) findViewById(R.id.ll_update);
        btn_exit = (LinearLayout) findViewById(R.id.btn_exit);
        tv_title_1 = (TextView) findViewById(R.id.tv_title_1);
        tv_title_1.setText("设置");
        iv_cell_number = (TextView) findViewById(R.id.iv_cell_number);
        tv_cache_size = (TextView) findViewById(R.id.tv_cache_size);
        iv_cell_status = (ImageView) findViewById(R.id.iv_cell_status);
        img_title_left.setOnClickListener(this);
        ll_cellphone.setOnClickListener(this);
        ll_setpassword.setOnClickListener(this);
        ll_clean.setOnClickListener(this);
        ll_update.setOnClickListener(this);
        btn_exit.setOnClickListener(this);
        pControl = new SettingPControl(this);
        Global.SetTouchView(img_title_left, ll_cellphone, ll_setpassword, ll_clean, ll_update, btn_exit);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                onBackPressed();
                break;
            case R.id.ll_cellphone:
                Bundle bundle = new Bundle();
                if (MainApp.user != null && !TextUtils.isEmpty(MainApp.user.mobile)) {
                    bundle.putInt(ExtraAndResult.SEND_ACTION, EditUserMobileActivity.ACTION_RENEWAL);
                    bundle.putString(ExtraAndResult.EXTRA_DATA, MainApp.user.mobile);
                } else {
                    bundle.putInt(ExtraAndResult.SEND_ACTION, EditUserMobileActivity.ACTION_BINDING);
                }
                MainApp.getMainApp().startActivity(SettingActivity.this, EditUserMobileActivity.class, MainApp.ENTER_TYPE_RIGHT, false, bundle);
                break;
            case R.id.ll_setpassword:
                app.startActivity(SettingActivity.this, SettingPasswordActivity_.class, MainApp.ENTER_TYPE_RIGHT, false, null);
                break;
            case R.id.ll_clean:
                sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        dismissSweetAlert();
                    }
                }, new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        showProgress("");
                        ImageLoader.getInstance().clearDiskCache();//清除本地磁盘缓存
                        pControl.diskCacheInfo();
                        dismissSweetAlert();
                    }
                }, "提醒", "确认清除缓存?");
                break;
            case R.id.ll_update:
                if (Utils.isNetworkAvailable(SettingActivity.this)) {
                    showLoading("正在更新组织架构，请稍等", false);
                    rushHomeData();
                    initService();
                    SaleStageConfig.getSaleStage();
                } else {
                    Toast("请检查您的网络连接");
                }
                break;
            case R.id.btn_exit:
                MenuFragment.callback.onExit(SettingActivity.this);
                break;
        }
    }

    public void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter("com.loyo.oa.v2.USER_EDITED");
        filter.addAction("com.loyo.oa.v2.ORGANIZATION_UPDATED");
        registerReceiver(mReceiver, filter);
    }

    /**
     * 更新 组织架构
     */
    void initService() {
        /* 更新登录用户信息 */
        InitDataService_.intent(SettingActivity.this).start();
        /* 拉取组织架构 */
        OrganizationService.startActionFetchAll(MainApp.getMainApp());
    }

    /**
     * 更新(当首页红点数据异常)
     */
    void rushHomeData() {
        RestAdapterFactory.getInstance().build(FinalVariables.RUSH_HOMEDATA).create(IUser.class).rushHomeDate(new RCallback<User>() {
            @Override
            public void success(final User user, final Response response) {
                HttpErrorCheck.checkResponse(response);
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void showProgress(String message) {
        showLoading("");
    }

    @Override
    public void hideProgress() {
        cancelLoading();
    }

    @Override
    public void showMsg(String message) {

    }

    @Override
    public void setCell(String cell) {
        iv_cell_number.setText(cell);
        iv_cell_status.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setCache(String cache) {
        tv_cache_size.setText(cache);
    }

}
