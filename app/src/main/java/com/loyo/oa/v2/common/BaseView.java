package com.loyo.oa.v2.common;

/**
 * Created by yyy on 16/10/12.
 */

public interface BaseView {

    /*显示带成功失败的提交状态*/
    void showStatusProgress();

    /*显示加载状态*/
    void showProgress(String message);

    /*隐藏加载状态*/
    void hideProgress();

    /*提示消息*/
    void showMsg(String message);

}
