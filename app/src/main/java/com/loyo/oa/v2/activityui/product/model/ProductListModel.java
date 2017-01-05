package com.loyo.oa.v2.activityui.product.model;

import java.io.Serializable;

/**
 * Created by loyo_dev1 on 17/1/3.
 */

public class ProductListModel implements Serializable{

    public String id;
    public String name;
    public float stock;

    public String getStock(){
        return stock > 1 ? (int) stock+"" : stock+"";
    }
}
