package com.loyo.oa.v2.activityui.contact.presenter.impl;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.contact.presenter.ContactInfoEditPresenter;
import com.loyo.oa.v2.activityui.contact.viewcontrol.ContactInfoView;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.SweetAlertDialogView;
import com.loyo.oa.v2.customview.multi_image_selector.MultiImageSelectorActivity;
import com.loyo.oa.v2.point.IUser;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;
import java.util.Calendar;
import java.util.HashMap;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.RetrofitError;
import retrofit.client.Response;
import static com.loyo.oa.v2.common.Global.Toast;

/**
 * Created by yyy on 16/10/12.
 */

public class ContactInfoEditPresenterImpl implements ContactInfoEditPresenter{

    private ContactInfoView crolView;
    private Context mContext;

    public ContactInfoEditPresenterImpl(ContactInfoView crolView,Context mContext){
        this.crolView = crolView;
        this.mContext = mContext;
    }


    /**
     * 编辑个人资料
     * */
    @Override
    public void updateProfile(String id,String tel,int sex,String birthDay,String weixinId,String path) {
        HashMap<String,Object> map = new HashMap<>();
        map.put("mobile", tel);
        map.put("gender", sex);
        map.put("birthDay", birthDay);
        map.put("weixinId", weixinId);
        map.put("avatar", path);

        RestAdapterFactory.getInstance().build(Config_project.SERVER_URL_LOGIN()).create(IUser.class).updateProfile(id, map, new RCallback<User>() {
            @Override
            public void success(final User user, final Response response) {
                HttpErrorCheck.checkResponse("修改个人信息", response);
                crolView.updateProfileEmbl();
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
    }

    /**
     * 生日选择控件
     * */
    @Override
    public void pickDate(final Handler mHandler) {
        Calendar cal = Calendar.getInstance();
        final DatePickerDialog mDialog = new DatePickerDialog(mContext, null,
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        //手动设置按钮
        mDialog.setButton(DialogInterface.BUTTON_POSITIVE, "完成", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatePicker datePicker = mDialog.getDatePicker();
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int day = datePicker.getDayOfMonth();

                int age = Utils.getAge(year + "");
                if (age > 0) {
                    String birthStr = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", day);
                    crolView.setBrithday(mHandler,birthStr);
                } else {
                    Toast("出生日期不能是未来时间，请重新设置");
                }
            }
        });

        //取消按钮，如果不需要直接不设置即可
        mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("BUTTON_NEGATIVE~~");
            }
        });

        mDialog.show();
    }

    /**
     * 弹出提示框
     * */
    @Override
    public void showLeaveDialog(final SweetAlertDialogView sweetAlertDialogView) {
        sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialogView.sweetAlertDialog.dismiss();
            }
        }, new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialogView.sweetAlertDialog.dismiss();
                crolView.leaveDialogEmbl();
            }
        },"提示",mContext.getString(R.string.app_userinfoedt_message));
    }

    /**
     * 判断数据是否被编辑过
     * */
    @Override
    public boolean isDataChange(TextView tv_mobile, TextView tv_birthday, EditText et_weixin, User mUser, int sex) {

        String tel = TextUtils.isEmpty(tv_mobile.getText().toString()) ? null : tv_mobile.getText().toString();
        String birthDay = TextUtils.isEmpty(tv_birthday.getText().toString()) ? null : tv_birthday.getText().toString();
        String weixinId = TextUtils.isEmpty(et_weixin.getText().toString()) ? null : et_weixin.getText().toString();

        if (tel != null) {
            if (!tel.equals(mUser.mobile)) {
                return false;
            }
        }

        if (birthDay != null) {
            if (!birthDay.equals(mUser.birthDay)) {
                return false;
            }
        }

        if (weixinId != null) {
            if (!weixinId.equals(mUser.weixinId)) {
                return false;
            }
        }
        if (sex + "" != null) {
            if (sex != mUser.gender) {
                return false;
            }
        }

        return true;
    }


    @Override
    public void setHeadImage(Activity mActivity,Intent mIntent,int REQUEST_IMAGE) {
        // 是否显示拍摄图片
        mIntent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        // 最大可选择图片数量
        mIntent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
        // 选择模式
        mIntent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
        mIntent.putExtra(MultiImageSelectorActivity.EXTRA_CROP_CIRCLE, true);
        // 默认选择
        //                if (mSelectPath != null && mSelectPath.size() > 0) {
        //                    intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mSelectPath);
        //                }
        mActivity.startActivityForResult(mIntent, REQUEST_IMAGE);
        mActivity.overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
    }
}
