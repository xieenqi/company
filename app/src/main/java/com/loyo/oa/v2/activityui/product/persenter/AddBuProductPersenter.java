package com.loyo.oa.v2.activityui.product.persenter;

import android.widget.EditText;

/**
 * Created by yyy on 17/1/3.
 */

public interface AddBuProductPersenter {

    void getProductDynm();

    void getProductDetails(String id);

    void getAttachment(String uuid);

    void setWatcherOnClick(EditText editText,int type);

}
