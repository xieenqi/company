package com.loyo.oa.v2.tool;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBeans;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.PagingGroupData_;
import com.loyo.oa.v2.beans.TaskRecord;
import com.loyo.oa.v2.beans.WorkReportRecord;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshExpandableListView;

import java.lang.reflect.Type;
import java.util.ArrayList;

public abstract class BaseMainListFragmentX_<T extends BaseBeans> extends BaseFragment implements PullToRefreshBase.OnRefreshListener2, View.OnTouchListener {

    protected View mView;
    protected ViewGroup layout_add;
    //获取布局解析器，方便子类解析布局
    protected LayoutInflater mInflater;
    protected TextView tv_add;
    public static final int REQUEST_CREATE = 4;
    public static final int REQUEST_REVIEW = 5;
    //保存原始数据
    protected PaginationX<JsonObject> pagination = new PaginationX<>(20);
    //显示列表的控件
    protected PullToRefreshExpandableListView mExpandableListView;
    //保存分类以后的数据
    protected ArrayList<PagingGroupData_<T>> pagingGroupDatas = new ArrayList<>();
    public LoadingLayout ll_loading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (null != getArguments()) {
           getInputArguments();
        }
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
                    getData();
                }
            });
            layout_add = (ViewGroup) mView.findViewById(R.id.layout_add);
            tv_add = (TextView) mView.findViewById(R.id.tv_add);
            layout_add.setOnTouchListener(Global.GetTouch());
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
        getData();
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

    /**
     * 展开全部的分组
     */
    public void expand() {
        for (int i = 0; i < pagingGroupDatas.size(); i++) {
            mExpandableListView.getRefreshableView().expandGroup(i, false);
        }
    }

    /**
     * fragment初始化参数
     */
    public abstract void getInputArguments();

    /**
     * 异步从网络加载数据
     */
    public abstract void getData();

    /**
     *  初始化adapter
     */
    public abstract void initAdapter();

    /**
     * 当数据改变的时候，比如，刷新，加载了数据，然后更新ui的时候调用
     */
    public abstract void dataChanged();

    /**
     * 添加一个新的记录
     */
    public abstract void addNewItem();

    /**
     * 点击一个item
     * @param groupPosition
     * @param childPosition
     */
    public abstract void openItem(int groupPosition, int childPosition);

    /**
     * 数据类型转换，把原始数据，转成需要的List类型
     */
    public abstract void convertData();

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        pagination.setFirstPage();
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        getData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //TODO 这里目前数加载数据，有空了，取消数据直接插入。
        if (resultCode == 0x09) {
            pagination.setFirstPage();
            getData();
            return;
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
                pagination.setFirstPage();
                pagination.setTotalRecords(pagination.getTotalRecords()+1);
                getData();
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
                    if (TextUtils.equals(pagination.getRecords().get(i).get("id").getAsString(), reviewItem.getId())) {
                        if (deleteFlag) {
                            pagination.getRecords().remove(i);
                            pagination.setTotalRecords(pagination.getTotalRecords()-1);
                        } else {
                           Type type = new TypeToken<JsonObject>() {}.getType();
                            pagination.getRecords().set(i, (JsonObject) MainApp.gson.fromJson(MainApp.gson.toJson(pagination.getRecords().get(i)),type));
                        }
                        break;
                    }
                }
                break;
        }
        convertData();
        dataChanged();
        expand();
    }
}
