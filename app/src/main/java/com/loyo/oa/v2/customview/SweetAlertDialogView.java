package com.loyo.oa.v2.customview;

import android.content.Context;

import com.loyo.oa.v2.R;

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
     * 带Icon 可操作
     * */
    public void alertIconClick(SweetAlertDialog.OnSweetClickListener clickListener,String title, String message){
        sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
        sweetAlertDialog.setCustomImage(R.drawable.icon_waring);
        sweetAlertDialog.setTitleText(title);
        sweetAlertDialog.setContentText(message);
        sweetAlertDialog.setConfirmClickListener(clickListener);
        sweetAlertDialog.show();
    }


    /**
     * 带Icon 无操作
     * */
    public void alertIcon(String title,String message){
        sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
        sweetAlertDialog.setCustomImage(R.drawable.icon_waring);
        sweetAlertDialog.setTitleText(title);
        sweetAlertDialog.setContentText(message);
        sweetAlertDialog.show();
    }

    /**
     * 只有文字 可操作
     * */
    public void alertMessageClick(SweetAlertDialog.OnSweetClickListener clickListener,String title,String message){
        sweetAlertDialog = new SweetAlertDialog(mContext);
        sweetAlertDialog.setTitleText(title);
        sweetAlertDialog.setContentText(message);
        sweetAlertDialog.setConfirmClickListener(clickListener);
        sweetAlertDialog.show();
    }

    /**
     * 只有文字 无操作
     * */
    public void alertMessage(String title,String message){
        sweetAlertDialog = new SweetAlertDialog(mContext);
        sweetAlertDialog.setTitleText(title);
        sweetAlertDialog.setContentText(message);
        sweetAlertDialog.show();
    }

    /**
     * 确定取消操作
     * */
    public void alertHandle(SweetAlertDialog.OnSweetClickListener cancelClick,
                            SweetAlertDialog.OnSweetClickListener confirmClick,
                            String title,String message){
        sweetAlertDialog = new SweetAlertDialog(mContext,SweetAlertDialog.NORMAL_TYPE);
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
