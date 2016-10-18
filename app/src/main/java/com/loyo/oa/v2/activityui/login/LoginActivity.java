package com.loyo.oa.v2.activityui.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.login.model.Token;
import com.loyo.oa.v2.activityui.login.presenter.LoginPresenter;
import com.loyo.oa.v2.activityui.login.presenter.impl.LoginPresenterImpl;
import com.loyo.oa.v2.activityui.login.viewcontrol.LoginView;
import com.loyo.oa.v2.activityui.setting.VerifyAccountActivity_;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.WaveView;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.ViewUtil;

import java.util.HashMap;

/**
<<<<<<< HEAD
 * 【登录界面】
=======
 * 【登录界面】MVP重构
>>>>>>> organization-merge
 * Restructure by yyy on 2016/10/8
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, WaveView.OnWaveCompleteListener,LoginView {

    private EditText edt_username, edt_password;
    private WaveView layout_login;
    private TextView tv_resetPassword, tv_qqLogin;
    private LinearLayout serverTest, serverFormal, layout_check_debug;
    private ImageView serverTestImg, serverFormalImg;
    private TextView serverTestTv, serverFormalTv;
    private LoginPresenter loginPresenter;


    private String username;
    private String password;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tintManager.setTintColor(android.R.color.transparent);
        setContentView(R.layout.activity_login);
        super.isNeedLogin = false;
        initUI();
    }

    /**
     * UI初始化
     * */
    void initUI() {
        layout_login = (WaveView) findViewById(R.id.layout_login);
        layout_login.setCallback(this);
        tv_qqLogin = (TextView) findViewById(R.id.tv_qqLogin);
        tv_resetPassword = (TextView) findViewById(R.id.tv_resetPassword);
        tv_qqLogin.setOnClickListener(this);
        tv_qqLogin.setOnTouchListener(new ViewUtil.OnTouchListener_view_transparency());
        tv_resetPassword.setOnTouchListener(Global.GetTouch());
        tv_resetPassword.setOnClickListener(this);
        edt_username = (EditText) findViewById(R.id.edt_username);
        edt_password = (EditText) findViewById(R.id.edt_password);
        layout_check_debug = (LinearLayout) findViewById(R.id.layout_check_debug);
        serverTest = (LinearLayout) findViewById(R.id.layout_server_test);
        serverFormal = (LinearLayout) findViewById(R.id.layout_server_formal);
        serverTestImg = (ImageView) findViewById(R.id.iv_server_test);
        serverFormalImg = (ImageView) findViewById(R.id.iv_server_formal);
        serverTestTv = (TextView) findViewById(R.id.tv_server_test);
        serverFormalTv = (TextView) findViewById(R.id.tv_server_formal);
        layout_login.setOnClickListener(this);

        /*Debug模式下*/
        if (!Config_project.isRelease) {
            edt_username.setText("17412345678");
            edt_password.setText("123456");
            if (Config_project.isRelease) {
                serverFormalTv.setTextColor(getResources().getColor(R.color.text33));
                serverTestTv.setTextColor(getResources().getColor(R.color.text99));
                serverTestImg.setVisibility(View.INVISIBLE);
                serverFormalImg.setVisibility(View.VISIBLE);
            } else {
                serverTestTv.setTextColor(getResources().getColor(R.color.text33));
                serverFormalTv.setTextColor(getResources().getColor(R.color.text99));
                serverTestImg.setVisibility(View.VISIBLE);
                serverFormalImg.setVisibility(View.INVISIBLE);
            }
        }

        /*测试环境*/
        serverTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Config_project.isRelease = false;
                serverTestTv.setTextColor(getResources().getColor(R.color.text33));
                serverFormalTv.setTextColor(getResources().getColor(R.color.text99));
                serverTestImg.setVisibility(View.VISIBLE);
                serverFormalImg.setVisibility(View.INVISIBLE);
                LogUtil.d("isRelease:" + Config_project.isRelease);
            }
        });

        /*正式环境*/
        serverFormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Config_project.isRelease = true;
                serverFormalTv.setTextColor(getResources().getColor(R.color.text33));
                serverTestTv.setTextColor(getResources().getColor(R.color.text99));
                serverTestImg.setVisibility(View.INVISIBLE);
                serverFormalImg.setVisibility(View.VISIBLE);
                LogUtil.d("isRelease:" + Config_project.isRelease);
            }
        });
        loginPresenter = new LoginPresenterImpl(this,layout_login,mContext);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {

            /*登录*/
            case R.id.layout_login:
                username = edt_username.getText().toString().trim();
                password = edt_password.getText().toString().trim();
                loginPresenter.requestStandBy(username,password);
                break;

            /*企业qq登陆*/

            case R.id.tv_qqLogin:
                app.startActivity(this, LoginBQQActivity.class, MainApp.ENTER_TYPE_RIGHT, true, null);
                break;
            /*忘记密码*/

            case R.id.tv_resetPassword:
                app.startActivity(this, VerifyAccountActivity_.class, MainApp.ENTER_TYPE_RIGHT, false, null);
                break;

            default:
                break;
        }
    }

    /**
     * WaveView赋值
     * */
    @Override

    public void onWaveComplete(final int color) {
        layout_login.setBackGroundColor(color);
    }

    /**
     * 验证失败
     * */
    @Override
    public void verifyError(int code) {
        switch (code){
            case 1:
                Toast("帐号不能为空!");
                break;

            case 2:
                Toast("密码不能为空!");
                break;

        }
    }

    /**
     * 登录成功
     * */
    @Override
    public void onSuccess(Token token) {
        loginPresenter.onSuccessEmbl(token,LoginActivity.this);
    }

    /**
     * 登录失败
     * */
    @Override
    public void onError() {
        loginPresenter.onErrorEmbl();
    }

    /**
     * 输入框监听
     * */
    @Override
    public void editTextListener() {
        if (!TextUtils.equals(layout_login.getText(), "登录")) {
            layout_login.setText("登录");
            layout_login.setBackGroundColor(getResources().getColor(R.color.title_bg1));
            layout_login.setMode(WaveView.WAVE_MODE_SHRINK);
            layout_login.setChangeColor(false);
            layout_login.startDraw();
        }
    }

    /**
     * 登录验证通过
     * */
    @Override
    public void verifySuccess() {
        layout_login.setText("登录中...");
        loginPresenter.changeColor(-1, R.color.lightgreen);
        HashMap<String, Object> body = new HashMap<String, Object>();
        body.put("username", username);
        body.put("password", password);
        loginPresenter.requestLogin(body);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
