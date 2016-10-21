package com.loyo.oa.v2.common;

/**
 * Created by yyy on 16/10/12.
 */

public interface BaseView {

    /**
     * 显示加载状态
     */
    void showProgress(String message);

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

}
