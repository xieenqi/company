package com.loyo.oa.v2.activityui.product.api;

import com.loyo.oa.v2.activityui.product.model.ProductDynmModel;
import com.loyo.oa.v2.network.RetrofitAdapterFactory;
import com.loyo.oa.v2.tool.Config_project;
import java.util.HashMap;
import rx.Observable;

/**
 * Created by yyy on 16/12/30.
 */

public class ProductService {

    //获取产品动态字段
    public static Observable<ProductDynmModel> getFupCusClue(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(IProduct.class)
                        .getProductDynm(params)
                        .compose(RetrofitAdapterFactory.<ProductDynmModel>compatApplySyncSchedulers());
    }
}
