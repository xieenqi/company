package com.loyo.oa.v2.tool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBeans;
import com.loyo.oa.v2.beans.Pagination;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.PagingGroupData;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshExpandableListView;

import org.apache.http.Header;

import java.util.ArrayList;

public abstract class BaseMainListFragmentX<T extends BaseBeans> extends BaseFragment implements PullToRefreshBase.OnRefreshListener2, AbsListView.OnScrollListener, View.OnTouchListener {

    protected View mView;
    protected Button btn_add;
    protected ViewGroup img_title_left, img_title_right, layout_title_action, layout_category, layout_add;
    protected LayoutInflater mInflater;
    protected TextView tv_title_1, tv_add;

    protected DrawerLayout mDrawerLayout;
    protected int mFragmentContainerViewId;

    public static final int REQUEST_CREATE = 4;
    public static final int REQUEST_REVIEW = 5;

    protected PaginationX<T> pagination = new PaginationX<T>(20);
    protected ArrayList<PagingGroupData<T>> pagingGroupDatas = new ArrayList<>();
    protected ArrayList<T> lstData;

    protected PullToRefreshExpandableListView mExpandableListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInflater = LayoutInflater.from(getActivity());
        GetData(false, false);
    }

    private Runnable UiRunner = new Runnable() {
        @Override
        public void run() {
            btn_add.setVisibility(View.INVISIBLE);
        }
    };

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
//        if(btn_add.getVisibility()!=View.VISIBLE) {
//            btn_add.setVisibility(View.VISIBLE);
//            mView.postDelayed(UiRunner,5000);
//        }
        return false;
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
//        switch (scrollState) {
//            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
//                break;
//            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
//            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
//                if(btn_add.getVisibility()!=View.VISIBLE) {
//                    btn_add.setVisibility(View.VISIBLE);
//                    mView.postDelayed(UiRunner,5000);
//                }
//                break;
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_common_manager, container, false);

            tv_title_1 = (TextView) mView.findViewById(R.id.tv_title_1);
            tv_title_1.setText(GetTitle());
            layout_title_action = (ViewGroup) mView.findViewById(R.id.layout_title_action);
            layout_category = (ViewGroup) mView.findViewById(R.id.layout_category);

            layout_add = (ViewGroup) mView.findViewById(R.id.layout_add);
            tv_add = (TextView) mView.findViewById(R.id.tv_add);
            layout_add.setOnTouchListener(Global.GetTouch());
            layout_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addNewItem();
                }
            });

//            btn_add = (Button) mView.findViewById(R.id.btn_add);
//            btn_add.setOnTouchListener(ViewUtil.OnTouchListener_view_transparency.Instance());
//            btn_add.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    addNewItem();
//                }
//            });

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

            mExpandableListView = (PullToRefreshExpandableListView) mView.findViewById(R.id.expandableListView);
            mExpandableListView.setOnRefreshListener(this);

            init();

            mView.findViewById(R.id.img_title_search_right).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openSearch(v);
                }
            });
        }
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    void init() {
        ExpandableListView expandableListView = mExpandableListView.getRefreshableView();
        initAdapter();
        expand();

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                try {
                    openItem(groupPosition, childPosition);
                } catch (Exception e) {
                    Global.ProcException(e);
                }

                return false;
            }
        });
    }

    void expand() {
        for (int i = 0; i < pagingGroupDatas.size(); i++) {
            mExpandableListView.getRefreshableView().expandGroup(i, false);
        }
    }

    public void setDrawerLayout(DrawerLayout drawerLayout, int fragmentId) {
        mFragmentContainerViewId = fragmentId;
        mDrawerLayout = drawerLayout;
    }

    public abstract void GetData(Boolean isTopAdd, Boolean isBottomAdd);

    public abstract void initAdapter();

    public abstract void changeAdapter();

    public abstract void addNewItem();

    public abstract void openItem(int groupPosition, int childPosition);

    public abstract void openSearch(View v);

    public abstract String GetTitle();

    public abstract ArrayList<T> GetTData(Pagination pagination);

    public abstract void filterGetData(Intent intent);

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        GetData(true, false);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        GetData(false, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK || data == null
                || data.getExtras() == null
                || data.getExtras().size() == 0) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CREATE:
                T item = (T) data.getSerializableExtra("data");

                if (item == null) {
                    return;
                }
                showDataLayout();
                GetData(true, false);
                return;
            case REQUEST_REVIEW:
                T reviewItem = (T) data.getSerializableExtra("review");

                boolean deleteFlag = false;

                if (reviewItem == null) {
                    reviewItem = (T) data.getSerializableExtra("delete");
                    deleteFlag = true;
                }

                for (int i = 0; i < lstData.size(); i++) {
                    if (lstData.get(i).getId() == reviewItem.getId()) {
                        if (deleteFlag) {
                            lstData.remove(i);
                        } else {
                            lstData.set(i, reviewItem);
                        }
                        break;
                    }
                }
                break;
        }

        pagingGroupDatas = PagingGroupData.convertGroupData(lstData);
        //防止多次设置适配器，导致列表重叠 ykb 07-16
//        init();
        changeAdapter();
        expand();
    }

    public class AsyncHandler_get extends BaseAsyncHttpResponseHandler {
        @Override
        public Activity getActivity() {
            return mActivity;
        }

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            Pagination pagination_current = null;
            try {
                pagination_current = MainApp.gson.fromJson(getStr(arg2), Pagination.class);
            } catch (Exception ex) {
            }

            if (!Pagination.isEmpty(pagination_current)) {
                showDataLayout();

                ArrayList<T> lstDataTemp = GetTData(pagination_current);
                pagination.setPageIndex(pagination_current.getPageIndex());

                //下接获取最新时，清空
                if (!isBottomAdd && lstData.size() > 0) {
                    lstData.clear();
                }

                lstData.addAll(lstDataTemp);
                pagingGroupDatas = PagingGroupData.convertGroupData(lstData);
                changeAdapter();
                expand();
            } else {
                showEmptyData(isBottomAdd, isTopAdd);
            }
            mView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mExpandableListView.onRefreshComplete();
                }
            },5000);
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            mView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mExpandableListView.onRefreshComplete();
                }
            }, 5000);
            super.onFailure(i, headers, bytes, throwable);
        }
    }

    void showEmptyData(boolean isBottomAdd, boolean isTopAdd) {
        if ((isBottomAdd || isTopAdd) && lstData.size() > 0) {
            showDataLayout();
        } else {
            showNoDataLayout();
        }
    }

    void showNoDataLayout() {
        View v = mView.findViewById(R.id.vs_nodata);
        if (v != null) {
            v.setVisibility(View.VISIBLE);
//            v.setOnTouchListener(this);
        } else {
            v = mView.findViewById(R.id.layout_nodata);
            v.setVisibility(View.VISIBLE);
//            v.setOnTouchListener(this);
        }
//        mView.postDelayed(UiRunner,5000);
    }

    void showDataLayout() {
        View v = mView.findViewById(R.id.vs_nodata);
        if (v != null) {
            v.setVisibility(View.GONE);
        } else {
            mView.findViewById(R.id.layout_nodata).setVisibility(View.GONE);
        }
//        mExpandableListView.setOnScrollListener(this);
//        mView.postDelayed(UiRunner,5000);
    }
}
