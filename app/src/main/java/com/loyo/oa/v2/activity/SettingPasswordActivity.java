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
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IUser;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

@EActivity(R.layout.activity_setting_setpassword)
public class SettingPasswordActivity extends BaseActivity {
    @ViewById ViewGroup img_title_left;
    @ViewById TextView tv_title_1;
    @ViewById Button btn_submit;
    @ViewById EditText et_old_password, et_new_password, et_confirm_new_password;

    @AfterViews
    void initUI() {
        setTouchView(NO_SCROLL);
        tv_title_1.setText("修改密码");
        Global.SetTouchView(img_title_left, btn_submit);
    }

    @Click(R.id.img_title_left)
    void back() {
        onBackPressed();
    }

    @Click(R.id.btn_submit)
    void submit() {
        String oldPassword = getText(et_old_password);
        String newPassword = getText(et_new_password);
        String confirmNewPassword = getText(et_confirm_new_password);

        if (TextUtils.isEmpty(oldPassword)) {
            Toast("原密码不能为空");
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            Toast("新密码不能为空");
            return;
        }
        if (TextUtils.isEmpty(confirmNewPassword)) {
            Toast("确认新码不能为空");
            return;
        }

        if (!TextUtils.equals(newPassword, confirmNewPassword)) {
            Toast("两次输入的新密码不一致");
            return;
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("oldpasswd", oldPassword);
        map.put("newpasswd", confirmNewPassword);
        RestAdapterFactory.getInstance().build(FinalVariables.URL_UPDATE_PASSWORD).create(IUser.class).updatePassword(map, new RCallback<Object>() {
            @Override
            public void success(Object o, Response response) {
                HttpErrorCheck.checkResponse(response);
                Toast("修改密码成功");
                onBackPressed();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast(error.getBody().toString());
                super.failure(error);
            }
        });
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

    @Override
    public void onBackPressed() {
        app.finishActivity(SettingPasswordActivity.this, MainApp.ENTER_TYPE_LEFT, 0, null);
    }
}
