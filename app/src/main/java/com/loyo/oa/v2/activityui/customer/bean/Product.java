package com.loyo.oa.v2.activityui.customer.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2014/12/23 0023.
 */
public class Product implements Serializable {
    public ArrayList<ExtraData> extDatas = new ArrayList<>();
    public String name;
    public String prunit;
    public String unitPrice;
    public String unit;
    public String memo;//string, optional): ,
    public String id;//int64, optional): ,

}
