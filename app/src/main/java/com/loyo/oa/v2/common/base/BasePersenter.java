package com.loyo.oa.v2.common.base;

/**
 * persenter的基类
 * Created by xeq on 16/10/13.
 */

public interface BasePersenter {
    /**
     * 获取页面(详情、列表)数据
     */
    void getPageData(Object... pag);

    /**
     * 绑定页面(详情、列表)数据
     */
    void bindPageData(Object obj);

    /**
     * 发送提交页面数据
     */
    void sendPageData(Object obj);

}
