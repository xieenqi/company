package com.loyo.oa.v2.activityui.other;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.hud.progress.LoyoProgressHUD;
import com.loyo.oa.hud.toast.LoyoToast;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.pulltorefresh.PullToRefreshRecyclerView2;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.other.presenter.Impl.BulletinManagerPresenterImpl;
import com.loyo.oa.v2.activityui.other.viewcontrol.BulletinManagerView;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Bulletin;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

/**
 * 【通知公告】列表页
 * Restructure by yyy on 16/10/10
 */
@EActivity(R.layout.activity_notice)
public class BulletinManagerActivity extends BaseActivity implements PullToRefreshListView.OnRefreshListener2, BulletinManagerView {

    @ViewById
    ViewGroup img_title_left;
    @ViewById
    TextView tv_title_1;
    @ViewById
    PullToRefreshRecyclerView2 lv_notice;
    @ViewById
    Button btn_notice_add;
    @ViewById
    LoadingLayout ll_loading;
    protected PaginationX<Bulletin> paginationX = new PaginationX(20);
    private final int REQUEST_NEW = 1;
    private LinearLayoutManager layoutManager;
    private BulletinManagerPresenterImpl managerPresenter;


    @AfterViews
    void initViews() {
        managerPresenter = new BulletinManagerPresenterImpl(this, mContext, BulletinManagerActivity.this);
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

        ll_loading.setOnReloadListener(new LoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View v) {
                loadingData();
            }
        });
        loadingData();
    }

    private void loadingData() {
        ll_loading.setStatus(LoadingLayout.Loading);
        paginationX.setFirstPage();
        getData();
    }

    /**
     * 获取通知列表
     */
    void getData() {
        managerPresenter.requestListData(paginationX);
    }

    /**
     * 返回
     */
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
        paginationX.setFirstPage();
        getData();
    }

    @Override
    public void onPullUpToRefresh(final PullToRefreshBase refreshView) {
        getData();
    }

    @OnActivityResult(REQUEST_NEW)
    void onCreateResult(final int resultCode, final Intent data) {
        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        Bulletin b = (Bulletin) data.getSerializableExtra("data");
        if (b != null) {
            paginationX.setFirstPage();
            getData();
        }
    }

    /**
     * 权限认证成功
     */
    @Override
    public void permissionSuccess() {
        btn_notice_add.setVisibility(View.VISIBLE);
        Utils.btnHideForRecy(lv_notice.getRefreshableView(), btn_notice_add);
    }


    @Override
    public void showMsg(String message)
    {
        LoyoToast.info(this, message);
    }

    @Override
    public LoyoProgressHUD showStatusProgress() {
        showCommitLoading();
        return hud;
    }

    /**
     * 展示Loading
     */
    @Override
    public LoyoProgressHUD showProgress(String msg) {
        showLoading2(msg);
        return hud;
    }

    /**
     * 隐藏Loading
     */
    @Override
    public void hideProgress() {
        cancelLoading2();
    }

    /**
     * 绑定数据
     */
    @Override
    public void bindListData() {
        managerPresenter.bindListData(lv_notice, paginationX);
//        ll_loading.setStatus(LoadingLayout.Success);
    }

    /**
     * 刷新完成
     */
    @Override
    public void refreshCmpl() {
        lv_notice.onRefreshComplete();
    }

    @Override
    public void emptyData() {
        ll_loading.setStatus(LoadingLayout.Empty);
    }

    @Override
    public LoadingLayout getLoadingLayout() {
        return ll_loading;
    }
}
