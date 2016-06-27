package com.loyo.oa.v2.ui.customview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.loyo.oa.v2.R;

/**
 * 通用提示弹出框
 * Created by yyy on 16/3/2.
 */
public class GeneralPopView extends Dialog {

    private TextView tv_message;
    private boolean kind;

    public GeneralPopView(Context context, boolean kind) {
        super(context);
        this.kind = kind;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_generalpopview);
        tv_message = (TextView) findViewById(R.id.dialog_generalpopview_message);

        if (!kind) {
            findViewById(R.id.dialog_generalpopview_ll).setVisibility(View.GONE);
            findViewById(R.id.dialog_nocancelpopview_ll).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.dialog_nocancelpopview_ll).setVisibility(View.GONE);
            findViewById(R.id.dialog_generalpopview_ll).setVisibility(View.VISIBLE);
        }
    }

    public void setMessage(String strMessage) {
        tv_message.setText(strMessage);
    }

    /**
     * 【确定】 取消
     *
     * @param listener
     */
    public GeneralPopView setSureOnclick(View.OnClickListener listener) {
        findViewById(R.id.dialog_generalpopview_sure).setOnClickListener(listener);
        return this;
    }

    /**
     * 确定 【取消】
     *
     * @param listener
     */
    public GeneralPopView setCancelOnclick(View.OnClickListener listener) {
        findViewById(R.id.dialog_generalpopview_cancel).setOnClickListener(listener);
        return this;
    }

    /**
     * 只有【确定】
     *
     * @param listener
     */
    public void setNoCancelOnclick(View.OnClickListener listener) {
        findViewById(R.id.dialog_nocancelpopview_sure).setOnClickListener(listener);
    }

    public void dismisDialog() {
        this.dismiss();
    }
}
