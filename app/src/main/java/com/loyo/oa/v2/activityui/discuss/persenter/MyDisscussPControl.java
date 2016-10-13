package com.loyo.oa.v2.activityui.discuss.persenter;

import com.loyo.oa.v2.activityui.discuss.MyDiscussActivity;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;

/**
 * 【我的讨论】的相关操作
 * Created by xeq on 16/10/13.
 */

public class MyDisscussPControl implements MyDiscussPersenter {
    private MyDiscussActivity.DiscussAdapter adapter;
    private boolean isTopAdd = false;
    private int pageIndex = 1;
    private boolean isfirst = true;

    public MyDisscussPControl(MyDiscussActivity context) {
    }

    @Override
    public void getPageData(Object... pag) {

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void bindPageData(Object obj) {

    }

    @Override
    public void skipAtMy() {

    }

    @Override
    public void openItem() {

    }


    @Override
    public void showMsg(String message) {

    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {

    }
}
