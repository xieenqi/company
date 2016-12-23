package com.loyo.oa.v2.common;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.commonview.LoadStatusView;
import com.loyo.oa.v2.tool.LogUtil;

/**
 * 关于与 加载 dialog 的相关类
 * Created xnq 16/1/15.
 */
public class DialogHelp {

    public static LoadStatusView loadStatusDialog; // 带成功失败的loading(2.7.4)
    public static Dialog loadingDialog;            // 加载loading(老版)
    public static TextView tipTextView;

    public static void showLoading(Context context, String msg) {
        showLoading(context, msg, true);
    }

    /**
     * 带成功,失败的加载Dialog
     */
    public static void showStatusLoading(boolean outTouch, Context context) {
        loadStatusDialog = new LoadStatusView(context);
        loadStatusDialog.setCanceledOnTouchOutside(outTouch);
        loadStatusDialog.show();
    }

    /**
     * 成功
     */
    public static void successStatusLoad() {
        if (loadStatusDialog != null) {
            loadStatusDialog.animSuccessEmbl();
        }
    }

    /**
     * 失败
     */
    public static void errorStatusLoading(String message) {
        if (loadStatusDialog != null) {
            loadStatusDialog.animErrorEmbl(message);
        }
    }

    public static void cancelStatusLoading() {
        try {
            loadStatusDialog.dismiss();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            LogUtil.d("提交 loading 取消异常!!!!!!");
        }
    }

    /**
     * 得到自定义的progressDialog
     *
     * @param context
     * @param msg
     * @return
     */
    public static void showLoading(Context context, String msg, boolean Cancelable) {
        LogUtil.d(" 开始logo ");
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_loading, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.load_animayion);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        if (!TextUtils.isEmpty(msg)) {
            tipTextView.setText(msg);// 设置加载信息
        } else {
            tipTextView.setVisibility(View.GONE);
        }

        loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
        loadingDialog.setCancelable(Cancelable);// 不可以用“返回键”取消
        loadingDialog.setCanceledOnTouchOutside(Cancelable);
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
        loadingDialog.setCanceledOnTouchOutside(false);
        try {
            loadingDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cancelLoading() {
        LogUtil.d("消失取消log");
        try {
            if (null != loadingDialog && loadingDialog.isShowing()) {
                loadingDialog.cancel();
                loadingDialog = null;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            LogUtil.d("加载loding 参数异常！！！");
        }
    }

    public static void setLoadingMsg(String msg) {
        if (tipTextView != null && !TextUtils.isEmpty(msg)) {
            tipTextView.setText(msg);
        }
    }

    public static boolean isShow() {
        if (null == loadingDialog) {
            return false;
        }
        return loadingDialog.isShowing();
    }

    public static Dialog createLoadingDialog(Context context) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.layout_loading_dialog, null); // 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view); // 加载布局
        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog); // 创建自定义样式dialog
        loadingDialog.setCancelable(false); // 不可以用"返回键"取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        return loadingDialog;
    }

}
