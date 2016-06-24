package com.loyo.oa.v2.tool.customview;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.common.Global;


/**
 * *****************************************************************
 * 加载页面
 * *****************************************************************
 */

public class CustomProgressDialog extends Dialog {
    private Activity mParentActivity;
    public ProgressBar progressBar_loading;
    public TextView textView_loadingmsg;
    public int iLoadingTypeCount = 0;
    private Context context;
    private CustomProgressDialogHandler handler;

    public CustomProgressDialog(Activity activity) {
        this(activity, R.style.CustomProgressDialog);
        mParentActivity = activity;
    }

    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
        setContentView(R.layout.dialog_custom_progress);
        getWindow().getAttributes().gravity = Gravity.CENTER;
        this.context = context;
        init();
    }

    private void init() {
        setCanceledOnTouchOutside(false);
        progressBar_loading = (ProgressBar) findViewById(R.id.progressBar_loading);
        textView_loadingmsg = (TextView) findViewById(R.id.textView_loadingmsg);
        handler = new CustomProgressDialogHandler();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (iLoadingTypeCount > 0) {
                    iLoadingTypeCount_subtract();
                }

                break;

            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void show() {
        if (mParentActivity != null && !mParentActivity.isFinishing()) {
            try {
                super.show();

            } catch (Exception e) {
                Global.ProcException(e);
            }
        }
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();    //调用超类对应方法
        } catch (Exception e) {
            Global.ProcException(e);
        }
    }

    public synchronized void iLoadingTypeCount_Add(int type) {
        iLoadingTypeCount_Add(context.getString(type));
    }

    public synchronized void iLoadingTypeCount_Add(String t) {
        textView_loadingmsg.setText(t);
        iLoadingTypeCount++;
        Message msg = new Message();
        msg.what = CustomProgressDialogHandler.UPD_iLoadingTypeCount_Add;
        handler.sendMessage(msg);
    }

    public synchronized void iLoadingTypeCount_subtract() {
        iLoadingTypeCount--;
        Message msg = new Message();
        msg.what = CustomProgressDialogHandler.UPD_iLoadingTypeCount_subtract;
        handler.sendMessage(msg);
    }

    public void show_by_iLoadingTypeCount() {

        if (iLoadingTypeCount <= 0) {
            iLoadingTypeCount = 0;
            textView_loadingmsg.setText(context.getString(R.string.app_dialog_progress_default));
            if (isShowing()) {
                dismiss();
            }
        } else if (iLoadingTypeCount > 0) {
            if (!isShowing()) {
                try {
                    show();
                } catch (Exception e) {
                    Global.ProcException(e);
                }
            }
        }
    }

    class CustomProgressDialogHandler extends Handler {
        public static final int UPD_iLoadingTypeCount_Add = 1;
        public static final int UPD_iLoadingTypeCount_subtract = 2;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPD_iLoadingTypeCount_Add:
                    show_by_iLoadingTypeCount();
                    break;
                case UPD_iLoadingTypeCount_subtract:
                    show_by_iLoadingTypeCount();
                    break;
            }
        }
    }

}
