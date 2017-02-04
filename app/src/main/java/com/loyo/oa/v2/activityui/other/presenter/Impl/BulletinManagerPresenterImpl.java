package com.loyo.oa.v2.activityui.other.presenter.Impl;

import android.app.Activity;
import android.content.Context;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.pulltorefresh.PullToRefreshRecyclerView2;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.other.adapter.NoticeAdapter2;
import com.loyo.oa.v2.activityui.other.presenter.BulletinManagerPresenter;
import com.loyo.oa.v2.activityui.other.viewcontrol.BulletinManagerView;
import com.loyo.oa.v2.announcement.api.AnnouncementService;
import com.loyo.oa.v2.beans.Bulletin;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.permission.BusinessOperation;
import com.loyo.oa.v2.permission.PermissionManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 【通知公告】Presenter
 * Created by yyy on 16/10/10.
 */

public class BulletinManagerPresenterImpl implements BulletinManagerPresenter {

    private NoticeAdapter2 adapter;
    private BulletinManagerView crolView;

    private Activity mActivity;
    private Context mContext;

    public BulletinManagerPresenterImpl(BulletinManagerView crolView, Context mContext, Activity mActivity) {
        this.crolView = crolView;
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    /**
     * 请求列表数据
     */
    @Override
    public void requestListData(final PaginationX<Bulletin> paginationX) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", paginationX.getShouldLoadPageIndex());
        map.put("pageSize", paginationX.getPageSize());
        AnnouncementService.getNoticeList(map)
                .subscribe(new DefaultLoyoSubscriber<PaginationX<Bulletin>>(crolView.getLoadingLayout()) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        crolView.refreshCmpl();
                    }

                    @Override
                    public void onNext(PaginationX<Bulletin> pagination) {
                        paginationX.loadRecords(pagination);
                        if(paginationX.isEnpty()){
                            crolView.emptyData();
                        }else{
                            crolView.getLoadingLayout().setStatus(LoadingLayout.Success);
                            crolView.bindListData();
                        }
                        crolView.refreshCmpl();
                    }
                });
    }

    /**
     * 数据绑定
     */
    @Override
    public void bindListData(PullToRefreshRecyclerView2 mRecycleView,PaginationX<Bulletin> paginationX) {
        if (null == adapter) {
            adapter = new NoticeAdapter2(paginationX.getRecords(), mContext, mActivity);
            mRecycleView.getRefreshableView().setAdapter(adapter);

        } else {
            adapter.setmDatas(paginationX.getRecords());
        }
    }

    /**
     * 权限认证
     */
    @Override
    public void isPermission() {
        if (PermissionManager.getInstance().hasPermission(BusinessOperation.ANNOUNCEMENT_POSTING)) {
            crolView.permissionSuccess();
        }
    }
}
