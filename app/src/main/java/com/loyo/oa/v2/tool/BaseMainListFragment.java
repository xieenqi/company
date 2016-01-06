package com.loyo.oa.v2.tool;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Pagination;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshBase;

public abstract class BaseMainListFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2 {

    protected View mView;
    protected Button btn_add;
    protected ViewGroup img_title_left, img_title_right;
    protected LayoutInflater mInflater;

    protected DrawerLayout mDrawerLayout;
    protected int mFragmentContainerViewId;

    public static final int REQUEST_CREATE = 4;
    public static final int REQUEST_REVIEW = 5;

    protected Pagination pagination = new Pagination(20);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInflater = LayoutInflater.from(getActivity());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        img_title_left = (ViewGroup) mView.findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.finishActivity(mActivity, MainApp.ENTER_TYPE_LEFT, Activity.RESULT_CANCELED, null);
            }
        });
        img_title_left.setOnTouchListener(ViewUtil.OnTouchListener_view_transparency.Instance());

        img_title_right = (ViewGroup) mView.findViewById(R.id.img_title_right);
        img_title_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    View fragment = mActivity.findViewById(mFragmentContainerViewId);
                    if (v != null) {
                        if (!mDrawerLayout.isDrawerOpen(fragment)) {
                            mDrawerLayout.openDrawer(fragment);
                        } else {
                            mDrawerLayout.closeDrawers();
                        }
                    }
                } catch (Exception e) {
                    Global.ProcException(e);
                }
            }
        });
        img_title_right.setOnTouchListener(ViewUtil.OnTouchListener_view_transparency.Instance());

    }

    public void setDrawerLayout(DrawerLayout drawerLayout, int fragmentId) {
        mFragmentContainerViewId = fragmentId;
        mDrawerLayout = drawerLayout;
    }

    public abstract void GetData(Boolean isTopAdd, Boolean isBottomAdd);

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        GetData(true, false);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        GetData(false, true);
    }

}
