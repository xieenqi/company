package com.loyo.oa.v2.activityui.product.persenter;

import android.widget.EditText;

import com.loyo.oa.hud.progress.LoyoProgressHUD;
import com.loyo.oa.v2.activityui.sale.bean.SaleIntentionalProduct;
import java.util.HashMap;

/**
 * Created by yyy on 17/1/3.
 */

public interface AddBuProductPersenter {

    void getProductDynm();

    void getProductDetails(String id);

    void getAttachment(String uuid);

    void setWatcherOnClick(EditText editText,int type);

    void editProduct(HashMap<String, Object> map, SaleIntentionalProduct data, String saleId,LoyoProgressHUD hud);

}
