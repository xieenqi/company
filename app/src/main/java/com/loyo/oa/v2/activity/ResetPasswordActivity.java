package com.loyo.oa.v2.activity;

import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.point.IMain;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * com.loyo.oa.v2.activity
 * 描述 :重设密码界面
 * 作者 : ykb
 * 时间 : 15/11/10.
 */
@EActivity(R.layout.activity_reset_password)
public class ResetPasswordActivity extends BaseActivity {
    @ViewById ViewGroup img_title_left;
    @ViewById TextView tv_title_1;
    @ViewById Button btn_submit;
    @ViewById EditText et_new_password, et_confirm_new_password;
    @Extra String tel;

    @AfterViews
    void initUI() {
        setTouchView(NO_SCROLL);
        tv_title_1.setText("重置密码");
        Global.SetTouchView(img_title_left, btn_submit);
    }

    @Click(R.id.img_title_left)
    void back() {
        onBackPressed();
    }

    @Click(R.id.btn_submit)
    void submit() {
        String newPassword = getText(et_new_password);
        String confirmNewPassword = getText(et_confirm_new_password);

        if (TextUtils.isEmpty(newPassword)) {
            Toast("新密码不能为空");
            return;
        }
        if (TextUtils.isEmpty(confirmNewPassword)) {
            Toast("确认新码不能为空");
            return;
        }

        if(!TextUtils.equals(newPassword,confirmNewPassword)){
            Toast("两次输入的新密码不一致");
            return;
        }

        HashMap<String,Object> map=new HashMap<>();
        map.put("tel",tel);
        map.put("password",confirmNewPassword);
        RestAdapterFactory.getInstance().build(FinalVariables.URL_RESET_PASSWORD).create(IMain.class).resetPassword(map, new RCallback<Object>() {
            @Override
            public void success(Object o, Response response) {
                Toast("重置密码成功");
                onBackPressed();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast("重置密码失败");
                super.failure(error);
            }
        });
    }

    @Override
    public void onBackPressed() {
        app.finishActivity(this, MainApp.ENTER_TYPE_LEFT,RESULT_CANCELED,null);
    }

    /**
     * 获取edittext里的内容
     *
     * @param editText
     * @return
     */
    private String getText(EditText editText) {
        return editText.getText().toString().trim();
    }

}
