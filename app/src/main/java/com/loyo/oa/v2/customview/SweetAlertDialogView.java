package com.loyo.oa.v2.customview;

import android.content.Context;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 【SweetAlertDialog】弹出框
 * Created by yyy on 16/9/22.
 */
public class SweetAlertDialogView {

    public SweetAlertDialog sweetAlertDialog;
    public Context mContext;

    public SweetAlertDialogView(Context mContext){
        this.mContext = mContext;
    }

    /**
     * 警告,错误带icon弹出框
     * */
    public void alertIcon(String title,String message){
        sweetAlertDialog = new SweetAlertDialog(mContext,SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog.setTitleText(title);
        sweetAlertDialog.setContentText(message);
        sweetAlertDialog.show();
    }

    /**
     * 只有文字 弹出框
     * */
    public void alertMessage(String title,String message){
        sweetAlertDialog = new SweetAlertDialog(mContext);
        sweetAlertDialog.setTitleText(title);
        sweetAlertDialog.setContentText(message);
        sweetAlertDialog.show();
    }

    /**
     * 确定取消操作 弹出框
     * */
    public void alertHandle(SweetAlertDialog.OnSweetClickListener cancelClick,
                            SweetAlertDialog.OnSweetClickListener confirmClick,
                            String title,String message){
        sweetAlertDialog = new SweetAlertDialog(mContext,SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog.setTitleText(title);
        sweetAlertDialog.setContentText(message);
        sweetAlertDialog.setCancelText("取消");
        sweetAlertDialog.setConfirmText("确定");
        sweetAlertDialog.showCancelButton(true);
        sweetAlertDialog.setCancelClickListener(cancelClick);
        sweetAlertDialog.setConfirmClickListener(confirmClick);
        sweetAlertDialog.show();
    }

}
