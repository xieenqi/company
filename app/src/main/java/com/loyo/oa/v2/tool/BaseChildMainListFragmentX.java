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
import com.loyo.oa.v2.beans.TaskRecord;
import com.loyo.oa.v2.beans.WfInstanceRecord;
import com.loyo.oa.v2.beans.WorkReportRecord;
import com.loyo.oa.v2.activityui.wfinstance.WfinstanceInfoActivity_;
import com.loyo.oa.v2.activityui.wfinstance.WfInTypeSelectActivity;
import com.loyo.oa.v2.activityui.work.WorkReportAddActivity;
import com.loyo.oa.v2.activityui.project.HttpProject;
import com.loyo.oa.v2.activityui.tasks.TasksAddActivity_;
import com.loyo.oa.v2.activityui.tasks.TasksInfoActivity_;
import com.loyo.oa.v2.activityui.work.WorkReportAddActivity_;
import com.loyo.oa.v2.activityui.work.WorkReportsInfoActivity_;
import com.loyo.oa.v2.activityui.other.adapter.CommonExpandableListAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBeans;
import com.loyo.oa.v2.beans.Pagination;
import com.loyo.oa.v2.beans.PagingGroupData_;
import com.loyo.oa.v2.beans.Permission;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IProject;
import com.loyo.oa.v2.customview.GeneralPopView;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * com.loyo.oa.v2.tool
 * 描述 :项目下子内容共用页【任务 报告 审批】
 * 作者 : ykb
 * 时间 : 15/9/7.
 */
public class BaseChildMainListFragmentX extends BaseMainListFragmentX_ implements AbsListView.OnScrollListener {

