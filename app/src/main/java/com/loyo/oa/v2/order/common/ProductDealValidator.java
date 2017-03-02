package com.loyo.oa.v2.order.common;

import android.view.Gravity;
import android.widget.Toast;

import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.order.model.ProductDeal;

/**
 * Created by EthanGong on 2017/2/28.
 */

public class ProductDealValidator {

    private static Toast mCurrentToast;

    public static boolean validateAndToast(ProductDeal deal, String title) {

        return validate(deal, true,title);
    }

    public static boolean validate(ProductDeal deal, boolean needToast, String title) {

        if (deal.product == null) {
            if (needToast) {
                Toast("“" + title +"”" + "产品未选择");
            }
            return false;
        }
        if (deal.price < 0) {
            if (needToast) {
                Toast("“" + title +"”" + "销售价格未填写");
            }
            return false;
        }

        if (deal.amount <= 0) {
            if (needToast) {
                Toast("“" + title +"”" + "数量未填写");
            }
            return false;
        }

        if (deal.amount > deal.product.stock && deal.stockEnabled) {
            if (needToast) {
                Toast("“" + title +"”" + "库存不足");
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
