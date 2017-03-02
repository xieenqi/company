package com.loyo.oa.v2.order.common;

import android.view.Gravity;
import android.widget.Toast;

import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.order.model.CapitalReturn;

import java.util.Date;

/**
 * Created by EthanGong on 2017/2/28.
 */

public class CapitalReturnValidator {

    private static Toast mCurrentToast;

    public static boolean validateAndToast(CapitalReturn capitalReturn, String title) {

        return validate(capitalReturn, true,title);
    }

    public static boolean validate(CapitalReturn capitalReturn, boolean needToast, String title) {

        if (capitalReturn.receivedAt == 0
                && capitalReturn.defaultReceivedAt == 0) {
            if (needToast) {
                Toast("“" + title +"”" + "回款日期未填写");
            }
            return false;
        }
        long timestamp = capitalReturn.receivedAt >0 ?capitalReturn.receivedAt:capitalReturn.defaultReceivedAt;
        if (timestamp > new Date().getTime()/1000) {
            if (needToast) {
                Toast("“" + title +"”" + "回款日期不能选择未来时间");
            }
            return false;
        }
        if (capitalReturn.receivedMoney == 0) {
            if (needToast) {
                Toast("“" + title +"”" + "回款金额未填写");
            }
            return false;
        }

        if (capitalReturn.billingMoney < 0) {
            if (needToast) {
                Toast("“" + title +"”" + "开票金额未填写");
            }
            return false;
        }

        if (capitalReturn.payeeUser == null) {
            if (needToast) {
                Toast("“" + title +"”" + "收款人未选择");
            }
            return false;
        }

        return true;
    }

    protected static void Toast(String msg) {
        if (null != mCurrentToast) {
            mCurrentToast.cancel();
        }

        mCurrentToast = Toast.makeText(MainApp.getMainApp().getBaseContext(), msg, Toast.LENGTH_SHORT);
        mCurrentToast.setGravity(Gravity.CENTER, 0, 0);
        mCurrentToast.show();
    }
}
