package com.loyo.oa.v2.activityui.product.viewcontrol;

import com.loyo.oa.v2.activityui.product.model.ProductDetails;
import com.loyo.oa.v2.activityui.product.model.ProductDynmModel;
import com.loyo.oa.v2.network.model.BaseResponse;

import java.util.ArrayList;

/**
 * Created by loyo_dev1 on 17/1/3.
 */

public interface AddBuProductView {

    void getDynmSuccessEmbl(ArrayList<ProductDynmModel> model);

    void getDynmErrorEmbl();

    void getDetailsSuccessEmbl(ProductDetails details);

    void getDetailsErrorEmbl();

    void getAttachmentEmbl();

    void getAttachmentErrorEmbl();

}
