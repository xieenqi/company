package com.loyo.oa.v2.common.http;

import android.view.Gravity;
import android.widget.Toast;

import com.loyo.oa.v2.application.MainApp;

import retrofit.RetrofitError;

/**
 * Created by pj on 15/12/18.
 */
public class HttpErrorCheck {
    private static Toast mCurrentToast;

    private static void Toast(String msg) {
        if (null != mCurrentToast) {
            mCurrentToast.cancel();
        }

        mCurrentToast = Toast.makeText(MainApp.getMainApp().getBaseContext(), msg, Toast.LENGTH_SHORT);
        mCurrentToast.setGravity(Gravity.CENTER, 0, 0);
        mCurrentToast.show();
    }
    public static void checkError(RetrofitError error){
        String msg=error.getMessage();
        if(msg.contains("500")){
        }
        Toast("失败："+error.getMessage());
    }

}
