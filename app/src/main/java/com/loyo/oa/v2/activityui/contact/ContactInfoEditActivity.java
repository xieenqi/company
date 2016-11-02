package com.loyo.oa.v2.activityui.contact;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.contact.presenter.ContactInfoEditPresenter;
import com.loyo.oa.v2.activityui.contact.presenter.impl.ContactInfoEditPresenterImpl;
import com.loyo.oa.v2.activityui.contact.viewcontrol.ContactInfoView;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.RoundImageView;
import com.loyo.oa.v2.customview.multi_image_selector.MultiImageSelectorActivity;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.db.bean.DBUser;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.RegexUtil;
import com.loyo.oa.v2.tool.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

import retrofit.client.Response;
import retrofit.http.HEAD;

/**
 * 【编辑个人信息】
 * Restruture by yyy on 16/10/12
 */

@EActivity(R.layout.activity_contactinfo_edit)
public class ContactInfoEditActivity extends BaseActivity implements ContactInfoView {

    private final int REQUEST_IMAGE = 100;

    @ViewById
    ViewGroup layout_back;
    @ViewById
    ImageView iv_submit;
    @ViewById
    TextView tv_title;
    @ViewById
    ViewGroup layout_set_avartar;
    @ViewById
    RoundImageView img_title_user;
    @ViewById
    ViewGroup ll_sex;
    @ViewById
    TextView tv_sex;
    @ViewById
    TextView tv_mobile;
    @ViewById
    ViewGroup layout_weixin;
    @ViewById
    TextView et_weixin;
    //    @ViewById
//    RadioButton sex_famale;
    @ViewById
    ViewGroup layout_birthday;
    @ViewById
    TextView tv_birthday;
    @ViewById
    TextView tv_age;
    @ViewById
    TextView tv_departments;
    @ViewById
    TextView tv_positions;
    @ViewById
    TextView name_title_user;
    @Extra
    String userId;

    private DBUser user;


    private int sex;
    private String path = null;
    private MHandler mHandler = new MHandler(this);
    private String resultPhone;
    private String birthStr;
    private int age;

    private ContactInfoEditPresenter mPresenter;

    private class MHandler extends Handler {
        private WeakReference<ContactInfoEditActivity> mActivity;

        private MHandler(final ContactInfoEditActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);

            if (msg.what == 0x01) {
                Utils.setContent(tv_birthday, birthStr);
                Utils.setContent(tv_age, age + "");
            }

