package com.loyo.oa.v2.activity.contact;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IUser;
import com.loyo.oa.v2.service.InitDataService_;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.customview.RoundImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * com.loyo.oa.v2.activity
 * 描述 :联系人信息详情页
 * 作者 : ykb
 * 时间 : 15/8/25.
 */
@EActivity(R.layout.activity_contactinfo)
public class ContactInfoActivity extends BaseActivity {

    @ViewById
    ViewGroup layout_back;
    @ViewById
    RoundImageView img_title_user;
    @ViewById
    TextView tv_edit;
    @ViewById
    TextView tv_realname;
    @ViewById
    TextView tv_deptname;
    @ViewById
    TextView tv_phone;
    @ViewById
    TextView tv_qq;
    @ViewById
    TextView tv_weixin;
    @ViewById
    TextView tv_sex;
    @ViewById
    TextView tv_birthday;
    @ViewById
    TextView tv_age;
    @ViewById
    ViewGroup layout_call;
    @ViewById
    ViewGroup layout_msg;
    @ViewById
    ViewGroup layout_action;
    @Extra
    User user;

    @AfterViews
    void initViews() {
        layout_call.setOnTouchListener(Global.GetTouch());
        layout_msg.setOnTouchListener(Global.GetTouch());
        layout_back.setOnTouchListener(Global.GetTouch());
        tv_edit.setOnTouchListener(Global.GetTouch());
        if (user.equals(MainApp.user)) {
            tv_edit.setVisibility(View.VISIBLE);
            layout_action.setVisibility(View.GONE);
        }
        initData();
    }

    @Click({R.id.tv_edit, R.id.layout_back, R.id.layout_call, R.id.layout_msg})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_back:
                app.finishActivity(this, MainApp.ENTER_TYPE_ZOOM_IN, 0, null);
                break;
            case R.id.tv_edit:
                Bundle b = new Bundle();
                b.putSerializable("user", user);
                app.startActivityForResult(this, ContactInfoEditActivity_.class, MainApp.ENTER_TYPE_BUTTOM, ExtraAndResult.request_Code, b);
                break;
            case R.id.layout_call:
                call();
                break;
            case R.id.layout_msg:
                sendSms();
                break;
        }
    }

    /**
     * 更新登录用户资料
     */
    void getUserInfo() {

        RestAdapterFactory.getInstance().build(FinalVariables.GET_PROFILE).create(IUser.class).getProfile(new RCallback<User>() {
            @Override
            public void success(User users, Response response) {

                user = users;
                HttpErrorCheck.checkResponse(response);
                initData();

            }

            @Override
            public void failure(RetrofitError error) {
                super.failure(error);
                Toast("获取用户资料失败");
                HttpErrorCheck.checkError(error);
                finish();
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (null == user)
            return;
        ImageLoader.getInstance().displayImage(user.getAvatar(), img_title_user);
        LogUtil.dll("头像地址:"+user.getAvatar());
        Utils.setContent(tv_realname, user.getRealname());
        String dept = user.depts.get(0).getShortDept().getName() + " - " + user.getRealname();
        Utils.setContent(tv_deptname, dept);

        Utils.setContent(tv_phone, user.mobile);
        String gender = "";
        if (user.gender == 2)
            gender = "女";
        else if (user.gender == 1)
            gender = "男";
        Utils.setContent(tv_sex, gender);
        Utils.setContent(tv_weixin, user.weixinId);
        if (!TextUtils.isEmpty(user.birthDay)) {
            int age = Utils.getAge(user.birthDay.substring(0, 4));
            if (age >= 150) {
                return;
            }
            Utils.setContent(tv_birthday, user.birthDay);
            Utils.setContent(tv_age, age + "");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != ExtraAndResult.request_Code || resultCode != RESULT_OK) {
            return;
        }
        getUserInfo();
    }

    /**
     * 更新组织架构
     */
    private void updateOrganization() {
        InitDataService_.intent(this).start();
    }

    /**
     * 发送短信
     */
    private void sendSms() {
        if (TextUtils.isEmpty(user.mobile)) {
            Toast("联系人电话号码为空");
            return;
        }
        Uri uri = Uri.parse("smsto:" + user.mobile);
        Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
        sendIntent.putExtra("sms_body", "");
        startActivity(sendIntent);
    }

    /**
     * 打电话
     */
    private void call() {
        if (TextUtils.isEmpty(user.mobile)) {
            Toast("联系人电话号码为空");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + user.mobile));
        startActivity(intent);
    }
}
