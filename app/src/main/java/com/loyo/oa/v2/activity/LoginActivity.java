package com.loyo.oa.v2.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.point.ILogin;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.SharedUtil;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.ViewUtil;
import com.loyo.oa.v2.tool.WXUtil;
import com.loyo.oa.v2.tool.customview.WaveView;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 登陆界面
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, WaveView.OnWaveCompleteListener {
    EditText edt_username, edt_password;
    WaveView layout_login;
    ViewGroup tv_bqq_login;
    ViewGroup tv_bwx_login;
    ViewGroup layout_third_login, layout_reset_password;
    IWXAPI iwxapi;
    HashMap<String, String> wxUnionIds = new HashMap<>();

    private JSONObject jsObj;
    private int codes;

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action) && action.equals(WXUtil.ACTION_WX_CODE_RETURN))
                getWxToken();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        super.isNeedLogin = false;
        iwxapi = WXUtil.getInstance().getIwxapi();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter(WXUtil.ACTION_WX_CODE_RETURN));
        initUI();
    }

    /**
     * 获取微信TOKEN和APPID
     */
    private void getWxToken() {
        wxUnionIds.clear();
        WXUtil.getInstance().getWxAccessToken(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    JSONObject json = new JSONObject(new String(bytes));
                    Log.e(getClass().getSimpleName(), "getWxAccessToken,result : " + json.toString());
                    if (!json.has("errcode")) {
                        WXUtil.wxToken = (String) json.get("access_token");
                        WXUtil.wxOpenId = (String) json.get("openid");
                        String wxUninoId = (String) json.get("unionid");
                        WXUtil.wxUnionid = wxUninoId;
                        String account = edt_username.getText().toString().trim();
                        if (TextUtils.isEmpty(account))
                            account = "bind";
                        wxUnionIds.put(account, wxUninoId);
                        Log.e(getClass().getSimpleName(), "getWxAccessToken,success,wxToken : " + WXUtil.wxToken + " wxOpenId : " + WXUtil.wxOpenId + " unionid : " + WXUtil.wxUnionid);
                        loginWithWx(wxUninoId);
                    } else
                        Toast("获取微信OPENID和TOKEN失败");
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "getWxAccessToken,success,exception : " + e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast("获取微信OPENID和TOKEN失败");
            }
        });
    }

    void initUI() {
        layout_login = (WaveView) findViewById(R.id.layout_login);
        layout_login.setCallback(this);

        layout_third_login = (ViewGroup) findViewById(R.id.layout_third_login);

        tv_bqq_login = (ViewGroup) findViewById(R.id.tv_bqq_login);
        //tv_bwx_login = (ViewGroup) findViewById(R.id.tv_bwx_login);
        layout_reset_password = (ViewGroup) findViewById(R.id.layout_reset_password);
        tv_bqq_login.setOnClickListener(this);
        //tv_bwx_login.setOnClickListener(this);
        tv_bqq_login.setOnTouchListener(new ViewUtil.OnTouchListener_view_transparency());
        //tv_bwx_login.setOnTouchListener(Global.GetTouch());
        layout_reset_password.setOnTouchListener(Global.GetTouch());
        layout_reset_password.setOnClickListener(this);

        edt_username = (EditText) findViewById(R.id.edt_username);
        edt_password = (EditText) findViewById(R.id.edt_password);

        edt_username.addTextChangedListener(nameWatcher);
        edt_password.addTextChangedListener(nameWatcher);


        layout_login.setOnClickListener(this);

        if (Config_project.is_developer_mode) {
            edt_username.setText("GTF");
            edt_password.setText("123456");
        }
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
            if (!TextUtils.equals(layout_login.getText(), "登  录")) {
                resetLogin();
            }
        }
    };

    private void resetLogin() {
        layout_login.setText("登  录");
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
            //企业qq登陆
            case R.id.tv_bqq_login:
                app.startActivity(this, LoginBQQActivity.class, MainApp.ENTER_TYPE_BUTTOM, true, null);
                break;
           /* case R.id.tv_bwx_login:
                if (regToWx()) {
                    requestWeixinInfo();
                }

                break;*/

            //忘记密码

            case R.id.layout_reset_password:
                app.startActivity(this, VerifyAccountActivity_.class, MainApp.ENTER_TYPE_RIGHT, false, null);
                break;
        }
    }

    /**
     * 请求获取微信信息
     */
    private void requestWeixinInfo() {
        SendAuth.Req req = new SendAuth.Req();
        req.scope = WXUtil.WEIXIN_SCOPE;
        req.state = WXUtil.WEIXIN_STATE;
        iwxapi.sendReq(req);
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

                if (error.getKind() == RetrofitError.Kind.NETWORK) {
                    Toast("请检查您的网络连接");
                } else if (error.getKind() == RetrofitError.Kind.HTTP) {

                    switch (type) {

                        case 1:
                          //Toast(type == 1 ? "微信登录失败,请先填写账号、密码，点击登录按钮绑定微信号" : "登录失败");
                            Toast("微信登录失败,请先填写账号、密码，点击登录按钮绑定微信号");
                            break;

                        case 2:
                            try {
                                String errorStr = Utils.convertStreamToString(error.getResponse().getBody().in());
                                codes = error.getResponse().getStatus();
                                jsObj = new JSONObject(errorStr);
                                Log.d("LOG", "服务器返回信息：" + errorStr);
                                Log.d("LOG", "ERROR信息：" + jsObj.getString("error"));
                                Log.d("LOG","code:"+codes);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (codes == 500) {
                                Toast("手机号码或密码错误");
                            } else if (codes == 401) {
                                Toast("登录失败，请稍后再试");
                            }

                            break;
                    }
                }

                layout_login.setText("登录失败");
                changeColor(R.color.title_bg1, R.color.red);

            }
        });
    }

    /**
     * 微信登录
     */
    private void loginWithWx(String wxUninoId) {
        HashMap<String, Object> body = new HashMap<String, Object>();
        body.put("wxUnionId", wxUninoId);
        login(body, 1);
    }

    /**
     * 注册到微信
     */
    private boolean regToWx() {
        if (!iwxapi.isWXAppInstalled()) {
            Toast("你的手机没有安装微信，请先安装微信客户端");
            return false;
        }
        iwxapi.registerApp(WXUtil.WX_APPID);

        return true;
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }
}