            if (msg.what == 0x02) {
                tv_mobile.setText(resultPhone);
            }
        }
    }

    @AfterViews
    void initViews() {
        setTouchView(-1);
        mPresenter = new ContactInfoEditPresenterImpl(this, mContext, ContactInfoEditActivity.this);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText("个人信息");
        iv_submit.setVisibility(View.VISIBLE);
        layout_back.setOnTouchListener(Global.GetTouch());
        iv_submit.setOnTouchListener(Global.GetTouch());
        ll_sex.setOnTouchListener(Global.GetTouch());
        layout_set_avartar.setOnTouchListener(Global.GetTouch());
        layout_weixin.setOnTouchListener(Global.GetTouch());
        layout_birthday.setOnTouchListener(Global.GetTouch());
        initData();
    }

    @Click({R.id.layout_back, R.id.layout_set_avartar, R.id.ll_sex, R.id.iv_submit, R.id.layout_birthday, R.id.layout_weixin})
    void onClick(final View v) {
        switch (v.getId()) {

            /*返回*/
            case R.id.layout_back:
                onBackPressed();
                break;

            /*设置头像*/
            case R.id.layout_set_avartar:
                Intent intent = new Intent(this, MultiImageSelectorActivity.class);
                mPresenter.setHeadImage(ContactInfoEditActivity.this, intent, REQUEST_IMAGE);
                break;

            /*生日设置*/
            case R.id.layout_birthday:
                mPresenter.pickDate(mHandler);
                break;

            /*提交*/
            case R.id.iv_submit:
                //关闭键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                if (imm.isActive()) {
//                    imm.hideSoftInputFromWindow(et_weixin.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//                }
                updateProfile();
                break;
            case R.id.layout_weixin:
                Toast("修改微信");
                break;
//            /*修改电话*/
//            case R.id.layout_mobile:
//                app.startActivityForResult(this, ResePhoneActivity.class,MainApp.ENTER_TYPE_RIGHT,ExtraAndResult.MSG_SEND,new Bundle());
//                break;
            /* 性别设置 */
            case R.id.ll_sex:
                Bundle sexBundle = new Bundle();
                sexBundle.putString("sex", tv_sex.getText().toString());
                app.startActivityForResult(this, SexSelectActivity.class, MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.MSG_SEND, sexBundle);
                break;
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        user = OrganizationManager.shareManager().getUser(userId);
        if (null == user) {
            return;
        }
        int defaultAvatao;
        if (null == user.avatar || user.avatar.isEmpty() || !user.avatar.contains("http")) {
            if (user.gender == 2) {
                defaultAvatao = R.drawable.icon_contact_avatar;
            } else {
                defaultAvatao = R.drawable.img_default_user;
            }
            img_title_user.setImageResource(defaultAvatao);
        } else {
            ImageLoader.getInstance().displayImage(user.avatar, img_title_user);
        }
        path = user.avatar;
        Utils.setContent(tv_mobile, user.mobile);
        Utils.setContent(et_weixin, user.weixinId);
        Utils.setContent(name_title_user, MainApp.user.getRealname());
        setSex(user.gender);
        if (!TextUtils.isEmpty(user.birthDay)) {
            int age = Utils.getAge(user.birthDay.substring(0, 4));
            if (age >= 150) {
                return;
            }
            Utils.setContent(tv_birthday, user.birthDay);
            Utils.setContent(tv_age, Utils.getAge(user.birthDay.substring(0, 4)) + "");
        }
        tv_departments.setText(mPresenter.getDepartments(user.shortDeptNames));
        tv_positions.setText(mPresenter.getPositions(user.shortDeptNames));
    }

    private void setSex(int sex) {
        if (sex == 2) {
            tv_sex.setText("女");
        } else if (sex == 1) {
            tv_sex.setText("男");
        }
    }

    /**
     * 编辑个人信息
     */
    private void updateProfile() {

//        if (!et_weixin.getText().toString().isEmpty()) {
//            if (!RegexUtil.regexk(et_weixin.getText().toString(), RegexUtil.StringType.WX)) {
//                Toast("微信号码不正确");
//                return;
//            }
//        }

        showLoading("正在提交");
        mPresenter.updateProfile(user.id, tv_mobile.getText().toString(),
                sex, tv_birthday.getText().toString(),
                et_weixin.getText().toString(), path);

    }


    /**
     * 更新资料成功处理
     */
    @Override
    public void updateProfileEmbl() {
        Toast("修改个人信息成功");
//        Intent mIntent = new Intent();
//        mIntent.putExtra(ExtraAndResult.STR_SUPER_ID, ExtraAndResult.TYPE_SHOW_DEPT_USER);
//        app.finishActivity(ContactInfoEditActivity.this, MainApp.ENTER_TYPE_ZOOM_IN, RESULT_OK, mIntent);
        if (user.id != null) {
            user.mobile = tv_mobile.getText().toString();
            user.birthDay = tv_birthday.getText().toString();
            user.weixinId = et_weixin.getText().toString();
            user.avatar = path;
            user.gender = sex;
            OrganizationManager.shareManager().updateUser(user);

            Intent it = new Intent("com.loyo.oa.v2.USER_EDITED");
            it.putExtra("userId", user.id);
            sendBroadcast(it);
        }
        setSex(sex);
    }

    /**
     * 设置生日
     */
    @Override
    public void setBrithday(Handler mHandler, String birthStr) {
        this.birthStr = birthStr;
        mHandler.sendEmptyMessage(0x01);
    }

    /**
     * 弹出框响应
     */
    @Override
    public void leaveDialogEmbl() {
        updateProfile();
    }

    /**
     * 设置头像
     */
    @Override
    public void setHeadImage(String path) {
        this.path = path;
    }

    @Override
    public void showProgress(String msg) {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showMsg(String message) {

    }

    /**
     * 退出事件
     */
    @Override
    public void onBackPressed() {
        if (!mPresenter.isDataChange(tv_mobile, tv_birthday, et_weixin, user, sex)) {
            mPresenter.showLeaveDialog(sweetAlertDialogView);
        } else {
            app.finishActivity(ContactInfoEditActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, null);
        }
    }

    /**
     * Result回调
     */
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (null == data) {
            return;
        }
        switch (requestCode) {
            /* 设置头像 */
            case REQUEST_IMAGE:
                List<String> mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                mPresenter.upload(mSelectPath, img_title_user);
                updateProfile();
                break;
            case ExtraAndResult.MSG_SEND:
                sex = data.getIntExtra("sex", -1);
                updateProfile();
                break;

        }

//        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
//            List<String> mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
//            mPresenter.upload(mSelectPath, uuid, img_title_user);
//        } else if (requestCode == ExtraAndResult.MSG_SEND) {
//            resultPhone = data.getStringExtra("phone");
//            mHandler.sendEmptyMessage(0x02);
//        }
    }
}
