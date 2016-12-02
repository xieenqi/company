package com.loyo.oa.v2.activityui.other.presenter.Impl;

import android.app.Activity;
import android.content.Context;

import com.loyo.oa.pulltorefresh.PullToRefreshRecyclerView2;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.other.adapter.NoticeAdapter2;
import com.loyo.oa.v2.activityui.other.presenter.BulletinManagerPresenter;
import com.loyo.oa.v2.activityui.other.viewcontrol.BulletinManagerView;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Bulletin;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.permission.BusinessOperation;
import com.loyo.oa.v2.permission.PermissionManager;
import com.loyo.oa.v2.point.INotice;
import com.loyo.oa.v2.tool.RCallback;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【通知公告】Presenter
 * Created by yyy on 16/10/10.
 */

public class BulletinManagerPresenterImpl implements BulletinManagerPresenter {

    private NoticeAdapter2 adapter;
    private BulletinManagerView crolView;
    private PaginationX<Bulletin> mPagination = new PaginationX(20);
    private ArrayList<Bulletin> bulletins = new ArrayList<>();

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
    public void requestListData(int pageIndex, int pageSize, final boolean isTopAdd) {
//        crolView.showProgress("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", pageIndex);
        map.put("pageSize", pageSize);
        MainApp.getMainApp().getRestAdapter().create(INotice.class).getNoticeList(map, new RCallback<PaginationX<Bulletin>>() {
            @Override
            public void success(final PaginationX<Bulletin> pagination, final Response response) {
                HttpErrorCheck.checkResponse(response);
                if (!PaginationX.isEmpty(pagination)) {
                    ArrayList<Bulletin> lstData_bulletin_current = pagination.getRecords();
                    mPagination = pagination;

                    if (isTopAdd) {
                        bulletins.clear();
                        if (lstData_bulletin_current != null && !(lstData_bulletin_current.size() > 0))
                            crolView.emptyData();
                    }
                    bulletins.addAll(lstData_bulletin_current);
                    crolView.bindListData();
                } else {
                    Global.Toast(!isTopAdd ? R.string.app_list_noMoreData : R.string.app_no_newest_data);
                }
                crolView.refreshCmpl();
            }

            @Override
            public void failure(final RetrofitError error) {
                HttpErrorCheck.checkError(error,crolView.getLoadingLayout());
                super.failure(error);
                crolView.refreshCmpl();
            }
        });
    }

    /**
     * 数据绑定
     */
    @Override
    public void bindListData(PullToRefreshRecyclerView2 mRecycleView) {
        if (null == adapter) {
            adapter = new NoticeAdapter2(bulletins, mContext, mActivity);
            mRecycleView.getRefreshableView().setAdapter(adapter);

        } else {
            adapter.setmDatas(bulletins);
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
