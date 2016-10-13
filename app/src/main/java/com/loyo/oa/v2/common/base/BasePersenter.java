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
     * 显示加载状态
     */
    void showProgress();

    /**
     * 隐藏加载状态
     */
    void hideProgress();

    /**
     * 提示消息
     *
     * @param message
     */
    void showMsg(String message);

    /**
     * 绑定页面(详情、列表)数据
     */
    void bindPageData(Object obj);

}
