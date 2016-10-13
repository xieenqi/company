package com.loyo.oa.v2.activityui.contact.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.widget.EditText;
import android.widget.TextView;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.customview.RoundImageView;
import com.loyo.oa.v2.customview.SweetAlertDialogView;

import java.util.List;

/**
 * Created by yyy on 16/10/12.
 */

public interface ContactInfoEditPresenter {

    /*编辑个人资料*/
    void updateProfile(String id,String tel,int sex,String birthDay,String weixinId,String path);

    /*生日选择控件*/
    void pickDate(Handler mHandler);

    /*弹出提示框*/
    void showLeaveDialog(SweetAlertDialogView sweetAlertDialogView);

    /*判断数据是否被编辑过*/
    boolean isDataChange(TextView tv_mobile, TextView tv_birthday, EditText et_weixin, User mUser, int sex);

    /*设置头像跳转*/
    void setHeadImage(Activity mActivity, Intent mIntent, int REQUEST_IMAGE);

    /*上传头像*/
    void upload(List<String> mSelectPath, String uuid, RoundImageView imageView);

}
