package com.loyo.oa.v2.activityui.contact;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.photo.PhotoPicker;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.contact.presenter.ContactInfoEditPresenter;
import com.loyo.oa.v2.activityui.contact.presenter.impl.ContactInfoEditPresenterImpl;
import com.loyo.oa.v2.activityui.contact.viewcontrol.ContactInfoView;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.RoundImageView;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.db.bean.DBUser;
import com.loyo.oa.v2.service.InitDataService_;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 【编辑个人信息】
 * Restruture by yyy on 16/10/12
 */

@EActivity(R.layout.activity_contactinfo_edit)
public class ContactInfoEditActivity extends BaseActivity implements ContactInfoView {

    private final int REQUEST_IMAGE = 111;

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
    private String wechat;
    private String birthStr;
//    private int age;

    private ContactInfoEditPresenterImpl mPresenter;

    private class MHandler extends Handler {
        private WeakReference<ContactInfoEditActivity> mActivity;

        private MHandler(final ContactInfoEditActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);

            if (msg.what == 0x01) {
                updateProfile();
//                Utils.setContent(tv_age, age + "");
            }
//            if (msg.what == 0x02) {
//                tv_mobile.setText(resultPhone);
//            }

        }
    }

    @AfterViews
    void initViews() {
//        setTouchView(-1);
        mPresenter = new ContactInfoEditPresenterImpl(this, mContext, ContactInfoEditActivity.this);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText("个人信息");
        iv_submit.setVisibility(View.VISIBLE);
        Global.SetTouchView(layout_back, iv_submit, ll_sex, layout_set_avartar, layout_weixin, layout_birthday);
        iv_submit.setVisibility(View.GONE);
        initData();
    }

    @Click({R.id.layout_back, R.id.layout_set_avartar, R.id.ll_sex, R.id.layout_birthday, R.id.layout_weixin})
    void onClick(final View v) {
        switch (v.getId()) {

            /*返回*/
            case R.id.layout_back:
                onBackPressed();
                break;
            /*设置头像*/
            case R.id.layout_set_avartar:
                mPresenter.setHeadImage(ContactInfoEditActivity.this, null, 0);
                break;
            /*生日设置*/
            case R.id.layout_birthday:
                mPresenter.pickDate(mHandler);
                break;
            case R.id.layout_weixin:
                Bundle wechatBundle = new Bundle();
                wechatBundle.putString("wechat", et_weixin.getText().toString());
                app.startActivityForResult(this, AlterWechatActivity.class, MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE, wechatBundle);
                break;
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

//        StringBuffer deptBuffer = new StringBuffer();
//        Iterator<DBDepartment> iterator=user.depts.iterator();
//        while(iterator.hasNext()){
//            deptBuffer.append(iterator.next().name+" ");
//        }

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

//        Utils.setContent(tv_departments, TextUtils.isEmpty(deptBuffer.toString()) ? " " :  deptBuffer.toString());
//        Utils.setContent(tv_positions,  user.shortDeptNames);
//
//        String[] oneSql;
//        String[] twoSql;
//        StringBuffer buffer = new StringBuffer();
//
//        /*截取职位信息*/
//        if(null != user.shortDeptNames && !TextUtils.isEmpty(user.shortDeptNames)){
//            if(user.shortDeptNames.contains(",")){
//                oneSql = user.shortDeptNames.split(",");
//                for(int i = 0;i<oneSql.length;i++){
//                    if(oneSql[i].contains("|")){
//                        twoSql = oneSql[i].split("\\|");
//                        for(int k = 0;k<twoSql.length;k++){
//                        }
//                        buffer.append(twoSql[1]+" ");
//                    }
//                }
//            }else{
//                twoSql = user.shortDeptNames.split("\\|");
//                buffer.append(twoSql[1]+" ");
//            }
//        }
//
//        path = user.avatar;
//        Utils.setContent(tv_departments, TextUtils.isEmpty(deptBuffer.toString()) ? " " :  deptBuffer.toString());
//        Utils.setContent(tv_positions,  buffer.toString());
        Utils.setContent(tv_mobile, user.mobile);
        Utils.setContent(et_weixin, user.weixinId);
        wechat = user.weixinId;
        Utils.setContent(name_title_user, MainApp.user.getRealname());
        setSex(user.gender);
        if (!TextUtils.isEmpty(user.birthDay)) {
            int age = Utils.getAge(user.birthDay.substring(0, 4));
            if (age >= 150) {
                return;
            }
            Utils.setContent(tv_birthday, user.birthDay);
            birthStr = user.birthDay;
            Utils.setContent(tv_age, Utils.getAge(user.birthDay.substring(0, 4)) + "");
        }
        tv_departments.setText(mPresenter.getDepartments(user.shortDeptNames));
        tv_positions.setText(mPresenter.getPositions(user.shortDeptNames));
    }

    private void setSex(int sex) {
        this.sex = sex;
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
//        showLoading("正在提交");
        mPresenter.updateProfile(user.id,
                tv_mobile.getText().toString(), sex, birthStr, wechat, path);
    }


    /**
     * 更新资料成功处理
     */
    @Override
    public void updateProfileEmbl() {
        setSex(sex);
        et_weixin.setText(wechat);
        Utils.setContent(tv_birthday, birthStr);
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
        Toast("修改个人信息成功");
        InitDataService_.intent(ContactInfoEditActivity.this).start();
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
        updateProfile();
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
        Intent mIntent = new Intent();
        mIntent.putExtra(ExtraAndResult.STR_SUPER_ID, ExtraAndResult.TYPE_SHOW_DEPT_USER);
        app.finishActivity(ContactInfoEditActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, mIntent);
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
            case PhotoPicker.REQUEST_CODE:
                if (data != null) {
                    List<String> mSelectPath = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    if (mSelectPath.size() >0) {
                        mPresenter.upload(mSelectPath, img_title_user);
                    }
                }
                break;
            /* 设置性别 */
            case ExtraAndResult.MSG_SEND:
                sex = data.getIntExtra("sex", -1);
                updateProfile();
                break;
            case ExtraAndResult.REQUEST_CODE:
                wechat = data.getStringExtra("wechat");
                updateProfile();
                break;
        }
    }
}
