package com.loyo.oa.v2.activityui.setting;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IMain;
import com.loyo.oa.v2.point.IMobile;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RegexUtil;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.R.attr.action;

/**
 * 更改 手机号【设置账号】
 * Created xnq 16/1/12./
 */
public class EditUserMobileActivity extends BaseActivity {
    public static final int ACTION_BINDING = 120;//绑定手机号
    public static final int ACTION_RENEWAL = 130;//更换手机号
    private LinearLayout img_title_left, ll_binding, ll_renewal;
    private TextView tv_title_1, tv_renewal_cellnumber;
    private Button bt_verificationCode, btn_complete, bt_renewal;
    private EditText et_mobile, et_code, et_pwd;
    private CheckBox cb_showHide;
    private String verificatioNumber, pwd, mobile;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_mobile);
        initViews();
    }

    void initViews() {

        img_title_left = (LinearLayout) findViewById(R.id.img_title_left);
        ll_binding = (LinearLayout) findViewById(R.id.ll_binding);
        ll_renewal = (LinearLayout) findViewById(R.id.ll_renewal);
        img_title_left.setOnClickListener(click);
        tv_title_1 = (TextView) findViewById(R.id.tv_title_1);
        tv_title_1.setText("绑定手机号");
        bt_verificationCode = (Button) findViewById(R.id.bt_verificationCode);
        bt_verificationCode.setOnClickListener(click);
        btn_complete = (Button) findViewById(R.id.btn_complete);
        btn_complete.setOnClickListener(click);
        et_mobile = (EditText) findViewById(R.id.et_mobile);
        et_mobile.addTextChangedListener(textWatcher);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        et_code = (EditText) findViewById(R.id.et_code);
        cb_showHide = (CheckBox) findViewById(R.id.cb_showHide);
        tv_renewal_cellnumber = (TextView) findViewById(R.id.tv_renewal_cellnumber);
        bt_renewal = (Button) findViewById(R.id.bt_renewal);
        bt_renewal.setOnClickListener(click);
        cb_showHide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                et_pwd.setTransformationMethod(isChecked ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
            }
        });
        setTouchView(NO_SCROLL);
        Global.SetTouchView(img_title_left, bt_verificationCode, btn_complete, bt_renewal);

    }

    @Override
    protected void onResume() {
        super.onResume();
        activityAction();
    }

    private void activityAction() {
        int action = getIntent().getIntExtra(ExtraAndResult.SEND_ACTION, 0);
        String mobile = getIntent().getStringExtra(ExtraAndResult.EXTRA_DATA);
        if (ACTION_BINDING == action) {
            ll_binding.setVisibility(View.VISIBLE);
            showInputKeyboard(et_mobile);
        } else if (ACTION_RENEWAL == action) {
            ll_renewal.setVisibility(View.VISIBLE);
            tv_renewal_cellnumber.setText("绑定手机号为 : " + mobile);
        }
    }

    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch (v.getId()) {
                case R.id.img_title_left:
                    onBackPressed();
                    break;
                case R.id.bt_verificationCode:
                    mobile = et_mobile.getText().toString().trim();
                    if (TextUtils.isEmpty(mobile)) {
                        Toast("请填写手机号");
                        return;
                    }
                    verifyPhone(mobile);
                    break;
                case R.id.btn_complete:
                    complete();
                    break;
                case R.id.bt_renewal://更换手机号
                    app.startActivity(EditUserMobileActivity.this, RenewalMobileOneActivty.class, MainApp.ENTER_TYPE_RIGHT, false, null);
                    break;

            }
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
                bt_verificationCode.setEnabled(true);
                bt_verificationCode.setBackgroundResource(R.drawable.round_bg_shpe);//getResources().getColor(R.color.title_bg1)
                bt_verificationCode.setTextColor(Color.parseColor("#ffffff"));
            } else {
                bt_verificationCode.setEnabled(false);
                bt_verificationCode.setBackgroundResource(R.drawable.round_bg_shpe2);
                bt_verificationCode.setTextColor(Color.parseColor("#999999"));
                if (editable.length() == 11) {
                    Toast("请输入正确的手机号码");
                }
            }
        }
    };

    /**
     * 验证手机号
     *
     * @param tel
     */
    private void verifyPhone(final String tel) {
        RestAdapterFactory.getInstance().build(Config_project.GET_VERIFICATION_CODE).create(IMain.class).getVerificationCode(tel, new RCallback<Object>() {
            @Override
            public void success(final Object o, final Response response) {
                et_mobile.removeCallbacks(countRunner);
                et_mobile.post(countRunner);
                bt_verificationCode.setEnabled(false);
                et_mobile.setEnabled(false);
                Toast("发送验证码成功");

            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                bt_verificationCode.setEnabled(true);
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
                et_mobile.setEnabled(true);
                bt_verificationCode.setEnabled(true);
                bt_verificationCode.setText("获取验证码");
                return;
            }
            bt_verificationCode.setText("重新获取(" + time + ")");
            time--;
            tv_title_1.postDelayed(this, 1000);
        }
    };

    /**
     * 完成 绑定 QQ于手机
     */
    private void complete() {
        verificatioNumber = et_code.getText().toString();
        pwd = et_pwd.getText().toString();
        if (TextUtils.isEmpty(verificatioNumber)) {
            Toast("验证码不能为空");
            return;
        } else if (verificatioNumber.length() != 6) {
            Toast("验证码错误");
            return;
        } else if (TextUtils.isEmpty(pwd)) {
            Toast("密码不能为空");
            return;
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("tel", mobile);
        map.put("code", verificatioNumber);
        map.put("password", pwd);
        RestAdapterFactory.getInstance().build(Config_project.BIND_MOBLIE).create(IMobile.class).bindMobile(map, new RCallback<Object>() {
            @Override
            public void success(final Object o, final Response response) {
                HttpErrorCheck.checkResponse("绑定手机号码", response);
                Toast("绑定成功");
                onBackPressed();
//                et_account.removeCallbacks(countRunner);
//                Bundle bundle = new Bundle();
//                bundle.putString("tel", mobile);
//                app.startActivity(VerifyAccountActivity.this, ResetPasswordActivity_.class, MainApp.ENTER_TYPE_RIGHT, true, bundle);
            }

            @Override
            public void failure(final RetrofitError error) {
                HttpErrorCheck.checkError(error);
                super.failure(error);
            }
        });


    }
}
