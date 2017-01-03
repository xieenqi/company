package com.loyo.oa.v2.activityui.product.viewcontrol;

import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.product.model.ProductDetails;
import com.loyo.oa.v2.activityui.product.model.ProductDynmModel;
import com.loyo.oa.v2.network.model.BaseResponse;

import java.util.ArrayList;

/**
 * Created by yyy on 17/1/3.
 */

public interface AddBuProductView {

    void getDynmSuccessEmbl(ArrayList<ProductDynmModel> model);

    void getDynmErrorEmbl();

    void getDetailsSuccessEmbl(ProductDetails details);

    void getDetailsErrorEmbl();

    void getAttachmentSuccessEmbl(ArrayList<Attachment> attachments);

    void getAttachmentErrorEmbl();

}
