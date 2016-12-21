package com.loyo.oa.v2.activityui.setting;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.home.fragment.MenuFragment;
import com.loyo.oa.v2.activityui.setting.api.SettingService;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBean;
import com.loyo.oa.v2.common.DialogHelp;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RegexUtil;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【更换手机号】 第二步
 * 修改手机号
 * Created by yyy on 2016/9/29
 */
public class RenewalMobileTwoActivty extends BaseActivity implements View.OnClickListener {

    private ViewGroup ll_back;
    private TextView tv_title;
    private Button btn_get_code;
    private EditText et_account, et_code;
    private ImageView iv_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renewal_mobile_two);
//        setTouchView(NO_SCROLL);
        initUI();
    }

    void initUI() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_submit = (ImageView) findViewById(R.id.iv_submit);
        tv_title.setText("更换手机号");
        iv_submit.setImageResource(R.drawable.right_submit1);
//        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_get_code = (Button) findViewById(R.id.btn_get_code);
        et_account = (EditText) findViewById(R.id.et_account);
        et_code = (EditText) findViewById(R.id.et_code);

        Global.SetTouchView(ll_back, iv_submit, btn_get_code);
        et_account.addTextChangedListener(textWatcher);
        ll_back.setOnClickListener(this);
        btn_get_code.setOnClickListener(this);
        iv_submit.setOnClickListener(this);
    }

    void getCode() {
        String mobile = et_account.getText().toString().trim();
        if (TextUtils.isEmpty(mobile)) {
            Toast("请填写手机号");
            return;
        }
        verifyPhone(mobile);
    }


    /**
     * 验证手机号
     *
     * @param tel
     */
    private void verifyPhone(final String tel) {
        showLoading("");
//        //验证手机号
//        RestAdapterFactory.getInstance().build(FinalVariables.URL_VERIFY_PHONE).create(IMobile.class).verifyPhone(tel, new RCallback<Object>() {
//            @Override
//            public void success(final Object o, final Response response) {
//                HttpErrorCheck.checkResponse("验证手机号", response);
//                //请求验证码
//                RestAdapterFactory.getInstance().build(FinalVariables.URL_GET_CODE).create(IMobile.class).getVerifyCode(tel, new RCallback<Object>() {
//                    @Override
//                    public void success(final Object o, final Response response) {
//                        HttpErrorCheck.checkResponse("请求手机验证码", response);
//                        et_account.removeCallbacks(countRunner);
//                        et_account.post(countRunner);
//                        btn_get_code.setEnabled(false);
//                        et_account.setEnabled(false);
//                    }
//
//                    @Override
//                    public void failure(final RetrofitError error) {
//                        super.failure(error);
//                        HttpErrorCheck.checkError(error);
//                    }
//                });
//
//            }
//
//            @Override
//            public void failure(final RetrofitError error) {
//                super.failure(error);
//                HttpErrorCheck.checkError(error);
//                btn_get_code.setEnabled(true);
////                if ("500".equals(error.getMessage().substring(0, 3))) {
////                    Toast("该手机号已被录入本系统,请勿重复使用!");
////                }
//            }
//        });

        SettingService.verifyPhone(tel).subscribe(new DefaultLoyoSubscriber<Object>() {
            @Override
            public void onNext(Object o) {
                SettingService.getVerifyCode(tel).subscribe(new DefaultLoyoSubscriber<Object>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        DialogHelp.cancelLoading();
                    }

                    @Override
                    public void onNext(Object o) {
                        DialogHelp.cancelLoading();
                        et_account.removeCallbacks(countRunner);
                        et_account.post(countRunner);
                        btn_get_code.setEnabled(false);
                        et_account.setEnabled(false);
                    }
                });
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                DialogHelp.cancelLoading();
                btn_get_code.setEnabled(true);
            }
        });
    }

    /**
     * 确认更改手机号
     */
    void doNext() {

        final String mobile = et_account.getText().toString();
        String code = et_code.getText().toString();
        if (TextUtils.isEmpty(mobile)) {
            Toast("请填写手机号");
            return;
        }
        if (TextUtils.isEmpty(code)) {
            Toast("请填写验证码");
            return;
        }
        showLoading("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("tel", mobile);
        map.put("code", code);
//        RestAdapterFactory.getInstance().build(Config_project.MAIN_RED_DOT).create(IMobile.class).
//                modifyMobile(map, new RCallback<BaseBean>() {
//                    @Override
//                    public void success(final BaseBean o, final Response response) {
//                        HttpErrorCheck.checkResponse(response);
//                        if(o.errcode==0){
//                            MenuFragment.callback.onExit(RenewalMobileTwoActivty.this);
//                        }
////                Intent mIntent = new Intent();
////                mIntent.putExtra("phone", mobile);
////                app.finishActivity(RenewalMobileTwoActivty.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, mIntent);
////                finish();
//                    }
//
//                    @Override
//                    public void failure(final RetrofitError error) {
//                        super.failure(error);
//                        HttpErrorCheck.checkError(error);
//                    }
//                });
        SettingService.modifyMobile(map).subscribe(new DefaultLoyoSubscriber<BaseBean>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                DialogHelp.cancelLoading();
            }

            @Override
            public void onNext(BaseBean o) {
                DialogHelp.cancelLoading();
                if(o.errcode==0){
                    MenuFragment.callback.onExit(RenewalMobileTwoActivty.this);
                }
            }
        });
    }

    private Runnable countRunner = new Runnable() {
        private int time = 60;

        @Override
        public void run() {
            if (time == 0) {
                time = 60;
                et_account.setEnabled(true);
                btn_get_code.setEnabled(true);
                btn_get_code.setText("获取验证码");
                return;
            }
            btn_get_code.setText("重新获取(" + time + ")");
            time--;
            tv_title.postDelayed(this, 1000);
        }
    };

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {

        }

        @Override
        public void onTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {

        }

        @Override
        public void afterTextChanged(final Editable editable) {
            if (RegexUtil.regexk(editable.toString().trim(), RegexUtil.StringType.MOBILEL)) {
                btn_get_code.setEnabled(true);
                btn_get_code.setBackgroundResource(R.drawable.round_bg_shpe);
                btn_get_code.setTextColor(Color.parseColor("#ffffff"));
            } else {
                btn_get_code.setEnabled(false);
                btn_get_code.setBackgroundResource(R.drawable.round_bg_shpe2);
                btn_get_code.setTextColor(Color.parseColor("#999999"));
                if (editable.length() == 11) {
                    Toast("请输入正确的手机号码");
                }
            }
        }
    };

    @Override
    public void onBackPressed() {
        app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, null);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            /*返回*/
            case R.id.ll_back:
                onBackPressed();
                break;
            /*获取验证码*/
            case R.id.btn_get_code:
                getCode();
                break;
            /*提交*/
            case R.id.iv_submit:
                doNext();
                break;
        }
    }
}
