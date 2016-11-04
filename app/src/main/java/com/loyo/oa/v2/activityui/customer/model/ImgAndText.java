package com.loyo.oa.v2.activityui.customer.model;

import java.io.Serializable;

/**
 * Created by xeq on 16/11/3.
 */

public class ImgAndText implements Serializable {
    public String type;
    public String data;

    public ImgAndText(String type, String data) {
        this.type = type;
        this.data = data;
    }
}
