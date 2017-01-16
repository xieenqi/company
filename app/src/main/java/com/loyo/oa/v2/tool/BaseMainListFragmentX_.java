package com.loyo.oa.v2.tool;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.BaseBeans;
import com.loyo.oa.v2.beans.Pagination;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.PagingGroupData;
import com.loyo.oa.v2.beans.PagingGroupData_;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshExpandableListView;

import java.util.ArrayList;

public abstract class BaseMainListFragmentX_<T extends BaseBeans> extends BaseFragment implements PullToRefreshBase.OnRefreshListener2, View.OnTouchListener {

    protected View mView;
    protected ViewGroup layout_add;
    protected LayoutInflater mInflater;
    protected TextView tv_add;
    public static final int REQUEST_CREATE = 4;
    public static final int REQUEST_REVIEW = 5;
    protected PaginationX<T> pagination = new PaginationX<>(20);
    protected PullToRefreshExpandableListView mExpandableListView;
    protected ArrayList<PagingGroupData_<T>> pagingGroupDatas = new ArrayList<>();
    public LoadingLayout ll_loading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInflater = LayoutInflater.from(getActivity());
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_common_manager1, container, false);
            ll_loading = (LoadingLayout) mView.findViewById(R.id.ll_loading);
            ll_loading.setStatus(LoadingLayout.Loading);
            ll_loading.setOnReloadListener(new LoadingLayout.OnReloadListener() {
                @Override
                public void onReload(View v) {
                    ll_loading.setStatus(LoadingLayout.Loading);
                    pagination.setFirstPage();
                    GetData();
                }
            });
            layout_add = (ViewGroup) mView.findViewById(R.id.layout_add);
            tv_add = (TextView) mView.findViewById(R.id.tv_add);
            //layout_add.setOnTouchListener(Global.GetTouch());
            layout_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addNewItem();
                }
            });

            mExpandableListView = (PullToRefreshExpandableListView) mView.findViewById(R.id.expandableListView);
            mExpandableListView.setOnRefreshListener(this);
            init();
        }
        pagination.setFirstPage();
        GetData();
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

    public abstract void GetData();

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
        pagination.setFirstPage();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        GetData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //TODO 这里目前数加载数据，有空了，取消数据直接插入。
        pagination.setFirstPage();
        if (resultCode == 0x09) {
            GetData();
        }
        if (null == data) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CREATE:
                T item = (T) data.getSerializableExtra("data");
                if (item == null) {
                    return;
                }
                GetData();
                return;
            case REQUEST_REVIEW:
                T reviewItem = (T) data.getSerializableExtra("review");
                boolean deleteFlag = false;
                if (reviewItem == null) {
                    reviewItem = (T) data.getSerializableExtra("delete");
                    deleteFlag = true;
                }
                if (null == reviewItem) {
                    return;
                }
                for (int i = 0; i < pagination.getLoadedTotalRecords(); i++) {
                    if (TextUtils.equals(pagination.getRecords().get(i).getId(), reviewItem.getId())) {
                        if (deleteFlag) {
                            pagination.getRecords().remove(i);
                        } else {
                            pagination.getRecords().set(i, reviewItem);
                        }
                        break;
                    }
                }
                GetData();
                break;
        }

        pagingGroupDatas = PagingGroupData_.convertGroupData(pagination.getRecords());
        changeAdapter();
        expand();
    }
}
