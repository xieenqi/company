package com.loyo.oa.v2.activityui.product.persenter.impl;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.hud.progress.LoyoProgressHUD;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.product.api.ProductService;
import com.loyo.oa.v2.activityui.product.model.ProductDetails;
import com.loyo.oa.v2.activityui.product.model.ProductCustomField;
import com.loyo.oa.v2.activityui.product.persenter.AddBuProductPersenter;
import com.loyo.oa.v2.activityui.product.viewcontrol.AddBuProductView;
import com.loyo.oa.v2.activityui.sale.api.SaleService;
import com.loyo.oa.v2.activityui.sale.bean.SaleIntentionalProduct;
import com.loyo.oa.v2.activityui.sale.bean.SaleProductEdit;
import com.loyo.oa.v2.attachment.api.AttachmentService;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yyy on 17/1/3.
 */

public class AddBuProductPersenterImpl implements AddBuProductPersenter {

    private AddBuProductView addBuProductView;
    private LoadingLayout ll_loading;
    private Context mContext;

    public AddBuProductPersenterImpl(Context mContext,AddBuProductView addBuProductView,LoadingLayout ll_loading){
        this.addBuProductView = addBuProductView;
        this.ll_loading = ll_loading;
        this.mContext = mContext;
    }

    /**
     * 获取产品新增动态字段
     * */
    @Override
    public void getProductDynm() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("bizType",102);
        ProductService.getProductDynm(map).subscribe(new DefaultLoyoSubscriber<ArrayList<ProductCustomField>>(ll_loading) {
            @Override
            public void onNext(ArrayList<ProductCustomField> productCustomField) {
                addBuProductView.getDynmSuccessEmbl(productCustomField);
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
        ProductService.getProductDetails(id).subscribe(new DefaultLoyoSubscriber<ProductDetails>(ll_loading) {
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

    @Override
    public void editProduct(HashMap<String, Object> map, final SaleIntentionalProduct data, String saleId,LoyoProgressHUD hud) {
        SaleService.editSaleProduct(map,saleId).subscribe(new DefaultLoyoSubscriber<SaleProductEdit>(hud) {
            @Override
            public void onNext(SaleProductEdit saleProductEdit) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        addBuProductView.editProductSuccess(data);
                    }
                }, 2000);
            }
        });
    }
}
