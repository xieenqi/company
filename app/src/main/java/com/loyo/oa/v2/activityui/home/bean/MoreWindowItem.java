package com.loyo.oa.v2.activityui.home.bean;

import com.loyo.oa.v2.permission.BusinessOperation;

/**
 * 【快捷菜单】弹窗内容
 * Created by yyy on 16/6/17.
 */
public class MoreWindowItem {

    public String name;
    @BusinessOperation.Type
    public String code;
    public int    img;


    public MoreWindowItem(String name,
                          @BusinessOperation.Type String code,
                          int img){
        this.name = name;
        this.code = code;
        this.img  = img;
    }

}
