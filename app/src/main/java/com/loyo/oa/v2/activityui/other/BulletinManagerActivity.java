package com.loyo.oa.v2.activityui.other;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.other.presenter.Impl.BulletinManagerPresenterImpl;
import com.loyo.oa.v2.activityui.other.viewcontrol.BulletinManagerView;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Bulletin;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshRecycleView;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Utils;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

/**
 *【通知公告】列表页
 * Restructure by yyy on 16/10/10
 */
@EActivity(R.layout.activity_notice)
public class BulletinManagerActivity extends BaseActivity implements PullToRefreshListView.OnRefreshListener2,BulletinManagerView {

    @ViewById ViewGroup img_title_left;
    @ViewById TextView tv_title_1;
    @ViewById PullToRefreshRecycleView lv_notice;
    @ViewById Button btn_notice_add;
    protected PaginationX<Bulletin> mPagination = new PaginationX(20);
    private boolean isTopAdd = true;
    private final int REQUEST_NEW = 1;
    private LinearLayoutManager layoutManager;
    private BulletinManagerPresenterImpl managerPresenter;


    @AfterViews
    void initViews() {
        setTouchView(-1);
        managerPresenter = new BulletinManagerPresenterImpl(this,mContext,BulletinManagerActivity.this);
        managerPresenter.isPermission();

        img_title_left.setOnTouchListener(Global.GetTouch());
        btn_notice_add.setOnTouchListener(Global.GetTouch());
        lv_notice.setMode(PullToRefreshBase.Mode.BOTH);
        lv_notice.setOnRefreshListener(this);
        tv_title_1.setText("公告通知");

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lv_notice.getRefreshableView().setLayoutManager(layoutManager);
        lv_notice.setMode(PullToRefreshBase.Mode.BOTH);
        getData();
        Utils.btnHideForRecy(lv_notice.getRefreshableView(), btn_notice_add);
    }

    /**
     * 获取通知列表
     */
    void getData() {
        managerPresenter.requestListData(mPagination.getPageIndex(),isTopAdd ? mPagination.getPageSize() >= 20 ? mPagination.getPageSize() : 20 : 20);
    }

    /**
     * 返回
     * */
    @Click(R.id.img_title_left)
    void onClick(final View v) {
        onBackPressed();
    }


    /**
     * 添加 通知 公告
     */
    @Click(R.id.btn_notice_add)
    void onAddNew() {
        app.startActivityForResult(this, BulletinAddActivity_.class, MainApp.ENTER_TYPE_RIGHT, REQUEST_NEW, null);
    }

    @Override
    public void onPullDownToRefresh(final PullToRefreshBase refreshView) {
        isTopAdd = true;
        mPagination.setPageIndex(1);
        getData();
    }

    @Override
    public void onPullUpToRefresh(final PullToRefreshBase refreshView) {
        isTopAdd = false;
        mPagination.setPageIndex(mPagination.getPageIndex() + 1);
        getData();
    }

    @OnActivityResult(REQUEST_NEW)
    void onCreateResult(final int resultCode, final Intent data) {
        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        Bulletin b = (Bulletin) data.getSerializableExtra("data");
        if (b != null) {
            isTopAdd = true;
            mPagination.setPageIndex(1);
            getData();
        }
    }

    /**
     * 权限认证成功
     * */
    @Override
    public void permissionSuccess() {
        btn_notice_add.setVisibility(View.INVISIBLE);
    }

    /**
     * 权限错误
     * */
    @Override
    public void permissionError() {
        Toast("发布公告权限,code错误:0402");
    }

    /**
     * 展示Loading
     * */
    @Override
    public void showLoading() {
        showLoading("");
    }

    /**
     * 绑定数据
     * */
    @Override
    public void bindListData() {
        managerPresenter.bindListData(lv_notice);
    }

    /**
     * 刷新完成
     * */
    @Override
    public void refreshCmpl() {
        lv_notice.onRefreshComplete();
    }
}
