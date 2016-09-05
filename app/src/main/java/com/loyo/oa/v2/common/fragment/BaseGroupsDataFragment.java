package com.loyo.oa.v2.common.fragment;

import android.widget.ExpandableListView;

import com.loyo.oa.v2.common.adapter.BaseGroupsDataAdapter;
import com.loyo.oa.v2.common.GroupsData;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshExpandableListView;
import com.loyo.oa.v2.tool.BaseFragment;

/**
 * Created by EthanGong on 16/8/27.
 *
 * 分组列表Fragement基类
 *
 * 定义接口：
 * 1. 上拉下拉刷新
 *     PullToRefreshBase.OnRefreshListener2
 * 2. init ExpandableListView
 *     setupExpandableListView
 * 3. init Adapter
 *     initAdapter
 * 4. 展开 ExpandableListView
 *     expand
 * 5.
 */
public abstract class BaseGroupsDataFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2 {
    protected PullToRefreshExpandableListView mExpandableListView;
    protected ExpandableListView expandableListView;
    protected BaseGroupsDataAdapter adapter;

    protected GroupsData groupsData;

    protected int page = 1;     /*翻页页数*/
    protected boolean isPullDown = true;

    public abstract void initAdapter();

    protected void expand() {
        for (int i = 0; i < groupsData.size(); i++) {
            mExpandableListView.getRefreshableView().expandGroup(i, false);//true 自动滑到底部
        }
    }

    protected  void setupExpandableListView(ExpandableListView.OnGroupClickListener groupClickListener,
                                            ExpandableListView.OnChildClickListener childClickListener) {
        expandableListView = mExpandableListView.getRefreshableView();
        expandableListView.setOnGroupClickListener(groupClickListener);
        expandableListView.setOnChildClickListener(childClickListener);
    }

    /**
     * 请求列表数据
     */

    protected abstract void getData();

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        isPullDown = true;
        page = 1;
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        isPullDown = false;
        page++;
        getData();
    }
}
