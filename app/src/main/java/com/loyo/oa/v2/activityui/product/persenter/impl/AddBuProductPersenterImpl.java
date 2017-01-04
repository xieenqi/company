package com.loyo.oa.v2.activityui.product.persenter.impl;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.product.api.ProductService;
import com.loyo.oa.v2.activityui.product.model.ProductDetails;
import com.loyo.oa.v2.activityui.product.model.ProductDynmModel;
import com.loyo.oa.v2.activityui.product.persenter.AddBuProductPersenter;
import com.loyo.oa.v2.activityui.product.viewcontrol.AddBuProductView;
import com.loyo.oa.v2.attachment.api.AttachmentService;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.network.model.BaseResponse;
import com.loyo.oa.v2.tool.ListUtil;
import com.loyo.oa.v2.tool.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yyy on 17/1/3.
 */

public class AddBuProductPersenterImpl implements AddBuProductPersenter {

    private AddBuProductView addBuProductView;

    public AddBuProductPersenterImpl(AddBuProductView addBuProductView){
        this.addBuProductView = addBuProductView;
    }

    /**
     * 获取产品新增动态字段
     * */
    @Override
    public void getProductDynm() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("bizType",102);
        ProductService.getProductDynm(map).subscribe(new DefaultLoyoSubscriber<ArrayList<ProductDynmModel>>() {
            @Override
            public void onNext(ArrayList<ProductDynmModel> productDynmModel) {
                addBuProductView.getDynmSuccessEmbl(productDynmModel);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                addBuProductView.getDynmErrorEmbl();
            }
        });
    }

    /**
     * 获取产品详情
     * */
    @Override
    public void getProductDetails(String id) {
        HashMap<String, Object> map = new HashMap<>();
        ProductService.getProductDetails(id).subscribe(new DefaultLoyoSubscriber<ProductDetails>() {
            @Override
            public void onNext(ProductDetails details) {
                addBuProductView.getDetailsSuccessEmbl(details);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                addBuProductView.getDetailsErrorEmbl();
            }
        });
    }

    @Override
    public void getAttachment(String uuid) {
        AttachmentService.getAttachments(uuid)
                .subscribe(new DefaultLoyoSubscriber<ArrayList<Attachment>>() {
                    public void onError(Throwable e) {
                        addBuProductView.getAttachmentErrorEmbl();
                    }
                    @Override
                    public void onNext(ArrayList<Attachment> attachments) {
                        addBuProductView.getAttachmentSuccessEmbl(attachments);
                    }
                });
    }

    @Override
    public void setWatcherOnClick(EditText editText,int type) {

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
