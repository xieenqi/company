package com.loyo.oa.v2.activityui.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.home.api.HomeService;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBean;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseActivity;

import java.util.HashMap;

/**
 * 【更换手机号】 第一步
 * Created by xeq on 16/11/7.
 */

public class RenewalMobileOneActivty extends BaseActivity implements View.OnClickListener {

    private LinearLayout ll_back;
    private TextView tv_title;
    private TextView tv_add;
    private EditText et_pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renewal_mobile_one);
        initView();
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_add = (TextView) findViewById(R.id.tv_add);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        tv_title.setText("更换手机号");
        tv_add.setText("下一步");
        showInputKeyboard(et_pwd);
        Global.SetTouchView(ll_back, tv_add);
        ll_back.setOnClickListener(this);
        tv_add.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.tv_add:
                verifyPasseord();
                break;
        }
    }

    /**
     * 验证密码
     */
    private void verifyPasseord() {
        showLoading2("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("password", et_pwd.getText().toString());

        HomeService.verifyPasseord(map).subscribe(new DefaultLoyoSubscriber<BaseBean>(hud) {

            @Override
            public void onNext(BaseBean o) {
                if (o.errcode == 0) {
                    app.startActivity(RenewalMobileOneActivty.this, RenewalMobileTwoActivty.class,
                            MainApp.ENTER_TYPE_RIGHT, false, null);
                } else {
                    Toast(o.errmsg);
                }
            }
        });
    }
}