    private CommonExpandableListAdapter adapter;
    private HttpProject mProject;
    private int type;
    private int indicatorGroupId = -1;
    private int indicatorGroupHeight;
    private boolean taskPsn = true;
    private boolean workPsn = true;
    private boolean wiftPsn = true;
    private Permission permission;
    private FrameLayout indicatorGroup;
    private GeneralPopView generalPopView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (null != getArguments()) {
            if (getArguments().containsKey("type")) {
                type = getArguments().getInt("type");
            }
            if (getArguments().containsKey("project")) {
                mProject = (HttpProject) getArguments().getSerializable("project");
            }
        }
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    public void onProjectChange(int status) {
        if (null != mProject) {
            mProject.status = status;
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
        if (null == indicatorGroup) {
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
        if (!mProject.isProjectRelevant()) {
            layout_add.setVisibility(View.GONE);
        }
    }


    public void initView() {

        //超级管理员\权限判断
        if (null != MainApp.user && !MainApp.user.isSuperUser()) {
            try {
                permission = (Permission) MainApp.rootMap.get("0202");
                if (!permission.isEnable()) {
                    taskPsn = false;
                }

                permission = (Permission) MainApp.rootMap.get("0203");
                if (!permission.isEnable()) {
                    workPsn = false;
                }

                permission = (Permission) MainApp.rootMap.get("0204");
                if (!permission.isEnable()) {
                    wiftPsn = false;
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
                Toast("发布公告权限,code错误:0402");
            }
        }
    }

    int pageIndex;

    @Override
    public void GetData(final Boolean isTopAdd, final Boolean isBottomAdd) {
        if (null == mProject) {
            return;
        }

        if (lstData == null) {
            lstData = new ArrayList();
        }
        pageIndex = isBottomAdd ? (pagination.getPageIndex() + 1) : 1;
        int pageSize = isTopAdd ? lstData.size() >= 20 ? lstData.size() : 20 : pagination.getPageSize();
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", pageIndex);
        map.put("pageSize", pageSize);

        app.getRestAdapter().create(IProject.class).getProjectNewSubs(mProject.getId(), type, map, new RCallback<Pagination>() {
            @Override
            public void success(Pagination paginationx, Response response) {
                HttpErrorCheck.checkResponse("获取项目详情的任务，报告，审批json",response);

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
                } else {
                    if (!(paginationx.getRecords().size() > 0) && pageIndex == 1) {
                        pagingGroupDatas.clear();
                        changeAdapter();
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
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


    public void showPop() {
        generalPopView = new GeneralPopView(getActivity(), false);
        generalPopView.show();
        generalPopView.setMessage("此功能权限已关闭，请联系管理员开启后再试！");
        generalPopView.setNoCancelOnclick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generalPopView.dismiss();
            }
        });
    }

    /**
     * 新建报告 任务 审批
     */
    @Override
    public void addNewItem() {
        switch (type) {
            case 1:
                if (!workPsn) {
                    showPop();
                } else {
                    goToCreatePage(WorkReportAddActivity_.class);
                }
                break;
            case 2:
                if (!taskPsn) {
                    showPop();
                } else {
                    goToCreatePage(TasksAddActivity_.class);
                }
                break;
            case 12:
                if (!wiftPsn) {
                    showPop();
                } else {
                    goToCreatePage(WfInTypeSelectActivity.class);
                }
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
        intent.putExtra("projectId", mProject.id);
        intent.putExtra("projectTitle", mProject.title);
        intent.putExtra("type", WorkReportAddActivity.TYPE_PROJECT);
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

    /**
     * 相当于 item 监听
     *
     * @param groupPosition
     * @param childPosition
     */
    @Override
    public void openItem(int groupPosition, int childPosition) {
        switch (type) {
            case 1:
                goToReviewPage(WorkReportsInfoActivity_.class, ExtraAndResult.EXTRA_ID, ((WorkReportRecord) adapter.getChild(groupPosition, childPosition)).getId());
                break;
            case 2:
                goToReviewPage(TasksInfoActivity_.class, ExtraAndResult.EXTRA_ID, ((TaskRecord) adapter.getChild(groupPosition, childPosition)).getId());
                break;
            case 12:
                goToReviewPage(WfinstanceInfoActivity_.class, ExtraAndResult.EXTRA_ID, ((WfInstanceRecord) adapter.getChild(groupPosition, childPosition)).getId());
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
                type = new TypeToken<ArrayList<WorkReportRecord>>() {
                }.getType();
                break;
            case 2:
                type = new TypeToken<ArrayList<TaskRecord>>() {
                }.getType();
                break;
            case 12:
                type = new TypeToken<ArrayList<WfInstanceRecord>>() {
                }.getType();
                break;
        }
        return MainApp.gson.fromJson(MainApp.gson.toJson(p.getRecords()), type);
    }

    @Override
    public void filterGetData(Intent intent) {
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        //防止三星,魅族等手机第一个条目可以一直往下拉,父条目和悬浮同时出现的问题
        if (firstVisibleItem == 0) {
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
        long pos = listView.getExpandableListPosition(npos + firstVisibleItem);
        int childPos = ExpandableListView.getPackedPositionChild(pos);// 获取第一行child的id
        int groupPos = ExpandableListView.getPackedPositionGroup(pos);// 获取第一行group的id
        if (childPos == AdapterView.INVALID_POSITION) {// 第一行不是显示child,就是group,此时没必要显示指示器
            View groupView = listView.getChildAt(npos - listView.getFirstVisiblePosition());// 第一行的view
            indicatorGroupHeight = groupView.getHeight();// 获取group的高度
            indicatorGroup.setVisibility(View.GONE);// 隐藏指示器
        } else {
            TextView tv_title = (TextView) indicatorGroup.findViewById(R.id.tv_title);
            PagingGroupData_<BaseBeans> data = (PagingGroupData_<BaseBeans>) pagingGroupDatas.get(groupPos);
            if (data != null && data.getOrderStr() != null) {
                if (data.getOrderStr().contains("已")) {
                    tv_title.setTextColor(getResources().getColor(R.color.green));
                } else {
                    tv_title.setTextColor(getResources().getColor(R.color.title_bg1));
                }
                tv_title.setText(data.getRecords().size() + data.getOrderStr());
            }

            indicatorGroup.setVisibility(View.VISIBLE);// 滚动到第一行是child，就显示指示器
        }
        // get an error data, so return now
        if (indicatorGroupHeight == 0) {
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
        if (groupPos2 != indicatorGroupId) {// 如果不等于指示器当前的group
            View viewNext = listView.getChildAt(nEndPos - listView.getFirstVisiblePosition());
            showHeight = viewNext.getTop();
        }
        // update group position
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) indicatorGroup.getLayoutParams();
        layoutParams.topMargin = -(indicatorGroupHeight - showHeight);
        indicatorGroup.setLayoutParams(layoutParams);
    }
}
