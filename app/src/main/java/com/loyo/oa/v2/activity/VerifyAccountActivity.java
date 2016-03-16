package com.loyo.oa.v2.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.setting.ResetPasswordActivity_;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IMain;
import com.loyo.oa.v2.point.IMobile;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RegexUtil;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * com.loyo.oa.v2.activity
 * 描述 :找回密码验证账号界面
 * 作者 : ykb
 * 时间 : 15/11/10.
 */
@EActivity(R.layout.activity_verify_account)
public class VerifyAccountActivity extends BaseActivity {
    @ViewById ViewGroup img_title_left;
    @ViewById TextView tv_title_1;
    @ViewById Button btn_confirm, btn_get_code;
    @ViewById EditText et_account, et_code;

    @AfterViews
    void initViews() {
        setTouchView(NO_SCROLL);
        tv_title_1.setText("找回密码");
        Global.SetTouchView(img_title_left, btn_confirm, btn_get_code);
        et_account.addTextChangedListener(textWatcher);
    }

    @Click(R.id.img_title_left)
    void back() {
        onBackPressed();
    }

    @Click(R.id.btn_get_code)
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
        RestAdapterFactory.getInstance().build(FinalVariables.URL_VERIFY_PHONE).create(IMain.class).verifyPhone(tel, new RCallback<Object>() {
            @Override
            public void success(final Object o,final Response response) {
                et_account.removeCallbacks(countRunner);
                et_account.post(countRunner);
                btn_get_code.setEnabled(false);
                et_account.setEnabled(false);
                Toast("发送验证码成功");

            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
                btn_get_code.setEnabled(true);
            }
        });
    }

    /**
     * 密码找回确定请求
     */
    @Click(R.id.btn_confirm)
    void doNext() {
        final String mobile = et_account.getText().toString().trim();
        String code = et_code.getText().toString().trim();
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
        RestAdapterFactory.getInstance().build(FinalVariables.URL_VERIFY_CODE).create(IMobile.class).verifyCode(map, new RCallback<Object>() {
            @Override
            public void success(final Object o, final Response response) {
                et_account.removeCallbacks(countRunner);
                Bundle bundle = new Bundle();
                bundle.putString("tel", mobile);
                app.startActivity(VerifyAccountActivity.this, ResetPasswordActivity_.class, MainApp.ENTER_TYPE_RIGHT, true, bundle);
            }

            @Override
            public void failure(final RetrofitError error) {
                HttpErrorCheck.checkError(error);
                super.failure(error);
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

}
