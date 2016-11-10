package com.loyo.oa.v2.activityui.contact;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.activityui.commonview.bean.NewUser;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.RoundImageView;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.db.bean.DBUser;
import com.loyo.oa.v2.point.IUser;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;
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
    TextView tv_title;
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
    String  userId;

    private DBUser user;

    private int defaultAvatar;

    /* Broadcasr */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            updateUIWithUser(user);
            getUserInfo();
        }
    };

    public void registerBroadcastReceiver(){
        IntentFilter filter = new IntentFilter("com.loyo.oa.v2.USER_EDITED");
        registerReceiver(mReceiver, filter);
    }

    public void unregisterBroadcastReceiver() {
        unregisterReceiver(mReceiver);
    }

    @AfterViews
    void initViews() {
        layout_call.setOnTouchListener(Global.GetTouch());
        layout_msg.setOnTouchListener(Global.GetTouch());
        layout_back.setOnTouchListener(Global.GetTouch());
        tv_edit.setOnTouchListener(Global.GetTouch());
        if (userId!=null && userId.equals(MainApp.user.id)) {
            tv_edit.setVisibility(View.VISIBLE);
            layout_action.setVisibility(View.GONE);
        }
        registerBroadcastReceiver();

    }

    @Override
    protected void onResume(){
        super.onResume();
        initData();

    }

    @Override
    protected void onDestroy() {
        unregisterBroadcastReceiver();
        super.onDestroy();
    }

    @Click({R.id.tv_edit, R.id.layout_back, R.id.layout_call, R.id.layout_msg})
    void onClick(final View v) {
        switch (v.getId()) {
            case R.id.layout_back:
                onBackPressed();
                break;
            case R.id.tv_edit:

                // TODO:
                Bundle b = new Bundle();
                b.putSerializable("userId", userId);
                app.startActivityForResult(this, ContactInfoEditActivity_.class, MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE, b);
                break;
            case R.id.layout_call:
                call();
                break;
            case R.id.layout_msg:
                sendSms();
                break;
            default:
                break;
        }
    }

    /**
     * 更新登录用户资料
     */
    void getUserInfo() {
        RestAdapterFactory.getInstance().build(FinalVariables.GET_PROFILE).create(IUser.class).getProfile(new RCallback<NewUser>() {
            @Override
            public void success(final NewUser theUser, final Response response) {
                user.mobile = theUser.data.mobile;
                user.birthDay = theUser.data.birthDay;
                user.weixinId = theUser.data.weixinId;
                user.avatar = theUser.data.avatar;
                user.gender = theUser.data.gender;

                OrganizationManager.shareManager().updateUser(user);

                Intent it = new Intent("com.loyo.oa.v2.USER_REFRESH");
                it.putExtra("userId", user.id);
                sendBroadcast(it);

                HttpErrorCheck.checkResponse(response);
                updateUIWithUser(user);
            }

            @Override
            public void failure(final RetrofitError error) {
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
        tv_title.setText("通讯录");
        user = OrganizationManager.shareManager().getUser(userId);

        if (null == user) {
            return;
        }


        //默认头像，头像获取
        if (null == user.avatar || user.avatar.isEmpty() || !user.avatar.contains("http")) {
            if (user.gender == 2) {
                defaultAvatar = R.drawable.icon_contact_avatar;
            } else {
                defaultAvatar = R.drawable.img_default_user;
            }
            img_title_user.setImageResource(defaultAvatar);
        } else {
            ImageLoader.getInstance().displayImage(user.avatar, img_title_user);
        }

        Utils.setContent(tv_realname, user.name);
        Utils.setContent(tv_deptname, user.shortDeptNames);

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
            Utils.setContent(tv_birthday, user.birthDay.substring(0,10));
            Utils.setContent(tv_age, age + "");
        }

    }

    public void updateUIWithUser(DBUser user){
        if (null == user) {
            return;
        }


        //默认头像，头像获取
        if (null == user.avatar || user.avatar.isEmpty() || !user.avatar.contains("http")) {
            if (user.gender == 2) {
                defaultAvatar = R.drawable.icon_contact_avatar;
            } else {
                defaultAvatar = R.drawable.img_default_user;
            }
            img_title_user.setImageResource(defaultAvatar);
        } else {
            ImageLoader.getInstance().displayImage(user.avatar, img_title_user);
        }

        Utils.setContent(tv_realname, user.name);
        Utils.setContent(tv_deptname, user.shortDeptNames);

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
            Utils.setContent(tv_birthday, user.birthDay.substring(0, 10));
            Utils.setContent(tv_age, age + "");
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != ExtraAndResult.REQUEST_CODE || resultCode != RESULT_OK) {
            return;
        }
        getUserInfo();
    }

    /**
     * 发送短信
     */
    private void sendSms() {
        if (TextUtils.isEmpty(user.mobile)) {
            Toast("联系人电话号码为空");
            return;
        }
        Utils.sendSms(ContactInfoActivity.this, user.mobile);
    }

    /**
     * 打电话
     */
    private void call() {
        if (TextUtils.isEmpty(user.mobile)) {
            Toast("联系人电话号码为空");
            return;
        }
        Utils.call(ContactInfoActivity.this, user.mobile);
    }
}
