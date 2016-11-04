package com.loyo.oa.v2.activityui.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.setting.persenter.SettingPControl;
import com.loyo.oa.v2.activityui.setting.viewcontrol.SettingVControl;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 【设置】页面
 * Created by xeq on 16/11/2.
 */

public class SettingActivity extends BaseActivity implements View.OnClickListener, SettingVControl {

    private LinearLayout img_title_left, ll_cellphone, ll_setpassword, ll_clean, btn_exit, ll_update;
    private TextView tv_title_1, iv_cell_number, tv_cache_size;
    private ImageView iv_cell_status;
    private SettingPControl pControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
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
        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_left.setOnClickListener(this);
        ll_cellphone.setOnTouchListener(Global.GetTouch());
        ll_cellphone.setOnClickListener(this);
        ll_setpassword.setOnTouchListener(Global.GetTouch());
        ll_setpassword.setOnClickListener(this);
        ll_clean.setOnTouchListener(Global.GetTouch());
        ll_clean.setOnClickListener(this);
        ll_update.setOnTouchListener(Global.GetTouch());
        ll_update.setOnClickListener(this);
        btn_exit.setOnTouchListener(Global.GetTouch());
        btn_exit.setOnClickListener(this);
        pControl = new SettingPControl(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                onBackPressed();
                break;
            case R.id.ll_cellphone:
                Bundle bundle = new Bundle();
                bundle.putInt(ExtraAndResult.SEND_ACTION, EditUserMobileActivity.ACTION_BINDING);
                MainApp.getMainApp().startActivity(SettingActivity.this, EditUserMobileActivity.class, MainApp.ENTER_TYPE_RIGHT, false, bundle);
                break;
            case R.id.ll_setpassword:
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
                break;
            case R.id.btn_exit:
                break;
        }
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
