package com.loyo.oa.v2.tool;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.WfInstanceAddActivity_;
import com.loyo.oa.v2.activity.WfinstanceInfoActivity_;
import com.loyo.oa.v2.activity.WorkReportAddActivity_;
import com.loyo.oa.v2.activity.WorkReportsInfoActivity_;
import com.loyo.oa.v2.activity.tasks.TasksAddActivity_;
import com.loyo.oa.v2.activity.tasks.TasksInfoActivity_;
import com.loyo.oa.v2.adapter.CommonExpandableListAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBeans;
import com.loyo.oa.v2.beans.Pagination;
import com.loyo.oa.v2.beans.PagingGroupData_;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.beans.Task;
import com.loyo.oa.v2.beans.WfInstance;
import com.loyo.oa.v2.beans.WorkReport;
import com.loyo.oa.v2.point.IProject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * com.loyo.oa.v2.tool
 * 描述 :项目下子内容共用页
 * 作者 : ykb
 * 时间 : 15/9/7.
 */
public class BaseChildMainListFragmentX extends BaseMainListFragmentX_ implements AbsListView.OnScrollListener {
    private CommonExpandableListAdapter adapter;
    private Project mProject;
    private int type;

    private FrameLayout indicatorGroup;
    private int indicatorGroupId = -1;
    private int indicatorGroupHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (null != getArguments()) {
            if (getArguments().containsKey("type")) {
                type = getArguments().getInt("type");
            }
            if (getArguments().containsKey("project")) {
                mProject = (Project) getArguments().getSerializable("project");
            }
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onProjectChange(int status) {
        if (null != mProject) {
            mProject.status=status;
        }
        if (layout_add == null) {
            return;
        }
        if (status == Project.STATUS_FINISHED) {
            layout_add.setVisibility(View.GONE);
        } else {
            layout_add.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle b) {
        super.onViewCreated(view, b);
        if(null==indicatorGroup){
            indicatorGroup = (FrameLayout) view.findViewById(R.id.topGroup);
            indicatorGroup.getBackground().setAlpha(150);
            mExpandableListView.getRefreshableView().setOnScrollListener(this);
            mInflater.inflate(R.layout.item_sign_show_group, indicatorGroup, true);
        }

        if (mProject != null && mProject.status == Project.STATUS_FINISHED) {
            layout_add.setVisibility(View.GONE);
        } else {
            layout_add.setVisibility(View.VISIBLE);
        }

        switch (type) {
            case 1:
                tv_add.setText("新建报告");
                break;
            case 2:
                tv_add.setText("新建任务");
                break;
            case 12:
                tv_add.setText("新建审批");
                break;
        }
    }

    @Override
    public void GetData(final Boolean isTopAdd, final Boolean isBottomAdd) {
        if (null == mProject) {
            return;
        }

        if (lstData == null) {
            lstData = new ArrayList();
        }
        int pageIndex = isBottomAdd ? (pagination.getPageIndex() + 1) : 1;
        int pageSize = isTopAdd ? lstData.size() >= 20 ? lstData.size() : 20 : pagination.getPageSize();
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", pageIndex);
        map.put("pageSize", pageSize);

      LogUtil.d("获取项目详情的任务，报告，审批：GetData,type : " + type + " projectId : " + mProject.getId() + " pageIndex : " + pageIndex + " pageSize : " + pageSize);
        app.getRestAdapter().create(IProject.class).getProjectSubs(mProject.getId(), type, map, new RCallback<Pagination>() {
            @Override
            public void success(Pagination paginationx, Response response) {
                mExpandableListView.onRefreshComplete();
                if (!Pagination.isEmpty(paginationx)) {
                    ArrayList lstDataTemp = GetTData(paginationx);
                    pagination.setPageIndex(paginationx.getPageIndex());
                    pagination.setPageSize(paginationx.getPageSize());

                    if (isTopAdd) {
                        lstData.clear();
                    }
                    lstData.addAll(lstDataTemp);
                    onLoadSuccess(paginationx.getTotalRecords());
                    pagingGroupDatas = PagingGroupData_.convertGroupData(lstData);
                    changeAdapter();
                    expand();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                super.failure(error);
                mExpandableListView.onRefreshComplete();
            }
        });
    }

    @Override
    public void initAdapter() {
        if (null == adapter) {
            adapter = new CommonExpandableListAdapter(mActivity, pagingGroupDatas);
            mExpandableListView.getRefreshableView().setAdapter(adapter);
        }
    }

    @Override
    public void changeAdapter() {
        if (null == adapter) {
            initAdapter();
            return;
        }
        adapter.setData(pagingGroupDatas);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void addNewItem() {
        switch (type) {
            case 1:
                goToCreatePage(WorkReportAddActivity_.class);
                break;
            case 2:
                goToCreatePage(TasksAddActivity_.class);
                break;
            case 12:
                goToCreatePage(WfInstanceAddActivity_.class);
                break;
        }
    }

    /**
     * 打开新建页
     *
     * @param _class
     */
    private void goToCreatePage(Class<?> _class) {
        Intent intent = new Intent(mActivity, _class);
        intent.putExtra("project", mProject);
        startActivityForResult(intent, REQUEST_CREATE);
    }

    /**
     * 打开查看页
     *
     * @param _class
     */
    private void goToReviewPage(Class<?> _class, String key, Serializable data) {
        Intent intent = new Intent(mActivity, _class);
        intent.putExtra(key, data);
        startActivityForResult(intent, REQUEST_REVIEW);
    }

    @Override
    public void openItem(int groupPosition, int childPosition) {
        switch (type) {
            case 1:
                goToReviewPage(WorkReportsInfoActivity_.class, "workreport", (WorkReport) adapter.getChild(groupPosition, childPosition));
                break;
            case 2:
                goToReviewPage(TasksInfoActivity_.class, "task", (Task) adapter.getChild(groupPosition, childPosition));
                break;
            case 12:
                goToReviewPage(WfinstanceInfoActivity_.class, "data", (WfInstance) adapter.getChild(groupPosition, childPosition));
                break;
        }
    }

    @Override
    public void openSearch(View v) {

    }

    @Override
    public String GetTitle() {
        return "";
    }

    @Override
    public ArrayList GetTData(Pagination p) {
        Type type = null;
        switch (this.type) {
            case 1:
                type = new TypeToken<ArrayList<WorkReport>>() {}.getType();
                break;
            case 2:
                type = new TypeToken<ArrayList<Task>>() {}.getType();
                break;
            case 12:
                type = new TypeToken<ArrayList<WfInstance>>() {}.getType();
                break;
        }
        return MainApp.gson.fromJson(MainApp.gson.toJson(p.getRecords()), type);
    }

    @Override
    public void filterGetData(Intent intent) {}

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount)
    {
        //防止三星,魅族等手机第一个条目可以一直往下拉,父条目和悬浮同时出现的问题
        if(firstVisibleItem==0){
            indicatorGroup.setVisibility(View.GONE);
        }
        final ExpandableListView listView = (ExpandableListView) view;
        /**
         * calculate point (0,0)
         */
        int npos = view.pointToPosition(firstVisibleItem, 0);// 其实就是firstVisibleItem
        if (npos == AdapterView.INVALID_POSITION) {// 如果第一个位置值无效
            return;
        }
        long pos = listView.getExpandableListPosition(npos+firstVisibleItem);
        int childPos = ExpandableListView.getPackedPositionChild(pos);// 获取第一行child的id
        int groupPos = ExpandableListView.getPackedPositionGroup(pos);// 获取第一行group的id
        if (childPos == AdapterView.INVALID_POSITION)
        {// 第一行不是显示child,就是group,此时没必要显示指示器
            View groupView = listView.getChildAt(npos - listView.getFirstVisiblePosition());// 第一行的view
            indicatorGroupHeight = groupView.getHeight();// 获取group的高度
            indicatorGroup.setVisibility(View.GONE);// 隐藏指示器
        }
        else
        {
            TextView tv_title = (TextView) indicatorGroup.findViewById(R.id.tv_title);
            PagingGroupData_<BaseBeans> data=( PagingGroupData_<BaseBeans>)pagingGroupDatas.get(groupPos);
            if (data != null && data.getOrderStr() != null) {
                if(data.getOrderStr().contains("已")){
                    tv_title.setTextColor(getResources().getColor(R.color.green));
                }else {
                    tv_title.setTextColor(getResources().getColor(R.color.title_bg1));
                }
                tv_title.setText(data.getRecords().size()+data.getOrderStr());
            }

            indicatorGroup.setVisibility(View.VISIBLE);// 滚动到第一行是child，就显示指示器
        }
        // get an error data, so return now
        if (indicatorGroupHeight == 0)
        {
            return;
        }
        // update the data of indicator group view
        if (groupPos != indicatorGroupId) {// 如果指示器显示的不是当前group
            adapter.getGroupView(groupPos, listView.isGroupExpanded(groupPos),
                    indicatorGroup.getChildAt(0), null);// 将指示器更新为当前group
            indicatorGroupId = groupPos;
            // mAdapter.hideGroup(indicatorGroupId); // we set this group view
            // to be hided
        }
        if (indicatorGroupId == -1) {// 如果此时group的id无效，则返回
            return;
        }

        /**
         * calculate point (0,indicatorGroupHeight) 下面是形成往上推出的效果
         */
        int showHeight = indicatorGroupHeight;
        int nEndPos = listView.pointToPosition(0, indicatorGroupHeight);// 第二个item的位置
        if (nEndPos == AdapterView.INVALID_POSITION) {// 如果无效直接返回
            return;
        }
        long pos2 = listView.getExpandableListPosition(nEndPos);
        int groupPos2 = ExpandableListView.getPackedPositionGroup(pos2);// 获取第二个group的id
        if (groupPos2 != indicatorGroupId)
        {// 如果不等于指示器当前的group
            View viewNext = listView.getChildAt(nEndPos - listView.getFirstVisiblePosition());
            showHeight = viewNext.getTop();
        }
        // update group position
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) indicatorGroup.getLayoutParams();
        layoutParams.topMargin = -(indicatorGroupHeight - showHeight);
        indicatorGroup.setLayoutParams(layoutParams);
    }
}
