package com.loyo.oa.v2.activityui.setting;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IMobile;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RegexUtil;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import org.androidannotations.annotations.Click;

import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 修改手机号
 * Created by yyy on 2016/9/29
 */
public class ResePhoneActivity extends BaseActivity implements View.OnClickListener {

    private ViewGroup img_title_left;
    private TextView tv_title_1;
    private Button btn_confirm, btn_get_code;
    private EditText et_account, et_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_phone);
        setTouchView(NO_SCROLL);
        initUI();
    }

    void initUI() {
        img_title_left = (LinearLayout) findViewById(R.id.img_title_left);
        tv_title_1 = (TextView) findViewById(R.id.tv_title_1);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_get_code = (Button) findViewById(R.id.btn_get_code);
        et_account = (EditText) findViewById(R.id.et_account);
        et_code = (EditText) findViewById(R.id.et_code);

        Global.SetTouchView(img_title_left, btn_confirm, btn_get_code);
        et_account.addTextChangedListener(textWatcher);
        tv_title_1.setText("修改手机号");
        img_title_left.setOnClickListener(this);
        btn_get_code.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
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

        //验证手机号
        RestAdapterFactory.getInstance().build(FinalVariables.URL_VERIFY_PHONE).create(IMobile.class).verifyPhone(tel, new RCallback<Object>() {
            @Override
            public void success(final Object o, final Response response) {
                HttpErrorCheck.checkResponse("验证手机号", response);
                //请求验证码
                RestAdapterFactory.getInstance().build(FinalVariables.URL_GET_CODE).create(IMobile.class).getVerifyCode(tel, new RCallback<Object>() {
                    @Override
                    public void success(final Object o, final Response response) {
                        HttpErrorCheck.checkResponse("请求手机验证码", response);
                        et_account.removeCallbacks(countRunner);
                        et_account.post(countRunner);
                        btn_get_code.setEnabled(false);
                        et_account.setEnabled(false);
                    }

                    @Override
                    public void failure(final RetrofitError error) {
                        super.failure(error);
                        HttpErrorCheck.checkError(error);
                    }
                });
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
                btn_get_code.setEnabled(true);
                if ("500".equals(error.getMessage().substring(0, 3))) {
                    Toast("该手机号已被录入本系统,请勿重复使用!");
                }
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
        HashMap<String, Object> map = new HashMap<>();
        map.put("tel", mobile);
        map.put("code", code);
        app.getRestAdapter(1).create(IMobile.class).modifyMobile(map, new RCallback<Object>() {
            @Override
            public void success(final Object o, final Response response) {
                HttpErrorCheck.checkResponse(response);
                Toast("修改手机号码成功");
                Intent mIntent = new Intent();
                mIntent.putExtra("phone", mobile);
                app.finishActivity(ResePhoneActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, mIntent);
                finish();
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
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
            tv_title_1.postDelayed(this, 1000);
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
                btn_get_code.setBackgroundResource(R.drawable.round_bg_shpe);//getResources().getColor(R.color.title_bg1)
            } else {
                btn_get_code.setEnabled(false);
                btn_get_code.setBackgroundResource(R.drawable.round_bg_shpe2);
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
            case R.id.img_title_left:
                onBackPressed();
                break;
            /*获取验证码*/
            case R.id.btn_get_code:
                getCode();
                break;
            /*提交*/
            case R.id.btn_confirm:
                doNext();
                break;
        }
    }
}
