package com.loyo.oa.v2.activityui.product.model;

import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.customer.model.ExtraData;

import java.util.ArrayList;

/**
 * Created by yyy on 17/1/3.
 */

public class ProductDetails {

    public String attachmentUUId;
    public String id;
    public String category;
    public String categoryId;
    public String category_path;
    public String name;
    public String unit;
    public float unitPrice;
    public String memo;
    public float stock;
    public ArrayList<ExtraData> extDatas = new ArrayList<>();
    public ArrayList<Attachment> attachment = new ArrayList<>();

    public String getStock(){
        return stock > 1 ? (int) stock+"" : stock+"";
    }

}
