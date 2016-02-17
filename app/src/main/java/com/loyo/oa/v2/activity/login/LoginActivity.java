package com.loyo.oa.v2.activity.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.MainActivity_;
import com.loyo.oa.v2.activity.VerifyAccountActivity_;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ILogin;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.SharedUtil;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.ViewUtil;
import com.loyo.oa.v2.tool.customview.WaveView;

import org.json.JSONObject;

import java.util.HashMap;

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
    private HashMap<String, String> wxUnionIds = new HashMap<>();
    private JSONObject jsObj;
    private int codes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        edt_username.addTextChangedListener(nameWatcher);
        edt_password.addTextChangedListener(nameWatcher);
        layout_login.setOnClickListener(this);
//        if (Config_project.is_developer_mode) {
//            edt_username.setText("17780704580");
//            edt_password.setText("123456");
//        }
    }

    TextWatcher nameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
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
    public void onClick(View v) {
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
                layout_login.setText("登录中");
                changeColor(-1, R.color.lightgreen);
                HashMap<String, Object> body = new HashMap<String, Object>();
                body.put("username", username);
                body.put("password", password);
                if (!wxUnionIds.isEmpty()) {
                    String wxUninoId = wxUnionIds.get(username);
                    if (TextUtils.isEmpty(wxUninoId))
                        wxUninoId = wxUnionIds.get("bind");
                    if (!TextUtils.isEmpty(wxUninoId))
                        body.put("wxUnionId", wxUninoId);
                }
                login(body, 2);

                break;
            case R.id.tv_qqLogin://企业qq登陆
                app.startActivity(this, LoginBQQActivity.class, MainApp.ENTER_TYPE_BUTTOM, true, null);
                break;
            case R.id.tv_resetPassword://忘记密码
                app.startActivity(this, VerifyAccountActivity_.class, MainApp.ENTER_TYPE_RIGHT, false, null);
                break;
        }
    }

    @Override
    public void onWaveComplete(int color) {
        layout_login.setBackGroundColor(color);
    }

    /**
     * 改变波纹颜色和按钮颜色
     *
     * @param bgColor
     * @param waveColor
     */
    private void changeColor(int bgColor, int waveColor) {
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
     * @param type 1，微信登录；2，普通登录
     *             <p/>
     *             成功 getStatus 状态码
     *             失败 getKind 状态码
     */
    private void login(HashMap<String, Object> body, final int type) {
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(FinalVariables.GET_TOKEN) //URL
                .setLogLevel(RestAdapter.LogLevel.FULL) //是否Debug
                .build();
        adapter.create(ILogin.class).login(body, new RCallback<Token>() {
            @Override
            public void success(Token token, Response response) {
                HttpErrorCheck.checkResponse("登录", response);
                if (null == token || TextUtils.isEmpty(token.access_token)) {

                    Toast(type == 1 ? "微信登录失败,请先填写账号、密码，点击登录按钮绑定微信号" : "登录失败");
                    if (type == 2) {
                        layout_login.setText("登录失败");
                        changeColor(R.color.title_bg1, R.color.red);
                    }
                    return;
                }
                if (2 == type)
                    layout_login.setText("登录成功");

                //登录成功
                MainApp.setToken(token.getAccess_token());
                SharedUtil.put(mContext, FinalVariables.TOKEN, token.getAccess_token());
                app.startActivity(LoginActivity.this, MainActivity_.class, MainApp.ENTER_TYPE_BUTTOM, true, new Bundle());
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
                super.failure(error);
                layout_login.setText("登录失败");
                changeColor(R.color.title_bg1, R.color.red);

            }
        });
    }


    public class Token {

        private String access_token;

        private String error;

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }
}
