package com.loyo.oa.v2.activity.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.VerifyAccountActivity_;
import com.loyo.oa.v2.activity.home.NewMainActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ILogin;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.SharedUtil;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.ViewUtil;
import com.loyo.oa.v2.tool.customview.WaveView;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 登陆界面
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, WaveView.OnWaveCompleteListener {

    private EditText edt_username, edt_password;
    private WaveView layout_login;
    private TextView tv_resetPassword, tv_qqLogin;
    private LinearLayout serverTest, serverFormal, layout_check_debug;
    private ImageView serverTestImg, serverFormalImg;
    private TextView serverTestTv, serverFormalTv;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tintManager.setTintColor(android.R.color.transparent);
        setContentView(R.layout.activity_login);
        super.isNeedLogin = false;
        initUI();
    }

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

        edt_username.addTextChangedListener(nameWatcher);
        edt_password.addTextChangedListener(nameWatcher);
        layout_login.setOnClickListener(this);

        /*Debug模式下*/
        if (Config_project.is_developer_mode) {
            edt_username.setText("18235169100");
            edt_password.setText("123456");
//            layout_check_debug.setVisibility(View.VISIBLE);
            if (Config_project.isRelease) {
                serverFormalTv.setTextColor(getResources().getColor(R.color.black));
                serverTestTv.setTextColor(getResources().getColor(R.color.gray));

                serverTestImg.setVisibility(View.INVISIBLE);
                serverFormalImg.setVisibility(View.VISIBLE);
            } else {
                serverTestTv.setTextColor(getResources().getColor(R.color.black));
                serverFormalTv.setTextColor(getResources().getColor(R.color.gray));

                serverTestImg.setVisibility(View.VISIBLE);
                serverFormalImg.setVisibility(View.INVISIBLE);
            }
        }

        /*测试环境*/
        serverTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Config_project.isRelease = false;
                serverTestTv.setTextColor(getResources().getColor(R.color.black));
                serverFormalTv.setTextColor(getResources().getColor(R.color.gray));
                serverTestImg.setVisibility(View.VISIBLE);
                serverFormalImg.setVisibility(View.INVISIBLE);
                LogUtil.d("isRelease:"+Config_project.isRelease);
            }
        });

        /*正式环境*/
        serverFormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Config_project.isRelease = true;
                serverFormalTv.setTextColor(getResources().getColor(R.color.black));
                serverTestTv.setTextColor(getResources().getColor(R.color.gray));
                serverTestImg.setVisibility(View.INVISIBLE);
                serverFormalImg.setVisibility(View.VISIBLE);
                LogUtil.d("isRelease:" + Config_project.isRelease);
            }
        });
    }

    TextWatcher nameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {

        }

        @Override
        public void onTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {

        }

        @Override
        public void afterTextChanged(final Editable editable) {
            if (!TextUtils.equals(layout_login.getText(), "登录")) {
                resetLogin();
            }
        }
    };

    private void resetLogin() {
        layout_login.setText("登录");
        layout_login.setBackGroundColor(getResources().getColor(R.color.title_bg1));
        layout_login.setMode(WaveView.WAVE_MODE_SHRINK);
        layout_login.setChangeColor(false);
        layout_login.startDraw();
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.layout_login:
                String username = edt_username.getText().toString().trim();
                String password = edt_password.getText().toString().trim();
                if (StringUtil.isEmpty(username)) {
                    Toast("帐号不能为空!");
                    return;
                }
                if (StringUtil.isEmpty(password)) {
                    Toast("密码不能为空!");
                    return;
                }
                layout_login.setText("登录中...");
                changeColor(-1, R.color.lightgreen);
                HashMap<String, Object> body = new HashMap<String, Object>();
                body.put("username", username);
                body.put("password", password);
                login(body);
                LogUtil.d("login ：" + Config_project.isRelease);
                break;
            case R.id.tv_qqLogin://企业qq登陆
                app.startActivity(this, LoginBQQActivity.class, MainApp.ENTER_TYPE_BUTTOM, true, null);
                break;
            case R.id.tv_resetPassword://忘记密码
                app.startActivity(this, VerifyAccountActivity_.class, MainApp.ENTER_TYPE_RIGHT, false, null);
                break;
            default:

                break;
        }
    }

    @Override
    public void onWaveComplete(final int color) {
        layout_login.setBackGroundColor(color);
    }

    /**
     * 改变波纹颜色和按钮颜色
     *
     * @param bgColor
     * @param waveColor
     */
    private void changeColor(final int bgColor, final int waveColor) {
        if (bgColor > 0)
            layout_login.setBackGroundColor(getResources().getColor(bgColor));
        if (waveColor > 0)
            layout_login.setWaveColor(getResources().getColor(waveColor));
        layout_login.setMode(WaveView.WAVE_MODE_SPREAD);
        layout_login.setChangeColor(true);
        layout_login.startDraw();
    }

    /**
     * 登陆
     *
     * @param body 表单
     *             、 @param type 1，微信登录；2，普通登录
     *             <p/>
     *             成功 getStatus 状态码
     *             失败 getKind 状态码
     */
    private void login(final HashMap<String, Object> body) {
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(FinalVariables.GET_TOKEN) //URL
                .setLogLevel(RestAdapter.LogLevel.FULL) //是否Debug
                .build();
        adapter.create(ILogin.class).login(body, new RCallback<Token>() {
            @Override
            public void success(final Token token, final Response response) {
                HttpErrorCheck.checkResponse(response);
                if (null == token || TextUtils.isEmpty(token.access_token)) {
                    loginFial();
                    return;
                } else {
                    loginSuccess(token);
                    layout_login.setText("登录成功");
                }
            }

            @Override
            public void failure(final RetrofitError error) {
                HttpErrorCheck.checkError(error);
                super.failure(error);
                loginFial();
            }
        });
    }

    /**
     * 登录失败
     */
    private void loginFial() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        changeColor(R.color.title_bg1, R.color.red);
                        layout_login.setText("登录失败");
                    }
                });
            }
        }, 500);
    }

    /**
     * 登录cg
     */
    private void loginSuccess(final Token token) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //登录成功
                        MainApp.setToken(token.access_token);
                        SharedUtil.put(mContext, FinalVariables.TOKEN, token.access_token);
                        SharedUtil.putBoolean(getApplicationContext(), ExtraAndResult.WELCOM_KEY, true);//预览过引导页面内
                        //app.startActivity(LoginActivity.this, MainActivity_.class, MainApp.ENTER_TYPE_BUTTOM, true, new Bundle());
                        app.startActivity(LoginActivity.this, NewMainActivity.class, MainApp.ENTER_TYPE_BUTTOM, true, new Bundle());
                    }
                });
            }
        }, 200);

    }

    public class Token {
        public String access_token;
        public String error;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }
}
