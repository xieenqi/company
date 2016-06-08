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
import com.loyo.oa.v2.tool.LogUtil;

/**
 * 关于与 加载 dialog 的相关类
 * Created xnq 16/1/15.
 */
public class DialogHelp {

    private static Dialog loadingDialog;//加载loading
    private static TextView tipTextView;
    public static void showLoading(Context context, String msg) {
        showLoading(context, msg, true);
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
        loadingDialog.show();
    }

    public static void cancelLoading() {
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
}
