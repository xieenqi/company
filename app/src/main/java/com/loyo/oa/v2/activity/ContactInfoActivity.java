package com.loyo.oa.v2.activity;

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
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.service.InitDataService_;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.customview.RoundImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

/**
 * com.loyo.oa.v2.activity
 * 描述 :联系人信息详情页
 * 作者 : ykb
 * 时间 : 15/8/25.
 */
@EActivity(R.layout.activity_contactinfo)
public class ContactInfoActivity extends BaseActivity
{
    public static final int REQUEST_EDIT_CONTACT_INFO =101;

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
    void initViews(){
        layout_call.setOnTouchListener(Global.GetTouch());
        layout_msg.setOnTouchListener(Global.GetTouch());
        layout_back.setOnTouchListener(Global.GetTouch());
        tv_edit.setOnTouchListener(Global.GetTouch());
        if(user.equals(MainApp.user)) {
            tv_edit.setVisibility(View.VISIBLE);
            layout_action.setVisibility(View.GONE);
        }
        initData();
    }

    @Click({R.id.tv_edit,R.id.layout_back,R.id.layout_call,R.id.layout_msg})
    void onClick(View v){
        switch (v.getId()){
            case R.id.layout_back:
                app.finishActivity(this, MainApp.ENTER_TYPE_ZOOM_IN,0,null);
                break;
            case R.id.tv_edit:
                Bundle b=new Bundle();
                b.putSerializable("user",user);
                app.startActivityForResult(this, ContactInfoEditActivity_.class, MainApp.ENTER_TYPE_BUTTOM, REQUEST_EDIT_CONTACT_INFO,b);
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
     * 初始化数据
     */
    private void initData(){
        if(null==user)
            return;
        if(!TextUtils.isEmpty(user.getAvatar()))
            ImageLoader.getInstance().displayImage(user.getAvatar(), img_title_user);
        Utils.setContent(tv_realname, user.getRealname());
        String dept=user.getDepartmentsName()+" | "+user.getRealname();
        Utils.setContent(tv_deptname, dept);

        Utils.setContent(tv_phone, user.getMobile());
        String gender="";
        if(user.getGender()==2)
            gender="女";
        else if(user.getGender()==1)
            gender="男";
        Utils.setContent(tv_sex, gender);
        Utils.setContent(tv_weixin, user.getWeixinId());
        if(!TextUtils.isEmpty(user.getBirthDay())) {
            int age= Utils.getAge(user.getBirthDay().substring(0, 4));
            if (age>=150){
                return;
            }
            Utils.setContent(tv_birthday,user.getBirthDay());
            Utils.setContent(tv_age, age + "");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode!= REQUEST_EDIT_CONTACT_INFO||resultCode!=RESULT_OK){
            return;
        }

        user=(User)data.getSerializableExtra("user");
        initData();
        updateOrganization();
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
    private void sendSms(){
        if(TextUtils.isEmpty(user.getMobile())){
            Toast("联系人电话号码为空");
            return;
        }
        Uri uri = Uri.parse("smsto:" + user.getMobile());
        Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
        sendIntent.putExtra("sms_body", "");
        startActivity(sendIntent);
    }

    /**
     * 打电话
     */
    private void call(){
        if(TextUtils.isEmpty(user.getMobile())){
            Toast("联系人电话号码为空");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + user.getMobile()));
        startActivity(intent);
    }
}
